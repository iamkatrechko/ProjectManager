package com.iamkatrechko.projectmanager.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.iamkatrechko.projectmanager.entity.Tag;

/**
 * Диалог запроса на удаление
 * @author iamkatrechko
 *         Date: 10.03.2017
 */
public class DialogDeleteConfirm extends DialogFragment {

    /**
     * Возвращает новый инстанс фрагмента
     * @param title заголовок диалога
     * @param text  текст диалога
     * @return новый инстанс фрагмента
     */
    public static DialogDeleteConfirm newInstance(String title, String text, Tag tag) {
        DialogDeleteConfirm fragment = new DialogDeleteConfirm();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putParcelable("tag", tag);

        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(boolean delete) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("delete", delete);
        a.putExtra("tag", getArguments().getParcelable("tag"));
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        String title = getArguments().getString("title");
        String text = getArguments().getString("text");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(true);
                            }
                        })
                .setNegativeButton("Отмена", null).create();
    }
}
