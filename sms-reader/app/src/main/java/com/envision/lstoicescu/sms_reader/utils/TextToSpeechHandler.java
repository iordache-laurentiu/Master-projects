package com.envision.lstoicescu.sms_reader.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class TextToSpeechHandler {
    private static TextToSpeechHandler singleton;
    private TextToSpeech tts;

    private TextToSpeechHandler() {

    }

    public static TextToSpeechHandler getInstance() {
        if (singleton == null) {
            singleton = new TextToSpeechHandler();
        }
        return singleton;
    }

    public void createtts(Context context) {
        if (tts == null)
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.US);
                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("ERROR", "This Language is not supported");
                        } else {

                        }
                    } else
                        Log.e("ERROR", "Initilization Failed!");
                }
            });
    }

    public void convertTextToSpeech(String text) {
        if (text == null || "".equals(text)) {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void pauseTTS() {
        if (tts != null) {
            tts.stop();
        }
    }

    public void stopTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }


}
