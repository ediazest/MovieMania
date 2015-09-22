package com.development.edu.moviemania.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by edu on 31/08/2015.
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE = 100;

    static final int MOVIE_WITH_ID = 101;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder sMovieByMovieIdQueryBuilder;

    //movie.movie_id = ?
    private static final String sMovieSettingSelection =
            MoviesContract.MovieEntry.TABLE_NAME +
                    "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    static {
        sMovieByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieByMovieIdQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME);
    }

    private MovieDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {

        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String contentAuthority = MoviesContract.CONTENT_AUTHORITY;

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_MOVIE, MOVIE);

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);

        return sURIMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_WITH_ID:
                retCursor = getMovieByMovieID(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.MovieEntry.getMovieIdFromUri(uri);


        String[] selectionArgs = {};
        String selection = "";

        if ((movieId != null) && (!movieId.isEmpty())) {
            selection = sMovieSettingSelection;
            selectionArgs = new String[]{movieId};
        }

        return sMovieByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        if (sUriMatcher.match(uri) == MOVIE) {

            long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
            if (_id > 0)
                returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);

        } else
            throw new UnsupportedOperationException("Unknown uri: " + uri);

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        switch (match) {
            case MOVIE:
            case MOVIE_WITH_ID:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
            case MOVIE_WITH_ID:

                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
