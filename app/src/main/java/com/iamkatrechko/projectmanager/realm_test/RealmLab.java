package com.iamkatrechko.projectmanager.realm_test;

import android.content.Context;

import io.realm.Realm;

/**
 * Created on 09.03.2017
 * author: iamkatrechko
 */
public class RealmLab {

    private Context mContext;
    private static RealmLab mRealmLab;
    private Realm mRealm;

    private RealmLab(Context context){
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
    }

    public static RealmLab get(Context context) {
        if (mRealmLab == null) {
            mRealmLab = new RealmLab(context);
        }
        return mRealmLab;
    }


    private void createDemoData() {

    }
}
