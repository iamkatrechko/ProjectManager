package com.iamkatrechko.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import com.iamkatrechko.projectmanager.adapter.MainMenuAdapter;
import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItem;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems.MENU_ITEM_PROJECTS;

public class MainActivity extends AppCompatActivity {

    /** Основной тулбра */
    private Toolbar toolbar;
    /** Главный лэйаут */
    private DrawerLayout drawerLayout;
    /** Менеджер фрагментов */
    private FragmentManager fragmentManager;
    /** Класс по работе с проектами и задачами */
    private ProjectLab lab;
    /** Двухуровневый виджет-список главного меню */
    private ExpandableListView mExpandableListView;
    /** Адаптер списка главного меню */
    private MainMenuAdapter adapter;
    /** Список элеметов главного меню */
    private List<ExpMenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Themes(this);
        lab = ProjectLab.get(this);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_main);

        initToolBar();
        setSupportActionBar(toolbar);
        initNavigationView();

        fragmentManager = getSupportFragmentManager();
        //UUID ID = lab.getProjects().get(0).getID();
        //getProjectFragment(ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.service_menu:
                intent = new Intent(this, ServiceMenu.class);
                startActivity(intent);
                return true;
            case R.id.scroll:
                intent = new Intent(this, ScrollActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initToolBar() {
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

        mExpandableListView = (ExpandableListView) findViewById(R.id.exListView);

        menuItems = new ArrayList<>();
        for (ExpMenuItems expMenuItems : ExpMenuItems.values()) {
            ExpMenuItem item = new ExpMenuItem(expMenuItems);
            switch (expMenuItems) {
                case MENU_ITEM_PROJECTS:
                    ArrayList<Project> projects = lab.getProjects();
                    for (Project project : projects) {
                        item.addChildItem(new ExpMenuItem.ChildItem(project.getTitle(), project.getColor()));
                    }
                    break;
                case MENU_ITEM_FILTERS:
                    String[] list = getResources().getStringArray(R.array.filters);
                    for (String filter : list) {
                        item.addChildItem(new ExpMenuItem.ChildItem(filter, Color.BLACK));
                    }
                    break;
                case MENU_ITEM_TAGS:
                    ArrayList<Tag> tags = lab.getTags();
                    for (Tag tag : tags) {
                        item.addChildItem(new ExpMenuItem.ChildItem(tag.getTitle(), Color.BLACK));
                    }
                    break;
            }

            menuItems.add(item);
        }
        //adapter = new ExpListAdapter(getApplicationContext(), listGroup);
        adapter = new MainMenuAdapter(this, menuItems);
        mExpandableListView.setAdapter(adapter);
        mExpandableListView.setItemChecked(MENU_ITEM_PROJECTS.ordinal(), true);

        //При нажатии на пункт меню
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                ExpMenuItems expMenuItem = menuItems.get(groupPosition).getExpMenuItem();
                switch (expMenuItem) {
                    case MENU_ITEM_TODAY:
                    case MENU_ITEM_WEEK:
                    case MENU_ITEM_CALENDAR:
                    //case MENU_ITEM_TAGS:
                    case MENU_ITEM_PROJECTS_EDIT:
                        getFragment(expMenuItem);
                        break;
                    case MENU_ITEM_SETTINGS:
                        Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(intent);
                        break;
                    default:
                        return false;
                }
                expandableListView.setItemChecked(groupPosition, true);
                return true;
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                ExpMenuItems expMenuItem = menuItems.get(groupPosition).getExpMenuItem();
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);
                switch (expMenuItem) {
                    case MENU_ITEM_PROJECTS:
                        List<Project> projects = lab.getProjects();
                        final UUID _id = projects.get(childPosition).getID();
                        getProjectFragment(_id);
                        return true;
                    case MENU_ITEM_FILTERS:
                        getFilterFragment(childPosition);
                        return true;
                    case MENU_ITEM_TAGS:
                        UUID id = lab.getTags().get(childPosition).getID();
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

    public void getFilterFragment(int filterType) {
        drawerLayout.closeDrawers();
        switch (filterType) {
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

    public void getFragment(ExpMenuItems expMenuItem) {
        drawerLayout.closeDrawers();
        switch (expMenuItem) {
            case MENU_ITEM_TODAY:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListTodayFragment.newInstance())
                        .commit();
                break;
            case MENU_ITEM_WEEK:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListOfWeekFragment.newInstance())
                        .commit();
                break;
            case MENU_ITEM_CALENDAR:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CalendarFragment.newInstance())
                        .commit();
                break;
            case MENU_ITEM_PROJECTS_EDIT:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProjectsListFragment.newInstance())
                        .commit();
                break;
            case MENU_ITEM_TAGS_EDIT:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TagsListFragment.newInstance())
                        .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProjectLab lab = ProjectLab.get(this);
        lab.saveProjectsIntoJSON();
        lab.saveTagsIntoJSON();
    }
}
