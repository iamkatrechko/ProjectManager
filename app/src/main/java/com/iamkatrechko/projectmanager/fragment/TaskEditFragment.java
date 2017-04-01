package com.iamkatrechko.projectmanager.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.DialogChoiceDatesFragment;
import com.iamkatrechko.projectmanager.DialogSetTagsFragment;
import com.iamkatrechko.projectmanager.Methods;
import com.iamkatrechko.projectmanager.MyNotificationManager;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TaskEditFragment extends Fragment implements View.OnClickListener{
    Methods m;
    ProjectLab lab;
    MyNotificationManager myNotificationManager;

    private static int DIALOG_DATE_TIME_FRAGMENT = 175255;
    private static int DIALOG_SET_TAGS_FRAGMENT = 112521;

    EditText etTitle;
    EditText etDescription;
    TextView tvDateTime;
    TextView tvPriority;
    TextView tvRemind;
    TextView tvTag;

    private String[] priorities;
    private String[] notifys = {"Выключено", "Включено"};

    private UUID ID;
    private String Operation;
    private int tPriority = 0;
    private boolean tIsNotify = false;
    private ArrayList<UUID> tTagsList = new ArrayList<>();

    String date = "null";
    String time = "null";

    int year;
    int month;
    int day;

    int hour;
    int minute;

    private Task task;

    public static TaskEditFragment newInstance(String ID, String operation, String parentID) {
        TaskEditFragment fragment = new TaskEditFragment();

        Bundle args = new Bundle();
        args.putString("mId", ID);
        args.putString("Operation", operation);
        args.putString("parentID", parentID);
        fragment.setArguments(args);
        return fragment;
    }

    public void onActivityResult(int n, int n2, Intent intent) {
        if (n2 == 0 && n == DIALOG_DATE_TIME_FRAGMENT) {
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            setDateTime();
        }
        if (n2 == 0 && n == DIALOG_SET_TAGS_FRAGMENT){
            //Конвертируем айди меток к задаче в список с UUID
            ArrayList<UUID> newList = new ArrayList<>();
            String[] list = intent.getStringArrayExtra("TagsID");
            for (String s : list){
                newList.add(UUID.fromString(s));
            }
            tTagsList = newList;
            tvTag.setText(getTagsInString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        lab = ProjectLab.get(getActivity());
        m = new Methods(getActivity());
        myNotificationManager = new MyNotificationManager(getActivity());

        String checkID = getArguments().getString("mId");
        if (!checkID.equals("0")) {
            ID = UUID.fromString(getArguments().getString("mId"));
        }
        Operation = getArguments().getString("Operation");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subtask_edit, parent, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText) v.findViewById(R.id.editTextTitle);
        etDescription = (EditText) v.findViewById(R.id.editTextDescription);
        tvDateTime = (TextView) v.findViewById(R.id.textView_date_time);
        tvPriority = (TextView) v.findViewById(R.id.textView_priority);
        tvRemind = (TextView) v.findViewById(R.id.textViewReminder);
        tvTag = (TextView) v.findViewById(R.id.textViewTag);


        priorities = getActivity().getResources().getStringArray(R.array.priorities);
        if (Operation.equals("edit")){
            getActivity().setTitle(R.string.activity_subtask_edit);
            task = lab.getTaskOnAllLevel(ID);
            tPriority = task.getPriority();
            tIsNotify = task.getIsNotify();
            date = task.getDate();
            time = task.getTime();
            for (UUID id : task.getTags()){
                tTagsList.add(id);
            }
            setValues();
        }else{
            getActivity().setTitle(R.string.activity_subtask_add);
            setDefaultValues();
        }

        tvPriority.setText(priorities[tPriority]);

        v.findViewById(R.id.buttonSave).setOnClickListener(this);
        v.findViewById(R.id.linear_date_time).setOnClickListener(this);
        v.findViewById(R.id.linear_priority).setOnClickListener(this);
        v.findViewById(R.id.linear_reminder).setOnClickListener(this);
        v.findViewById(R.id.linear_tag).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSave:
                if (Operation.equals("edit")) {
                    task.setTitle(etTitle.getText().toString());
                    task.setDescription(etDescription.getText().toString());
                    task.setDate(date);
                    task.setTime(time);
                    task.setType(Task.TASK_TYPE_TASK);
                    task.setPriority(tPriority);
                    task.setIsNotify(tIsNotify);
                    task.setTags(tTagsList);

                    myNotificationManager.addNotification(task.getID());
                }else{
                    UUID ID = UUID.fromString(getArguments().getString("parentID"));
                    Task task = new Task(etTitle.getText().toString());
                    task.setDescription(etDescription.getText().toString());
                    task.setDate(date);
                    task.setTime(time);
                    task.setType(Task.TASK_TYPE_TASK);
                    task.setPriority(tPriority);
                    task.setIsNotify(tIsNotify);
                    task.setTags(tTagsList);
                    lab.getTasksListOnAllLevel(ID).add(task);

                    myNotificationManager.addNotification(task.getID());
                }
                getActivity().finish();
                break;
            case R.id.linear_priority:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Выберите приоритет")
                        .setSingleChoiceItems(priorities, tPriority,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int item) {
                                        tPriority = item;
                                        tvPriority.setText(priorities[item]);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.linear_reminder:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setTitle("Напоминание...")
                        .setSingleChoiceItems(notifys, tIsNotify ? 1 : 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int item) {
                                        tIsNotify = (item != 0);
                                        tvRemind.setText(notifys[item]);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
                break;
            case R.id.linear_date_time:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DialogChoiceDatesFragment fragmentDialog = DialogChoiceDatesFragment.newInstance(date, time);
                fragmentDialog.setTargetFragment(this, DIALOG_DATE_TIME_FRAGMENT);
                fragmentDialog.show(fragmentManager, "setDateTimeDialog");
                break;
            case R.id.linear_tag:
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                DialogSetTagsFragment fragmentDialog2 = DialogSetTagsFragment.newInstance(tTagsList);
                fragmentDialog2.setTargetFragment(this, DIALOG_SET_TAGS_FRAGMENT);
                fragmentDialog2.show(fragmentManager2, "setTagsDialog");
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_edit_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Удаление задачи
        if (id == R.id.delete) {
            if (Operation.equals("edit")){
                myNotificationManager.deleteNotification(task.getID());
                lab.removeTaskByID(task.getID());
                getActivity().finish();
            }else{
                getActivity().finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Методы //////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String getTagsInString(){
        String tagsString = "";
        for (int i = 0; i < tTagsList.size(); i++) {
            if (i == 0) {
                tagsString += lab.getTag(tTagsList.get(i)).getTitle();
            } else {
                tagsString += ", " + lab.getTag(tTagsList.get(i)).getTitle();
            }
        }
        if (tagsString.length() == 0){
            return "Не задано";
        }else {
            return tagsString;
        }
    }

    private void setDefaultValues(){
        tvDateTime.setText("Не задано");
        tvRemind.setText(notifys[0]);
        tvTag.setText(getTagsInString());
    }

    private void setValues(){
        etTitle.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        tvDateTime.setText(m.getFormatDateForSubTaskEdit(task.getDate(), task.getTime()));
        tvRemind.setText(notifys[tIsNotify ? 1 : 0]);
        tvTag.setText(getTagsInString());
    }

    private void setDateTime(){
        tvDateTime.setText(m.getFormatDateForSubTaskEdit(date, time));
    }
}