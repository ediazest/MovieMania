package com.development.edu.moviemania;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by edu on 27/09/2015.
 */
public class FetchMoviesLoaderClass extends AsyncTaskLoader {


    public FetchMoviesLoaderClass(Context context) {
        super(context);

    }

    @Override
    public Object loadInBackground() {
        return null;
    }

}
