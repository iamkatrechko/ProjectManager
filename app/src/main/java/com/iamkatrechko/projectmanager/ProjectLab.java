package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ProjectLab {
    private ArrayList<Project> mProjects;
    private ArrayList<Tag> mTags;
    private static ProjectLab sProjectLab;
    private Context mAppContext;
    private Methods m;
    ProjectsJSONSerializer projectsJSONSerializer;

    private ProjectLab(Context appContext) {
        mAppContext = appContext;
        m = new Methods(appContext);
        mProjects = new ArrayList<>();
        mTags = new ArrayList<>();
        generateTags();
        projectsJSONSerializer = ProjectsJSONSerializer.get(appContext);


        //loadProjectsFromJSON();
        //if (mProjects.size() == 0){
            generateTestData();
        //}
        //generateTestData();
    }

    public static ProjectLab get(Context c) {
        if (sProjectLab == null) {
            sProjectLab = new ProjectLab(c.getApplicationContext());
        }
        return sProjectLab;
    }

    public void loadProjectsFromJSON(){
        try {
            mProjects = projectsJSONSerializer.loadProjects();
        } catch (IOException | JSONException e) {
            Log.d("ProjectLab", "Ошибка загрузки");
            e.printStackTrace();
        }
    }

    public void saveProjectsIntoJSON(){
        try {
            projectsJSONSerializer.saveProjects(mProjects);
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
     * @param id ID искомого проекта
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
     * @param id ID искомого подпроекта/задачи
     * @return Подпроект/задача
     */
    public Task getTaskOnAllLevel(UUID id){
        for (Project p : mProjects) {
            for (Task t1 : p.getTasks()) {
                if (t1.getID().equals(id)) {
                    return t1;
                } else {
                    for (Task t2 : t1.getTasks()) {
                        if (t2.getID().equals(id)) {
                            return t2;
                        }else {
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
     * @param id ID проекта/подпроекта/задачи
     * @return Уровень (0 - 4)
     */
    public int getLevelOfParent(UUID id){
        //Уровни: 0 - проект; 1, 2, 3 - подпроекты
        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                return 0;                                                                                               //Уровень 0
            }else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(id)) {
                        return 1;                                                                                       //Уровень 1
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(id)) {
                                return 2;                                                                               //Уровень 2
                            }else{                                                                                      //Отключение создания подпроектов на уровне
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
     * @param parentID ID родителя (проекта/подпроекта)
     * @return Список задач
     */
    public List<Task> getTasksListOnAllLevel(UUID parentID){
        for (Project p : mProjects) {
            if (p.getID().equals(parentID)) {
                return p.getTasks();
            }else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(parentID)) {
                        return t1.getTasks();
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(parentID)) {
                                return t2.getTasks();
                            }else{
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
     * @param parentID ID родителя (проекта/подпроекта)
     * @return Позиция в списке
     */
    public int getLastTaskIndex(UUID parentID){
        List<Task> list = getTasksListOnAllLevel(parentID);
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getType().equals(Task.TASK_TYPE_TASK)){
                return i;
            }
        }
        return 0;
    }

    /**
     * Возвращает список задач на заданную дату
     * (с добавление метки-даты в на 1-ой позиции списка списка)
     * @param date строковая переменная типа "YYYY.MM.DD"
     * @return Список задач
     */
    public List<Task> getTodayTasksList(String date){
        List<Task> todayList = new ArrayList<Task>();
        Task taskDate = new Task();
        taskDate.setTitle("");
        taskDate.setDescription("");
        taskDate.setDate("Сегодня - " + date);
        taskDate.setTime("11:11");
        taskDate.setIsDone(true);
        taskDate.setType("date");
        todayList.add(taskDate);

        for (Project p : mProjects) {

            for (Task t1 : p.getTasks()) {
                if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                    if (t1.getDate().equals(date)) {
                        todayList.add(t1);
                    }
                }else {

                    for (Task t2 : t1.getTasks()) {
                        if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                            if (t2.getDate().equals(date)) {
                                todayList.add(t2);
                            }
                        }else {

                            for (Task t3 : t2.getTasks()) {
                                if (t3.getType().equals(Task.TASK_TYPE_TASK)) {
                                    if (t3.getDate().equals(date)) {
                                        todayList.add(t3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (todayList.size() == 1){
            todayList.get(0).setDate("Нет задач на сегодня");
        }

        return todayList;
    }

    /**
     * Возвращает список задач на ближайшую неделю
     * (с добавление меток-дат между задачами)
     * @return Список задач
     */
    public List<Task> getOfWeekTasksList(){
        ArrayList<Task> todayList = new ArrayList<Task>();

        String[] week = m.getWeekDate();
        int i = 0;
        for (String date : week) {
            i++;
            Task taskDate = new Task();
            taskDate.setTitle("");
            taskDate.setDescription("");
            if (i == 1){
                taskDate.setDate("Сегодня - " + m.getDate(date));
            }else if (i == 2){
                taskDate.setDate("Завтра - " + m.getDate(date));
            }else {
                taskDate.setDate(m.getDate(date));
            }
            taskDate.setTime("11:11");
            taskDate.setIsDone(true);
            taskDate.setType("date");
            todayList.add(taskDate);

            for (Project p : mProjects) {

                for (Task t1 : p.getTasks()) {
                    if (t1.getType().equals(Task.TASK_TYPE_TASK)) {
                        if (t1.getDate().equals(date)) {
                            todayList.add(t1);
                        }
                    }else {

                        for (Task t2 : t1.getTasks()) {
                            if (t2.getType().equals(Task.TASK_TYPE_TASK)) {
                                if (t2.getDate().equals(date)) {
                                    todayList.add(t2);
                                }
                            }else {

                                for (Task t3 : t2.getTasks()) {
                                    if (t3.getType().equals(Task.TASK_TYPE_TASK)) {
                                        if (t3.getDate().equals(date)) {
                                            todayList.add(t3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Если в этот день нет задач, удаляем элемент с датой
            if (todayList.get(todayList.size() - 1).getType().equals("date")){
                todayList.remove(todayList.size() - 1);
            }
        }

        //Если задач нет, оставляем метку об этом
        if (todayList.size() == 0){
            Task taskDate = new Task();
            taskDate.setTitle("");
            taskDate.setDescription("");
            taskDate.setDate("Нет задач");
            taskDate.setTime("11:11");
            taskDate.setIsDone(true);
            taskDate.setType("date");
            todayList.add(taskDate);
        }

        return todayList;
    }

    /**
     * Возвращает {@link Project}, в котором находится входная задача
     * @param id ID задачи
     * @return Проект
     */
    public Project getProjectOfTask(UUID id){
        for (Project p : mProjects) {
            if (p.getID().equals(id)) {
                return p;
            }else {
                for (Task t1 : p.getTasks()) {
                    if (t1.getID().equals(id)) {
                        return p;
                    } else {
                        for (Task t2 : t1.getTasks()) {
                            if (t2.getID().equals(id)) {
                                return p;
                            }else{
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
     * Полностью удаляет {@link Task} по его ID
     * @param id ID задачи для удаления
     */
    public void removeTaskByID(UUID id){
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
     * @param parentID ID проекта/подпроекта перемещаемой задачи
     */
    public void moveItem(UUID parentID, int fromPosition, int toPosition){
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
     * Возвращает ID родительского элемента
     * @param id ID подпроекта/задачи, родителя которого
     *           требуется найти
     * @return ID родителя
     */
    public UUID getParentIdOfTask(UUID id){
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
     * @param id ID проекта/подпроекта
     * @return Путь к проекту/подпроекту
     */
    public String getHistory(UUID id){
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
    public ArrayList<Task> getAllTasks(){
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
    public ArrayList<Task> getTasksPriority(int priority){
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
        ArrayList<Task> tasksList = new ArrayList<Task>();

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

    public Tag getTagByID(UUID tagID){
        for (Tag t : mTags){
            if (t.getID().equals(tagID)){
                return t;
            }
        }
        // Доделать удаление метки из задач, если ее нету
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void generateTags(){
        for (int i = 0; i < 6; i++){
            Tag t = new Tag();
            t.setTitle("Тэг #" + i);
            mTags.add(t);
        }
    }

    public void reloadData(){
        mProjects = new ArrayList<>();
        generateTestData();
    }

    public void deleteData(){
        mProjects = new ArrayList<>();
        Project p = new Project();
        p.setTitle("Общий список");
        p.setColor(mAppContext.getResources().getColor(R.color.actionBarColor1));
        mProjects.add(p);
    }

    public void generateTestData(){
        String[] projectNamesList = {"Общий список - ", "Личные цели - ", "Рабочий список - ", "Покупки - ", "Фильмы к просмотру", "Проект #"};
        Random random = new Random();

        for (int i = 1; i <= projectNamesList.length; i++) {
            Project p = new Project();
            p.setTitle(projectNamesList[i - 1] + i);
            int color = -random.nextInt(16777216) + 1;
            p.setColor(color);

            for (int k = 1; k < 4; k++) {
                Task task = new Task();
                task.setTitle("Подпроект " + i + "-" + k);
                task.setDescription("Описание " + i + "-" + k);
                task.setType(Task.TASK_TYPE_SUB_PROJECT);

                for (int m = 1; m < 4; m++) {
                    Task task2 = new Task();
                    task2.setTitle("Подподпроект " + i + "-" + m);
                    task2.setDescription("Описание " + i + "-" + m);
                    task2.setType(Task.TASK_TYPE_SUB_PROJECT);

                    for (int n = 1; n < 15; n++) {
                        Task subTask = new Task();
                        subTask.setTitle("Задача " + i + "-" + k + "-" + n);
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
                    Task subTask = new Task();
                    subTask.setTitle("Задача " + i + "-" + k + "-" + n);
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
                Task t = new Task();
                t.setTitle("Задача " + i + "-" + k);
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
