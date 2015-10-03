package com.development.edu.moviemania;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.MoviesContract;
import com.development.edu.moviemania.data.Review;
import com.development.edu.moviemania.data.Trailer;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoviesFragment extends Fragment implements OnMovieListListener {

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    @Bind(R.id.MoviesGridView)
    GridView movieGridView;

    private String SELECTED_KEY = "selected_position";
    private String LIST_KEY = "movie_list";

    private MovieAdapter mMovieAdapter;
    private int mPosition = GridView.INVALID_POSITION;

    private String mSortBy = "";

    private ArrayList<Movie> movieList;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setRetainInstance(true);

        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY))
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            if (savedInstanceState.containsKey(LIST_KEY))
                movieList = savedInstanceState.getParcelableArrayList(LIST_KEY);

        }

        if (movieList == null)
            movieList = new ArrayList<Movie>();

        mMovieAdapter = new MovieAdapter(getActivity(), movieList);

        movieGridView.setAdapter(mMovieAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "click on " + position);


                Movie movie = mMovieAdapter.getItem(position);

                ((Callback) getActivity()).onItemSelected(movie);

                mPosition = position;

                mMovieAdapter.setPositionSelected(mPosition);

            }
        });

        if (mPosition != GridView.INVALID_POSITION) {
            movieGridView.smoothScrollToPosition(mPosition);
            mMovieAdapter.setPositionSelected(mPosition);
        }

        return view;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, mPosition);

        movieList = mMovieAdapter.getMovies();
        if (movieList != null)
            outState.putParcelableArrayList(LIST_KEY, movieList);

        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();

        String sortBy = Utility.getPreferredSorter(getActivity());
        if (!sortBy.equals(mSortBy)) {
            mSortBy = sortBy;
            updateMovieList(sortBy);
            mPosition = GridView.INVALID_POSITION;
        }
    }

    public void refreshMovieList() {
        if (mSortBy.equals(getString(R.string.pref_favourites))) {
            updateMovieList(mSortBy);
        }
    }

    private void updateMovieList(String sortBy) {


        if (sortBy.equals(getString(R.string.pref_favourites))) {

            Log.d(LOG_TAG, "list of favourite movies");
            getFavouriteMovies();

        } else {

            if (Utility.isNetworkAvailable(getActivity())) {


                Log.d(LOG_TAG, "requesting movies to server");
                FetchMoviesTask fmt = new FetchMoviesTask(getActivity(), mMovieAdapter, this);

                String apiKey = getActivity().getString(R.string.api_key);
                fmt.execute(sortBy, apiKey);

            } else {

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.network_connection_dialog_title))
                        .setMessage(getString(R.string.network_connection_dialog_msg))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

        }

    }

    private void getFavouriteMovies() {

        List<Movie> movies = fetchFavouriteMovies();

        if (movies != null) {

            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
        }
        onMovieListLoadComplete();

    }

    private List<Movie> fetchFavouriteMovies() {

        List<Movie> movies = null;

        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        if (cursor.moveToFirst()) {

            movies = new ArrayList<>();

            do {

                Movie movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID)));
                movie.setRating(cursor.getFloat(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING)));
                movie.setRelease(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)));
                movie.setPoster(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER)));
                movie.setRuntime(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME)));

                movie.getTrailers().addAll(fetchTrailersFromMovie(movie.getId()));
                movie.getReviews().addAll(fetchReviewsFromMovie(movie.getId()));

                movies.add(movie);

            } while (cursor.moveToNext());


        }

        return movies;
    }

    private ArrayList<Trailer> fetchTrailersFromMovie(int movieId) {

        ArrayList<Trailer> trailers = new ArrayList<>();

        String selection = MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.TrailerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, // cols for "where" clause
                selectionArgs, // values for "where" clause
                null
        );

        if (cursor.moveToFirst()) {

            do {
                Trailer trailer = new Trailer();
                trailer.setName(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_TRAILER_NAME)));
                trailer.setUrl(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_TRAILER_URL)));

                trailers.add(trailer);

            } while (cursor.moveToNext());

        }

        return trailers;
    }

    private ArrayList<Review> fetchReviewsFromMovie(int movieId) {

        ArrayList<Review> reviews = new ArrayList<>();

        String selection = MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, // cols for "where" clause
                selectionArgs, // values for "where" clause
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Review review = new Review();
                review.setAuthor(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR)));
                review.setContent(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT)));

                reviews.add(review);

            } while (cursor.moveToNext());

        }

        return reviews;
    }

    @Override
    public void onMovieListLoadComplete() {
        if (movieList != null && movieList.isEmpty())
            movieList = mMovieAdapter.getMovies();
        if (MainActivity.getTwoPane() && movieList != null && !movieList.isEmpty() && mPosition == GridView.INVALID_POSITION)
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mPosition = 0;
                    movieGridView.performItemClick(
                            movieGridView.getChildAt(mPosition),
                            mPosition, 0);
                }
            });

    }
}
