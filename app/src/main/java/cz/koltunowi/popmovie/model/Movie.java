package cz.koltunowi.popmovie.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;

import cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies;

/**
 * Created by jedrz on 10.02.2017.
 */

public class Movie {

    public static final String KEY_MOVIE_ID = "movie_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_POPULARITY = "popularity";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_RELEASE_DATE = "release_date";

    public int movieId;
    public String title;
    public String overview;
    public double popularity;
    public double voteAverage;
    public String posterPath;
    public String releaseDate;

    public Movie(){}

    public Movie(int movieId, String title, double popularity, double voteAverage,
                 String releaseDate, String posterPath, String overview) {

        this.movieId = movieId;
        this.title = title;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    private Movie(Bundle bundle) {
        this.movieId = bundle.getInt(KEY_MOVIE_ID);
        this.title = bundle.getString(KEY_TITLE);
        this.overview = bundle.getString(KEY_OVERVIEW);
        this.popularity = bundle.getDouble(KEY_POPULARITY);
        this.posterPath = bundle.getString(KEY_POSTER_PATH);
        this.voteAverage = bundle.getDouble(KEY_VOTE_AVERAGE);
        this.releaseDate = bundle.getString(KEY_RELEASE_DATE);
    }

    public ArrayList<Movie> getArrayFromCursor (Cursor cursor) {

        while (cursor.moveToNext()) {

            int movieId = cursor.getInt(FavoriteMovies.INDEX_MOVIE_ID);
            String title = cursor.getString(FavoriteMovies.INDEX_TITLE);
            String overview = cursor.getString(FavoriteMovies.INDEX_OVERVIEW);
            double popularity = cursor.getDouble(FavoriteMovies.INDEX_POPULARITY);
            String posterPath = cursor.getString(FavoriteMovies.INDEX_POSTER_PATH);
            double voteAverage = cursor.getDouble(FavoriteMovies.INDEX_VOTE_AVERAGE);
            String releaseDate = cursor.getString(FavoriteMovies.INDEX_RELEASE_DATE);

            Movie movie = new Movie(movieId, title, popularity, voteAverage, releaseDate, posterPath, overview);

        }
        return null;
    }

    private Movie (Cursor cursor) {
        this.movieId = cursor.getInt(FavoriteMovies.INDEX_MOVIE_ID);
        this.title = cursor.getString(FavoriteMovies.INDEX_TITLE);
        this.overview = cursor.getString(FavoriteMovies.INDEX_OVERVIEW);
        this.popularity = cursor.getDouble(FavoriteMovies.INDEX_POPULARITY);
        this.posterPath = cursor.getString(FavoriteMovies.INDEX_POSTER_PATH);
        this.voteAverage = cursor.getDouble(FavoriteMovies.INDEX_VOTE_AVERAGE);
        this.releaseDate = cursor.getString(FavoriteMovies.INDEX_RELEASE_DATE);
    }

    public static Movie fromBundle(Bundle bundle) {
        return new Movie(bundle);
    }

    public static Movie fromCursor(Cursor cursor) {
        return new Movie(cursor);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putDouble(KEY_POPULARITY, popularity);
        bundle.putDouble(KEY_VOTE_AVERAGE, voteAverage);
        bundle.putString(KEY_POSTER_PATH, posterPath);
        bundle.putString(KEY_RELEASE_DATE, releaseDate);

        return bundle;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMovies.COLUMN_MOVIE_ID, movieId);
        cv.put(FavoriteMovies.COLUMN_TITLE, title);
        cv.put(FavoriteMovies.COLUMN_RELEASE_DATE, releaseDate);
        cv.put(FavoriteMovies.COLUMN_POPULARITY, popularity);
        cv.put(FavoriteMovies.COLUMN_VOTE_AVERAGE, voteAverage);
        cv.put(FavoriteMovies.COLUMN_OVERVIEW, overview);
        cv.put(FavoriteMovies.COLUMN_POSTER_PATH, posterPath);

        return cv;
    }
}
