package com.android.example.bakingapp.ui.detail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.example.bakingapp.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DetailFragment detailFragment = new DetailFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_container, detailFragment).commit();
    }

}
