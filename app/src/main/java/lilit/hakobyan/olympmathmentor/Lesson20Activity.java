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

public class Lesson20Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    // ՓՈՓՈԽԱԿԱՆՆԵՐ (Pause / Resume)
    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson20);

        Button btnGoToTest20 = findViewById(R.id.btnGoToTest20);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        // 1. TextToSpeech-ի սկզբնավորում
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.85f); // Կարդալու արագություն

                    // Պատրաստում ենք տեքստերը
                    prepareTextChunks();
                    setupTTSListener();
                }
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Կարդալու կոճակի սեղմումը
        btnReadLesson.setOnClickListener(v -> {
            if (!isInitialized || textChunks.isEmpty()) return;

            if (isPlaying) {
                // ԴԱԴԱՐ (Pause)
                textToSpeech.stop();
                isPlaying = false;
                btnReadLesson.setText("Resume Reading");
            } else {
                // ՇԱՐՈՒՆԱԿԵԼ (Resume/Play)
                isPlaying = true;
                btnReadLesson.setText("Pause Reading");
                speakFromCurrentIndex();
            }
        });

        // Թեստի էջին անցում
        btnGoToTest20.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(Lesson20Activity.this, Test20Activity.class);
            startActivity(intent);
        });
    }

    // Հավաքում է ամբողջ տեքստը ցուցակի մեջ (մաս-մաս)
    private void prepareTextChunks() {
        textChunks.clear();

        // Վերնագիր և Ենթավերնագիր
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle)).getText().toString() + ". ");

        // Բաժին 1
        textChunks.add(((TextView) findViewById(R.id.tvSec1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1CaseA)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1FormulaA)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1CaseB)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1FormulaB)).getText().toString() + ". ");

        // Բաժին 2
        textChunks.add(((TextView) findViewById(R.id.tvSec2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Formula)).getText().toString() + ". ");

        // Բաժին 3
        textChunks.add(((TextView) findViewById(R.id.tvSec3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Formula)).getText().toString() + ". ");

        // Բաժին 4
        textChunks.add(((TextView) findViewById(R.id.tvSec4Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4List)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Fact)).getText().toString() + ". ");

        // Մաստերկլաս
        textChunks.add(((TextView) findViewById(R.id.tvMasterTitle)).getText().toString() + ". ");

        // Խնդիր 1
        textChunks.add(((TextView) findViewById(R.id.tvProb1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Ans)).getText().toString() + ". ");

        // Խնդիր 2
        textChunks.add(((TextView) findViewById(R.id.tvProb2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S4)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Ans)).getText().toString() + ". ");

        // Խնդիր 3
        textChunks.add(((TextView) findViewById(R.id.tvProb3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S4)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3Ans)).getText().toString() + ". ");

        textChunks.add("End of lesson 20.");
    }

    // Շարունակում է կարդալ այնտեղից, որտեղ կանգնել էր
    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
        }

        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    // Հետևում է ընթացքին (որպեսզի պահպանի ինդեքսը)
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