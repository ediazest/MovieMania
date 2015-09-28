package com.development.edu.moviemania;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.MoviesContract;

/**
 * Created by edu on 27/08/2015.
 */
public class Utility {

    public static String getPreferredSorter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sortby_key),
                context.getString(R.string.pref_most_popular));
    }

    public static ContentValues getMovieContentValues(Movie movie, Context context) {

        ContentValues movieValues = new ContentValues();

        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getPoster());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING, movie.getRating());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE, movie.getRelease());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME, movie.getRuntime());


        return movieValues;

    }

    public static ContentValues getTrailerContentValues(Movie movie, Context context) {

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getPoster());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING, movie.getRating());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE, movie.getRelease());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        trailerValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME, movie.getRuntime());


        return trailerValues;

    }


    public static boolean isNetworkAvailable(Activity callingActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) callingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

