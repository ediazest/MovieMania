package com.development.edu.moviemania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.development.edu.moviemania.data.Movie;
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
    @Bind(R.id.item_movie_rating)
    TextView movieRating;
    @Bind(R.id.item_movie_overview)
    TextView movieOverview;
    private Movie mMovie;


    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MoviesFragment.MOVIE_ITEM)) {
            mMovie = (Movie) intent.getParcelableExtra(MoviesFragment.MOVIE_ITEM);

            movieTitle.setText(mMovie.getTitle());
            movieRelease.setText(mMovie.getRelease());
            movieRating.setText(mMovie.getRating() + "/10.0");

            String imageUrl = "http://image.tmdb.org/t/p/w185/" + mMovie.getPoster();
            Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.abc_ic_menu_selectall_mtrl_alpha).into(moviePoster);

            movieOverview.setText(mMovie.getOverview());

        }

        return view;
    }
}
