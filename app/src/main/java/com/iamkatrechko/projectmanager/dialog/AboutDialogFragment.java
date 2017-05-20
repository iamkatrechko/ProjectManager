package com.iamkatrechko.projectmanager.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.utils.Utils;

/**
 * Диалог с информацией о приложении
 * @author iamkatrechko
 *         Date: 20.05.2017
 */
public class AboutDialogFragment extends DialogFragment {

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, null);

        v.findViewById(R.id.button_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openGooglePlayApplication(getContext());
            }
        });
        v.findViewById(R.id.button_show_other_apps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openGooglePlayDeveloper(getContext());
            }
        });
        v.findViewById(R.id.button_send_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendMail(getContext(), "");
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
