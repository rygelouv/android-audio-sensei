package com.rygelouv.audiosensei;

/**
 * Created by rygelouv on 3/12/18.
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
 */

public class ActivityRecorderNotProvidedException extends RuntimeException
{
    public ActivityRecorderNotProvidedException()
    {
        super("Activity not provided. Set activity on Recorder using with() method");
    }

    public ActivityRecorderNotProvidedException(String message) {
        throw new RuntimeException("Stub!");
    }

    public ActivityRecorderNotProvidedException(String message, Throwable cause) {
        throw new RuntimeException("Stub!");
    }

    public ActivityRecorderNotProvidedException(Throwable cause) {
        throw new RuntimeException("Stub!");
    }
}
