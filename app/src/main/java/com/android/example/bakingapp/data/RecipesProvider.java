package com.android.example.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class RecipesProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private RecipesDbHelper recipesDbHelper;

    private static final int CODE_RECIPE = 100;
    private static final int CODE_RECIPE_ID = 101;

    private static final int CODE_INGREDIENT = 200;
    private static final int CODE_INGREDIENT_ID = 201;

    private static final int CODE_STEP = 300;
    private static final int CODE_STEP_ID = 301;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.AUTHORITY;

        matcher.addURI(authority, RecipesContract.PATH_RECIPES, CODE_RECIPE);
        matcher.addURI(authority, RecipesContract.PATH_RECIPES + "/#", CODE_RECIPE_ID);

        matcher.addURI(authority, RecipesContract.PATH_INGREDIENTS, CODE_INGREDIENT);
        matcher.addURI(authority, RecipesContract.PATH_INGREDIENTS + "/#", CODE_INGREDIENT_ID);

        matcher.addURI(authority, RecipesContract.PATH_STEPS, CODE_STEP);
        matcher.addURI(authority, RecipesContract.PATH_STEPS + "/#", CODE_STEP_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        recipesDbHelper = new RecipesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor mCursor;

        switch (uriMatcher.match(uri)) {

            case CODE_RECIPE_ID: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.RecipeEntry.TABLE_NAME,
                        projection,
                        RecipesContract.RecipeEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_RECIPE: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_INGREDIENT_ID: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.IngredientEntry.TABLE_NAME,
                        projection,
                        RecipesContract.IngredientEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_INGREDIENT: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_STEP_ID: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.StepEntry.TABLE_NAME,
                        projection,
                        RecipesContract.StepEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_STEP: {
                mCursor = recipesDbHelper.getReadableDatabase().query(
                        RecipesContract.StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        mCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return mCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = recipesDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case CODE_RECIPE: {
                long _id = db.insert(RecipesContract.RecipeEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RecipesContract.RecipeEntry.makeUriForRecipe(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CODE_INGREDIENT: {
                long _id = db.insert(RecipesContract.IngredientEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RecipesContract.IngredientEntry.makeUriForIngredient(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CODE_STEP: {
                long _id = db.insert(RecipesContract.StepEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RecipesContract.StepEntry.makeUriForStep(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = recipesDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {
            case CODE_RECIPE:
                rowsDeleted = db.delete(
                        RecipesContract.RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_INGREDIENT:
                rowsDeleted = db.delete(
                        RecipesContract.IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_STEP:
                rowsDeleted = db.delete(
                        RecipesContract.StepEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = recipesDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        int rowsUpdated;

        switch (match) {
            case CODE_RECIPE:
                rowsUpdated = db.update(RecipesContract.RecipeEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case CODE_INGREDIENT:
                rowsUpdated = db.update(RecipesContract.IngredientEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case CODE_STEP:
                rowsUpdated = db.update(RecipesContract.StepEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = recipesDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        switch (match) {
            case CODE_RECIPE: {

                db.beginTransaction();

                int returnCount = 0;

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipesContract.RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;

            } case CODE_INGREDIENT: {

                db.beginTransaction();

                int returnCount = 0;

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipesContract.IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;

            } case CODE_STEP:

                db.beginTransaction();

                int returnCount = 0;

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipesContract.StepEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

}
