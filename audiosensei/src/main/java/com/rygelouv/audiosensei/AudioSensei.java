package com.rygelouv.audiosensei;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by rygelouv on 2/28/18.
 * <p>
 * AndroidAudioSensei library
 * <p>
 * Copyright 2017 Rygelouv. Initial work by Google.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

public class AudioSensei
{
    public static final String TAG = AudioSensei.class.getSimpleName();
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private MediaRecorder mAudioRecorder;
    private String outputFile;

    private AudioRecordInfo mAudioRecordInfo;

    private static AudioSensei mInstance;

    public static synchronized AudioSensei getInstance()
    {
        if (mInstance == null)
            mInstance = new AudioSensei();

        return mInstance;
    }

    private void startRecording(AudioRecordInfo audioRecordInfo)
    {
        this.mAudioRecordInfo = audioRecordInfo;
        this.outputFile = mAudioRecordInfo.getProperPath();
        requestAudioPermissions(mAudioRecordInfo.activity);
    }

    public void stopRecording()
    {
        if (this.mAudioRecorder != null)
        {
            this.mAudioRecorder.stop();
            this.mAudioRecorder.release();
            this.mAudioRecorder = null;
            this.mAudioRecordInfo = null;
        }
    }

    public void cancelRecording()
    {
        stopRecording();
        try {
            if (RecorderUtils.deleteLastFile(new File(getLastRecordedOutputFile())))
                Log.i(TAG, "File deleted");
            else
                Log.e(TAG, "File cannot be deleted");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestAudioPermissions(Activity activity)
    {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO))
            {
                Log.i(TAG, "Requesting permission");
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            } else
            {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
        {
            //Go ahead with recording audio now
            recordAudio();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_RECORD_AUDIO:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted!
                    recordAudio();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.i(TAG, "Permissions Denied to record audio");
                }
                return;
            }
        }
    }

    private void recordAudio()
    {
        this.mAudioRecorder = new MediaRecorder();
        this.mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        this.mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        this.mAudioRecorder.setOutputFile(outputFile);

        try {
            this.mAudioRecorder.prepare();
            this.mAudioRecorder.start();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Log.i(TAG, "Audion Recording started");
    }

    public String getLastRecordedOutputFile()
    {
        Log.e(TAG, !TextUtils.isEmpty(this.outputFile) ? this.outputFile : "Empty output file");
        return this.outputFile;
    }

    public static Recorder Recorder()
    {
        return new Recorder();
    }

    public static class Recorder
    {
        private AudioRecordInfo audioRecordInfo;

        public Recorder() {
            audioRecordInfo = new AudioRecordInfo();
        }

        public Recorder with(Activity activity){
            this.audioRecordInfo.activity = activity;
            return this;
        }

        public Recorder name(String name){
            this.audioRecordInfo.name = name;
            return this;
        }

        public Recorder to(@AudioRecordInfo.AudioPath int audioPath){
            this.audioRecordInfo.path = audioPath;
            return this;
        }

        public void start()
        {
            if (this.audioRecordInfo.activity == null)
                throw new ActivityRecorderNotProvidedException();
            if (TextUtils.isEmpty(this.audioRecordInfo.name))
                this.audioRecordInfo.name = UUID.randomUUID().toString();
            if (this.audioRecordInfo.path == 0)
                this.audioRecordInfo.path = AudioRecordInfo.AudioPath.PHONE_PUBLIC_MUSIC;

            getInstance().startRecording(this.audioRecordInfo);
        }
    }
}
