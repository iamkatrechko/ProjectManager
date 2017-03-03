package com.iamkatrechko.projectmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.UUID;

/**
 * Created by Muxa on 12.04.2016.
 */
public class TagEditFragment extends Fragment implements View.OnClickListener {
    ProjectLab lab;
    private UUID ID;
    private String Operation;
    EditText etTitle;

    private Tag tag;
    public static TagEditFragment newInstance(String ID, String operation){
        TagEditFragment fragment = new TagEditFragment();

        Bundle args = new Bundle();
        args.putString("mId", ID);
        args.putString("Operation", operation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lab = ProjectLab.get(getActivity());

        String checkID = getArguments().getString("mId");
        if (!checkID.equals("0")) {
            ID = UUID.fromString(checkID);
        }
        Operation = getArguments().getString("Operation");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tag_edit, parent, false);

        etTitle = (EditText) v.findViewById(R.id.etTitle);

        if (Operation.equals("edit")){
            //getActivity().setTitle(R.string.activity_project_edit);
            tag = lab.getTagByID(ID);
            etTitle.setText(tag.getTitle());
        }else{
            //getActivity().setTitle(R.string.activity_project_add);
        }

        v.findViewById(R.id.buttonSave).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSave:
                if (Operation.equals("edit")) {
                    tag.setTitle(etTitle.getText().toString());
                } else {
                    Tag tag = new Tag();
                    tag.setTitle(etTitle.getText().toString());
                    lab.getTags().add(tag);
                }
                getActivity().finish();
                break;
        }
    }
}
