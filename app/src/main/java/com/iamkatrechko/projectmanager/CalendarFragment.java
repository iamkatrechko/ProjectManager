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
import com.iamkatrechko.projectmanager.new_entity.AbstractTaskObject;
import com.iamkatrechko.projectmanager.utils.TasksUtils;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09.03.2017
 * author: iamkatrechko
 */
public class CalendarFragment extends Fragment {

    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Виджет списка задач */
    private RecyclerView recyclerView;
    /** Адаптер списка задач */
    private TasksListAdapter adapter;
    /** Список задач с временными метками */
    private List<AbstractTaskObject> mTasksWithDates = new ArrayList<>();
    /** Виджет календаря */
    private MaterialCalendarView mCalendarView;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, parent, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.section_list);
        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCalendarView.setTopbarVisible(false);

        List<Task> tasks = lab.getAllTasks();
        mTasksWithDates = TasksUtils.addDateLabels(tasks);

        adapter = new TasksListAdapter(getActivity(), true, true,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);

        adapter.setData(mTasksWithDates);
        recyclerView.setAdapter(adapter);

        return view;
    }
}