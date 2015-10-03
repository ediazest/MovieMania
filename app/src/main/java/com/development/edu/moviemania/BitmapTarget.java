package com.development.edu.moviemania;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by edu on 01/10/2015.
 */
public class BitmapTarget implements Target {

    private int mMovieId;
    private Context mContext;

    public BitmapTarget(Context context, int movieId) {
        this.mMovieId = movieId;
        this.mContext = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        Utility.storageBitmap(bitmap, mMovieId, mContext);

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }

}