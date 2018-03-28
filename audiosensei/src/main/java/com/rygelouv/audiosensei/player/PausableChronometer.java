package com.rygelouv.audiosensei.player;



import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;


/**
 * Created by rygelouv on 3/28/18.
 * <p>
 * MakebaMoney
 * Copyright (c) 2017 Makeba Inc All rights reserved.
 */
public class PausableChronometer extends Chronometer {

    private long timeWhenStopped = 0;

    public PausableChronometer(Context context) {
        super(context);
    }

    public PausableChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PausableChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        timeWhenStopped = getBase() - SystemClock.elapsedRealtime();
    }

    public void reset() {
        stop();
        setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

    public long getCurrentTime() {
        return timeWhenStopped;
    }

    public void setCurrentTime(long time) {
        timeWhenStopped = time;
        setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
    }
}