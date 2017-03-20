package com.iamkatrechko.projectmanager;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.iamkatrechko.projectmanager.entity.Project;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Muxa on 21.02.2016.
 */
public class ServiceMenu extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_menu);

        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button3:
                showNotification();
                return;
            case R.id.button4:
                ProjectLab.get(this).reloadData();
                return;
            case R.id.button5:
                ProjectLab.get(this).deleteAllData();
                return;
            case R.id.button6:
                ProjectLab.get(this).generateTags();
                return;
        }
    }

    public void showNotification(){
        NotificationManager nm;
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent scheduledIntent = new Intent(this, MainActivity.class);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                scheduledIntent, 0);

        Resources res = getResources();
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setTicker("Название оповещения")// текст в строке состояния
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle("Название контента") // Заголовок уведомления
                .setContentText("Текст оповещения"); // Текст
        // уведомления
        Notification n = builder.getNotification();

        nm.notify(1, n);
    }
}