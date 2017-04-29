package com.iamkatrechko.projectmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Диалог подтверждения сохранения перед выходом
 * @author iamkatrechko
 *         Date: 10.03.2017
 */
public class DialogSaveChanges extends DialogFragment {

    /**
     * Возвращает новый инстанс фрагмента
     * @return новый инстанс фрагмента
     */
    public static DialogSaveChanges newInstance() {
        return new DialogSaveChanges();
    }

    private void sendResult(boolean delete) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("save_changes", delete);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Изменения не сохранены")
                .setMessage("Сохранить изменения перед выходом?")
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(true);
                            }
                        })
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(false);
                            }
                        })
                .setNeutralButton("Отмена", null)
                .create();
    }
}
