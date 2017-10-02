package com.android.example.bakingapp.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


public class Step implements Parcelable {

    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;


    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public Step(Cursor cursor) {
        this.id = cursor.getInt(RecipesContract.StepEntry.POSITION_ID);
        this.shortDescription = cursor.getString(RecipesContract.StepEntry.POSITION_SHORT_DESCRIPTION);
        this.description = cursor.getString(RecipesContract.StepEntry.POSITION_DESCRIPTION);
        this.videoURL = cursor.getString(RecipesContract.StepEntry.POSITION_VIDEO_URL);
        this.thumbnailURL = cursor.getString(RecipesContract.StepEntry.POSITION_THUMBNAIL_URL);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

    protected Step(Parcel in) {
        this.id = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}
