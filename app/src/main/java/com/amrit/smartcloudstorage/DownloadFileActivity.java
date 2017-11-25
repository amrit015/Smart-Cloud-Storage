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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Amrit on 11/24/2017.
 */

public class DownloadFileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    GridLayoutManager mGridLayoutManager;
    //database reference
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com");

    //list to hold all the uploaded images
    private ArrayList<ObjectModule> moduleList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mLayoutManager = new LinearLayoutManager(getActivity());
        mGridLayoutManager = new GridLayoutManager(DownloadFileActivity.this,2);
        mRecyclerView.setHasFixedSize(false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchImages();
    }

    private void fetchImages() {
        final String userId = databaseReference.push().getKey();
        DatabaseReference info = databaseReference.child("FileStorage");

        info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                moduleList = new ArrayList<>();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ObjectModule objectModule = postSnapshot.getValue(ObjectModule.class);

                    Log.i("FileActivity", "FIle type: "+ objectModule);

                    Log.i("DownloadFileActivity", "results: " + objectModule.getUrl());
                    moduleList.add(objectModule);
//                    Log.i("DownloadImageActivity", "list: " + moduleList);
                }
                MyRecyclerViewAdapter mAdapter = new MyRecyclerViewAdapter(DownloadFileActivity.this, moduleList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
