package com.woodswolf.correctedreading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class ReadingActivity extends AppCompatActivity {
    public static final String EXTRA_SCROLL_Y = "SCROLL_Y";
    private int scroll_y = 0;

    protected Work work;
    protected int chapter_number;
    protected Chapter chapter;

    private ImageButton backButton;
    private ImageButton settingsButton;

    private Spinner chapterSelectSpinner;
    private ImageButton prevChapterButton;
    private ImageButton nextChapterButton;

    private TextView workTitleText;
    private TextView chapterTitleText;
    private TextView startNote;
    private TextView chapterText;
    private TextView endNote;
    private ScrollView chapterScrollContainer;

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

        setContentView(R.layout.activity_reading);


        
        scroll_y = 0;
        if (savedInstanceState != null) {
            scroll_y = savedInstanceState.getInt(ReadingActivity.EXTRA_SCROLL_Y);
        }

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;



        Intent intent = getIntent();
        work = intent.getParcelableExtra(Work.EXTRA_WORK);

        chapter_number = intent.getIntExtra(Chapter.EXTRA_CHAPTER_NUMBER, 1);
        if (chapter_number < 1 || chapter_number > work.getChapterCount()) {
            chapter_number = 1;
        }
        chapter = work.getChapters().get(chapter_number - 1);



        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.SOURCE_ACTIVITY, SettingsActivity.ACTIVITY_READING);
                intent.putExtra(Work.EXTRA_WORK, work);
                intent.putExtra(Chapter.EXTRA_CHAPTER_NUMBER, chapter_number);
                intent.putExtra(ReadingActivity.EXTRA_SCROLL_Y, chapterScrollContainer.getScrollY());
                startActivity(intent);
            }
        });

        prevChapterButton = findViewById(R.id.prev_chapter_button);
        prevChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chapter.getChapterNumber() > 1) {
                    int new_chapter = chapter.getChapterNumber() - 1;
                    chapterSelectSpinner.setSelection(new_chapter-1);
                }
            }
        });

        nextChapterButton = findViewById(R.id.next_chapter_button);
        nextChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chapter.getChapterNumber() < work.getChapterCount()) {
                    int new_chapter = chapter.getChapterNumber() + 1;
                    chapterSelectSpinner.setSelection(new_chapter-1);
                }
            }
        });

        chapterScrollContainer = findViewById(R.id.chapter_scroll_container);

        chapterSelectSpinner = findViewById(R.id.chapter_select_spinner);
        ArrayAdapter<Chapter> adapter = new ArrayAdapter<Chapter>(this,
                R.layout.spinner_item,
                work.getChapters());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapterSelectSpinner.setAdapter(adapter);
        chapterSelectSpinner.setSelection(chapter.getChapterNumber()-1);
        chapterSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                chapter = work.getChapters().get(position);
                chapter_number = position + 1;
                initializeChapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        int topWidth = (int) ((dpWidth - 96) * density + 0.5f);
        int bottomWidth = (int) ((dpWidth - 72) * density + 0.5f);
        ViewGroup.LayoutParams params = chapterSelectSpinner.getLayoutParams();
        params.width = bottomWidth;
        chapterSelectSpinner.setLayoutParams(params);

        workTitleText = findViewById(R.id.work_title);
        workTitleText.setWidth(topWidth);
        chapterTitleText = findViewById(R.id.chapter_title);
        chapterTitleText.setTextSize(text_heading_size_sp);
        startNote = findViewById(R.id.start_note);
        startNote.setTextSize(text_normal_size_sp);
        chapterText = findViewById(R.id.chapter_text);
        chapterText.setTextSize(text_normal_size_sp);
        endNote = findViewById(R.id.end_note);
        endNote.setTextSize(text_normal_size_sp);

        chapterSelectSpinner.setSelection(chapter_number-1);
    }

    protected void initializeChapter() {
        // if first chapter, disable prev chapter button, else enable it
        if (chapter.getChapterNumber() > 1) {
            prevChapterButton.setEnabled(true);
            prevChapterButton.setImageResource(R.drawable.baseline_chevron_left_white_48);
        } else {
            prevChapterButton.setEnabled(false);
            prevChapterButton.setImageResource(R.drawable.baseline_chevron_left_black_48);
        }

        // if last chapter, disable next chapter button, else enable it
        if (chapter.getChapterNumber() < work.getChapterCount()) {
            nextChapterButton.setEnabled(true);
            nextChapterButton.setImageResource(R.drawable.baseline_chevron_right_white_48);
        } else {
            nextChapterButton.setEnabled(false);
            nextChapterButton.setImageResource(R.drawable.baseline_chevron_right_black_48);
        }

        // set work title
        workTitleText.setText(work.getWorkTitleId());

        // set chapter title text
        chapterTitleText.setText(chapter.getChapterTitleId());

        // set start note
        String startNoteText = getResources().getString(chapter.getStartNoteId());
        startNote.setText(Html.fromHtml(startNoteText, Html.FROM_HTML_MODE_LEGACY));

        // set chapter text
        chapterText.setText(Html.fromHtml(chapter.getChapterText(), Html.FROM_HTML_MODE_LEGACY));

        // set end note
        String endNoteText = getResources().getString(chapter.getEndNoteId());
        endNote.setText(Html.fromHtml(endNoteText, Html.FROM_HTML_MODE_LEGACY));

        // scroll back to top of scroll container
        chapterScrollContainer.scrollTo(0,scroll_y);
        scroll_y = 0;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(ReadingActivity.EXTRA_SCROLL_Y, chapterScrollContainer.getScrollY());
    }
}