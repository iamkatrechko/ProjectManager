package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class ProjectsListFragment extends Fragment{

    ArrayList<Project> projects;
    RecyclerAdapter adapter;

    public static ProjectsListFragment newInstance() {
        return new ProjectsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_projects_list, parent, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.section_list);

        // Initialize contacts
        ProjectLab lab = ProjectLab.get(getActivity().getApplication());
        projects = lab.getProjects();
        // Create adapter passing in the sample user data
        adapter = new RecyclerAdapter(projects, getActivity());
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplication()));
        // That's all!

        AddFloatingActionButton button = (AddFloatingActionButton) v.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProjectEditActivity.class);
                intent.putExtra("ID", "0");
                intent.putExtra("Operation", "add");
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public static Context mContext;
        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public UUID _id;
            public TextView nameTextView;
            public ImageView projectColor;
            public ImageView projectEdit;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
                projectColor = (ImageView) itemView.findViewById(R.id.imageView);
                projectEdit = (ImageView) itemView.findViewById(R.id.imageViewEdit);
                projectEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ProjectEditActivity.class);
                        intent.putExtra("ID", _id.toString());
                        intent.putExtra("Operation", "edit");
                        mContext.startActivity(intent);
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProjectEditActivity.class);
                        intent.putExtra("ID", _id.toString());
                        intent.putExtra("Operation", "edit");
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        private List<Project> mProjects;

        // Pass in the contact array into the constructor
        public RecyclerAdapter(List<Project> projects, Context context) {
            mProjects = projects;
            mContext = context;
        }

        // Usually involves inflating a layout from XML and returning the holder
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View recyclerView = inflater.inflate(R.layout.recycler_project_item, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(recyclerView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            Project project = mProjects.get(position);
            viewHolder._id = project.getID();

            // Set item views based on the data model
            TextView textView = viewHolder.nameTextView;
            textView.setText(project.getTitle());

            ImageView button = viewHolder.projectColor;
            button.setColorFilter(project.getColor());
        }

        // Return the total count of items
        @Override
        public int getItemCount() {
            return mProjects.size();
        }
    }
}
