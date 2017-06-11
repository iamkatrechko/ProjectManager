package com.iamkatrechko.projectmanager.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.entity.Task;

import java.util.UUID;

/**
 * Created by Muxa on 29.02.2016.
 */
public class MyScheduledReceiver extends BroadcastReceiver {

    /** Тег для логирования */
    private static final String TAG = MyScheduledReceiver.class.getSimpleName();

    /** Действие выполнения задачи */
    public static final String ACTION_RECEIVER_SET_DONE = "ACTION_RECEIVER_SET_DONE";
    /** Действие отображение уведомления о выполнении задачи */
    public static final String ACTION_RECEIVER_SHOW_MESSAGE = "ACTION_RECEIVER_SHOW_MESSAGE";

    /** Вложенные данные. Идентификатор задачи */
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Менеджер уведомлений */
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: action: " + intent.getAction());
        if (intent.getAction() == null) {
            return;
        }
        lab = ProjectLab.get(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        UUID id = (UUID) intent.getSerializableExtra(EXTRA_TASK_ID);
        switch (intent.getAction()) {
            case ACTION_RECEIVER_SET_DONE:
                setDone(id);
                break;
            case ACTION_RECEIVER_SHOW_MESSAGE:
                showNotification(context, id);
                break;
        }
    }

    /**
     * Отображает уведомления о задаче
     * @param context контекст
     * @param taskId  идентификатор задачи
     */
    private void showNotification(Context context, UUID taskId) {
        Task task = lab.getTaskOnAllLevel(taskId);
        if (task == null) {
            return;
        }

        Intent intentSetDone = new Intent(context, MyScheduledReceiver.class);
        intentSetDone.putExtra(EXTRA_TASK_ID, taskId);
        intentSetDone.setAction(ACTION_RECEIVER_SET_DONE);
        PendingIntent pIntentSetDone = PendingIntent.getBroadcast(context, taskId.hashCode(), intentSetDone, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_done)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon))
                .setContentTitle(task.getTitle())
                .addAction(0, context.getString(R.string.action_done), pIntentSetDone)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentText(task.getDescription());

        notificationManager.notify(taskId.hashCode(), builder.build());
    }

    /**
     * Помечает задачу как выполненную
     * @param taskId идентификатор задачи
     */
    private void setDone(UUID taskId) {
        Log.d(TAG, "Выполнение задачи:");
        notificationManager.cancel(taskId.hashCode());
        lab.removeTaskByID(taskId);
        Log.d(TAG, "Задача выполнена");
    }
}