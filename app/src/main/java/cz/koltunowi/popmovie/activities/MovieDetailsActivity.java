package cz.koltunowi.popmovie.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import cz.koltunowi.popmovie.HelperClasses;
import cz.koltunowi.popmovie.R;
import cz.koltunowi.popmovie.data.MovieContract.FavoriteMovies;
import cz.koltunowi.popmovie.model.Movie;
import cz.koltunowi.popmovie.model.Review;
import cz.koltunowi.popmovie.model.Video;
import cz.koltunowi.popmovie.network.DataFetchedListener;
import cz.koltunowi.popmovie.network.DataLoaderCallbacks;
import cz.koltunowi.popmovie.network.JSONUtils;
import cz.koltunowi.popmovie.network.NetworkTools;

public class MovieDetailsActivity extends AppCompatActivity implements DataFetchedListener,
                                                                       View.OnClickListener {

    public static final String TAG = MovieDetailsActivity.class.getSimpleName();

    public static final int ADDITIONAL_INFO_LOADER = 23423;
    public static final int VIDEO_INFO_LOADER = 34598773;
    public static final int REVIEWS_LOADER = 3466296;


    private Movie mMovieData;
    private boolean mCurrentFavoriteState;
    private boolean mInitialFavoriteState;

    private ImageView mStarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportLoaderManager()
                .initLoader(ADDITIONAL_INFO_LOADER, null, new DataLoaderCallbacks(this, this));


        /*JUST FOR DEBUGGING*/
        Cursor cursor = getContentResolver().query(FavoriteMovies.CONTENT_URI, null, null, null, null);
        HelperClasses.printDatabase(cursor);
        cursor.close();

        /* SET UP TOOLBAR*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /* TAKE DATA FROM INTENT AND POPULATE VIEWS*/
        Bundle bundle = getIntent().getExtras().getBundle(HelperClasses.ACTIVITY_EXTRA_MOVIE_DETAILS);
        mMovieData = Movie.fromBundle(bundle);

        ImageView backgroundImageView = (ImageView) findViewById(R.id.iv_background);
        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        TextView dateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);
        TextView voteAverage = (TextView) findViewById(R.id.tv_vote_average);
        TextView popularity = (TextView) findViewById(R.id.tv_popularity);


        URL url = NetworkTools.getPhotoUrl(mMovieData.posterPath);
        Picasso.with(this).load(url.toString()).into(backgroundImageView);

        titleTextView.setText(mMovieData.title);
        dateTextView.setText(mMovieData.releaseDate);
        overviewTextView.setText(mMovieData.overview);
        voteAverage.setText(String.valueOf(mMovieData.voteAverage));

        int intPopularity = (int) Math.round(mMovieData.popularity);
        popularity.setText(String.valueOf(intPopularity));

        ScrollView moviesScrollView = (ScrollView) findViewById(R.id.sv_movies_scroll_view);


        /* Dynamically sets the offset of the scrollView so the moving
        * pane is visible on all devices */

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        moviesScrollView.setPadding(0, (int) (0.80 * metrics.heightPixels), 0, 0);


         /* Checks if the movie is in the local favorite database and sets the star ImageView
         accordingly */

        getFavoriteState(mMovieData.movieId);
        mStarImageView = (ImageView) findViewById(R.id.iv_favorite_star);
        setStarIcon();

        fetchExtraData(NetworkTools.getMovieDetailsUrl(mMovieData.movieId), ADDITIONAL_INFO_LOADER);
        fetchExtraData(NetworkTools.getVideosUrl(mMovieData.movieId), VIDEO_INFO_LOADER);
        fetchExtraData(NetworkTools.getReviewsUrl(mMovieData.movieId), REVIEWS_LOADER);

    }



    @Override
    protected void onStop() {
        super.onStop();

        if (mCurrentFavoriteState != mInitialFavoriteState) {

            Intent intent = getIntent();
            setResult(RESULT_OK, intent);

        }
    }

    /* Checks in local database if the movie is favorited, and changes icon accordingly*/
    private void getFavoriteState(int movieId) {
        Uri uri = FavoriteMovies.getMovieUri(movieId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mCurrentFavoriteState = true;

        } else {
            mCurrentFavoriteState = false;
        }

        mInitialFavoriteState = mCurrentFavoriteState;

        cursor.close();

    }

    /* Switches the drawable of favorite button */
    private void setStarIcon() {
        if (mCurrentFavoriteState) {
            mStarImageView.setImageResource(R.drawable.ic_star_favorite_true);

        } else {
            mStarImageView.setImageResource(R.drawable.ic_star_favorite_false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    /* Flips the mIsFavorite value and inserts movie info into database or deletes from it.
    * Also changes starIcon state */
    public void onStarClicked(View view) {

        mCurrentFavoriteState = !mCurrentFavoriteState;
        System.out.println(mCurrentFavoriteState);


        if (!mCurrentFavoriteState) {
            Uri uri = FavoriteMovies.getMovieUri(mMovieData.movieId);
            getContentResolver().delete(uri, null, null);

        } else {
            Uri uri = FavoriteMovies.CONTENT_URI;
            getContentResolver().insert(uri, mMovieData.getContentValues());
        }

        setStarIcon();

    }




    private void fetchExtraData(URL url, int loaderId) {

        if (NetworkTools.isNetworkAvailable(this)) {
            Log.d(TAG, url.toString());

            Bundle queryBundle = new Bundle();
            queryBundle.putString(DataLoaderCallbacks.LOADER_EXTRA_KEY_URL, url.toString());

            LoaderManager lm = getSupportLoaderManager();
            Loader<String> loader = lm.getLoader(loaderId);

            DataLoaderCallbacks dlc = new DataLoaderCallbacks(this, this);

            if (loader == null) {

                lm.initLoader(loaderId, queryBundle, dlc);
            } else {
                lm.restartLoader(loaderId, queryBundle, dlc);
            }
        }


    }


    /* Processes additional data, and sets the TextViews to display it */
    @Override
    public void onDataFetched(String data, int id) {
        if (data == null || data.length() == 0) {
            return;
        }

        switch (id) {
            case ADDITIONAL_INFO_LOADER :

                Bundle additionalInfoBundle = JSONUtils.getAdditionalInfo(data);

                if (additionalInfoBundle == null) {
                    return;
                }

                LinearLayout additionalInfo = (LinearLayout) findViewById(R.id.ll_additional_info);
                additionalInfo.setVisibility(View.VISIBLE);

                TextView runtimeTextView = (TextView) findViewById(R.id.tv_runtime);
                TextView genresTextView = (TextView) findViewById(R.id.tv_genres);

                String runtime =  additionalInfoBundle.getInt(JSONUtils.JSON_KEY_RUNTIME) + " mins";
                runtimeTextView.setText(runtime);
                genresTextView.setText(additionalInfoBundle.getString(JSONUtils.JSON_KEY_GENRES));
                break;

            case VIDEO_INFO_LOADER :
                List<Video> videos = JSONUtils.getVideos(data);

                if (videos == null || videos.size() <= 0) {
                    return;
                }

                (findViewById(R.id.tv_videos_heading)).setVisibility(View.VISIBLE);

                LinearLayout videosLinearLayout = (LinearLayout) findViewById(R.id.ll_videos);
                for (Video video : videos) {
                    if (video.site.equals(Video.KEY_YOUTUBE)) {
                        ConstraintLayout videoRow = (ConstraintLayout) getLayoutInflater().inflate(R.layout.video_row, null);


                        TextView textView = (TextView) videoRow.findViewById(R.id.tv_video_title);
                        textView.setText(video.name);


                        videoRow.setTag(R.string.video_row_view_tag, video.key);
                        videoRow.setOnClickListener(this);

                        videosLinearLayout.addView(videoRow);
                    }
                }

                break;

            case REVIEWS_LOADER :
                List<Review> reviews = JSONUtils.getReviews(data);

                if (reviews == null || reviews.size() <= 0) {
                    return;
                }

                (findViewById(R.id.tv_reviews_heading)).setVisibility(View.VISIBLE);


                LinearLayout reviewsLinearLayout = (LinearLayout) findViewById(R.id.ll_reviews);
                for (Review review : reviews) {

                    RelativeLayout reviewLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.review, null);


                    TextView author = (TextView) reviewLayout.findViewById(R.id.tv_review_author);
                    author.setText(review.author);

                    TextView content = (TextView) reviewLayout.findViewById(R.id.tv_review_content);
                    content.setText(review.content);

                    reviewsLinearLayout.addView(reviewLayout);

                }

                break;

        }
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag(R.string.video_row_view_tag);
        Uri uri = Video.getYTUri(tag);
        System.out.println(Video.getYTUri(tag).toString());
        startActivity(new Intent(Intent.ACTION_VIEW, uri));

    }
}
