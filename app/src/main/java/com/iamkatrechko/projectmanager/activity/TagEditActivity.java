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
import com.iamkatrechko.projectmanager.utils.Utils;
import com.iamkatrechko.projectmanager.entity.Tag;
import com.iamkatrechko.projectmanager.fragment.TagEditFragment;

/**
 * Активность создания/редактирования тега
 * @author iamkatrechko
 *         Date: 12.04.2016
 */
public class TagEditActivity extends AppCompatActivity {

    /** Экземпляр фрагмента редактирования тега */
    private TagEditFragment mTagEditFragment;

    public static Intent getIntent(Context context, Tag tag) {
        Intent intent = new Intent(context, TagEditActivity.class);
        intent.putExtra("tag", tag);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_template);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Tag tag = getIntent().getParcelableExtra("tag");
        mTagEditFragment = TagEditFragment.newInstance(tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mTagEditFragment)
                    .commit();
            // TODO на основе данных (или их отсутствия) отображать заголовок "создание/редактирование"
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

    @Override
    public void onBackPressed() {
        mTagEditFragment.onBackPressed();
    }
}
