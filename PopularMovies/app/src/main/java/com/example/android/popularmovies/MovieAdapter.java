package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>  {

    final private ListItemClickListener mOnClickListener;
    private int mNumberItems;
    private static int viewHolderCount;
    private ArrayList<Movie> arrayMovies=new ArrayList<>();
    public static final String BASE_IMAGE_URL="http://image.tmdb.org/t/p/w342/";

    public MovieAdapter(ArrayList<Movie> arrayMovies ,int numberItems, ListItemClickListener mOnClickListener ){
        this.mOnClickListener=mOnClickListener;
        mNumberItems=numberItems;
        this.arrayMovies=arrayMovies;
        viewHolderCount=0;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_grid;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
        Context context = viewHolder.imageView.getContext();
        Picasso.with(context)
                .load(BASE_IMAGE_URL+arrayMovies.get(position).getVerticalImage())
                .error(R.drawable.error_image)
                .into(viewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }



public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView imageView;

    public MovieViewHolder(View viewHolder){
        super(viewHolder);
        imageView = (ImageView) itemView.findViewById(R.id.image_tv);
        viewHolder.setOnClickListener(this);
    }

    public void onClick(View v) {
        int clickedPosition = getAdapterPosition();
        mOnClickListener.onListItemClick(clickedPosition);
    }
}

}
