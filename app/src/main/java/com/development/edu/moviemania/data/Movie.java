package com.development.edu.moviemania.data;

import java.io.Serializable;

/**
 * Created by edu on 27/08/2015.
 */
public class Movie implements Serializable {

    protected String poster;
    protected String title;
    protected String overview;
    protected float rating;
    protected String release;

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
}
