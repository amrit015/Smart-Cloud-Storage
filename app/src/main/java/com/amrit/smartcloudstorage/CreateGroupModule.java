package com.amrit.smartcloudstorage;

/**
 * Created by Amrit on 12/4/2017.
 */

public class CreateGroupModule {

    String groupName;
    String groupPass;

    public CreateGroupModule(){

    }

    public CreateGroupModule(String groupName, String groupPass) {
        this.groupName = groupName;
        this.groupPass = groupPass;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPass() {
        return groupPass;
    }

    public void setGroupPass(String groupPass) {
        this.groupPass = groupPass;
    }
}
