package com.iamkatrechko.projectmanager.expandable_menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс элемента списка главного бокового меню
 * Created on 07.03.2017
 * author: iamkatrechko
 */
public class ExpMenuItem {

    /** Элемент меню из {@link Enum} */
    private ExpMenuItems mExpMenuItem;
    /** Список вложенных пунктов меню */
    private List<ChildItem> mChildItems = new ArrayList<>();

    /**
     * Конструктор
     * @param expMenuItem enum-элемент списка меню
     */
    public ExpMenuItem(ExpMenuItems expMenuItem) {
        mExpMenuItem = expMenuItem;
    }

    /**
     * Содержит ли элемент вложенные пункты меню
     * @return {@code true} - содержит, {@code false} - не содержит
     */
    public boolean existChildren() {
        return mChildItems.size() != 0;
    }

    /**
     * Возвращает тип элемента меню
     * @return тип элемента списка
     */
    public ExpMenuItems getExpMenuItem() {
        return mExpMenuItem;
    }

    /**
     * Возвращает идентификатор заголовка
     * @return имя элемента меню
     */
    public int getTitleId() {
        return mExpMenuItem.getTitleId();
    }

    /**
     * Возвращает id иконки элемента меню
     * @return id иконки элемента меню
     */
    public int getIconId() {
        return mExpMenuItem.getIconId();
    }

    /**
     * Возвращает количество вложенных пунктов
     * @return количество вложенных пунктов
     */
    public int getChildItemCount() {
        return mChildItems.size();
    }

    public List<ChildItem> getChildren() {
        return mChildItems;
    }

    /**
     * Добавить вложенный пункт меню
     * @param childItem вложенный пункт меню
     */
    public void addChildItem(ChildItem childItem) {
        mChildItems.add(childItem);
    }

    /** Вложенные пункты меню */
    public static class ChildItem {

        /** Заголовок пункта меню */
        private String mTitle;
        /** Цвет иконки пункта меню */
        private int mIconColor;

        /**
         * Конструктор
         * @param title     заголовок пункта меню
         * @param iconColor цвет иконки пункта меню
         */
        public ChildItem(String title, int iconColor) {
            mTitle = title;
            mIconColor = iconColor;
        }

        /**
         * Возвращает заголовок вложенного пункта меню
         * @return заголовок вложенного пункта меню
         */
        public String getTitle() {
            return mTitle;
        }

        /**
         * Возвращает цвет иконки вложенного пункта меню
         * @return цвет иконки вложенного пункта меню
         */
        public int getIconColor() {
            return mIconColor;
        }
    }
}