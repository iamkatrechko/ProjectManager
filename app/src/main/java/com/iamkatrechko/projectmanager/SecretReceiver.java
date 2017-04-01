package com.iamkatrechko.projectmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iamkatrechko.projectmanager.activity.ServiceMenuActivity;

/**
 * @author iamkatrechko
 *         Date: 20.03.2017
 */
public class SecretReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            Intent i = new Intent(context, ServiceMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
