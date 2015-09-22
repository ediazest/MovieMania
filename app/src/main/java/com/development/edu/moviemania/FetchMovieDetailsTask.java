package com.development.edu.moviemania;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.development.edu.moviemania.data.Movie;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edu on 26/08/2015.
 */
public class FetchMovieDetailsTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieDetailsTask(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(String... params) {
        {
            Log.v(LOG_TAG, "" + params.length);
            if (params == null || params.length < 2)
                return null;

            String movieId = params[0];
            String apiKey = params[1];

            Log.v(LOG_TAG, "Parameters: " + movieId + " , " + apiKey);
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesListJsonStr = null;

            try {

                //http://api.themoviedb.org/3/movie/76341?api_key=103d17c09399fc788a4e2f5f663fd1a8&append_to_response=trailers,reviews
                final String MOVIE_LIST_BASE_URL =
                        "http://api.themoviedb.org/";
                final String API_KEY_PARAM = "api_key";

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
                moviesListJsonStr = buffer.toString();
                Log.v(LOG_TAG, moviesListJsonStr);

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
                    getMoviesDataFromJson(moviesListJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private List<Movie> getMoviesDataFromJson(String moviesListJsonStr) throws JSONException {

        List<Movie> movies = new ArrayList<Movie>();

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_LIST = "results";

        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_RELEASE = "release_date";
        final String TMDB_ID = "id";

        JSONObject moviesJson = new JSONObject(moviesListJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_LIST);

        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject movieJson = moviesArray.getJSONObject(i);

            Movie movie = new Movie();

            movie.setId(movieJson.getInt(TMDB_ID));
            movie.setPoster(movieJson.getString(TMDB_POSTER_PATH));
            movie.setTitle(movieJson.getString(TMDB_TITLE));
            movie.setOverview(movieJson.getString(TMDB_OVERVIEW));
            movie.setRating((float) movieJson.getDouble(TMDB_RATING));
            movie.setRelease(movieJson.getString(TMDB_RELEASE));

            movies.add(movie);

        }

        return movies;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
