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
import com.iamkatrechko.projectmanager.adapter.ProjectsListAdapter;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.ArrayList;

/**
 * Created by Muxa on 25.02.2016.
 */
public class ProjectsListFragment extends Fragment {

    private ArrayList<Project> projects;
    private ProjectsListAdapter adapter;
    private ProjectLab lab;

    public static ProjectsListFragment newInstance() {
        return new ProjectsListFragment();
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
        View v = inflater.inflate(R.layout.fragment_projects_list, parent, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplication()));

        projects = lab.getProjects();
        adapter = new ProjectsListAdapter();
        adapter.setOnItemClickListener(new ProjectsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Project project) {
                Intent intent = new Intent(getActivity(), ProjectEditActivity.class);
                intent.putExtra("mId", project.getID().toString());
                intent.putExtra("Operation", "edit");
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        AddFloatingActionButton button = (AddFloatingActionButton) v.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProjectEditActivity.class);
                intent.putExtra("mId", "0");
                intent.putExtra("Operation", "add");
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
