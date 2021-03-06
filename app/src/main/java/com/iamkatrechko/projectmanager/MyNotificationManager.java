package com.iamkatrechko.projectmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.receiver.MyScheduledReceiver;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Muxa on 26.03.2016.
 */
public class MyNotificationManager {

    /** Тег для логирования */
    private static final String TAG = MyNotificationManager.class.getSimpleName();

    /** Контекст */
    private Context mContext;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Сервис будильников */
    private AlarmManager alarmManager;

    public MyNotificationManager(Context context) {
        mContext = context;
        lab = ProjectLab.get(context);
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Установка напоминания для заданной задачи
     * @param taskId идентификатор задачи для установки
     */
    public void addNotification(UUID taskId) {
        Task task = lab.getTaskOnAllLevel(taskId);
        deleteNotification(taskId);

        String date = task.getStringDate();
        String time = task.getTime();

        if (date.equals("null") || time.equals("null")) {
            Log.d(TAG, "Установка напоминания отменена (время не задано) - " + task.getTitle());
            return;
        }
        if (!task.getIsNotify()) {
            Log.d(TAG, "Установка напоминания отменена (напоминание выключено) - " + task.getTitle());
            return;
        }
        Log.d(TAG, "Установка напоминания - " + task.getTitle());

        Intent intent = new Intent(mContext, MyScheduledReceiver.class);
        intent.putExtra(MyScheduledReceiver.EXTRA_TASK_ID, taskId);
        intent.setAction(MyScheduledReceiver.ACTION_RECEIVER_SHOW_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, taskId.hashCode(), intent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMil(date, time), pendingIntent);
    }

    /**
     * Удаляет напоминание для заданной задачи
     * @param id mId задачи для удаления
     */
    public void deleteNotification(UUID id) {
        Log.d(TAG, "Удаление напоминания - " + lab.getTaskOnAllLevel(id).getTitle());

        Intent intent = new Intent(mContext, MyScheduledReceiver.class);
        intent.putExtra(MyScheduledReceiver.EXTRA_TASK_ID, id);
        intent.setAction(MyScheduledReceiver.ACTION_RECEIVER_SHOW_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id.hashCode(), intent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * Возвращает полученные дату и время в миллисекундах
     * @param date дата в формате "DD.MM.YYYY"
     * @param time время в формате "HH:MM"
     * @return полученное время в миллисекундах
     */
    private long getTimeInMil(String date, String time) {
        int year = Integer.valueOf(date.split("\\.")[2]);
        int month = Integer.valueOf(date.split("\\.")[1]) - 1;
        int day = Integer.valueOf(date.split("\\.")[0]);
        int hour = Integer.valueOf(time.split("\\:")[0]);
        int minute = Integer.valueOf(time.split("\\:")[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Log.d(TAG, "Установленное время: " + calendar.getTime());
        return calendar.getTimeInMillis();
    }
}
