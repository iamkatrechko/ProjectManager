package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Muxa on 27.02.2016.
 */
public class ProjectsJSONSerializer {
    private static ProjectsJSONSerializer sProjectJSON;
    private String mFilename = "JSON_FILE";
    private Context mContext;

    public ProjectsJSONSerializer(Context c) {
        mContext = c;
    }

    public static ProjectsJSONSerializer get(Context c) {
        if (sProjectJSON == null) {
            sProjectJSON = new ProjectsJSONSerializer(c.getApplicationContext());
        }
        return sProjectJSON;
    }

    public ArrayList<Project> loadProjects() throws IOException, JSONException {
        ArrayList<Project> projects = new ArrayList<Project>();
        BufferedReader reader = null;
        try {
            // Открытие и чтение файла в StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Разбор JSON с использованием JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            // Построение массива объектов Crime по данным JSONObject
            for (int i = 0; i < array.length(); i++) {
                projects.add(new Project(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            Log.d("JSONSerializer", "JSON файл не найден");

            //Генерация проектов по умолчанию при первом запуске
            //Происходит при начале "с нуля"; не обращайте внимания
        } finally {
            if (reader != null)
                reader.close();
        }
        return projects;
    }

    public void saveProjects(ArrayList<Project> crimes)
            throws JSONException, IOException {
        // Построение массива в JSON
        JSONArray array = new JSONArray();
        for (Project c : crimes)
            array.put(c.toJSON());
        // Запись файла на диск
        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
