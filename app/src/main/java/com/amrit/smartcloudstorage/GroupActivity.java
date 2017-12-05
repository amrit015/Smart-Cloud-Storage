package com.amrit.smartcloudstorage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Amrit on 12/4/2017.
 */

public class GroupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // for upload
    DatabaseReference groupDatabaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com" + "/GroupUsers");
    // for download
    DatabaseReference groupDbReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com");
    TextView tCreateGroup;
    ListView listview;
    CreateGroupModule createGroupModule;
    String userId;
    FirebaseUser currentUser;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    ArrayList<CreateGroupModule> groupList;
    ArrayList<GroupUsersModule> groupUsersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        tCreateGroup = (TextView) findViewById(R.id.new_group);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_group);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(GroupActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(GroupActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };
        // Creating new user node, which returns the unique key value
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = currentUser.getEmail();
        tCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(GroupActivity.this);
                final View view = inflater.inflate(R.layout.alert_create_group, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(GroupActivity.this).create();
                alertDialog.setTitle("Enter Group Details!");
                alertDialog.setCancelable(false);
                final EditText name = view.findViewById(R.id.create_group_name);
                final EditText pass = view.findViewById(R.id.create_group_pass);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupName = name.getText().toString();
                        String groupPass = pass.getText().toString();
                        createGroupModule = new CreateGroupModule(groupName, groupPass);
                        userId = groupDatabaseReference.push().getKey();
                        groupDatabaseReference.child(userId).setValue(createGroupModule);
                    }
                });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
            }
        });
        fetchGroupUsers();
        fetchGroups();
    }

    private void fetchGroupUsers() {

    }

    private void fetchGroups() {

        final DatabaseReference groupAuth = groupDbReference.child("Group_Authentication");
        groupAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupUsersList = new ArrayList<>();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GroupUsersModule groupUsersModule = postSnapshot.getValue(GroupUsersModule.class);

                    Log.i("FileActivity", "FIle type: "+ groupUsersModule);

                    Log.i("DownloadFileActivity", "results: " + groupUsersModule.getUser());
                    groupUsersList.add(groupUsersModule);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference group = groupDbReference.child("GroupUsers");

        group.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList = new ArrayList<>();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CreateGroupModule createGroupModule = postSnapshot.getValue(CreateGroupModule.class);

                    Log.i("FileActivity", "FIle type: "+ createGroupModule);

                    Log.i("DownloadFileActivity", "results: " + createGroupModule.getGroupName());
                    groupList.add(createGroupModule);
//                    Log.i("DownloadImageActivity", "list: " + moduleList);
                }
//                MyGroupViewAdapter mAdapter = new MyGroupViewAdapter(GroupActivity.this, groupList ,groupUsersList);
                MyGroupViewAdapter mAdapter = new MyGroupViewAdapter(GroupActivity.this, groupList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
