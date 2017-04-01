package com.iamkatrechko.projectmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamkatrechko.projectmanager.OnItemClickListener;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;
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
    private ProjectLab mLab;
    /** Виджет списка задач */
    private RecyclerView mRecyclerView;
    /** Адаптер списка задач */
    private TasksListAdapter mAdapter;
    /** Список задач с временными метками */
    private List<TaskListItem> mTasksWithDates = new ArrayList<>();
    /** Виджет календаря */
    private MaterialCalendarView mCalendarView;
    /** Кастомный лэйаут-менеджер, для получения позиции первого выделенного элемента */
    private LinearLayoutManager linearLayoutManager;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_calendar, parent, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.section_list);
        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mCalendarView.setTopbarVisible(false);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstVisiblePos != -1) {
                    RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(firstVisiblePos);
                    if (holder instanceof TasksListAdapter.ViewHolderDate) {
                        mCalendarView.setDateSelected(((TasksListAdapter.ViewHolderDate) holder).mCalendar, false);
                    }
                }
            }
        });

        List<Task> tasks = mLab.getAllTasks();
        mTasksWithDates = TasksUtils.addDateLabels(tasks);

        mAdapter = new TasksListAdapter(getActivity(), true, true,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, false);

        mAdapter.setData(mTasksWithDates);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int type, TaskListItem item) {
                mCalendarView.setTop(mCalendarView.getTop() - mCalendarView.getTouchables().get(0).getHeight());
                mRecyclerView.setTop(mRecyclerView.getTop() - mCalendarView.getTouchables().get(0).getHeight());
            }
        });

        return view;
    }
}