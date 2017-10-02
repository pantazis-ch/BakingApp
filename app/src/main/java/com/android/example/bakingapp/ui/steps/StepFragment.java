package com.android.example.bakingapp.ui.steps;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;


public class StepFragment extends Fragment {

    public static StepFragment newInstance(Step step, int position) {
        StepFragment stepFragment = new StepFragment();

        Bundle args = new Bundle();
        args.putParcelable("step", step);
        args.putInt("position", position);

        stepFragment.setArguments(args);

        return stepFragment;
    }

    private Step step;
    private int position;

    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;

    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    private TextView stepDescriptionTextView;
    private TextView stepShortDescriptionTextView;

    private ImageView imageView;
    private TextView imageTexteView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        step = getArguments().getParcelable("step");
        position = getArguments().getInt("position");

        if (!step.getVideoURL().equals("")) {
            playerView = (SimpleExoPlayerView) getView().findViewById(R.id.video_view);
            playerView.setVisibility(View.VISIBLE);
        } else {
            imageView = (ImageView) getView().findViewById(R.id.image_view);
            imageView.setVisibility(View.VISIBLE);

            imageTexteView = (TextView) getView().findViewById(R.id.image_text);
            imageTexteView.setVisibility(View.VISIBLE);
        }

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        boolean hasDescription = (!isLandscape || isTablet);

        if(hasDescription) {
            stepShortDescriptionTextView =(TextView) getView().findViewById(R.id.tv_step_short_description);
            stepShortDescriptionTextView.setText((position + 1) + ". " + step.getShortDescription());

            stepDescriptionTextView =(TextView) getView().findViewById(R.id.tv_step_description);
            String description = step.getDescription();
            description = description.replaceFirst("\\d+. ", "");
            stepDescriptionTextView.setText(description);
        }

        if(savedInstanceState != null){
            playWhenReady = savedInstanceState.getBoolean("playWhenReady");
            currentWindow = savedInstanceState.getInt("currentWindow");
            playbackPosition = savedInstanceState.getLong("playbackPosition");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("playWhenReady", playWhenReady);
        outState.putInt("currentWindow", currentWindow);
        outState.putLong("playbackPosition", playbackPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (playerView !=null) {
            if (Util.SDK_INT > 23) {
                initializePlayer();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if (playerView!=null) {
            if ((Util.SDK_INT <= 23 || player == null)) {
                initializePlayer();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null) {
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(playerView !=null) {
            if (Util.SDK_INT > 23) {
                releasePlayer();
            }
        }

    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(step.getVideoURL());
        //Uri uri = Uri.parse("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4");
        //System.out.println("Uri parsed");

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
