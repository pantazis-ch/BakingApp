package com.android.example.bakingapp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.bakingapp.ui.detail.DetailActivity;
import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.Recipe;
import com.android.example.bakingapp.data.RecipesContract;
import com.android.example.bakingapp.sync.RecipePullIntentService;
import com.android.example.bakingapp.utilities.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        RecipeRecyclerAdapter.RecipeAdapterOnClickHandler {

    private static final String ACTION_SYNC_STARTED = "com.android.example.bakingapp.ACTION_SYNC_STARTED";
    private static final String ACTION_SYNC_ERROR = "com.android.example.bakingapp.ACTION_SYNC_ERROR";
    private static final String ACTION_DATA_UPDATED = "com.android.example.bakingapp.ACTION_DATA_UPDATED";

    private static final int ERROR_NO_CONNECTIVITY = 399;
    private static final int ERROR_DATA_PARSING = 400;
    private static final int ERROR_NO_DATA = 401;

    private static final int RECIPE_LOADER = 0;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_recipes)
    RecyclerView mRecyclerView;

    private RecipeRecyclerAdapter mAdapter;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.pb_recipe_loading)
    ProgressBar progressBar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_main_error)
    TextView errorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        //progressBar = (ProgressBar) findViewById(R.id.pb_recipe_loading);
        //errorTextView = (TextView) findViewById(R.id.tv_main_error) ;

        swipeRefreshLayout.setOnRefreshListener(this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SYNC_STARTED);
        intentFilter.addAction(ACTION_SYNC_ERROR);
        intentFilter.addAction(ACTION_DATA_UPDATED);

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if (action.equals(ACTION_SYNC_STARTED)) {
                    if(errorTextView.getVisibility() == View.VISIBLE) {
                        errorTextView.setVisibility(View.GONE);
                    }

                    if(swipeRefreshLayout.isRefreshing()){
                        if(progressBar.getVisibility() == View.VISIBLE){
                            progressBar.setVisibility(View.GONE);
                        }
                        return;
                    }

                    if (mAdapter == null || mAdapter.getItemCount() == 0) {
                            progressBar.setVisibility(View.VISIBLE);
                    }
                } else if (action.equals(ACTION_SYNC_ERROR)){

                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }

                    showErrorMessageIfNeeded(context, intent.getIntExtra("errorType", ERROR_DATA_PARSING));
                } else if (action.equals(ACTION_DATA_UPDATED)) {

                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }
        };

        setupRecyclerView();

        if(PrefUtils.isFirstTime(this)) {
            Intent msgIntent = new Intent(this, RecipePullIntentService.class);
            startService(msgIntent);
        }

        getSupportLoaderManager().initLoader(RECIPE_LOADER, null, this);

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);

        Intent msgIntent = new Intent(this, RecipePullIntentService.class);
        startService(msgIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                RecipesContract.RecipeEntry.URI,
                null,
                null,
                null,
                RecipesContract.RecipeEntry._ID);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if((cursor.getCount() != 0 || cursor != null)) {
            mAdapter.setCursor(cursor);
        } else {
            errorTextView.setText(getString(R.string.error_no_data));
            errorTextView.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    private void showErrorMessageIfNeeded(Context context, int errorCode) {

        if(mAdapter == null || mAdapter.getItemCount() == 0) {

            if( errorCode == ERROR_NO_CONNECTIVITY) {
                errorTextView.setText(getString(R.string.error_no_connectivity));
            } else {
                errorTextView.setText(getString(R.string.error_data_parsing));
            }

            errorTextView.setVisibility(View.VISIBLE);
        } else {
            if( errorCode == ERROR_NO_CONNECTIVITY) {
                Toast.makeText(this,getString(R.string.error_no_connectivity_short), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,getString(R.string.error_data_parsing_short), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("recipeName", recipe.getName());
        intent.putExtra("servings", recipe.getServings());
        startActivity(intent);
    }

    private void setupRecyclerView(){

        //mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipes);

        mAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        int noOfColumns = calculateNoOfColumns();
        if(noOfColumns > 2) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, noOfColumns));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        mRecyclerView.setHasFixedSize(true);

    }

    public int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
