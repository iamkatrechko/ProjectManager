package com.iamkatrechko.projectmanager.expandable_menu;

import android.support.annotation.DrawableRes;

import com.iamkatrechko.projectmanager.R;

/**
 * Список пунктов бокового меню
 * @author iamkatrechko
 *         Date: 07.03.2017
 */
public enum ExpMenuItems {

    /** Шапка меню */
    EXP_ITEM_HEADER("Шапка", R.drawable.ic_launcher),
    /** Список задач на сегодня */
    MENU_ITEM_TODAY("Сегодня", R.drawable.ic_today),
    /** Список задач на неделю */
    MENU_ITEM_WEEK("7+ дней", R.drawable.ic_event_note),
    /** Календарь со списком задач */
    MENU_ITEM_CALENDAR("Календарь", R.drawable.ic_date_range),
    /** Проекты */
    MENU_ITEM_PROJECTS("Проекты", R.drawable.ic_assignment),
    /** Управление проектами */
    MENU_ITEM_PROJECTS_EDIT("Управление проектами", R.drawable.ic_projects_edit),
    /** Метки */
    MENU_ITEM_TAGS("Метки", R.drawable.ic_label_outline),
    /** Управление метками */
    MENU_ITEM_TAGS_EDIT("Управление метками", R.drawable.ic_info),
    /** Фильтры */
    MENU_ITEM_FILTERS("Фильтры", R.drawable.ic_filter),
    /** Настройки */
    MENU_ITEM_SETTINGS("Настройки", R.drawable.ic_settings),
    /** О программе */
    MENU_ITEM_ABOUT("О программе", R.drawable.ic_info);

    /** Заголовок пункта меню */
    private String mTitle;
    /** Иконка пункта меню */
    private int mIconId;

    /**
     * Конструктор
     * @param title  заголовок пункта меню
     * @param iconId иконка пункта меню
     */
    ExpMenuItems(String title, @DrawableRes int iconId) {
        mTitle = title;
        mIconId = iconId;
    }

    /**
     * Возвращает заголовок пункта меню
     * @return заголовок пункта меню
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Возвращает идентификатор иконки пункта меню
     * @return идентификатор иконки пункта меню
     */
    public int getIconId() {
        return mIconId;
    }
}
