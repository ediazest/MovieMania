package com.development.edu.moviemania;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.MoviesContract;
import com.development.edu.moviemania.data.Review;
import com.development.edu.moviemania.data.Trailer;
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

        if (mMovie != null && !mMovie.getTrailers().isEmpty()) {
            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // If onLoadFinished happens before this, we can go ahead and set the share intent now.

            mShareActionProvider.setShareIntent(createShareForecastIntent());

        }
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch this trailer of " + mMovie.getTitle() + ": " + mMovie.getTrailers().get(0).getUrl());
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
        TextView movieOverview = (TextView) view.findViewById(R.id.item_movie_overview);

        RatingBar movieRatingBar = (RatingBar) view.findViewById(R.id.item_movie_ratingBar);

        final ImageButton markAsFavourite = (ImageButton) view.findViewById(R.id.item_movie_button);


        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = (Movie) arguments.getParcelable(MainActivity.MOVIE_ITEM);

            movieTitle.setText(mMovie.getTitle());
            movieRelease.setText(mMovie.getRelease().split("-")[0]);
            movieLength.setText(mMovie.getRuntime() + "min");

            float rating = (float) ((mMovie.getRating() * 5.0) / 10.0);
            movieRatingBar.setRating(rating);

            String imageUrl = "http://image.tmdb.org/t/p/w185/" + mMovie.getPoster();
            Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.abc_ic_menu_selectall_mtrl_alpha).into(moviePoster);

            movieOverview.setText(mMovie.getOverview());

            LinearLayout trailerLayout = (LinearLayout) view.findViewById(R.id.item_movie_trailer_layout);

            if (!mMovie.getTrailers().isEmpty()) {
                for (final Trailer trailer : mMovie.getTrailers()) {

                    TextView v = (TextView) inflater.inflate(R.layout.list_item_movie_trailer, null);
                    v.setText(trailer.getName());

                    trailerLayout.addView(v);

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getUrl())));
                        }
                    });

                }
            } else
                trailerLayout.setVisibility(View.GONE);

            LinearLayout reviewLayout = (LinearLayout) view.findViewById(R.id.item_movie_review_layout);

            if (!mMovie.getReviews().isEmpty()) {
                for (final Review review : mMovie.getReviews()) {

                    View v = inflater.inflate(R.layout.list_item_movie_review, null);

                    ((TextView) v.findViewById(R.id.item_movie_review_user)).setText(review.getAuthor());
                    ((TextView) v.findViewById(R.id.item_movie_review_content)).setText(review.getContent());

                    reviewLayout.addView(v);
                }
            } else
                reviewLayout.setVisibility(View.GONE);


        }

        isFavourite = movieIsFavourite();
        if (isFavourite) {

            markAsFavourite.setImageResource(R.drawable.unfav_btn);
        } else {
            markAsFavourite.setImageResource(R.drawable.fav_btn);
        }


        markAsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMovie != null) {

                    if (!isFavourite) {

                        getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, Utility.getMovieContentValues(mMovie, getActivity()));
                        Toast.makeText(getActivity(), "Marked as favourite", Toast.LENGTH_SHORT).show();
                        markAsFavourite.setImageResource(R.drawable.unfav_btn);
                        isFavourite = true;
                    } else {

                        getActivity().getContentResolver().delete(MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()), null, null);
                        Toast.makeText(getActivity(), "Removed from favourites", Toast.LENGTH_SHORT).show();

                        markAsFavourite.setImageResource(R.drawable.fav_btn);
                        isFavourite = false;
                    }


                }

            }
        });


        return view;
    }

    private boolean movieIsFavourite() {

        boolean isFavourite = false;

        if (mMovie == null)
            return isFavourite;

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
