package com.iamkatrechko.projectmanager.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.activity.MainActivity;
import com.iamkatrechko.projectmanager.activity.TagEditActivity;
import com.iamkatrechko.projectmanager.adapter.TagListAdapter;
import com.iamkatrechko.projectmanager.dialog.DialogDeleteConfirm;
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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DialogDeleteConfirm fragmentDialog = DialogDeleteConfirm.newInstance(getString(R.string.warning), getString(R.string.tag_delete), tag);
                fragmentDialog.setTargetFragment(TagsListFragment.this, 125125);
                fragmentDialog.show(fragmentManager, "DIALOG_DELETE_CONFIRM");
            }

            @Override
            public void onEditClick(Tag tag) {
                Intent intent = TagEditActivity.getIntent(getActivity(), tag);
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        AddFloatingActionButton button = (AddFloatingActionButton) v.findViewById(R.id.action_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TagEditActivity.getIntent(getActivity(), null);
                startActivity(intent);
            }
        });

        return v;
    }

    public int getTagPosition(Tag tag) {
        return mTagsList.indexOf(tag);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 125125) {
            boolean delete = data.getBooleanExtra("delete", false);
            if (delete) {
                Tag tag = data.getParcelableExtra("tag");
                adapter.notifyItemRemoved(getTagPosition(tag));
                lab.deleteTag(tag);
                ((MainActivity) getActivity()).initMenuItems();
            }
        }
    }
}
