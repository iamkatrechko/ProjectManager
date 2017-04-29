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
 * Фрагмент со списком задач с датированными метками
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class TasksListWithDatesFragment extends Fragment {

    /** Ключ параметра. Режим отображения задач */
    private static final String EXTRA_MODE = "MODE";

    /** Режимы отображения задач во фрагменте */
    public enum LIST_WITH_DATES_MODE {
        /** Список задач на сегодня */
        LIST_TODAY,
        /** Список задач на неделю */
        LIST_OF_WEEK
    }

    /** Текущий режим отображения задач */
    private LIST_WITH_DATES_MODE currentMode;
    /** Список задач с датированными метками */
    private List<TaskListItem> mTasksWithDates;
    /** Адаптер списка задач */
    private TasksListAdapter adapter;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Виджет списка задач */
    private RecyclerView recyclerView;
    /** Кнопка добавления новой задачи */
    private AddFloatingActionButton actionAdd;

    /**
     * Возвращает новый экземпляр фрагмента
     * @param mode режим отображения задач
     * @return новый экземпляр фрагмента
     */
    public static TasksListWithDatesFragment newInstance(LIST_WITH_DATES_MODE mode) {
        TasksListWithDatesFragment fragment = new TasksListWithDatesFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_MODE, mode.ordinal());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        lab = ProjectLab.get(getActivity());
        currentMode = LIST_WITH_DATES_MODE.values()[getArguments().getInt(EXTRA_MODE)];
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
        adapter.setData(mTasksWithDates);
        adapter.setOnSwipedListener(new SimpleItemTouchHelperCallback.OnItemSwipeListener() {

            @Override
            public void onItemLeftSwipe(int position) {

            }

            @Override
            public void onItemRightSwipe(int position) {
                lab.removeTaskByID(((Task) mTasksWithDates.get(position)).getID());
                updateTasksList();
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
        updateTasksList();
    }

    /** Обновлят список задач */
    private void updateTasksList() {
        switch (currentMode) {
            case LIST_TODAY:
                mTasksWithDates = TasksUtils.addDateLabels(lab.getTodayTasks());
                break;
            case LIST_OF_WEEK:
                mTasksWithDates = TasksUtils.addDateLabels(lab.getAllTasks());
                break;
        }
        adapter.setData(mTasksWithDates);
    }
}