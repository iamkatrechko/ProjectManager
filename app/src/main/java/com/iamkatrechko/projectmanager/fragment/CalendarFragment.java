package com.iamkatrechko.projectmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.iamkatrechko.projectmanager.utils.DateUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Фрагмент просмотра задач на календаре
 * @author iamkatrechko
 *         Date: 09.03.2017
 */
public class CalendarFragment extends Fragment {

    /** Класс по работе с проектами и задачами */
    private ProjectLab mLab;
    /** Виджет списка задач */
    private RecyclerView mRecyclerView;
    /** Адаптер списка задач */
    private TasksListAdapter mAdapter;
    /** Список задач с временными метками */
    private List<? extends TaskListItem> mTasks = new ArrayList<>();
    /** Виджет календаря */
    private MaterialCalendarView mCalendarView;
    /** Текстовая метка с выбранной датой */
    private TextView textViewCurrentDate;
    /** Выбранная дата */
    private Calendar selectedDay = Calendar.getInstance();

    /**
     * Возвращает новый экземпляр фрагмента
     * @return новый экземпляр фрагмента
     */
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
        textViewCurrentDate = (TextView) view.findViewById(R.id.text_view_current_date);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCalendarView.setTopbarVisible(false);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                updateSelectedDayInfo(date.getCalendar());
                updateListForDay(date.getCalendar());
            }
        });

        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                updateMonthNameTitle(date.getCalendar());
            }
        });

        mAdapter = new TasksListAdapter(getActivity());

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setEmptyView(view.findViewById(R.id.emptyView));
        mAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int type, TaskListItem item) {
                if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
                    Task subProject = (Task) item;
                    Intent editIntent = TaskEditActivity.getEditActivityIntent(getContext(), subProject.getID());
                    startActivity(editIntent);
                    getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
                }
            }
        });

        mCalendarView.setCurrentDate(Calendar.getInstance().getTime());
        mCalendarView.setSelectedDate(Calendar.getInstance());
        updateMonthNameTitle(Calendar.getInstance());
        updateSelectedDayInfo(Calendar.getInstance());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListForDay(selectedDay);
    }

    /**
     * Обновляет список задач за выбранный день
     * @param calendar календарный день
     */
    private void updateListForDay(Calendar calendar) {
        mTasks = mLab.getTasksForDate(calendar);
        mAdapter.setData(mTasks);
        selectedDay = calendar;
    }

    /**
     * Обновляет заголовок с названием месяца
     * @param calendar календарный месяц
     */
    private void updateMonthNameTitle(Calendar calendar) {
        getActivity().setTitle(DateUtils.getMonthName(getActivity(), calendar) + " " + Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Обновляет информацию о выбранном дне
     * @param calendar календарный день
     */
    private void updateSelectedDayInfo(Calendar calendar) {
        textViewCurrentDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.getTimeInMillis()));
    }
}