package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class DetailMovie extends AppCompatActivity implements TrailerAdapter.ListItemClickListener{
    private TrailerAdapter tAdapter;
    private ReviewAdapter rAdapter;
    private RecyclerView tMoviesList,rMoviesList;

    public static final String SAVED_LAYOUT_MANAGER="statesaved";
    public static final String POSITION = "com.example.popularmovies.POSITION";
    public static final String MOVIES_ARRAY = "com.example.andorid.popularmovies.DetailMovie.MOVIES_ARRAY";
    public static final String BASE_IMAGE_URL="http://image.tmdb.org/t/p/w500//";
    public static final String VIDEOS_URL="videos";
    public static final String REVIEWS_URL="reviews";
    public static final String YOUTUBE_BASE_URL="http://www.youtube.com/watch?v=";
    private TextView movieTitleView,movieAvgView,movieOverviewView ,movieReleaseDateView, movieGeners,movieGenersLabel;
    private ImageView imageView;
    private RatingBar ratingBarView;
    public ScrollView scroll;
    final static int FALSE=0;
    final static int TRUE=1;
    Movie movie;
    int favStatus=0;    //will store the actual movie is selected to be favorite or not
    FloatingActionButton fab ;
    private ArrayList<String> trailersArray;
    private ArrayList<String> reviewsArray;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String TRAILER_LIST_STATE_KEY = "trailer_list_state";
    private final String REVIEW_LIST_STATE_KEY = "review_list_state";
    public static int scrollX = 0;
    public static int scrollY = -1;
    static Parcelable tListState;
    static Parcelable rListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        scroll = (ScrollView) findViewById(R.id.addresses_scroll);

        Log.d("i am here","i am here");
        /*Horizontal Recyclerview for the movie trailers*/
        tMoviesList = (RecyclerView) findViewById(R.id.movie_trailers_tv);
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tMoviesList.setLayoutManager(tLayoutManager);
        tMoviesList.setHasFixedSize(true);

        /*Vertical Recyclerview for the movie reviews*/
        rMoviesList = (RecyclerView) findViewById(R.id.movie_reviews_tv);
        LinearLayoutManager rLayoutManager = new LinearLayoutManager(this);
        rMoviesList.setLayoutManager(rLayoutManager);
        rMoviesList.setHasFixedSize(true);

        trailersArray= new ArrayList<>();
        reviewsArray= new ArrayList<>();

        movieTitleView = (TextView) findViewById(R.id.movie_title_tv);
        movieAvgView = (TextView)findViewById(R.id.movie_avg_tv);
        movieGeners = (TextView)findViewById(R.id.movie_geners_tv);
        movieGenersLabel = (TextView)findViewById(R.id.movie_geners_label);
        movieReleaseDateView = (TextView)findViewById(R.id.movie_release_date_tv);
        movieOverviewView = (TextView)findViewById(R.id.movie_overview_tv);
        imageView = (ImageView) findViewById(R.id.image_id);
        ratingBarView = (RatingBar) findViewById(R.id.rating_bar);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        Bundle data = getIntent().getExtras();
        movie = (Movie) data.getParcelable(MOVIES_ARRAY);

        new getMoviesFavoriteStatus().execute(); //first update fab star off/on icon
        new MovieDbQueryTask().execute(getUrls());
        populateUI();
    }


    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        /*save trailers recyclerview list position on rotation screen*/
        tListState = tMoviesList.getLayoutManager().onSaveInstanceState();
        state.putParcelable(TRAILER_LIST_STATE_KEY, tListState);

        /*save reviews recyclerview list position on rotation screen*/
        rListState = rMoviesList.getLayoutManager().onSaveInstanceState();
        state.putParcelable(REVIEW_LIST_STATE_KEY, rListState);
    }


    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null) {
            tListState = state.getParcelable(TRAILER_LIST_STATE_KEY);
            rListState = state.getParcelable(REVIEW_LIST_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tListState != null) {
            tMoviesList.getLayoutManager().onRestoreInstanceState(tListState);
        }
        if (rListState != null) {
            rMoviesList.getLayoutManager().onRestoreInstanceState(rListState);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        scrollX = scroll.getScrollX();
        scrollY = scroll.getScrollY();

    }

    public URL[] getUrls (){
        URL searchUrl = NetworkUtils.buildUrl(VIDEOS_URL,""+movie.getMovieId()); /*url to movie trailers*/
        URL searchUrl1 = NetworkUtils.buildUrl(REVIEWS_URL,""+movie.getMovieId()); /*url to movie reviews*/
        URL[] movieUrls = new URL[2];
        movieUrls[0]=searchUrl;
        movieUrls[1]=searchUrl1;

        return movieUrls;
    }


    public void populateUI(){
        Picasso.with(this)
                .load(BASE_IMAGE_URL+movie.getHorizontalImage())
                .error(R.drawable.error_image)
                .into(imageView);

        movieTitleView.setText(movie.getTitle());

        double avg=Double.parseDouble(movie.getVoteAvg());
        avg=(avg/10)*5;
        ratingBarView.setRating((float)avg);

        movieAvgView.setText(new DecimalFormat("#.#").format(avg)+"/5");
        movieReleaseDateView.setText(movie.getReleaseDate());

        if(movie.getStringGeners()=="")  // I didn't stored the movies geners to the sqldb, so when we try to see Favorite movies
            movieGenersLabel.setVisibility(View.INVISIBLE);

        movieGeners.setText(movie.getStringGeners());
        movieOverviewView.setText(movie.getDescription());
    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(URL... params) {
            URL searchTrailerUrl = params[0];
            URL searchReviewUrl = params[1];

            ArrayList<String> resultsArr = new ArrayList<>();

            try {
                resultsArr.add(NetworkUtils.getResponseFromHttpUrl(searchTrailerUrl));
                resultsArr.add(NetworkUtils.getResponseFromHttpUrl(searchReviewUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultsArr;
        }

        @Override
        protected void onPostExecute(ArrayList<String> movieDbSearchJsonResults) {
            if(movieDbSearchJsonResults==null)
                Log.d("errrorrrr!!!","erroorrr!!");
            else {
                trailersArray = NetworkUtils.parseMovieTrailerJson(movieDbSearchJsonResults.get(0));
                reviewsArray = NetworkUtils.parseMovieReviewJson(movieDbSearchJsonResults.get(1));

                rAdapter = new ReviewAdapter(reviewsArray, reviewsArray.size());
                rMoviesList.setAdapter(rAdapter);

                tAdapter = new TrailerAdapter(trailersArray, trailersArray.size(), DetailMovie.this);
                tMoviesList.setAdapter(tAdapter);

                updateScrollViewPosition();

            }
        }
    }

    public void updateScrollViewPosition(){
        scroll.post(new Runnable()
        {
            @Override
            public void run()
            {
                scroll.scrollTo(scrollX, scrollY);
            }
        });

    }
    public void onListItemClick(int position) {
        String link = trailersArray.get(position);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL+link)));
    }


    public void addToFavoritesDb(View view){ /*on clicking the fab star button*/
        String selection= "movieId = "+movie.getMovieId();
        ContentValues cv = new ContentValues();

        if (favStatus ==TRUE) {
            fab.setImageResource(android.R.drawable.btn_star_big_off);
            favStatus =FALSE;
            cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, FALSE);
            int updated = getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, cv,selection,null);

            if(updated>0)
                Log.d("star updated to OFF",""+updated);
        }
        else {
            fab.setImageResource(android.R.drawable.btn_star_big_on);
            favStatus =TRUE;
            cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, TRUE);
            int updated = getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, cv,selection,null);
            if(updated>0)
                Log.d("star updated to ON",""+updated);
        }
    }



    public class getMoviesFavoriteStatus extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor cursor=null;
            try {
                ContentResolver resolver = getContentResolver();
                String selection= "movieId = "+movie.getMovieId();
                cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        null,selection , null, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor==null) {
                ////error message
            }
            else {
                if (cursor.getCount()==0)  /*movie does not appear at the db 'favorite movies'*/
                    addToDb();
                else {
                    cursor.moveToPosition(0);
                    favStatus = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
                    if (favStatus == TRUE)  /*check if the movie allready selected as favorite and update the star fab*/
                        fab.setImageResource(android.R.drawable.btn_star_big_on);
                    else
                        fab.setImageResource(android.R.drawable.btn_star_big_off);

                }
            }
        }
    }
    public void addToDb(){
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, FALSE);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_DECRIPTION, movie.getDescription());
        cv.put(MovieContract.MovieEntry.COLUMN_HORIZONTAL_POSTER, movie.getHorizontalImage());
        cv.put(MovieContract.MovieEntry.COLUMN_VERTICAL_POSTER, movie.getVerticalImage());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MovieContract.MovieEntry.COLUMN_AVG, movie.getVoteAvg());
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
    }







}
