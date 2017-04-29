package com.iamkatrechko.projectmanager;

import android.content.Context;
import com.iamkatrechko.projectmanager.utils.DateUtils;

public class Methods {

    private Context mContext;

    public Methods(Context context) {
        mContext = context;
    }

    /**
     * Возвращает полученные дату и время в формате "DD.MM.YYYY - HH.MM".
     * Если дата и время не заданы, вовзращает строку - "Не задано"
     * @param date Дата
     * @param time Время
     * @return Строка в формате "DD.MM.YYYY - HH.MM"
     */
    public String getFormatDateForSubTaskEdit(String date, String time) {
        String formatDate;

        if (date.equals("null") && time.equals("null")) {
            formatDate = "Не задано";
        } else {
            formatDate = getFormatDate(date, time);
        }

        return formatDate;
    }

    /**
     * Возвращает полученные дату и время в формате "DD.MM.YYYY - HH.MM"
     * @param date Дата
     * @param time Время
     * @return Строка в формате "DD.MM.YYYY - HH.MM"
     */
    public String getFormatDate(String date, String time) {
        String formatDate;

        if (date.equals(DateUtils.getTodayDate())) {                                                          //Если дата = сегодня

            if (time.equals("null")) {
                formatDate = "Сегодня";
            } else {
                formatDate = "Сегодня - " + time;
            }

        } else if (date.equals(DateUtils.getTomorrowDate())) {                                                //Если дата = завтра

            if (time.equals("null")) {
                formatDate = "Завтра";
            } else {
                formatDate = "Завтра - " + time;
            }

        } else if (date.equals("null")) {                                                           //Если дата не стоит
            formatDate = "";

        } else {                                                                                    //Если дата стоит, но не сегодня и не завтра

            if (time.equals("null")) {
                formatDate = setBufDate(date);
            } else {
                formatDate = setBufDate(date) + " - " + time;
            }
        }

        return formatDate;
    }

    private String setBufDate(String date) {
        String[] months = mContext.getResources().getStringArray(R.array.months_names);
        int year = Integer.valueOf(date.split("\\.")[2]);
        int month = Integer.valueOf(date.split("\\.")[1]) - 1;
        int day = Integer.valueOf(date.split("\\.")[0]);

        String newDate = "" + day + " " + months[month];

        //Если год не совпадает с текущим
        if (Integer.valueOf(DateUtils.getTodayDate().split("\\.")[2]) != year) {
            newDate += " " + year;
        }

        return newDate;
    }
}