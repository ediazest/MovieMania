package com.development.edu.moviemania.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.development.edu.moviemania.data.MoviesContract.MovieEntry;
import com.development.edu.moviemania.data.MoviesContract.ReviewEntry;
import com.development.edu.moviemania.data.MoviesContract.TrailerEntry;

/**
 * Created by edu on 31/08/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                MovieEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this movie data
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL "
                + " );";


        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +

                TrailerEntry._ID + " INTEGER PRIMARY KEY," +

                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +

                // the ID of the location entry associated with this trailer data
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +

                // Set up the movie column as a foreign key to trailer table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") "
                + " );";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +

                ReviewEntry._ID + " INTEGER PRIMARY KEY," +

                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +

                // the ID of the location entry associated with this review data
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +

                // Set up the movie column as a foreign key to review table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") "
                + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
