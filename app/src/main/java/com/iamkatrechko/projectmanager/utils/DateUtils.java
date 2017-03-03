package com.iamkatrechko.projectmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Утилитный класс для работы с датами
 * Created on 03.03.2017
 * author: ivanov_m
 */
public class DateUtils {

    /**
     * Возвращает текущую дату
     * @return Строка в формате "16.02.2017"
     */
    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(c.getTime());
    }

    /**
     * Возвращает завтрашнюю дату
     * @return Строка в формате "16.02.2017"
     */
    public static String getTomorrowDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(c.getTime());
    }
}
