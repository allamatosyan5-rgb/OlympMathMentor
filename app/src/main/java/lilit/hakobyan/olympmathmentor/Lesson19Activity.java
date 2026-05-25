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

public class Lesson19Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    // Դիրքը հիշելու փոփոխականներ
    private int currentCharOffset = 0;
    private int baseOffsetForCurrentChunk = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson19);

        Button btnGoToTest19 = findViewById(R.id.btnGoToTest19);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.85f);

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

        btnGoToTest19.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(Lesson19Activity.this, Test19Activity.class);
            startActivity(intent);
        });
    }

    private void prepareTextChunks() {
        textChunks.clear();
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1RuleTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1RuleText)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2FormulaTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Formula)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Formula)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Formulas)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvMasterTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S4)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Ans)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S4)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Ans)).getText().toString() + ". ");
        textChunks.add("End of lesson 19.");
    }

    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
            currentCharOffset = 0;
        }

        baseOffsetForCurrentChunk = currentCharOffset;

        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            String textToSpeak = textChunks.get(i);
            if (i == currentChunkIndex && currentCharOffset > 0 && currentCharOffset < textToSpeak.length()) {
                textToSpeak = textToSpeak.substring(currentCharOffset);
            }
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                try {
                    int id = Integer.parseInt(utteranceId);
                    if (currentChunkIndex != id) {
                        currentChunkIndex = id;
                        currentCharOffset = 0;
                        baseOffsetForCurrentChunk = 0;
                    }
                } catch (NumberFormatException e) { e.printStackTrace(); }
            }

            @Override
            public void onDone(String utteranceId) {
                try {
                    int id = Integer.parseInt(utteranceId);
                    if (id == textChunks.size() - 1) {
                        currentChunkIndex = 0;
                        currentCharOffset = 0;
                        baseOffsetForCurrentChunk = 0;
                        isPlaying = false;
                        runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                    }
                } catch (NumberFormatException e) { e.printStackTrace(); }
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                try {
                    int id = Integer.parseInt(utteranceId);
                    if (id == currentChunkIndex) {
                        currentCharOffset = baseOffsetForCurrentChunk + start;
                    } else {
                        currentCharOffset = start;
                    }
                } catch (NumberFormatException e) { e.printStackTrace(); }
            }

            @Override
            public void onError(String utteranceId) {}
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