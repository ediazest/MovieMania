package com.development.edu.moviemania.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.development.edu.moviemania.data.MoviesContract.MovieEntry;
import com.development.edu.moviemania.data.MoviesContract.ReviewEntry;
import com.development.edu.moviemania.data.MoviesContract.TrailerEntry;

import java.util.HashSet;

/**
 * Created by edu on 31/08/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all tables
        assertTrue("Error: Your database was created without all tables.",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_POSTER);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_RATING);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_RELEASE);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_TITLE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + TrailerEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(TrailerEntry._ID);
        trailerColumnHashSet.add(TrailerEntry.COLUMN_MOVIE_KEY);
        trailerColumnHashSet.add(TrailerEntry.COLUMN_TRAILER_NAME);
        trailerColumnHashSet.add(TrailerEntry.COLUMN_TRAILER_URL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            trailerColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required trailer
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required trailer entry columns",
                trailerColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewColumnHashSet = new HashSet<String>();
        reviewColumnHashSet.add(ReviewEntry._ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_MOVIE_KEY);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_REVIEW_AUTHOR);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_REVIEW_CONTENT);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            reviewColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required review
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required review entry columns",
                reviewColumnHashSet.isEmpty());

        db.close();

    }

    public void testMovieTable() {


        long movieRowId = insertMovie();

        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Query the database and receive a Cursor back

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME, //Table to Query
                null, //all columns
                null, //Coluns for the "where" clause
                null, //Values for the "where" clause
                null, //columns to group by
                null, // columns to filter by row group
                null);//sort order

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Failure validating query result", cursor, TestUtilities.createMovieValues());

        assertFalse("Error: More than one record returned from movie query", cursor.moveToNext());

        // Finally, close the cursor and database

        cursor.close();
        db.close();

    }

    public void testTrailerTable() {

        long movieRowId = insertMovie();

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId;
        trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Trailer Values", trailerRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                TrailerEntry.TABLE_NAME, //Table to Query
                null, //all columns
                null, //Coluns for the "where" clause
                null, //Values for the "where" clause
                null, //columns to group by
                null, // columns to filter by row group
                null);//sort order

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from trailer query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Failure validating query result", cursor, trailerValues);

        assertFalse("Error: More than one record returned from weather query", cursor.moveToNext());

        // Finally, close the cursor and database

        cursor.close();
        db.close();
    }

    public void testReviewTable() {

        long movieRowId = insertMovie();

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId;
        reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Review Values", reviewRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                ReviewEntry.TABLE_NAME, //Table to Query
                null, //all columns
                null, //Coluns for the "where" clause
                null, //Values for the "where" clause
                null, //columns to group by
                null, // columns to filter by row group
                null);//sort order

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from review query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Failure validating query result", cursor, reviewValues);

        assertFalse("Error: More than one record returned from review query", cursor.moveToNext());

        // Finally, close the cursor and database

        cursor.close();
        db.close();
    }

    public long insertMovie() {

        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back

        long movieRowId;
        movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);
        db.close();

        return movieRowId;

    }

}



