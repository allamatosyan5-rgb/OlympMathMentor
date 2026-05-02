package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lesson4Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    // ՆՈՐ ՓՈՓՈԽԱԿԱՆՆԵՐ
    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson4);

        Button btnGoToTest4 = findViewById(R.id.btnGoToTest4);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.9f);

                    prepareTextChunks();
                    setupTTSListener();
                }
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        btnReadLesson.setOnClickListener(v -> {
            if (!isInitialized || textChunks.isEmpty()) return;

            if (isPlaying) {
                textToSpeech.stop();
                isPlaying = false;
                btnReadLesson.setText("Resume Reading");
            } else {
                isPlaying = true;
                btnReadLesson.setText("Pause Reading");
                speakFromCurrentIndex();
            }
        });

        btnGoToTest4.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(Lesson4Activity.this, Test4Activity.class);
            startActivity(intent);
        });
    }

    private void prepareTextChunks() {
        textChunks.clear();

        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Formula)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Formula)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvRules)).getText().toString() + ". ");

        textChunks.add("End of lesson 4.");
    }

    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
        }

        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                try {
                    currentChunkIndex = Integer.parseInt(utteranceId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDone(String utteranceId) {
                try {
                    int id = Integer.parseInt(utteranceId);
                    if (id == textChunks.size() - 1) {
                        currentChunkIndex = 0;
                        isPlaying = false;
                        runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String utteranceId) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}