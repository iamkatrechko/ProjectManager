package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 25.02.2016.
 */
public class TasksDoneListFragment extends Fragment{
    MyNotificationManager myNotificationManager;

    List<Task> mTasksList;
    public TasksAdapter adapter;
    public static ProjectLab lab;
    RecyclerView recyclerView;

    UUID ID;
    String Type;

    public static TasksDoneListFragment newInstance(UUID ID) {
        TasksDoneListFragment fragment = new TasksDoneListFragment();
        Bundle args = new Bundle();

        args.putString("ID", ID.toString());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        ID = UUID.fromString(getArguments().getString("ID"));

        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);
        myNotificationManager = new MyNotificationManager(getActivity());

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);

        mTasksList = lab.getTasksListOnAllLevel(ID);

        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*mTasksList = lab.getTasksListOnAllLevel(ID);
        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>  {
        final public static int ADAPTER_ITEM_TYPE_SUB_PROJECT = 0;
        final public static int ADAPTER_ITEM_TYPE_TASK = 1;
        public Context aContext;
        private List<Task> aTasks;
        public String[] aColors;                                                                     //Цвета для обозначения приоритетов

        public TasksAdapter(List<Task> tasks, Context context) {
            aTasks = tasks;
            aContext = context;
            aColors = context.getResources().getStringArray(R.array.priorities_colors);
        }

        @Override
        public int getItemViewType(int position) {
            //Log.d("TasksListFragment", "getItemViewType, pos=" + position);
            try {
                if (aTasks.get(position).getType().equals(Task.TASK_TYPE_SUB_PROJECT)){
                    return ADAPTER_ITEM_TYPE_SUB_PROJECT;
                }else{
                    return ADAPTER_ITEM_TYPE_TASK;
                }
            }catch (Exception e){
                Log.d("TasksListFragment", e.getLocalizedMessage());
            }
            return 0;
        }

        public void onItemDismiss(int position) {
            myNotificationManager.deleteNotification(aTasks.get(position).getID());
            aTasks.remove(position);
            notifyItemRemoved(position);
            //notifyItemRangeChanged(position, aTasks.size());
        }

        public void setIsDone(int position){
            myNotificationManager.addNotification(mTasksList.get(position).getID());
            aTasks.get(position).setIsDone(true);
            notifyDataSetChanged();
        }

        @Override
        public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View recyclerView;

            if (viewType == ADAPTER_ITEM_TYPE_SUB_PROJECT){
                recyclerView = inflater.inflate(R.layout.recycler_task_item_poject, parent, false);
            }else {
                recyclerView = inflater.inflate(R.layout.recycler_task_item, parent, false);
            }

            return new ViewHolder(recyclerView);
        }


        @Override
        public void onBindViewHolder(final TasksAdapter.ViewHolder vHolder, final int position) {
            final Task task = aTasks.get(position);
            vHolder._id = task.getID();
            vHolder.sType = task.getType();

            if (vHolder.getItemViewType() == ADAPTER_ITEM_TYPE_SUB_PROJECT){
                vHolder.itemView.findViewById(R.id.card_view).setVisibility(View.GONE);
                return;
            }else{
                vHolder.itemView.findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            }

            vHolder.itemView.findViewById(R.id.card_view).setVisibility(task.getIsDone() ? View.VISIBLE : View.GONE);
            vHolder.tvTitle.setText(task.getTitle());
            vHolder.flPriority.setBackgroundColor(Color.parseColor(aColors[task.getPriority()]));
            vHolder.ivImageRemind.setVisibility(task.getIsNotify() ? View.VISIBLE : View.GONE);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
            public UUID _id;
            public String sType;
            public TextView tvTitle;
            public TextView tvDescription;
            public ImageView ivImage;
            public ImageView ivImageRemind;
            public FrameLayout flPriority;                                                          //Полоса с цветом приоритета

            public ViewHolder(final View itemView) {
                super(itemView);

                if (getItemViewType() == ADAPTER_ITEM_TYPE_SUB_PROJECT){
                    return;
                }else {
                    tvTitle = (TextView) itemView.findViewById(R.id.title);
                    tvDescription = (TextView) itemView.findViewById(R.id.description);
                    flPriority = (FrameLayout) itemView.findViewById(R.id.priority_color);
                    ivImageRemind = (ImageView) itemView.findViewById(R.id.imageViewRemind);
                }

                itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Нажатие на задачу
                    }
                });
            }

            @Override
            public void onItemSelected() {
                //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(4));
            }

            @Override
            public void onItemClear() {
                //((CardView) itemView.findViewById(R.id.card_view)).setCardElevation(m.getPXfromDP(2));
            }
        }

        @Override
        public int getItemCount() {
            return aTasks.size();
        }
    }
}