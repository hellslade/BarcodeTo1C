package ru.bp.barcodeto1c;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSService extends Service implements TextToSpeech.OnInitListener {
    private static final String TAG = "TTSService";
    private TextToSpeech repeatTTS;

    TTSBinder mBinder = new TTSBinder();

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        repeatTTS = new TextToSpeech(this, this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        // Start service intent catch here. Starting work now, while stopSelf() don't called
        return Service.START_STICKY; // Restart service after system killed it
    }

    @Override
    public void onDestroy() {
        if (repeatTTS != null) {
            repeatTTS.stop();
            repeatTTS.shutdown();
        }
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    public void PlaySpeech(final String speech) {
        final String utteranceId = "0x0001";
        new Thread(new Runnable() {
            @Override
            public void run() {
                repeatTTS.speak(speech, TextToSpeech.QUEUE_ADD, null, utteranceId);
            }
        }).start();
    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            Locale locale_ru = new Locale("ru","RU");
            repeatTTS.setLanguage(locale_ru);
        }
    }

    class TTSBinder extends Binder {
        TTSService getService() {
            return TTSService.this;
        }
    }
}
