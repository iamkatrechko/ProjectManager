package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.AbstractTaskObject;
import com.iamkatrechko.projectmanager.utils.DateUtils;
import com.iamkatrechko.projectmanager.utils.TasksUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListTodayFragment extends Fragment {
    static Methods m;
    List<Task> mTasksList;
    TasksListAdapter adapter;
    static ProjectLab lab;
    RecyclerView recyclerView;
    boolean needUpdate = true;                                                                      //Требуется ли полностью обновить (обработать) список задач
    private Paint p = new Paint();

    UUID ID;

    public static TasksListTodayFragment newInstance() {
        TasksListTodayFragment fragment = new TasksListTodayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        m = new Methods(getActivity());

        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_calendar, parent, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.section_list);

        AddFloatingActionButton actionAdd = (AddFloatingActionButton) view.findViewById(R.id.action_add);

        mTasksList = lab.getTodayTasks();
        needUpdate = false;//Одноразовое отключение повторной загрузки при старте в onResume

        adapter = new TasksListAdapter(getActivity(), true, false,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setData(TasksUtils.addDateLabels(mTasksList));

        // Создание задачи
        actionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                intent.putExtra("mId", "0");
                intent.putExtra("parent_ID", lab.getProjects().get(0).getID().toString());
                intent.putExtra("Operation", "add");
                startActivity(intent);
            }
        });
        return view;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        fullUpdateList();
    }

    //Полная перезагрузка списка задач со сменой дат и прочее
    private void fullUpdateList() {
        if (needUpdate) {
            mTasksList = lab.getTodayTasksList(DateUtils.getTodayDate());
            adapter = new a(mTasksList, getActivity());

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        needUpdate = true;
    }*/
}