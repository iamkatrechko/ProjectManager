package com.iamkatrechko.projectmanager.adapter;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
 * Адаптер списка проектов
 * @author iamkatrechko
 *         Date: 07.03.2017
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
        /** Кнопка открытия меню */
        public ImageView imageViewMenu;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            icCircle = (ImageView) itemView.findViewById(R.id.image_view_label);
            imageViewMenu = (ImageView) itemView.findViewById(R.id.image_view_menu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mProjects.get(getAdapterPosition()));
                    }
                }
            });
            imageViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.popup_menu_project);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_edit:
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(mProjects.get(getAdapterPosition()));
                                    }
                                    return true;
                                case R.id.action_delete:
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onDeleteClick(mProjects.get(getAdapterPosition()));
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        private void bindView(Project project) {
            _id = project.getID();
            tvTitle.setText(project.getTitle());
            icCircle.setColorFilter(project.getColor());
            imageViewMenu.setVisibility(getAdapterPosition() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Project project);

        void onDeleteClick(Project project);
    }
}
