package com.android.example.bakingapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class RecipePullIntentService extends IntentService {

    public RecipePullIntentService() {
        super("RecipePullIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RecipeSyncJob.getRecipes(getApplicationContext());
    }
}
