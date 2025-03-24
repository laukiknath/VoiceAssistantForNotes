package com.example.voiceassistantfornotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private  static final int req_mic=1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText=findViewById(R.id.editTextOutput);
        imageButton=findViewById(R.id.imgBtn);

        checkMicPermission();

        intializeSpeech();

        imageButton.setOnClickListener(view -> startSpeechRecognition());

    }

    private void checkMicPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},req_mic);
        }
    }

    private void intializeSpeech(){
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        }
        else{
            Toast.makeText(MainActivity.this,"Speech Recognizer not present",Toast.LENGTH_SHORT).show();
        }
    }

    private  void startSpeechRecognition(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO
                )==PackageManager.PERMISSION_GRANTED){
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Toast.makeText(MainActivity.this,"Listening..",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    Toast.makeText(MainActivity.this,"End of Listening..",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int error) {
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches =results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if(matches !=null && !matches.isEmpty()){
                        String currenttxt=editText.getText().toString();
                        String newtxt=matches.get(0);

                        editText.setText(currenttxt+" "+newtxt);
                        editText.setSelection(editText.getText().length());
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}

            });
            speechRecognizer.startListening(intent);
        }
        else{
            Toast.makeText(MainActivity.this,"Microphone Permission required",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(speechRecognizer !=null){
            speechRecognizer.destroy();
        }
    }
}