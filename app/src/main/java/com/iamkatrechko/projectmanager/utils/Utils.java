package com.iamkatrechko.projectmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.iamkatrechko.projectmanager.R;

public class Utils {

    private static int sTheme;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same sType.
     */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
        //activity.overridePendingTransition(R.anim.act_alpha_left_in, R.anim.act_alpha_left_out);
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity, Integer id) {
        String pName = activity.getApplicationContext().getPackageName();
        Resources mRes = activity.getApplicationContext().getResources();
        sTheme = id;

        activity.setTheme(mRes.getIdentifier("AppTheme" + sTheme, "style", pName));
        //activity.setTheme(mRes.getIdentifier("AppTheme", "style", pName));
    }

    /**
     * Соединяет массив объектов в одну строку через запятые
     * @param objects массив объектов
     * @return массив объектов в одну строку через запятые
     */
    public static String concatToString(@NonNull Object[] objects) {
        String result = "";
        for (int i = 0; i < objects.length; i++) {
            if (i == (objects.length - 1)) {
                result += objects[i].toString();
            } else {
                result += objects[i].toString() + ", ";
            }
        }
        return result;
    }

    /**
     * Открывает страницу приложения в Google Play
     * @param context контекст
     */
    public static void openGooglePlayApplication(Context context) {
        final String appName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }

    /**
     * Открывает страницу разработчика в Google Play
     * @param context контекст
     */
    public static void openGooglePlayDeveloper(Context context) {
        final String appName = "I'm Katrechko";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=" + appName)));
        }
    }

    /**
     * Открывает окно отправки сообщения разработчику
     * @param context контекст
     * @param text    текст сообщения
     */
    public static void sendMail(Context context, String text) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // Тип сообщения
        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"iamkatrechko@gmail.com"});
        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(emailIntent, context.getResources().getText(R.string.select_app_for_send)));
    }
}