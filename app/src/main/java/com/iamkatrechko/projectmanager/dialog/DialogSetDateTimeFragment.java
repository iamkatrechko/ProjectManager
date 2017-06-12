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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.utils.DateUtils;

public class DialogSetDateTimeFragment extends DialogFragment {

    private String date;
    private String time;

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    public static DialogSetDateTimeFragment newInstance(String date, String time) {
        DialogSetDateTimeFragment fragment = new DialogSetDateTimeFragment();
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

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_time, null);
        final EditText editTextDate = (EditText) view.findViewById(R.id.editTextDate);
        final EditText editTextTime = (EditText) view.findViewById(R.id.editTextTime);
        final CheckBox checkBoxTime = (CheckBox) view.findViewById(R.id.checkBoxTime);

        if (date.equals("null")) {
            editTextDate.setText(DateUtils.getTodayDate());
            setBufDate(DateUtils.getTodayDate());
        } else {
            editTextDate.setText(date);
            setBufDate(date);
        }

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DatePickerDialog.OnDateSetListener kDatePickerListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year = i;
                                month = i1 + 1;
                                day = i2;
                                date = "" + String.format("%02d", day) + "." + String.format("%02d", month) + "." + year;
                                ((EditText) v).setText(date);
                            }
                        };
                DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), kDatePickerListener, year, month, day);
                dateDialog.show();
            }
        });

        if (time.equals("null")) {
            editTextTime.setText("12:00");
            setBufTime("12:00");
        } else {
            checkBoxTime.setChecked(true);
            editTextTime.setEnabled(true);
            editTextTime.setText(time);
            setBufTime(time);
        }
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TimePickerDialog.OnTimeSetListener kTimePickerListener =
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hour = i;
                                minute = i1;
                                String bTime = "" + String.format("%02d", hour) + ":" + String.format("%02d", minute);
                                ((EditText) v).setText(bTime);
                            }
                        };

                TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), kTimePickerListener, hour, minute, true);
                timeDialog.show();
            }
        });

        checkBoxTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextTime.setEnabled(isChecked);
                if (isChecked) {
                    TimePickerDialog.OnTimeSetListener kTimePickerListener =
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    hour = i;
                                    minute = i1;
                                    String bTime = "" + String.format("%02d", hour) + ":" + String.format("%02d", minute);
                                    editTextTime.setText(bTime);
                                }
                            };
                    TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), kTimePickerListener, hour, minute, true);
                    timeDialog.show();
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.set_time)
                .setPositiveButton(R.string.result_apply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int n) {
                        if (checkBoxTime.isChecked()) {
                            time = editTextTime.getText().toString();
                        } else {
                            time = "null";
                        }
                        date = editTextDate.getText().toString();
                        sendResult(date, time);
                    }
                }).setNegativeButton(R.string.without_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date = "null";
                        time = "null";
                        sendResult(date, time);
                    }
                }).create();
    }

    private void setBufDate(String date) {
        year = Integer.valueOf(date.split("\\.")[2]);
        month = Integer.valueOf(date.split("\\.")[1]) - 1;
        day = Integer.valueOf(date.split("\\.")[0]);
    }

    private void setBufTime(String time) {
        hour = Integer.valueOf(time.split("\\:")[0]);
        minute = Integer.valueOf(time.split("\\:")[1]);
    }
}

