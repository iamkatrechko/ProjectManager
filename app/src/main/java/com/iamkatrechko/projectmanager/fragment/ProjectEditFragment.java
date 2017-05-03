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
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Project;

import java.util.Random;
import java.util.UUID;

/**
 * Фрагмент создания/редактирования проекта
 * @author iamkatrechko
 *         Date: 26.02.2016
 */
public class ProjectEditFragment extends Fragment implements View.OnClickListener {

    private ProjectLab lab;

    private UUID ID;
    private String Operation;

    private Project tProject;

    private EditText etTitle;
    private ImageView imageViewCurrentColor;

    private int color;

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
        imageViewCurrentColor = (ImageView) v.findViewById(R.id.imageViewColor);

        if (Operation.equals("edit")) {
            getActivity().setTitle(R.string.activity_project_edit);
            tProject = lab.getProject(ID);
            etTitle.setText(tProject.getTitle());
            color = tProject.getColor();
            imageViewCurrentColor.setColorFilter(tProject.getColor());
        } else {
            getActivity().setTitle(R.string.activity_project_add);
            Random random = new Random();
            color = -random.nextInt(16777216) + 1;
            imageViewCurrentColor.setColorFilter(color);
        }

        v.findViewById(R.id.fab).setOnClickListener(this);
        v.findViewById(R.id.linearColor).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (etTitle.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), R.string.error_enter_project_title, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Operation.equals("edit")) {
                    tProject.setTitle(etTitle.getText().toString());
                    tProject.setColor(color);
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
                        .setPositiveButton(R.string.result_save, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                imageViewCurrentColor.setColorFilter(selectedColor);
                                color = selectedColor;
                            }
                        })
                        .build()
                        .show();
                break;
        }
    }
}
