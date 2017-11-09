package cz.koltunowi.popmovie.network;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jedrz on 15.02.2017.
 */

public class DataLoaderCallbacks implements LoaderManager.LoaderCallbacks<String> {


    public static final String TAG = DataLoaderCallbacks.class.getSimpleName();
    public static final String LOADER_EXTRA_KEY_URL = "url";
    private DataFetchedListener dfl;
    private Context context;

    private String downloadedData;

    public DataLoaderCallbacks(DataFetchedListener dfl, Context context) {
        this.dfl = dfl;
        this.context = context;

    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");

        return new AsyncTaskLoader<String>(context) {

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading()");
                super.onStartLoading();
                if (bundle == null) {
                    Log.d(TAG, "Bundle null");
                    return;
                }

                if (downloadedData != null) {
                    deliverResult(downloadedData);
                } else {

                    forceLoad();
                }

            }

            @Override
            public String loadInBackground() {
                Log.d(TAG, "loadInBackground()");
                String searchUrl = bundle.getString(LOADER_EXTRA_KEY_URL);
                Log.d(TAG, "searchUrl: " + searchUrl);
                if (searchUrl == null || TextUtils.isEmpty(searchUrl)) {
                    return null;
                }

                try {
                    URL url = new URL(searchUrl);
                    return NetworkTools.getResponseFromHttpUrl(url);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                }
            }

            @Override
            public void deliverResult(String data) {
                downloadedData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "onLoadFinished()");
        try {

            dfl.onDataFetched(data, loader.getId());

        } catch (Exception e) {
            e.printStackTrace();
            dfl.onDataFetched(null, loader.getId());
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        Log.d(TAG, "onLoaderReset();");

    }
}
