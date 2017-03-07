package com.iamkatrechko.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;

/**
 * Created by Muxa on 25.02.2016.
 */
public class FilterTasksListFragment extends Fragment {

    private List<Task> mTasksList;
    private TasksListAdapter adapter;
    private ProjectLab lab;
    private RecyclerView recyclerView;

    private FloatingActionsMenu fMenu;

    int filterType = 0;

    public static FilterTasksListFragment newInstance(int filterType) {
        FilterTasksListFragment fragment = new FilterTasksListFragment();

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

        //mId = UUID.fromString(getArguments().getString("mId"));
        //Type = getArguments().getString("Type");
        filterType = getArguments().getInt("filterType");

        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);
        fMenu = (FloatingActionsMenu) v.findViewById(R.id.multiple_actions);
        fMenu.setVisibility(View.GONE);

        v.findViewById(R.id.textViewHistory).setVisibility(View.GONE);
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
        //fMenu.collapseImmediately();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            Intent intent = new Intent(getActivity(), TasksDoneListActivity.class);
            //intent.putExtra("mId", mId.toString());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}