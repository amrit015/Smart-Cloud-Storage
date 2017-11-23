package com.amrit.smartcloudstorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aditya.filebrowser.FileChooser;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


/**
 * Created by Amrit on 11/10/2017.
 */

public class UserActivity extends AppCompatActivity {

    Button chooseImg, uploadImg, downloadImg;
    Button chooseDocu, uploadDocu;
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    ModuleParcelable userDetails;
    ArrayList<Image> images;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://smartcloudstorage-017.appspot.com");
    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com"+"/StoragePhotoUrl/");

    String userId;
    FirebaseUser currentUser;
    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //firebase offline mode
//        database.setPersistenceEnabled(true);

        chooseImg = (Button) findViewById(R.id.chooseImg);
        uploadImg = (Button) findViewById(R.id.uploadImg);
        downloadImg = (Button) findViewById(R.id.downloadImg);
        chooseDocu = (Button) findViewById(R.id.chooseDocument);
        uploadDocu = (Button) findViewById(R.id.uploadDocument);
        imgView = (ImageView) findViewById(R.id.imgView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_chat);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };

        // Creating new user node, which returns the unique key value

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = currentUser.getDisplayName();
        final String userEmail = currentUser.getEmail();
        String userProvider = currentUser.getProviderId();
        Log.i("UserActivity : ", "Current User: " + currentUser);
        Log.i("UserActivity : ", "User Details: " + userName + " " + userEmail + " " + userProvider);
        userDetails.setEmail(userEmail);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, ChatServiceActivity.class);
                intent.putExtra("user", userEmail);
                startActivity(intent);
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserActivity.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });

        downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, DownloadImagesActivity.class);
                startActivity(intent);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri[] uri=new Uri[images.size()];
                for (int i =0 ; i < images.size(); i++) {
                    uri[i] = Uri.parse("file://"+images.get(i).path);
                    Log.i("UserActivity", "uri : "+ uri);
                    StorageReference ref = storageRef.child("Gallery/" + uri[i].getLastPathSegment());
                    final String temp = uri[i].getLastPathSegment();

                    Log.i("UserActivity", "ref : " + ref);
                    ref.putFile(uri[i])
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                String photoUrl = downloadUrl.toString();
                                String email = userDetails.getEmail();
                                String title = temp;

                                if (photoUrl.length()>0){
                                ModuleParcelable moduleParcelable = new ModuleParcelable(title,email,photoUrl);
                                    userId = databaseReference.push().getKey();
                                    databaseReference.child(userId).setValue(moduleParcelable);
                                }
                                Toast.makeText(UserActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            } }
        });

        chooseDocu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating an intent for file chooser
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
            }
        });

        uploadDocu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
        }
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                Log.i("UserActivity","pdf data : "+ data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
