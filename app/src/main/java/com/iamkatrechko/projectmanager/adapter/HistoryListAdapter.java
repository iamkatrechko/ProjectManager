package com.iamkatrechko.projectmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер отображаемой иерархии задач
 * @author iamkatrechko
 *         Date: 03.05.2017
 */
public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    /** Список названий подпроектов */
    private List<String> mHistory = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.recycler_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bindView(mHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    /**
     * Устанавливает данные в адаптер и обновляет список
     * @param history список названий подпроектов дерева
     */
    public void setData(List<String> history) {
        mHistory = history;
        notifyDataSetChanged();
    }

    /** Холдер узла иерархии истории задачи */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Текстовая метка с названием узла дерева */
        public TextView tvTitle;

        /**
         * Конструктор
         * @param itemView основной виджет элемента списка
         */
        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        }

        /**
         * Настраивает виджет элемента списка
         * @param history заговок узла дерева иерархии
         */
        private void bindView(String history) {
            tvTitle.setText(history);
        }
    }
}
