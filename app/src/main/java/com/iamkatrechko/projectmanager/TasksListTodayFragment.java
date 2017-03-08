package com.iamkatrechko.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.utils.TasksUtils;

import java.util.List;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListTodayFragment extends Fragment {
    private List<Task> mTasksList;
    private TasksListAdapter adapter;
    private ProjectLab lab;
    private RecyclerView recyclerView;

    public static TasksListTodayFragment newInstance() {
        return new TasksListTodayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        lab = ProjectLab.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_calendar, parent, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        AddFloatingActionButton actionAdd = (AddFloatingActionButton) view.findViewById(R.id.action_add);

        mTasksList = lab.getTodayTasks();

        adapter = new TasksListAdapter(getActivity(), true, false,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);
        adapter.setEmptyListView(view.findViewById(R.id.emptyView));
        adapter.setData(TasksUtils.addDateLabels(mTasksList));
        recyclerView.setAdapter(adapter);

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