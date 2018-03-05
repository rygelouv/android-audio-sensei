package com.rygelouv.audiosensei;

/**
 * Created by rygelouv on 2/28/18.
 * <p>
 * AndroidAudioSensei
 * Copyright (c) 2017 Makeba Inc All rights reserved.
 */

public interface RecordAudioPermissionCallback
{
    void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}
