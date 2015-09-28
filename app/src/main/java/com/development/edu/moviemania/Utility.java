package com.development.edu.moviemania;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


    public static boolean isNetworkAvailable(Activity callingActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) callingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
