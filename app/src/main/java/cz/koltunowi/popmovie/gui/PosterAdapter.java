package cz.koltunowi.popmovie.gui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import cz.koltunowi.popmovie.R;
import cz.koltunowi.popmovie.activities.MainActivity;
import cz.koltunowi.popmovie.model.Movie;
import cz.koltunowi.popmovie.network.NetworkTools;

/**
 * Created by jedrz on 10.02.2017.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    public static final String TAG = PosterAdapter.class.getSimpleName();
    PosterClickListener pcl;
    ArrayList<Movie> data;
    MainActivity context;

    public PosterAdapter(ArrayList<Movie> movies, MainActivity context, PosterClickListener pcl) {
        this.data = new ArrayList<>();
        this.context = context;
        this.pcl = pcl;
    }

    @Override
    public PosterAdapter.PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster, null);
        PosterViewHolder pvh = new PosterViewHolder(view);
        return pvh;

    }

    @Override
    public void onBindViewHolder(PosterAdapter.PosterViewHolder holder, int position) {
        Movie movie = data.get(position);

        URL url = NetworkTools.getPhotoUrl(movie.posterPath);
        Picasso.with(context)
                .load(url.toString())
                .placeholder(context.getResources().getDrawable(R.drawable.image_picasso_placeholder))
                .into(holder.poster);

        holder.title.setText(movie.title);

        if (context.getCurrentSort() == MainActivity.SORT_POPULAR) {
            int intPopularity = (int) Math.round(movie.popularity);
            holder.popularity.setText(String.valueOf(intPopularity));
        } else {
            holder.popularity.setText(String.valueOf(movie.voteAverage));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView poster;
        TextView title;
        TextView popularity;

        public PosterViewHolder(View itemView) {
            super(itemView);
            this.poster = (ImageView) itemView.findViewById(R.id.iv_background);
            this.title = (TextView) itemView.findViewById(R.id.tv_title);
            this.popularity = (TextView) itemView.findViewById(R.id.tv_popularity);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            pcl.onPosterClicked(getAdapterPosition(), poster);
        }
    }

    public void swapData(ArrayList<Movie> movies) {
        this.data = movies;
    }

    public ArrayList<Movie> getData() {
        return data;
    }


}
