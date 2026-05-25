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

public class AdvLesson15Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    // Ընդմիջումից շարունակելու համար անհրաժեշտ փոփոխականներ
    private int currentCharOffset = 0;
    private int baseOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_lesson15);

        Button btnStartTest = findViewById(R.id.btnStartTest);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                if (textToSpeech.setLanguage(Locale.US) >= 0) {
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

        btnStartTest.setOnClickListener(v -> {
            if (textToSpeech != null) textToSpeech.stop();
            startActivity(new Intent(AdvLesson15Activity.this, AdvTest15Activity.class));
            finish();
        });
    }

    private void addTextSafe(int viewId) {
        TextView tv = findViewById(viewId);
        if (tv != null && tv.getText() != null) {
            textChunks.add(tv.getText().toString());
        }
    }

    private void prepareTextChunks() {
        textChunks.clear();
        addTextSafe(R.id.tvTitle);
        addTextSafe(R.id.tvSubtitle1);
        addTextSafe(R.id.tvContent1);
        addTextSafe(R.id.tvSubtitle2);
        addTextSafe(R.id.tvContent2);
        addTextSafe(R.id.tvSubtitle3);
        addTextSafe(R.id.tvContent3);
        addTextSafe(R.id.tvProblemTitle);
        addTextSafe(R.id.tvProblemText);
        addTextSafe(R.id.tvSolution);
        textChunks.add("End of Advanced Lesson 15.");
    }

    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) {
            currentChunkIndex = 0;
            currentCharOffset = 0;
            baseOffset = 0;
        }

        String currentText = textChunks.get(currentChunkIndex);
        baseOffset = Math.min(currentCharOffset, currentText.length());
        String textToSpeak = currentText.substring(baseOffset);

        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentChunkIndex));

        for (int i = currentChunkIndex + 1; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                int id = Integer.parseInt(utteranceId);
                if (id != currentChunkIndex) {
                    currentChunkIndex = id;
                    currentCharOffset = 0;
                    baseOffset = 0;
                }
            }

            @Override
            public void onDone(String utteranceId) {
                int id = Integer.parseInt(utteranceId);
                if (id == textChunks.size() - 1) {
                    currentChunkIndex = 0;
                    currentCharOffset = 0;
                    baseOffset = 0;
                    isPlaying = false;
                    runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                }
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                currentCharOffset = baseOffset + start;
            }

            @Override
            public void onError(String utteranceId) {}
        });
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) textToSpeech.stop();
        if (isPlaying) {
            isPlaying = false;
            runOnUiThread(() -> btnReadLesson.setText("Resume Reading"));
        }
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