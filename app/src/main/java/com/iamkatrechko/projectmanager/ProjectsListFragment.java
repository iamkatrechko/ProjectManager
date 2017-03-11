package com.iamkatrechko.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.adapter.ProjectsListAdapter;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Фрагмент со списком проектов для их редактирования
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class ProjectsListFragment extends Fragment {

    /** Список проектов */
    private ArrayList<Project> projects;
    /** Адаптер списка проектов */
    private ProjectsListAdapter adapter;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;

    public static ProjectsListFragment newInstance() {
        return new ProjectsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);

        lab = ProjectLab.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects_list, parent, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

            @Override
            public void onDeleteClick(Project project) {
                Toast.makeText(getActivity(), "Удаление не реализовано", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setData(projects);
        recyclerView.setAdapter(adapter);

        AddFloatingActionButton button = (AddFloatingActionButton) view.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProjectEditActivity.class);
                intent.putExtra("mId", "0");
                intent.putExtra("Operation", "add");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
