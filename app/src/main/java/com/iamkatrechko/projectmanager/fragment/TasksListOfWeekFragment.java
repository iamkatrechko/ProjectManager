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
import com.iamkatrechko.projectmanager.OnItemClickListener;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.activity.TaskEditActivity;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;
import com.iamkatrechko.projectmanager.utils.TasksUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListOfWeekFragment extends Fragment {

    private ProjectLab lab;

    boolean needUpdate = true;                                                                      //Требуется ли полностью обновить (обработать) список задач

    /** Виджет списка задач */
    private RecyclerView recyclerView;
    /** Адаптер списка задач */
    private TasksListAdapter adapter;
    /** Список задач с временными метками */
    private List<TaskListItem> mTasksWithDates = new ArrayList<>();

    public static TasksListOfWeekFragment newInstance() {
        return new TasksListOfWeekFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recycler_calendar, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        AddFloatingActionButton actionAdd = (AddFloatingActionButton) v.findViewById(R.id.action_add);

        needUpdate = false;                                                                         //Одноразовое отключение повторной загрузки при старте в onResume

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new TasksListAdapter(getActivity(), true, false,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);

        adapter.setOnSwipedListener(new SimpleItemTouchHelperCallback.OnItemSwipeListener() {
            @Override
            public void onItemLeftSwipe(int position) {
                if (mTasksWithDates.get(position) instanceof Task) {
                    Task task = (Task) mTasksWithDates.get(position);
                    lab.removeTaskByID(task.getID());
                    mTasksWithDates.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onItemRightSwipe(int position) {

            }
        });
        List<Task> tasks = lab.getAllTasks();
        mTasksWithDates = TasksUtils.addDateLabels(tasks);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int type, TaskListItem item) {
                if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
                    Task task = (Task) item;
                    Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                    intent.putExtra("mId", task.getID().toString());
                    intent.putExtra("Operation", "edit");
                    intent.putExtra("parent_ID", "0");
                    getActivity().startActivity(intent);
                }
            }
        });

        adapter.setData(mTasksWithDates);
        recyclerView.setAdapter(adapter);

        //Создание задачи
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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //fullUpdateList();
    }

    //Полная перезагрузка списка задач со сменой дат и прочее
    /*public void fullUpdateList() {
        if (needUpdate) {
            mTasksList = lab.getOfWeekTasksList();
            adapter = new TasksAdapter(mTasksList, getActivity());

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        needUpdate = true;
    }*/
}