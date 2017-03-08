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

    private List<Project> mProjects = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public ProjectsListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.recycler_project_item, parent, false));
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Project project = mProjects.get(position);
        viewHolder._id = project.getID();

        TextView textView = viewHolder.nameTextView;
        textView.setText(project.getTitle());

        ImageView button = viewHolder.projectColor;
        button.setColorFilter(project.getColor());
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public UUID _id;
        public TextView nameTextView;
        public ImageView projectColor;
        public ImageView projectEdit;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            projectColor = (ImageView) itemView.findViewById(R.id.image_view_label);
            projectEdit = (ImageView) itemView.findViewById(R.id.imageViewEdit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mProjects.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Project project);
    }
}
