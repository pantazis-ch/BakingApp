package com.android.example.bakingapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.example.bakingapp.R;


public final class PrefUtils {

    public static boolean isFirstTime(Context context) {

        String key = context.getString(R.string.pref_is_first_time_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isFirstTime = prefs.getBoolean(key, true);

        return isFirstTime;
    }

    public static void setIsFirstTime(Context context, boolean isFirstTime ) {
        String key = context.getString(R.string.pref_is_first_time_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, isFirstTime);
        editor.apply();
    }


    public static void setCurrentRecipePosition(Context context, int recipePosition ) {
        String key = context.getString(R.string.pref_current_recipe_position_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, recipePosition);
        editor.apply();
    }

    public static int getCurrentRecipePosition(Context context) {
        String key = context.getString(R.string.pref_current_recipe_position_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }


    public static void setRecipeCount(Context context, int recipeCount ) {
        String key = context.getString(R.string.pref_recipe_count_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, recipeCount);
        editor.apply();
    }

    public static int getRecipeCount(Context context) {
        String key = context.getString(R.string.pref_recipe_count_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }

}
