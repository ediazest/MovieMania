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

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private String SELECTED_KEY = "selected_position";
    private String LIST_KEY = "movie_list";
    private String SORT_BY_KEY = "sort_by";

    private MovieAdapter mMovieAdapter;
    private GridView mMovieGridView;

    private int mPosition = GridView.INVALID_POSITION;

    private ArrayList<Movie> movieList;
    private String sortBy;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setRetainInstance(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY))
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            if (savedInstanceState.containsKey(LIST_KEY))
                movieList = savedInstanceState.getParcelableArrayList(LIST_KEY);
            if (savedInstanceState.containsKey(SORT_BY_KEY))
                sortBy = savedInstanceState.getString(SORT_BY_KEY);
        }

        if (movieList == null)
            movieList = new ArrayList<Movie>();

        mMovieGridView = (GridView) view.findViewById(R.id.MoviesGridView);

        mMovieAdapter = new MovieAdapter(getActivity(), movieList);

        mMovieGridView.setAdapter(mMovieAdapter);

        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "click on " + position);


                Movie movie = mMovieAdapter.getItem(position);

                ((Callback) getActivity()).onItemSelected(movie);


                mPosition = position;

            }
        });

        if (!MainActivity.getTwoPane() && movieList != null && !movieList.isEmpty() && mPosition == GridView.INVALID_POSITION)
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mPosition = 0;
                    mMovieGridView.performItemClick(
                            mMovieGridView.getChildAt(mPosition),
                            mPosition, 0);
                }
            });


        return view;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, mPosition);

        movieList = mMovieAdapter.getMovies();
        if (movieList != null)
            outState.putParcelableArrayList(LIST_KEY, movieList);

        if (sortBy != null)
            outState.putString(SORT_BY_KEY, sortBy);


        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }


    private void updateMovieList() {

        String tmpSortBy = Utility.getPreferredSorter(getActivity());

        if (!tmpSortBy.equals(sortBy)) {
            movieList.clear();
            if (tmpSortBy.equals(getString(R.string.pref_favourites))) {

                Log.d(LOG_TAG, "list of favourite movies");
                getFavouriteMovies();

            } else {

                if (Utility.isNetworkAvailable(getActivity())) {

                    if (movieList.isEmpty()) {

                        Log.d(LOG_TAG, "requesting movies to server");
                        FetchMoviesTask fmt = new FetchMoviesTask(getActivity(), mMovieAdapter);

                        String apiKey = getActivity().getString(R.string.api_key);
                        fmt.execute(tmpSortBy, apiKey);
                    }

                } else {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Sorry, we need a network connection to show movies")
                            .setMessage("Please come back when you have a network connection available")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }

            }
            sortBy = tmpSortBy;
        }
    }

    private void getFavouriteMovies() {

        List<Movie> movies = fetchFavouriteMovies();

        if (movies != null) {

            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
        }

    }

    private List<Movie> fetchFavouriteMovies() {

        List<Movie> movies = null;
        // A cursor is your primary interface to the query results.
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

                movies.add(movie);

            } while (cursor.moveToNext());


        }

        return movies;
    }

    public interface Callback {

        void onItemSelected(Movie movie);
    }
}
