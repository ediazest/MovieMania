package com.development.edu.moviemania.data;

/**
 * Created by edu on 31/08/2015.
 */


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.development.edu.moviemania";


    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    //    Inner class that defines the table contents of the movie table
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        // ID is stored as a Integer
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Title is stored as a String
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        // Overview is stored as a String
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";

        // Release date is stored as a String
        public static final String COLUMN_MOVIE_RELEASE = "movie_release";

        // Rating is stored as a float
        public static final String COLUMN_MOVIE_RATING = "movie_rating";

        // Poster is stored as a String
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        // Poster is stored as a Integer
        public static final String COLUMN_MOVIE_RUNTIME = "movie_runtime";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    //    Inner class that defines the table contents of the trailer table
    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        // Name is stored as a String
        public static final String COLUMN_TRAILER_NAME = "trailer_name";

        // URL is stored as a String
        public static final String COLUMN_TRAILER_URL = "trailer_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    //    Inner class that defines the table contents of the review table
    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        // Author is stored as a String
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";

        // Content is stored as a String
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
