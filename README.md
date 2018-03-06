# Introduction

Android Audio Sensei is a high-level library that aims to make working with audio on Android more simple. 
The reason is simple, when it comes to work with audio there is a lot of work to do before to get the feature you want
for example:
- Requesting permission before recording audio
- Handling the permission when granted
- Handling playback control (pause, play, stop) on the MediaPlayer with your UI
- Reporting the media playing progress using a seekbar or something similar 
- Ensure to release the MediaPlayer when not needed anymore

When it comes to do the same audio playing in a list like a Recyclerview, this becomes more complicated
- Stop and reset the current playing audio before playing another
- As Recyclerview's items are not directly bounded to the Activity/Fragment lifecycle, releasing the MediaPlayer may become a problem
- Most of the times on list of audio track (like in an Audion Player app or a chat app like whatsapp), 
wen you play hit the pause button, the play button appear and vice versa. Doing the same thing in a list items is 
a bit problematic
- Etc...

Audio Sensei take care of all of this for you and provide a very simple way to record and play audio even in a Recyclerview

<img src="https://github.com/Rygelouv/Android-Audio-Sensei/blob/master/device-2018-03-05-200415.png" width="200"> <img src="https://github.com/Rygelouv/android-audio-sensei/blob/master/device-2018-03-06-112730.png" width="200"> <img src="https://github.com/Rygelouv/android-audio-sensei/blob/master/device-2018-03-06-112821.png" width="200">


**AudioSensei library does not use any other library to do its job**

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.Rygelouv:android-audio-sensei:v0.0.3-beta'
}
```

# Recording audio

You simply need an instance of AudioSensei class to start recording

Using `Recorder()`

```java
AudioSensei.Recorder()
        .with(MainActivity.this)
        .name(UUID.randomUUID().toString())
        .to(AudioRecordInfo.AudioPath.APP_PUBLIC_MUSIC)
        .start();
```

## Handling runtime permission

When call `startRecording()`, AudioSensei check first if the `RECORD_AUDIO` permission is granted
if not the permission is requested so you don't have to do it yourself.

To make sure the AudioSensei will start recording after the permission is granted, you need to
override the method `onRequestPermissionsResult` and tell `AudioSensei` to do the rest

```java
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        AudioSensei.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```

## Stop recording
```java
AudioSensei.getInstance().stopRecording();
```

# Create a player view

Most of the time an audio player just follow the same pattern. You have 2 buttons, Play and Pause,
sometimes the stop button and the view that indicate the progress which mostly a SeekBar. 
After creating the view most of the next step is to get an instance of MediaPlayer and play/pause/stop
depending on which button is clicked. Handling the progress is done by using `MediaPlayer.getCurrentPosition()`

Now gess what you don't need to do any of that now. With AudioSensei library, 
you can just do this:

```xml
<com.rygelouv.audiosensei.player.AudioSenseiPlayerView
        android:id="@+id/audio_player"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
````

Even when you put this as item for a Recyclerview, nothing changes. AudioSensei knows if
a media is being played and will stop the current media in order to play the new one.

## Add custom layout

If you want to add you owm custom design (i encourage you to do so), you just need to provide your 
layout file via the attribute `app:custom_layout="@layout/custom_player"`

```xml
<com.rygelouv.audiosensei.player.AudioSenseiPlayerView
        android:id="@+id/audio_player"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:custom_layout="@layout/custom_player"
        />
````

#### To make this work properly you need to provide same id for player views:

- `button_play` : For play button
- `button_pause`: For pause button
- `seekbar_audio`: For SeekBar that shows audio progress


# TODO

This is library is in development. This what remains :
-  Fix some bugs
-  Customize the look and feel of the `AudioSenseiPlayerView`
-  Give the ability to provide a custom layout file for audioSenseiPlayerView
-  Allow to play audio in a background Service to keep playing even when the user leave the app or the current page


# Credits

Author: Rygel Louv [http://www.rygelouv.wordpress.com/](http://www.rygelouv.wordpress.com/)


License
--------

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