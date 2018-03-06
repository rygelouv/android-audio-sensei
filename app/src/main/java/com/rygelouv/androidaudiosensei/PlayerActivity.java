package com.rygelouv.androidaudiosensei;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;

/**
 Created by rygelouv on 3/1/18.
 Copyright 2017 Rygelouv.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/
public class PlayerActivity extends AppCompatActivity
{
    public static final String TAG = "PlayerActivity";
    public static final int MEDIA_RES_ID = R.raw.jazz_in_paris;

    private AudioSenseiPlayerView audioSenseiPlayerView1;
    //private AudioSenseiPlayerView audioSenseiPlayerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Custom AudioSensei player");
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

        /*audioSenseiPlayerView2 = findViewById(R.id.player2);
        audioSenseiPlayerView2.setAudioTarget(R.raw.francisco_tarrega_lagrima);*/
    }
}
