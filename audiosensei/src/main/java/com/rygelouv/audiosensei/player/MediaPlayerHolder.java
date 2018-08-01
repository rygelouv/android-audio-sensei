package com.rygelouv.audiosensei.player;

/**
 Copyright 2017 Rygelouv. Initial work by Google.

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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Exposes the functionality of the {@link MediaPlayer} and implements the {@link PlayerAdapter}
 * so that {@link AudioSenseiPlayerView} can control music playback.
 */
public final class MediaPlayerHolder implements PlayerAdapter, MediaActionHandler
{

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;
    private AudioTarget mTarget;

    private static MediaPlayerHolder mInstance;

    public static synchronized MediaPlayerHolder getInstance(Context context)
    {
        if (mInstance == null) {
            mInstance = new MediaPlayerHolder(context);
            AudioSenseiListObserver.getInstance().registerActionHandler(mInstance);
        }

        return mInstance;
    }

    public MediaPlayerHolder(Context context)
    {
        mContext = context.getApplicationContext();
    }

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {@link AudioSenseiPlayerView} the {@link MediaPlayer} is
     * released. Then in the onStart() of the {@link AudioSenseiPlayerView} a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    private void initializeMediaPlayer()
    {
        if (mMediaPlayer == null)
        {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer)
                {
                    stopUpdatingCallbackWithPosition(true);
                    logToUI("MediaPlayer playback completed");
                    if (mPlaybackInfoListener != null)
                    {
                        mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                        mPlaybackInfoListener.onPlaybackCompleted();
                    }
                }
            });
            logToUI("mMediaPlayer = new MediaPlayer()");
        }
    }

    @Override
    public void setPlaybackInfoListener(PlaybackInfoListener listener)
    {
        mPlaybackInfoListener = listener;
    }


    @Override
    public boolean hasTarget(AudioTarget audioTarget)
    {
        return mTarget == audioTarget;
    }

    private void removeTarget()
    {
        mTarget = null;
    }

    // Implements PlaybackControl.
    @Override
    public void loadMedia(AudioTarget target)
    {
        mTarget = target;
        initializeMediaPlayer();

        try
        {
            logToUI("load() {1. setDataSource}");

            switch (target.targetType)
            {
                case AudioTarget.Type.RESOURCE:
                    Log.e("MEDIAPLAY_HOLDER_TAG", "Type is RESOURCE");
                    AssetFileDescriptor assetFileDescriptor =
                            mContext.getResources().openRawResourceFd(target.resource);
                    mMediaPlayer.setDataSource(assetFileDescriptor);
                    break;
                case AudioTarget.Type.REMOTE_FILE_URL:
                    Log.e("MEDIAPLAY_HOLDER_TAG", "Type is REMOTE_FILE_URL");
                    mMediaPlayer.setDataSource(target.remoteUrl);
                    break;
                case AudioTarget.Type.LOCAL_FILE_URI:
                    Log.e("MEDIAPLAY_HOLDER_TAG", "Type is LOCAL_FILE_URI");
                    File audioFile = new File(target.fileUri.toString());
                    if (audioFile.exists())
                        mMediaPlayer.setDataSource(target.fileUri.toString());
                    break;
            }

        } catch (Exception e)
        {
            logToUI(e.toString());
            e.printStackTrace();
        }

        try
        {
            logToUI("load() {2. prepare}");
            mMediaPlayer.prepare();
        } catch (Exception e)
        {
            logToUI(e.toString());
            e.printStackTrace();
        }

        initializeProgressCallback();
        logToUI("initializeProgressCallback()");
    }

    @Override
    public void release()
    {
        if (mMediaPlayer != null)
        {
            logToUI("release() and mMediaPlayer = null");
            mMediaPlayer.release();
            mMediaPlayer = null;
            removeTarget();
        }
    }

    @Override
    public boolean isPlaying()
    {
        if (mMediaPlayer != null)
        {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play()
    {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying())
        {
            // logToUI(String.format("playbackStart() %s", mContext.getResources().getResourceEntryName(mTarget.resource)));

            mMediaPlayer.start();
            if (mPlaybackInfoListener != null)
            {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @Override
    public void reset(boolean reload)
    {
        if (mMediaPlayer != null)
        {
            logToUI("playbackReset()");
            mMediaPlayer.reset();
            if (reload)
                loadMedia(mTarget);
            if (mPlaybackInfoListener != null)
            {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause()
    {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null)
            {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
            logToUI("playbackPause()");
        }
    }

    @Override
    public void seekTo(int position)
    {
        if (mMediaPlayer != null)
        {
            logToUI(String.format("seekTo() %d ms", position));
            mMediaPlayer.seekTo(position);
        }
    }


    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition()
    {
        if (mExecutor == null)
        {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null)
        {
            mSeekbarPositionUpdateTask = new Runnable()
            {
                @Override
                public void run()
                {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition)
    {
        if (mExecutor != null)
        {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null)
            {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask()
    {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null)
            {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void initializeProgressCallback()
    {
        final int duration = mMediaPlayer.getDuration();
        if (mPlaybackInfoListener != null)
        {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
            logToUI(String.format("firing setPlaybackDuration(%d sec)",
                    TimeUnit.MILLISECONDS.toSeconds(duration)));
            logToUI("firing setPlaybackPosition(0)");
        }
    }

    private void logToUI(String message)
    {
        Log.i("MEDIAPLAY_HOLDER_TAG", message);
        if (mPlaybackInfoListener != null)
        {
            mPlaybackInfoListener.onLogUpdated(message);
        }
    }

    @Override
    public void onAction()
    {
        reset(false);
        release();
    }
}