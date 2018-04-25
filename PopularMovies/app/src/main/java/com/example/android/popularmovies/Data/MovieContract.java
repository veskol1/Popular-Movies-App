package com.example.android.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;




public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "movielist";



    public static final class MovieEntry implements BaseColumns  {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "movielist";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_FAVORITE = "movie_favorite";
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_DECRIPTION = "movieDecription";
        public static final String COLUMN_HORIZONTAL_POSTER = "moviePosterH";
        public static final String COLUMN_VERTICAL_POSTER = "moviePosterV";
        public static final String COLUMN_RELEASE_DATE = "movieReleaseDate" ;
        public static final String COLUMN_AVG = "movieAvg";

    }

}
