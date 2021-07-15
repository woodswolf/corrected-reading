package com.woodswolf.correctedreading;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Work implements Parcelable {
    public static final String EXTRA_WORK = "EXTRA_WORK";
    private String work_internal_name;
    private int workTitleId;
    private int authorId;
    private int descriptionId;
    private int coverArtId;
    private int wordCountArrayId;
    private int datePublishedId;
    private int dateCompletedId;
    private ArrayList<Chapter> chapters;



    protected Work(String work_internal_name) {
        Resources resources = MainActivity.getRes();
        this.work_internal_name = work_internal_name;

        String packageName = MainActivity.getCtx().getPackageName();
        this.workTitleId = resources.getIdentifier(this.work_internal_name + "__title", "string", packageName);
        this.authorId = resources.getIdentifier(this.work_internal_name + "__author", "string", packageName);
        this.descriptionId = resources.getIdentifier(this.work_internal_name + "__description", "string", packageName);
        this.coverArtId = resources.getIdentifier(this.work_internal_name + "__cover_art", "drawable", packageName);
        if (this.coverArtId == 0) {
            this.coverArtId = R.drawable.corrected_reading_default_white;
        }

        this.wordCountArrayId = resources.getIdentifier(this.work_internal_name + "__chapter_word_counts", "array", packageName);
        int wordCountArrayLength = resources.getIntArray(this.wordCountArrayId).length;

        this.chapters = new ArrayList<Chapter>();
        for (int chapterNumber = 1; chapterNumber <= wordCountArrayLength; chapterNumber++) {
            this.chapters.add(new Chapter(this.work_internal_name, chapterNumber, packageName));
        }

        this.datePublishedId = resources.getIdentifier(this.work_internal_name + "__published_date", "string", packageName);
        this.dateCompletedId = resources.getIdentifier(this.work_internal_name + "__completed_date", "string", packageName);
    }

    protected Work(String work_internal_name, int workTitleId, int authorId, int descriptionId, int coverArtId, int wordCountArrayId, int datePublishedId, int dateCompletedId) {
        this.work_internal_name = work_internal_name;
        this.workTitleId = workTitleId;
        this.authorId = authorId;
        this.descriptionId = descriptionId;
        this.coverArtId = coverArtId;
        if (this.coverArtId == 0) {
            this.coverArtId = R.drawable.corrected_reading_default_white;
        }

        Resources resources = MainActivity.getRes();
        this.wordCountArrayId = wordCountArrayId;

        this.datePublishedId = datePublishedId;
        this.dateCompletedId = dateCompletedId;
    }




    protected Work(Parcel in) {
        this.work_internal_name = in.readString();
        this.workTitleId = in.readInt();
        this.authorId = in.readInt();
        this.descriptionId = in.readInt();
        this.coverArtId = in.readInt();
        this.wordCountArrayId = in.readInt();
        this.datePublishedId = in.readInt();
        this.dateCompletedId = in.readInt();
        this.chapters = in.createTypedArrayList(Chapter.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.work_internal_name);
        dest.writeInt(this.workTitleId);
        dest.writeInt(this.authorId);
        dest.writeInt(this.descriptionId);
        dest.writeInt(this.coverArtId);
        dest.writeInt(this.wordCountArrayId);
        dest.writeInt(this.datePublishedId);
        dest.writeInt(this.dateCompletedId);
        dest.writeTypedList(this.chapters);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Work> CREATOR = new Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel in) {
            return new Work(in);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };

    protected void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = new ArrayList<Chapter>();
        for (Chapter chapter : chapters) {
            this.chapters.add(new Chapter(chapter));
        }
    }

    protected String getWorkInternalName() {
        return this.work_internal_name;
    }

    public int getWorkTitleId() {
        return this.workTitleId;
    }

    public int getAuthorId() {
        return this.authorId;
    }

    public int getDescriptionId() {
        return this.descriptionId;
    }

    public int getCoverArtId() {
        return this.coverArtId;
    }

    public int getChapterCount() {
        Resources resources = MainActivity.getRes();
        return resources.getIntArray(this.wordCountArrayId).length;
    }

    public int getWordCountArrayId() {
        return this.wordCountArrayId;
    }

    public int getWorkWordCount() {
        Resources resources = MainActivity.getRes();
        int[] wordCountArray = resources.getIntArray(this.wordCountArrayId);

        int total = 0;
        for (int wordCount : wordCountArray) {
            total += wordCount;
        }

        return total;
    }

    public int getDatePublishedId() {
        return this.datePublishedId;
    }

    public int getDateCompletedId() {
        return this.dateCompletedId;
    }

    public ArrayList<Chapter> getChapters() {
        return this.chapters;
    }

    public String toString() {
        return "\"" + MainActivity.getRes().getString(this.workTitleId) + "\" by " + MainActivity.getRes().getString(this.authorId);
    }
}