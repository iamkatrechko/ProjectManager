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
import com.iamkatrechko.projectmanager.MyNotificationManager;
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
import com.iamkatrechko.projectmanager.dialog.DialogChoiceDatesFragment;
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

    /** Ключ аргемента идентификатора проекта/подпроекта */
    private static final String EXTRA_PARENT_ID = "mId";

    /** Идентификатор окна голосового ввода */
    private static final int DIALOG_RESULT_SPEECH = 1;
    /** Идентификатор диалога удаления подпроекта */
    private static final int DIALOG_DELETE_SUB_PROJECT = 126512;
    /** Идентификатор диалога смены даты выполнения */
    private static final int DIALOG_CHANGE_DATE = 723242;

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
    private UUID parentId;
    /** Виджет списка узлов иерархии задачи */
    private RecyclerView mRecyclerViewHistory;
    /** Адаптер списка узлов иерархии задачи */
    private HistoryListAdapter mHistoryListAdapter;
    /** Менеджер уведомлений */
    private MyNotificationManager mMyNotificationManager;

    /**
     * Возвращает новый инстанс фрагмента
     * @param ID идентификатор открываемого подпроекта
     * @return новый инстанс фрагмента
     */
    public static TasksListFragment newInstance(UUID ID) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();

        args.putString(EXTRA_PARENT_ID, ID.toString());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mMyNotificationManager = new MyNotificationManager(getActivity());
        parentId = UUID.fromString(getArguments().getString(EXTRA_PARENT_ID));
        mProjectLab = ProjectLab.get(getActivity());
        mTasksList = mProjectLab.getTasksListOnAllLevel(parentId);
        mHistoryListAdapter = new HistoryListAdapter();
        adapter = new TasksListAdapter(getActivity(), true, true,
                getResources().getColor(R.color.swipe_to_change_date_color),
                getResources().getColor(R.color.swipe_to_set_done_color),
                R.drawable.ic_today_white_24dp,
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
                intent.putExtra(EXTRA_PARENT_ID, "0");
                intent.putExtra("parent_ID", parentId.toString());
                intent.putExtra("Operation", "add");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
            }
        });
        //Создание задачи
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = TaskEditActivity.getAddActivityIntent(getContext(), parentId);
                startActivity(addIntent);
                getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
            }
        });
        //Создание задачи
        fabAddTaskSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(getContext(), RecognizerActivity.class);
                startActivityForResult(speechIntent, DIALOG_RESULT_SPEECH);
            }
        });

        Log.d("TasksListFragment", "Уровень вхождения - " + mProjectLab.getLevelOfParent(parentId));
        if (mProjectLab.getLevelOfParent(parentId) == 2) {                                                         //Скрытие кнопки добавление подпроекта
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
                    intent.putExtra(EXTRA_PARENT_ID, subProject.getID().toString());
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
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                DialogChoiceDatesFragment dialogChoiceDatesFragment = DialogChoiceDatesFragment.newInstance(null, null, bundle);
                dialogChoiceDatesFragment.setTargetFragment(TasksListFragment.this, DIALOG_CHANGE_DATE);
                dialogChoiceDatesFragment.show(getFragmentManager(), DialogChoiceDatesFragment.class.getSimpleName());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onItemRightSwipe(int position) {
                UUID taskId = ((Task) mTasksList.get(position)).getID();
                mProjectLab.removeTaskByID(taskId);
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
                intent.putExtra(EXTRA_PARENT_ID, subProjectId.toString());
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
        mHistoryListAdapter.setData(mProjectLab.getHistoryList(parentId));

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
                startActivity(TasksDoneListActivity.getActivityIntent(getContext(), parentId));
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
        if (requestCode == DIALOG_RESULT_SPEECH) {
            if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
                String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT);
                if (!result.isEmpty()) {
                    Intent addIntent = TaskEditActivity.getAddActivityIntent(getContext(), parentId, result);
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
        if (resultCode == 0 && requestCode == DIALOG_CHANGE_DATE) {
            int pos = data.getIntExtra("pos", -1);
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            if (date != null) {
                mProjectLab.setTaskDate(((Task) mTasksList.get(pos)).getID(), date);
            }
            if (time != null) {
                mProjectLab.setTaskTime(((Task) mTasksList.get(pos)).getID(), time);
            }
            adapter.notifyItemChanged(pos);
        }
    }
}