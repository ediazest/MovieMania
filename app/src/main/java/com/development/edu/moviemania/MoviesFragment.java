package com.development.edu.moviemania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public static final String MOVIE_ITEM = "MovieToShow";
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final String TAG = MoviesFragment.class.getSimpleName();
    private String SELECTED_KEY = "selected_position";

    private MovieAdapter mMovieAdapter;
    private GridView mMovieGridView;

    private int mPosition = GridView.INVALID_POSITION;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setRetainInstance(true);

        mMovieGridView = (GridView) view.findViewById(R.id.MoviesGridView);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        mMovieGridView.setAdapter(mMovieAdapter);

        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "click on " + position);

                Movie movie = mMovieAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(MOVIE_ITEM, movie);
                startActivity(intent);

                mPosition = position;

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, mPosition);

        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }


    private void updateMovieList() {
        String sortBy = Utility.getPreferredSorter(getActivity());

        if (sortBy.equals(getString(R.string.pref_favourites))) {

            Log.d(TAG, "list of favourite movies");
            getFavouriteMovies();

        } else {

            FetchMoviesTask fmt = new FetchMoviesTask(getActivity(), mMovieAdapter);

            String apiKey = getActivity().getString(R.string.api_key);
            fmt.execute(sortBy, apiKey);
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
}
