package com.iamkatrechko.projectmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.iamkatrechko.projectmanager.entity.Task;

import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class SubProjectEditFragment extends Fragment implements View.OnClickListener{
    ProjectLab lab;

    EditText editTextTitle;
    EditText editTextDescription;

    private UUID ID;
    private String Operation;

    private Task task;

    public static SubProjectEditFragment newInstance(String ID, String operation, String parentID) {
        SubProjectEditFragment fragment = new SubProjectEditFragment();
        Bundle args = new Bundle();

        args.putString("mId", ID);
        args.putString("Operation", operation);
        args.putString("parentID", parentID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lab = ProjectLab.get(getActivity());

        String checkID = getArguments().getString("mId");
        if (!checkID.equals("0")) {
            ID = UUID.fromString(getArguments().getString("mId"));
        }
        Operation = getArguments().getString("Operation");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_task_edit, parent, false);

        editTextTitle = (EditText) v.findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) v.findViewById(R.id.editTextDescription);


        if (Operation.equals("edit")){
            getActivity().setTitle(R.string.activity_task_edit);
            task = lab.getTaskOnAllLevel(ID);
            editTextTitle.setText(task.getTitle());
            editTextDescription.setText(task.getDescription());
        }else{
            getActivity().setTitle(R.string.activity_task_add);
        }

        v.findViewById(R.id.buttonSave).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSave:
                if (Operation.equals("edit")) {
                    task.setTitle(editTextTitle.getText().toString());
                    task.setDescription(editTextDescription.getText().toString());
                    task.setType(Task.TASK_TYPE_SUB_PROJECT);
                }else{
                    UUID ID = UUID.fromString(getArguments().getString("parentID"));
                    Task task = new Task();
                    task.setTitle(editTextTitle.getText().toString());
                    task.setDescription(editTextDescription.getText().toString());
                    task.setType(Task.TASK_TYPE_SUB_PROJECT);
                    lab.getTasksListOnAllLevel(ID).add(lab.getLastTaskIndex(ID), task);
                }
                getActivity().finish();
                return;
        }
    }
}