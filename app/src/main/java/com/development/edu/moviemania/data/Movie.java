package com.development.edu.moviemania.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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
    protected int id;
    protected String poster;
    protected String title;
    protected String overview;
    protected float rating;
    protected String release;
    protected int runtime;

    protected List<Trailer> trailers;
    protected List<Review> reviews;

    public Movie() {
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        title = in.readString();
        overview = in.readString();
        rating = in.readFloat();
        release = in.readString();
        runtime = in.readInt();

        trailers = in.readArrayList(Trailer.class.getClassLoader());

        reviews = in.readArrayList(Review.class.getClassLoader());

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Trailer> getTrailers() {
        if (trailers == null)
            trailers = new ArrayList<>();
        return trailers;
    }

    public List<Review> getReviews() {
        if (reviews == null)
            reviews = new ArrayList<>();
        return reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeFloat(rating);
        dest.writeString(release);
        dest.writeInt(runtime);
        dest.writeList(trailers);
        dest.writeList(reviews);
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}

