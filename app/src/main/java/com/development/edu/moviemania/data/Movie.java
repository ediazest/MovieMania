package com.development.edu.moviemania.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edu on 27/08/2015.
 */
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    protected String poster;
    protected String title;
    protected String overview;
    protected float rating;
    protected String release;

    public Movie() {
    }

    protected Movie(Parcel in) {
        poster = in.readString();
        title = in.readString();
        overview = in.readString();
        rating = in.readFloat();
        release = in.readString();
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeFloat(rating);
        dest.writeString(release);
    }
}
