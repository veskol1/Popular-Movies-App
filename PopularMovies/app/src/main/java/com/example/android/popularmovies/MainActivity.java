package com.example.android.popularmovies;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Data.MovieDbHelper;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/*
 *Created By Vesko Latchev
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private TextView mErrorMessageDisplay;
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private ProgressBar mLoadingIndicator;
    private ArrayList<Movie> arrayMovies;

    public static Boolean needToUpdatedTheView=FALSE;
    final static String MOST_POPULAR_MOVIES="Most Popular Movies";
    final static String TOP_RATED_MOVIES="Top Rated Movies";
    final static String FAVORITE_MOVIES="Favorite Movies";
    final static String POPULAR_QUERY="popular";
    final static String TOP_RATED_QUERY="top_rated";
    private final String ON_STATE = "on_state";
    private final String NEED_TO_UPDATE_DB = "need_to_update_db";
    public static HashMap<String,String> genersHash = new HashMap<>();
    public SQLiteDatabase mDb;
    public MovieDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addGenersToHash();              /*create hashmap contains the ganers ids*/

        dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();


        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mMoviesList = (RecyclerView) findViewById(R.id.rv_numbers);

        GridLayoutManager gridManager = new GridLayoutManager(this,numberOfColumns());
        mMoviesList.setLayoutManager(gridManager);
        mMoviesList.setHasFixedSize(true);
        arrayMovies = new ArrayList<>();

        if(savedInstanceState == null) {
            URL searchUrl = NetworkUtils.buildUrl(POPULAR_QUERY); /*default db query search*/
            new MovieDbQueryTask().execute(searchUrl);   /*first time asynctask*/
        }
        else {
            if (needToUpdatedTheView!=TRUE){ /*we already updated once the db onResume if needed*/
                arrayMovies = savedInstanceState.getParcelableArrayList(ON_STATE);
                mAdapter = new MovieAdapter(arrayMovies, arrayMovies.size(), MainActivity.this);
                mMoviesList.setAdapter(mAdapter);
            }

        }
    }

    public void onResume() {
        super.onResume();  //maybe there is  a better way to updated the view
        /*after pressing back and returning to the 'Favorite Movies' menu update the view*/
        if (needToUpdatedTheView==TRUE){
            setTitle(FAVORITE_MOVIES);
            arrayMovies.clear();
            new getFavMoviesFromDb().execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ON_STATE, arrayMovies);
        outState.putBoolean(NEED_TO_UPDATE_DB,needToUpdatedTheView);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        needToUpdatedTheView= state.getBoolean(NEED_TO_UPDATE_DB);

    }



    public void onListItemClick(int position) {
        Intent intent = new Intent(this, DetailMovie.class);
        intent.putExtra(DetailMovie.POSITION, position);
        Movie movie= arrayMovies.get(position);
        intent.putExtra(DetailMovie.MOVIES_ARRAY,movie);

        startActivity(intent);
    }

    private void makeGithubSearchQuery(String DbMoviesQuery) {
        URL searchUrl = NetworkUtils.buildUrl(DbMoviesQuery);      /*switch with popular/most*/
        new MovieDbQueryTask().execute(searchUrl);
    }


    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbSearchJsonResults = null;
            try {
                movieDbSearchJsonResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieDbSearchJsonResults;
        }

        @Override
        protected void onPostExecute(String movieDbSearchJsonResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieDbSearchJsonResults==null)
                showErrorMessage();
            else {
                removeErrorMessage();
            arrayMovies = NetworkUtils.parseMovieJson(movieDbSearchJsonResults);

            mAdapter = new MovieAdapter(arrayMovies, arrayMovies.size(), MainActivity.this);
            mMoviesList.setAdapter(mAdapter);
            }
        }
    }



    public class getFavMoviesFromDb extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor cursor=null;
            try {
                ContentResolver resolver = getContentResolver();
                cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        null, null, null, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(cursor==null)
                showErrorMessage();
            else {
                removeErrorMessage();

            arrayMovies =  favoriteArrayMovies(cursor);
            mAdapter = new MovieAdapter(arrayMovies, arrayMovies.size(), MainActivity.this);
            mMoviesList.setAdapter(mAdapter);
            }
        }

    }


    public ArrayList<Movie> favoriteArrayMovies(Cursor cursor){

        for(int i=0; i< cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Integer favorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
            if(favorite==1) {
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                Integer movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                String description = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DECRIPTION));
                String posterH = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_HORIZONTAL_POSTER));
                String posterV = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VERTICAL_POSTER));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                String avg = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_AVG));

                Movie movie = new Movie(movieId, title, null,null, description, releaseDate, posterV, posterH, avg);
                arrayMovies.add(movie);
            }
        }

        return arrayMovies;
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void removeErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_popular) {
            setTitle(MOST_POPULAR_MOVIES);
            arrayMovies.clear();
            needToUpdatedTheView=FALSE;
            makeGithubSearchQuery(POPULAR_QUERY);
            return true;
        }
        else if(itemThatWasClickedId==R.id.action_top_rated){
            setTitle(TOP_RATED_MOVIES);
            arrayMovies.clear();
            needToUpdatedTheView=FALSE;
            makeGithubSearchQuery(TOP_RATED_QUERY);
            return true;
        }
        else if(itemThatWasClickedId==R.id.action_favorite_movies){
            setTitle(FAVORITE_MOVIES);
            arrayMovies.clear();
            needToUpdatedTheView=TRUE;
            new getFavMoviesFromDb().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addGenersToHash(){
        genersHash.put("28","Action");
        genersHash.put("12","Adventure");
        genersHash.put("16","Animation");
        genersHash.put("35","Comedy");
        genersHash.put("80","Crime");
        genersHash.put("99","Documentary");
        genersHash.put("18","Drama");
        genersHash.put("10751","Family");
        genersHash.put("14","Fantasy");
        genersHash.put("36","History");
        genersHash.put("27","Horror");
        genersHash.put("10402","Music");
        genersHash.put("9648","Mystery");
        genersHash.put("10749","Romance");
        genersHash.put("878","Science Fiction");
        genersHash.put("10770","TV Movie");
        genersHash.put("53","Thriller");
        genersHash.put("10752","War");
        genersHash.put("37","Western");


    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = displayMetrics.widthPixels/3;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}







