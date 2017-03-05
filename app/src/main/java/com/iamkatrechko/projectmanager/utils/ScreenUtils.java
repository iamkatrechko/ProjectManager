package com.iamkatrechko.projectmanager.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Утилита для работы с экраном устройства
 * Created on 05.03.2017
 * author: ivanov_m
 */
public class ScreenUtils {

    /**
     * Переводит dp в пиксели
     * @param dp количество dp
     * @return количество пикселей
     */
    public static float getPXfromDP(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
