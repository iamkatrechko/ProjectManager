package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
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
import com.iamkatrechko.projectmanager.OnItemClickListener;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback;
import com.iamkatrechko.projectmanager.SimpleItemTouchHelperCallback.*;
import com.iamkatrechko.projectmanager.SubProjectEditActivity;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.DateLabel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 03.03.2017.
 */
public class TasksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveAndSwipeListener {

    final public static int ADAPTER_ITEM_TYPE_SUB_PROJECT = 0;
    final public static int ADAPTER_ITEM_TYPE_TASK = 1;
    final public static int ADAPTER_ITEM_TYPE_DATE = 3;
    private Context mContext;
    private List<? extends TaskListItem> mTasks = new ArrayList<>();
    /** Цвета для обозначения приоритетов */
    private String[] aColors;
    /** Слушатель нажатия на подпроект/задачу */
    private OnItemClickListener mItemClickListener;
    /** Слушатель свайпа элементов списка влево/вправо */
    private OnItemSwipeListener mItemSwipeListener;
    private ProjectLab lab;
    private Methods m;
    private SimpleItemTouchHelperCallback callback;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    public TasksListAdapter(Context context, boolean swipeToLeft, boolean swipeToRight,
                            @ColorInt int swipeToLeftColor, @ColorInt int swipeToRightColor,
                            @DrawableRes int swipeToLeftIcon, @DrawableRes int swipeToRightIcon,
                            boolean dragItem) {
        mContext = context;
        lab = ProjectLab.get(context);
        m = new Methods(mContext);
        aColors = context.getResources().getStringArray(R.array.priorities_colors);
        callback = new SimpleItemTouchHelperCallback(mContext, this, swipeToLeft, swipeToRight,
                swipeToLeftColor, swipeToRightColor,
                swipeToLeftIcon, swipeToRightIcon, dragItem);
    }

    public void setData(List<? extends TaskListItem> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    public void setEmptyListView(View view) {
        mEmptyView = view;
        super.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mEmptyView != null && mRecyclerView != null) {
                    mEmptyView.setVisibility(mTasks.isEmpty() ? View.VISIBLE : View.GONE);
                    mRecyclerView.setVisibility(mTasks.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        // Инициализация свайпов
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.get(position).getViewType();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition == toPosition || getItemViewType(toPosition) != ADAPTER_ITEM_TYPE_TASK) {
            return;
        }

        TaskListItem taskObject = mTasks.get(fromPosition);
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
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vHolder, final int position) {
        TaskListItem taskObject = mTasks.get(position);

        // TODO Добавить ViewHolder с меткой проекта для списка в календаре из разметки recycler_task_item_calendar
        switch (getItemViewType(position)) {
            case ADAPTER_ITEM_TYPE_SUB_PROJECT: {
                ((ViewHolderSubProject) vHolder).bindView(taskObject);
                break;
            }
            case ADAPTER_ITEM_TYPE_TASK: {
                ((ViewHolderTask) vHolder).bindView(taskObject);
                break;
            }
            case ADAPTER_ITEM_TYPE_DATE: {
                ((ViewHolderDate) vHolder).bindView(taskObject);
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

        public void bindView(TaskListItem taskListItem) {
            Task task = (Task) taskListItem;
            _id = task.getID();
            tvTitle.setText(task.getTitle());
            //vH.itemView.findViewById(R.id.card_view).setVisibility(task.getIsDone() ? View.GONE : View.VISIBLE);
            tvDescription.setText(m.getFormatDate(task.getDate(), task.getTime()));
            flPriority.setBackgroundColor(Color.parseColor(aColors[task.getPriority()]));
            ivImageRemind.setVisibility(task.getIsNotify() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onItemSelected() {
            //TODO доделать тень
            //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(4));
        }

        @Override
        public void onItemClear() {
            //TODO доделать тень
            //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(2));
        }
    }

    /** ViewHolderTask подпроекта */
    private class ViewHolderSubProject extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private UUID _id;
        /** Название подпроекта */
        private TextView tvTitle;
        /** Описание подпроекта */
        private TextView tvDescription;
        /** Кнопка редактирования подпроекта */
        private ImageView ivEdit;

        public ViewHolderSubProject(final View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvDescription = (TextView) itemView.findViewById(R.id.description);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

        public void bindView(TaskListItem taskListItem) {
            Task task = (Task) taskListItem;
            _id = task.getID();
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
        }

        @Override
        public void onItemSelected() {
            //TODO доделать тень
            //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(4));
        }

        @Override
        public void onItemClear() {
            //TODO доделать тень
            //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(2));
        }
    }

    /** ViewHolderTask временной метки */
    public class ViewHolderDate extends RecyclerView.ViewHolder {
        /** Метка, отображающая дату */
        public TextView tvDate;
        /** Календарь с датой разделителя */
        public Calendar mCalendar;

        public ViewHolderDate(final View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.text_view_date);
        }

        public void bindView(TaskListItem taskListItem) {
            DateLabel dateLabel = (DateLabel) taskListItem;
            tvDate.setText(dateLabel.getDate());
            mCalendar = dateLabel.getCalendar();
        }
    }
}