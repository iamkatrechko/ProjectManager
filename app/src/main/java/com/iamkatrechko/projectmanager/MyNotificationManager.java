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

    private Context mContext;
    private ProjectLab lab;

    public MyNotificationManager(Context context){
        mContext = context;
        lab = ProjectLab.get(context);
    }

    /**
     * Установка напоминания для заданной задачи
     * @param id mId задачи для установки
     */
    public void addNotification(UUID id){
        Task task = lab.getTaskOnAllLevel(id);
        String date = task.getStringDate();
        String time = task.getTime();

        deleteNotification(id);
        if (time.equals("null")){
            Log.d("MyNotificationManager", "Установка напоминания отменена - " + task.getTitle());
            return;
        }
        if (!task.getIsNotify()){
            Log.d("MyNotificationManager", "Установка напоминания отменена - " + task.getTitle());
            return;
        }

        Intent intent = new Intent(mContext, MyScheduledReceiver.class);
        intent.putExtra("mId", String.valueOf(id));
        intent.setAction(MyScheduledReceiver.ACTION_RECEIVER_SHOW_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, generateRequestCode(id), intent, PendingIntent.FLAG_ONE_SHOT);

        Log.d("MyNotificationManager", "Установка напоминания - " + task.getTitle());
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMil(date, time), pendingIntent);
    }

    /**
     * Удаляет напоминание для заданной задачи
     * @param id mId задачи для удаления
     */
    public void deleteNotification(UUID id){
        Log.d("MyNotificationManager", "Удаление напоминания - " + lab.getTaskOnAllLevel(id).getTitle());

        Intent intent = new Intent(mContext, MyScheduledReceiver.class);
        intent.putExtra("mId", String.valueOf(id));
        intent.setAction(MyScheduledReceiver.ACTION_RECEIVER_SHOW_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, generateRequestCode(id), intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private int generateRequestCode(UUID id){
        return id.hashCode();
    }

    /**
     * Возвращает полученные дату и время в миллисекундах
     * @param date дата в формате "DD.MM.YYYY"
     * @param time время в формате "HH:MM"
     * @return полученное время в миллисекундах
     */
    public long getTimeInMil(String date, String time){
        int year = Integer.valueOf(date.split("\\.")[2]);
        int month = Integer.valueOf(date.split("\\.")[1]) - 1;
        int day = Integer.valueOf(date.split("\\.")[0]);
        int hour = Integer.valueOf(time.split("\\:")[0]);
        int minute =  Integer.valueOf(time.split("\\:")[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Log.d("MyNotificationManager", "Установленное время: " + calendar.getTime());
        return calendar.getTimeInMillis();
    }
}
