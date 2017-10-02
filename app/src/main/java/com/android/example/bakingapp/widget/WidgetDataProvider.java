package com.android.example.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.RecipesContract;

import java.text.DecimalFormat;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Cursor cursor = null;

    private Context mContext = null;

    private int recipeId;



    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        recipeId = intent.getIntExtra("recipeId", -1);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        if (cursor != null) {
            cursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();

        cursor = mContext.getContentResolver().query(
                RecipesContract.IngredientEntry.URI,
                null,
                "recipe_id=?",
                new String[]{String.valueOf(recipeId)},
                RecipesContract.IngredientEntry._ID);

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list_item);

        cursor.moveToPosition(position);

        double quantity = cursor.getDouble(RecipesContract.IngredientEntry.POSITION_QUANTITY);
        DecimalFormat quantityFormat = new DecimalFormat("0.#");

        String measure = cursor.getString(RecipesContract.IngredientEntry.POSITION_MEASURE);
        if (measure.equals("UNIT")) {
            measure = "";
        }

        String ingredientStr = cursor.getString(RecipesContract.IngredientEntry.POSITION_INGREDIENT);

        views.setTextViewText(R.id.widget_tv_ingredient_quantity, quantityFormat.format(quantity) + " " + measure);
        views.setTextViewText(R.id.widget_tv_ingredient, ingredientStr);

        //Bundle extras = new Bundle();
        //extras.putString(CollectionWidget.EXTRA_ITEM, symbol);

        //Intent fillInIntent = new Intent();
        //fillInIntent.putExtras(extras);

        //views.setOnClickFillInIntent(R.id.list_item, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        //return 0;
        return 1;
    }

    @Override
    public long getItemId(int position) {
        //return 0;
        return position;
    }

    @Override
    public boolean hasStableIds() {
        //return false;
        return true;
    }
}
