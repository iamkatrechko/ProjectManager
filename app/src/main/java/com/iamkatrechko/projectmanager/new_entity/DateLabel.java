package com.iamkatrechko.projectmanager.new_entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created on 05.03.2017
 * author: ivanov_m
 */
public class DateLabel extends AbstractTaskObject {

    private Calendar mCalendar;
    private String dateString;

    public DateLabel() {
        mCalendar = Calendar.getInstance();
    }

    @Override
    public int getViewType() {
        return 3;
    }

    public String getDate() {
        if (dateString == null) {
            return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mCalendar.getTimeInMillis());
        } else {
            return dateString;
        }
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public void setCalendarString(String string) {
        dateString = string;
    }
}
