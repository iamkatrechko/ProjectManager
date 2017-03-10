package com.iamkatrechko.projectmanager.entity;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Класс сущность проекта с задачами
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class Project {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_COLOR = "color";
    private static final String JSON_LIST = "tasks_list";

    /** Идентификатор проекта */
    private UUID mID;
    /** Заголовок проекта */
    private String mTitle;
    /** Цвет иконки проекта */
    private int mColor;
    /** Список задач */
    private ArrayList<Task> mTasks = new ArrayList<>();

    /**
     * Конструктор
     * @param title заголовок проекта
     */
    public Project(String title) {
        this(title, generateRandomColor());
    }

    public Project(String title, @ColorInt int color) {
        mID = UUID.randomUUID();
        mTitle = title;
        mColor = color;
        mTasks = new ArrayList<>();
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

        mTasks = new ArrayList<>();
        JSONArray array = json.getJSONArray(JSON_LIST);
        for (int i = 0; i < array.length(); i++) {
            mTasks.add(new Task(array.getJSONObject(i)));
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mID.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_COLOR, mColor);

        //Сохранение списка проектов/подзадач
        JSONArray array = new JSONArray();
        for (Task t : mTasks)
            array.put(t.toJSON());
        json.put(JSON_LIST, array);
        /////////////////////////////////////

        return json;
    }

    private static int generateRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return Color.rgb(r, g, b);
    }
}
