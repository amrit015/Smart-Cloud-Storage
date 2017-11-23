package com.amrit.smartcloudstorage;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrit on 11/22/2017.
 */

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.EventsHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    Context context;
    PhotoModule photoModule;
    private ArrayList<PhotoModule> mDataset;
    private String name;
    private int position;
    private String image;

    public MyRecyclerViewAdapter(DownloadImagesActivity activity, ArrayList<PhotoModule> mDataset) {
        context = activity;
        this.mDataset = mDataset;
    }

    @Override
    public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_myrecycler_view, parent, false);
        context = parent.getContext();
        EventsHolder eventsHolder = new EventsHolder(view);
        return eventsHolder;
    }

    @Override
    public void onBindViewHolder(final EventsHolder holder, final int position) {
        // binding the views to display the name, description and user in the recyclerview
        photoModule = mDataset.get(position);
        if (!photoModule.getPhotoUrl().equals("")) {
            Log.i("RecyclerViewAdapter","photoUrl: "+ photoModule.getPhotoUrl());
            holder.progressBar.setVisibility(View.VISIBLE);
            //using Glide to load images
            Glide.clear(holder.storedImage);
            Glide.with(context)
                    .load(photoModule.getPhotoUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.storedImage);
        } else {
            holder.cardView.setVisibility(View.GONE);
        }
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                moduleParcelable = mDataset.get(position);
//                setPosition(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public class EventsHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView storedImage;
        ProgressBar progressBar;

        public EventsHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            storedImage = (ImageView) itemView.findViewById(R.id.stored_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            Log.i(LOG_TAG, "Adding Listener");
        }
    }
}
