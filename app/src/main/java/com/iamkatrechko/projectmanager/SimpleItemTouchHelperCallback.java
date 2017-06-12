package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.interfce.ItemTouchHelperViewHolder;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final TasksListAdapter mAdapter;
    /** Возможность свайпа влево */
    private boolean mSwipeToLeft;
    /** Возможность свайпа вправо */
    private boolean mSwipeToRight;
    private Paint p = new Paint();
    /** Контекст */
    private Context mContext;

    /** Цвет заполнения для свайпа влево */
    private int mSwipeToLeftColor;
    /** Цвет заполнения для свайпа вправо */
    private int mSwipeToRightColor;
    private int mSwipeToLeftIcon;
    private int mSwipeToRightIcon;
    private boolean mDragItem;

    public SimpleItemTouchHelperCallback(Context context, TasksListAdapter adapter,
                                         boolean swipeToLeft, boolean swipeToRight,
                                         @ColorInt int swipeToLeftColor, @ColorInt int swipeToRightColor,
                                         @DrawableRes int swipeToLeftIcon, @DrawableRes int swipeToRightIcon,
                                         boolean dragItem) {
        mContext = context;
        mAdapter = adapter;
        mSwipeToLeft = swipeToLeft;
        mSwipeToRight = swipeToRight;
        mSwipeToLeftColor = swipeToLeftColor;
        mSwipeToRightColor = swipeToRightColor;
        mSwipeToLeftIcon = swipeToLeftIcon;
        mSwipeToRightIcon = swipeToRightIcon;
        mDragItem = dragItem;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (mAdapter.getItemViewType(viewHolder.getAdapterPosition()) != 1) {
            return 0;
        }
        final int dragFlags = mDragItem ? (ItemTouchHelper.UP | ItemTouchHelper.DOWN) : 0;
        final int swipeFlags = (mSwipeToLeft ? ItemTouchHelper.START : 0) | (mSwipeToRight ? ItemTouchHelper.END : 0);
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.END) {
            Log.d("onSwiped", "RIGHT");
            mAdapter.onItemRightSwipe(position);
        } else {
            Log.d("onSwiped", "LEFT");
            mAdapter.onItemLeftSwipe(position);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Считывать разрешение на свайп через интерфейс у адаптера на каждый холдер и принимать во внимание общее разрешение на свайп
        //Полоса приоритета скрыта => подпроект => запретить свайп
        boolean isSubProject = viewHolder.getItemViewType() == TasksListAdapter.ADAPTER_ITEM_TYPE_SUB_PROJECT;
        if (isSubProject) {
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Bitmap icon;
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            RectF icon_dest;
            if (dX < 0) {
                p.setColor(mSwipeToLeftColor);
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), mSwipeToLeftIcon);
                icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
            } else {
                p.setColor(mSwipeToRightColor);
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), mSwipeToRightIcon);
                icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
            }
            c.drawBitmap(icon, null, icon_dest, p);
        }
    }

    /** Слушатель перемешения и свайпа элементов списка */
    public interface OnItemMoveAndSwipeListener extends OnItemSwipeListener {

        /**
         * Слушатель перемещения элемента списка
         * @param fromPosition начальная позиция
         * @param toPosition   конечная позиция
         */
        void onItemMove(int fromPosition, int toPosition);
    }

    /** Слушатель свайпа элементов списка влево/вправо */
    public interface OnItemSwipeListener {

        /**
         * Слушатель свайпа элемента влево
         * @param position позиция смещенного элемента
         */
        void onItemLeftSwipe(int position);

        /**
         * Слушатель свайпа элемента вправо
         * @param position позиция смещенного элемента
         */
        void onItemRightSwipe(int position);
    }
}
