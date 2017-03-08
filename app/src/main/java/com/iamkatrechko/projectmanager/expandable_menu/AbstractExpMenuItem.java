package com.iamkatrechko.projectmanager.expandable_menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый абстрактный класс элемента главного списка элементов меню
 * Created on 07.03.2017
 * author: iamkatrechko
 */
public class AbstractExpMenuItem {

    private ExpMenuItems mExpMenuItem;

    private List<ChildItem> mChildItems = new ArrayList<>();

    public AbstractExpMenuItem(ExpMenuItems expMenuItem) {
        mExpMenuItem = expMenuItem;
    }

    /**
     * Содержит ли элемент дочерние элементы
     * @return {@code true} - содержит, {@code false} - не содержит
     */
    public boolean existChildren(){
        return mChildItems.size() != 0;
    }

    /**
     * Возвращает тип элемента списка
     * @return тип элемента списка
     */
    public ExpMenuItems getExpMenuItem() {
        return mExpMenuItem;
    }

    /**
     * Возвращает имя элемента меню
     * @return имя элемента меню
     */
    public String getTitle() {
        return mExpMenuItem.getTitle();
    }

    /**
     * Возвращает id иконки элемента меню
     * @return id иконки элемента меню
     */
    public int getIconId() {
        return mExpMenuItem.getIconId();
    }

    /**
     * Возвращает количество дочерних элементов
     * @return количество дочерних элементов
     */
    public int getChildItemCount() {
        return mChildItems.size();
    }

    public List<ChildItem> getChilders() {
        return mChildItems;
    }

    public void addChildItem(ChildItem childItem) {
        mChildItems.add(childItem);
    }

    public static class ChildItem {
        private String mTitle;
        private int mIconColor;

        public ChildItem(String title, int iconColor) {
            mTitle = title;
            mIconColor = iconColor;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getIconColor() {
            return mIconColor;
        }
    }
}