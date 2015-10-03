package com.development.edu.moviemania;

import com.development.edu.moviemania.data.Movie;

/**
 * Created by edu on 01/10/2015.
 */
public interface Callback {

    void onItemSelected(Movie movie);

    void onFavouriteItem(boolean isFavourite);
}
