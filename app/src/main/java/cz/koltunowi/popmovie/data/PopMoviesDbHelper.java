package cz.koltunowi.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies;

/**
 * Created by jedrz on 23.02.2017.
 */

public class PopMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popmovies.db";
    public static final int DATABASE_VERSION = 3;

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =

                "CREATE TABLE " + FavoriteMovies.TABLE_NAME + " (" +
                FavoriteMovies._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMovies.COLUMN_MOVIE_ID      + " INTEGER NOT NULL, "                     +
                FavoriteMovies.COLUMN_TITLE         + " TEXT NOT NULL, "                     +
                FavoriteMovies.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, "                     +
                FavoriteMovies.COLUMN_POPULARITY    + " REAL NOT NULL, "                     +
                FavoriteMovies.COLUMN_VOTE_AVERAGE  + " REAL NOT NULL, "                     +
                FavoriteMovies.COLUMN_OVERVIEW      + " TEXT, "                              +
                FavoriteMovies.COLUMN_POSTER_PATH   + " TEXT, "                              +

                " UNIQUE (" + FavoriteMovies.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovies.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
