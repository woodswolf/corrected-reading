package com.woodswolf.correctedreading;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Chapter implements Parcelable {
    public static final String EXTRA_CHAPTER_NUMBER = "EXTRA_CHAPTER_NUMBER";
    private int chapterNumber;
    private int chapterTitleId;
    private int chapterStartNoteId;
    private int chapterEndNoteId;
    private int chapterWordCount;
    private String chapterFileName;

    protected Chapter(String work_internal_name, int chapterNumber, String packageName) {
        Resources resources = MainActivity.getRes();
        this.chapterNumber = chapterNumber;
        this.chapterTitleId = resources.getIdentifier(work_internal_name + "__ch" + chapterNumber + "_title", "string", packageName);
        this.chapterStartNoteId = resources.getIdentifier(work_internal_name + "__ch" + chapterNumber + "_startnote", "string", packageName);
        this.chapterEndNoteId = resources.getIdentifier(work_internal_name + "__ch" + chapterNumber + "_endnote", "string", packageName);
        int chapterWordCountId = resources.getIdentifier(work_internal_name + "__chapter_word_counts", "array", packageName);
        this.chapterWordCount = resources.getIntArray(chapterWordCountId)[this.chapterNumber-1];
        this.chapterFileName = work_internal_name + "/" + work_internal_name + "__ch" + chapterNumber + ".txt";
    }

    protected Chapter(int chapterNumber, int chapterTitleId, int chapterStartNoteId, int chapterEndNoteId, int chapterWordCount, String chapterFileName) {
        this.chapterNumber = chapterNumber;
        this.chapterTitleId = chapterTitleId;
        this.chapterStartNoteId = chapterStartNoteId;
        this.chapterEndNoteId = chapterEndNoteId;
        this.chapterWordCount = chapterWordCount;
        this.chapterFileName = chapterFileName;
    }

    protected Chapter(Chapter other) {
        this(other.getChapterNumber(), other.getChapterTitleId(), other.getStartNoteId(), other.getEndNoteId(), other.getChapterWordCount(), other.getChapterFileName());
    }

    protected Chapter(Parcel in) {
        this.chapterNumber = in.readInt();
        this.chapterTitleId = in.readInt();
        this.chapterStartNoteId = in.readInt();
        this.chapterEndNoteId = in.readInt();
        this.chapterWordCount = in.readInt();
        this.chapterFileName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.chapterNumber);
        dest.writeInt(this.chapterTitleId);
        dest.writeInt(this.chapterStartNoteId);
        dest.writeInt(this.chapterEndNoteId);
        dest.writeInt(this.chapterWordCount);
        dest.writeString(this.chapterFileName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };



    public int getChapterNumber() {
        return this.chapterNumber;
    }

    public int getChapterTitleId() {
        return this.chapterTitleId;
    }

    public int getStartNoteId() {
        return this.chapterStartNoteId;
    }

    public int getEndNoteId() {
        return this.chapterEndNoteId;
    }

    public int getChapterWordCount() {
        return this.chapterWordCount;
    }

    public String getChapterFileName() {
        return this.chapterFileName;
    }



    public String getChapterText() {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(MainActivity.getAsset().open(this.chapterFileName)));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return this.chapterNumber + ". " + MainActivity.getRes().getString(this.chapterTitleId);
    }

}
