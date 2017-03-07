package com.iamkatrechko.projectmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksDoneListFragment extends Fragment{

    private List<Task> mTasksList;
    private TasksListAdapter adapter;
    private ProjectLab lab;
    private RecyclerView recyclerView;

    private UUID ID;

    public static TasksDoneListFragment newInstance(UUID ID) {
        TasksDoneListFragment fragment = new TasksDoneListFragment();
        Bundle args = new Bundle();

        args.putString("mId", ID.toString());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        lab = ProjectLab.get(getActivity());

        ID = UUID.fromString(getArguments().getString("mId"));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);

        //TODO считать все выполненные задачи
        mTasksList = lab.getTasksListOnAllLevel(ID);

        adapter = new TasksListAdapter(getActivity(), true, true,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);
        adapter.setTasksData(mTasksList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*mTasksList = lab.getTasksListOnAllLevel(mId);
        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}