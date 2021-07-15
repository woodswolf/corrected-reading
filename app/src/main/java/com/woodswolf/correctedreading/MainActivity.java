package com.woodswolf.correctedreading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static WeakReference<Context> context;
    private static WeakReference<Resources> resources;
    private static WeakReference<AssetManager> assets;

    private TextView mainTitle;
    private ImageButton mainSettingsButton;

    public static final String[] work_internal_names = {"alices_adventures_in_wonderland", "a_christmas_carol", "a_modest_proposal", "the_strange_case_of_dr_jekyll_and_mr_hyde"};
    public Work[] works;

    private int text_heading_size_sp;
    private int text_subheading_size_sp;
    private int text_normal_size_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        switch (settings.getInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, -1)) {
            case SettingsActivity.SP_NO_LOCK:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case SettingsActivity.SP_HORIZONTAL:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                break;
            case SettingsActivity.SP_VERTICAL:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                break;
            default:
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, SettingsActivity.SP_NO_LOCK);
                edit.apply();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        switch(settings.getInt(SettingsActivity.SHARED_PREFERENCES_THEME, -1)) {
            case SettingsActivity.SP_LIGHT_THEME:
                setTheme(R.style.LightThemeNoActionBar);
                break;
            case SettingsActivity.SP_DARK_THEME:
                setTheme(R.style.DarkThemeNoActionBar);
                break;
            default:
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_THEME, SettingsActivity.SP_LIGHT_THEME);
                edit.apply();
                setTheme(R.style.LightThemeNoActionBar);
        }

        int headingTextSize = settings.getInt(SettingsActivity.SHARED_PREFERENCES_TEXT_HEADING_SIZE, -1);
        if (headingTextSize >= 1 && headingTextSize <= 36) {
            text_heading_size_sp = headingTextSize;
        } else {
            SharedPreferences.Editor edit = settings.edit();
            edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_HEADING_SIZE, SettingsActivity.TEXT_HEADING_SIZE_DEFAULT);
            edit.apply();
            text_heading_size_sp = SettingsActivity.TEXT_HEADING_SIZE_DEFAULT;
        }

        int subheadingTextSize = settings.getInt(SettingsActivity.SHARED_PREFERENCES_TEXT_SUBHEADING_SIZE, -1);
        if (subheadingTextSize > 0 && subheadingTextSize <= 36) {
            text_subheading_size_sp = subheadingTextSize;
        } else {
            SharedPreferences.Editor edit = settings.edit();
            edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_SUBHEADING_SIZE, SettingsActivity.TEXT_SUBHEADING_SIZE_DEFAULT);
            edit.apply();
            text_subheading_size_sp = SettingsActivity.TEXT_SUBHEADING_SIZE_DEFAULT;
        }

        int normalTextSize = settings.getInt(SettingsActivity.SHARED_PREFERENCES_TEXT_NORMAL_SIZE, -1);
        if (normalTextSize > 0 && normalTextSize <= 36) {
            text_normal_size_sp = normalTextSize;
        } else {
            SharedPreferences.Editor edit = settings.edit();
            edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_NORMAL_SIZE, SettingsActivity.TEXT_NORMAL_SIZE_DEFAULT);
            edit.apply();
            text_normal_size_sp = SettingsActivity.TEXT_NORMAL_SIZE_DEFAULT;
        }

        setContentView(R.layout.activity_main);



        context = new WeakReference<Context>(getApplicationContext());
        resources = new WeakReference<Resources>(getResources());
        assets = new WeakReference<AssetManager>(getAssets());



        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;

        mainTitle = findViewById(R.id.main_title);
        int topWidth = (int) ((dpWidth - 96) * density + 0.5f);
        mainTitle.setWidth(topWidth);

        mainSettingsButton = findViewById(R.id.settings_button_main);
        mainSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.SOURCE_ACTIVITY, SettingsActivity.ACTIVITY_MAIN);
                startActivity(intent);
            }
        });

        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor workCursor = db.query(DBHelper.WORK_TABLE,               // table
                    new String[] {"_id",                                    // columns
                            DBHelper.WORK_INTERNAL_NAME_WFIELD,
                            DBHelper.WORK_TITLE_ID_WFIELD,
                            DBHelper.AUTHOR_ID_WFIELD,
                            DBHelper.DESCRIPTION_ID_WFIELD,
                            DBHelper.COVER_ART_ID_WFIELD,
                            DBHelper.WORD_COUNT_ARRAY_ID_WFIELD,
                            DBHelper.PUBLISHED_DATE_WFIELD,
                            DBHelper.COMPLETED_DATE_WFIELD},

                    null,                                           // selection
                    null,                                       // selectionArgs
                    null,                                           // groupBy
                    null,                                               // having
                    DBHelper.LAST_READ_WFIELD + " DESC, "
                            + DBHelper.WORK_INTERNAL_NAME_WFIELD + " ASC");   // orderBy
            workCursor.moveToFirst();
            int works_index = 0;
            works = new Work[work_internal_names.length];
            do {
                works[works_index] = new Work(workCursor.getString(workCursor.getColumnIndex(DBHelper.WORK_INTERNAL_NAME_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.WORK_TITLE_ID_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.AUTHOR_ID_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.DESCRIPTION_ID_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.COVER_ART_ID_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.WORD_COUNT_ARRAY_ID_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.PUBLISHED_DATE_WFIELD)),
                        workCursor.getInt(workCursor.getColumnIndex(DBHelper.COMPLETED_DATE_WFIELD)));
                long work_pkey = workCursor.getLong(workCursor.getColumnIndex("_id"));
                Cursor chapterCursor = db.query(DBHelper.CHAPTER_TABLE,     // table
                        new String[]{DBHelper.CHAPTER_NUMBER_CFIELD,        // columns
                                DBHelper.CHAPTER_TITLE_ID_CFIELD,
                                DBHelper.CHAPTER_START_NOTE_ID_CFIELD,
                                DBHelper.CHAPTER_END_NOTE_ID_CFIELD,
                                DBHelper.CHAPTER_WORD_COUNT_CFIELD,
                                DBHelper.CHAPTER_FILE_NAME_CFIELD},

                        DBHelper.WORK_PKEY_CFIELD + " = ?",         // selection
                        new String[]{Long.toString(work_pkey)},             // selectionArgs
                        null,                                       // groupBy
                        null,                                       // having
                        DBHelper.CHAPTER_NUMBER_CFIELD + " ASC");   // orderBy

                chapterCursor.moveToFirst();
                ArrayList<Chapter> chapters = new ArrayList<Chapter>();
                do {
                    chapters.add(new Chapter(chapterCursor.getInt(chapterCursor.getColumnIndex(DBHelper.CHAPTER_NUMBER_CFIELD)),
                            chapterCursor.getInt(chapterCursor.getColumnIndex(DBHelper.CHAPTER_TITLE_ID_CFIELD)),
                            chapterCursor.getInt(chapterCursor.getColumnIndex(DBHelper.CHAPTER_START_NOTE_ID_CFIELD)),
                            chapterCursor.getInt(chapterCursor.getColumnIndex(DBHelper.CHAPTER_END_NOTE_ID_CFIELD)),
                            chapterCursor.getInt(chapterCursor.getColumnIndex(DBHelper.CHAPTER_WORD_COUNT_CFIELD)),
                            chapterCursor.getString(chapterCursor.getColumnIndex(DBHelper.CHAPTER_FILE_NAME_CFIELD))));
                    chapterCursor.moveToNext();
                } while (!chapterCursor.isAfterLast());
                chapterCursor.close();

                works[works_index].setChapters(chapters);
                workCursor.moveToNext();
                works_index += 1;
            } while(!workCursor.isAfterLast());
            workCursor.close();
            db.close();
            dbHelper.close();

        } catch (SQLiteException e) {
            System.out.println("Uh oh stinky!");
            e.printStackTrace();
        }

        RecyclerView workRecycler = findViewById(R.id.work_recycler);
        final WorkCardViewAdapter workAdapter = new WorkCardViewAdapter(works);
        workAdapter.setTextSizes(text_heading_size_sp, text_subheading_size_sp, text_normal_size_sp);
        workRecycler.setAdapter(workAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        workRecycler.setLayoutManager(layoutManager);

        workAdapter.setListener(new WorkCardViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                try {
                    SQLiteOpenHelper dbHelper = new DBHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues newLastReadDate = new ContentValues();
                    newLastReadDate.put(DBHelper.LAST_READ_WFIELD, System.currentTimeMillis());
                    db.update(DBHelper.WORK_TABLE,
                            newLastReadDate,
                            DBHelper.WORK_INTERNAL_NAME_WFIELD + " = '" + works[position].getWorkInternalName() + "'",
                            null);
                } catch (SQLiteException e) {
                    System.out.println("Ya Done Fucked Up Somehow!");
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                intent.putExtra(Work.EXTRA_WORK, works[position]);
                intent.putExtra(Chapter.EXTRA_CHAPTER_NUMBER, 1);

                workAdapter.notifyDataSetChanged();

                startActivity(intent);
            }
        });
    }

    public static Context getCtx() {
        return context.get();
    }

    public static Resources getRes() {
        return resources.get();
    }

    public static AssetManager getAsset() {
        return assets.get();
    }
}