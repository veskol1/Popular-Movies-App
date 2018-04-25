package com.example.android.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkUtils {

    final static String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String PARAM_KEY = "api_key";
    final static String KEY="5b609fa354e920b753e95dc5f803bbdc";
    final static String PARAM_LANGUAGE = "language";
    final static String LANGUAGE = "en-US";


    public static final String JSON_MOVIE_ID = "id";
    public static final String JSON_INTENT_KEY = "results";
    public static final String JSON_TITLE_MOVIE_KEY = "title";
    public static final String JSON_GENERS_MOVIE_KEY = "genre_ids";
    public static final String JSON_OVERVIEW_MOVIE_KEY = "overview";
    public static final String JSON_RELEASE_DATE_MOVIE_KEY = "release_date";
    public static final String JSON_POSTER_MOVIE_KEY = "poster_path";
    public static final String JSON_BACKDROP_MOVIE_KEY = "backdrop_path";
    public static final String JSON_AVG_MOVIE_KEY = "vote_average";

    public static final String JSON_MOVIE_KEY = "key";

    public static final String JSON_REVIEW_AUTHOR = "author";
    public static final String JSON_REVIEW_CONTENT = "content";

    public static URL buildUrl(String query,String movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL +movieId+"/"+query).buildUpon()
                .appendQueryParameter(PARAM_KEY, KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }



    public static URL buildUrl(String movieDbQuery) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL +movieDbQuery).buildUpon()
                .appendQueryParameter(PARAM_KEY, KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }




    public static ArrayList<String> parseMovieReviewJson(String json) {
        ArrayList<String> reviewsArray = new ArrayList<>();
        String author;
        String content;
        String review;

        try {
            JSONObject reviewJsonObjects = new JSONObject(json);
            JSONArray reviewsObjectsArray = reviewJsonObjects.getJSONArray(JSON_INTENT_KEY);
            JSONObject reviewObject = null;

            for (int i=0; i<reviewsObjectsArray.length(); i++) {
                reviewObject = reviewsObjectsArray.getJSONObject(i);
                author = reviewObject.optString(JSON_REVIEW_AUTHOR);
                content = reviewObject.optString(JSON_REVIEW_CONTENT);
                review = author+"$"+content;
                reviewsArray.add(review);
            }

        }catch (JSONException e) {
            Log.d("error","Error!");
        }
        return reviewsArray;
    }





    public static ArrayList<String> parseMovieTrailerJson(String json) {
        ArrayList<String> trailersArray = new ArrayList<>();
        String movieTrailerLink;

        try {
            JSONObject trailerJsonObjects = new JSONObject(json);
            JSONArray trailersObjectsArray = trailerJsonObjects.getJSONArray(JSON_INTENT_KEY);
            JSONObject trailerObject = null;

            for (int i=0; i<trailersObjectsArray.length(); i++) {
                trailerObject = trailersObjectsArray.getJSONObject(i);
                movieTrailerLink = trailerObject.optString(JSON_MOVIE_KEY);
                trailersArray.add(movieTrailerLink);
                 }

        }catch (JSONException e) {
            Log.d("error","Error!");
        }
        return trailersArray;
    }


    public static ArrayList<Movie> parseMovieJson(String json) {
         Integer movie_id=null;
         String title=null;
         ArrayList<String> ganers=null;

         String description=null;
         String releaseDate=null;
         String verticalImage=null;
         String horizontalImage=null;
         String voteAvg=null ;

        ArrayList<Movie> moviesArray = new ArrayList<>();

        try {
            JSONObject movieJsonObject = new JSONObject(json);
            JSONArray moviesObjects = movieJsonObject.getJSONArray(JSON_INTENT_KEY);
            JSONObject movieObject = null;

            for (int i=0; i<moviesObjects.length(); i++) {
                movieObject = moviesObjects.getJSONObject(i);
                movie_id = movieObject.optInt(JSON_MOVIE_ID);
                title = movieObject.optString(JSON_TITLE_MOVIE_KEY);


                JSONArray movieGanersObject = movieObject.getJSONArray(JSON_GENERS_MOVIE_KEY);
                ganers= new ArrayList<>();
                for(int j=0;j<movieGanersObject.length();j++) {
                    String indexHash =movieGanersObject.optString(j);
                    MainActivity.genersHash.get(indexHash);
                    ganers.add(MainActivity.genersHash.get(indexHash));
                }

                description=movieObject.optString(JSON_OVERVIEW_MOVIE_KEY);
                releaseDate=movieObject.optString(JSON_RELEASE_DATE_MOVIE_KEY);
                verticalImage=movieObject.optString(JSON_POSTER_MOVIE_KEY);
                horizontalImage=movieObject.optString(JSON_BACKDROP_MOVIE_KEY);
                voteAvg=movieObject.optString(JSON_AVG_MOVIE_KEY);

                moviesArray.add(new Movie(movie_id,title,ganers,null,description,releaseDate,verticalImage,horizontalImage,voteAvg));
            }

        } catch (JSONException e) {

            Log.d("error","Error!");
        }

        return moviesArray;
    }







}