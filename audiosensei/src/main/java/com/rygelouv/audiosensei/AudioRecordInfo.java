package com.rygelouv.audiosensei;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.os.Environment.DIRECTORY_MUSIC;
import static com.rygelouv.audiosensei.AudioRecordInfo.AudioPath.APP_PRIVATE_AUDIO;
import static com.rygelouv.audiosensei.AudioRecordInfo.AudioPath.APP_PUBLIC_MUSIC;
import static com.rygelouv.audiosensei.AudioRecordInfo.AudioPath.PHONE_PUBLIC_MUSIC;

/**
 Created by rygelouv on 3/6/18.
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

public class AudioRecordInfo
{
    public String extension = ".3gp";

    public String name;
    public Activity activity;
    public @AudioPath int path;

    @IntDef({PHONE_PUBLIC_MUSIC, APP_PUBLIC_MUSIC, APP_PRIVATE_AUDIO,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioPath{
        int PHONE_PUBLIC_MUSIC = 1;
        int APP_PUBLIC_MUSIC = 2;
        int APP_PRIVATE_AUDIO = 3;
    }

    public String getProperPath()
    {
        switch (path)
        {
            case AudioPath.PHONE_PUBLIC_MUSIC:
                return Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + File.separator + name + extension;
            case AudioPath.APP_PUBLIC_MUSIC:
                return activity.getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath() + File.separator + name + extension;
            case AudioPath.APP_PRIVATE_AUDIO:
                Log.e("GAG", activity.getFilesDir().getAbsolutePath() + File.separator + "audios" + File.separator + name + extension);
                return activity.getFilesDir().getAbsolutePath() + File.separator + "audios" + File.separator + name + extension;
            default:
                return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name + extension;
        }
    }
}
