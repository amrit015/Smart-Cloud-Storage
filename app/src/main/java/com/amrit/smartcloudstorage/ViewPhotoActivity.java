package com.amrit.smartcloudstorage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebEntity;
import com.google.api.services.vision.v1.model.WebPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrit on 12/2/2017.
 */

public class ViewPhotoActivity extends AppCompatActivity {
    CardView cardView;
    TextView tTitle, tUploader, tResults;
    ProgressBar progressBar, progressBarResults;
    String sTitle, sUploader, sResults, sUrl;
    ImageView imageView;
    String VISION_API_KEY;
    ArrayList<ObjectVisionResults> listResults = new ArrayList<>();
    private String LOG_TAG = "ViewPhotoActivity";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        cardView = (CardView) findViewById(R.id.cardview_photo);
        tTitle = (TextView) findViewById(R.id.photo_title);
        tUploader = (TextView) findViewById(R.id.photo_uploader);
        imageView = (ImageView) findViewById(R.id.photo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarResults = (ProgressBar) findViewById(R.id.progress_results);
        VISION_API_KEY = getResources().getString(R.string.vision_api);

        mRecyclerView = (RecyclerView) findViewById(R.id.photo_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(ViewPhotoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sTitle = bundle.getString("photoTitle");
            sUploader = bundle.getString("uploader");
            sUrl = bundle.getString("photoUrl");
        }

        //using Glide to load images
        Glide.clear(imageView);
        Glide.with(ViewPhotoActivity.this)
                .load(sUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        tTitle.setText("Title : " + sTitle);
        tUploader.setText("Uploader : " + sUploader);
        new CallCloudVisionForResults().execute();
    }

    private ArrayList convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("Results:\n\n");
        message.append("Labels:\n");
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        String label1, label2;
        if (labels != null) {
            label1 = labels.get(0).getDescription();
            label2 = labels.get(1).getDescription();
        } else {
            label1 = "";
            label2 = "";
        }

        message.append("Landmarks:\n");
        List<EntityAnnotation> landmarks = response.getResponses().get(0)
                .getLandmarkAnnotations();
        String landmark1, landmark2;
        if (landmarks != null) {
            landmark1 = landmarks.get(0).getDescription();
            landmark2 = landmarks.get(1).getDescription();
        } else {
            landmark1 = "";
            landmark2 = "";
        }

        message.append("Web Entities:\n");
        WebDetection webEntities = response.getResponses().get(0)
                .getWebDetection();
        String webDescription1, webDescription2, webPage2, webPage1;
        List<WebEntity> webResults = webEntities.getWebEntities();
        List<WebPage> webLink = webEntities.getPagesWithMatchingImages();
        if (webResults != null) {
            webDescription1 = webResults.get(0).getDescription();
            webDescription2 = webResults.get(1).getDescription();
        } else {
            webDescription1 = "";
            webDescription2 = "";
        }

        if (webLink != null) {
            webPage1 = webLink.get(0).getUrl();
            webPage2 = webLink.get(1).getUrl();
        } else {
            webPage1 = "";
            webPage2 = "";
        }

        ObjectVisionResults obj = new ObjectVisionResults();
        obj.setPrimaryLabel(label1);
        obj.setSecondaryLabel(label2);
        obj.setPrimaryLandmark(landmark1);
        obj.setSecondaryLandmark(landmark2);
        obj.setPrimaryWeb(webDescription1);
        obj.setSecondaryWeb(webDescription2);
        obj.setPrimaryPage(webPage1);
        obj.setSecondaryPage(webPage2);
        listResults.add(obj);
        return listResults;
    }

    // converting the image to jpeg from bitmap
    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    private class CallCloudVisionForResults extends AsyncTask<Object, Object, ArrayList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList doInBackground(Object... params) {
            try {
                // converting the image in firebase to bitmap
                URL url = new URL(sUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                // vision api initialization and call
                Vision.Builder visionBuilder = new Vision.Builder(
                        new NetHttpTransport(),
                        new AndroidJsonFactory(),
                        null);

                visionBuilder.setVisionRequestInitializer(
                        new VisionRequestInitializer(VISION_API_KEY));

                Vision vision = visionBuilder.build();

                // features in the vision api
                List<Feature> featureList = new ArrayList<>();

                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(2);
                featureList.add(labelDetection);

                Feature webEntities = new Feature();
                webEntities.setType("WEB_DETECTION");
                webEntities.setMaxResults(2);
                featureList.add(webEntities);
//
//                Feature textDetection = new Feature();
//                textDetection.setType("TEXT_DETECTION");
//                textDetection.setMaxResults(10);
//                featureList.add(textDetection);

                Feature landmarkDetection = new Feature();
                landmarkDetection.setType("LANDMARK_DETECTION");
                landmarkDetection.setMaxResults(2);
                featureList.add(landmarkDetection);

                //  decoding the image to bitmap and passing the image to vision API
                List<AnnotateImageRequest> imageList = new ArrayList<>();
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                annotateImageRequest.setImage(base64EncodedImage);
                annotateImageRequest.setFeatures(featureList);
                imageList.add(annotateImageRequest);

                BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                        new BatchAnnotateImagesRequest();
                batchAnnotateImagesRequest.setRequests(imageList);

                Vision.Images.Annotate annotateRequest =
                        vision.images().annotate(batchAnnotateImagesRequest);
                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                annotateRequest.setDisableGZipContent(true);
                Log.d(LOG_TAG, "sending request");

                BatchAnnotateImagesResponse response = annotateRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.e(LOG_TAG, "Request failed: " + e.getContent());
            } catch (IOException e) {
                Log.d(LOG_TAG, "Request failed: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            Log.i(LOG_TAG, "Results from Vision API : " + result);
            MyVisionResultsAdapter mAdapter = new MyVisionResultsAdapter(ViewPhotoActivity.this, result);
            mRecyclerView.setAdapter(mAdapter);
            progressBarResults.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }
}
