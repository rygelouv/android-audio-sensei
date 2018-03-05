package com.rygelouv.androidaudiosensei;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;

/**
 * Created by rygelouv on 3/1/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2018 Makeba Inc All rights reserved.
 */

public class PlayerActivity extends AppCompatActivity
{
    public static final String TAG = "PlayerActivity";
    public static final int MEDIA_RES_ID = R.raw.jazz_in_paris;

    private AudioSenseiPlayerView audioSenseiPlayerView1;
    private AudioSenseiPlayerView audioSenseiPlayerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initializeUI();
        Log.d(TAG, "onCreate: finished");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: create MediaPlayer");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeUI() {
        audioSenseiPlayerView1 = findViewById(R.id.player1);
        audioSenseiPlayerView1.setAudioTarget(MEDIA_RES_ID);

        audioSenseiPlayerView2 = findViewById(R.id.player2);
        audioSenseiPlayerView2.setAudioTarget(R.raw.francisco_tarrega_lagrima);
    }
}
