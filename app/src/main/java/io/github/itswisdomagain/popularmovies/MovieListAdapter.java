package io.github.itswisdomagain.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ItemViewHolder> {
    private final MoviesListClickListener clickListener;
    private final ArrayList<Movie> movies;
    private Context mContext;

    MovieListAdapter(MoviesListClickListener clickListener, ArrayList<Movie> movies) {
        this.clickListener = clickListener;
        this.movies = movies;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.movies_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(mContext)
                .load(movie.getPoster_path())
                .placeholder(R.drawable.loading_image)
                .into(holder.getImgMovieThumbnail());
    }

    private Movie getItem(int position){
        return movies.get(position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    interface MoviesListClickListener {
        void onMovieSelected(Movie movie);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imgMovieThumbnail;

        ItemViewHolder(View itemView) {
            super(itemView);
            imgMovieThumbnail = (ImageView) itemView;
            imgMovieThumbnail.setOnClickListener(this);
        }

        ImageView getImgMovieThumbnail() {
            return imgMovieThumbnail;
        }

        @Override
        public void onClick(View v) {
            Movie selected = MovieListAdapter.this.getItem(getAdapterPosition());
            clickListener.onMovieSelected(selected);
        }
    }
}
