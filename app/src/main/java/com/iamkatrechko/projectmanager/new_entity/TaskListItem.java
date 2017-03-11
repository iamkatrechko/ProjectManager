package com.iamkatrechko.projectmanager.new_entity;

/**
 * Сущность списка задач
 * @author iamkatrechko
 *         Date: 05.03.2017
 */
public interface TaskListItem {

    /**
     * Возвращает тип сущности для отображения в списке
     * @return тип сущности для отображения
     */
    int getViewType();
}