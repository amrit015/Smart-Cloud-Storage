package com.amrit.smartcloudstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Amrit on 12/4/2017.
 * User's Home Page inside the group.
 * The contents images, videos, songs, documents shared within the group are displayed on this activity.
 */

public class UserHomeActivity extends AppCompatActivity {

    Button chooseImg, uploadImg, downloadImg;
    Button chooseFile, uploadFile, downloadFile;
    ArrayList<Image> images;
    Uri documentPath;
    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://smartcloudstorage-017.appspot.com");
    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // for upload
    DatabaseReference fileDatabaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com" + "/FileStorage");
    // for download
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com");
    // for saving users into group
    DatabaseReference groupDataRef = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com" + "/Group_Authentication");

    private RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    //list to hold all the uploaded images
    private ArrayList<ObjectModule> moduleList;
    ProgressBar recyclerProgress;
    String userGroup;

    String userId;
    FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    FloatingActionButton chatButton, fileButton, imageButton;
    public static final String SHARED_PREF_NAME = "cloudLogIn";
    String userEmail;
    int i =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        //firebase offline mode
//        database.setPersistenceEnabled(true);

        chooseImg = findViewById(R.id.chooseImg);
        uploadImg = findViewById(R.id.uploadImg);
        downloadImg = findViewById(R.id.downloadImg);
        chooseFile = findViewById(R.id.chooseFile);
        uploadFile = findViewById(R.id.uploadFile);
        downloadFile = findViewById(R.id.downloadFile);
        chatButton = findViewById(R.id.floating_chat);
        fileButton = findViewById(R.id.floating_file_upload);
        imageButton = findViewById(R.id.floating_image_upload);
        mRecyclerView = findViewById(R.id.recycler_view);
        recyclerProgress = findViewById(R.id.progress_recycler);
        mGridLayoutManager = new GridLayoutManager(UserHomeActivity.this, 2);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchFiles();

        //getting the group on which the user is currently logged in
        // fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Fetching the boolean value form sharedpreferences
        userGroup = sharedPreferences.getString("user_group", "");

        Log.i("UserHomeActivity", "group name : " + userGroup);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserHomeActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = currentUser.getDisplayName();
        userEmail = currentUser.getEmail();
        String userProvider = currentUser.getProviderId();
        Log.i("UserHomeActivity : ", "Current User: " + currentUser);
        Log.i("UserHomeActivity : ", "User Details: " + userName + " " + userEmail + " " + userProvider);
        ModuleParcelable.setEmail(userEmail);

        // for chat within the group
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, ChatServiceActivity.class);
                intent.putExtra("user", userEmail);
                startActivity(intent);
            }
        });

        // for uploading images
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomeActivity.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);

            }
        });

        // for uploading files
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "image/*", "audio/*", "video/*",
                                "application/pdf",
                                "application/zip"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // version specfic
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 0);
            }
        });
    }

    // for returning the selected paths of files and images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            uploadImages();
        }
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                documentPath = data.getData();
                uploadFile();
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // uploading images
    private void uploadImages() {
        Toast.makeText(UserHomeActivity.this, "Uploading....", Toast.LENGTH_SHORT).show();
        if (images != null) {
            final Uri[] uri = new Uri[images.size()];
            for (int i = 0; i < images.size(); i++) {
                uri[i] = Uri.parse("file://" + images.get(i).path);
                Log.i("UserHomeActivity", "uri : " + uri);
                StorageReference ref = storageRef.child("Gallery/" + uri[i].getLastPathSegment());
                final String temp = uri[i].getLastPathSegment();

                Log.i("UserHomeActivity", "ref : " + ref);
                ref.putFile(uri[i])
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                String photoUrl = downloadUrl.toString();
                                String email = ModuleParcelable.getEmail();
                                String title = temp;
                                @SuppressWarnings("VisibleForTests") String type = taskSnapshot.getMetadata().getContentType();
                                Log.i("UserHomeActivity", "type :::::: " + type);

                                if (photoUrl.length() > 0) {
                                    ObjectModule objectModule = new ObjectModule(title, email, photoUrl, type, userGroup);
                                    userId = fileDatabaseReference.push().getKey();
                                    fileDatabaseReference.child(userId).setValue(objectModule);
                                }
                                Toast.makeText(UserHomeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserHomeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // uploading files
    private void uploadFile() {
        if (documentPath != null) {
            Toast.makeText(UserHomeActivity.this, "Uploading....", Toast.LENGTH_SHORT).show();
            Log.i("UserHomeActivity", "uri : " + documentPath);
            final String docuTitle = new File((documentPath).getPath()).getName();
            StorageReference ref = storageRef.child("Documents/" + docuTitle);

            Log.i("UserHomeActivity", "name : " + docuTitle);
            ref.putFile(documentPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri documentUrl = taskSnapshot.getDownloadUrl();
                            String fileUrl = documentUrl.toString();
                            String email = ModuleParcelable.getEmail();
                            String title = docuTitle;
                            @SuppressWarnings("VisibleForTests") String type = (taskSnapshot.getMetadata().getContentType());
                            Log.i("UserHomeActivity", "type :::::: " + type);

                            if (fileUrl.length() > 0) {
                                ObjectModule objectModule = new ObjectModule(title, email, fileUrl, type, userGroup);
                                userId = fileDatabaseReference.push().getKey();
                                fileDatabaseReference.child(userId).setValue(objectModule);
                            }
                            Toast.makeText(UserHomeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserHomeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // fetching files from the Firebase Storage
    private void fetchFiles() {
        final String userId = databaseReference.push().getKey();
        DatabaseReference info = databaseReference.child("FileStorage");

        info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                moduleList = new ArrayList<>();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ObjectModule objectModule = postSnapshot.getValue(ObjectModule.class);

                    Log.i("FileActivity", "FIle type: " + objectModule);

                    Log.i("DownloadFileActivity", "results: " + objectModule.getUrl());
                    // only adding to list if the user is in the group
                    if (userGroup.equals(objectModule.getUserGroup())) {
                        moduleList.add(objectModule);
                    }
                }
                MyFilesViewAdapter mAdapter = new MyFilesViewAdapter(UserHomeActivity.this, moduleList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                recyclerProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //menu initialization
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // using switch statement to implement actions for each menuItem
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                SignOutUser();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // signing out users
    private void SignOutUser() {
        auth.signOut();
        startActivity(new Intent(UserHomeActivity.this, SignInActivity.class));
        finish();
    }

}
