package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksListOfWeekFragment extends Fragment {
    Methods m;
    List<Task> mTasksList;
    TasksAdapter adapter;
    ProjectLab lab;

    boolean needUpdate = true;                                                                      //Требуется ли полностью обновить (обработать) список задач
    RecyclerView recyclerView;

    UUID ID;

    public static TasksListOfWeekFragment newInstance() {
        return new TasksListOfWeekFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        m = new Methods(getActivity());
        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recycler_calendar, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        AddFloatingActionButton actionAdd = (AddFloatingActionButton) v.findViewById(R.id.action_add);

        mTasksList = lab.getOfWeekTasksList();
        needUpdate = false;                                                                         //Одноразовое отключение повторной загрузки при старте в onResume

        adapter = new TasksAdapter(mTasksList, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initSwipe();

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
        fullUpdateList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
        private Context mContext;
        private List<Task> mTasks;
        private String[] colors;

        public TasksAdapter(List<Task> tasks, Context context) {
            mTasks = tasks;
            mContext = context;
            colors = context.getResources().getStringArray(R.array.priorities_colors);
        }

        @Override
        public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View recyclerView = inflater.inflate(R.layout.recycler_task_item_calendar, parent, false);

            return new ViewHolder(recyclerView);
        }

        public void removeItem(int position) {
            lab.removeTaskByID(mTasks.get(position).getID());
            mTasks = lab.getOfWeekTasksList();
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(TasksAdapter.ViewHolder vHolder, int position) {
            Task task = mTasks.get(position);
            vHolder._id = task.getID();
            vHolder.sType = task.getType();

            vHolder.tvTitle.setText(task.getTitle());

            if (!task.getTime().equals("null")) {
                vHolder.tvDescription.setText(task.getTime());
            } else {
                vHolder.tvDescription.setText("");
            }

            vHolder.flPriority.setBackgroundColor(Color.parseColor(colors[task.getPriority()]));

            if (task.getType().equals("date")) {
                vHolder.tvDateOfTasks.setVisibility(View.VISIBLE);
                vHolder.tvDateOfTasks.setText(task.getDate());
                vHolder.cardView.setVisibility(View.GONE);
            }
            if (task.getType().equals(Task.TASK_TYPE_TASK)) {
                vHolder.tvDateOfTasks.setVisibility(View.GONE);
                vHolder.cardView.setVisibility(View.VISIBLE);
            }

            Project project = lab.getProjectOfTask(task.getID());
            vHolder.tvProjectName.setText(project.getTitle());
            vHolder.ivProjectColor.setColorFilter(project.getColor());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public UUID _id;
            public String sType;
            public TextView tvTitle;
            public TextView tvDescription;
            public TextView tvDateOfTasks;
            public View cardView;
            public FrameLayout flPriority;                                                          //Полоса с цветом приоритета
            public TextView tvProjectName;
            public ImageView ivProjectColor;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTitle = (TextView) itemView.findViewById(R.id.title);
                tvDescription = (TextView) itemView.findViewById(R.id.description);
                tvDateOfTasks = (TextView) itemView.findViewById(R.id.textView2);
                cardView = itemView.findViewById(R.id.card_view);
                flPriority = (FrameLayout) itemView.findViewById(R.id.priority_color);
                tvProjectName = (TextView) itemView.findViewById(R.id.textViewProjectName);
                ivProjectColor = (ImageView) itemView.findViewById(R.id.imageViewProjectColor);

                itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sType.equals("date")) {
                            return;
                        }
                        Intent intent = new Intent(mContext, TaskEditActivity.class);
                        intent.putExtra("mId", _id.toString());
                        intent.putExtra("Operation", "edit");
                        intent.putExtra("parent_ID", "0");
                        mContext.startActivity(intent);
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sType.equals("date")) {
                            return;
                        }
                        Intent intent = new Intent(mContext, TaskEditActivity.class);
                        intent.putExtra("mId", _id.toString());
                        intent.putExtra("Operation", "edit");
                        intent.putExtra("parent_ID", "0");
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }
    }

    //Полная перезагрузка списка задач со сменой дат и прочее
    public void fullUpdateList() {
        if (needUpdate) {
            mTasksList = lab.getOfWeekTasksList();
            adapter = new TasksAdapter(mTasksList, getActivity());

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        needUpdate = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private Paint p = new Paint();

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (mTasksList.get(position).getType().equals("date")) {
                    return;
                }

                if (direction == ItemTouchHelper.LEFT) {
                    Log.d("onSwiped", "LEFT");
                    adapter.removeItem(position);
                } else {
                    Log.d("onSwiped", "RIGHT");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                //Полоса приоритета скрыта => подпроект => запретить свайп
                if (viewHolder.itemView.findViewById(R.id.card_view).getVisibility() == View.GONE) {
                    return;
                }

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        //Resources resources = getActivity().getResources();
                        //DisplayMetrics metrics = resources.getDisplayMetrics();
                        //float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
                        //float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
                        //Log.d("dX", String.valueOf(dX));
                        //Log.d("dp", String.valueOf(dp));
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_down);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}