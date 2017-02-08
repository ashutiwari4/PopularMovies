package ashutosh.nanodegree.beans;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import ashutosh.nanodegree.database.AppDatabase;

/**
 * Created by ashutosh on 1/3/16.
 */
public class Movies implements Parcelable {
    private String posterPath, overview, title, orignalLanguage;
    private boolean adult, video;
    private int id, voteCount;
    private float popularity, voteAvg;
    private String releaseDate;
    private boolean favourite;

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Movies() {
    }

    private Movies(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        title = in.readString();
        orignalLanguage = in.readString();
        releaseDate = in.readString();
        id = in.readInt();
        voteCount = in.readInt();
        popularity = in.readFloat();
        voteAvg = in.readFloat();
        adult = in.readByte() != 0;
        video = in.readByte() != 0;
    }

    public final static Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrignalLanguage() {
        return orignalLanguage;
    }

    public void setOrignalLanguage(String orignalLanguage) {
        this.orignalLanguage = orignalLanguage;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(float voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //this.releaseDate.toString()

        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.title);
        dest.writeString(this.orignalLanguage);
        dest.writeString(this.releaseDate);

        dest.writeInt(this.id);
        dest.writeInt(this.voteCount);
        dest.writeFloat(this.popularity);
        dest.writeFloat(this.voteAvg);

        //dest.write
        dest.writeBooleanArray(new boolean[]{this.adult, this.video});
    }

    public void markAsFav(Context context, boolean status) {
        if (!status) {
            AppDatabase.getInstance(context).insert(id, posterPath);
        } else AppDatabase.getInstance(context).delete(id);
    }
}
