package com.rygelouv.audiosensei.player;

import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by rygelouv on 3/2/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2018 Rygel Louv All rights reserved.
 */

public class AudioTarget
{
    public @Type int targetType;
    public Uri fileUri;
    public int resource;
    public String remoteUrl;

    public AudioTarget(AudioTarget.Builder builder)
    {
        this.fileUri = builder.fileUri;
        this.remoteUrl = builder.remoteUrl;
        this.resource = builder.resource;
        this.targetType = builder.targetType;
    }


    @IntDef({Type.INVALID, Type.RESOURCE, Type.LOCAL_FILE_URI, Type.REMOTE_FILE_URL,})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type
    {
        int RESOURCE = 1;
        int LOCAL_FILE_URI = 2;
        int REMOTE_FILE_URL = 3;
        int INVALID = -1;
    }

    public static class Builder
    {
        public Uri fileUri;
        public int resource;
        public String remoteUrl;
        public @Type int targetType;

        public Builder withLocalFile(Uri uri)
        {
            this.fileUri = uri;
            this.targetType = Type.LOCAL_FILE_URI;
            return this;
        }

        public Builder withRemoteUrl(String url)
        {
            this.remoteUrl = url;
            this.targetType = Type.REMOTE_FILE_URL;
            return this;
        }

        public Builder withResource(int res)
        {
            this.resource = res;
            this.targetType = Type.RESOURCE;
            return this;
        }

        public AudioTarget build()
        {
            return new AudioTarget(this);
        }
    }
}
