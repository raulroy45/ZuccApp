package com.example.zuccapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private boolean isListening;
    private TextView textView;
    private int counter = 0;
    private List<TextView> list;
    private ImageView micButton;
    private Intent intent;
    TextToSpeech tts;
    private static final String ENDPOINT_URL = "https://www.merovingian.cs.washington.edu:1104/";
    private static final int CONNECT_TIMEOUT = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        isListening = false;

        LinearLayout layout = findViewById(R.id.itemsLayout);

        getInventory();
        makeList(layout, new String[]{"lmfao", "kokok", "hello"});
        list.get(counter).setTextColor(Color.RED);

        micButton = (ImageView) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.text);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");

        tts = new TextToSpeech(getApplicationContext(), i -> {});
        tts.setLanguage(Locale.US);
        createSpeechRecognizer();
    }

    private void createSpeechRecognizer() {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                textView.setText(data.get(0));
                if (data.get(0).toLowerCase().equals("next")) {
                    // SEND AND HANDLE POST REQUEST
                    // START SPEAKING
                    // WHILE IS SPEAKING, WAIT IN LOOP
                    tts.speak("placed item", TextToSpeech.QUEUE_FLUSH, null);
                    list.get(counter).setTextColor(Color.GREEN);
                    counter++;
                    if (counter < list.size())
                        list.get(counter).setTextColor(Color.RED);
                    // WHEN NOT SPEAKING, THEN CREATE NEW SPEECH RECOGNIZER
                }
                createSpeechRecognizer();
                speechRecognizer.startListening(intent);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListening) {
                    speechRecognizer.startListening(intent);
                    isListening = true;
                } else {
                    speechRecognizer.stopListening();
                    isListening = false;
                }
            }
        });
    }

    private void checkPermission () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void getInventory() {
        // GET INVENTORY
        try {
            URL url = new URL(ENDPOINT_URL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                // Do normal input or output stream reading
                Toast.makeText(this, "Arre", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "RIP", Toast.LENGTH_SHORT).show(); // See documentation for more info on response handling
            }
            try {
                InputStream in = new BufferedInputStream(conn.getInputStream());

            } catch (Exception e){
                e.printStackTrace();
                conn.disconnect();
            }

//            try {
//                urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
//                urlConnection.connect();
//
//                Toast.makeText(this, "Kuch Granted", Toast.LENGTH_SHORT).show();
//                // Fetch the data and collect the response body.
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                // process result based on type
//                Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
//                urlConnection.disconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeList(LinearLayout layout, String[] items) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,16,0,0);
        int i = 0;
        list = new ArrayList<>();
        for (String s : items) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(s);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params);
            tv.setTextSize(20);
            tv.setId(i + 1);

            layout.addView(tv, params);
            list.add(tv);
        }
    }
}