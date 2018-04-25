package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>  {

    private static int viewHolderCount;
    final private TrailerAdapter.ListItemClickListener mOnClickListener;
    private int mNumberItems;
    private ArrayList<String> movieTrailers=new ArrayList<>();
    public static final String BASE_IMAGE_URL="https://img.youtube.com/vi/";
    public static final String BASE_END_IMAGE_URL="/mqdefault.jpg";

    public TrailerAdapter(ArrayList<String> movieTrailers ,int numberItems, TrailerAdapter.ListItemClickListener mOnClickListener ){
        this.mOnClickListener=mOnClickListener;
        mNumberItems=numberItems;
        this.movieTrailers=movieTrailers;
        viewHolderCount=0;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailerAdapter.TrailerViewHolder viewHolder = new TrailerAdapter.TrailerViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder viewHolder, int position) {
        Context context = viewHolder.imageView.getContext();
        Picasso.with(context)
               .load(BASE_IMAGE_URL+movieTrailers.get(position)+BASE_END_IMAGE_URL)
                .error(R.drawable.error_image)
                .into(viewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }



    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;

        public TrailerViewHolder(View movieView){
            super(movieView);
            imageView = (ImageView) itemView.findViewById(R.id.trailer_image_tv);
            movieView.setOnClickListener(this);
        }

        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}


