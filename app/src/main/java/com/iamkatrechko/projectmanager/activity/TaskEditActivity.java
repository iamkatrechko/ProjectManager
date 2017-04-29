package com.iamkatrechko.projectmanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.Themes;
import com.iamkatrechko.projectmanager.fragment.TaskEditFragment;
import com.iamkatrechko.projectmanager.utils.Utils;

/**
 * Created by Muxa on 26.02.2016.
 */
public class TaskEditActivity extends AppCompatActivity {
    public android.support.v7.app.ActionBar a;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_template2);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setSubtitleTextColor(Color.WHITE);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //a = getSupportActionBar();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String ID = getIntent().getStringExtra("mId");
        String operation = getIntent().getStringExtra("Operation");
        String parentID = getIntent().getStringExtra("parent_ID");

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TaskEditFragment.newInstance(ID, operation, parentID))
                    .commit();
            //setTitle(R.string.title_section1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.act_slide_up_in, R.anim.act_slide_up_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.act_slide_up_in, R.anim.act_slide_up_out);
    }
}
