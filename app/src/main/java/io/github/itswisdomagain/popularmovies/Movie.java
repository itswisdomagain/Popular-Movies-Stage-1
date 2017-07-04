package io.github.itswisdomagain.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

class Movie implements Parcelable {
    static final String MOVIE_INTENT_EXTRA = "movie";
    private static final String POSTER_PATH_PREFIX = "https://image.tmdb.org/t/p/w185";

    private String poster_path, original_title, overview, release_date;
    private int vote_average;

    Movie(){}

    private Movie(Parcel in) {
        poster_path = in.readString();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote_average = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    String getPoster_path() {
        return POSTER_PATH_PREFIX.concat(poster_path);
    }

    void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    String getOriginal_title() {
        return original_title;
    }

    void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    int getVote_average() {
        return vote_average;
    }

    void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }

    String getRelease_date() {
        return release_date;
    }

    void setRelease_date(String release_date) {
        this.release_date = release_date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(vote_average);
    }
}
