package cz.koltunowi.popmovie.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import cz.koltunowi.popmovie.model.Movie;

/**
 * Created by jedrz on 10.02.2017.
 */

public class FetchMoviesTask extends AsyncTask<URL, Void, String> {

    public static final String TAG = FetchMoviesTask.class.getSimpleName();

    MoviesResultListener listener;

    public FetchMoviesTask(MoviesResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];
        try {
            return NetworkTools.getResponseFromHttpUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        ArrayList<Movie> movies = JSONUtils.getMovies(result);
        if (movies != null) {
            listener.onFetchMoviesResult(movies);
        } else {
            listener.onFetchMoviesResult(null);
        }

    }
}
