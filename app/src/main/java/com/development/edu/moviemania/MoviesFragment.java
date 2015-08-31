package com.development.edu.moviemania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.development.edu.moviemania.data.Movie;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    public static final String MOVIE_ITEM = "MovieToShow";
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
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
        FetchMoviesTask fmt = new FetchMoviesTask(getActivity(), mMovieAdapter);

        String sortBy = Utility.getPreferredSorter(getActivity());
        String apiKey = getActivity().getString(R.string.api_key);
        fmt.execute(sortBy, apiKey);
    }
}
