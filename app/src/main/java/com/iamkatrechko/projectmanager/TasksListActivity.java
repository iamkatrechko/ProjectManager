package com.iamkatrechko.projectmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.iamkatrechko.projectmanager.entity.Task;

import java.util.UUID;

/**
 * Created by Muxa on 26.02.2016.
 */
public class TasksListActivity extends AppCompatActivity {
    public android.support.v7.app.ActionBar a;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_tasks_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        a = getSupportActionBar();

        UUID ID = UUID.fromString(getIntent().getStringExtra("mId"));
        String Type = getIntent().getStringExtra("Type");

        if (Type.equals(Task.TASK_TYPE_SUB_PROJECT)){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TasksListFragment.newInstance(ID))
                    .commit();
            //setTitle(R.string.title_section1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.act_slide_right_in, R.anim.act_slide_right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.act_slide_right_in, R.anim.act_slide_right_out);
    }
}
