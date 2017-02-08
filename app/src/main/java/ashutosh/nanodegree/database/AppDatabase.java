package ashutosh.nanodegree.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import ashutosh.nanodegree.beans.Movies;

/**
 * Created by ashutosh on 6/6/16.
 */
public class AppDatabase extends SQLiteOpenHelper {
    private static AppDatabase mDbHelper;
    public static final String DATABASE_NAME = "popular_movies";
    public static final int DATABASE_VERSION = 1;

    public static class MessageTable {
        public static final String TABLE_NAME = "fav_table";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MOVIE_ID = "_movie_id";
        public static final String COLUMN_IS_FAVOURITE = "_is_favourite";
        public static final String COLUMN_POSTER_PATH = "_movie_poster_path";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                "( " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_POSTER_PATH + " TEXT, " +
                COLUMN_IS_FAVOURITE + " INTEGER DEFAULT 0"
                + " )";

        public static final String DROP_TABLE_QUERY = "DROP TABLE" + TABLE_NAME;
        public static final String SELECT_TABLE_QUERY = "SELECT * FROM " + TABLE_NAME;
        public static final String QUERY_FAV = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_MOVIE_ID + " = ?";
        public static final String DELETE = COLUMN_MOVIE_ID + " = ?";
    }

    public static AppDatabase getInstance(Context context) {
        if (mDbHelper == null) {
            synchronized (AppDatabase.class) {
                if (mDbHelper == null) {
                    mDbHelper = new AppDatabase(context);
                }
            }
        }
        return mDbHelper;
    }


    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(MessageTable.CREATE_TABLE_QUERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(MessageTable.DROP_TABLE_QUERY);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int isFav(int id) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String[] args = {String.valueOf(id)};
        if (readableDatabase != null && readableDatabase.isOpen()) {
            Cursor cursor = readableDatabase.rawQuery(MessageTable.QUERY_FAV, args);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public List<Movies> getAllFavMovies(List<Movies> moviesList) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        if (readableDatabase != null && readableDatabase.isOpen()) {
            Cursor cursor = readableDatabase.rawQuery(MessageTable.SELECT_TABLE_QUERY, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Movies movies = new Movies();
                movies.setId(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MOVIE_ID)));
                movies.setPosterPath(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_POSTER_PATH)));
                movies.setFavourite(true);
                moviesList.add(movies);
                cursor.moveToNext();
            }
        }
        return moviesList;
    }

    public boolean insert(int movieId, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_MOVIE_ID, movieId);
        values.put(MessageTable.COLUMN_IS_FAVOURITE, true);
        values.put(MessageTable.COLUMN_POSTER_PATH, posterPath);

        db.insert(MessageTable.TABLE_NAME, null, values);
        return true;
    }


    public int delete(int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MessageTable.TABLE_NAME, MessageTable.DELETE, new String[]{Integer.toString(movieId)});
    }
}
