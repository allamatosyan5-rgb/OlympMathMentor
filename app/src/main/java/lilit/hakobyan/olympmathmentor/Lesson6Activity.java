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

public class Lesson6Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_lesson6);

        Button btnGoToTest6 = findViewById(R.id.btnGoToTest6);
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
                    textToSpeech.setSpeechRate(0.85f);

                    // Պատրաստում ենք տեքստերը և դնում լսող (listener)
                    prepareTextChunks();
                    setupTTSListener();
                }
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Կարդալու / Դադարի կոճակի սեղմումը
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
        btnGoToTest6.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(Lesson6Activity.this, Test6Activity.class);
            startActivity(intent);
        });
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Հավաքում է ամբողջ տեքստը ցուցակի մեջ (մաս-մաս)
    private void prepareTextChunks() {
        textChunks.clear();

        // Վերնագիր և Ենթավերնագիր
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle)).getText().toString() + ". ");

        // Բաժին 1
        textChunks.add(((TextView) findViewById(R.id.tvSec1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Text)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1FormulasTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Formulas)).getText().toString() + ". ");

        // Բաժին 2
        textChunks.add(((TextView) findViewById(R.id.tvSec2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text3)).getText().toString() + ". ");

        // Բաժին 3
        textChunks.add(((TextView) findViewById(R.id.tvSec3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec3Text3)).getText().toString() + ". ");

        // Բաժին 4
        textChunks.add(((TextView) findViewById(R.id.tvSec4Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec4Text3)).getText().toString() + ". ");

        // Բաժին 5
        textChunks.add(((TextView) findViewById(R.id.tvSec5Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec5Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec5Text2)).getText().toString() + ". ");

        // Բաժին 6
        textChunks.add(((TextView) findViewById(R.id.tvSec6Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec6Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec6Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec6Text3)).getText().toString() + ". ");

        // Բաժին 7
        textChunks.add(((TextView) findViewById(R.id.tvSec7Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec7Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec7Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec7Text3)).getText().toString() + ". ");

        // Մաստերկլաս և Խնդիր 1
        textChunks.add(((TextView) findViewById(R.id.tvMasterTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb1S4)).getText().toString() + ". ");

        // Խնդիր 2
        textChunks.add(((TextView) findViewById(R.id.tvProb2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S4)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb2S5)).getText().toString() + ". ");

        // Խնդիր 3
        textChunks.add(((TextView) findViewById(R.id.tvProb3Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3Q)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProb3S4)).getText().toString() + ". ");

        // Վերջնական խորհուրդ
        textChunks.add(((TextView) findViewById(R.id.tvAdviceTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvAdviceText)).getText().toString() + ". ");

        textChunks.add("End of lesson 6.");
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Շարունակում է կարդալ այնտեղից, որտեղ կանգնել էր
    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
        }

        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Հետևում է ընթացքին (որպեսզի պահպանի ինդեքսը)
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