package com.development.edu.moviemania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie mMovie;

    private boolean isFavourite;


    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragmentdetails, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.

        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "FUNCIONA");
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView movieTitle = (TextView) view.findViewById(R.id.item_movie_title);
        ImageView moviePoster = (ImageView) view.findViewById(R.id.item_movie_poster);
        TextView movieRelease = (TextView) view.findViewById(R.id.item_movie_release);
        TextView movieLength = (TextView) view.findViewById(R.id.item_movie_length);
        TextView movieRating = (TextView) view.findViewById(R.id.item_movie_rating);
        TextView movieOverview = (TextView) view.findViewById(R.id.item_movie_overview);

        Button markAsFavourite = (Button) view.findViewById(R.id.item_movie_button);


        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MoviesFragment.MOVIE_ITEM)) {
            mMovie = (Movie) intent.getSerializableExtra(MoviesFragment.MOVIE_ITEM);

            movieTitle.setText(mMovie.getTitle());
            movieRelease.setText(mMovie.getRelease().split("-")[0]);
            movieRating.setText(mMovie.getRating() + "/10.0");
            movieLength.setText(mMovie.getRuntime() + "min");

            String imageUrl = "http://image.tmdb.org/t/p/w185/" + mMovie.getPoster();
            Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.abc_ic_menu_selectall_mtrl_alpha).into(moviePoster);

            movieOverview.setText(mMovie.getOverview());

        }

        isFavourite = movieIsFavourite();
        if (isFavourite) {

            markAsFavourite.setText("Remove from favourites");
        } else {
            markAsFavourite.setText("Mark as favourite");
        }


        markAsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMovie != null) {

                    if (!isFavourite) {

                        getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, Utility.getMovieContentValues(mMovie, getActivity()));
                        Toast.makeText(getActivity(), "Marked as favourite", Toast.LENGTH_SHORT).show();
                    } else {

                        getActivity().getContentResolver().delete(MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()), null, null);
                        Toast.makeText(getActivity(), "Removed from favourites", Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });


        return view;
    }

    private boolean movieIsFavourite() {

        boolean isFavourite = false;

        // A cursor is your primary interface to the query results.
        Cursor cursor = getActivity().getContentResolver().query(MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        if (cursor.moveToFirst())
            isFavourite = true;

        return isFavourite;
    }
}
