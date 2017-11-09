package cz.koltunowi.popmovie;

import android.database.Cursor;

import cz.koltunowi.popmovie.data.MovieContract;

/**
 * Created by jedrz on 10.02.2017.
 */

public class HelperClasses {


    public static final String ACTIVITY_EXTRA_MOVIE_DETAILS = "movie_details";

    public static void printDatabase(Cursor data) {

        while (data.moveToNext()) {
            System.out.print( data.getString(MovieContract.FavoriteMovies.INDEX_TITLE) + " | ");
            System.out.print( data.getString(MovieContract.FavoriteMovies.INDEX_RELEASE_DATE) + " | ");
            System.out.print( data.getString(MovieContract.FavoriteMovies.INDEX_POPULARITY) + " | ");
            System.out.print( data.getString(MovieContract.FavoriteMovies.INDEX_VOTE_AVERAGE) + " | ");
            System.out.println( data.getString(MovieContract.FavoriteMovies.INDEX_MOVIE_ID));
        }
    }
}
