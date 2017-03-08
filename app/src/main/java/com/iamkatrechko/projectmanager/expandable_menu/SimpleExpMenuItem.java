package com.iamkatrechko.projectmanager.expandable_menu;

/**
 * Created on 07.03.2017
 * author: iamkatrechko
 */
public class SimpleExpMenuItem extends AbstractExpMenuItem {

    public SimpleExpMenuItem(ExpMenuItems expMenuItems) {
        super(expMenuItems);
    }

    @Override
    public boolean existChildren() {
        return false;
    }
}
