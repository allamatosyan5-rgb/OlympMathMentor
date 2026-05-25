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

public class Lesson1Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    // Այս փոփոխականը պահում է ընթացիկ չանկի մեջ որերորդ սիմվոլից պետք է սկսել կարդալ
    private int currentCharOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson1);

        Button btnStartTest = findViewById(R.id.btnStartTest);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.9f);
                    prepareTextChunks();
                    setupTTSListener();
                }
            }
        });

        btnReadLesson.setOnClickListener(v -> {
            if (!isInitialized || textChunks.isEmpty()) return;

            if (isPlaying) {
                textToSpeech.stop(); // Սա կանգնեցնում է ընթացիկ հնչողությունը
                isPlaying = false;
                btnReadLesson.setText("Resume Reading");
            } else {
                isPlaying = true;
                btnReadLesson.setText("Pause Reading");
                speakFromCurrentIndex();
            }
        });

        btnStartTest.setOnClickListener(v -> {
            if (textToSpeech != null) textToSpeech.stop();
            startActivity(new Intent(Lesson1Activity.this, Test1Activity.class));
            finish();
        });
    }

    private void prepareTextChunks() {
        textChunks.clear();
        int[] ids = {R.id.tvTitle, R.id.tvSubtitle1, R.id.tvContent1, R.id.tvExamples1,
                R.id.tvProperties1, R.id.tvProblem1Title, R.id.tvProblem1Text,
                R.id.tvSolution1, R.id.tvSubtitle2, R.id.tvContent2, R.id.tvProperties2};

        for (int id : ids) {
            TextView tv = findViewById(id);
            if (tv != null) textChunks.add(tv.getText().toString());
        }
    }

    private void speakFromCurrentIndex() {
        // Եթե սահմաններից դուրս է, զրոյացնում ենք
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
            currentCharOffset = 0;
        }

        // Կարդում ենք ընթացիկ չանկը՝ հաշվի առնելով offset-ը
        String currentText = textChunks.get(currentChunkIndex);
        String textToSpeak = currentText.substring(Math.min(currentCharOffset, currentText.length()));

        // Կարդում ենք հիմիկվա չանկը
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentChunkIndex));

        // Ավելացնում ենք հաջորդ չանկերը հերթում
        for (int i = currentChunkIndex + 1; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                currentChunkIndex = Integer.parseInt(utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                int id = Integer.parseInt(utteranceId);
                if (id == textChunks.size() - 1) {
                    currentChunkIndex = 0;
                    currentCharOffset = 0;
                    isPlaying = false;
                    runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                }
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                // `start`-ը ցույց է տալիս, թե որտեղից է սկսել հնչել հենց այս պահին
                // Քանի որ մենք կտրել ենք տեքստը substring-ով, պետք է գումարենք նախկին offset-ը
                currentCharOffset = Math.min(currentCharOffset, textChunks.get(Integer.parseInt(utteranceId)).length()) + start;
            }

            @Override
            public void onError(String utteranceId) {}
        });
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) textToSpeech.stop();
        super.onPause();
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