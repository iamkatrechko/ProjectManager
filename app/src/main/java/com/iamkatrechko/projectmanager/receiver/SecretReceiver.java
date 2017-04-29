package com.iamkatrechko.projectmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iamkatrechko.projectmanager.activity.ServiceMenuActivity;

/**
 * Секретный ресивер, отображающий меню разработчика при вводе послежовательности символов в номеронабирателе
 * *#*#66666#*#*
 * @author iamkatrechko
 *         Date: 20.03.2017
 */
public class SecretReceiver extends BroadcastReceiver {

    private static final String ACTION_SECRET_CODE = "android.provider.Telephony.SECRET_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SECRET_CODE)) {
            Intent i = new Intent(context, ServiceMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
