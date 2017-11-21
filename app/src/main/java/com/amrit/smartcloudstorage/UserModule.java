package com.amrit.smartcloudstorage;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amrit on 11/11/2017.
 */

public class UserModule {
    public String name;
    public String note;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public UserModule() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public UserModule(String name, String note) {
        this.name = name;
        this.note = note;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("note", note);
        result.put("starCount", starCount);
        result.put("stars", stars);
        return result;
    }
}