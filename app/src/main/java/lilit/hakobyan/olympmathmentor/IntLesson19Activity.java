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

public class IntLesson19Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;
    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_lesson19);

        Button btnStartTest = findViewById(R.id.btnStartTest);
        btnReadLesson = findViewById(R.id.btnReadLesson);
        textChunks = new ArrayList<>();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                isInitialized = true;
                prepareTextChunks();
                setupTTSListener();
            }
        });

        btnReadLesson.setOnClickListener(v -> {
            if (!isInitialized) return;
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
            startActivity(new Intent(this, IntTest19Activity.class));
        });
    }

    private void prepareTextChunks() {
        textChunks.clear();
        textChunks.add(((TextView) findViewById(R.id.tvTitle)).getText().toString());
        textChunks.add(((TextView) findViewById(R.id.tvContent1)).getText().toString());
        textChunks.add(((TextView) findViewById(R.id.tvContent2)).getText().toString());
        textChunks.add(((TextView) findViewById(R.id.tvContent3)).getText().toString());
        textChunks.add(((TextView) findViewById(R.id.tvContent4)).getText().toString());
        textChunks.add("End of Lesson Nineteen.");
    }

    private void speakFromCurrentIndex() {
        for (int i = currentChunkIndex; i < textChunks.size(); i++) {
            textToSpeech.speak(textChunks.get(i), TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
        }
    }

    private void setupTTSListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override public void onStart(String utteranceId) { currentChunkIndex = Integer.parseInt(utteranceId); }
            @Override public void onDone(String utteranceId) {
                if (Integer.parseInt(utteranceId) == textChunks.size() - 1) {
                    isPlaying = false;
                    currentChunkIndex = 0;
                    runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                }
            }
            @Override public void onError(String utteranceId) {}
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) { textToSpeech.stop(); textToSpeech.shutdown(); }
        super.onDestroy();
    }
}