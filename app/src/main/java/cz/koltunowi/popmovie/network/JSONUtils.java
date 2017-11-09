package cz.koltunowi.popmovie.network;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.koltunowi.popmovie.model.Movie;
import cz.koltunowi.popmovie.model.Review;
import cz.koltunowi.popmovie.model.Video;

import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_MOVIE_ID;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_OVERVIEW;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_POPULARITY;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_POSTER_PATH;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_RELEASE_DATE;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_TITLE;
import static cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies.COLUMN_VOTE_AVERAGE;


/**
 * Created by jedrz on 10.02.2017.
 */

public class JSONUtils {

    public static final String TAG = JSONUtils.class.getSimpleName();


    public static final String JSON_KEY_RESULTS = "results";
    public static final String JSON_KEY_MOVIE_ID = "id";
    public static final String JSON_KEY_TITLE = "title";
    public static final String JSON_KEY_OVERVIEW = "overview";
    public static final String JSON_KEY_POPULARITY = "popularity";
    public static final String JSON_KEY_POSTER_PATH = "poster_path";
    public static final String JSON_KEY_RELEASE_DATE = "release_date";
    public static final String JSON_KEY_VOTE_AVERAGE = "vote_average";

    public static final String JSON_KEY_GENRES = "genres";
    public static final String JSON_KEY_GENRE_NAME = "name";
    public static final String JSON_KEY_RUNTIME = "runtime";

    public static final String JSON_KEY_VIDEOS_RESULTS = "results";
    public static final String JSON_KEY_VIDEOS_KEY = "key";
    public static final String JSON_KEY_VIDEOS_NAME = "name";
    public static final String JSON_KEY_VIDEOS_SITE = "site";
    public static final String JSON_KEY_VIDEOS_TYPE = "type";


    public static final String JSON_KEY_REVIEWS_RESULTS = "results";
    public static final String JSON_KEY_REVIEWS_AUTHOR= "author";
    public static final String JSON_KEY_REVIEWS_URL = "url";
    public static final String JSON_KEY_REVIEWS_CONTENT = "content";



    public static ArrayList<Movie> getMovies(String rawData) {
        if (rawData == null) {
            return null;
        }

        try {
            JSONObject jsonData = new JSONObject(rawData);
            return extractListOfMovies(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ContentValues getMovieDetailsContentValues(String rawData) {
        if (rawData == null) {
            return null;
        }

        try {
            JSONObject jsonData = new JSONObject(rawData);
            return extractMovieDetails(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bundle getAdditionalInfo(String rawData) {
        if (rawData == null) {
            return null;
        }

        try {
            JSONObject jsonData = new JSONObject(rawData);
            return extractAdditionalInfo(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Video> getVideos(String rawData) {
        if (rawData == null) {
            return null;
        }

        try {
            JSONObject jsonData = new JSONObject(rawData);
            return extractVideos(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Review> getReviews(String rawData) {
        if (rawData == null) {
            return null;
        }

        try {
            JSONObject jsonData = new JSONObject(rawData);
            return extractReviews(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




    private static ArrayList<Movie> extractListOfMovies(JSONObject jsonData) {

        JSONArray results = null;
        try {
            results = jsonData.getJSONArray(JSON_KEY_RESULTS);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONUtils.extractListOfMovies(): Error or empty results");
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject result = results.getJSONObject(i);
                Movie movie = new Movie();

                movie.movieId = result.getInt(JSON_KEY_MOVIE_ID);
                movie.title = result.getString(JSON_KEY_TITLE);
                movie.overview = result.getString(JSON_KEY_OVERVIEW);
                movie.popularity = result.getDouble(JSON_KEY_POPULARITY);
                movie.voteAverage = result.getDouble(JSON_KEY_VOTE_AVERAGE);
                movie.posterPath = result.getString(JSON_KEY_POSTER_PATH);
                movie.releaseDate = result.getString(JSON_KEY_RELEASE_DATE);

                movies.add(movie);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Missing data in results JSONArray");
                return null;

            }
        }

        return movies;
    }

    private static ContentValues extractMovieDetails(JSONObject jsonData) {
        ContentValues movieDetails = new ContentValues();

        try {
            movieDetails.put(COLUMN_MOVIE_ID, jsonData.getInt(JSON_KEY_MOVIE_ID));
            movieDetails.put(COLUMN_TITLE, jsonData.getString(JSON_KEY_TITLE));
            movieDetails.put(COLUMN_RELEASE_DATE, jsonData.getString(JSON_KEY_RELEASE_DATE));
            movieDetails.put(COLUMN_POPULARITY, jsonData.getInt(JSON_KEY_POPULARITY));
            movieDetails.put(COLUMN_VOTE_AVERAGE, jsonData.getDouble(JSON_KEY_VOTE_AVERAGE));
            movieDetails.put(COLUMN_OVERVIEW, jsonData.getString(JSON_KEY_OVERVIEW));
            movieDetails.put(COLUMN_POSTER_PATH, jsonData.getString(JSON_KEY_POSTER_PATH));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Missing data in results JSONArray");
            return null;

        }

        return movieDetails;
    }

    private static Bundle extractAdditionalInfo(JSONObject jsonData) {

        Bundle bundle = new Bundle();

        try {
            bundle.putInt(JSON_KEY_RUNTIME, jsonData.getInt(JSON_KEY_RUNTIME));

            JSONArray ja = jsonData.getJSONArray(JSON_KEY_GENRES);
            StringBuilder genresBuilder = new StringBuilder();

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                genresBuilder.append(jo.getString(JSON_KEY_GENRE_NAME));
                if (i < ja.length() - 1) {
                    genresBuilder.append(", ");
                }

            }


            bundle.putString(JSON_KEY_GENRES, genresBuilder.toString());
            Log.d(TAG, bundle.toString());

            return bundle;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    private static List<Video> extractVideos(JSONObject jsonData) {
        JSONArray results = null;

        try {
            results = jsonData.getJSONArray(JSON_KEY_VIDEOS_RESULTS);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        List<Video> videos = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject videoJson = results.getJSONObject(i);

                Video video = new Video();

                video.key = videoJson.getString(JSON_KEY_VIDEOS_KEY);
                video.name = videoJson.getString(JSON_KEY_VIDEOS_NAME);
                video.site = videoJson.getString(JSON_KEY_VIDEOS_SITE);
                video.type = videoJson.getString(JSON_KEY_VIDEOS_TYPE);

                videos.add(video);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

        return videos;
    }

    private static List<Review> extractReviews(JSONObject jsonData) {
        JSONArray results = null;

        try {
            results = jsonData.getJSONArray(JSON_KEY_REVIEWS_RESULTS);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject reviewJson = results.getJSONObject(i);

                Review review = new Review();

                review.author = reviewJson.getString(JSON_KEY_REVIEWS_AUTHOR);
                review.url = reviewJson.getString(JSON_KEY_REVIEWS_URL);

                String reviewString = reviewJson.getString(JSON_KEY_REVIEWS_CONTENT);

                reviewString = reviewString.replace("\r\n", " ").replace("\n", " ").trim();

                if (reviewString.length() >= 300) {
                    reviewString = reviewString.substring(0,300) + "...";
                }

                review.content = reviewString;

                reviews.add(review);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

        return reviews;

    }


}
