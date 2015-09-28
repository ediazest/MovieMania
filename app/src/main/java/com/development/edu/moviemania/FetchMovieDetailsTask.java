package com.development.edu.moviemania;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.development.edu.moviemania.data.Movie;
import com.development.edu.moviemania.data.Review;
import com.development.edu.moviemania.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by edu on 26/08/2015.
 */
public class FetchMovieDetailsTask extends AsyncTask<String, Void, Movie> {

    private static final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();
    private final Context mContext;
    DetailsLoader mCallback;
    private Movie mMovie;

    public FetchMovieDetailsTask(Context context, Movie movie, DetailsLoader callback) {
        mContext = context;
        mMovie = movie;
        mCallback = callback;

    }


    @Override
    protected Movie doInBackground(String... params) {
        {
            Log.v(LOG_TAG, "" + params.length);
            if (params == null || params.length < 1)
                return null;

            String apiKey = params[0];

            Log.v(LOG_TAG, "Parameters: " + mMovie.getId() + " , " + apiKey);
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDetailsJsonStr = null;

            try {

                //http://api.themoviedb.org/3/movie/76341?api_key=103d17c09399fc788a4e2f5f663fd1a8&append_to_response=trailers,reviews
                final String MOVIE_LIST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";

                String movieId = String.valueOf(mMovie.getId());
                Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .appendQueryParameter("append_to_response", "trailers,reviews")
                        .build();


                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "HTTP request to " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "inputStream null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.v(LOG_TAG, "buffer empty");
                    return null;
                }
                movieDetailsJsonStr = buffer.toString();
                Log.v(LOG_TAG, movieDetailsJsonStr);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

                try {
                    return getMovieDetailsFromJson(movieDetailsJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Movie getMovieDetailsFromJson(String moviesListJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_LENGHT = "runtime";

        final String TMDB_TRAILER = "trailers";
        final String TMDB_TRAILER_YOUTUBE = "youtube";
        final String TMDB_TRAILER_YOUTUBE_NAME = "name";
        final String TMDB_TRAILER_YOUTUBE_SOURCE = "source";

        final String TMDB_REVIEWS = "reviews";
        final String TMDB_REVIEWS_RESULTS = "results";
        final String TMDB_REVIEWS_RESULTS_AUTHOR = "author";
        final String TMDB_REVIEWS_RESULTS_CONTENT = "content";

        Movie result = new Movie();

        JSONObject movieDetailsJson = new JSONObject(moviesListJsonStr);

        result.setRuntime(movieDetailsJson.getInt(TMDB_LENGHT));

        JSONObject movieTrailersJson = movieDetailsJson.getJSONObject(TMDB_TRAILER);
        JSONArray movieYoutubeTrailersArray = movieTrailersJson.getJSONArray(TMDB_TRAILER_YOUTUBE);

        for (int i = 0; i < movieYoutubeTrailersArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject movieTrailerJson = movieYoutubeTrailersArray.getJSONObject(i);
            Trailer trailer = new Trailer();
            trailer.setName(movieTrailerJson.getString(TMDB_TRAILER_YOUTUBE_NAME));
            trailer.setUrl("https://www.youtube.com/watch?v=" + movieTrailerJson.getString(TMDB_TRAILER_YOUTUBE_SOURCE));

            result.getTrailers().add(trailer);
        }

        JSONObject movieReviewsJson = movieDetailsJson.getJSONObject(TMDB_REVIEWS);
        JSONArray movieReviewsResultJson = movieReviewsJson.getJSONArray(TMDB_REVIEWS_RESULTS);

        for (int i = 0; i < movieReviewsResultJson.length(); i++) {

            // Get the JSON object representing the day
            JSONObject movieReviewJson = movieReviewsResultJson.getJSONObject(i);
            Review review = new Review();
            review.setAuthor(movieReviewJson.getString(TMDB_REVIEWS_RESULTS_AUTHOR));
            review.setContent(movieReviewJson.getString(TMDB_REVIEWS_RESULTS_CONTENT));

            result.getReviews().add(review);
        }

        return result;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);

        movie.setRating(mMovie.getRating());
        movie.setPoster(mMovie.getPoster());
        movie.setOverview(mMovie.getOverview());
        movie.setRelease(mMovie.getRelease());
        movie.setId(mMovie.getId());
        movie.setTitle(mMovie.getTitle());

        mCallback.onMovieDetailsComplete(movie);
    }
}
