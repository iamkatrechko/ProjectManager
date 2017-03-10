package com.iamkatrechko.projectmanager.realm_test.entity;

import io.realm.RealmObject;

/**
 * Created on 09.03.2017
 * author: iamkatrechko
 */
public class RealmTask extends RealmObject {

    private String mTitle;

    public RealmTask() {

    }

    public RealmTask(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
