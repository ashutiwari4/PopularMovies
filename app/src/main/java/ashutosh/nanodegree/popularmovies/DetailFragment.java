package ashutosh.nanodegree.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ashutosh.nanodegree.application.BaseActivity;
import ashutosh.nanodegree.application.BaseFragment;
import ashutosh.nanodegree.beans.Movies;
import ashutosh.nanodegree.beans.SuperModel;
import ashutosh.nanodegree.database.AppDatabase;
import ashutosh.nanodegree.network.NetworkLoader;
import ashutosh.nanodegree.network.ServerResponse;
import ashutosh.nanodegree.network.WebUtils;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends BaseFragment implements View.OnClickListener {

    private static final int id = 0x01;
    private TextView title, year, duration, rating, popularity, genre, description, reviewTitle, trailerTitle;
    private ImageView posterImage;
    private LinearLayout llReview, trailersLinearLayout;
    private View trailerSeparator, reviewSeparator;
    private Palette palette;
    private Button markAsFav;
    private Movies movie;
    private boolean isFavStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.details_fragment, container, false);
        networkFound(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void networkFound(View rootView, Bundle bundle) {
        super.networkFound(rootView, bundle);

        title = (TextView) rootView.findViewById(R.id.tv_aid_title);
        year = (TextView) rootView.findViewById(R.id.tv_aid_year);
        duration = (TextView) rootView.findViewById(R.id.tv_aid_time);
        rating = (TextView) rootView.findViewById(R.id.tv_aid_rating);
        popularity = (TextView) rootView.findViewById(R.id.tv_aid_fav);
        genre = (TextView) rootView.findViewById(R.id.tv_aid_genere);
        description = (TextView) rootView.findViewById(R.id.tv_aid_description);
        posterImage = (ImageView) rootView.findViewById(R.id.iv_aid_poster);
        reviewSeparator = rootView.findViewById(R.id.view_review_seprator);
        llReview = (LinearLayout) rootView.findViewById(R.id.ll_reviews);
        trailerSeparator = rootView.findViewById(R.id.view_trailers_seprator);
        trailersLinearLayout = (LinearLayout) rootView.findViewById(R.id.ll_trailers);
        reviewTitle = (TextView) rootView.findViewById(R.id.tv_title_reviews);
        trailerTitle = (TextView) rootView.findViewById(R.id.tv_aid_trailers);
        markAsFav = (Button) rootView.findViewById(R.id.mark_as_fav);


        if (getActivity().getIntent().hasExtra("itemDetails")) {
            movie = getActivity().getIntent().getExtras().getParcelable("itemDetails");//.getParcelableExtra();
        } else {
            movie = getArguments().getParcelable("itemDetails");
        }
        checkFavStatus();
        makeRequest("" + movie.getId());

        Glide.with(getActivity()).load(WebUtils.imageBaseUrl + movie.getPosterPath()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap myBitmap, GlideAnimation glideAnimation) {
                if (myBitmap != null && !myBitmap.isRecycled()) {
                    palette = Palette.from(myBitmap).generate();
                    posterImage.setImageBitmap(myBitmap);
                    title.setBackgroundColor(palette.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    title.setTextColor(palette.getVibrantColor(Color.WHITE));
                    trailerSeparator.setBackgroundColor(palette.getLightVibrantColor(getResources().getColor(R.color.darkGray)));
                    reviewSeparator.setBackgroundColor(palette.getLightVibrantColor(getResources().getColor(R.color.darkGray)));
                    year.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                    duration.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                    rating.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                    popularity.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                    genre.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                    description.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                }
            }
        });

        if (!isFavStatus) setData();
        markAsFav.setOnClickListener(this);
    }


    private void setData() {
        title.setText(movie.getTitle());
        String date_s = movie.getReleaseDate();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
        Date date;
        try {
            date = dt.parse(date_s);
            SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
            year.setText("" + dt1.format(date) == null ? getString(R.string.not_available) : dt1.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        rating.setText("" + movie.getVoteAvg() + "/10");
        popularity.setText("" + movie.getPopularity());
        description.setText(movie.getOverview());
    }

    private void checkFavStatus() {
        isFavStatus = AppDatabase.getInstance(getContext()).isFav(movie.getId()) == 0 ? false : true;
        markAsFav.setText(isFavStatus ? getString(R.string.favourited) : getString(R.string.mark_as_fav));

    }

    private void makeRequest(String movieId) {
        if (getActivity().getSupportLoaderManager() != null) {
            Bundle bundle = new Bundle();
            String tempUrl;
            tempUrl = WebUtils.MOVIE_DETAILS_BASE_URL + movieId + "?&api_key=" + WebUtils.API_KEY + "&append_to_response=trailers,reviews";
            bundle.putString(NetworkLoader.URL_PARAM, tempUrl);
            getActivity().getSupportLoaderManager().restartLoader(id, bundle, this);
        }
    }


    @Override
    public void onLoadFinished(Loader<ServerResponse> loader, ServerResponse data) {
        super.onLoadFinished(loader, data);
        if (data.getResponseCode() == 200) {
            parse(data.getServerResponse());
        } else if (data.getServerResponse() == null || data.getServerResponse().equals("")) {
            ((BaseActivity) getActivity()).getSnackBar(Snackbar.LENGTH_SHORT, getString(R.string.no_data_available), null).show();
        } else {
            ((BaseActivity) getActivity()).getSnackBar(Snackbar.LENGTH_SHORT, data.getException().getMessage(), null).show();
        }
    }


    private void parse(String s) {
        JSONObject jo = SuperModel.createJsonFromSring(s);
        if (jo != null) {

            if (isFavStatus) {
                movie.setTitle(SuperModel.getString(jo, "original_title"));
                movie.setReleaseDate(SuperModel.getString(jo, "release_date"));
                movie.setVoteAvg(SuperModel.getFloat(jo, "vote_average"));
                movie.setPopularity(SuperModel.getFloat(jo, "popularity"));
                movie.setOverview(SuperModel.getString(jo, "overview"));
                setData();
            }


            String genereStr = "";
            JSONArray ja = jo.optJSONArray("genres");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject genereObject = SuperModel.getJObject(ja, i);
                genereStr += genereStr.length() < 1 ? SuperModel.getString(genereObject, "name") : "/" + SuperModel.getString(genereObject, "name");
            }
            genre.setText(genereStr);
            duration.setText(SuperModel.getString(jo, "runtime") == null ? getString(R.string.not_available) : SuperModel.getString(jo, "runtime") + "  Mins");
            JSONObject reviewsObject = SuperModel.getJObject(jo, "reviews");
            JSONArray reviewArray = SuperModel.getJArray(reviewsObject, "results");
            if (reviewArray.length() == 0) reviewTitle.setVisibility(View.GONE);
            for (int i = 0; i < reviewArray.length(); i++) {

                CardView cv = new CardView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(10, 10, 10, 10);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cv.setElevation(16);
                }
                cv.setRadius(5);
                if (palette != null)
                    cv.setBackgroundColor(palette.getLightMutedColor(getResources().getColor(R.color.lightYello)));
                else cv.setBackgroundColor(getResources().getColor(R.color.lightYello));

                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(16, 16, 16, 16);

                JSONObject reviewObject = SuperModel.getJObject(reviewArray, i);
                String content = SuperModel.getString(reviewObject, "content");
                String aurthor = SuperModel.getString(reviewObject, "author");
                TextView tv = new TextView(getContext());
                tv.setText(content);

                if (palette != null)
                    tv.setTextColor(palette.getDarkMutedColor(getResources().getColor(R.color.darkGray)));
                else tv.setTextColor(getResources().getColor(R.color.darkGray));
                tv.setTypeface(tv.getTypeface(), Typeface.ITALIC);
                ll.addView(tv);
                TextView tv1 = new TextView(getContext());
                tv1.setText("- " + aurthor);
                ll.addView(tv1);
                if (palette != null)
                    tv1.setTextColor(palette.getDarkVibrantColor(getResources().getColor(R.color.darkGray)));
                else
                    tv1.setTextColor(getResources().getColor(R.color.colorAccent));
                tv1.setTypeface(tv.getTypeface(), Typeface.BOLD);
                tv1.setGravity(Gravity.RIGHT);
                cv.addView(ll);
                llReview.addView(cv, params);
            }
        }

        JSONObject videos = SuperModel.getJObject(jo, "trailers");
        JSONArray youtubeVideo = SuperModel.getJArray(videos, "youtube");
        if (youtubeVideo.length() == 0) trailerTitle.setVisibility(View.GONE);
        for (int i = 0; i < youtubeVideo.length(); i++) {
            final JSONObject videoObject = SuperModel.getJObject(youtubeVideo, i);
            CardView cv = new CardView(getContext());
            TextView tv = new TextView(getContext());
            tv.setPadding(10, 10, 10, 10);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 10, 10, 10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cv.setElevation(16);
            }
            cv.setRadius(5);
            cv.setBackgroundColor(getResources().getColor(R.color.youTubecolor));
            cv.addView(tv);
            tv.setText(SuperModel.getString(videoObject, "name"));
            trailersLinearLayout.addView(cv, params);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), YoutubeActivity.class).putExtra("videoId", SuperModel.getString(videoObject, "source")));
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if (v.equals(markAsFav)) {
            movie.markAsFav(getContext(), isFavStatus);
            checkFavStatus();
        }
    }
}
