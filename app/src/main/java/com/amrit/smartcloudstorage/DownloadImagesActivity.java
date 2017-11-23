package com.amrit.smartcloudstorage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.input;

/**
 * Created by Amrit on 11/21/2017.
 */

public class DownloadImagesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    GridLayoutManager mGridLayoutManager;
    //database reference
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com");

    //list to hold all the uploaded images
    private ArrayList<PhotoModule> moduleList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mLayoutManager = new LinearLayoutManager(getActivity());
        mGridLayoutManager = new GridLayoutManager(DownloadImagesActivity.this,2);
        mRecyclerView.setHasFixedSize(false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchImages();
    }

    private void fetchImages() {
        final String userId = databaseReference.push().getKey();
        DatabaseReference info = databaseReference.child("StoragePhotoUrl");

        info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                moduleList = new ArrayList<>();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PhotoModule photoModule = postSnapshot.getValue(PhotoModule.class);
                    Log.i("DownloadImageActivity", "results: " + photoModule.getPhotoUrl());
                    moduleList.add(photoModule);
//                    Log.i("DownloadImageActivity", "list: " + moduleList);
                }
                MyRecyclerViewAdapter mAdapter = new MyRecyclerViewAdapter(DownloadImagesActivity.this, moduleList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
