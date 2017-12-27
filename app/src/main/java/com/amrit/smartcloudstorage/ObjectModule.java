package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 11/22/2017.
 * Object for files in the database
 */

public class ObjectModule {
    public String title;
    public String uploader;
    public String url;
    public String type;
    public String userGroup;

    public ObjectModule(){

    }

    public ObjectModule(String title, String uploader, String url, String type, String userGroup){
        this.title = title;
        this.uploader = uploader;
        this.url = url;
        this.type = type;
        this.userGroup = userGroup;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }
}
