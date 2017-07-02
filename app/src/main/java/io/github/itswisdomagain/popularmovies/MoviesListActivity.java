package io.github.itswisdomagain.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MoviesListActivity extends AppCompatActivity implements MovieListAdapter.MoviesListClickListener {
    private RecyclerView rvMoviePosters;
    private View progressBar;
    private String apiKey;
    private TextView tvError;

    private ArrayList<Movie> moviesList;

    private FetchMovieListOnlineTask fetchMovieListOnlineTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);
        progressBar = findViewById(R.id.progressBar);
        tvError = (TextView) findViewById(R.id.tvError);
        apiKey = getString(R.string.movie_db_api_key);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvMoviePosters.setLayoutManager(layoutManager);

        moviesList = new ArrayList<>(0);
        MovieListAdapter adapter = new MovieListAdapter(MoviesListActivity.this, moviesList);
        rvMoviePosters.setAdapter(adapter);

        fetchMovieListOnlineTask = new FetchMovieListOnlineTask();
        fetchMovieListOnlineTask.execute(Values.SORT_POPULAR);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.popularity_sorting_text));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((StaggeredGridLayoutManager)rvMoviePosters.getLayoutManager()).setSpanCount(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            ((StaggeredGridLayoutManager)rvMoviePosters.getLayoutManager()).setSpanCount(2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()){
            case R.id.menu_sort_popular:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setSubtitle(getString(R.string.popularity_sorting_text));
                reFetchMovies(Values.SORT_POPULAR);
                return true;

            case R.id.menu_sort_top_rated:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setSubtitle(getString(R.string.rating_sorting_text));
                reFetchMovies(Values.SORT_TOP_RATED);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reFetchMovies(String sort_key){
        moviesList.clear();
        rvMoviePosters.getAdapter().notifyDataSetChanged();

        fetchMovieListOnlineTask.cancel(true);
        fetchMovieListOnlineTask = new FetchMovieListOnlineTask();
        fetchMovieListOnlineTask.execute(sort_key);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Intent movieDetailActivityIntent = new Intent(MoviesListActivity.this, MovieDetailActivity.class);
        movieDetailActivityIntent.putExtra(Movie.MOVIE_INTENT_EXTRA, movie);
        startActivity(movieDetailActivityIntent);
    }

    private class FetchMovieListOnlineTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rvMoviePosters.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            String sort_type = params[0];
            Uri apiUri = Uri.parse(Values.API_BASE_URL).buildUpon()
                    .appendPath(sort_type)
                    .appendQueryParameter(Values.API_KEY_PARAM, apiKey)
                    .build();

            try {
                URL url = new URL(apiUri.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                if (isCancelled())
                    return null;

                ArrayList<Movie> movies = new ArrayList<>();

                if (scanner.hasNext()){
                    String jsonFormattedString = scanner.next();

                    JSONObject apiResult = new JSONObject(jsonFormattedString);
                    if (!apiResult.has("results"))
                        return null;

                    if (isCancelled())
                        return null;

                    JSONArray moviesJsonArray = apiResult.getJSONArray("results");
                    JSONObject movieJsonObject;
                    Movie movie;
                    for (int i = 0; i < moviesJsonArray.length(); i++){
                        if (isCancelled())
                            return null;

                        movieJsonObject = moviesJsonArray.getJSONObject(i);
                        movie = new Movie();

                        if (movieJsonObject.has("poster_path"))
                            movie.setPoster_path(movieJsonObject.getString("poster_path"));
                        if (movieJsonObject.has("original_title"))
                            movie.setOriginal_title(movieJsonObject.getString("original_title"));
                        if (movieJsonObject.has("overview"))
                            movie.setOverview(movieJsonObject.getString("overview"));
                        if (movieJsonObject.has("vote_average"))
                            movie.setVote_average(movieJsonObject.getInt("vote_average"));
                        if (movieJsonObject.has("release_date"))
                            movie.setRelease_date(movieJsonObject.getString("release_date"));

                        movies.add(movie);
                    }

                    return movies;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            progressBar.setVisibility(View.GONE);

            if (movies != null && movies.size() > 0){
                moviesList.addAll(movies);
                rvMoviePosters.setVisibility(View.VISIBLE);
                rvMoviePosters.getAdapter().notifyDataSetChanged();
            }
            else if (movies == null){
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.check_internet_prompt));
            }
            else {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.no_movies));
            }
        }
    }
}
