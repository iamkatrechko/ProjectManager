package com.iamkatrechko.projectmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 07.03.2017
 * author: iamkatrechko
 */
public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ViewHolder> {
    /** Список проектов */
    private List<Project> mProjects = new ArrayList<>();
    /** Слушатель нажатия на проект */
    private OnItemClickListener mOnItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.recycler_project_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bindView(mProjects.get(position));
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<Project> projects) {
        mProjects = projects;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /** Идентификатор проекта */
        public UUID _id;
        /** Название проекта */
        public TextView tvTitle;
        /** Кружок рядом с названием проекта */
        public ImageView icCircle;
        /** Кнопка редактирования проекта */
        public ImageView ivEdit;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            icCircle = (ImageView) itemView.findViewById(R.id.image_view_label);
            ivEdit = (ImageView) itemView.findViewById(R.id.image_view_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mProjects.get(getAdapterPosition()));
                    }
                }
            });
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onDeleteClick(mProjects.get(getAdapterPosition()));
                    }
                }
            });
        }

        private void bindView(Project project) {
            _id = project.getID();
            tvTitle.setText(project.getTitle());
            icCircle.setColorFilter(project.getColor());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Project project);
        void onDeleteClick(Project project);
    }
}
