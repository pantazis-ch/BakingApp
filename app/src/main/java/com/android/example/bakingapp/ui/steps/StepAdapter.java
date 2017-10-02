package com.android.example.bakingapp.ui.steps;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.example.bakingapp.data.Step;

import java.util.ArrayList;


public class StepAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Step> steps;

    public StepAdapter(FragmentManager fm, ArrayList<Step> steps) {
        super(fm);
        this.steps = steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public Fragment getItem(int position) {
        return StepFragment.newInstance(steps.get(position), position);
    }
}
