package com.iamkatrechko.projectmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.iamkatrechko.projectmanager.R;

import java.util.UUID;

/**
 * Диалог запроса на удаление подпроекта
 * @author iamkatrechko
 *         Date: 03.05.2017
 */
public class DialogDeleteSubProjectConfirm extends DialogFragment {

    public static final String EXTRA_SUB_PROJECT_ID = "EXTRA_SUB_PROJECT_ID";
    public static final String EXTRA_SUB_PROJECT_POSITION = "EXTRA_SUB_PROJECT_POSITION";

    /**
     * Возвращает новый инстанс фрагмента
     * @param subProjectId идентификатор удаляемого подпроекта
     * @return новый инстанс фрагмента
     */
    public static DialogDeleteSubProjectConfirm newInstance(UUID subProjectId, int position) {
        DialogDeleteSubProjectConfirm fragment = new DialogDeleteSubProjectConfirm();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SUB_PROJECT_ID, subProjectId);
        args.putInt(EXTRA_SUB_PROJECT_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(boolean delete) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtras(getArguments());
        intent.putExtra("delete", delete);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_sub_project_title)
                .setMessage(R.string.delete_sub_project_confirm)
                .setPositiveButton(R.string.result_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(true);
                            }
                        })
                .setNegativeButton(R.string.result_no, null).create();
    }
}
