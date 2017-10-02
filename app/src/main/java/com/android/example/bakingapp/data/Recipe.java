package com.android.example.bakingapp.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Recipe implements Parcelable {

    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int servings;
    private String image;


    public Recipe(String name, int servings, String image) {
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String image) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public Recipe(Cursor cursor) {
        this.id = cursor.getInt(RecipesContract.RecipeEntry.POSITION_ID);
        this.name = cursor.getString(RecipesContract.RecipeEntry.POSITION_NAME);
        this.servings = cursor.getInt(RecipesContract.RecipeEntry.POSITION_SERVINGS);
        this.image = cursor.getString(RecipesContract.RecipeEntry.POSITION_IMAGE);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    protected Recipe(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
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
