package io.github.itswisdomagain.popularmovies;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (getIntent().hasExtra(Movie.MOVIE_INTENT_EXTRA)){
            Movie movie = getIntent().getParcelableExtra(Movie.MOVIE_INTENT_EXTRA);
            loadMovieDetails(movie);
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadMovieDetails(Movie movie){
        setViewText(R.id.tvMovieTitle, movie.getOriginal_title());
        setViewText(R.id.tvMovieSynopsis, movie.getOverview());
        setViewText(R.id.tvReleaseDate, String.format(getString(R.string.release_date), movie.getRelease_date()));

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        setViewText(R.id.tvRating, decimalFormat.format(movie.getVote_average()));

        ImageView imgMovieThumbnail = (ImageView)findViewById(R.id.imgMovieThumbnail);
        Picasso.with(MovieDetailActivity.this)
                .load(movie.getPoster_path())
                .placeholder(R.drawable.loading_image)
                .into(imgMovieThumbnail);
    }

    private void setViewText(int resourceId, String text){
        ((TextView)findViewById(resourceId)).setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
