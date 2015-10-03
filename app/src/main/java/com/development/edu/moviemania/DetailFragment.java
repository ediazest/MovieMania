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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    @Bind(R.id.item_movie_title)
    TextView movieTitle;

    @Bind(R.id.item_movie_poster)
    ImageView moviePoster;

    @Bind(R.id.item_movie_release)
    TextView movieRelease;

    @Bind(R.id.item_movie_overview)
    TextView movieOverview;

    @Bind(R.id.item_movie_length)
    TextView movieLength;

    @Bind(R.id.item_movie_ratingBar)
    RatingBar movieRatingBar;

    @Bind(R.id.item_movie_button)
    ImageButton markAsFavourite;

    private Movie mMovie;

    private boolean isFavourite;

    private ShareActionProvider mShareActionProvider;
    private Callback mCallback;

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

            mShareActionProvider.setShareIntent(createShareTrailerIntent());

        }
    }


    private Intent createShareTrailerIntent() {
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

        ButterKnife.bind(this, view);


        // The detail Activity called via intent.  Inspect the intent for movie data.
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(MainActivity.MOVIE_ITEM);

            movieTitle.setText(mMovie.getTitle());
            movieRelease.setText(mMovie.getRelease().split("-")[0]);
            movieLength.setText(mMovie.getRuntime() + "min");

            float rating = (float) ((mMovie.getRating() * 5.0) / 10.0);
            movieRatingBar.setRating(rating);

            //check if the picture has been downloaded
            if (Utility.hasExternalStoragePrivateFile(getActivity(), "img_" + mMovie.getId() + ".png")) {
                Picasso.with(getActivity()).load(Utility.getBitmapFile(getActivity(), "img_" + mMovie.getId() + ".png"))
                        .into(moviePoster);
            } else {

                String imageUrl = Utility.POSTER_ROOT + mMovie.getPoster();
                Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.abc_ic_menu_selectall_mtrl_alpha).into(moviePoster);

            }

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
                        getActivity().getContentResolver().bulkInsert(MoviesContract.TrailerEntry.CONTENT_URI, Utility.getTrailerContentValues(mMovie, getActivity()));
                        getActivity().getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, Utility.getReviewContentValues(mMovie, getActivity()));

                        //using picasso to download the picture and storage it
                        Picasso.with(getActivity()).load(Utility.POSTER_ROOT + mMovie.getPoster()).into(new BitmapTarget(getActivity(), mMovie.getId()));

                        Toast.makeText(getActivity(), getString(R.string.favourites_save), Toast.LENGTH_SHORT).show();
                        markAsFavourite.setImageResource(R.drawable.unfav_btn);
                        isFavourite = true;
                    } else {

                        getActivity().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{String.valueOf(mMovie.getId())});
                        getActivity().getContentResolver().delete(MoviesContract.TrailerEntry.CONTENT_URI,
                                MoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                                new String[]{String.valueOf(mMovie.getId())});
                        getActivity().getContentResolver().delete(MoviesContract.ReviewEntry.CONTENT_URI,
                                MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                                new String[]{String.valueOf(mMovie.getId())});
                        Toast.makeText(getActivity(), getString(R.string.favourites_delete), Toast.LENGTH_SHORT).show();

                        markAsFavourite.setImageResource(R.drawable.fav_btn);
                        isFavourite = false;

                        Utility.deleteExternalStoragePrivateFile(getActivity(), "img_" + mMovie.getId() + ".png");
                    }

                    if (mCallback != null)
                        mCallback.onFavouriteItem(isFavourite);

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

    public void setTwoPanel(Callback callback) {
        mCallback = callback;
    }
}
