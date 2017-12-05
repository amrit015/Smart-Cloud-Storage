package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 12/4/2017.
 */

public class GroupUsersModule {

    String user;
    String group;

    public GroupUsersModule(){

    }

    public GroupUsersModule(String user, String group) {
        this.user = user;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
