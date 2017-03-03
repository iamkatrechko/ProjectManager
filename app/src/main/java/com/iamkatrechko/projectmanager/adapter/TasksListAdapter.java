package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

import com.iamkatrechko.projectmanager.ItemTouchHelperAdapter;
import com.iamkatrechko.projectmanager.ItemTouchHelperViewHolder;
import com.iamkatrechko.projectmanager.Methods;
import com.iamkatrechko.projectmanager.MyNotificationManager;
import com.iamkatrechko.projectmanager.OnItemClickListener;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.SubProjectEditActivity;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 03.03.2017.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    final private static int ADAPTER_ITEM_TYPE_SUB_PROJECT = 0;
    final private static int ADAPTER_ITEM_TYPE_TASK = 1;
    MyNotificationManager myNotificationManager;
    private Context mContext;
    private List<Task> aTasks;
    /** Цвета для обозначения приоритетов */
    private String[] aColors;
    /** Слушатель нажатия на подпроект/задачу */
    private OnItemClickListener mItemClickListener;
    private ProjectLab lab;
    private Paint p = new Paint();
    UUID mId;
    Methods m;

    public TasksListAdapter(List<Task> tasks, Context context, UUID ID) {
        lab = ProjectLab.get(context);
        mId = ID;
        aTasks = tasks;
        mContext = context;
        m = new Methods(mContext);
        aColors = context.getResources().getStringArray(R.array.priorities_colors);
        myNotificationManager = new MyNotificationManager(context);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        if (toPosition < lab.getLastTaskIndex(mId)) {
            return;
        }

        lab.moveItem(lab.getParentIdOfTask(aTasks.get(fromPosition).getID()), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        myNotificationManager.deleteNotification(aTasks.get(position).getID());
        aTasks.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, aTasks.size());
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (aTasks.get(position).getType().equals(Task.TASK_TYPE_SUB_PROJECT)) {
                return ADAPTER_ITEM_TYPE_SUB_PROJECT;
            } else {
                return ADAPTER_ITEM_TYPE_TASK;
            }
        } catch (Exception e) {
            Log.d("TasksListFragment", e.getLocalizedMessage());
        }
        return 0;
    }

    public void setIsDone(int position) {
        Log.d("setIsDone", String.valueOf(position) + " - " + aTasks.get(position).getTitle());
        myNotificationManager.deleteNotification(aTasks.get(position).getID());
        aTasks.get(position).setIsDone(true);
        notifyItemRemoved(position);
        notifyItemRangeInserted(position, 1);
        //notifyDataSetChanged();
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
        final Task task = aTasks.get(position);
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
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //Полоса приоритета скрыта => подпроект => запретить свайп
        if (viewHolder.itemView.findViewById(R.id.priority_color).getVisibility() == View.GONE) {
            return;
        }

        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                p.setColor(mContext.getResources().getColor(R.color.swipe_color_right));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_delete);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                p.setColor(mContext.getResources().getColor(R.color.swipe_color_left));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_done);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
        }
    }

    @Override
    public int getItemCount() {
        return aTasks.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
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
                        mItemClickListener.onItemClicker(sType, _id);
                    }
                }
            });

                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });*/
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
