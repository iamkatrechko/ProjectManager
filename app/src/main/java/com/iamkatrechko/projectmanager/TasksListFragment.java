package com.iamkatrechko.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListFragment extends Fragment {
    Methods m;

    List<Task> mTasksList;
    public TasksListAdapter adapter;
    public ProjectLab lab;
    private RecyclerView mTasksListRecyclerView;
    private ItemTouchHelper mItemTouchHelper;

    FloatingActionsMenu fMenu;

    UUID ID;
    String Type;

    public static TasksListFragment newInstance(UUID ID, String Type) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();

        args.putString("mId", ID.toString());
        args.putString("Type", Type);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        m = new Methods(getActivity());

        ID = UUID.fromString(getArguments().getString("mId"));
        Type = getArguments().getString("Type");

        lab = ProjectLab.get(getActivity());
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
        ((TextView) v.findViewById(R.id.textViewHistory)).setText(lab.getHistory(ID));
        if (lab.getLevelOfParent(ID) == 2) {                                                         //Скрытие кнопки добавление подпроекта
            fMenu.removeButton(actionA);
        }

        mTasksList = lab.getTasksListOnAllLevel(ID);

        adapter = new TasksListAdapter(mTasksList, getActivity(), ID);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicker(String type, UUID id) {
                if (type.equals(Task.TASK_TYPE_SUB_PROJECT)) {
                    //Если нажата "Задача" -> переходим дальше
                    Intent intent = new Intent(getActivity(), TasksListActivity.class);
                    intent.putExtra("mId", id.toString());
                    intent.putExtra("Type", type);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.act_slide_left_in, R.anim.act_slide_left_out);
                } else {
                    //Если нажата "Подзадача" -> редактируем
                    Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                    intent.putExtra("mId", id.toString());
                    intent.putExtra("Operation", "edit");
                    intent.putExtra("parent_ID", "0");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
                }
            }
        });
        mTasksListRecyclerView.setHasFixedSize(true);
        mTasksListRecyclerView.setAdapter(adapter);
        mTasksListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Инициализация свайпов
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mTasksListRecyclerView);
        ///////////////////////

        Log.d("TasksListFragment", "Уровень вхождения - " + lab.getLevelOfParent(ID));
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
        fMenu.collapseImmediately();
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
            intent.putExtra("mId", ID.toString());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}