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

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.adapter.TagListAdapter;
import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.ArrayList;

public class TagsListFragment extends Fragment {
    private ProjectLab lab;
    private ArrayList<Tag> mTagsList = new ArrayList<>();
    private TagListAdapter adapter;

    public static TagsListFragment newInstance() {
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
        adapter = new TagListAdapter(mTagsList);
        adapter.setOnClickListener(new TagListAdapter.OnTagItemClickListener() {
            @Override
            public void onDeleteClick(Tag tag) {
                //Подтверждение удаления
                //Удаление тега
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                        /*Tag tag = ProjectLab.get(getActivity()).getTagByID(id);
                                        int pos = mTagsList.indexOf(tag);
                                        mTagsList.remove(tag);
                                        notifyItemRemoved(pos);*/
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onEditClick(Tag tag) {
                Intent intent = new Intent(getActivity(), TagEditActivity.class);
                intent.putExtra("mId", tag.getID().toString());
                intent.putExtra("Operation", "edit");
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplication()));

        AddFloatingActionButton button = (AddFloatingActionButton) v.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TagEditActivity.class);
                intent.putExtra("mId", "0");
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
}
