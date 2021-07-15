package com.woodswolf.correctedreading;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    protected static final String DB_NAME = "CorrectedReading";
    protected static final int DB_VERSION = 1;

    protected static final String WORK_TABLE = "WORKS";
    protected static final String WORK_INTERNAL_NAME_WFIELD = "WORK_INTERNAL_NAME";
    protected static final String WORK_TITLE_ID_WFIELD = "WORK_TITLE_ID";
    protected static final String AUTHOR_ID_WFIELD = "AUTHOR_ID";
    protected static final String DESCRIPTION_ID_WFIELD = "DESCRIPTION_ID";
    protected static final String COVER_ART_ID_WFIELD = "COVER_ART_ID";
    protected static final String WORD_COUNT_ARRAY_ID_WFIELD = "WORD_COUNT_ARRAY_ID";
    protected static final String PUBLISHED_DATE_WFIELD = "PUBLISH_DATE";
    protected static final String COMPLETED_DATE_WFIELD = "COMPLETION_DATE";
    protected static final String LAST_READ_WFIELD = "LAST_READ";

    protected static final String CHAPTER_TABLE = "CHAPTERS";
    protected static final String WORK_PKEY_CFIELD = "WORK_PKEY_CFIELD";
    protected static final String CHAPTER_NUMBER_CFIELD = "CHAPTER_NUMBER";
    protected static final String CHAPTER_TITLE_ID_CFIELD = "CHAPTER_TITLE_ID";
    protected static final String CHAPTER_START_NOTE_ID_CFIELD = "CHAPTER_START_NOTE_ID";
    protected static final String CHAPTER_END_NOTE_ID_CFIELD = "CHAPTER_END_NOTE_ID";
    protected static final String CHAPTER_WORD_COUNT_CFIELD = "CHAPTER_WORD_COUNT";
    protected static final String CHAPTER_FILE_NAME_CFIELD = "CHAPTER_FILE_NAME";

    protected DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        updateMyDatabase(db, i, i1);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE " + WORK_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WORK_INTERNAL_NAME_WFIELD + " TEXT, " +
                    WORK_TITLE_ID_WFIELD + " INTEGER, " +
                    AUTHOR_ID_WFIELD + " INTEGER, " +
                    DESCRIPTION_ID_WFIELD + " INTEGER, " +
                    COVER_ART_ID_WFIELD + " INTEGER, " +
                    WORD_COUNT_ARRAY_ID_WFIELD + " INTEGER, " +
                    PUBLISHED_DATE_WFIELD + " TEXT," +
                    COMPLETED_DATE_WFIELD + " TEXT," +
                    LAST_READ_WFIELD + " INTEGER);");

            db.execSQL("CREATE TABLE " + CHAPTER_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WORK_PKEY_CFIELD + " INTEGER, " +
                    CHAPTER_NUMBER_CFIELD + " INTEGER, " +
                    CHAPTER_TITLE_ID_CFIELD + " INTEGER, " +
                    CHAPTER_START_NOTE_ID_CFIELD + " INTEGER, " +
                    CHAPTER_END_NOTE_ID_CFIELD + " INTEGER, " +
                    CHAPTER_WORD_COUNT_CFIELD + " INTEGER, " +
                    CHAPTER_FILE_NAME_CFIELD + " TEXT);");

            for (String work_internal_name : MainActivity.work_internal_names) {
                insertWork(db, new Work(work_internal_name));
            }
        }
    }

    private static void insertWork(SQLiteDatabase db, Work work) {
        ContentValues workValues = new ContentValues();
        workValues.put(WORK_INTERNAL_NAME_WFIELD, work.getWorkInternalName());
        workValues.put(WORK_TITLE_ID_WFIELD, work.getWorkTitleId());
        workValues.put(AUTHOR_ID_WFIELD, work.getAuthorId());
        workValues.put(DESCRIPTION_ID_WFIELD, work.getDescriptionId());
        workValues.put(COVER_ART_ID_WFIELD, work.getCoverArtId());
        workValues.put(WORD_COUNT_ARRAY_ID_WFIELD, work.getWordCountArrayId());
        workValues.put(PUBLISHED_DATE_WFIELD, work.getDatePublishedId());
        workValues.put(COMPLETED_DATE_WFIELD, work.getDateCompletedId());
        workValues.put(LAST_READ_WFIELD, 0);
        long work_pkey = db.insert(WORK_TABLE, null, workValues);

        for (Chapter chapter : work.getChapters()) {
            ContentValues chapterValues = new ContentValues();
            chapterValues.put(WORK_PKEY_CFIELD, work_pkey);
            chapterValues.put(CHAPTER_NUMBER_CFIELD, chapter.getChapterNumber());
            chapterValues.put(CHAPTER_TITLE_ID_CFIELD, chapter.getChapterTitleId());
            chapterValues.put(CHAPTER_START_NOTE_ID_CFIELD, chapter.getStartNoteId());
            chapterValues.put(CHAPTER_END_NOTE_ID_CFIELD, chapter.getEndNoteId());
            chapterValues.put(CHAPTER_WORD_COUNT_CFIELD, chapter.getChapterWordCount());
            chapterValues.put(CHAPTER_FILE_NAME_CFIELD, chapter.getChapterFileName());
            db.insert(CHAPTER_TABLE, null, chapterValues);
        }
    }
}
