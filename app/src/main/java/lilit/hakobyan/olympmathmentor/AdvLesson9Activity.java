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

public class AdvLesson9Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_lesson9);

        Button btnStartTest = findViewById(R.id.btnStartTest);
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

        btnStartTest.setOnClickListener(v -> {
            if (textToSpeech != null) textToSpeech.stop();
            Intent intent = new Intent(AdvLesson9Activity.this, AdvTest9Activity.class);
            startActivity(intent);
        });
    }

    private void prepareTextChunks() {
        textChunks.clear();
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvContent1)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvContent2)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSubtitle3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvContent3)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProblemTitle)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvProblemText)).getText().toString() + ". ");
        textChunks.add(((TextView) findViewById(R.id.tvSolution)).getText().toString() + ". ");
        textChunks.add("End of Advanced Lesson 9.");
    }

    private void speakFromCurrentIndex() {
        if (currentChunkIndex >= textChunks.size()) currentChunkIndex = 0;
        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                try { currentChunkIndex = Integer.parseInt(utteranceId); }
                catch (NumberFormatException ignored) {}
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
                } catch (NumberFormatException ignored) {}
            }
            @Override
            public void onError(String utteranceId) {}
        });
    }

    @Override
    public void onBackPressed() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) textToSpeech.stop();
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