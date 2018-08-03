package com.rygelouv.audiosensei.player;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/**
 * Created by rygelouv on 8/1/18.
 * <p>
 * AndroidAudioSensei
 *
 * Copyright 2017 Rygelouv.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
public class AudioSenseiListObserver implements LifecycleObserver
{
    private static AudioSenseiListObserver mInstance;
    private MediaActionHandler actionHandler;

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
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        Log.e("TAG", "================================>>>> lifecycle STARTED");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        Log.e("TAG", "================================>>>> lifecycle STOPED");
        if (this.actionHandler != null) {
            this.actionHandler.onAction();
        }
    }
}
