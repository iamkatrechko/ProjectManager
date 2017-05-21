package com.iamkatrechko.projectmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.iamkatrechko.projectmanager.R;
import com.iamkatrechko.projectmanager.Themes;
import com.iamkatrechko.projectmanager.fragment.TaskEditFragment;
import com.iamkatrechko.projectmanager.utils.Utils;

import java.util.UUID;

/**
 * Активность создания/редактирования задачи
 * @author iamkatrechko
 *         Date: 26.02.2016
 */
public class TaskEditActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_ID = "mId";
    private static final String EXTRA_PARENT_ID = "parent_ID";
    private static final String EXTRA_OPERATION = "Operation";
    private static final String EXTRA_TASK_TITLE = "task_title";

    /**
     * Возвращает интент создания задачи
     * @param context  контекст
     * @param parentId идентификатор родителя
     * @return интент создания задачи
     */
    public static Intent getAddActivityIntent(Context context, UUID parentId) {
        return getAddActivityIntent(context, parentId, null);
    }

    /**
     * Возвращает интент создания задачи
     * @param context   контекст
     * @param parentId  идентификатор родителя
     * @param taskTitle название задачи
     * @return интент создания задачи
     */
    public static Intent getAddActivityIntent(Context context, UUID parentId, String taskTitle) {
        return getActivityIntent(context, null, parentId, "add", taskTitle);
    }

    /**
     * Возвращает интент редактирования задачи
     * @param context контекст
     * @param taskId  идентификатор задачи
     * @return интент редактирования задачи
     */
    public static Intent getEditActivityIntent(Context context, UUID taskId) {
        return getActivityIntent(context, taskId, null, "edit", null);
    }

    /**
     * Возвращает интент редактирования задачи
     * @param context   контекст
     * @param taskId    идентификатор задачи
     * @param parentId  идентификатор родителя
     * @param operation тип операции
     * @param taskTitle название задачи
     * @return интент редактирования задачи
     */
    private static Intent getActivityIntent(Context context, UUID taskId, UUID parentId, String operation, String taskTitle) {
        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        intent.putExtra(EXTRA_OPERATION, operation);
        intent.putExtra(EXTRA_TASK_TITLE, taskTitle);
        return intent;
    }

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

        String ID = getIntent().getStringExtra(EXTRA_TASK_ID);
        String parentID = getIntent().getStringExtra(EXTRA_PARENT_ID);
        String operation = getIntent().getStringExtra(EXTRA_OPERATION);
        String taskTitle = getIntent().getStringExtra(EXTRA_TASK_TITLE);

        try {
            UUID taskId = (UUID) getIntent().getSerializableExtra(EXTRA_TASK_ID);
            UUID parentId = (UUID) getIntent().getSerializableExtra(EXTRA_PARENT_ID);
            if (taskId == null) {
                ID = "0";
            } else {
                ID = taskId.toString();
            }
            if (parentId == null) {
                parentID = "0";
            } else {
                parentID = parentId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TaskEditFragment.newInstance(ID, operation, parentID, taskTitle))
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
