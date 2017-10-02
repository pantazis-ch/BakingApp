package com.android.example.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class RecipesContract {

    public static final String AUTHORITY = "com.android.example.bakingapp";

    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);


    private RecipesContract() {
    }

    //@SuppressWarnings("unused")
    public static final class RecipeEntry implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "recipes";

        //public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";

        public static final int POSITION_ID = 0;
        public static final int POSITION_NAME = 1;
        public static final int POSITION_SERVINGS = 2;
        public static final int POSITION_IMAGE = 3;


        /*public static final ImmutableList<String> RECIPE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_NAME,
                COLUMN_SERVINGS,
                COLUMN_IMAGE
        );*/


        public static Uri makeUriForRecipe(long id) {
            return URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        public static String getRecipeIdFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }

    }

    public static final class IngredientEntry implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String TABLE_NAME = "ingredients";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        //public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";

        public static final int POSITION_ID = 0;
        public static final int POSITION_RECIPE_ID = 1;
        //public static final int POSITION_INGREDIENT_ID = 2;
        public static final int POSITION_QUANTITY = 2;
        public static final int POSITION_MEASURE = 3;
        public static final int POSITION_INGREDIENT = 4;


        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_RECIPE_ID,
                COLUMN_QUANTITY,
                COLUMN_COLUMN_MEASURE,
                COLUMN_INGREDIENT
        );*/


        public static Uri makeUriForIngredient(long id) {
            return URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        static String getIngredientIdFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }
    }

    public static final class StepEntry implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STEPS).build();

        public static final String TABLE_NAME = "steps";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_STEP_ID = "step_id";
        public static final String COLUMN_SHORT_DESCRIPTION = "shortDescription";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "videoURL";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailURL";

        public static final int POSITION_ID = 0;
        public static final int POSITION_RECIPE_ID = 1;
        public static final int POSITION_STEP_ID = 2;
        public static final int POSITION_SHORT_DESCRIPTION = 3;
        public static final int POSITION_DESCRIPTION = 4;
        public static final int POSITION_VIDEO_URL = 5;
        public static final int POSITION_THUMBNAIL_URL = 6;


        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_RECIPE_ID,
                COLUMN_STEP_ID,
                COLUMN_SHORT_DESCRIPTION,
                COLUMN_DESCRIPTION,
                COLUMN_VIDEO_URL,
                COLUMN_THUMBNAIL_URL
        );*/


        public static Uri makeUriForStep(long id) {
            return URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        static String getStepIdFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }
    }

}
