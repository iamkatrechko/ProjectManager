package com.iamkatrechko.projectmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Muxa on 29.02.2016.
 */
public class MyScheduledReceiver extends BroadcastReceiver {
    final public static String ACTION_RECEIVER_SET_DONE = "actionSetDone";
    final public static String ACTION_RECEIVER_SHOW_MESSAGE = "showMessage";

    ProjectLab lab;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = String.valueOf(intent.getAction());                                         //Обернул, чтобы null превращался в строку
        Log.d("onReceive", "Action: " + action);
        lab = ProjectLab.get(context);

        switch (action){
            case ACTION_RECEIVER_SET_DONE:
                setDone(context, intent);
                break;
            case ACTION_RECEIVER_SHOW_MESSAGE:
                showNotification(context, intent);
                break;
        }

    }

    private void showNotification(Context context, Intent intent){
        UUID id = UUID.fromString(intent.getStringExtra("ID"));
        //Task task = lab.getTaskOnAllLevel(id);
        Task task = new Task();
        task.setTitle("Задача");
        task.setDescription("Описание");
        task.setIsDone(false);
        task.setType(Task.TASK_TYPE_TASK);
        task.setDate("01.01.2001");
        task.setTime("11:11");
        task.setPriority(3);
        task.setIsRepeat(false);
        if (task == null){
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent scheduledIntent = new Intent(context, MainActivity.class);
        Intent scheduledIntent = new Intent();
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, scheduledIntent, 0);



        Intent intentSetDone = new Intent(context, MyScheduledReceiver.class);
        intentSetDone.putExtra("ID", String.valueOf(id));
        intentSetDone.setAction(ACTION_RECEIVER_SET_DONE);
        PendingIntent pIntentSetDone = PendingIntent.getBroadcast(context, id.hashCode(), intentSetDone, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_done)
                        //.setSmallIcon(R.drawable.anim_status_bar)                                         //Анимированное оповещение
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon))
                .setTicker("Название оповещения")// текст в строке состояния
                        //.setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(task.getTitle()) // Заголовок уведомления
                .addAction(0, "Выполнить", pIntentSetDone)
                .addAction(0, "Кнопка 2", contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setAutoCancel(true)
                .setContentText(task.getDescription()); // Текст уведомления

        Notification n = builder.getNotification();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        //Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //n.sound = ringURI;

        //long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };                             //Вибрация, пауза, вибрация...
        //n.vibrate = vibrate;

        nm.notify(id.hashCode(), n);
    }

    private void setDone(Context context, Intent intent){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        UUID id = UUID.fromString(intent.getStringExtra("ID"));
        nm.cancel(id.hashCode());
        Task task = lab.getTaskOnAllLevel(id);
        if (task != null){
            Log.d("MyScheduledReceiver", "setIsDone: задача найдена");
            task.setIsDone(true);
        }
        Log.d("MyScheduledReceiver", "setIsDone: задача не найдена");
    }
}