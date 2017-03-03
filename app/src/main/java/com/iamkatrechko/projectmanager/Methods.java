package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;

import com.iamkatrechko.projectmanager.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Methods {
    private Context mContext;

    public Methods(Context context) {
        mContext = context;
    }

    /**
     * Возвращает полученные дату и время в формате "DD.MM.YYYY - HH.MM"
     * @param date Дата
     * @param time Время
     * @return Строка в формате "DD.MM.YYYY - HH.MM"
     */
    public String getFormatDate(String date, String time) {
        return getFormatDate2(date, time);
        /*String formatDate;

        if (date.equals(getTodayDate())){
            if (time.equals("null")){
                formatDate = "Сегодня";
            }else {
                formatDate = "Сегодня - " + time;
            }
        }else if (date.equals("null")){
            formatDate = "";
        }else{
            if (time.equals("null")){
                formatDate = date;
            }else{
                formatDate = date + " - " + time;
            }
        }

        return formatDate;*/
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

    public String getFormatDate2(String date, String time) {
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

    public String getDate(String date) {
        return setBufDate(date);
    }

    private String setBufDate(String date) {
        String[] months = mContext.getResources().getStringArray(R.array.months);
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

    /**
     * Возвращает массив с датами следующих 7-ми дней, в т.ч. сегодня
     * @return Массив строк в формате "YYYY.MM.DD"
     */
    public String[] getWeekDate() {
        String[] result = new String[7];

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(c.getTime());
            result[i] = date;
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        return result;
    }

    /**
     * Переводит DP в пиксели
     * @param dp Количество DP
     * @return Количество пикселей
     */
    public float getPXfromDP(int dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}