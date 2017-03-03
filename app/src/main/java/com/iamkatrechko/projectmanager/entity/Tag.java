package com.iamkatrechko.projectmanager.entity;

import java.util.UUID;

public class Tag {

    private UUID mID;
    private String mTitle;

    public Tag(){
        mID = UUID.randomUUID();
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
