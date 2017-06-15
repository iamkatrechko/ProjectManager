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
import android.widget.Toast;

import com.iamkatrechko.projectmanager.BuildConfig;
import com.iamkatrechko.projectmanager.Methods;
import com.iamkatrechko.projectmanager.MyNotificationManager;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.dialog.DialogChoiceDatesFragment;
import com.iamkatrechko.projectmanager.dialog.DialogSetTagsFragment;
import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.utils.Utils;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Фрагмент редактирования задачи
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class TaskEditFragment extends Fragment implements View.OnClickListener {

    private Methods m;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    private MyNotificationManager myNotificationManager;

    private static int DIALOG_DATE_TIME_FRAGMENT = 175255;
    private static int DIALOG_SET_TAGS_FRAGMENT = 112521;

    private EditText etTitle;
    private EditText etDescription;
    private TextView tvDateTime;
    private TextView tvPriority;
    private TextView tvRemind;
    private TextView tvTag;
    /** Текстовое поле с наименованием проекта задачи */
    private TextView textViewProject;

    private String[] priorities;

    private UUID ID;
    private String Operation;
    private int tPriority = 0;
    private boolean tIsNotify = false;
    private ArrayList<UUID> tTagsList = new ArrayList<>();

    private String date = "null";
    private String time = "null";

    private Task task;

    public static TaskEditFragment newInstance(String ID, String operation, String parentID) {
        return newInstance(ID, operation, parentID, null);
    }

    public static TaskEditFragment newInstance(String ID, String operation, String parentID, String taskTitle) {
        TaskEditFragment fragment = new TaskEditFragment();

        Bundle args = new Bundle();
        args.putString("mId", ID);
        args.putString("Operation", operation);
        args.putString("parentID", parentID);
        args.putString("TASK_TITLE", taskTitle);
        fragment.setArguments(args);

        return fragment;
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

        v.findViewById(R.id.linear_repeat).setVisibility(BuildConfig.isDeveloper ? View.VISIBLE : View.GONE);

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
        textViewProject = (TextView) v.findViewById(R.id.text_view_project);

        priorities = getActivity().getResources().getStringArray(R.array.priorities);
        if (Operation.equals("edit")) {
            getActivity().setTitle(R.string.activity_subtask_edit);
            task = lab.getTaskOnAllLevel(ID);
            tPriority = task.getPriority();
            tIsNotify = task.getIsNotify();
            date = task.getStringDate();
            time = task.getTime();
            for (UUID id : task.getTags()) {
                tTagsList.add(id);
            }
            setValues();
        } else {
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
        switch (view.getId()) {
            case R.id.buttonSave:
                String error = checkValues();
                if (error != null) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
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
                } else {
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
                builder.setTitle(R.string.change_priority)
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
                String[] remindValues = new String[]{getString(R.string.remind_off), getString(R.string.remind_on)};
                builder2.setTitle(R.string.change_notification)
                        .setSingleChoiceItems(remindValues, tIsNotify ? 1 : 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int item) {
                                        tIsNotify = (item != 0);
                                        tvRemind.setText(getString(item == 0 ? R.string.remind_off : R.string.remind_on));
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
                break;
            case R.id.linear_date_time:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DialogChoiceDatesFragment dialogChoiceDatesFragment = DialogChoiceDatesFragment.newInstance(date, time);
                dialogChoiceDatesFragment.setTargetFragment(this, DIALOG_DATE_TIME_FRAGMENT);
                dialogChoiceDatesFragment.show(fragmentManager, "DIALOG_DATE_TIME_FRAGMENT");
                break;
            case R.id.linear_tag:
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                DialogSetTagsFragment dialogSetTagsFragment = DialogSetTagsFragment.newInstance(tTagsList);
                dialogSetTagsFragment.setTargetFragment(this, DIALOG_SET_TAGS_FRAGMENT);
                dialogSetTagsFragment.show(fragmentManager2, "DIALOG_SET_TAGS_FRAGMENT");
                break;
        }
    }

    /**
     * Проверяет значения полей на корректность
     * @return текст ошибки или null - если ошибок нет
     */
    private String checkValues() {
        if (etTitle.getText().toString().isEmpty()) {
            return getString(R.string.error_enter_task_title);
        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (Operation.equals("edit")) {
                    lab.removeTaskByID(task.getID());
                    getActivity().finish();
                }
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int n, int n2, Intent intent) {
        if (n2 == 0 && n == DIALOG_DATE_TIME_FRAGMENT) {
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            setDateTime();
        }
        if (n2 == 0 && n == DIALOG_SET_TAGS_FRAGMENT) {
            //Конвертируем айди меток к задаче в список с UUID
            ArrayList<UUID> newList = new ArrayList<>();
            String[] list = intent.getStringArrayExtra("TagsID");
            for (String s : list) {
                newList.add(UUID.fromString(s));
            }
            tTagsList = newList;
            tvTag.setText(getTagsInString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Методы //////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Возвращает список тэгов в виде строки с перечислением
     * @return список тэгов в виде строки с перечислением
     */
    private String getTagsInString() {
        Tag[] tags = new Tag[tTagsList.size()];
        for (int i = 0; i < tags.length; i++) {
            tags[i] = lab.getTag(tTagsList.get(i));
        }
        String tagsString = Utils.concatToString(tags);
        if (tagsString.length() == 0) {
            return getString(R.string.not_specified);
        } else {
            return tagsString;
        }
    }

    /** Устанавливает значения по умолчанию для всех полей */
    private void setDefaultValues() {
        if (getArguments().getString("TASK_TITLE", null) != null) {
            etTitle.setText(getArguments().getString("TASK_TITLE", null));
        }
        tvDateTime.setText(getString(R.string.not_specified));
        tvRemind.setText(getString(R.string.remind_off));
        tvTag.setText(getTagsInString());
        Project project = null;
        if (task != null) {
            project = lab.getProjectOfTask(task.getID());
        }
        if (project == null) {
            project = lab.getProjectOfTask(UUID.fromString(getArguments().getString("parentID")));
        }
        textViewProject.setText((project).getTitle());
    }

    /** Устанавливает значения для всех полей по текущей задаче */
    private void setValues() {
        etTitle.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        tvDateTime.setText(m.getFormatDateForSubTaskEdit(task.getStringDate(), task.getTime()));
        tvRemind.setText(getString(!tIsNotify ? R.string.remind_off : R.string.remind_on));
        tvTag.setText(getTagsInString());
        textViewProject.setText(lab.getProjectOfTask(task.getID()).getTitle());
    }

    /** Устанавливает время в текстовое поле */
    private void setDateTime() {
        tvDateTime.setText(m.getFormatDateForSubTaskEdit(date, time));
    }
}