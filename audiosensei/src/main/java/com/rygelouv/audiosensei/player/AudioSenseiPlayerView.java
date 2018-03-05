package com.rygelouv.audiosensei.player;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.rygelouv.audiosensei.R;

/**
 * Created by rygelouv on 3/1/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2018 Rygel Louv All rights reserved.
 */

public class AudioSenseiPlayerView extends RelativeLayout
{
    public static final String TAG = AudioSenseiPlayerView.class.getSimpleName();

    View rootView;
    private SeekBar mSeekbarAudio;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private AudioTarget mTarget;

    public AudioSenseiPlayerView(Context context)
    {
        super(context);
        init(context);
    }

    public AudioSenseiPlayerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public AudioSenseiPlayerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioSenseiPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
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

    void init(Context context)
    {
        Log.i(TAG, "Init AudioPlayerSensei");
        rootView = inflate(context, R.layout.default_audio_senei_player_view, this);
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

    public void reset()
    {
        mPlayButton.setVisibility(VISIBLE);
        mPauseButton.setVisibility(GONE);
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
        Log.i(TAG,"on Detached Window called...");
        mPlayerAdapter.release();
    }
}
