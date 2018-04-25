package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;


public class Movie implements Parcelable{
    private Integer movie_id;
    private String title;
    private ArrayList<String> geners = new ArrayList<>();
    private ArrayList<String> trailers = new ArrayList<>();
    private String description;
    private String releaseDate;
    private String verticalImage;
    private String horizontalImage;
    private String voteAvg = null;



    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel in){
        this.movie_id=in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.releaseDate =  in.readString();
        this.verticalImage = in.readString();
        this.horizontalImage = in.readString();
        this.voteAvg = in.readString();
        geners = new ArrayList<String>();
        in.readList(geners,null);
        trailers = new ArrayList<String>();
        in.readList(trailers,null);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movie_id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.releaseDate);
        dest.writeString(this.verticalImage);
        dest.writeString(this.horizontalImage);
        dest.writeString(this.voteAvg);
        if (geners != null)
            dest.writeArray(this.geners.toArray());
        if(trailers!=null)
            dest.writeArray(this.trailers.toArray());
    }


    public Movie() {
    }

    public Movie(Integer movie_id,String title, ArrayList<String> geners,ArrayList<String>trailers, String description, String releaseDate, String verticalImage, String horizontalImage, String voteAvg) {
        this.movie_id = movie_id;
        this.title = title;
        this.geners = geners;
        this.trailers=trailers;
        this.description = description;
        this.releaseDate = releaseDate;
        this.verticalImage = verticalImage;
        this.horizontalImage = horizontalImage;
        this.voteAvg = voteAvg;
    }

    public Integer getMovieId (){ return  movie_id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStringGeners() {
        String genersStr="";
        for (int i = 0; i< geners.size(); i++)
            genersStr=geners.get(i)+", "+genersStr;
        return genersStr;
    }

    public ArrayList<String> getArrayGeners() {
        return geners;
    }

    public ArrayList<String> getArrayTrailers() {
        return trailers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVerticalImage() {
        return verticalImage;
    }

    public void setVerticalImage(String verticalImage) {
        this.verticalImage = verticalImage;
    }

    public String getHorizontalImage() {
        return horizontalImage;
    }

    public void setHorizontalImage(String horizontalImage) {this.horizontalImage = horizontalImage;
    }
    public String getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(String voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getReleaseDate() {return this.releaseDate;}

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }



}
