package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static int viewHolderCount;
    private int mNumberItems;
    private ArrayList<String> movieReviews = new ArrayList<>();
    public static final String BASE_IMAGE_URL = "https://img.youtube.com/vi/";
    public static final String BASE_END_IMAGE_URL = "/mqdefault.jpg";

    public ReviewAdapter(ArrayList<String> movieReviews, int numberItems) {
        mNumberItems = numberItems;
        this.movieReviews = movieReviews;
        viewHolderCount = 0;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewAdapter.ReviewViewHolder viewHolder = new ReviewAdapter.ReviewViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder viewHolder, int position) {
        String content = movieReviews.get(position);
        String[] review = content.split("\\$");
        viewHolder.textView.setText(Html.fromHtml(("<b>"+review[0]+"</b>"+"<br>"+review[1])));

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ReviewViewHolder(View movieView) {
            super(movieView);
            textView = (TextView) itemView.findViewById(R.id.review_image_tv);
        }


    }
}