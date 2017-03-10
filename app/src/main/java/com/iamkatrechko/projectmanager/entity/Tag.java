package com.iamkatrechko.projectmanager.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Tag {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";

    private UUID mID;
    private String mTitle;

    public Tag() {
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

    public Tag(JSONObject json) throws JSONException {
        mID = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mID.toString());                                                                              //Сохранение ID
        json.put(JSON_TITLE, mTitle);                                                                                   //Сохранение названия
        return json;
    }
}
