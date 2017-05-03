package com.iamkatrechko.projectmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.activity.SubProjectEditActivity;
import com.iamkatrechko.projectmanager.activity.TaskEditActivity;
import com.iamkatrechko.projectmanager.activity.TasksDoneListActivity;
import com.iamkatrechko.projectmanager.activity.TasksListActivity;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.contract.OnItemClickListener;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Фрагмент со списком всех подпроектов и задач в ее иерархии
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class TasksListFragment extends Fragment {

    /** Список задач и подпроектов */
    private List<? extends TaskListItem> mTasksList = new ArrayList<>();
    /** Адаптер списка задач и подпроектов */
    private TasksListAdapter adapter;
    /** Класс по работе с проектами и задачами */
    private ProjectLab mProjectLab;
    /** Виджет списка подпроектов и задач */
    private RecyclerView mTasksListRecyclerView;
    /** Кнопка создания подпроектов и задач */
    private FloatingActionsMenu fMenu;
    /** Id текущего подпроекта или задачи */
    private UUID ID;

    /**
     * Возвращает новый инстанс фрагмента
     * @param ID id открываемого подпроекта
     * @return новый инстанс фрагмента
     */
    public static TasksListFragment newInstance(UUID ID) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();

        args.putString("mId", ID.toString());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        ID = UUID.fromString(getArguments().getString("mId"));
        mProjectLab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        mTasksListRecyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        fMenu = (FloatingActionsMenu) v.findViewById(R.id.multiple_actions);
        FloatingActionButton actionA = (FloatingActionButton) v.findViewById(R.id.action_a);
        FloatingActionButton actionB = (FloatingActionButton) v.findViewById(R.id.action_b);
        //Создание подпроекта
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SubProjectEditActivity.class);
                intent.putExtra("mId", "0");
                intent.putExtra("parent_ID", ID.toString());
                intent.putExtra("Operation", "add");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
            }
        });
        //Создание задачи
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                intent.putExtra("mId", "0");
                intent.putExtra("parent_ID", ID.toString());
                intent.putExtra("Operation", "add");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
            }
        });
        ((TextView) v.findViewById(R.id.textViewHistory)).setText(mProjectLab.getHistory(ID));
        if (mProjectLab.getLevelOfParent(ID) == 2) {                                                         //Скрытие кнопки добавление подпроекта
            fMenu.removeButton(actionA);
        }
        mTasksList = mProjectLab.getTasksListOnAllLevel(ID);

        adapter = new TasksListAdapter(getActivity(), true, true,
                getResources().getColor(R.color.swipe_to_set_done_color),
                getResources().getColor(R.color.swipe_to_delete_color),
                R.drawable.ic_done, R.drawable.ic_delete, true);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int type, TaskListItem item) {
                if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_SUB_PROJECT) {
                    //Если нажата "Задача" -> переходим дальше
                    Task subProject = (Task) item;
                    Intent intent = new Intent(getActivity(), TasksListActivity.class);
                    intent.putExtra("mId", subProject.getID().toString());
                    intent.putExtra("Type", subProject.getType());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.act_slide_left_in, R.anim.act_slide_left_out);
                } else if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
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
        adapter.setOnSwipedListener(new SimpleItemTouchHelperCallback.OnItemSwipeListener() {
            @Override
            public void onItemLeftSwipe(int position) {
                Log.d("setIsDone", String.valueOf(position) + " - " + ((Task) mTasksList.get(position)).getTitle());
                //myNotificationManager.deleteNotification(mTasks.get(position).getID());
                ((Task) mTasksList.get(position)).setIsDone(true);
                adapter.notifyItemRemoved(position);
                //notifyDataSetChanged();
            }

            @Override
            public void onItemRightSwipe(int position) {
                //myNotificationManager.deleteNotification(mTasks.get(position).getID());
                mProjectLab.removeTaskByID(((Task) mTasksList.get(position)).getID());
                adapter.notifyItemRemoved(position);
            }
        });

        mTasksListRecyclerView.setHasFixedSize(true);
        mTasksListRecyclerView.setAdapter(adapter);
        mTasksListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setData(mTasksList);

        Log.d("TasksListFragment", "Уровень вхождения - " + mProjectLab.getLevelOfParent(ID));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        fMenu.collapseImmediately();
        mProjectLab.saveProjectsIntoJSON();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                Intent intent = new Intent(getActivity(), TasksDoneListActivity.class);
                intent.putExtra("mId", ID.toString());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}