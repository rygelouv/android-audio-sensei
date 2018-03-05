package com.rygelouv.audiosensei;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.rygelouv.audiosensei.player.PlaybackInfoListener;

import java.io.IOException;

/**
 * Created by rygelouv on 2/28/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2017 Makeba Inc All rights reserved.
 */

public class AudioSensei
{
    public static final String TAG = AudioSensei.class.getSimpleName();
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private @PlaybackInfoListener.State int mState;

    private static AudioSensei mInstance;

    public static synchronized AudioSensei getInstance()
    {
        if (mInstance == null)
            mInstance = new AudioSensei();

        return mInstance;
    }

    public @PlaybackInfoListener.State int getState()
    {
        return mState;
    }

    public void setState(@PlaybackInfoListener.State int mState)
    {
        this.mState = mState;
    }

    public void startRecording(Activity activity)
    {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        requestAudioPermissions(activity);
    }

    public void stopRecording()
    {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
    }

    private void requestAudioPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO)) {
                Log.i(TAG, "Requesting permission");

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            recordAudio();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    recordAudio();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "Permissions Denied to record audio");
                }
                return;
            }
        }
    }

    private void recordAudio()
    {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
        Log.i(TAG, "Audion Recording started");
    }

    public String getLastRecordedOutputFile()
    {
        return outputFile;
    }
}
