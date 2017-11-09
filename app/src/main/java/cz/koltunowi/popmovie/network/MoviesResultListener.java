package cz.koltunowi.popmovie.network;

import java.util.ArrayList;

import cz.koltunowi.popmovie.model.Movie;

/**
 * Created by jedrz on 10.02.2017.
 */

public interface MoviesResultListener {
    void onFetchMoviesResult(ArrayList<Movie> movies);
}
