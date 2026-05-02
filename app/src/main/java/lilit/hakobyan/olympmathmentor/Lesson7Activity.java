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

public class Lesson7Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    // ՆՈՐ ՓՈՓՈԽԱԿԱՆՆԵՐ
    private List<String> textChunks; // Տեքստի կտորների ցուցակ
    private int currentChunkIndex = 0; // Թե որերորդ կտորն է հիմա կարդում
    private boolean isPlaying = false; // Արդյոք հիմա կարդու՞մ է, թե դադարի մեջ է

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson7);

        Button btnGoToTest7 = findViewById(R.id.btnGoToTest7);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>(); // Սկզբնավորում ենք ցուցակը

        // 1. TextToSpeech-ի սկզբնավորում
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.85f);

                    // Պատրաստում ենք տեքստերը
                    prepareTextChunks();
                    // Հետևում ենք կարդալու ընթացքին
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
                // Եթե կարդում է -> ԴԱԴԱՐ (Pause)
                textToSpeech.stop(); // Կանգնեցնում ենք շարժիչը
                isPlaying = false;
                btnReadLesson.setText("Resume Reading"); // Փոխում ենք տեքստը «Շարունակել»
            } else {
                // Եթե կանգնած է -> ՍԿՍԵԼ կամ ՇԱՐՈՒՆԱԿԵԼ (Play / Resume)
                isPlaying = true;
                btnReadLesson.setText("Pause Reading"); // Փոխում ենք տեքստը «Դադար»
                speakFromCurrentIndex();
            }
        });

        // Թեստի էջին անցում
        btnGoToTest7.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(Lesson7Activity.this, Test7Activity.class);
            startActivity(intent);
        });
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Լցնում ենք ցուցակը տեքստի առանձին կտորներով
    private void prepareTextChunks() {
        textChunks.clear();
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1FormulasTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec1Formulas)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Title)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSec2Text3)).getText().toString() + ". ");
        // Այստեղ կարող ես ավելացնել էջի մնացած բոլոր տեքստերը՝ նույն տրամաբանությամբ...
        textChunks.add("End of lesson."); // Վերջաբան
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Շարունակում է կարդալ այնտեղից, որտեղ կանգնել էր
    private void speakFromCurrentIndex() {
        // Եթե հասել ենք վերջ, զրոյացնում ենք, որ սկսի սկզբից
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
        }

        // Հերթով ավելացնում ենք մնացած կտորները կարդալու հերթի մեջ (QUEUE_ADD)
        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            // Վերջին պարամետրը (String.valueOf(i)) հանդիսանում է կտորի ID-ն
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    // ՆՈՐ ՖՈՒՆԿՑԻԱ. Հետևում է, թե որ կտորն է կարդացվում հենց հիմա
    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Երբ սկսում է կարդալ նոր կտոր, պահպանում ենք դրա ID-ն (ինդեքսը)
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