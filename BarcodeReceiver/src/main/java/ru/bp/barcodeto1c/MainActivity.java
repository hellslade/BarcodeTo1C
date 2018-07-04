package ru.bp.barcodeto1c;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final String TAG = "MainActivity";
    private static final String COMMAND = "Command";
    private static final String COMMAND_START = "START";
    private static final String COMMAND_STOP = "STOP";
    private static final String MESSAGE = "Message";

    private static final String VOICE_COMMMAND_START = "VOICE_RECOGNITION";
    private static final String SPEECH_COMMMAND_PLAY = "VOICE_SPEECH";
    private static final String SPEECH_MESSAGE = "SPEECH_MESSAGE";

    private static final int VR_REQUEST_CODE = 0x000999;
    private static final int SPEECH_REQUEST_CODE = 0x000998;

    private TextToSpeech repeatTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String toastMessage = "";
        Intent intent = getIntent();
        if (intent.hasExtra(COMMAND)) {
            String command = intent.getStringExtra(COMMAND);

            if (command.equalsIgnoreCase(COMMAND_START)) {
                // get command to start service
                this.startService(new Intent(this, BarcodeService.class));
            } else if (command.equalsIgnoreCase(COMMAND_STOP)) {
                // get command to stop service
                this.stopService(new Intent(this, BarcodeService.class));
            }
            if (intent.hasExtra(MESSAGE)) {
                toastMessage = intent.getStringExtra(MESSAGE);
                if (!toastMessage.isEmpty()) {
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
                }
            }
            /* Voice recognition */
            if (command.equalsIgnoreCase(VOICE_COMMMAND_START)) {
                if (checkRecognitionPossibility()) {
                    listenToSpeech();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.voice_recognition_no), Toast.LENGTH_LONG).show();
                }
            }
            if (command.equalsIgnoreCase(SPEECH_COMMMAND_PLAY)) {
                if (intent.hasExtra(SPEECH_MESSAGE)) {
                    String speech = intent.getStringExtra(SPEECH_MESSAGE);
                    if (!speech.isEmpty()) {
                        PlaySpeech(speech);
                    }
                }
            }
        }

        // create intent to check tts engine
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, SPEECH_REQUEST_CODE);

        // No need to show activity, it's must created for initialize receivers and service...
        finish();
    }

    private void PlaySpeech(String speech) {
        String utteranceId = "0x0001";
        repeatTTS.speak(speech, TextToSpeech.QUEUE_ADD, null, utteranceId);
    }

    private void listenToSpeech(){
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // message shown to user in В процессе распознования выводим сообщение
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,getResources().getString(R.string.voice_recognition_prompt));
        // we search model, like user speak for web apps (chrome, youtube, etc)
        // free form modek, common model, not specialized
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        // max recognition result returned
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);

        // start to listen
        startActivityForResult(listenIntent, VR_REQUEST_CODE);
    }

    private boolean checkRecognitionPossibility() {
        PackageManager packManager= getPackageManager();
        List<ResolveInfo> intActivities= packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        return intActivities.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // if return recognition result
        if (requestCode == VR_REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Add recognition words to a result
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            StringBuilder sb = new StringBuilder();
            for (String s : suggestedWords) {
                sb.append(s);
                sb.append(";");
            }
            // send broadcast to 1c
            sendIntentTo1C(sb.toString());
        }

        // returned from TTS data check
        if (requestCode == SPEECH_REQUEST_CODE) {
            // tts engine is set, create it
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                repeatTTS = new TextToSpeech(this, this);
            else {
                // tts engine is not install, try to get it
                // tts engine install intent from google play
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onInit(int initStatus){
        if (initStatus == TextToSpeech.SUCCESS) {
            Locale locale_ru = new Locale("ru","RU");
            repeatTTS.setLanguage(locale_ru);
        }
    }

    private void sendIntentTo1C(String text) {
        Intent intentFor1C = new Intent("com.google.android.c2dm.intent.RECEIVE");
        intentFor1C.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentFor1C.putExtra("text", "2");
        intentFor1C.putExtra("data", text);
        this.sendBroadcast(intentFor1C);
    }
}
