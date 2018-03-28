package com.rygelouv.audiosensei.player;

import java.util.concurrent.TimeUnit;

/**
 * Created by rygelouv on 3/28/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2017 Makeba Inc All rights reserved.
 */

public class PlayerUtils
{
    public static String getDurationFormated(int durationn)
    {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) durationn),
                TimeUnit.MILLISECONDS.toSeconds((long) durationn) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) durationn)));
    }
}
