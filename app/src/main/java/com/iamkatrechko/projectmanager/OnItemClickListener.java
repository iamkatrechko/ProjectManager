package com.iamkatrechko.projectmanager;

import com.iamkatrechko.projectmanager.new_entity.AbstractTaskObject;

/**
 * Created by Muxa on 03.03.2017.
 */
public interface OnItemClickListener {

    void onItemClick(int type, AbstractTaskObject item);
}
