package com.iamkatrechko.projectmanager.fragment;

import android.app.Activity;
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
import com.iamkatrechko.projectmanager.activity.MainActivity;
import com.iamkatrechko.projectmanager.activity.ProjectEditActivity;
import com.iamkatrechko.projectmanager.adapter.ProjectsListAdapter;
import com.iamkatrechko.projectmanager.dialog.DialogDeleteProjectConfirm;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Фрагмент со списком проектов для их редактирования
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class ProjectsListFragment extends Fragment {

    private final static int DIALOG_DELETE_PROJECT = 124125;

    /** Список проектов */
    private ArrayList<Project> projects;
    /** Адаптер списка проектов */
    private ProjectsListAdapter adapter;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;

    /**
     * Возвращает новый экземпляр фрагмента
     * @return новый экземпляр фрагмента
     */
    public static ProjectsListFragment newInstance() {
        return new ProjectsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
                DialogDeleteProjectConfirm fragmentDialog = DialogDeleteProjectConfirm.newInstance(project.getID());
                fragmentDialog.setTargetFragment(ProjectsListFragment.this, DIALOG_DELETE_PROJECT);
                fragmentDialog.show(getActivity().getSupportFragmentManager(), "DIALOG_DELETE_PROJECT");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DIALOG_DELETE_PROJECT) {
            boolean delete = data.getBooleanExtra("delete", false);
            if (delete) {
                UUID projectId = (UUID) data.getSerializableExtra(DialogDeleteProjectConfirm.EXTRA_PROJECT_ID);
                adapter.notifyItemRemoved(lab.deleteProject(projectId));
                ((MainActivity) getActivity()).initMenuItems();
            }
        }
    }
}
