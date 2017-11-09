package cz.koltunowi.popmovie.data;

import android.database.Cursor;

/**
 * Created by jedrz on 26.02.2017.
 */

public interface CursorLoaderResultListener {
    void onCursorDataLoaded(Cursor cursor);
    void onCursorReset();

}
