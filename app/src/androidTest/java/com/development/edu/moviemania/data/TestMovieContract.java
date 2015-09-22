package com.development.edu.moviemania.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by edu on 31/08/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 123456;

    public void testBuildMovie() {
        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MoviesContract.",
                movieUri);

        assertEquals("Error: Movie id not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("Error: Movie id Uri doesn't match our expected result",
                movieUri.toString(),
                "content://" + MoviesContract.CONTENT_AUTHORITY + "/" + MoviesContract.PATH_MOVIE + "/" + TEST_MOVIE_ID);
    }
}
