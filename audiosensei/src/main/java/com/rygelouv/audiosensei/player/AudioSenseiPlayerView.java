package com.rygelouv.audiosensei.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.rygelouv.audiosensei.R;

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

public class AudioSenseiPlayerView extends RelativeLayout
{
    public static final String TAG = AudioSenseiPlayerView.class.getSimpleName();

    View rootView;
    private SeekBar mSeekbarAudio;
    private View mPlayButton;
    private View mPauseButton;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private AudioTarget mTarget;
    private @LayoutRes int customLayout;
    private SparseArray<OnPlayerViewClickListener> playerViewClickListenersArray = new SparseArray<>();

    public AudioSenseiPlayerView(Context context)
    {
        super(context);
        init(context);
    }

    public AudioSenseiPlayerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getAttributes(context, attrs);
        init(context);
    }

    public AudioSenseiPlayerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioSenseiPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttributes(context, attrs);
        init(context);
    }

    private void getAttributes(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AudioSenseiPlayerView, 0, 0);
        customLayout = ta.getResourceId(R.styleable.AudioSenseiPlayerView_custom_layout, R.layout.default_audio_senei_player_view);
    }

    public void setAudioTarget(Uri uri)
    {
        mTarget = new AudioTarget.Builder().withLocalFile(uri).build();
    }

    public void setAudioTarget(int resource)
    {
        mTarget = new AudioTarget.Builder().withResource(resource).build();
    }

    public void setAudioTarget(String url)
    {
        mTarget = new AudioTarget.Builder().withRemoteUrl(url).build();
    }

    /**
     * Registers click listener for view by id
     *
     * @param viewId                     view
     * @param onPlayerViewClickListener click listener.
     */
    public void registerViewClickListener(int viewId, OnPlayerViewClickListener onPlayerViewClickListener) {
        Log.i(TAG, "view registered");
        this.playerViewClickListenersArray.append(viewId, onPlayerViewClickListener);
    }

    public void commitClickEvents()
    {
        for (int i = 0; i < playerViewClickListenersArray.size(); i++) {
            final int key = playerViewClickListenersArray.keyAt(i);
            final View view = rootView.findViewById(key);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerViewClickListenersArray.get(key).onPlayerViewClick(view);
                    }
                });
            }
        }
    }


    void init(Context context)
    {
        Log.i(TAG, "Init AudioPlayerSensei");
        rootView = inflate(context, customLayout, this);
        mPlayButton = findViewById(R.id.button_play);
        mPauseButton = findViewById(R.id.button_pause);
        mSeekbarAudio = findViewById(R.id.seekbar_audio);

        mPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayerAdapter != null)
                        {
                            mPlayerAdapter.pause();
                            mPlayButton.setVisibility(VISIBLE);
                            mPauseButton.setVisibility(GONE);
                        }
                    }
                });
        mPlayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayerAdapter == null)
                        {
                            initializeSeekbar();
                            initializePlaybackController();
                        }

                        if (mTarget != null)
                        {
                            if (!mPlayerAdapter.hasTarget(mTarget)) {
                                Log.i(TAG, "Does not Have same target...");
                                mPlayerAdapter.reset(false);
                                initializePlaybackController();
                                mPlayerAdapter.loadMedia(mTarget);
                            }
                            else Log.i(TAG, "has same target...");

                            mPlayerAdapter.play();
                            mPlayButton.setVisibility(GONE);
                            mPauseButton.setVisibility(VISIBLE);
                        }
                        else throw new RuntimeException("Audio Target not provided!");
                    }
                });
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = MediaPlayerHolder.getInstance(getContext());
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    private void reset()
    {
        mPlayButton.setVisibility(VISIBLE);
        mPauseButton.setVisibility(GONE);
    }

    public void stop()
    {
        if (mPlayerAdapter != null)
        {
            mPlayerAdapter.reset(false);
            mPlayerAdapter.release();
        }
    }

    public class PlaybackListener extends PlaybackInfoListener
    {
        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mSeekbarAudio.setProgress(position, true);
                }
                else {
                    mSeekbarAudio.setProgress(position);
                }
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        @Override
        public void onStateChanged(@PlaybackStateCompat.State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format("onStateChanged(%s)", stateToString));
            if (state == State.RESET)
                reset();
            else if (state == State.COMPLETED)
            {
                mPlayButton.setVisibility(VISIBLE);
                mPauseButton.setVisibility(GONE);
            }
        }

        @Override
        public void onPlaybackCompleted() {
        }

        @Override
        public void onLogUpdated(String message) {
            Log.i(TAG, message);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        Log.i(TAG,"on Detached Window called ==> detache AuioPlayer");
        if (mPlayerAdapter != null)
        {
            mPlayerAdapter.reset(false);
            mPlayerAdapter.release();
        }
    }
}
