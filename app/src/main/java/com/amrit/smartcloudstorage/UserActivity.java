package com.amrit.smartcloudstorage;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Amrit on 11/10/2017.
 */

public class UserActivity extends AppCompatActivity {

    Button chooseImg, uploadImg, downloadImg;
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    ArrayList<UserModule> userList = new ArrayList<UserModule>();
    UserModule userModule;
    UserDetails userDetails;
    ArrayList<Image> images;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://smartcloudstorage-017.appspot.com");
    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://smartcloudstorage-017.firebaseio.com");

    String userId;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //firebase offline mode
//        database.setPersistenceEnabled(true);

        chooseImg = (Button) findViewById(R.id.chooseImg);
        uploadImg = (Button) findViewById(R.id.uploadImg);
        downloadImg = (Button) findViewById(R.id.downloadImg);
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
        // new user node would be /users/$userid/
        userId = databaseReference.push().getKey();

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


        // creating user in database
//        writeNewUser("Amrit008", "amrit008@gmail.com");
        //writing in the user
//        writeNewPost("Amrit", "Just a sample note");

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                Intent intent = new Intent(UserActivity.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });

        downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference imageRef = storageRef.child("image.jpg");
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imgView.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                } catch (IOException e) {
                }
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (filePath != null) {
//                    pd.show();
//
//                    StorageReference childRef = storageRef.child("image.jpg");
//
//                    //uploading the image
//                    UploadTask uploadTask = childRef.putFile(filePath);
//
//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            pd.dismiss();
//                            Toast.makeText(UserActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            pd.dismiss();
//                            Toast.makeText(UserActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(UserActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
//                }

                final Uri[] uri=new Uri[images.size()];
                for (int i =0 ; i < images.size(); i++) {
                    uri[i] = Uri.parse("file://"+images.get(i).path);
                    Log.i("UserActivity", "uri : "+ uri);
                    StorageReference ref = storageRef.child("Gallery/" + uri[i].getLastPathSegment());
                    final String title = uri[i].getLastPathSegment();

                    Log.i("UserActivity", "ref : " + ref);
                    ref.putFile(uri[i])
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                String photoUrl = downloadUrl.toString();
                                String email = userDetails.getEmail();
                                if (photoUrl.length()>0){
                                UserDetails userDetails = new UserDetails();
                                    userDetails.photoTitle = title;
                                    userDetails.photoUploader = email;
                                    userDetails.photoUrl = photoUrl;
                                    databaseReference.child("StoragePhotoUrl/").child(userId).setValue(userDetails);
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
    }

//    private void writeNewPost(String name, String note) {
//        // Create new post at /user-posts/$userid/$postid and at
//        // /posts/$postid simultaneously
//        String key = mDatabase.child("posts").push().getKey();
//        UserModule post = new UserModule(name, note);
//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//        mDatabase.updateChildren(childUpdates);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//
//            try {
//                //getting image from gallery
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//
//                //Setting image to ImageView
//                imgView.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
        }
    }

//    private void writeNewUser(String username, String email) {
//        User user = new User(username, email);
//
//        mDatabase.child("users").child(userId).setValue(user);
//    }

}
