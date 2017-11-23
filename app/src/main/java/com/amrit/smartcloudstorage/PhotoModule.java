package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 11/22/2017.
 */

public class PhotoModule {
    public String photoTitle;
    public String photoUploader;
    public String photoUrl;

    public PhotoModule(){

    }

    public PhotoModule(String title,String uploader,String url){
        photoTitle = title;
        photoUploader = uploader;
        photoUrl = url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUploader() {
        return photoUploader;
    }

    public void setPhotoUploader(String photoUploader) {
        this.photoUploader = photoUploader;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }
}
