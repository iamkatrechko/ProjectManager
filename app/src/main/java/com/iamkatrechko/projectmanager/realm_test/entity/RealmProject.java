package com.iamkatrechko.projectmanager.realm_test.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created on 09.03.2017
 * author: iamkatrechko
 */
public class RealmProject extends RealmObject {

    private String mTitle;
    private RealmList<RealmTask> mRealmTasks = new RealmList<>();

    public RealmProject() {

    }

    public RealmProject(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public RealmList<RealmTask> getRealmTasks() {
        return mRealmTasks;
    }

    public void addTask(RealmTask task) {
        mRealmTasks.add(task);
    }
}
