package com.android.example.bakingapp;


import android.app.Instrumentation;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.ui.detail.DetailRecyclerAdapter;
import com.android.example.bakingapp.ui.main.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ValidateDataOnGUI {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    private IntentServiceIdlingResource idlingResource;

    @Before
    public void registerIntentServiceIdlingResource() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        idlingResource = new IntentServiceIdlingResource(instrumentation.getTargetContext());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIntentServiceIdlingResource() {
        Espresso.unregisterIdlingResources(idlingResource);
    }


    @Test
    public void ValidateData() {
        Context context = getInstrumentation().getTargetContext();

        Cursor recipeCursor = context.getContentResolver().query(
                RecipesContract.RecipeEntry.URI,
                null,
                null,
                null,
                RecipesContract.RecipeEntry._ID);

        for (int i = 0; i < recipeCursor.getCount(); i++) {
            recipeCursor.moveToPosition(i);

            String recipeName = recipeCursor.getString(RecipesContract.RecipeEntry.POSITION_NAME);
            onView(withText(recipeName)).check(matches(isDisplayed()));
        }

        Random r = new Random();

        int random = new Random().nextInt(recipeCursor.getCount());

        recipeCursor.moveToPosition(random);
        int recipeId = recipeCursor.getInt(RecipesContract.RecipeEntry.POSITION_ID);

        onView(ViewMatchers.withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(random,
                        click()));

        try{
            Thread.sleep(3000);
        } catch(InterruptedException e) {

        }

        Cursor ingredientCursor = context.getContentResolver().query(
                RecipesContract.IngredientEntry.URI,
                null,
                "recipe_id=?",
                new String[]{String.valueOf(recipeId)},
                RecipesContract.IngredientEntry._ID);


        for (int i = 0; i < ingredientCursor.getCount(); i++) {
            ingredientCursor.moveToPosition(i);

            String ingredientName = ingredientCursor.getString(RecipesContract.IngredientEntry.POSITION_INGREDIENT);

            onView(withText(ingredientName)).check(matches(isDisplayed()));
        }

        Cursor stepCursor = context.getContentResolver().query(
                RecipesContract.StepEntry.URI,
                null,
                "recipe_id=?",
                new String[]{String.valueOf(recipeId)},
                RecipesContract.StepEntry._ID);

        try{
            Thread.sleep(3000);
        } catch(InterruptedException e) {

        }

        for (int i = 0; i < stepCursor.getCount(); i++) {
            stepCursor.moveToPosition(i);

            String stepName = stepCursor.getString(RecipesContract.StepEntry.POSITION_SHORT_DESCRIPTION);
            onView(withId(R.id.rv_detail)).perform(
                    RecyclerViewActions.scrollToHolder(
                            stepHolderView(stepName)
                    )
            );

            onView(withText(stepName)).check(matches(isDisplayed()));

        }


        int randomPosition = new Random().nextInt(stepCursor.getCount());
        int randomStep = ingredientCursor.getCount() + 2 + randomPosition;

        onView(withId(R.id.rv_detail))
                .perform(RecyclerViewActions.actionOnItemAtPosition(randomStep, click()));

        try{
            Thread.sleep(3000);
        } catch(InterruptedException e) {

        }

        stepCursor.moveToPosition(randomPosition);

        String stepDescription = stepCursor.getString(RecipesContract.StepEntry.POSITION_DESCRIPTION);
        stepDescription = stepDescription.replaceFirst("\\d+. ", "");
        onView(withText(stepDescription)).check(matches(isDisplayed()));
    }

    public static Matcher<RecyclerView.ViewHolder> stepHolderView(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder, DetailRecyclerAdapter.StepViewHolder>(DetailRecyclerAdapter.StepViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("No ViewHolder found with text: " + text);
            }

            @Override
            protected boolean matchesSafely(DetailRecyclerAdapter.StepViewHolder item) {
                TextView stepTextView = (TextView) item.itemView.findViewById(R.id.tv_step);
                if (stepTextView == null) {
                    return false;
                }
                return stepTextView.getText().toString().contains(text);
            }
        };
    }

}
