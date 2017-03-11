package com.iamkatrechko.projectmanager.utils;

import com.iamkatrechko.projectmanager.adapter.TasksListAdapter;
import com.iamkatrechko.projectmanager.entity.Task;
import com.iamkatrechko.projectmanager.new_entity.TaskListItem;
import com.iamkatrechko.projectmanager.new_entity.DateLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита по работе с задачами
 * Created on 07.03.2017
 * author: iamkatrechko
 */
public class TasksUtils {

    /**
     * Добавляет метки дат перед задачами за каждый день
     * @param tasks список задач
     * @return список объектов для {@link TasksListAdapter} с метками дат
     */
    public static List<TaskListItem> addDateLabels(List<Task> tasks) {
        List<TaskListItem> result = new ArrayList<>();

        String date = "";
        for (Task task : tasks) {
            if (task.getViewType() == TasksListAdapter.ADAPTER_ITEM_TYPE_TASK) {
                if (task.getDate() != null) {
                    if (!date.equals(task.getDate())) {
                        DateLabel dateLabel = new DateLabel();
                        dateLabel.setCalendarString(task.getDate());
                        result.add(dateLabel);
                        date = task.getDate();
                    }

                    result.add(task);
                }
            }
        }

        return result;
    }
}
