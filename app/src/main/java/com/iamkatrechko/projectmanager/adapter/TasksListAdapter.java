package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.ItemTouchHelperViewHolder;
import com.iamkatrechko.projectmanager.Methods;
import com.iamkatrechko.projectmanager.MyNotificationManager;
import com.iamkatrechko.projectmanager.OnItemClickListener;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback.*;
import com.iamkatrechko.projectmanager.SubProjectEditActivity;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 03.03.2017.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.ViewHolder> implements OnItemMoveAndSwipeListener {

    enum ViewTypes {VIEW_TYPE_TASK, VIEW_TYPE_PROJECT, VIEW_TYPE_DATE_LABEL}

    final public static int ADAPTER_ITEM_TYPE_SUB_PROJECT = 0;
    final public static int ADAPTER_ITEM_TYPE_TASK = 1;
    private MyNotificationManager myNotificationManager;
    private Context mContext;
    private List<Task> mTasks;
    /** Цвета для обозначения приоритетов */
    private String[] aColors;
    /** Слушатель нажатия на подпроект/задачу */
    private OnItemClickListener mItemClickListener;
    /** Слушатель свайпа элементов списка влево/вправо */
    private OnItemSwipeListener mItemSwipeListener;
    private ProjectLab lab;
    private UUID mId;
    private Methods m;
    private SimpleItemTouchHelperCallback callback;

    public TasksListAdapter(Context context, UUID ID, boolean swipeToLeft, boolean swipeToRight,
                            @ColorInt int swipeToLeftColor, @ColorInt int swipeToRightColor,
                            @DrawableRes int swipeToLeftIcon, @DrawableRes int swipeToRightIcon,
                            boolean dragItem) {
        mContext = context;
        lab = ProjectLab.get(context);
        mId = ID;
        m = new Methods(mContext);
        aColors = context.getResources().getStringArray(R.array.priorities_colors);
        myNotificationManager = new MyNotificationManager(context);
        callback = new SimpleItemTouchHelperCallback(mContext, this, swipeToLeft, swipeToRight,
                swipeToLeftColor, swipeToRightColor,
                swipeToLeftIcon, swipeToRightIcon, dragItem);
    }

    public void setData(List<Task> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // Инициализация свайпов
        ItemTouchHelper mItemTouchHelper;
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (mTasks.get(position).getType().equals(Task.TASK_TYPE_SUB_PROJECT)) {
                return ADAPTER_ITEM_TYPE_SUB_PROJECT;
            } else {
                return ADAPTER_ITEM_TYPE_TASK;
            }
        } catch (Exception e) {
            Log.d("TasksListFragment", e.getLocalizedMessage());
        }
        return 0;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // Если предыдущая позиция равна настоящей, либо задача поднялась выше подпроекта
        // TODO вычислять подпроект через instanceof
        if (fromPosition == toPosition || toPosition < lab.getLastTaskIndex(mId)) {
            return;
        }

        lab.moveItem(lab.getParentIdOfTask(mTasks.get(fromPosition).getID()), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemLeftSwipe(int position) {
        if (mItemSwipeListener != null) {
            mItemSwipeListener.onItemLeftSwipe(position);
        }
    }

    @Override
    public void onItemRightSwipe(int position) {
        if (mItemSwipeListener != null) {
            mItemSwipeListener.onItemRightSwipe(position);
        }
    }

    @Override
    public TasksListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recyclerView;
        if (viewType == ADAPTER_ITEM_TYPE_SUB_PROJECT) {
            recyclerView = inflater.inflate(R.layout.recycler_task_item_poject, parent, false);
        } else {
            recyclerView = inflater.inflate(R.layout.recycler_task_item, parent, false);
        }

        return new TasksListAdapter.ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(final TasksListAdapter.ViewHolder vHolder, final int position) {
        final Task task = mTasks.get(position);
        vHolder._id = task.getID();
        vHolder.sType = task.getType();

        if (vHolder.getItemViewType() == ADAPTER_ITEM_TYPE_SUB_PROJECT) {
            vHolder.tvTitle.setText(task.getTitle());
            vHolder.tvDescription.setText(task.getDescription());
            return;
        }

        vHolder.tvTitle.setText(task.getTitle());
        vHolder.itemView.findViewById(R.id.card_view).setVisibility(task.getIsDone() ? View.GONE : View.VISIBLE);
        vHolder.tvDescription.setText(m.getFormatDate(task.getDate(), task.getTime()));
        vHolder.flPriority.setBackgroundColor(Color.parseColor(aColors[task.getPriority()]));
        vHolder.ivImageRemind.setVisibility(task.getIsNotify() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnSwipedListener(OnItemSwipeListener swipeListener) {
        mItemSwipeListener = swipeListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public UUID _id;
        public String sType;
        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView ivImage;
        public ImageView ivImageRemind;
        public FrameLayout flPriority; //Полоса с цветом приоритета

        public ViewHolder(final View itemView) {
            super(itemView);

            if (getItemViewType() == ADAPTER_ITEM_TYPE_SUB_PROJECT) {
                tvTitle = (TextView) itemView.findViewById(R.id.title);
                tvDescription = (TextView) itemView.findViewById(R.id.description);
                ivImage = (ImageView) itemView.findViewById(R.id.imageView2);
                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Не отрабатывается нажатие
                        Intent intent = new Intent(mContext, SubProjectEditActivity.class);
                        intent.putExtra("mId", _id.toString());
                        intent.putExtra("Operation", "edit");
                        intent.putExtra("parent_ID", "0");
                        mContext.startActivity(intent);
                    }
                });
            } else {
                tvTitle = (TextView) itemView.findViewById(R.id.title);
                tvDescription = (TextView) itemView.findViewById(R.id.description);
                flPriority = (FrameLayout) itemView.findViewById(R.id.priority_color);
                ivImageRemind = (ImageView) itemView.findViewById(R.id.imageViewRemind);
            }

            itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(sType, _id);
                    }
                }
            });
        }

        @Override
        public void onItemSelected() {
            ((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(4));
        }

        @Override
        public void onItemClear() {
            ((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(2));
        }
    }
}
