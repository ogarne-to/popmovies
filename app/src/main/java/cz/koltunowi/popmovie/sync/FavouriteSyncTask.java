package cz.koltunowi.popmovie.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.koltunowi.popmovie.data.MovieContract;
import cz.koltunowi.popmovie.network.JSONUtils;
import cz.koltunowi.popmovie.network.NetworkTools;

/**
 * Created by jedrz on 24.02.2017.
 */

public class FavouriteSyncTask {


    synchronized public static void syncFavoriteDatabase(Context context) {

        List<Integer> ids = getFavoriteIds(context);

        if (ids == null || ids.size() == 0) {
            return;
        }

        int numberOfRowsAffected = 0;

        for (Integer id : ids) {

            URL url = NetworkTools.getMovieDetailsUrl(id);

            try {
                String movieData = NetworkTools.getResponseFromHttpUrl(url);
                ContentValues movieContentValues = JSONUtils.getMovieDetailsContentValues(movieData);
                System.out.println(movieContentValues.toString());
                if (movieContentValues != null && movieContentValues.size() > 0) {

                    int nr = context.getContentResolver().update(MovieContract.FavoriteMovies.getMovieUri(id),
                            movieContentValues,
                            null,
                            null);
                    System.out.println(nr+"");
                    numberOfRowsAffected += nr;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        System.out.println("Sync ended. Number of rows updated: " + numberOfRowsAffected);


    }


    /* Fetches cursor with favorite movies ids and returns ArrayList of values*/
    private static List<Integer> getFavoriteIds(Context context) {

        Cursor cursor = context.getContentResolver().query(MovieContract.FavoriteMovies.CONTENT_URI,
                new String[]{MovieContract.FavoriteMovies.COLUMN_MOVIE_ID},
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        List<Integer> ids = new ArrayList<>();
        int index = cursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_MOVIE_ID);
        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(index));
            System.out.println(cursor.getInt(index) + "");
        }

        cursor.close();

        return ids;
    }

}
