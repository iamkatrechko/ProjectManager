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
    EXP_ITEM_HEADER(R.string.app_name, R.drawable.ic_launcher),
    /** Список задач на сегодня */
    MENU_ITEM_TODAY(R.string.nav_menu_today, R.drawable.ic_today),
    /** Список задач на неделю */
    MENU_ITEM_WEEK(R.string.nav_menu_week, R.drawable.ic_event_note),
    /** Календарь со списком задач */
    MENU_ITEM_CALENDAR(R.string.nav_menu_calendar, R.drawable.ic_date_range),
    /** Проекты */
    MENU_ITEM_PROJECTS(R.string.nav_menu_projects, R.drawable.ic_assignment),
    /** Управление проектами */
    MENU_ITEM_PROJECTS_EDIT(R.string.nav_menu_projects_edit, R.drawable.ic_projects_edit),
    /** Метки */
    MENU_ITEM_TAGS(R.string.nav_menu_tags, R.drawable.ic_label_outline),
    /** Управление метками */
    MENU_ITEM_TAGS_EDIT(R.string.nav_menu_tags_edit, R.drawable.ic_info),
    /** Фильтры */
    MENU_ITEM_FILTERS(R.string.nav_menu_filters, R.drawable.ic_filter),
    /** Настройки */
    MENU_ITEM_SETTINGS(R.string.nav_menu_settings, R.drawable.ic_settings),
    /** О программе */
    MENU_ITEM_ABOUT(R.string.nav_menu_info, R.drawable.ic_info);

    /** Заголовок пункта меню */
    private int mTitleId;
    /** Иконка пункта меню */
    private int mIconId;

    /**
     * Конструктор
     * @param title  заголовок пункта меню
     * @param iconId иконка пункта меню
     */
    ExpMenuItems(int title, @DrawableRes int iconId) {
        mTitleId = title;
        mIconId = iconId;
    }

    /**
     * Возвращает заголовок пункта меню
     * @return заголовок пункта меню
     */
    public int getTitleId() {
        return mTitleId;
    }

    /**
     * Возвращает идентификатор иконки пункта меню
     * @return идентификатор иконки пункта меню
     */
    public int getIconId() {
        return mIconId;
    }
}
