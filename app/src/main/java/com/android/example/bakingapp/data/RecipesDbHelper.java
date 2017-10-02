package com.android.example.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class RecipesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipes.db";

    private static final int DATABASE_VERSION = 1;


    public RecipesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold recipes.
        final String SQL_CREATE_RECIPES_TABLE =
                "CREATE TABLE " + RecipesContract.RecipeEntry.TABLE_NAME + " (" +
                        RecipesContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RecipesContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        RecipesContract.RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, " +
                        RecipesContract.RecipeEntry.COLUMN_IMAGE + " TEXT NOT NULL " +
                        "); ";

        // Create a table to hold ingredients.
        final String SQL_CREATE_INGREDIENTS_TABLE =
                "CREATE TABLE " + RecipesContract.IngredientEntry.TABLE_NAME + " (" +
                        RecipesContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RecipesContract.IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                        RecipesContract.IngredientEntry.COLUMN_QUANTITY + " REAL NOT NULL, " +
                        RecipesContract.IngredientEntry.COLUMN_MEASURE + " TEXT NOT NULL, " +
                        RecipesContract.IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL " +
                        "); ";

        // Create a table to hold steps.
        final String SQL_CREATE_STEPS_TABLE =
                "CREATE TABLE " + RecipesContract.StepEntry.TABLE_NAME + " (" +
                        RecipesContract.StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RecipesContract.StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                        RecipesContract.StepEntry.COLUMN_STEP_ID + " INTEGER NOT NULL, " +
                        RecipesContract.StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                        RecipesContract.StepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        RecipesContract.StepEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                        RecipesContract.StepEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL " +
                        "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEPS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesContract.IngredientEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesContract.StepEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
