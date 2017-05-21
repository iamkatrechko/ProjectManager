package com.iamkatrechko.projectmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.Themes;
import com.iamkatrechko.projectmanager.fragment.TasksDoneListFragment;
import com.iamkatrechko.projectmanager.utils.Utils;

import java.util.UUID;

/**
 * Created by Muxa on 26.02.2016.
 */
public class TasksDoneListActivity extends AppCompatActivity {

    private static final String EXTRA_PARENT_ID = "parentId";
    public android.support.v7.app.ActionBar a;

    /**
     * Возвращает интент на данную активность
     * @param context  контекст
     * @param parentId идентификатор родителя
     * @return интент на данную активность
     */
    public static Intent getActivityIntent(Context context, UUID parentId) {
        Intent intent = new Intent(context, TasksDoneListActivity.class);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_tasks_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        a = getSupportActionBar();

        UUID parentId = (UUID) getIntent().getSerializableExtra(EXTRA_PARENT_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TasksDoneListFragment.newInstance(parentId))
                    .commit();
            //setTitle(R.string.title_section1);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
