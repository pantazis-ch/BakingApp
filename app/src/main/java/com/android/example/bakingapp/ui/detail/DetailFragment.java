package com.android.example.bakingapp.ui.detail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.ui.steps.StepActivity;
import com.android.example.bakingapp.ui.steps.StepFragment;
import com.android.example.bakingapp.data.Ingredient;
import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.data.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        DetailRecyclerAdapter.StepOnClickHandler {

    private int recipeId;
    String recipeName;

    private final int INGREDIENT_LOADER = 100;
    private final int STEP_LOADER = 101;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_detail)
    RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;

    private DetailRecyclerAdapter mAdapter;

    public ArrayList<Ingredient> ingredients = null;
    public ArrayList<Step> steps = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,getView());

        recipeId = getActivity().getIntent().getIntExtra("recipeId", 7);
        recipeName = getActivity().getIntent().getStringExtra("recipeName");

        getActivity().setTitle(recipeName);

        setupRecyclerView();

        getActivity().getSupportLoaderManager().initLoader(INGREDIENT_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(STEP_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case INGREDIENT_LOADER:{
                return new CursorLoader(getContext(),
                        RecipesContract.IngredientEntry.URI,
                        null,
                        "recipe_id=?",
                        new String[]{String.valueOf(recipeId)},
                        RecipesContract.IngredientEntry._ID);
            }
            case STEP_LOADER:{
                return new CursorLoader(getContext(),
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

            case INGREDIENT_LOADER:{
                ingredients.clear();

                for (int i = 0; i < data.getCount(); i++) {
                    data.moveToPosition(i);

                    double quantity = data.getDouble(RecipesContract.IngredientEntry.POSITION_QUANTITY);
                    String measure = data.getString(RecipesContract.IngredientEntry.POSITION_MEASURE);
                    String ingredientStr = data.getString(RecipesContract.IngredientEntry.POSITION_INGREDIENT);

                    Ingredient ingredient = new Ingredient(quantity, measure, ingredientStr);

                    ingredients.add(ingredient);

                }

                mAdapter.notifyDataSetChanged();

                break;
            }
            case STEP_LOADER:{

                steps.clear();

                for (int i = 0; i < data.getCount(); i++) {

                    data.moveToPosition(i);

                    int id = data.getInt(RecipesContract.StepEntry.POSITION_ID);
                    String shortDescription = data.getString(RecipesContract.StepEntry.POSITION_SHORT_DESCRIPTION);
                    String description = data.getString(RecipesContract.StepEntry.POSITION_DESCRIPTION);
                    String videoURL = data.getString(RecipesContract.StepEntry.POSITION_VIDEO_URL);
                    String thumbnailURL = data.getString(RecipesContract.StepEntry.POSITION_THUMBNAIL_URL);

                    Step step = new Step(id, shortDescription, description, videoURL, thumbnailURL);

                    steps.add(step);
                }

                mAdapter.notifyDataSetChanged();

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mAdapter.setIngredients(null);
        //mAdapter.setSteps(null);
    }

    private void setupRecyclerView(){

        ingredients = new ArrayList<Ingredient>();
        steps = new ArrayList<Step>();

        //mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv_detail);

        mAdapter = new DetailRecyclerAdapter(getContext(),ingredients, steps, this, getActivity().getIntent().getIntExtra("servings",8));
        mRecyclerView.setAdapter(mAdapter);

        //mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onClick(Step step, int stepPosition) {

        if(getActivity().findViewById(R.id.step_container) == null) {
            Intent intent = new Intent(getContext(), StepActivity.class);
            intent.putExtra("recipeId", recipeId);
            intent.putExtra("recipeName", recipeName);
            intent.putExtra("stepPosition", stepPosition);
            startActivity(intent);
        } else {
            StepFragment stepFragment = StepFragment.newInstance(step, stepPosition);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.step_container, stepFragment);
            transaction.addToBackStack(null);

            transaction.commit();

        }
    }
}
