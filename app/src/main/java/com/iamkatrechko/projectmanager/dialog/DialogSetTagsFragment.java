package com.iamkatrechko.projectmanager.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.ArrayList;
import java.util.UUID;

public class DialogSetTagsFragment extends DialogFragment {

    private ArrayList<UUID> tTagsList = new ArrayList<>();

    public static DialogSetTagsFragment newInstance(ArrayList<UUID> tTagsList) {
        DialogSetTagsFragment fragment = new DialogSetTagsFragment();

        String[] mass = new String[tTagsList.size()];
        for (int i = 0; i < tTagsList.size(); i++) {
            mass[i] = tTagsList.get(i).toString();
        }

        Bundle args = new Bundle();
        args.putStringArray("TagsID", mass);
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(ArrayList<UUID> tagArrayList) {
        if (getTargetFragment() == null) {
            return;
        }
        String[] mass = new String[tagArrayList.size()];
        for (int i = 0; i < tagArrayList.size(); i++) {
            mass[i] = tagArrayList.get(i).toString();
        }

        Intent intent = new Intent();
        intent.putExtra("TagsID", mass);
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final ArrayList<Tag> tagList = ProjectLab.get(getActivity()).getTags();
        String[] tagListInTask = getArguments().getStringArray("TagsID");

        if (tagListInTask != null) {
            for (String s : tagListInTask) {
                tTagsList.add(UUID.fromString(s));
            }
        }

        final boolean[] tagListCheck = new boolean[tagList.size()];
        final String[] tagListString = new String[tagList.size()];
        for (int i = 0; i < tagList.size(); i++) {
            tagListString[i] = tagList.get(i).getTitle();
            tagListCheck[i] = tTagsList.contains(tagList.get(i).getID());
        }

        android.support.v7.app.AlertDialog.Builder builder;
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_tags)
                .setMultiChoiceItems(tagListString, tagListCheck,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                tagListCheck[which] = isChecked;
                            }
                        })

                .setPositiveButton(R.string.result_apply,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                for (int i = 0; i < tagListString.length; i++) {
                                    if (tagListCheck[i]) {
                                        if (!tTagsList.contains(tagList.get(i).getID())) tTagsList.add(tagList.get(i).getID());
                                    } else {
                                        tTagsList.remove(tagList.get(i).getID());
                                    }
                                }
                                sendResult(tTagsList);
                            }
                        })

                .setNegativeButton(R.string.result_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        return builder.create();
    }
}
