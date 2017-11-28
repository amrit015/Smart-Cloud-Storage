package com.amrit.smartcloudstorage;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
 */

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.EventsHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    Context context;
    ObjectModule objectModule;
    private ArrayList<ObjectModule> mDataset;
    private String name;
    private int position;
    private String image;
    String fileType;
    String type;

    public MyRecyclerViewAdapter(DownloadImagesActivity activity, ArrayList<ObjectModule> mDataset) {
        context = activity;
        this.mDataset = mDataset;
    }

    public MyRecyclerViewAdapter(DownloadFileActivity downloadFileActivity, ArrayList<ObjectModule> moduleList) {
        context = downloadFileActivity;
        this.mDataset = moduleList;
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
        objectModule = mDataset.get(position);
        if (!objectModule.getUrl().equals("")) {
            Log.i("RecyclerViewAdapter", "url: " + objectModule.getUrl());
            type = objectModule.getType();
            fileType = type.substring(type.lastIndexOf('/') + 1);
            Log.i("RecyclerView", "type:" + type);
            holder.progressBar.setVisibility(View.VISIBLE);
            String img[];
            img = new String[]{"jpeg", "png", "jpg"};
            Boolean checkType = false;
            checkType = containsAny(type,img);

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
                holder.progressBar.setVisibility(View.GONE);
                holder.storedImage.setImageResource(R.drawable.file);
//                holder.storedImage.setCropToPadding(true);
                holder.textImage.setText(objectModule.getTitle());
                holder.textImage.setVisibility(View.VISIBLE);
            }

        } else {
            holder.cardView.setVisibility(View.GONE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                objectModule = mDataset.get(position);
//                setPosition(position);
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                           Uri.parse(objectModule.getUrl()));
//                Log.i("RecylcerVIew ", "url : " + objectModule.getUrl());
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("application/*");
//                PackageManager pm = context.getPackageManager();
//                List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
//                if (activities.size() > 0) {
//                    context.startActivity(intent);
//                } else {
//                    // Do something else here. Maybe pop up a Dialog or Toast
//                    Toast.makeText(context, "No app found", Toast.LENGTH_SHORT).show();
//                }

                type = objectModule.getType();
                fileType = type.substring(type.lastIndexOf('/') + 1);

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
        });
    }

    public static boolean containsAny(String str, String[] words)
    {
        boolean bResult=false; // will be set, if any of the words are found
        //String[] words = {"word1", "word2", "word3", "word4", "word5"};

        List<String> list = Arrays.asList(words);
        for (String word: list ) {
            boolean bFound = str.contains(word);
            if (bFound) {bResult=bFound; break;}
        }
        return bResult;
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
        TextView textImage;

        public EventsHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            storedImage = (ImageView) itemView.findViewById(R.id.stored_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            textImage = (TextView) itemView.findViewById(R.id.image_text);
            Log.i(LOG_TAG, "Adding Listener");
        }
    }
}
