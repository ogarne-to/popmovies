package cz.koltunowi.popmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jedrz on 23.02.2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "cz.koltunowi.popmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /*Favorite Movies table*/

    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteMovies implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";


        /* These are indexes of colums */
        public static final int INDEX_MOVIE_ID = 1;
        public static final int INDEX_TITLE = 2;
        public static final int INDEX_RELEASE_DATE = 3;
        public static final int INDEX_POPULARITY = 4;
        public static final int INDEX_VOTE_AVERAGE = 5;
        public static final int INDEX_OVERVIEW = 6;
        public static final int INDEX_POSTER_PATH = 7;

        public static Uri getMovieUri(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

    }

}
