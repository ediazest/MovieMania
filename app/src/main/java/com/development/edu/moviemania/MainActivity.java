package com.development.edu.moviemania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.development.edu.moviemania.data.Movie;

public class MainActivity extends AppCompatActivity implements Callback, OnMovieDetailsListener {

    public static final String MOVIE_ITEM = "MovieToShow";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static boolean mTwoPane;
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    public static boolean getTwoPane() {
        return mTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent mIntent = new Intent(this, SettingsActivity.class);
            startActivity(mIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {

        if (Utility.isNetworkAvailable(this)) {

            Log.d(LOG_TAG, "requesting movie details to server");
            FetchMovieDetailsTask fmdt = new FetchMovieDetailsTask(this, movie, this);

            String apiKey = this.getString(R.string.api_key);
            fmdt.execute(apiKey);

        } else {
            showMovieDetails(movie);
        }


    }

    @Override
    public void onFavouriteItem(boolean isFavourite) {
        if (!isFavourite) {

            MoviesFragment moviesFragment = ((MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main));
            moviesFragment.refreshMovieList();

        }
    }


    private void showMovieDetails(Movie movie) {

        if (mTwoPane) {

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putParcelable(MOVIE_ITEM, movie);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);
            df.setTwoPanel(this);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG).commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(MOVIE_ITEM, movie);
            startActivity(intent);
        }

    }

    @Override
    public void onMovieDetailsComplete(Movie movie) {

        showMovieDetails(movie);
    }
}
