package com.android.example.bakingapp.ui.steps;


import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.data.Step;

import java.util.ArrayList;


public class StepActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STEP_LOADER = 29;

    private StepAdapter mAdapter;
    private ViewPager mPager;

    private int recipeId;
    private String recipeName;
    private int currentPosition;

    private ArrayList<Step> steps;

    private TextView stepCounterTextView;
    private Button goToFirstButton;
    private Button goToLastButton;

    boolean isLandscape;
    boolean isTablet;

    boolean hasDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        recipeId = getIntent().getIntExtra("recipeId", 7);
        recipeName = getIntent().getStringExtra("recipeName");

        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPosition");
            steps = savedInstanceState.getParcelableArrayList("steps");
        } else {
            currentPosition = getIntent().getIntExtra("stepPosition", 0);
            steps = new ArrayList<Step>();
        }

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        isTablet = getResources().getBoolean(R.bool.isTablet);

        hasDescription = (!isLandscape || isTablet);

        if(hasDescription){
            stepCounterTextView = (TextView) findViewById(R.id.tv_step_indicator);
            stepCounterTextView.setText(getString(R.string.label_step) + " " + (currentPosition+1) + "/" + steps.size());
            setTitle(recipeName);
        } else {
            if (steps!=null && steps.size()!=0){
                setTitle(steps.get(currentPosition).getShortDescription());
            }
        }

        mAdapter = new StepAdapter(getSupportFragmentManager(), steps);

        mPager = (ViewPager)findViewById(R.id.step_pager);
        mPager.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(STEP_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(hasDescription) {
            goToFirstButton = (Button)findViewById(R.id.btn_goto_first);
            goToFirstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPager.setCurrentItem(0);
                }
            });

            goToLastButton = (Button)findViewById(R.id.btn_goto_last);
            goToLastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPager.setCurrentItem(steps.size() - 1);
                }
            });
        }

        ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // Do Nothing.

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // Do Nothing.
            }

            @Override
            public void onPageSelected(int pos) {
                if(hasDescription){
                    currentPosition = pos;
                    stepCounterTextView.setText(getString(R.string.label_step) + " " + (pos+1) + "/" + steps.size());
                } else {
                    setTitle((pos+1) + ". " + steps.get(mPager.getCurrentItem()).getShortDescription());
                }

            }

        };
        mPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(hasDescription) {
            goToFirstButton.setOnClickListener(null);
            goToLastButton.setOnClickListener(null);
        }

        mPager.addOnPageChangeListener(null);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("steps", steps);
        outState.putInt("currentPosition", mPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case STEP_LOADER:{
                return new CursorLoader(this,
                        RecipesContract.StepEntry.URI,
                        null,
                        "recipe_id = ?",
                        new String[]{String.valueOf(recipeId)},
                        RecipesContract.StepEntry._ID);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
            case STEP_LOADER:{

                steps.clear();

                for (int i = 0; i < data.getCount(); i++) {

                    data.moveToPosition(i);

                    int id = data.getInt(RecipesContract.StepEntry.POSITION_STEP_ID);
                    String shortDescription = data.getString(RecipesContract.StepEntry.POSITION_SHORT_DESCRIPTION);
                    String description = data.getString(RecipesContract.StepEntry.POSITION_DESCRIPTION);
                    String videoURL = data.getString(RecipesContract.StepEntry.POSITION_VIDEO_URL);
                    String thumbnailURL = data.getString(RecipesContract.StepEntry.POSITION_THUMBNAIL_URL);

                    Step step = new Step(id, shortDescription, description, videoURL, thumbnailURL);

                    steps.add(step);
                }

                mAdapter.notifyDataSetChanged();

                mPager.setCurrentItem(currentPosition);

                if(!hasDescription){
                    setTitle((currentPosition+1) + ". " + steps.get(currentPosition).getShortDescription());
                } else {
                    setTitle(recipeName);
                    if(currentPosition == 0) {
                        stepCounterTextView.setText(getString(R.string.label_step) + " " + "1/" + steps.size());
                    }
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        steps.clear();
    }

}
