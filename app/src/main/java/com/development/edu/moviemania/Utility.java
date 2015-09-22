package com.development.edu.moviemania;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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

        return movieValues;

    }
}
