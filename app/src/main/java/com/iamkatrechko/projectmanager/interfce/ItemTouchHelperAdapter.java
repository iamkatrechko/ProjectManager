package com.iamkatrechko.projectmanager.interfce;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     //* @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolderTask)
     //* @see RecyclerView.ViewHolderTask#getAdapterPosition()
     */
    void onItemMove(int fromPosition, int toPosition);

    /**
     * Called when an item has been dismissed by a swipe.
     *
     * @param position The position of the item dismissed.
     //* @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolderTask)
     //* @see RecyclerView.ViewHolderTask#getAdapterPosition()
     */
    void onItemDismiss(int position);

    void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
}
