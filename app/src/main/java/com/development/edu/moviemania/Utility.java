package com.development.edu.moviemania;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.MoviesContract;
import com.development.edu.moviemania.data.Review;
import com.development.edu.moviemania.data.Trailer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by edu on 27/08/2015.
 */
public class Utility {

    public static final String POSTER_ROOT = "http://image.tmdb.org/t/p/w185/";
    public static final String FOLDER = "movimania/";

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

    public static ContentValues getTrailerContentValues(Trailer trailer, long movieId, Context context) {

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TRAILER_URL, trailer.getUrl());
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());

        return trailerValues;

    }

    public static ContentValues[] getTrailerContentValues(Movie movie, Context context) {

        List<Trailer> trailers = movie.getTrailers();

        ContentValues[] trailerValues = new ContentValues[trailers.size()];

        for (int i = 0; i < trailerValues.length; i++) {
            trailerValues[i] = new ContentValues();
            trailerValues[i].put(MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY, movie.getId());
            trailerValues[i].put(MoviesContract.TrailerEntry.COLUMN_TRAILER_URL, trailers.get(i).getUrl());
            trailerValues[i].put(MoviesContract.TrailerEntry.COLUMN_TRAILER_NAME, trailers.get(i).getName());

        }
        return trailerValues;

    }

    public static ContentValues getReviewContentValues(Review review, long movieId, Context context) {

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);
        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());

        return reviewValues;

    }

    public static ContentValues[] getReviewContentValues(Movie movie, Context context) {

        List<Review> reviews = movie.getReviews();

        ContentValues[] reviewValues = new ContentValues[reviews.size()];

        for (int i = 0; i < reviewValues.length; i++) {
            reviewValues[i] = new ContentValues();
            reviewValues[i].put(MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, movie.getId());
            reviewValues[i].put(MoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, reviews.get(i).getAuthor());
            reviewValues[i].put(MoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT, reviews.get(i).getContent());

        }
        return reviewValues;

    }

    public static boolean isNetworkAvailable(Activity callingActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) callingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void storageBitmap(Bitmap bitmap, int mMovieId, Context context) {

        String filename = "img_" + mMovieId + ".png";
        File sd = context.getExternalFilesDir(null);
        File dest = new File(sd, filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteExternalStoragePrivateFile(Context context, String fileName) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), fileName);
        if (file != null) {
            file.delete();
        }
    }

    public static boolean hasExternalStoragePrivateFile(Context context, String fileName) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), fileName);
        if (file != null) {
            return file.exists();
        }
        return false;
    }

    public static File getBitmapFile(Context context, String fileName) {

        return new File(context.getExternalFilesDir(null), fileName);

    }
}

