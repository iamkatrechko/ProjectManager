package com.iamkatrechko.projectmanager.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.iamkatrechko.projectmanager.ProjectLab;
import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.Themes;
import com.iamkatrechko.projectmanager.adapter.MainMenuAdapter;
import com.iamkatrechko.projectmanager.dialog.AboutDialogFragment;
import com.iamkatrechko.projectmanager.entity.Project;
import com.iamkatrechko.projectmanager.entity.Tag;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItem;
import com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems;
import com.iamkatrechko.projectmanager.fragment.CalendarFragment;
import com.iamkatrechko.projectmanager.fragment.TasksListFilterFragment;
import com.iamkatrechko.projectmanager.fragment.ProjectsListFragment;
import com.iamkatrechko.projectmanager.fragment.TagsListFragment;
import com.iamkatrechko.projectmanager.fragment.TasksListByTagFragment;
import com.iamkatrechko.projectmanager.fragment.TasksListFragment;
import com.iamkatrechko.projectmanager.fragment.TasksListWithDatesFragment;
import com.iamkatrechko.projectmanager.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.yandex.speechkit.SpeechKit;

import static com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems.MENU_ITEM_PROJECTS;
import static com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems.MENU_ITEM_PROJECTS_EDIT;
import static com.iamkatrechko.projectmanager.expandable_menu.ExpMenuItems.MENU_ITEM_TAGS_EDIT;

/**
 * Главная активность с боковым меню (точка входа в приложение)
 * @author iamkatrechko
 *         Date: 20.02.2016
 */
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
    private List<ExpMenuItem> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Themes(this);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_main);
        lab = ProjectLab.get(this);
        fragmentManager = getSupportFragmentManager();

        initToolBar();
        setSupportActionBar(toolbar);
        initNavigationView();

        if (!lab.getProjects().isEmpty()) {
            UUID ID = lab.getProjects().get(0).getID();
            getProjectFragment(ID);
        }

        String key = "79b8f85e-b240-44a0-a2ff-a2f618831303";
        SpeechKit.getInstance().configure(getApplicationContext(), key);
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

        initMenuItems();

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
                        startFragment(expMenuItem);
                        break;
                    case MENU_ITEM_SETTINGS:
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case MENU_ITEM_ABOUT:
                        AboutDialogFragment aboutDialogFragment = AboutDialogFragment.newInstance();
                        aboutDialogFragment.setTargetFragment(null, -1);
                        aboutDialogFragment.show(getSupportFragmentManager(), null);
                        drawerLayout.closeDrawers();
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
                ExpMenuItem menuItem = menuItems.get(groupPosition);
                ExpMenuItems expMenuItem = menuItem.getExpMenuItem();
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);
                switch (expMenuItem) {
                    case MENU_ITEM_PROJECTS:
                        if (childPosition == menuItem.getChildItemCount() - 1) {
                            startFragment(MENU_ITEM_PROJECTS_EDIT);
                            return true;
                        }
                        List<Project> projects = lab.getProjects();
                        final UUID _id = projects.get(childPosition).getID();
                        getProjectFragment(_id);
                        return true;
                    case MENU_ITEM_FILTERS:
                        getFilterFragment(childPosition);
                        return true;
                    case MENU_ITEM_TAGS:
                        if (childPosition == menuItem.getChildItemCount() - 1) {
                            startFragment(MENU_ITEM_TAGS_EDIT);
                            return true;
                        }
                        UUID id = lab.getTags().get(childPosition).getID();
                        getTagFragment(id);
                        return true;
                }
                return true;
            }
        });
    }

    public void initMenuItems() {
        menuItems.clear();
        for (ExpMenuItems expMenuItems : ExpMenuItems.values()) {
            ExpMenuItem item = new ExpMenuItem(expMenuItems);
            switch (expMenuItems) {
                case MENU_ITEM_PROJECTS:
                    ArrayList<Project> projects = lab.getProjects();
                    for (Project project : projects) {
                        item.addChildItem(new ExpMenuItem.ChildItem(project.getTitle(), project.getColor()));
                    }
                    item.addChildItem(new ExpMenuItem.ChildItem(getString(R.string.nav_menu_projects_edit), Color.BLACK));
                    break;
                case MENU_ITEM_FILTERS:
                    String[] list = getResources().getStringArray(R.array.filters);
                    for (String filter : list) {
                        item.addChildItem(new ExpMenuItem.ChildItem(filter, Color.GRAY));
                    }
                    break;
                case MENU_ITEM_TAGS:
                    ArrayList<Tag> tags = lab.getTags();
                    for (Tag tag : tags) {
                        item.addChildItem(new ExpMenuItem.ChildItem(tag.getTitle(), Color.GRAY));
                    }
                    item.addChildItem(new ExpMenuItem.ChildItem(getString(R.string.nav_menu_tags_edit), Color.BLACK));
                    break;
                case MENU_ITEM_PROJECTS_EDIT:
                case MENU_ITEM_TAGS_EDIT:
                    continue;
            }

            menuItems.add(item);
        }
        //adapter = new ExpListAdapter(getApplicationContext(), listGroup);
        adapter = new MainMenuAdapter(this, menuItems);
        mExpandableListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMenuItems();
    }

    public void getProjectFragment(UUID _id) {
        drawerLayout.closeDrawers();
        fragmentManager.beginTransaction()
                .replace(R.id.container, TasksListFragment.newInstance(_id))
                .commit();
        //setTitle(R.string.title_section1);
    }

    public void getFilterFragment(int filterType) {
        drawerLayout.closeDrawers();
        switch (filterType) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListFilterFragment.newInstance(0))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListFilterFragment.newInstance(1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListFilterFragment.newInstance(2))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListFilterFragment.newInstance(3))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListFilterFragment.newInstance(4))
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

    public void startFragment(ExpMenuItems expMenuItem) {
        drawerLayout.closeDrawers();
        switch (expMenuItem) {
            case MENU_ITEM_TODAY:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListWithDatesFragment.newInstance(TasksListWithDatesFragment.LIST_WITH_DATES_MODE.LIST_TODAY))
                        .commit();
                break;
            case MENU_ITEM_WEEK:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TasksListWithDatesFragment.newInstance(TasksListWithDatesFragment.LIST_WITH_DATES_MODE.LIST_OF_WEEK))
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
    public void setTitle(CharSequence title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProjectLab lab = ProjectLab.get(this);
        lab.saveProjectsIntoJSON();
        lab.saveTagsIntoJSON();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
