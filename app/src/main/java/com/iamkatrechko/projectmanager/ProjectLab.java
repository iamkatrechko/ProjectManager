package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.util.Log;

import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;
import com.iamkatrechko.projectmanager.entity.Task;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/** Класс по работе с проектами и задачами */
public class ProjectLab {
    /** Список проектов и задач */
    private ArrayList<Project> mProjects = new ArrayList<>();
    /** Список тэгов */
    private ArrayList<Tag> mTags = new ArrayList<>();
    /** Статический экземпляр синглтона */
    private static ProjectLab sProjectLab;
    /** Контекст */
    private Context mContext;
    /** Класс для сериализации данных в JSON формате */
    private JSONSerializer mJSONSerializer;

    private ProjectLab(Context context) {
        mContext = context;
        mJSONSerializer = JSONSerializer.get(context);

        //generateTags();
        loadTagsFromJSON();
        loadProjectsFromJSON();
        //generateTags();
        //generateTestData();
    }

    public static ProjectLab get(Context c) {
        if (sProjectLab == null) {
            sProjectLab = new ProjectLab(c.getApplicationContext());
        }
        return sProjectLab;
    }

    private void loadProjectsFromJSON() {
        try {
            mProjects = mJSONSerializer.loadProjects();
        } catch (IOException | JSONException e) {
            Log.d("ProjectLab", "Ошибка загрузки");
            e.printStackTrace();
        }
    }

    public void saveProjectsIntoJSON() {
        try {
            mJSONSerializer.saveProjects(mProjects);
        } catch (JSONException | IOException e) {
            Log.d("ProjectLab", "Ошибка сохранения");
            e.printStackTrace();
        }
    }

    private void loadTagsFromJSON() {
        try {
            mTags = mJSONSerializer.loadTags();
        } catch (IOException | JSONException e) {
            Log.d("ProjectLab", "Ошибка загрузки");
            e.printStackTrace();
        }
    }

    public void saveTagsIntoJSON() {
        try {
            mJSONSerializer.saveTags(mTags);
        } catch (JSONException | IOException e) {
            Log.d("ProjectLab", "Ошибка сохранения");
            e.printStackTrace();
        }
    }

    public ArrayList<Project> getProjects() {
        return mProjects;
    }

    /**
     * Возвращает {@link Project}
     * из списка всех проектов
     * @param id mId искомого проекта
     * @return Проект
     */
    public Project getProject(UUID id) {
        for (Project p : mProjects) {
            if (p.getID().equals(id))
                return p;
        }
        return null;
    }

    /**
     * Возвращает {@link Task}, находящийся
     * на любой глубине проекта
     * @param id mId искомого подпроекта/задачи
     * @return Подпроект/задача
     */
    public Task getTaskOnAllLevel(UUID id) {
        for (Project p : mProjects) {
            for (Task t1 : p.getTasks()) {
                if (t1.getID().equals(id)) {
                    return t1;
                } else {
                    for (Task t2 : t1.getTasks()) {
                        if (t2.getID().equals(id)) {
                            return t2;
                        } else {
                            for (Task t3 : t2.getTasks()) {
                                if (t3.getID().equals(id)) {
                                    return t3;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    //Поиск любого родительского элемента на уровнях проектов и подпроектов (2 из 3)
    /*public Object getProjectOrTask(UUID id){
        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                return p;
            }else {
                for (Task t : p.getTasks()) {
                    if (t.getID().equals(id))
                        return t;
                }
            }
        }
        return null;
    }*/

    /**
     * Возвращает уровень (глубину) проекта/подпроекта/задачи
     * @param id mId проекта/подпроекта/задачи
     * @return Уровень (0 - 4)
     */
    public int getLevelOfParent(UUID id) {
        //Уровни: 0 - проект; 1, 2, 3 - подпроекты
        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                return 0;                                                                                               //Уровень 0
            } else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(id)) {
                        return 1;                                                                                       //Уровень 1
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(id)) {
                                return 2;                                                                               //Уровень 2
                            } else {                                                                                      //Отключение создания подпроектов на уровне
                                for (Task t3 : t2.getTasks()) {
                                    if (t3.getID().equals(id)) {
                                        return 3;                                                                       //Уровень 3
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Возвращает список {@link Task} проекта/подпроекта
     * @param parentID mId родителя (проекта/подпроекта)
     * @return Список задач
     */
    public List<Task> getTasksListOnAllLevel(UUID parentID) {
        for (Project p : mProjects) {
            if (p.getID().equals(parentID)) {
                return p.getTasks();
            } else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(parentID)) {
                        return t1.getTasks();
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(parentID)) {
                                return t2.getTasks();
                            } else {
                                for (Task t3 : t2.getTasks()) {
                                    if (t3.getID().equals(parentID)) {
                                        return t3.getTasks();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Возвращает позицию последнего подпроекта в списке задач/подпроектов
     * для вставки созданного подпроекта перед задачами
     * @param parentID mId родителя (проекта/подпроекта)
     * @return Позиция в списке
     */
    public int getLastTaskIndex(UUID parentID) {
        List<Task> list = getTasksListOnAllLevel(parentID);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType().equals(Task.TASK_TYPE_TASK)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Возвращает список задач на сегодня
     * @return список задач на сегодня
     */
    public List<Task> getTodayTasks() {
        List<Task> result = new ArrayList<>();
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().getTimeInMillis());

        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                    if (t1.getDate().equals(date)) {
                        result.add(t1);
                    }
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                            if (t2.getDate().equals(date)) {
                                result.add(t2);
                            }
                        } else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.getType().equals(Task.TASK_TYPE_TASK)) {
                                    if (t3.getDate().equals(date)) {
                                        result.add(t3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Возвращает {@link Project}, в котором находится входная задача
     * @param id mId задачи
     * @return Проект
     */
    public Project getProjectOfTask(UUID id) {
        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                return p;
            } else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(id)) {
                        return p;
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(id)) {
                                return p;
                            } else {
                                for (Task t3 : t2.getTasks()) {
                                    if (t3.getID().equals(id)) {
                                        return p;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return mProjects.get(0);
    }

    /**
     * Полностью удаляет {@link Task} по его mId
     * @param id mId задачи для удаления
     */
    public void removeTaskByID(UUID id) {
        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getID().equals(id)) {
                    p.getTasks().remove(t1);
                    return;
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getID().equals(id)) {
                            t1.getTasks().remove(t2);
                            return;
                        } else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.getID().equals(id)) {
                                    t2.getTasks().remove(t3);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Перемещает {@link Task} на выбранную позицию,
     * сдвигая остальные вверх/вниз
     * @param parentID mId проекта/подпроекта перемещаемой задачи
     */
    public void moveItem(UUID parentID, int fromPosition, int toPosition) {
        List<Task> list = getTasksListOnAllLevel(parentID);

        Log.d("onItemMove - Positions", "" + fromPosition + " : " + toPosition);
        Log.d("onItemMove - Title", list.get(fromPosition).getTitle());

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

    /**
     * Возвращает mId родительского элемента
     * @param id mId подпроекта/задачи, родителя которого
     *           требуется найти
     * @return mId родителя
     */
    public UUID getParentIdOfTask(UUID id) {
        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getID().equals(id)) {
                    return p.getID();
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getID().equals(id)) {
                            return t1.getID();
                        } else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.getID().equals(id)) {
                                    return t2.getID();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Возвращает путь к проекту/подпроекту, включая его имя
     * @param id mId проекта/подпроекта
     * @return Путь к проекту/подпроекту
     */
    public String getHistory(UUID id) {
        String history = " > ";

        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                history += p.getTitle() + " > ";
                return history;
            }

            for (Task t1 : p.getTasks()) {
                if (t1.getID().equals(id)) {
                    history += p.getTitle() + " > " + t1.getTitle() + " > ";
                    return history;
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getID().equals(id)) {
                            history += p.getTitle() + " > " + t1.getTitle() + " > " + t2.getTitle() + " > ";
                            return history;
                        }
                    }
                }
            }
        }

        return history;
    }

    /**
     * Возвращает список всез задач {@link Task} всех проектов
     * @return Список задач {@link Task}
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<Task>();

        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                    tasksList.add(t1);
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                            tasksList.add(t2);
                        } else {

                            for (Task t3 : t2.getTasks()) {
                                tasksList.add(t3);
                            }
                        }
                    }
                }
            }
        }

        return tasksList;
    }

    /**
     * Возвращает задачи определенного приоритета
     * @param priority Искомый приоритет
     * @return Список задач
     */
    public ArrayList<Task> getTasksPriority(int priority) {
        ArrayList<Task> tasksList = new ArrayList<Task>();

        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                    if (t1.getPriority() == priority) {
                        tasksList.add(t1);
                    }
                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                            if (t2.getPriority() == priority) {
                                tasksList.add(t2);
                            }
                        } else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.getPriority() == priority) {
                                    tasksList.add(t3);
                                }
                            }
                        }
                    }
                }
            }
        }

        return tasksList;
    }

    public ArrayList<Task> getTasksListByTagID(UUID tagID) {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                    if (t1.existTag(tagID)) tasksList.add(t1);

                } else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                            if (t2.existTag(tagID)) tasksList.add(t2);

                        } else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.existTag(tagID)) tasksList.add(t3);
                            }
                        }
                    }
                }
            }
        }

        return tasksList;
    }

    public Tag getTagByID(UUID tagID) {
        for (Tag t : mTags) {
            if (t.getID().equals(tagID)) {
                return t;
            }
        }
        // Доделать удаление метки из задач, если ее нету
        return null;
    }

    public void addNewTag(Tag tag) {
        mTags.add(tag);
        saveTagsIntoJSON();
    }

    public void saveTag(UUID id, Tag tag) {
        Tag tagToSave = getTagByID(id);
        tagToSave.setTitle(tag.getTitle());
        saveTagsIntoJSON();
    }

    /**
     * Удаляет заданный тег
     * @param tag удаляемый тег
     */
    public void deleteTag(Tag tag) {
        mTags.remove(tag);
        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                t1.deleteTag(tag);

                for (Task t2 : t1.getTasks()) {
                    t2.deleteTag(tag);

                    for (Task t3 : t2.getTasks()) {
                        t3.deleteTag(tag);
                    }
                }
            }
        }
        saveTagsIntoJSON();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void generateTags() {
        for (int i = 0; i < 6; i++) {
            Tag t = new Tag("Тэг #" + i);
            mTags.add(t);
        }
    }

    public void reloadData() {
        mProjects = new ArrayList<>();
        generateTestData();
    }

    public void deleteData() {
        mProjects = new ArrayList<>();
        Project p = new Project("Общий список");
        p.setColor(mContext.getResources().getColor(R.color.actionBarColor1));
        mProjects.add(p);
    }

    public void generateTestData() {
        String[] projectNamesList = {"Общий список - ", "Личные цели - ", "Рабочий список - ", "Покупки - ", "Фильмы к просмотру", "Проект #"};
        Random random = new Random();

        for (int i = 1; i <= projectNamesList.length; i++) {
            Project p = new Project(projectNamesList[i - 1] + i);
            int color = -random.nextInt(16777216) + 1;
            p.setColor(color);

            for (int k = 1; k < 4; k++) {
                Task task = new Task("Подпроект " + i + "-" + k);
                task.setDescription("Описание " + i + "-" + k);
                task.setType(Task.TASK_TYPE_SUB_PROJECT);

                for (int m = 1; m < 4; m++) {
                    Task task2 = new Task("Подподпроект " + i + "-" + m);
                    task2.setDescription("Описание " + i + "-" + m);
                    task2.setType(Task.TASK_TYPE_SUB_PROJECT);

                    for (int n = 1; n < 15; n++) {
                        Task subTask = new Task("Задача " + i + "-" + k + "-" + n);
                        subTask.setDescription("Описание " + i + "-" + k + "-" + n);
                        subTask.setIsDone(n % 5 == 0);
                        subTask.setType(Task.TASK_TYPE_TASK);
                        subTask.setDate("01.01.2001");
                        subTask.setTime("11:11");
                        subTask.setPriority(random.nextInt(4));
                        subTask.setIsRepeat(false);
                        subTask.getTags().add(mTags.get(0).getID());
                        subTask.getTags().add(mTags.get(2).getID());
                        task2.getTasks().add(subTask);
                    }
                    task.getTasks().add(task2);
                }

                for (int n = 1; n < 15; n++) {
                    Task subTask = new Task("Задача " + i + "-" + k + "-" + n);
                    subTask.setDescription("Описание " + i + "-" + k + "-" + n);
                    subTask.setIsDone(n % 5 == 0);
                    subTask.setType(Task.TASK_TYPE_TASK);
                    subTask.setDate("01.01.2001");
                    subTask.setTime("11:11");
                    subTask.setPriority(random.nextInt(4));
                    subTask.setIsRepeat(false);
                    subTask.getTags().add(mTags.get(1).getID());
                    subTask.getTags().add(mTags.get(3).getID());
                    task.getTasks().add(subTask);
                }
                p.getTasks().add(task);
            }

            for (int k = 1; k < 15; k++) {
                Task t = new Task("Задача " + i + "-" + k);
                t.setDescription("Описание " + i + "-" + k);
                t.setIsDone(k % 5 == 0);
                t.setType(Task.TASK_TYPE_TASK);
                t.setDate("01.01.2001");
                t.setTime("11:11");
                t.setPriority(random.nextInt(4));
                t.setIsRepeat(false);
                t.getTags().add(mTags.get(3).getID());
                t.getTags().add(mTags.get(4).getID());
                p.getTasks().add(t);
            }
            mProjects.add(p);
        }
    }

    public ArrayList<Tag> getTags() {
        return mTags;
    }
}