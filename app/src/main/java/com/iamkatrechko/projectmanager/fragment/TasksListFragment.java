package com.iamkatrechko.projectmanager.fragment;

import android.app.Activity;
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
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.activity.ServiceMenuActivity;
import com.iamkatrechko.projectmanager.activity.SubProjectEditActivity;
import com.iamkatrechko.projectmanager.activity.TaskEditActivity;
import com.iamkatrechko.projectmanager.activity.TasksDoneListActivity;
import com.iamkatrechko.projectmanager.activity.TasksListActivity;
import com.iamkatrechko.projectmanager.adapter.HistoryListAdapter;
import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.dialog.DialogDeleteSubProjectConfirm;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.interfce.OnItemClickListener;
import com.iamkatrechko.projectmanager.interfce.OnSubProjectOptionsClick;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.yandex.speechkit.gui.RecognizerActivity;

/**
 * Фрагмент со списком всех подпроектов и задач в ее иерархии
 * @author iamkatrechko
 *         Date: 25.02.2016
 */
public class TasksListFragment extends Fragment {

    /** Идентификатор окна голосового ввода */
    private static final int ACTIVITY_RESULT_SPEECH = 1;
    /** Идентификатор диалога удаления подпроекта */
    private static final int DIALOG_DELETE_SUB_PROJECT = 126512;

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
    /** Виджет списка узлов иерархии задачи */
    private RecyclerView mRecyclerViewHistory;
    /** Адаптер списка узлов иерархии задачи */
    private HistoryListAdapter mHistoryListAdapter;

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
        mTasksList = mProjectLab.getTasksListOnAllLevel(ID);
        mHistoryListAdapter = new HistoryListAdapter();
        adapter = new TasksListAdapter(getActivity(), false, true,
                getResources().getColor(R.color.swipe_to_delete_color),
                getResources().getColor(R.color.swipe_to_set_done_color),
                R.drawable.ic_delete,
                R.drawable.ic_done,
                true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        mTasksListRecyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        mRecyclerViewHistory = (RecyclerView) v.findViewById(R.id.recycler_view_history);
        fMenu = (FloatingActionsMenu) v.findViewById(R.id.multiple_actions);
        FloatingActionButton fabAddSubProject = (FloatingActionButton) v.findViewById(R.id.action_create_subproject);
        FloatingActionButton fabAddTask = (FloatingActionButton) v.findViewById(R.id.action_create_task);
        FloatingActionButton fabAddTaskSpeech = (FloatingActionButton) v.findViewById(R.id.action_create_task_speech);

        //Создание подпроекта
        fabAddSubProject.setOnClickListener(new View.OnClickListener() {
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
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = TaskEditActivity.getAddActivityIntent(getContext(), ID);
                startActivity(addIntent);
                getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
            }
        });
        //Создание задачи
        fabAddTaskSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(getContext(), RecognizerActivity.class);
                startActivityForResult(speechIntent, ACTIVITY_RESULT_SPEECH);
            }
        });

        Log.d("TasksListFragment", "Уровень вхождения - " + mProjectLab.getLevelOfParent(ID));
        if (mProjectLab.getLevelOfParent(ID) == 2) {                                                         //Скрытие кнопки добавление подпроекта
            fMenu.removeButton(fabAddSubProject);
        }

        adapter.setEmptyView(v.findViewById(R.id.emptyView));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int type, TaskListItem item) {
                if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_SUB_PROJECT) {
                    //Если нажат "Подпроект" -> переходим дальше
                    Task subProject = (Task) item;
                    Intent intent = new Intent(getActivity(), TasksListActivity.class);
                    intent.putExtra("mId", subProject.getID().toString());
                    intent.putExtra("Type", subProject.getType());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.act_slide_left_in, R.anim.act_slide_left_out);
                } else if (type == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
                    //Если нажата "задача" -> редактируем
                    Task subProject = (Task) item;
                    Intent editIntent = TaskEditActivity.getEditActivityIntent(getContext(), subProject.getID());
                    startActivity(editIntent);
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
        adapter.setOnSubProjectOptionsClick(new OnSubProjectOptionsClick() {
            @Override
            public void onDeleteClick(UUID subProjectId, int position) {
                DialogDeleteSubProjectConfirm fragmentDialog = DialogDeleteSubProjectConfirm.newInstance(subProjectId, position);
                fragmentDialog.setTargetFragment(TasksListFragment.this, DIALOG_DELETE_SUB_PROJECT);
                fragmentDialog.show(getActivity().getSupportFragmentManager(), null);
            }

            @Override
            public void onEditClick(UUID subProjectId, int position) {
                Intent intent = new Intent(getActivity(), SubProjectEditActivity.class);
                intent.putExtra("mId", subProjectId.toString());
                intent.putExtra("Operation", "edit");
                intent.putExtra("parent_ID", "0");
                getActivity().startActivity(intent);
            }
        });

        mTasksListRecyclerView.setHasFixedSize(true);
        mTasksListRecyclerView.setAdapter(adapter);
        mTasksListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setData(mTasksList);

        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setAdapter(mHistoryListAdapter);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mHistoryListAdapter.setData(mProjectLab.getHistoryList(ID));

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
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_tasks:
                startActivity(TasksDoneListActivity.getActivityIntent(getContext(), ID));
                return true;
            case R.id.developer_menu:
                startActivity(new Intent(getActivity(), ServiceMenuActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SPEECH) {
            if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
                String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT);
                if (!result.isEmpty()) {
                    Intent addIntent = TaskEditActivity.getAddActivityIntent(getContext(), ID, result);
                    startActivity(addIntent);
                    getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
                }
            } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
                String error = ((ru.yandex.speechkit.Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR)).getString();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DIALOG_DELETE_SUB_PROJECT) {
            boolean delete = data.getBooleanExtra("delete", false);
            if (delete) {
                UUID subProjectId = (UUID) data.getSerializableExtra(DialogDeleteSubProjectConfirm.EXTRA_SUB_PROJECT_ID);
                int subProjectPosition = data.getIntExtra(DialogDeleteSubProjectConfirm.EXTRA_SUB_PROJECT_POSITION, -1);
                mProjectLab.removeTaskByID(subProjectId);
                adapter.notifyItemRemoved(subProjectPosition);
            }
        }
    }
}