package cz.koltunowi.popmovie.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

import cz.koltunowi.popmovie.HelperClasses;
import cz.koltunowi.popmovie.R;
import cz.koltunowi.popmovie.data.CursorLoaderCallbacks;
import cz.koltunowi.popmovie.data.CursorLoaderResultListener;
import cz.koltunowi.popmovie.data.MovieContract;
import cz.koltunowi.popmovie.gui.GridSpacingItemDecoration;
import cz.koltunowi.popmovie.gui.PosterAdapter;
import cz.koltunowi.popmovie.gui.PosterClickListener;
import cz.koltunowi.popmovie.model.Movie;
import cz.koltunowi.popmovie.network.DataFetchedListener;
import cz.koltunowi.popmovie.network.DataLoaderCallbacks;
import cz.koltunowi.popmovie.network.JSONUtils;
import cz.koltunowi.popmovie.network.NetworkTools;
import cz.koltunowi.popmovie.sync.FavoriteSyncUtils;

public class MainActivity extends AppCompatActivity implements DataFetchedListener, CursorLoaderResultListener,
        PosterClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();


    // sort id is also the id of loaders
    public static final int SORT_POPULAR = 1;
    public static final int SORT_TOP_RATED = 2;
    public static final int SORT_FAVORITES = 3;

    public static final int DETAIL_ACTIVITY_REQUEST_CODE = 42;

    public static final String KEY_INSTANCE_STATE_SORT = "sort";

    private int mCurrentSort;
    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;
    private ProgressBar mProgressBar;
    private ImageView mErrorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentSort = savedInstanceState.getInt(KEY_INSTANCE_STATE_SORT);
        } else {
            mCurrentSort = SORT_POPULAR;
        }



        mRecyclerView = (RecyclerView) findViewById(R.id.rv_poster_grid);
        int space = Math.round(8 * getResources().getDisplayMetrics().density);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, space, true));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mPosterAdapter = new PosterAdapter(null, this, this);
        mRecyclerView.setAdapter(mPosterAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_bar);
        mErrorImage = (ImageView) findViewById(R.id.iv_no_connection);


        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        fetchData();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
        outState.putInt(KEY_INSTANCE_STATE_SORT, mCurrentSort);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_sort_toggle);
        if (mCurrentSort == SORT_POPULAR) {
            item.setIcon(R.drawable.ic_action_popular);
        } else if (mCurrentSort == SORT_TOP_RATED) {
            item.setIcon(R.drawable.ic_action_top_rated);
        } else if (mCurrentSort == SORT_FAVORITES) {
            item.setIcon(R.drawable.ic_star_favorite_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_toggle:
                if (mCurrentSort == SORT_POPULAR) {
                    mCurrentSort = SORT_TOP_RATED;
                    item.setIcon(R.drawable.ic_action_top_rated);
                    fetchData();
                    break;

                } else if (mCurrentSort == SORT_TOP_RATED) {
                    mCurrentSort = SORT_FAVORITES;
                    item.setIcon(R.drawable.ic_star_favorite_white);
                    fetchData();
                    break;

                } else if (mCurrentSort == SORT_FAVORITES) {
                    mCurrentSort = SORT_POPULAR;
                    item.setIcon(R.drawable.ic_action_popular);
                    fetchData();
                    break;
                }

            case R.id.action_settings:
                Intent startSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsIntent);
                break;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_switch_sync_key))) {
            boolean sync = sharedPreferences.getBoolean(key, true);

            if (sync) {
                String interval = sharedPreferences.getString(getString(R.string.pref_list_sync_interval_key),
                        getString(R.string.pref_list_interval_value_3));
                FavoriteSyncUtils.scheduleFavoriteSync(this, interval);

            } else {
                FavoriteSyncUtils.stopFavoriteSync(this);
            }
        } else if (key.equals(getString(R.string.pref_list_sync_interval_key))) {
            String interval = sharedPreferences.getString(getString(R.string.pref_list_sync_interval_key),
                    getString(R.string.pref_list_interval_value_3));
            if (sharedPreferences.getBoolean(getString(R.string.pref_switch_sync_key), true)) {
                FavoriteSyncUtils.restartFavoriteSync(this, interval);

            }
        }

    }

    private void fetchData() {
        Log.d(TAG, "Current sort: " + mCurrentSort);

        if (mCurrentSort == SORT_POPULAR || mCurrentSort == SORT_TOP_RATED) {
            fetchMoviesFromDatabase();
        } else {
            fetchMoviesFromLocalDatabase();
        }
    }

    private void fetchMoviesFromDatabase() {
        hideConnectionError();
        showProgressBar();


        if (isNetworkAvailable()) {

            // chooses the build URL based on currently used sorting
            URL url = NetworkTools.buildUrl(mCurrentSort == SORT_POPULAR ?
                    NetworkTools.BASE_URL_POPULAR : NetworkTools.BASE_URL_TOP_RATED);

            Bundle queryBundle = new Bundle();
            queryBundle.putString(DataLoaderCallbacks.LOADER_EXTRA_KEY_URL, url.toString());

            LoaderManager lm = getSupportLoaderManager();


            // Start DataLoader
            Loader<String> loader = lm.getLoader(mCurrentSort);
            DataLoaderCallbacks mlc = new DataLoaderCallbacks(this, this);

            if (loader == null) {
                lm.initLoader(mCurrentSort, queryBundle, mlc);
            } else {
                lm.restartLoader(mCurrentSort, queryBundle, mlc);
            }


        } else {
            hideProgressBar();
            showConnectionError();
            Toast.makeText(this, R.string.toast_error_connectivity_problem, Toast.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    public void onDataFetched(String data, int id) {
        Log.d(TAG, id +"/" + mCurrentSort);

        if (id != mCurrentSort) {
            return;
        }

        ArrayList<Movie> movies = JSONUtils.getMovies(data);


        if (movies != null && movies.size() > 0) {
            mPosterAdapter.swapData(movies);
            mPosterAdapter.notifyDataSetChanged();
            hideProgressBar();

        } else {
            showConnectionError();
            Toast.makeText(this, R.string.toast_error_general_problem, Toast.LENGTH_SHORT).show();
        }

    }


    private void fetchMoviesFromLocalDatabase() {
        hideConnectionError();
        showProgressBar();


        // Prepare data bundle for database loader
        Uri contentUri = MovieContract.FavoriteMovies.CONTENT_URI;

        Bundle bundle = new Bundle();
        bundle.putString(CursorLoaderCallbacks.KEY_CONTENT_URI_EXTRA, contentUri.toString());


        LoaderManager lm = getSupportLoaderManager();
        Loader<Cursor> loader = lm.getLoader(SORT_FAVORITES);


        if (loader == null) {
            lm.initLoader(SORT_FAVORITES, bundle, new CursorLoaderCallbacks(this, this));
        } else {
            lm.restartLoader(SORT_FAVORITES, bundle, new CursorLoaderCallbacks(this, this));
        }
    }


    @Override
    public void onCursorDataLoaded(Cursor cursor) {

        if (mCurrentSort != SORT_FAVORITES) {
            return;
        }


        ArrayList<Movie> movies = new ArrayList<>();
        while (cursor.moveToNext()) {
            movies.add(Movie.fromCursor(cursor));
        }
        mPosterAdapter.swapData(movies);
        mPosterAdapter.notifyDataSetChanged();

        if (mCurrentSort == SORT_FAVORITES) {
            hideProgressBar();
        }
    }

    @Override
    public void onCursorReset() {
        mPosterAdapter.swapData(null);
    }


    @Override
    public void onPosterClicked(int position, ImageView view) {
        navigateToDetails(position, view);
    }

    private void navigateToDetails(int position, ImageView view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle bundle = mPosterAdapter.getData().get(position).toBundle();
        intent.putExtra(HelperClasses.ACTIVITY_EXTRA_MOVIE_DETAILS, bundle);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bundle transitionInfo = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, view, view.getTransitionName()).toBundle();
            startActivityForResult(intent,  DETAIL_ACTIVITY_REQUEST_CODE, transitionInfo);
        } else {
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && mCurrentSort == SORT_FAVORITES) {
                fetchData();
            }
        }
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showConnectionError() {
        mErrorImage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void hideConnectionError() {
        mErrorImage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    public int getCurrentSort() {
        return mCurrentSort;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }






}
