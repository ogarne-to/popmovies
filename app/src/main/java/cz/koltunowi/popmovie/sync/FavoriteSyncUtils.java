package cz.koltunowi.popmovie.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by jedrz on 24.02.2017.
 */

public class FavoriteSyncUtils {

    public static final String TAG = FavoriteSyncUtils.class.getSimpleName();
    //TODO: put some sane value here
    private static final int SYNC_FLEXTIME_SECONDS = 15;

    public static final String FAVORITE_SYNC_TAG = "job_sync_favorites_tag";

    public static boolean sInitialized;


    synchronized public static void scheduleFavoriteSync(@NonNull final Context context, String interval) {
        Log.d(TAG, "scheduling JobService with interval: " + interval);

        int intervalSeconds = Integer.valueOf(interval) * 60 * 60;

        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //TODO: Add .setConstraints(Constraint.ON_UNMETERED_NETWORK)
        Job syncFavoriteDatabaseJob = dispatcher.newJobBuilder()
                .setService(FavoritesSyncJobService.class)
                .setTag(FAVORITE_SYNC_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(intervalSeconds,
                        intervalSeconds + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncFavoriteDatabaseJob);

        sInitialized = true;
    }

    public static void stopFavoriteSync(Context context) {
        Log.d(TAG, "stopping JobService");
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        dispatcher.cancel(FAVORITE_SYNC_TAG);
    }

    public static void restartFavoriteSync(Context context, String interval) {
        Log.d(TAG, "restarting JobService with interval: " + interval);
        stopFavoriteSync(context);
        scheduleFavoriteSync(context, interval);

    }
}
