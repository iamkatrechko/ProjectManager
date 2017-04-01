package com.iamkatrechko.projectmanager.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.Themes;
import com.iamkatrechko.projectmanager.utils.Utils;

/**
 * Created by Muxa on 24.03.2016.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Themes t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this, Integer.valueOf(Themes.getNumTheme()));
        setContentView(R.layout.activity_settings);
        t = new Themes(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //a = getSupportActionBar();


        findViewById(R.id.linear_theme).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_theme:
                showDialog(0);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                final String[] mThemesName = {"Purple", "Indigo", "Teal", "Presentation"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Выберите тему"); // заголовок для диалога
                builder.setItems(mThemesName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        qChangeTheme(item + 1);
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    public void qChangeTheme(Integer num) {
        t.setNumTheme(String.valueOf(num));
        Utils.changeToTheme(this, num);
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
