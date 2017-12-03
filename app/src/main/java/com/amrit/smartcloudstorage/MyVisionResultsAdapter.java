package com.amrit.smartcloudstorage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Amrit on 12/3/2017.
 */

public class MyVisionResultsAdapter extends RecyclerView.Adapter<MyVisionResultsAdapter.ResultsHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    Context context;
    ObjectVisionResults objectVisionResults;
    private ArrayList<ObjectVisionResults> mDataset;
    private int position;

    public MyVisionResultsAdapter(ViewPhotoActivity viewPhotoActivity, ArrayList<ObjectVisionResults> moduleList) {
        context = viewPhotoActivity;
        this.mDataset = moduleList;
    }

    @Override
    public MyVisionResultsAdapter.ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_vision_results, parent, false);
        context = parent.getContext();
        MyVisionResultsAdapter.ResultsHolder resultsHolder = new MyVisionResultsAdapter.ResultsHolder(view);
        return resultsHolder;
    }

    @Override
    public void onBindViewHolder(final ResultsHolder holder, final int position) {
        // binding the views to display the name, description and user in the recyclerview
        objectVisionResults = mDataset.get(position);
        if (objectVisionResults != null) {
            holder.progressBar.setVisibility(View.GONE);
            holder.label1.setText(objectVisionResults.getPrimaryLabel());
            holder.label2.setText(objectVisionResults.getSecondaryLabel());
            holder.landmark1.setText(objectVisionResults.getPrimaryLandmark());
            holder.landmark2.setText(objectVisionResults.getSecondaryLandmark());
            holder.webDescription1.setText(objectVisionResults.getPrimaryWeb());
            holder.webDescription2.setText(objectVisionResults.getSecondaryWeb());
            holder.webPage1.setText(objectVisionResults.getPrimaryPage());
            holder.webPage2.setText(objectVisionResults.getSecondaryPage());
        } else {
        }
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


    public class ResultsHolder extends RecyclerView.ViewHolder {
        TextView label1, label2,
                landmark1, landmark2,
                webDescription1, webDescription2,
                webPage2, webPage1;
        ProgressBar progressBar;

        public ResultsHolder(View itemView) {
            super(itemView);
            label1 = itemView.findViewById(R.id.label1);
            label2 = itemView.findViewById(R.id.label2);
            landmark1 = itemView.findViewById(R.id.landmark1);
            landmark2 = itemView.findViewById(R.id.landmark2);
            webDescription1 = itemView.findViewById(R.id.web_results1);
            webDescription2 = itemView.findViewById(R.id.web_results2);
            webPage1 = itemView.findViewById(R.id.web_page1);
            webPage2 = itemView.findViewById(R.id.web_page2);
            progressBar = itemView.findViewById(R.id.progress_vision);
            Log.i(LOG_TAG, "Adding Listener");
        }
    }
}
