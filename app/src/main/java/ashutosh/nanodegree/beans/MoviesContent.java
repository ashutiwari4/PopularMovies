package ashutosh.nanodegree.beans;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ashutosh on 16/2/16.
 */
public class MoviesContent extends SuperModel {
    private static MoviesContent moviesContent;

    private ArrayList<Movies> moviesList = new ArrayList<>();

    public ArrayList<Movies> getMoviesList() {
        return moviesList;
    }

    private MoviesContent() {

    }

    public static MoviesContent getInstance(Context context, String json) {
        if (moviesContent == null)
            moviesContent = new MoviesContent();
        moviesContent.processJson(json);
        return moviesContent;

    }

    public static MoviesContent getInstance() {
        if (moviesContent == null)
            moviesContent = new MoviesContent();
        return moviesContent;

    }


    private void processJson(String json) {
        try {
            System.out.println(json);
            JSONArray ja = new JSONObject(json).getJSONArray("results");
            Movies movie;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            for (int i = 0; i < ja.length(); i++) {
                movie = new Movies();
                JSONObject jo = getJObject(ja, i);
                movie.setPosterPath(getString(jo, "poster_path"));
                movie.setAdult(getBoolean(jo, "adult"));
                movie.setOverview(getString(jo, "overview"));
                movie.setId(getInt(jo, "id"));

                JSONArray tempArr = jo.getJSONArray("genre_ids");

                movie.setTitle(getString(jo, "original_title"));
                movie.setOrignalLanguage(getString(jo, "original_language"));
                movie.setPopularity(getFloat(jo, "popularity"));
                movie.setVoteCount(getInt(jo, "vote_count"));
                movie.setVideo(getBoolean(jo, "video"));
                movie.setVoteAvg(getFloat(jo, "vote_average"));
                movie.setReleaseDate(getString(jo, "release_date"));
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
