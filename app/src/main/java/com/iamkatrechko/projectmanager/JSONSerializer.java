package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.util.Log;

import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

/**
 * Класс для работы с данными приложения в памяти устройства
 * @author iamkatrechko
 *         Date: 27.02.2016
 */
public class JSONSerializer {
    /** Статический экземпляр класса */
    private static JSONSerializer sProjectJSON;
    /** Имя файла для записи списка проектов */
    private static final String mFilename = "JSON_FILE";
    /** Имя файла для записи списка тегов */
    private static final String mFileNameTags = "JSON_FILE_TAGS";
    /** Контекст */
    private Context mContext;

    /**
     * Конструктор
     * @param context контекст
     */
    private JSONSerializer(Context context) {
        mContext = context;
    }

    /**
     * Получить статический экземпляр класса
     * @param context контекст
     * @return класс для работы с данными приложения в памяти устройства
     */
    public static JSONSerializer get(Context context) {
        if (sProjectJSON == null) {
            sProjectJSON = new JSONSerializer(context.getApplicationContext());
        }
        return sProjectJSON;
    }

    /**
     * Получить список проектов из памяти устройства
     * @return список проектов из памяти устройства
     * @throws JSONException ошибка парсинга JSON
     * @throws IOException   ошибка записи/чтения
     */
    public ArrayList<Project> loadProjects() throws IOException, JSONException {
        ArrayList<Project> projects = new ArrayList<>();
        try {
            JSONArray array = getJSONArray(mFilename);
            for (int i = 0; i < array.length(); i++) {
                projects.add(new Project(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            Log.d("JSONSerializer", "JSON файл не найден");
        }
        return projects;
    }

    /**
     * Получить список тегов из памяти устройства
     * @return список тегов из памяти устройства
     * @throws JSONException ошибка парсинга JSON
     * @throws IOException   ошибка записи/чтения
     */
    public ArrayList<Tag> loadTags() throws IOException, JSONException {
        ArrayList<Tag> resultTags = new ArrayList<>();
        try {
            JSONArray array = getJSONArray(mFileNameTags);
            // Построение массива объектов Crime по данным JSONObject
            for (int i = 0; i < array.length(); i++) {
                resultTags.add(new Tag(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            Log.d("JSONSerializer", "JSON Tags файл не найден");
        }
        return resultTags;
    }

    /**
     * Сохранить список проектов с задачами в память устройства
     * @param projects список проектов с задачами
     * @throws JSONException ошибка парсинга JSON
     * @throws IOException   ошибка записи/чтения
     */
    public void saveProjects(ArrayList<Project> projects) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Project c : projects) {
            array.put(c.toJSON());
        }
        saveToJSON(mFilename, array);
    }

    /**
     * Сохранить список тегов в память устройства
     * @param tags список тегов
     * @throws JSONException ошибка парсинга JSON
     * @throws IOException   ошибка записи/чтения
     */
    public void saveTags(ArrayList<Tag> tags) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Tag tag : tags) {
            array.put(tag.toJSON());
        }
        saveToJSON(mFileNameTags, array);
    }

    /**
     * Получить JSON-массив из файла
     * @param filename имя файла
     * @return JSON-массив
     * @throws JSONException ошибка парсинга JSON
     * @throws IOException   ошибка записи/чтения
     */
    private JSONArray getJSONArray(String filename) throws JSONException, IOException {
        JSONArray array;
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
        } finally {
            if (reader != null)
                reader.close();
        }
        return array;
    }

    /**
     * Сохранить JSON-массив в файл
     * @param fileName  имя файла
     * @param jsonArray JSON-массив
     * @throws IOException ошибка записи/чтения
     */
    private void saveToJSON(String fileName, JSONArray jsonArray) throws IOException {
        // Запись файла на диск
        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}