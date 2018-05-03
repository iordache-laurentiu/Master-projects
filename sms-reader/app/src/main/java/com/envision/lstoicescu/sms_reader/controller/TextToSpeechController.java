package com.envision.lstoicescu.sms_reader.controller;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.MainActivity;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class TextToSpeechController {
    public static TextToSpeechController singleton;

    private TextToSpeechController() {

    }

    public static TextToSpeechController getInstance() {
        if (singleton == null) {
            singleton = new TextToSpeechController();
        }
        return singleton;
    }


}
