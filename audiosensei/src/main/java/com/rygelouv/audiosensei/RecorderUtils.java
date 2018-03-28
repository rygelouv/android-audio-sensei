package com.rygelouv.audiosensei;

import java.io.File;

/**
 * Created by rygelouv on 3/28/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2017 Makeba Inc All rights reserved.
 */

public class RecorderUtils
{
    public static boolean deleteLastFile(File file)
    {
        if (file.exists())
            return file.delete();
        return false;
    }
}
