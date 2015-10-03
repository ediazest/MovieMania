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

    static final int TRAILER = 101;

    static final int REVIEW = 102;

    static final int MOVIE_WITH_ID = 103;


    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    private static final SQLiteQueryBuilder sTrailerByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieIdQueryBuilder;

    //movie.movie_id = ?
    private static final String sMovieSelection =
            MoviesContract.MovieEntry.TABLE_NAME +
                    "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    //trailer.movie_id = ?
    private static final String sTrailersMovieSelection =
            MoviesContract.TrailerEntry.TABLE_NAME +
                    "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";

    //review.movie_id = ?
    private static final String sReviewsMovieSelection =
            MoviesContract.ReviewEntry.TABLE_NAME +
                    "." + MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";

    static {
        sMoviesQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        sMoviesQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        MoviesContract.TrailerEntry.TABLE_NAME +
                        " ON " + MoviesContract.TrailerEntry.TABLE_NAME +
                        "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LEFT OUTER JOIN " +
                        MoviesContract.ReviewEntry.TABLE_NAME +
                        " ON " + MoviesContract.ReviewEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " GROUP BY "
                        + MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
    }


    static {
        sTrailerByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        sTrailerByMovieIdQueryBuilder.setTables(
                MoviesContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.TrailerEntry.TABLE_NAME +
                        "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static {
        sReviewByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        sReviewByMovieIdQueryBuilder.setTables(
                MoviesContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.ReviewEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    private MovieDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {

        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String contentAuthority = MoviesContract.CONTENT_AUTHORITY;

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_MOVIE, MOVIE);

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_TRAILER, TRAILER);

        sURIMatcher.addURI(contentAuthority, MoviesContract.PATH_REVIEW, REVIEW);

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

            case TRAILER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getTrailersByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);


        String[] selectionArgs = {};
        String selection = "";

        if ((movieId != null) && (!movieId.isEmpty())) {
            selection = sTrailersMovieSelection;
            selectionArgs = new String[]{movieId};
        }

        return sTrailerByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getTrailers(Uri uri, String[] projection, String sortOrder) {

        return sTrailerByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getMovieByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = {};
        String selection = "";

        if ((movieId != null) && (!movieId.isEmpty())) {
            selection = sMovieSelection;
            selectionArgs = new String[]{movieId};
        }

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

    }

    private Cursor getReviewsByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = {};
        String selection = "";

        if ((movieId != null) && (!movieId.isEmpty())) {
            selection = sReviewsMovieSelection;
            selectionArgs = new String[]{movieId};
        }

        return sReviewByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getReviews(Uri uri, String[] projection, String sortOrder) {

        return sReviewByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
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
            case TRAILER:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;
            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case TRAILER:
                _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case REVIEW:
                _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


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
            case TRAILER:
                rowsDeleted = db.delete(MoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowsDeleted = db.delete(MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
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

        int returnCount = 0;
        switch (match) {
            case MOVIE:

                db.beginTransaction();
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

            case TRAILER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
