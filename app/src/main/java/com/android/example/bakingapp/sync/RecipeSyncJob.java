package com.android.example.bakingapp.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.android.example.bakingapp.utilities.NetworkUtility;
import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.utilities.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public final class RecipeSyncJob {

    private static final String ACTION_SYNC_STARTED = "com.android.example.bakingapp.ACTION_SYNC_STARTED";
    private static final String ACTION_SYNC_ERROR = "com.android.example.bakingapp.ACTION_SYNC_ERROR";
    private static final String ACTION_DATA_UPDATED = "com.android.example.bakingapp.ACTION_DATA_UPDATED";

    private static final int ERROR_NO_CONNECTIVITY = 399;
    private static final int ERROR_DATA_PARSING = 400;

    private final static String RECIPE_NAME = "name";
    private final static String RECIPE_INGREDIENTS = "ingredients";
    private final static String RECIPE_STEPS = "steps";
    private final static String RECIPE_SERVINGS = "servings";
    private final static String RECIPE_IMAGE = "image";

    private final static String INGREDIENT_QUANTITY = "quantity";
    private final static String INGREDIENT_MEASURE = "measure";
    private final static String INGREDIENT_INGREDIENT = "ingredient";

    private final static String STEP_ID = "id";
    private final static String STEP_SHORT_DESCRIPTION = "shortDescription";
    private final static String STEP_DESCRIPTION = "description";
    private final static String STEP_VIDEO_URL = "videoURL";
    private final static String STEP_THUMBNAIL_URL = "thumbnailURL";

    private RecipeSyncJob() {
    }


    public static void getRecipes(Context context) {

        Intent syncStartedIntent = new Intent(ACTION_SYNC_STARTED);
        context.sendBroadcast(syncStartedIntent);


        URL url = NetworkUtility.buildUrl();
        String httpResponse = null;
        try {
            httpResponse = NetworkUtility.getResponseFromHttpUrl(url);
        } catch (IOException e) {

            Intent syncErrorIntent = new Intent(ACTION_SYNC_ERROR);
            syncErrorIntent.putExtra("errorType", ERROR_NO_CONNECTIVITY);
            context.sendBroadcast(syncErrorIntent);

            return;
        }


        JSONArray responseJsonArray = null;
        try {
            responseJsonArray = new JSONArray(httpResponse);
        } catch (JSONException e) {

            Intent syncErrorIntent = new Intent(ACTION_SYNC_ERROR);
            syncErrorIntent.putExtra("errorType", ERROR_DATA_PARSING);
            context.sendBroadcast(syncErrorIntent);

            return;
        }

        deletePreviousData(context);


        for (int i=0; i<responseJsonArray.length(); i++) {

            JSONObject recipeJSONObject = null;
            try {
                recipeJSONObject = responseJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String name = null;
            int servings = 0;
            String image = null;
            try {
                name = recipeJSONObject.getString(RECIPE_NAME);
                servings = recipeJSONObject.getInt(RECIPE_SERVINGS);
                image = recipeJSONObject.getString(RECIPE_IMAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ContentValues recipe = new ContentValues();
            recipe.put(RecipesContract.RecipeEntry.COLUMN_NAME, name);
            recipe.put(RecipesContract.RecipeEntry.COLUMN_SERVINGS, servings);
            recipe.put(RecipesContract.RecipeEntry.COLUMN_IMAGE, image);

            Uri recipeUri = context.getContentResolver().insert(RecipesContract.RecipeEntry.URI, recipe);

            int recipeId = Integer.parseInt(RecipesContract.RecipeEntry.getRecipeIdFromUri(recipeUri));


            JSONArray ingredientsJSONArray = null;
            try {
                ingredientsJSONArray = recipeJSONObject.getJSONArray(RECIPE_INGREDIENTS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<ContentValues> ingredients = new ArrayList<ContentValues>();

            for(int k=0; k<ingredientsJSONArray.length(); k++) {

                double quantity = 0;
                String measure = null;
                String ingredientName = null;
                try {
                    quantity = ingredientsJSONArray.getJSONObject(k).getDouble(INGREDIENT_QUANTITY);
                    measure = ingredientsJSONArray.getJSONObject(k).getString(INGREDIENT_MEASURE);
                    ingredientName = ingredientsJSONArray.getJSONObject(k).getString(INGREDIENT_INGREDIENT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ContentValues ingredient = new ContentValues();
                ingredient.put(RecipesContract.IngredientEntry.COLUMN_RECIPE_ID, recipeId);
                ingredient.put(RecipesContract.IngredientEntry.COLUMN_QUANTITY, quantity);
                ingredient.put(RecipesContract.IngredientEntry.COLUMN_MEASURE, measure);
                ingredient.put(RecipesContract.IngredientEntry.COLUMN_INGREDIENT, ingredientName);

                ingredients.add(ingredient);
            }

            context.getContentResolver().bulkInsert(RecipesContract.IngredientEntry.URI,
                    ingredients.toArray(new ContentValues[ingredients.size()]));


            JSONArray stepsJSONArray = null;
            try {
                stepsJSONArray = recipeJSONObject.getJSONArray(RECIPE_STEPS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int stepId = 0;
            String shortDescription = null;
            String description = null;
            String videoURL = null;
            String thumbnailURL = null;

            ArrayList<ContentValues> steps = new ArrayList<ContentValues>();

            for(int j=0; j<stepsJSONArray.length(); j++) {

                try {
                    stepId = stepsJSONArray.getJSONObject(j).getInt(STEP_ID);
                    shortDescription = stepsJSONArray.getJSONObject(j).getString(STEP_SHORT_DESCRIPTION);
                    description = stepsJSONArray.getJSONObject(j).getString(STEP_DESCRIPTION);
                    videoURL = stepsJSONArray.getJSONObject(j).getString(STEP_VIDEO_URL);
                    thumbnailURL = stepsJSONArray.getJSONObject(j).getString(STEP_THUMBNAIL_URL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ContentValues step = new ContentValues();
                step.put(RecipesContract.StepEntry.COLUMN_RECIPE_ID, recipeId);
                step.put(RecipesContract.StepEntry.COLUMN_STEP_ID, stepId);
                step.put(RecipesContract.StepEntry.COLUMN_SHORT_DESCRIPTION, shortDescription);
                step.put(RecipesContract.StepEntry.COLUMN_DESCRIPTION, description);
                step.put(RecipesContract.StepEntry.COLUMN_VIDEO_URL, videoURL);
                step.put(RecipesContract.StepEntry.COLUMN_THUMBNAIL_URL, thumbnailURL);

                steps.add(step);

            }

            context.getContentResolver().bulkInsert(RecipesContract.StepEntry.URI,
                    steps.toArray(new ContentValues[steps.size()]));
        }

        if(PrefUtils.isFirstTime(context)) {
            PrefUtils.setIsFirstTime(context, false);
        }

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);

    }

    public static void deletePreviousData(Context context ) {
        try {
            context.getContentResolver().delete(
                    RecipesContract.RecipeEntry.URI,
                    null,
                    null);

            context.getContentResolver().delete(
                    RecipesContract.IngredientEntry.URI,
                    null,
                    null);

            context.getContentResolver().delete(
                    RecipesContract.StepEntry.URI,
                    null,
                    null);
        } catch(SQLiteException e) {

        } catch (IllegalArgumentException e1) {

        }
    }

}
