package cz.koltunowi.popmovie.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by jedrz on 26.02.2017.
 */

public class CursorLoaderCallbacks implements LoaderCallbacks<Cursor> {
    public static final String TAG = CursorLoaderCallbacks.class.getSimpleName();
    public static final String KEY_CONTENT_URI_EXTRA = "uri";



    private CursorLoaderResultListener crl;
    private Context context;

    public CursorLoaderCallbacks (Context context, CursorLoaderResultListener crl) {
        this. context = context;
        this.crl = crl;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader()");

        if (args == null) {
            Log.d(TAG, "Bundle null");
            return null;
        }

        String uriString = args.getString(KEY_CONTENT_URI_EXTRA);
        Uri favoritesUri = Uri.parse(uriString);

        return new CursorLoader(context,
                favoritesUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        crl.onCursorDataLoaded(data);
        Log.d(TAG, "onLoadFinished()");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        crl.onCursorReset();
        Log.d(TAG, "onLoaderReset()");
    }


}
