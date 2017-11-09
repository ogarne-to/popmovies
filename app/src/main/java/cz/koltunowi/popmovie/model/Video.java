package cz.koltunowi.popmovie.model;

import android.net.Uri;

/**
 * Created by jedrz on 27.02.2017.
 */

public class Video {

    public static final String KEY_YOUTUBE = "YouTube";
    public static final int VIEW_TAG_KEY = 1;

    public static final String YOUTUBE_BASE_URI = "http://www.youtube.com/watch";

    public String key;
    public String name;
    public String site;
    public String type;

    public static Uri getYTUri(String key) {
        return Uri.parse(YOUTUBE_BASE_URI).buildUpon()
                .appendEncodedPath("?v=" + key)
                .build();
    }

}
