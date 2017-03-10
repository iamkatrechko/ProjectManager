package com.iamkatrechko.projectmanager.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Класс сущность тега к задачам
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class Tag {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";

    /** Идентификатор тэга */
    private UUID mID;
    /** Заголовок тега */
    private String mTitle;

    /**
     * Конструктор
     * @param title заголовок тега
     */
    public Tag(String title) {
        mID = UUID.randomUUID();
        mTitle = title;
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
        json.put(JSON_ID, mID.toString());
        json.put(JSON_TITLE, mTitle);
        return json;
    }
}
