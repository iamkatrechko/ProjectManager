package com.iamkatrechko.projectmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.projectmanager.BuildConfig;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItem;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems;

import java.util.ArrayList;
import java.util.List;

import static com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems.EXP_ITEM_HEADER;

/**
 * Created on 08.03.2017
 * author: iamkatrechko
 */
public class MainMenuAdapter extends BaseExpandableListAdapter {

    /** Список всех пунктов меню с их дочерними элементами */
    private List<ExpMenuItem> mMenuItems = new ArrayList<>();
    /** Контекст */
    private Context mContext;

    public MainMenuAdapter(Context context, List<ExpMenuItem> menuItems) {
        mContext = context;
        mMenuItems = menuItems;
    }

    @Override
    public int getGroupCount() {
        return mMenuItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mMenuItems.get(groupPosition).getChildItemCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mMenuItems;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mMenuItems.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (groupPosition == 0) {
                if (BuildConfig.isDeveloper) {
                    convertView = inflater.inflate(R.layout.navigation_view_header_debug, null);
                } else {
                    convertView = inflater.inflate(R.layout.navigation_view_header, null);
                }
            } else {
                convertView = inflater.inflate(R.layout.exp_list_group_view, null);
            }
        }

        ExpMenuItem menuItem = mMenuItems.get(groupPosition);
        if (menuItem.getExpMenuItem() == EXP_ITEM_HEADER) {
            //Настройка шапки
            //convertView.findViewById(R.id.linearMain).setBackgroundColor(color);
        } else {
            ImageView iconGroup = (ImageView) convertView.findViewById(R.id.image_view_icon);
            TextView textGroup = (TextView) convertView.findViewById(R.id.text_view_title);
            ImageView imageViewIndicator = (ImageView) convertView.findViewById(R.id.image_view_indicator);

            iconGroup.setImageResource(menuItem.getIconId());
            textGroup.setText(menuItem.getTitleId());
            imageViewIndicator.setVisibility(menuItem.existChildren() ? View.VISIBLE : View.GONE);

            if (isExpanded) {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_up);
            } else {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_down);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.text_view_title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_label);

        textChild.setText(mMenuItems.get(groupPosition).getChildren().get(childPosition).getTitle());
        imageView.setColorFilter(mMenuItems.get(groupPosition).getChildren().get(childPosition).getIconColor());
        if (mMenuItems.get(groupPosition).getExpMenuItem() == ExpMenuItems.MENU_ITEM_TAGS) {
            imageView.setImageResource(R.drawable.ic_label_outline);
        } else {
            imageView.setImageResource(R.drawable.ic_circle);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
