package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 11/11/2017.
 */

public class UserDetails {
    static String username = "";
    static String email = "";
    static String password = "";
    static String chatWith = "";
    public String photoTitle;
    public String photoUploader;
    public String photoUrl;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserDetails.email = email;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserDetails.username = username;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getPhotoUploader() {
        return photoUploader;
    }

    public void setPhotoUploader(String photoUploader) {
        this.photoUploader = photoUploader;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}