package com.amrit.smartcloudstorage;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Amrit on 11/22/2017.
 * RecyclerView adapter for displaying the files stored on the Firebase Storage
 */

class MyFilesViewAdapter extends RecyclerView.Adapter<MyFilesViewAdapter.EventsHolder> {
    private static String LOG_TAG = "MyFilesViewAdapter";
    Context context;
    ObjectModule objectModule;
    String fileType;
    String type;
    private ArrayList<ObjectModule> mDataset;

    public MyFilesViewAdapter(UserHomeActivity userHomeActivity, ArrayList<ObjectModule> fileList) {
        context = userHomeActivity;
        this.mDataset = fileList;
    }

    public static boolean containsAny(String str, String[] words) {
        boolean bResult = false; // will be set, if any of the words are found
        //String[] words = {"word1", "word2", "word3", "word4", "word5"};

        List<String> list = Arrays.asList(words);
        for (String word : list) {
            boolean bFound = str.contains(word);
            if (bFound) {
                bResult = bFound;
                break;
            }
        }
        return bResult;
    }

    @Override
    public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creating the views
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_myrecycler_view, parent, false);
        context = parent.getContext();
        EventsHolder eventsHolder = new EventsHolder(view);
        return eventsHolder;
    }

    @Override
    public void onBindViewHolder(final EventsHolder holder, final int position) {
        // binding the views to display the name, description and user in the recyclerview
        objectModule = mDataset.get(position);
        if (!objectModule.getUrl().equals("")) {
            //getting the group on which the user is currently logged in
                Log.i("RecyclerViewAdapter", "url: " + objectModule.getUrl());
                type = objectModule.getType();
                fileType = type.substring(type.lastIndexOf('/') + 1);
                Log.i("RecyclerView", "type:" + type);
                holder.progressBar.setVisibility(View.VISIBLE);
                String img[];
            // defining image types to perform different actions on the images
                img = new String[]{"jpeg", "png", "jpg"};
                Boolean checkType = false;
            // checking if the file is an image
                checkType = containsAny(type, img);

                if (checkType) {
                    //using Glide to load images
                    Glide.clear(holder.storedImage);
                    Glide.with(context)
                            .load(objectModule.getUrl())
                            .fitCenter()
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
                    // files
                    holder.progressBar.setVisibility(View.GONE);
                    holder.storedImage.setImageResource(R.drawable.file);
                    holder.textImage.setText(objectModule.getTitle());
                    holder.textImage.setVisibility(View.VISIBLE);
                }
        } else {
            holder.cardView.setVisibility(View.GONE);
        }

        // on-click on the files
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                objectModule = mDataset.get(position);

                type = objectModule.getType();
                fileType = type.substring(type.lastIndexOf('/') + 1);
                String img[];
                img = new String[]{"jpeg", "png", "jpg"};
                Boolean checkType = false;
                checkType = containsAny(type, img);

                if (checkType) {
                    // for images
                    Intent i = new Intent(context, ViewPhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("photoUrl", objectModule.getUrl());
                    bundle.putString("uploader", objectModule.getUploader());
                    bundle.putString("photoTitle", objectModule.getTitle());
                    i.putExtras(bundle);
                    context.startActivity(i);
                } else {
                    // for non-image files
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(objectModule.getUrl()), type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No app found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // displaying the contents on the cardview, images/files and filename
    public class EventsHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView storedImage;
        ProgressBar progressBar;
        TextView textImage;

        public EventsHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            storedImage = itemView.findViewById(R.id.stored_image);
            progressBar = itemView.findViewById(R.id.progressBar);
            textImage = itemView.findViewById(R.id.image_text);
            Log.i(LOG_TAG, "Adding Listener");
        }
    }
}
