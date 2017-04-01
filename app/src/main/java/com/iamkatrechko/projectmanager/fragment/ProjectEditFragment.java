package com.iamkatrechko.projectmanager.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Muxa on 26.02.2016.
 */
public class ProjectEditFragment extends Fragment implements View.OnClickListener {
    ProjectLab lab;

    private UUID ID;
    private String Operation;

    private Project tProject;

    private EditText etTitle;
    private ImageView ivColor;

    int color;

    public static ProjectEditFragment newInstance(String ID, String operation) {
        ProjectEditFragment fragment = new ProjectEditFragment();
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
            ID = UUID.fromString(getArguments().getString("mId"));
        }
        Operation = getArguments().getString("Operation");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_project_edit, parent, false);

        etTitle = (EditText) v.findViewById(R.id.editTextTitle);
        ivColor = (ImageView) v.findViewById(R.id.imageViewColor);

        if (Operation.equals("edit")) {
            getActivity().setTitle(R.string.activity_project_edit);
            tProject = lab.getProject(ID);
            etTitle.setText(tProject.getTitle());
            ivColor.setColorFilter(tProject.getColor());
        } else {
            getActivity().setTitle(R.string.activity_project_add);
            Random random = new Random();
            color = -random.nextInt(16777216) + 1;
            ivColor.setColorFilter(color);
        }

        v.findViewById(R.id.buttonSave).setOnClickListener(this);
        v.findViewById(R.id.linearColor).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSave:
                if (Operation.equals("edit")) {
                    tProject.setTitle(etTitle.getText().toString());
                    //Color color = ivColor.getColorFilter();
                } else {
                    Project project = new Project(etTitle.getText().toString());
                    project.setColor(color);
                    lab.getProjects().add(project);
                }
                getActivity().finish();
                return;
            case R.id.linearColor:
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .setTitle(R.string.choose_color)
                        .initialColor(Color.RED)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(18)
                        .lightnessSliderOnly()
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton(R.string.result_ok, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton(R.string.result_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                break;
        }
    }
}
