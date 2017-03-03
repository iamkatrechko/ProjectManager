package com.iamkatrechko.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static int color;
    int theme;
    final static int NAV_MENU_POS_HEADER = 0;
    final static int NAV_MENU_POS_TODAY = 1;
    final static int NAV_MENU_POS_WEEK = 2;
    final static int NAV_MENU_POS_PROJECTS = 4;
    final static int NAV_MENU_POS_PROJECTS_EDIT = 5;
    final static int NAV_MENU_POS_TAGS = 6;
    final static int NAV_MENU_POS_FILTERS = 7;
    final static int NAV_MENU_POS_SETTINGS = 8;
    final static int NAV_MENU_POS_TAGS_EDIT = 9;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    Themes t;
    ExpListAdapter adapter;

    public static FragmentManager fragmentManager;
    public ArrayList<myGroupItem> listGroup;

    ExpandableListView listView;
    ProjectLab lab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = new Themes(this);
        theme = Integer.valueOf(Themes.getNumTheme());
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(t.getNumTheme()));
        setContentView(R.layout.activity_main);
        lab = ProjectLab.get(this);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        initToolBar();
        setSupportActionBar(toolbar);
        initNavigationView();

        UUID ID = listGroup.get(NAV_MENU_POS_PROJECTS).getList().get(0).getID();
        fragmentManager = getSupportFragmentManager();
        getProjectFragment(ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.service_menu) {
            Intent i = new Intent(this, ServiceMenu.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.scroll) {
            Intent i = new Intent(this, ScrollActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        // Находим наш list
        listView = (ExpandableListView) findViewById(R.id.exListView);
        String[] names = getResources().getStringArray(R.array.nav_draw_names);
        String[] icons = getResources().getStringArray(R.array.nav_draw_icons);

        listGroup = new ArrayList<myGroupItem>();
        //Генерация элементов бокового меню
        for (int i = 0; i < names.length; i++) {
            myGroupItem group = new myGroupItem();
            group.setText(names[i]);
            group.setIcon(icons[i]);
            if (i == NAV_MENU_POS_PROJECTS) {
                ArrayList<Project> projects = lab.getProjects();
                group.setList(projects);
            }
            if (i == NAV_MENU_POS_FILTERS){
                String[] list = getResources().getStringArray(R.array.filters);
                group.setListFilters(list);
            }
            if (i == NAV_MENU_POS_TAGS){
                ArrayList<Tag> tags = lab.getTags();
                group.setListTags(tags);
            }

            listGroup.add(group);
        }

        adapter = new ExpListAdapter(getApplicationContext(), listGroup);
        listView.setAdapter(adapter);
        listView.setItemChecked(NAV_MENU_POS_PROJECTS + 1, true);

        //При нажатии на пункт меню
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.d("MainActivity", "CheckedIndex: " + i);
                if (i != NAV_MENU_POS_PROJECTS) {
                    expandableListView.setItemChecked(i, true);
                }
                switch (i){
                    case NAV_MENU_POS_TODAY:
                        getFragment(NAV_MENU_POS_TODAY);
                        break;
                    case NAV_MENU_POS_WEEK:
                        getFragment(NAV_MENU_POS_WEEK);
                        break;
                    case NAV_MENU_POS_PROJECTS_EDIT:
                        getFragment(NAV_MENU_POS_PROJECTS_EDIT);
                        break;
                    case NAV_MENU_POS_SETTINGS:
                        Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(intent);
                        break;
                    case NAV_MENU_POS_TAGS_EDIT:
                        getFragment(NAV_MENU_POS_TAGS_EDIT);
                        break;
                }
                return false;
            }
        });

        //Нажатие на проект
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i1));
                expandableListView.setItemChecked(index, true);
                Log.d("MainActivity", "CheckedIndex: " + index);
                switch (i){
                    case NAV_MENU_POS_PROJECTS:
                        final UUID _id = listGroup.get(i).getList().get(i1).getID();
                        getProjectFragment(_id);
                        return true;
                    case NAV_MENU_POS_FILTERS:
                        getFilterFragment(i1);
                        return true;
                    case NAV_MENU_POS_TAGS:
                        UUID id = listGroup.get(i).getListTags().get(i1).getID();
                        getTagFragment(id);
                        return true;
                }
                return true;
            }
        });
    }

    public void getProjectFragment(UUID _id) {
        drawerLayout.closeDrawers();
        fragmentManager.beginTransaction()
                .replace(R.id.container, TasksListFragment.newInstance(_id, "Project"))
                .commit();
        //setTitle(R.string.title_section1);
    }

    public void getFilterFragment(int filterType){
        drawerLayout.closeDrawers();
        switch (filterType){
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilterTasksListFragment.newInstance(0))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilterTasksListFragment.newInstance(1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilterTasksListFragment.newInstance(2))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilterTasksListFragment.newInstance(3))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilterTasksListFragment.newInstance(4))
                        .commit();
                break;
        }

    }

    public void getTagFragment(UUID id) {
        drawerLayout.closeDrawers();
        fragmentManager.beginTransaction()
                .replace(R.id.container, TasksListByTagFragment.newInstance(id))
                .commit();
        //setTitle(R.string.title_section1);
    }

    public void getFragment(int i){
        drawerLayout.closeDrawers();
        switch (i){
            case NAV_MENU_POS_TODAY:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListTodayFragment.newInstance())
                        .commit();
                break;
            case NAV_MENU_POS_WEEK:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListOfWeekFragment.newInstance())
                        .commit();
                break;
            case NAV_MENU_POS_PROJECTS_EDIT:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProjectsListFragment.newInstance())
                        .commit();
                break;
            case NAV_MENU_POS_TAGS_EDIT:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TagsListFragment.newInstance())
                        .commit();
        }
    }

    public static class ExpListAdapter extends BaseExpandableListAdapter {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private ArrayList<myGroupItem> mGroups;
        private Context mContext;
        //private Activity mActivity;

        public ExpListAdapter (Context context, ArrayList<myGroupItem> groups){
            mContext = context;
            mGroups = groups;
        }

        @Override
        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case NAV_MENU_POS_PROJECTS:
                    return mGroups.get(groupPosition).getList().size();
                case NAV_MENU_POS_FILTERS:
                    return mGroups.get(groupPosition).getListFilters().length;
                case NAV_MENU_POS_TAGS:
                    return mGroups.get(groupPosition).getListTags().size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition){
                case NAV_MENU_POS_PROJECTS:
                    return mGroups.get(groupPosition).getList();
                case NAV_MENU_POS_FILTERS:
                    return mGroups.get(groupPosition).getListFilters();
                case NAV_MENU_POS_TAGS:
                    return mGroups.get(groupPosition).getListTags();
            }
            return null;
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
                if (groupPosition == NAV_MENU_POS_HEADER){
                    convertView = inflater.inflate(R.layout.navigation_view_header, null);
                }else{
                    convertView = inflater.inflate(R.layout.exp_list_group_view, null);
                }
            }

            if (groupPosition == NAV_MENU_POS_HEADER) {
                //Настройка шапки
                //convertView.findViewById(R.id.linearMain).setBackgroundColor(color);
            }else{
                ImageView imageViewIndicator = (ImageView) convertView.findViewById(R.id.imageViewIndicator);

                //Показ индикатора при наличие подсписка
                if (groupPosition == NAV_MENU_POS_PROJECTS ||
                        groupPosition == NAV_MENU_POS_FILTERS ||
                        groupPosition == NAV_MENU_POS_TAGS) {
                    imageViewIndicator.setVisibility(View.VISIBLE);
                }

                if (isExpanded) {
                    //Изменяем что-нибудь, если текущая Group раскрыта
                    imageViewIndicator.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    //Изменяем что-нибудь, если текущая Group скрыта
                    imageViewIndicator.setImageResource(R.drawable.ic_arrow_down);
                }

                TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
                textGroup.setText(mGroups.get(groupPosition).getText());

                ImageView iconGroup = (ImageView) convertView.findViewById(R.id.iconGroup);
                iconGroup.setImageResource(mContext.getResources().getIdentifier(mGroups.get(groupPosition).getIcon(), "drawable", mContext.getPackageName()));
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

            TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
            switch (groupPosition){
                case NAV_MENU_POS_PROJECTS:
                    textChild.setText(mGroups.get(groupPosition).getList().get(childPosition).getTitle());

                    ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    imageView.setColorFilter(mGroups.get(groupPosition).getList().get(childPosition).getColor());
                    break;
                case NAV_MENU_POS_FILTERS:
                    textChild.setText(mGroups.get(groupPosition).getListFilters()[childPosition]);

                    //ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    //imageView.setColorFilter(mGroups.get(groupPosition).getList().get(childPosition).getColor());
                    break;
                case NAV_MENU_POS_TAGS:
                    textChild.setText(mGroups.get(groupPosition).getListTags().get(childPosition).getTitle());
                    break;
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theme != Integer.valueOf(Themes.getNumTheme())){
            qChangeTheme(Integer.valueOf(Themes.getNumTheme()));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProjectLab lab = ProjectLab.get(this);
        lab.saveProjectsIntoJSON();
    }

    public void qChangeTheme(Integer num){
        t.setNumTheme(String.valueOf(num));
        Utils.changeToTheme(this, num);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Объект списка меню////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class myGroupItem {

        final static String GROUP_ITEM_TYPE_HEADER = "header";
        final static String GROUP_ITEM_TYPE_GROUP = "group";
        final static String GROUP_ITEM_TYPE_SEPARATOR = "separatop";

        private String type;
        private String icon;
        private String text;
        private ArrayList<Project> list;
        private ArrayList<Tag> listTags;
        private String[] listFilters;

        public myGroupItem(){
            list = new ArrayList<Project>();
            listFilters = new String[]{""};
            listTags = new ArrayList<Tag>();
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ArrayList<Project> getList() {
            return list;
        }

        public void setList(ArrayList<Project> list) {
            this.list = list;
        }

        public String[] getListFilters() {
            return listFilters;
        }

        public void setListFilters(String[] listFilters) {
            this.listFilters = listFilters;
        }

        public ArrayList<Tag> getListTags() {
            return listTags;
        }

        public void setListTags(ArrayList<Tag> listTags) {
            this.listTags = listTags;
        }
    }
}
