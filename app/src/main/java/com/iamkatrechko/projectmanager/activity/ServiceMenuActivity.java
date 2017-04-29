package com.iamkatrechko.projectmanager.activity;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;

/**
 * Меню разработчика
 * Created by Muxa on 21.02.2016.
 */
public class ServiceMenuActivity extends AppCompatActivity implements View.OnClickListener {

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
                break;
            case R.id.button4:
                ProjectLab.get(this).reloadData();
                break;
            case R.id.button5:
                ProjectLab.get(this).deleteAllData();
                break;
            case R.id.button6:
                ProjectLab.get(this).generateTags();
                break;
        }
    }

    public void showNotification() {
        NotificationManager nm;
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent scheduledIntent = new Intent(this, MainActivity.class);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                scheduledIntent, 0);

        Resources res = getResources();
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_notification_icon))
                .setTicker("Название оповещения")// текст в строке состояния
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle("Название контента") // Заголовок уведомления
                .setContentText("Текст оповещения"); // Текст
        // уведомления
        Notification n = builder.getNotification();

        nm.notify(1, n);
    }
}