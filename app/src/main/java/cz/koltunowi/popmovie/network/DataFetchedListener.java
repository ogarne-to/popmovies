package cz.koltunowi.popmovie.network;

/**
 * Created by jedrz on 26.02.2017.
 */

public interface DataFetchedListener {

    void onDataFetched(String data, int key);
}
