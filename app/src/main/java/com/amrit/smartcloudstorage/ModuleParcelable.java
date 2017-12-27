package com.amrit.smartcloudstorage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amrit on 11/11/2017.
 * Parcelable Object
 */

public class ModuleParcelable implements Parcelable {
    public static final Parcelable.Creator<ModuleParcelable> CREATOR = new Parcelable.Creator<ModuleParcelable>() {
        //Generates instance of Parcelable class from a parcel

        @Override
        public ModuleParcelable createFromParcel(Parcel source) {
            /* Creates instance of new Parcelable class,instantiating it from the given Parcel
               whose data were previously written with writeToParcel
            */
            return new ModuleParcelable(source);
        }

        @Override
        public ModuleParcelable[] newArray(int size) {
            return new ModuleParcelable[size];
        }
    };

    static String username = "";
    static String email = "";
    static String password = "";
    static String chatWith = "";
    public String photoTitle;
    public String photoUploader;
    public String photoUrl;

    public ModuleParcelable() {
        //default constructor
    }

    public ModuleParcelable(String photoTitle,String photoUploader, String photoUrl){
        this.photoTitle = photoTitle;
        this.photoUploader = photoUploader;
        this.photoUrl = photoUrl;
    }
    private ModuleParcelable(Parcel in) {
        /* reading back each field from the parcel in the same order it was written
           to the parcel
        */
        username = in.readString();
        email = in.readString();
        password = in.readString();
        this.photoTitle = in.readString();
        this.photoUploader = in.readString();
        this.photoUrl = in.readString();
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        ModuleParcelable.email = email;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ModuleParcelable.username = username;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /* writing each field to the parcel,same order is used to read from parcel
        */
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(photoTitle);
        dest.writeString(photoUploader);
        dest.writeString(photoUrl);
    }
}