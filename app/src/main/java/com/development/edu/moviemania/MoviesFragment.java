package com.development.edu.moviemania;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
    private String LIST_KEY = "movie_list";

    private MovieAdapter mMovieAdapter;
    private GridView mMovieGridView;

    private int mPosition = GridView.INVALID_POSITION;

    private ArrayList<Movie> movieList;

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

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(MOVIE_ITEM, movie);
                startActivity(intent);

                mPosition = position;

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


        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }


    private void updateMovieList() {

        if (isNetworkAvailable()) {

            if (movieList.isEmpty()) {

                Log.d(LOG_TAG, "requesting movies to server");
                FetchMoviesTask fmt = new FetchMoviesTask(getActivity(), mMovieAdapter);

                String sortBy = Utility.getPreferredSorter(getActivity());
                String apiKey = getActivity().getString(R.string.api_key);
                fmt.execute(sortBy, apiKey);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
