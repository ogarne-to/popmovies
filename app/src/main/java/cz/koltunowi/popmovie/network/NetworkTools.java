package cz.koltunowi.popmovie.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by jedrz on 10.02.2017.
 */



public class NetworkTools {

    public static final String BASE_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    public static final String BASE_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";
    public static final String BASE_URL_MOVIE_DETAILS = "https://api.themoviedb.org/3/movie";
    public static final String BASE_URL_PHOTO = "https://image.tmdb.org/t/p";

    public static final String PATH_PHOTO_SIZE = "w342";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";

    public static final String PARAM_API = "api_key";
    public static final String API_KEY = "***REMOVED***";



    public static URL buildUrl(String baseUrl) {

        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        return toURL(uri);
    }


    public static URL getPhotoUrl(String id) {
        Uri uri = Uri.parse(BASE_URL_PHOTO).buildUpon()
                .appendPath(PATH_PHOTO_SIZE)
                .appendEncodedPath(id)
                .build();

        return toURL(uri);
    }

    public static URL getMovieDetailsUrl(int id) {
        Uri uri = Uri.parse(BASE_URL_MOVIE_DETAILS).buildUpon()
                .appendPath(Integer.toString(id))
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        return toURL(uri);

    }

    public static URL getVideosUrl(int id) {
        Uri uri = Uri.parse(BASE_URL_MOVIE_DETAILS).buildUpon()
                .appendPath(Integer.toString(id))
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        return toURL(uri);
    }

    public static URL getReviewsUrl(int id) {
        Uri uri = Uri.parse(BASE_URL_MOVIE_DETAILS).buildUpon()
                .appendPath(Integer.toString(id))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        return toURL(uri);
    }


    private static URL toURL(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
            System.out.println(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }




    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
