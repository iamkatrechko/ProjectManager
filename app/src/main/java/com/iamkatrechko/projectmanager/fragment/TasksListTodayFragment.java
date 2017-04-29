package com.iamkatrechko.projectmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.activity.TaskEditActivity;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;
import com.iamkatrechko.projectmanager.utils.TasksUtils;

import java.util.List;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListTodayFragment extends Fragment {

    private List<TaskListItem> mTaskListItems;
    private TasksListAdapter adapter;
    private ProjectLab lab;
    private RecyclerView recyclerView;
    private AddFloatingActionButton actionAdd;

    public static TasksListTodayFragment newInstance() {
        return new TasksListTodayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        lab = ProjectLab.get(getActivity());
        mTaskListItems = TasksUtils.addDateLabels(lab.getTodayTasks());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_list_today, parent, false);

        actionAdd = (AddFloatingActionButton) view.findViewById(R.id.action_add);
        recyclerView = (RecyclerView) view.findViewById(R.id.section_list);

        adapter = new TasksListAdapter(getActivity(), false, true,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(view.findViewById(R.id.emptyView));
        adapter.setData(mTaskListItems);
        adapter.setOnSwipedListener(new SimpleItemTouchHelperCallback.OnItemSwipeListener() {
            @Override
            public void onItemLeftSwipe(int position) {

            }

            @Override
            public void onItemRightSwipe(int position) {
                lab.removeTaskByID(((Task) mTaskListItems.get(position)).getID());
                mTaskListItems = TasksUtils.addDateLabels(lab.getTodayTasks());
                adapter.setData(mTaskListItems);
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        mTaskListItems = TasksUtils.addDateLabels(lab.getTodayTasks());
        adapter.setData(mTaskListItems);
    }
}