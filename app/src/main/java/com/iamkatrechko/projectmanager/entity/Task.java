package com.iamkatrechko.projectmanager.entity;

import com.iamkatrechko.projectmanager.new_entity.TaskListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Класс сущность подпроекта/задачи
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class Task implements TaskListItem {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_IS_DONE = "is_done";
    private static final String JSON_TYPE = "type";
    private static final String JSON_DATE = "date";
    private static final String JSON_TIME = "time";
    private static final String JSON_IS_NOTIFY = "is_notify";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_IS_REPEAT = "is_repeat";
    private static final String JSON_LIST = "tasks_list";
    private static final String JSON_LIST_TAGS = "tags_list";

    public final static String TASK_TYPE_SUB_PROJECT = "sub_project";
    public final static String TASK_TYPE_TASK = "task";

    /** ID подпроекта/задачи */
    private UUID mID;
    /** Название подпроекта/задачи */
    private String mTitle;
    /** Описание подпроекта/задачи */
    private String mDescription;
    /** Метка о выполнении */
    private Boolean mIsDone;
    /** Тип задачи (подпроект/задача) */
    private String mType;
    /** Дата. Формат "dd.MM.yyyy" */
    private String mDate;
    /** Время. Формат "HH:MM" */
    private String mTime;
    /** Уведомление включено? */
    private boolean mIsNotify;
    /** Приоритет: 0 - без, 3 - высокий */
    private int mPriority;
    /** Повторяется? */
    private boolean mIsRepeat;
    /** Список ID тегов ({@link Tag}) */
    private ArrayList<UUID> mTags = new ArrayList<>();
    /** Подзадачи */
    private ArrayList<Task> mTasks = new ArrayList<>();

    /**
     * Конструктор
     * @param title заголовок подпроекта/задачи
     */
    public Task(String title) {
        mID = UUID.randomUUID();
        mTitle = title;
        mTags = new ArrayList<>();
        mTasks = new ArrayList<>();
        setIsDone(false);
        setIsNotify(false);
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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Boolean getIsDone() {
        if (mIsDone == null) {
            mIsDone = false;
        }
        return mIsDone;
    }

    public void setIsDone(Boolean isDone) {
        mIsDone = isDone;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getDate() {
        if (mDate == null) {
            return "null";
        }
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        if (mTime == null) {
            return "null";
        }
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public boolean getIsNotify() {
        return mIsNotify;
    }

    public void setIsNotify(boolean isNotify) {
        mIsNotify = isNotify;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public boolean getIsRepeat() {
        return mIsRepeat;
    }

    public void setIsRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    public void setTags(ArrayList<UUID> tags) {
        mTags = tags;
    }

    public ArrayList<UUID> getTags() {
        return mTags;
    }

    public ArrayList<Task> getTasks() {
        return mTasks;
    }


    public Task(JSONObject json) throws JSONException {
        mID = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mDescription = json.getString(JSON_DESCRIPTION);
        mType = json.getString(JSON_TYPE);

        if (mType.equals(TASK_TYPE_SUB_PROJECT)) {
            JSONArray array = json.getJSONArray(JSON_LIST);
            for (int i = 0; i < array.length(); i++) {
                mTasks.add(new Task(array.getJSONObject(i)));
            }
        } else {
            mIsDone = json.getBoolean(JSON_IS_DONE);
            mDate = json.optString(JSON_DATE, "null");
            mTime = json.optString(JSON_TIME, "null");
            mIsNotify = json.getBoolean(JSON_IS_NOTIFY);
            mPriority = json.getInt(JSON_PRIORITY);
            mIsRepeat = json.getBoolean(JSON_IS_REPEAT);
            //
            JSONArray array = json.getJSONArray(JSON_LIST_TAGS);
            for (int i = 0; i < array.length(); i++) {
                mTags.add(UUID.fromString(array.getString(i)));
            }
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        //Если сохраняемая задача является подпроектом
        if (mType.equals(TASK_TYPE_SUB_PROJECT)) {
            json.put(JSON_ID, mID.toString());
            json.put(JSON_TITLE, mTitle);
            json.put(JSON_DESCRIPTION, mDescription);
            json.put(JSON_TYPE, mType);
            //Сохранение списка подпроектов/задач
            JSONArray array = new JSONArray();
            for (Task t : mTasks)
                array.put(t.toJSON());
            json.put(JSON_LIST, array);
            /////////////////////////////////////

        } else {
            json.put(JSON_ID, mID.toString());
            json.put(JSON_TITLE, mTitle);
            json.put(JSON_DESCRIPTION, mDescription);
            json.put(JSON_TYPE, mType);
            json.put(JSON_IS_DONE, mIsDone);
            json.put(JSON_DATE, mDate);
            json.put(JSON_TIME, mTime);
            json.put(JSON_IS_NOTIFY, mIsNotify);
            json.put(JSON_PRIORITY, mPriority);
            json.put(JSON_IS_REPEAT, mIsRepeat);

            JSONArray array = new JSONArray();
            for (UUID t : mTags)
                array.put(t.toString());
            json.put(JSON_LIST_TAGS, array);
        }

        return json;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Методы //////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Проверяет наличие данной метки в задаче
     * @param tagID ID искомой метки
     * @return true, если метка присутствует
     */
    public boolean existTag(UUID tagID) {
        return getTags().contains(tagID);
    }

    public void addTag(Tag t) {
        if (!existTag(t.getID())) {
            getTags().add(t.getID());
        }
    }

    public void deleteTag(Tag t) {
        if (existTag(t.getID())) {
            getTags().remove(t.getID());
        }
    }

    @Override
    public int getViewType() {
        return getType().equals(TASK_TYPE_SUB_PROJECT) ? 0 : 1;
    }
}
