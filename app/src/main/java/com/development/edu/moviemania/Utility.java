package com.development.edu.moviemania;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by edu on 27/08/2015.
 */
public class Utility {

    public static String getPreferredSorter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sortby_key),
                context.getString(R.string.pref_most_popular));
    }
}
