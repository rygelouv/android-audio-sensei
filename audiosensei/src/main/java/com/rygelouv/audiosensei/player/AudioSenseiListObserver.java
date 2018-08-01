package com.rygelouv.audiosensei.player;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/**
 * Created by rygelouv on 8/1/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2018 Makeba Inc All rights reserved.
 */
public class AudioSenseiListObserver implements LifecycleObserver
{
    private static AudioSenseiListObserver mInstance;
    private MediaActionHandler actionHandler;
    private Lifecycle lifecycle;

    public static synchronized AudioSenseiListObserver getInstance() {
        if (mInstance == null) {
            mInstance = new AudioSenseiListObserver();
        }
        return mInstance;
    }

    public void registerActionHandler(MediaActionHandler handler) {
        this.actionHandler = handler;
    }

    public void registerLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        Log.e("TAG", "================================>>>> lifecyle STARTED");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        Log.e("TAG", "================================>>>> lifecycle STOPED");
        this.actionHandler.onAction();
    }
}
