package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.iamkatrechko.projectmanager.new_entity.AbstractTaskObject;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.DateLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 03.03.2017.
 */
public class TasksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveAndSwipeListener {

    enum ViewTypes {VIEW_TYPE_TASK, VIEW_TYPE_PROJECT, VIEW_TYPE_DATE_LABEL}

    final public static int ADAPTER_ITEM_TYPE_SUB_PROJECT = 0;
    final public static int ADAPTER_ITEM_TYPE_TASK = 1;
    final public static int ADAPTER_ITEM_TYPE_DATE = 3;
    private MyNotificationManager myNotificationManager;
    private Context mContext;
    private List<AbstractTaskObject> mTasks = new ArrayList<>();
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

    public void setData(List<AbstractTaskObject> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    public void setTasksData(List<Task> tasks) {
        mTasks.clear();
        mTasks.addAll(tasks);
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
        return mTasks.get(position).getViewType();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // Если предыдущая позиция равна настоящей, либо задача поднялась выше подпроекта
        // TODO вычислять подпроект через instanceof
        if (fromPosition == toPosition || toPosition < lab.getLastTaskIndex(mId)) {
            return;
        }
        AbstractTaskObject taskObject = mTasks.get(fromPosition);
        if (taskObject instanceof Task) {
            Task task = (Task) taskObject;

            lab.moveItem(lab.getParentIdOfTask(task.getID()), fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ADAPTER_ITEM_TYPE_SUB_PROJECT:
                return new TasksListAdapter.ViewHolderSubProject(inflater.inflate(R.layout.recycler_task_item_poject, parent, false));
            case ADAPTER_ITEM_TYPE_TASK:
                return new ViewHolderTask(inflater.inflate(R.layout.recycler_task_item, parent, false));
            case ADAPTER_ITEM_TYPE_DATE:
                return new TasksListAdapter.ViewHolderDate(inflater.inflate(R.layout.recycler_date_item, parent, false));
        }
        return new ViewHolderTask(inflater.inflate(R.layout.recycler_task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vHolderAll, final int position) {
        final AbstractTaskObject taskObject = mTasks.get(position);

        // TODO Добавить ViewHolder с меткой проекта для списка в календаре из разметки recycler_task_item_calendar
        switch (getItemViewType(position)) {
            case ADAPTER_ITEM_TYPE_SUB_PROJECT: {
                ViewHolderSubProject vH = (ViewHolderSubProject) vHolderAll;
                Task task = (Task) taskObject;

                vH._id = task.getID();
                vH.tvTitle.setText(task.getTitle());
                vH.tvDescription.setText(task.getDescription());
                break;
            }
            case ADAPTER_ITEM_TYPE_TASK: {
                ViewHolderTask vH = (ViewHolderTask) vHolderAll;
                Task task = (Task) taskObject;
                vH._id = task.getID();
                vH.tvTitle.setText(task.getTitle());
                vH.itemView.findViewById(R.id.card_view).setVisibility(task.getIsDone() ? View.GONE : View.VISIBLE);
                vH.tvDescription.setText(m.getFormatDate(task.getDate(), task.getTime()));
                vH.flPriority.setBackgroundColor(Color.parseColor(aColors[task.getPriority()]));
                vH.ivImageRemind.setVisibility(task.getIsNotify() ? View.VISIBLE : View.GONE);
                break;
            }
            case ADAPTER_ITEM_TYPE_DATE: {
                DateLabel dateLabel = (DateLabel) taskObject;
                ViewHolderDate vHolder = (ViewHolderDate) vHolderAll;
                vHolder.tvDate.setText(dateLabel.getDate());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    /**
     * Установить слушатель нажатия элементов
     * @param listener слушатель нажатия элементов
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * УСтановить случатель перемещения и свайпа элементов
     * @param swipeListener случатель перемещения и свайпа элементов
     */
    public void setOnSwipedListener(OnItemSwipeListener swipeListener) {
        mItemSwipeListener = swipeListener;
    }

    /** ViewHolderTask списка задач */
    private class ViewHolderTask extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public UUID _id;
        /** Название задачи */
        public TextView tvTitle;
        /** Описание задачи */
        public TextView tvDescription;
        public ImageView ivImageRemind;
        /** Полоса с цветом приоритета */
        public FrameLayout flPriority;

        public ViewHolderTask(final View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvDescription = (TextView) itemView.findViewById(R.id.description);
            flPriority = (FrameLayout) itemView.findViewById(R.id.priority_color);
            ivImageRemind = (ImageView) itemView.findViewById(R.id.imageViewRemind);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(getItemViewType(), mTasks.get(getAdapterPosition()));
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

    /** ViewHolderTask списка подпроектов */
    private class ViewHolderSubProject extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public UUID _id;
        /** Название подпроекта */
        public TextView tvTitle;
        /** Описание подпроекта */
        public TextView tvDescription;
        public ImageView ivImage;

        public ViewHolderSubProject(final View itemView) {
            super(itemView);

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(getItemViewType(), mTasks.get(getAdapterPosition()));
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

    /** ViewHolderTask временной метки */
    private class ViewHolderDate extends RecyclerView.ViewHolder {
        /** Метка, отображающая дату */
        public TextView tvDate;

        public ViewHolderDate(final View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.text_view_date);
        }
    }
}
