package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 12/2/2017.
 * Object for storing the results obtained through the Google Vision API
 */

public class ObjectVisionResults {

    public String primaryLabel;
    public String secondaryLabel;
    public String primaryLandmark;
    public String secondaryLandmark;
    public String primaryWeb;
    public String secondaryWeb;
    public String primaryPage;
    public String secondaryPage;

    public String getSecondaryLabel() {
        return secondaryLabel;
    }

    public void setSecondaryLabel(String secondaryLabel) {
        this.secondaryLabel = secondaryLabel;
    }

    public String getPrimaryLabel() {
        return primaryLabel;
    }

    public void setPrimaryLabel(String primaryLabel) {
        this.primaryLabel = primaryLabel;
    }

    public String getSecondaryLandmark() {
        return secondaryLandmark;
    }

    public void setSecondaryLandmark(String secondaryLandmark) {
        this.secondaryLandmark = secondaryLandmark;
    }

    public String getPrimaryLandmark() {
        return primaryLandmark;
    }

    public void setPrimaryLandmark(String primaryLandmark) {
        this.primaryLandmark = primaryLandmark;
    }

    public String getPrimaryPage() {
        return primaryPage;
    }

    public void setPrimaryPage(String primaryPage) {
        this.primaryPage = primaryPage;
    }

    public String getSecondaryPage() {
        return secondaryPage;
    }

    public void setSecondaryPage(String secondaryPage) {
        this.secondaryPage = secondaryPage;
    }

    public String getSecondaryWeb() {
        return secondaryWeb;
    }

    public void setSecondaryWeb(String secondaryWeb) {
        this.secondaryWeb = secondaryWeb;
    }

    public String getPrimaryWeb() {
        return primaryWeb;
    }

    public void setPrimaryWeb(String primaryWeb) {
        this.primaryWeb = primaryWeb;
    }

}
