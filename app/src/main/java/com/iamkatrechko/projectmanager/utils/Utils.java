package com.iamkatrechko.projectmanager.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

public class Utils {
    private static int sTheme;
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same sType.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
        //activity.overridePendingTransition(R.anim.act_alpha_left_in, R.anim.act_alpha_left_out);
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity, Integer id)
    {
        String pName = activity.getApplicationContext().getPackageName();
        Resources mRes = activity.getApplicationContext().getResources();
        sTheme = id;

        activity.setTheme(mRes.getIdentifier("AppTheme" + sTheme, "style", pName));
        //activity.setTheme(mRes.getIdentifier("AppTheme", "style", pName));
    }
}