package com.woodswolf.correctedreading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES_FILE = "CorrectedReadingSharedPreferences";

    public static final String SHARED_PREFERENCES_THEME = "AppTheme";
    public static final int SP_LIGHT_THEME = 0;
    public static final int SP_DARK_THEME = 1;

    public static final String SHARED_PREFERENCES_ROTATION = "RotationLock";
    public static final int SP_NO_LOCK = 0;
    public static final int SP_HORIZONTAL = 1;
    public static final int SP_VERTICAL = 2;

    public static final String SHARED_PREFERENCES_TEXT_HEADING_SIZE = "TextHeadingSize";
    public static final int TEXT_HEADING_SIZE_DEFAULT = 22;     // 22sp
    public static final String SHARED_PREFERENCES_TEXT_SUBHEADING_SIZE = "TextSubHeadingSize";
    public static final int TEXT_SUBHEADING_SIZE_DEFAULT = 18;  // 18sp
    public static final String SHARED_PREFERENCES_TEXT_NORMAL_SIZE = "TextNormalSize";
    public static final int TEXT_NORMAL_SIZE_DEFAULT = 14;      // 14sp

    private int text_heading_size_sp;
    private int text_subheading_size_sp;
    private int text_normal_size_sp;

    private boolean isDarkTheme;

    public static final String SOURCE_ACTIVITY = "SourceActivity";
    public static final int ACTIVITY_MAIN = 1;
    public static final int ACTIVITY_READING = 2;

    private int sourceActivity;
    private Work work;
    private int chapter_number;
    private int reading_scroll_y;

    private ImageButton backToChapterButton;
    private TextView settings_title;

    private RadioGroup appThemeGroup;
    private RadioButton rbLightTheme;
    private RadioButton rbDarkTheme;

    private RadioGroup rotationLockGroup;
    private RadioButton rbNoLock;
    private RadioButton rbHorizontalLock;
    private RadioButton rbVerticalLock;

    ImageButton headingSizeDecreaseButton;
    ImageButton headingSizeIncreaseButton;
    TextView headingSizeTextView;
    TextView headingSizePreviewTextView;
    ImageButton subheadingSizeDecreaseButton;
    ImageButton subheadingSizeIncreaseButton;
    TextView subheadingSizeTextView;
    TextView subheadingSizePreviewTextView;
    ImageButton normalTextSizeDecreaseButton;
    ImageButton normalTextSizeIncreaseButton;
    TextView normalTextSizeTextView;
    TextView normalTextSizePreviewTextView;

    Button applyButton;

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
                isDarkTheme = false;
                break;
            case SettingsActivity.SP_DARK_THEME:
                setTheme(R.style.DarkThemeNoActionBar);
                isDarkTheme = true;
                break;
            default:
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_THEME, SettingsActivity.SP_LIGHT_THEME);
                edit.apply();
                setTheme(R.style.LightThemeNoActionBar);
                isDarkTheme = false;
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

        setContentView(R.layout.activity_settings);



        Intent intent = getIntent();
        sourceActivity = intent.getIntExtra(SOURCE_ACTIVITY, 1);
        if (sourceActivity == ACTIVITY_READING) {
            work = intent.getParcelableExtra(Work.EXTRA_WORK);
            chapter_number = intent.getIntExtra(Chapter.EXTRA_CHAPTER_NUMBER, 1);
            reading_scroll_y = intent.getIntExtra(ReadingActivity.EXTRA_SCROLL_Y, 0);
        }



        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;

        backToChapterButton = findViewById(R.id.back_to_chapter_button);
        backToChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sourceActivity == ACTIVITY_READING) {
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra(Work.EXTRA_WORK, work);
                    intent.putExtra(Chapter.EXTRA_CHAPTER_NUMBER, chapter_number);
                    intent.putExtra(ReadingActivity.EXTRA_SCROLL_Y, reading_scroll_y);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        settings_title = findViewById(R.id.settings_title);
        int topWidth = (int) ((dpWidth - 96) * density + 0.5f);
        settings_title.setWidth(topWidth);



        TextView theme_title = findViewById(R.id.theme_title);
        theme_title.setTextSize(text_heading_size_sp);

        appThemeGroup = findViewById(R.id.appThemeRadioGroup);

        rbLightTheme = findViewById(R.id.rbLightTheme);
        rbLightTheme.setTextSize(text_normal_size_sp);

        rbDarkTheme = findViewById(R.id.rbDarkTheme);
        rbDarkTheme.setTextSize(text_normal_size_sp);

        switch (settings.getInt(SettingsActivity.SHARED_PREFERENCES_THEME, -1)) {
            case SettingsActivity.SP_LIGHT_THEME:
                appThemeGroup.check(R.id.rbLightTheme);
                break;
            case SettingsActivity.SP_DARK_THEME:
                appThemeGroup.check(R.id.rbDarkTheme);
                break;
            default:
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_THEME, SettingsActivity.SP_LIGHT_THEME);
                edit.apply();
                appThemeGroup.check(R.id.rbLightTheme);
        }

        appThemeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                if (rbDarkTheme.isChecked()) {
                    edit.putInt(SettingsActivity.SHARED_PREFERENCES_THEME, SettingsActivity.SP_DARK_THEME);
                    edit.apply();

                } else {
                    edit.putInt(SettingsActivity.SHARED_PREFERENCES_THEME, SettingsActivity.SP_LIGHT_THEME);
                    edit.apply();
                }

                finish();

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.SOURCE_ACTIVITY, sourceActivity);
                if (sourceActivity == ACTIVITY_READING) {
                    intent.putExtra(Work.EXTRA_WORK, work);
                    intent.putExtra(Chapter.EXTRA_CHAPTER_NUMBER, chapter_number);
                    intent.putExtra(ReadingActivity.EXTRA_SCROLL_Y, reading_scroll_y);
                }
                startActivity(intent);
            }
        });



        TextView rotation_title = findViewById(R.id.rotation_title);
        rotation_title.setTextSize(text_heading_size_sp);

        rotationLockGroup = findViewById(R.id.rotationLockRadioGroup);

        rbNoLock = findViewById(R.id.rbNoLock);
        rbNoLock.setTextSize(text_normal_size_sp);

        rbHorizontalLock = findViewById(R.id.rbLockHorizontal);
        rbHorizontalLock.setTextSize(text_normal_size_sp);

        rbVerticalLock = findViewById(R.id.rbLockVertical);
        rbVerticalLock.setTextSize(text_normal_size_sp);

        switch (settings.getInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, -1)) {
            case SettingsActivity.SP_NO_LOCK:
                rotationLockGroup.check(R.id.rbNoLock);
                break;
            case SettingsActivity.SP_HORIZONTAL:
                rotationLockGroup.check(R.id.rbLockHorizontal);
                break;
            case SettingsActivity.SP_VERTICAL:
                rotationLockGroup.check(R.id.rbLockVertical);
                break;
            default:
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, SettingsActivity.SP_NO_LOCK);
                edit.apply();
                rotationLockGroup.check(R.id.rbNoLock);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        rotationLockGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                if (rbHorizontalLock.isChecked()) {
                    edit.putInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, SettingsActivity.SP_HORIZONTAL);
                    edit.apply();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                } else if (rbVerticalLock.isChecked()) {
                    edit.putInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, SettingsActivity.SP_VERTICAL);
                    edit.apply();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                } else {
                    edit.putInt(SettingsActivity.SHARED_PREFERENCES_ROTATION, SettingsActivity.SP_NO_LOCK);
                    edit.apply();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
            }
        });



        TextView text_size_title = findViewById(R.id.text_size_title);
        text_size_title.setTextSize(text_heading_size_sp);

        headingSizeTextView = findViewById(R.id.headingSizeTextView);
        headingSizeTextView.setText(Integer.toString(text_heading_size_sp));
        headingSizeTextView.setTextSize(text_normal_size_sp);
        headingSizePreviewTextView = findViewById(R.id.headingSizePreviewTextView);
        headingSizePreviewTextView.setTextSize(text_heading_size_sp);
        headingSizeDecreaseButton = findViewById(R.id.headingSizeDecreaseButton);
        headingSizeDecreaseButton.setEnabled(true);
        headingSizeIncreaseButton = findViewById(R.id.headingSizeIncreaseButton);
        headingSizeIncreaseButton.setEnabled(true);

        subheadingSizeTextView = findViewById(R.id.subheadingSizeTextView);
        subheadingSizeTextView.setText(Integer.toString(text_subheading_size_sp));
        subheadingSizeTextView.setTextSize(text_normal_size_sp);
        subheadingSizePreviewTextView = findViewById(R.id.subheadingSizePreviewTextView);
        subheadingSizePreviewTextView.setTextSize(text_subheading_size_sp);
        subheadingSizeDecreaseButton = findViewById(R.id.subheadingSizeDecreaseButton);
        subheadingSizeDecreaseButton.setEnabled(true);
        subheadingSizeIncreaseButton = findViewById(R.id.subheadingSizeIncreaseButton);
        subheadingSizeIncreaseButton.setEnabled(true);

        normalTextSizeTextView = findViewById(R.id.normalTextSizeTextView);
        normalTextSizeTextView.setText(Integer.toString(text_normal_size_sp));
        normalTextSizeTextView.setTextSize(text_normal_size_sp);
        normalTextSizePreviewTextView = findViewById(R.id.normalTextSizePreviewTextView);
        normalTextSizePreviewTextView.setTextSize(text_normal_size_sp);
        normalTextSizeDecreaseButton = findViewById(R.id.normalTextSizeDecreaseButton);
        normalTextSizeDecreaseButton.setEnabled(true);
        normalTextSizeIncreaseButton = findViewById(R.id.normalTextSizeIncreaseButton);
        normalTextSizeIncreaseButton.setEnabled(true);

        applyButton = findViewById(R.id.apply_button);
        applyButton.setTextSize(text_normal_size_sp);

        if (isDarkTheme) {
            headingSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_white_36);
            headingSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_white_36);
            subheadingSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_white_36);
            subheadingSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_white_36);
            normalTextSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_white_36);
            normalTextSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_white_36);
        } else {
            headingSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_black_36);
            headingSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_black_36);
            subheadingSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_black_36);
            subheadingSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_black_36);
            normalTextSizeDecreaseButton.setImageResource(R.drawable.baseline_remove_circle_outline_black_36);
            normalTextSizeIncreaseButton.setImageResource(R.drawable.baseline_add_circle_outline_black_36);
        }

        headingSizeDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_heading_size_sp -= 1;
                if (text_heading_size_sp <= 1) {
                    text_heading_size_sp = 1;
                    headingSizeDecreaseButton.setEnabled(false);
                    headingSizeIncreaseButton.setEnabled(true);
                }
                if (text_heading_size_sp >= 2) {
                    headingSizeDecreaseButton.setEnabled(true);
                    headingSizeIncreaseButton.setEnabled(true);
                }

                headingSizeTextView.setText(Integer.toString(text_heading_size_sp));
                headingSizePreviewTextView.setTextSize(text_heading_size_sp);
            }
        });

        headingSizeIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_heading_size_sp += 1;
                if (text_heading_size_sp >= 36) {
                    text_heading_size_sp = 36;
                    headingSizeIncreaseButton.setEnabled(false);
                    headingSizeDecreaseButton.setEnabled(true);
                }
                if (text_heading_size_sp <= 35) {
                    headingSizeDecreaseButton.setEnabled(true);
                    headingSizeIncreaseButton.setEnabled(true);
                }

                headingSizeTextView.setText(Integer.toString(text_heading_size_sp));
                headingSizePreviewTextView.setTextSize(text_heading_size_sp);
            }
        });

        subheadingSizeDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_subheading_size_sp -= 1;
                if (text_subheading_size_sp <= 1) {
                    text_subheading_size_sp = 1;
                    subheadingSizeDecreaseButton.setEnabled(false);
                }
                if (text_subheading_size_sp >= 2) {
                    subheadingSizeDecreaseButton.setEnabled(true);
                    subheadingSizeIncreaseButton.setEnabled(true);
                }

                subheadingSizeTextView.setText(Integer.toString(text_subheading_size_sp));
                subheadingSizePreviewTextView.setTextSize(text_subheading_size_sp);
            }
        });

        subheadingSizeIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_subheading_size_sp += 1;
                if (text_subheading_size_sp >= 36) {
                    text_subheading_size_sp = 36;
                    subheadingSizeIncreaseButton.setEnabled(false);
                }
                if (text_subheading_size_sp <= 35) {
                    subheadingSizeDecreaseButton.setEnabled(true);
                    subheadingSizeIncreaseButton.setEnabled(true);
                }

                subheadingSizeTextView.setText(Integer.toString(text_subheading_size_sp));
                subheadingSizePreviewTextView.setTextSize(text_subheading_size_sp);
            }
        });

        normalTextSizeDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_normal_size_sp -= 1;
                if (text_normal_size_sp <= 1) {
                    text_normal_size_sp = 1;
                    normalTextSizeDecreaseButton.setEnabled(false);
                }
                if (text_normal_size_sp >= 2) {
                    normalTextSizeDecreaseButton.setEnabled(true);
                    normalTextSizeIncreaseButton.setEnabled(true);
                }

                normalTextSizeTextView.setText(Integer.toString(text_normal_size_sp));
                normalTextSizePreviewTextView.setTextSize(text_normal_size_sp);
            }
        });

        normalTextSizeIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_normal_size_sp += 1;
                if (text_normal_size_sp >= 36) {
                    text_normal_size_sp = 36;
                    normalTextSizeIncreaseButton.setEnabled(false);
                }
                if (text_normal_size_sp <= 35) {
                    normalTextSizeDecreaseButton.setEnabled(true);
                    normalTextSizeIncreaseButton.setEnabled(true);
                }

                normalTextSizeTextView.setText(Integer.toString(text_normal_size_sp));
                normalTextSizePreviewTextView.setTextSize(text_normal_size_sp);
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();

                edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_HEADING_SIZE, text_heading_size_sp);
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_SUBHEADING_SIZE, text_subheading_size_sp);
                edit.putInt(SettingsActivity.SHARED_PREFERENCES_TEXT_NORMAL_SIZE, text_normal_size_sp);
                edit.apply();

                finish();

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.SOURCE_ACTIVITY, sourceActivity);
                if (sourceActivity == ACTIVITY_READING) {
                    intent.putExtra(Work.EXTRA_WORK, work);
                    intent.putExtra(Chapter.EXTRA_CHAPTER_NUMBER, chapter_number);
                    intent.putExtra(ReadingActivity.EXTRA_SCROLL_Y, reading_scroll_y);
                }
                startActivity(intent);
            }
        });
    }
}