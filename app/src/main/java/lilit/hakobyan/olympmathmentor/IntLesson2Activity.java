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

public class IntLesson2Activity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Button btnReadLesson;
    private boolean isInitialized = false;

    private List<String> textChunks;
    private int currentChunkIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_lesson2);

        textChunks = new ArrayList<>();

        // 1. ԱՊԱՀՈՎԱԳՐՎԱԾ ԹԵՍՏԻ ԿՈՃԱԿ
        // Փնտրում ենք և՛ հին, և՛ նոր ID-ներով, որ հաստատ գտնի
        Button btnStartTest = findViewById(R.id.btnStartTest);
        if (btnStartTest == null) {
            btnStartTest = findViewById(R.id.btnGoToTest2); // Սա մեր վերջին XML-ի կոճակն է
        }

        if (btnStartTest != null) {
            btnStartTest.setOnClickListener(v -> {
                if (textToSpeech != null) textToSpeech.stop();
                Intent intent = new Intent(IntLesson2Activity.this, IntTest2Activity.class);
                startActivity(intent);
            });
        }

        // 2. ԱՊԱՀՈՎԱԳՐՎԱԾ ԿԱՐԴԱԼՈՒ ԿՈՃԱԿ (TTS)
        btnReadLesson = findViewById(R.id.btnReadLesson);
        if (btnReadLesson != null) {
            btnReadLesson.setOnClickListener(v -> {
                if (!isInitialized || textChunks.isEmpty()) {
                    Toast.makeText(this, "No text to read or TTS not ready", Toast.LENGTH_SHORT).show();
                    return;
                }

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
        }

        // 3. TTS-Ի ՍԿԶԲՆԱՎՈՐՈՒՄ
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Եթե լեզուն չկա, ոչինչ չենք անում, որ չխանգարի
                } else {
                    isInitialized = true;
                    textToSpeech.setSpeechRate(0.85f);
                    prepareTextChunks();
                    setupTTSListener();
                }
            }
        });
    }

    // ԱՊԱՀՈՎ ՖՈՒՆԿՑԻԱ ՏԵՔՍՏԵՐԸ ԳՏՆԵԼՈՒ ՀԱՄԱՐ
    private void addTextSafe(int viewId) {
        TextView tv = findViewById(viewId);
        if (tv != null && tv.getText() != null) {
            textChunks.add(tv.getText().toString() + ". ");
        }
    }

    private void prepareTextChunks() {
        textChunks.clear();

        // Հիմա ծրագիրը ՉԻ ՔՐԱՇՎԻ, եթե անգամ այս ID-ներից որևէ մեկը պակասի XML-ում
        addTextSafe(R.id.tvTitle);
        addTextSafe(R.id.tvSubtitle1);
        addTextSafe(R.id.tvContent1);
        addTextSafe(R.id.tvSubtitle2);
        addTextSafe(R.id.tvContent2);
        addTextSafe(R.id.tvExamples1);
        addTextSafe(R.id.tvSubtitle3);
        addTextSafe(R.id.tvContent3);
        addTextSafe(R.id.tvProblem1Title);
        addTextSafe(R.id.tvProblem1Text);
        addTextSafe(R.id.tvSolution1);

        textChunks.add("End of Intermediate Lesson 2.");
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
                        if (btnReadLesson != null) {
                            runOnUiThread(() -> btnReadLesson.setText("Read Lesson Aloud"));
                        }
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