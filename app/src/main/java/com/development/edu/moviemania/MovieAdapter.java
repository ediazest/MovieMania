package com.development.edu.moviemania;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.development.edu.moviemania.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by edu on 27/08/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie_poster, null);

            // if it's not recycled, initialize some attributes
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        String imageUrl = "http://image.tmdb.org/t/p/w185/" + mMovies.get(position).getPoster();
        Log.d(LOG_TAG, imageUrl);
        Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.abc_ic_menu_selectall_mtrl_alpha).into(holder.posterView);

        return convertView;
    }

    public void clear() {
        mMovies = new ArrayList<Movie>();
    }

    public void addAll(List<Movie> movies) {

        for (Movie movie : movies)
            mMovies.add(movie);

        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        @Bind(R.id.list_item_poster)
        ImageView posterView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
