package com.android.example.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.ui.detail.DetailActivity;
import com.android.example.bakingapp.utilities.PrefUtils;


public class IngredientListWidget extends AppWidgetProvider {

    private static final String ACTION_DATA_UPDATED = "com.android.example.bakingapp.ACTION_DATA_UPDATED";
    private static final String ACTION_NEXT_RECIPE = "com.android.example.bakingapp.ACTION_NEXT_RECIPE";
    private static final String ACTION_PREVIOUS_RECIPE = "com.android.example.bakingapp.ACTION_PREVIOUS_RECIPE";

    private Cursor cursor = null;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeId, String recipeName) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_list);

        views.setTextViewText(R.id.widget_recipe_name, recipeName);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("recipeId", recipeId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(appWidgetId, R.id.widget_ingredient_list, intent);
        views.setEmptyView(R.id.widget_ingredient_list, R.id.widget_empty_view);

        Intent nextRecipeIntent = new Intent(context, IngredientListWidget.class);
        nextRecipeIntent.setAction(IngredientListWidget.ACTION_NEXT_RECIPE);
        nextRecipeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent nextRecipePendingIntent = PendingIntent.getBroadcast(context, 1, nextRecipeIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_btn_next, nextRecipePendingIntent);

        views.setTextViewText(R.id.widget_recipe_counter, String.valueOf(PrefUtils.getRecipeCount(context)));

        int currentRecipe = PrefUtils.getCurrentRecipePosition(context) + 1;
        int recipeCount = PrefUtils.getRecipeCount(context);

        views.setTextViewText(R.id.widget_recipe_counter, currentRecipe + "/" + recipeCount);

        Intent previousRecipeIntent = new Intent(context, IngredientListWidget.class);
        previousRecipeIntent.setAction(IngredientListWidget.ACTION_PREVIOUS_RECIPE);
        previousRecipeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent previousRecipePendingIntent = PendingIntent.getBroadcast(context, 1, previousRecipeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_btn_previous, previousRecipePendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final long identityToken = Binder.clearCallingIdentity();

        if(cursor == null){
            cursor = context.getContentResolver().query(
                    RecipesContract.RecipeEntry.URI,
                    null,
                    null,
                    null,
                    RecipesContract.RecipeEntry._ID);
        }

        Binder.restoreCallingIdentity(identityToken);

        PrefUtils.setCurrentRecipePosition(context, 0);
        PrefUtils.setRecipeCount(context, cursor.getCount());

        cursor.moveToFirst();

        int currentRecipeId = cursor.getInt(RecipesContract.RecipeEntry.POSITION_ID);
        String currentRecipeName = cursor.getString(RecipesContract.RecipeEntry.POSITION_NAME);


        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, currentRecipeId, currentRecipeName);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final long identityToken = Binder.clearCallingIdentity();

        if(cursor == null){
            cursor = context.getContentResolver().query(
                    RecipesContract.RecipeEntry.URI,
                    null,
                    null,
                    null,
                    RecipesContract.RecipeEntry._ID);
        }

        Binder.restoreCallingIdentity(identityToken);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_list);

        String action = intent.getAction();

        if (action.equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            onUpdate(context, appWidgetManager, appWidgetIds);
        }

        if (action.equals(ACTION_NEXT_RECIPE)) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

            int currentPosition = PrefUtils.getCurrentRecipePosition(context);
            int recipeCount = PrefUtils.getRecipeCount(context);

            if (currentPosition < recipeCount - 1) {

                currentPosition++;
                PrefUtils.setCurrentRecipePosition(context, currentPosition);

                cursor.moveToPosition(currentPosition);

                int currentRecipeId = cursor.getInt(RecipesContract.RecipeEntry.POSITION_ID);
                String currentRecipeName = cursor.getString(RecipesContract.RecipeEntry.POSITION_NAME);

                updateAppWidget(context,appWidgetManager,appWidgetId, currentRecipeId, currentRecipeName);
            }


        }

        if (action.equals(ACTION_PREVIOUS_RECIPE)) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

            int currentPosition = PrefUtils.getCurrentRecipePosition(context);
            int recipeCount = PrefUtils.getRecipeCount(context);

            if(currentPosition > 0) {
                currentPosition--;
                PrefUtils.setCurrentRecipePosition(context, currentPosition);
                cursor.moveToPosition(currentPosition);

                int currentRecipeId = cursor.getInt(RecipesContract.RecipeEntry.POSITION_ID);
                String currentRecipeName = cursor.getString(RecipesContract.RecipeEntry.POSITION_NAME);

                updateAppWidget(context,appWidgetManager,appWidgetId, currentRecipeId, currentRecipeName);
            }
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

