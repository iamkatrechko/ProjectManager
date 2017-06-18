package com.iamkatrechko.projectmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 10.04.2016.
 */
public class TasksListByTagFragment extends Fragment {

    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Идентификатор тега, по которому производится поиск */
    private UUID tagID;
    /** Виджет списка задач */
    private RecyclerView recyclerView;
    /** Список задач */
    private List<Task> mTasksList;
    /** Адаптер списка задач */
    private TasksListAdapter adapter;

    public static TasksListByTagFragment newInstance(UUID tagID) {
        TasksListByTagFragment fragment = new TasksListByTagFragment();

        Bundle args = new Bundle();
        args.putString("tagID", tagID.toString());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        tagID = UUID.fromString(getArguments().getString("tagID"));

        lab = ProjectLab.get(getActivity());
        mTasksList = lab.getTasksListByTag(tagID);
        adapter = new TasksListAdapter(getActivity());
        adapter.setData(mTasksList);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);
        v.findViewById(R.id.multiple_actions).setVisibility(View.GONE);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //resetAdapter();
    }

    /*private void resetAdapter(){
        mTasksList = lab.getTasksListByTag(tagID);
        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }*/
}
