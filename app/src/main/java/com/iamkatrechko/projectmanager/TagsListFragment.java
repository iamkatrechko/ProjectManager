package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;

public class TagsListFragment extends Fragment {
    ProjectLab lab;
    ArrayList<Tag> mTagsList = new ArrayList<>();
    TagListAdapter adapter;

    public static TagsListFragment newInstance(){
        return new TagsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        lab = ProjectLab.get(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_projects_list, parent, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        mTagsList = lab.getTags();
        adapter = new TagListAdapter(mTagsList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplication()));

        AddFloatingActionButton button = (AddFloatingActionButton) v.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TagEditActivity.class);
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

    public static class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {
        public static Context mContext;
        private ArrayList<Tag> aTagList = new ArrayList<>();

        public TagListAdapter(ArrayList<Tag> tagList, Context context) {
            aTagList = tagList;
            mContext = context;
        }

        public void deleteTag(UUID id){
            Tag tag = ProjectLab.get(mContext).getTagByID(id);
            int pos = aTagList.indexOf(tag);
            aTagList.remove(tag);
            notifyItemRemoved(pos);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View recyclerView = inflater.inflate(R.layout.recycler_tag_item, parent, false);

            return new ViewHolder(recyclerView);
        }

        @Override
        public void onBindViewHolder(ViewHolder vHolder, int position) {
            Tag tag = aTagList.get(position);

            vHolder._id = tag.getID();
            vHolder.tvTitle.setText(tag.getTitle());
        }

        @Override
        public int getItemCount() {
            return aTagList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public UUID _id;
            public TextView tvTitle;
            public ImageButton ibEdit;
            public ImageButton ibDelete;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                ibEdit = (ImageButton) itemView.findViewById(R.id.ibEdit);
                ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);

                ibEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, TagEditActivity.class);
                        intent.putExtra("ID", _id.toString());
                        intent.putExtra("Operation", "edit");
                        mContext.startActivity(intent);
                    }
                });

                ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Подтверждение удаления
                        //Удаление тега
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Подтверждение")
                                .setMessage("Вы уверены, что хотите удалить метку?")
                                //.setIcon(R.drawable.ic_android_cat)
                                .setCancelable(false)
                                .setNegativeButton("Нет",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                .setPositiveButton("Да",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteTag(_id);
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        }
    }
}
