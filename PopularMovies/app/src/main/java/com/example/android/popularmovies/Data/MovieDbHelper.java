package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.Data.MovieContract.*;
import com.example.android.popularmovies.model.Movie;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie11.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER ," +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL,"+
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL ,"+
                MovieEntry.COLUMN_DECRIPTION+ " TEXT NOT NULL,"+
                MovieEntry.COLUMN_HORIZONTAL_POSTER+ " TEXT NOT NULL,"+
                MovieEntry.COLUMN_VERTICAL_POSTER+ " TEXT NOT NULL,"+
                MovieEntry.COLUMN_RELEASE_DATE+ " TEXT NOT NULL,"+
                MovieEntry.COLUMN_AVG+ " TEXT NOT NULL"+
                ");       ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }




}
