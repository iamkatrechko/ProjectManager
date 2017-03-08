package com.iamkatrechko.projectmanager.expandable_menu;

import android.support.annotation.DrawableRes;

import com.iamkatrechko.projectmanager.R;

/**
 * Created on 07.03.2017
 *         author: iamkatrechko
 */
public enum ExpMenuItems {

    /** Шапка меню */
    EXP_ITEM_HEADER("Шапка", R.drawable.ic_launcher),
    /** Список задач на сегодня */
    MENU_ITEM_TODAY("Сегодня", R.drawable.ic_today),
    MENU_ITEM_WEEK("7+ дней", R.drawable.ic_event_note),
    MENU_ITEM_CALENDAR("Календарь", R.drawable.ic_date_range),
    MENU_ITEM_PROJECTS("Проекты", R.drawable.ic_assignment),
    MENU_ITEM_PROJECTS_EDIT("Управление проектами", R.drawable.ic_projects_edit),
    MENU_ITEM_TAGS("Метки", R.drawable.ic_label_outline),
    MENU_ITEM_FILTERS("Фильтры", R.drawable.ic_label),
    MENU_ITEM_SETTINGS("Настройки", R.drawable.ic_settings),
    MENU_ITEM_ABOUT("О программе", R.drawable.ic_info),
    MENU_ITEM_TAGS_EDIT("Настройки тегов", R.drawable.ic_info);

    private String mTitle;
    private int mIconId;

    ExpMenuItems(String title, @DrawableRes int iconId) {
        mTitle = title;
        mIconId = iconId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIconId() {
        return mIconId;
    }
}
