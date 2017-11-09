package cz.koltunowi.popmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;

import cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies;

/**
 * Created by jedrz on 23.02.2017.
 */

public class MoviesContentProvider extends ContentProvider {

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITE_MOVIE_DETAILS = 101;

    public static final UriMatcher sUriMatcher = builUriMatcher();
    private PopMoviesDbHelper mDbHelper;

    private static UriMatcher builUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE,
                CODE_FAVORITES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE + "/#",
                CODE_FAVORITE_MOVIE_DETAILS);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        System.out.println(uri);

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:

                cursor = mDbHelper.getReadableDatabase().query(FavoriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITE_MOVIE_DETAILS:

                cursor = mDbHelper.getReadableDatabase().query(FavoriteMovies.TABLE_NAME,
                        projection,
                        FavoriteMovies.COLUMN_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        System.out.println(uri);

        int numberOfRowsAffected = 0;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIE_DETAILS:
                numberOfRowsAffected = mDbHelper.getWritableDatabase()
                        .delete(FavoriteMovies.TABLE_NAME,
                                FavoriteMovies.COLUMN_MOVIE_ID + "=?",
                                new String[]{uri.getLastPathSegment()});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsAffected;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        System.out.println(uri);

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                long id = mDbHelper.getWritableDatabase()
                        .insert(FavoriteMovies.TABLE_NAME,
                                null,
                                values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovies.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into: " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        System.out.println(uri);

        int numberOfRowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIE_DETAILS :
                numberOfRowsUpdated = mDbHelper.getWritableDatabase()
                        .update(FavoriteMovies.TABLE_NAME,
                                values,
                                FavoriteMovies.COLUMN_MOVIE_ID + "=?",
                                new String[]{uri.getLastPathSegment()});
                break;
            default :
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if (numberOfRowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

}

