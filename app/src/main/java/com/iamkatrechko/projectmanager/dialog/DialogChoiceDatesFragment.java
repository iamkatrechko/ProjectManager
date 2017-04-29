/*
 * Decompiled with CFR 0_92.
 * 
 * Could not load the following classes:
 *  android.app.AlertDialog
 *  android.app.AlertDialog$Builder
 *  android.app.Dialog
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.Intent
 *  android.database.Cursor
 *  android.os.Bundle
 *  android.support.v4.app.DialogFragment
 *  android.support.v4.app.Fragment
 *  android.support.v4.app.FragmentActivity
 *  android.support.v4.content.CursorLoader
 *  android.support.v4.widget.CursorAdapter
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.widget.CheckBox
 *  android.widget.ListAdapter
 *  android.widget.ListView
 *  android.widget.TextView
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 */
package com.iamkatrechko.projectmanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.iamkatrechko.projectmanager.utils.DateUtils;

public class DialogChoiceDatesFragment extends DialogFragment {

    private String date;
    private String time;

    /**
     * Возвращает новый экземпляр фрагмента
     * @param date начальная дата
     * @param time начальное время
     * @return новый экземпляр фрагмента
     */
    public static DialogChoiceDatesFragment newInstance(String date, String time) {
        DialogChoiceDatesFragment fragment = new DialogChoiceDatesFragment();
        Bundle args = new Bundle();

        args.putString("date", date);
        args.putString("time", time);

        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(String date, String time) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("date", date);
        a.putExtra("time", time);
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        date = getArguments().getString("date");
        time = getArguments().getString("time");

        final String[] mItemsNames ={"Сегодня", "Завтра", "Установить вручную...", "Без срока"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(mItemsNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item){
                    case 0:
                        sendResult(DateUtils.getTodayDate(), "null");
                        break;
                    case 1:
                        sendResult(DateUtils.getTomorrowDate(), "null");
                        break;
                    case 2:
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        DialogSetDateTimeFragment fragmentDialog = DialogSetDateTimeFragment.newInstance(date, time);
                        fragmentDialog.setTargetFragment(getTargetFragment(), getTargetRequestCode());
                        fragmentDialog.show(fragmentManager, "setDateTime");
                        getDialog().cancel();
                        break;
                    case 3:
                        sendResult("null", "null");
                        break;
                }
            }
        });
        return builder.create();
    }
}

