package com.iamkatrechko.projectmanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class Project {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_COLOR = "color";
    private static final String JSON_LIST = "tasks_list";

    private UUID mID;
    private String mTitle;
    private int mColor;
    private ArrayList<Task> mTasks;

    public Project() {
        //Генерирование уникального идентификатора
        mID = UUID.randomUUID();
        mTasks = new ArrayList<Task>();
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

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public ArrayList<Task> getTasks() {
        return mTasks;
    }


    public Project(JSONObject json) throws JSONException {
        mID = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mColor = json.getInt(JSON_COLOR);

        mTasks = new ArrayList<Task>();
        JSONArray array = json.getJSONArray(JSON_LIST);
        for (int i = 0; i < array.length(); i++) {
            mTasks.add(new Task(array.getJSONObject(i)));
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mID.toString());                                                                              //Сохранение ID
        json.put(JSON_TITLE, mTitle);                                                                                   //Сохранение названия
        json.put(JSON_COLOR, mColor);                                                                                   //Сохранение цвета проекта

        //Сохранение списка проектов/подзадач
        JSONArray array = new JSONArray();
        for (Task t : mTasks)
            array.put(t.toJSON());
        json.put(JSON_LIST, array);
        /////////////////////////////////////

        return json;
    }
}
