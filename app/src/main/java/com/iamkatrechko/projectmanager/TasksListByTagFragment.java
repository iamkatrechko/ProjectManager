package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.entity.Task;

import java.util.List;
import java.util.UUID;

/**
 * Created by Muxa on 10.04.2016.
 */
public class TasksListByTagFragment extends Fragment {
    private ProjectLab lab;
    private UUID tagID;
    private RecyclerView recyclerView;
    private List<Task> mTasksList;
    private TasksAdapter adapter;
    private Methods m;
    private MyNotificationManager myNotificationManager;
    private Paint p = new Paint();

    public static TasksListByTagFragment newInstance(UUID tagID){
        TasksListByTagFragment fragment = new TasksListByTagFragment();

        Bundle args = new Bundle();
        args.putString("tagID", tagID.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        m = new Methods(getActivity());
        myNotificationManager = new MyNotificationManager(getActivity());

        tagID = UUID.fromString(getArguments().getString("tagID"));
        //Type = getArguments().getString("Type");

        lab = ProjectLab.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks_list, parent, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);

        mTasksList = lab.getTasksListByTagID(tagID);

        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        resetAdapter();
    }

    private void resetAdapter(){
        mTasksList = lab.getTasksListByTagID(tagID);
        adapter = new TasksAdapter(mTasksList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements ItemTouchHelperAdapter {
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
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition == toPosition){
                return;
            }
            //if (toPosition < lab.getLastTaskIndex(mId)){
            //    return;
            //}

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

        public void setIsDone(int position){
            Log.d("setIsDone", String.valueOf(position) + " - " + aTasks.get(position).getTitle());
            myNotificationManager.deleteNotification(aTasks.get(position).getID());
            aTasks.get(position).setIsDone(true);
            notifyItemRemoved(position);
            notifyItemRangeInserted(position, 1);
            //notifyDataSetChanged();
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
            if (viewHolder.itemView.findViewById(R.id.priority_color).getVisibility() == View.GONE){
                return;
            }

            Bitmap icon;
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                if(dX > 0){
                    p.setColor(getResources().getColor(R.color.swipe_color_left));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft()+ 2 * width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                } else {
                    p.setColor(getResources().getColor(R.color.swipe_color_right));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width,(float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
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
                    tvTitle = (TextView) itemView.findViewById(R.id.title);
                    tvDescription = (TextView) itemView.findViewById(R.id.description);
                    ivImage = (ImageView) itemView.findViewById(R.id.imageView2);
                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Не отрабатывается нажатие
                            Intent intent = new Intent(aContext, SubProjectEditActivity.class);
                            intent.putExtra("mId", _id.toString());
                            intent.putExtra("Operation", "edit");
                            intent.putExtra("parent_ID", "0");
                            aContext.startActivity(intent);
                        }
                    });
                }else {
                    tvTitle = (TextView) itemView.findViewById(R.id.title);
                    tvDescription = (TextView) itemView.findViewById(R.id.description);
                    flPriority = (FrameLayout) itemView.findViewById(R.id.priority_color);
                    ivImageRemind = (ImageView) itemView.findViewById(R.id.imageViewRemind);
                }

                itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sType.equals(Task.TASK_TYPE_SUB_PROJECT)) {                             //Если нажата "Задача" -> переходим дальше
                            Intent intent = new Intent(aContext, TasksListActivity.class);
                            intent.putExtra("mId", _id.toString());
                            intent.putExtra("Type", sType);
                            aContext.startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.act_slide_left_in, R.anim.act_slide_left_out);
                        } else {                                                                    //Если нажата "Подзадача" -> редактируем
                            Intent intent = new Intent(aContext, TaskEditActivity.class);
                            intent.putExtra("mId", _id.toString());
                            intent.putExtra("Operation", "edit");
                            intent.putExtra("parent_ID", "0");
                            aContext.startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
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

        @Override
        public int getItemCount() {
            return aTasks.size();
        }
    }
}
