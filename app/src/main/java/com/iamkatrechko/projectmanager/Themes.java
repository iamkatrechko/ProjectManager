package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Muxa on 12.09.2015.
 */
public class Themes {
    static SharedPreferences prefs;
    public static Themes themes;
    Context mContext;
    SharedPreferences.Editor prefsEditor;

    public Themes(Context context){
        mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getNumTheme(){
        return prefs.getString("sThemeNumber", "1");
    }

    public void setNumTheme(String number){
        prefsEditor = prefs.edit();
        prefsEditor.putString("sThemeNumber", number);
        prefsEditor.apply();
    }
}
