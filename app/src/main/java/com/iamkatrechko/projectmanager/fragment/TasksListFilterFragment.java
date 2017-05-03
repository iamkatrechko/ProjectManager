package com.iamkatrechko.projectmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.activity.TaskEditActivity;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.contract.OnItemClickListener;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;

import java.util.List;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListFilterFragment extends Fragment {

    private List<Task> mTasksList;
    private TasksListAdapter adapter;
    private ProjectLab lab;
    private RecyclerView recyclerView;

    private int filterType = 0;

    public static TasksListFilterFragment newInstance(int filterType) {
        TasksListFilterFragment fragment = new TasksListFilterFragment();

        Bundle args = new Bundle();
        args.putInt("filterType", filterType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        lab = ProjectLab.get(getActivity());
        filterType = getArguments().getInt("filterType");
        adapter = new TasksListAdapter(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);

        v.findViewById(R.id.multiple_actions).setVisibility(View.GONE);

        switch (filterType) {
            case 0:
                mTasksList = lab.getAllTasks();
                break;
            case 1:
                mTasksList = lab.getTasksPriority(0);
                break;
            case 2:
                mTasksList = lab.getTasksPriority(1);
                break;
            case 3:
                mTasksList = lab.getTasksPriority(2);
                break;
            case 4:
                mTasksList = lab.getTasksPriority(3);
                break;
        }
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int type, TaskListItem item) {
                if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
                    //Если нажата "Подзадача" -> редактируем
                    Task subProject = (Task) item;
                    Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                    intent.putExtra("mId", subProject.getID().toString());
                    intent.putExtra("Operation", "edit");
                    intent.putExtra("parent_ID", "0");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
                }
            }
        });

        adapter.setEmptyView(v.findViewById(R.id.emptyView));
        ((TextView) v.findViewById(R.id.empty_view_text)).setText(R.string.empty_tasks_list_select_filter);
        adapter.setData(mTasksList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}