package com.rygelouv.audiosensei.player;

import java.util.concurrent.TimeUnit;

/**
 * Created by rygelouv on 3/28/18.
 * <p>
 * AndroidAudioSensei
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

public class PlayerUtils
{
    public static String getDurationFormated(int durationn)
    {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) durationn),
                TimeUnit.MILLISECONDS.toSeconds((long) durationn) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) durationn)));
    }
}
