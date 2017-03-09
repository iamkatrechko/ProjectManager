package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created on 09.03.2017
 * author: iamkatrechko
 */
public class MyLayoutManager extends LinearLayoutManager {


    public MyLayoutManager(Context context) {
        super(context);
    }

    /**
     * Найти первый полностью видимый элемент списка
     * @return первый полностью видимый элемент списка
     */
    public int findFirst() {
        return findFirstCompletelyVisibleItemPosition();
    }
}
