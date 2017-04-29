package com.iamkatrechko.projectmanager.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.dialog.DialogSaveChanges;
import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.UUID;

/**
 * Фрагмент создания/редактирования тега
 * @author iamkatrechko
 *         Date: 12.04.2016
 */
public class TagEditFragment extends Fragment {
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Текстовое поле с заголовком тега */
    private EditText etTitle;
    /** Копия первоначального тега */
    private Tag initialTag;
    /** Копия редактируемого тега */
    private Tag tag;
    /** Открыто ли окно на создание нового тега */
    private boolean isNew = true;
    /** Идентификатор редактируемого тега */
    private UUID tagId;

    public static TagEditFragment newInstance(Tag tag) {
        TagEditFragment fragment = new TagEditFragment();

        Bundle args = new Bundle();
        args.putParcelable("tag", tag);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lab = ProjectLab.get(getActivity());

        tag = getArguments().getParcelable("tag");
        isNew = tag == null;
        if (tag != null) {
            tagId = tag.getID();
            tag = Tag.copyFromAnotherTag(tag);
        } else {
            tag = new Tag("");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_edit, parent, false);
        etTitle = (EditText) view.findViewById(R.id.etTitle);

        etTitle.setText(tag.getTitle());
        initialTag = Tag.copyFromAnotherTag(tag);

        view.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentTag();
            }
        });
        return view;
    }

    /**
     * Проверяет, был ли редактирован тэг
     * @return {@code true} - тег редактирован, {@code false} - тег не редактирован
     */
    private boolean isChange() {
        setContent();
        if (tag.getTitle().equals(initialTag.getTitle())) {
            return false;
        } else {
            return true;
        }
    }

    /** Установить все значения с виджетов в буфер-сущность */
    private void setContent() {
        tag.setTitle(etTitle.getText().toString());
    }

    public void onBackPressed() {
        if (isChange()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            DialogSaveChanges fragmentDialog = DialogSaveChanges.newInstance();
            fragmentDialog.setTargetFragment(TagEditFragment.this, 632232);
            fragmentDialog.show(fragmentManager, "DIALOG_SAVE_CHANGES");
        } else {
            getActivity().finish();
        }
    }

    /** Сохраняет текущий тег из буфер-сущности */
    private void saveCurrentTag() {
        setContent();
        if (!isNew) {
            lab.saveTag(tagId, tag);
        } else {
            lab.addNewTag(tag);
        }
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 632232) {
            boolean save = data.getBooleanExtra("save_changes", false);
            if (save) {
                saveCurrentTag();
                Toast.makeText(getActivity(), "Сохранено", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().finish();
            }
        }
    }
}