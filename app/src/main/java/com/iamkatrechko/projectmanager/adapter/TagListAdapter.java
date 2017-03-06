package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Список тегов
 * Created on 06.03.2017
 * author: ivanov_m
 */
public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    /** Список тегов */
    private ArrayList<Tag> mTagsList = new ArrayList<>();
    /** Интерфейс слушателя нажатий кнопки удалить/редактировать тег */
    private OnTagItemClickListener mClickListener;

    public TagListAdapter(ArrayList<Tag> tagList) {
        mTagsList = tagList;
    }

    public void setOnClickListener(OnTagItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.recycler_tag_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        Tag tag = mTagsList.get(position);

        vHolder._id = tag.getID();
        vHolder.tvTitle.setText(tag.getTitle());
    }

    @Override
    public int getItemCount() {
        return mTagsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public UUID _id;
        /** Название тега */
        public TextView tvTitle;
        /** Кнопка редактирования тега */
        public ImageButton ibEdit;
        /** Кнопка удаления тега */
        public ImageButton ibDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ibEdit = (ImageButton) itemView.findViewById(R.id.ibEdit);
            ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);

            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onEditClick(mTagsList.get(getAdapterPosition()));
                    }
                }
            });
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onDeleteClick(mTagsList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    /** Интерфейс слушателя нажатий кнопки удалить/редактировать тег */
    public interface OnTagItemClickListener {

        /** Слушатель нажатия кнопки "удалить тэг" */
        void onDeleteClick(Tag tag);

        /** Слушатель нажатия кнопки "редактировать тэг" */
        void onEditClick(Tag tag);
    }
}
