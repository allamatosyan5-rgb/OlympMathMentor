package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Test12Activity extends AppCompatActivity {

    private String[] questions = {
            "1. How many people must be in a room to guarantee that at least TWO were born in the same month?",
            "2. A drawer has 12 red socks and 12 blue socks. Minimum to draw in the dark to guarantee a pair?",
            "3. A drawer has 10 Left gloves and 10 Right gloves. Minimum to guarantee a pair?",
            "4. If you put 100 pigeons in 9 holes, at least one hole has EXACTLY or MORE than how many pigeons?",
            "5. If you pick 5 numbers randomly, is it guaranteed that two of them have a difference divisible by 4? (1=YES, 0=NO)",
            "6. In a party of 10 people, is it guaranteed two people have the exact same number of friends? (1=YES, 0=NO)",
            "7. You place 5 points inside a 1x1 square. Is it guaranteed that two points are at distance ≤ 0.75? (1=YES, 0=NO)",
            "8. Minimum cards to draw from a 52-card deck to guarantee 3 cards of the SAME suit?",
            "9. You have 3 colors of balls. How many to pick to guarantee 5 of the same color?",
            "10. A computer generates numbers from 1 to 10. How many must it generate to guarantee the same number appeared 3 times?"
    };

    private String[] correctAnswers = {"13", "3", "11", "12", "1", "1", "1", "9", "13", "21"};
    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test12);

        LinearLayout container = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            final int index = i;
            LinearLayout header = new LinearLayout(this);
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setPadding(0, 30, 0, 10);

            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[index]);
            tvQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvHeart = new TextView(this);
            tvHeart.setPadding(20, 20, 20, 20);
            tvHeart.setTextSize(22f);
            tvHeart.setText(isFavourite(questions[index], correctAnswers[index]) ? "❤️" : "🤍");
            tvHeart.setOnClickListener(v -> toggleFavourite(questions[index], correctAnswers[index], tvHeart));

            header.addView(tvQuestion);
            header.addView(tvHeart);
            container.addView(header);

            answerInputs[index] = new EditText(this);
            answerInputs[index].setHint("Type a number...");
            container.addView(answerInputs[index]);

            feedbackViews[index] = new TextView(this);
            feedbackViews[index].setVisibility(View.GONE);
            container.addView(feedbackViews[index]);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            startActivity(new Intent(Test12Activity.this, Lesson13Activity.class));
            finish();
        });
        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
    }

    private void checkResults() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String ans = answerInputs[i].getText().toString().trim().replaceAll("\\s+", "");
            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);
            if (ans.equals(correctAnswers[i])) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#2E7D32"));
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
                saveWrongQuestion(questions[i], correctAnswers[i]);
            }
        }
        showFinalResult(score);
    }

    private void showFinalResult(int score) {
        if (isFinishing() || isDestroyed()) return;

        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        View resultLayout = findViewById(R.id.resultLayout);
        if (resultLayout != null) resultLayout.setVisibility(View.VISIBLE);

        TextView tvScore = findViewById(R.id.tvScore);
        if (tvScore != null) tvScore.setText("Score: " + score + "/10");

        int stars = (score == 10) ? 3 : (score >= 8) ? 2 : (score >= 6) ? 1 : 0;

        if (score >= 6) {
            View btnNext = findViewById(R.id.btnNextLesson);
            if(btnNext != null) btnNext.setVisibility(View.VISIBLE);

            int[] medalIds = {R.id.medal1, R.id.medal2, R.id.medal3};
            for (int i = 0; i < stars; i++) {
                ImageView m = findViewById(medalIds[i]);
                if (m != null) m.setColorFilter(Color.parseColor("#FFD700"));
            }

            playSafeSound(stars);
            saveLessonStars(12, stars);
        } else {
            playSafeSound(0);
            saveLessonStars(12, 0);
        }
    }

    private void playSafeSound(int stars) {
        try {
            int[] sounds = {R.raw.sad, R.raw.star1, R.raw.star2, R.raw.star3};
            MediaPlayer.create(this, stars == 0 ? sounds[0] : sounds[stars]).start();
        } catch (Exception ignored) {}
    }

    private void saveLessonStars(int lesson, int stars) {
        getSharedPreferences("UserProgress", MODE_PRIVATE).edit().putInt("stars_lesson_" + lesson, stars).apply();
    }

    private void saveWrongQuestion(String q, String a) {
        SharedPreferences p = getSharedPreferences("UserProgress", MODE_PRIVATE);
        p.edit().putString("wrong_questions_list", p.getString("wrong_questions_list", "") + q + " Correct: " + a + "###").apply();
    }

    private void toggleFavourite(String q, String a, TextView icon) {
        SharedPreferences p = getSharedPreferences("UserProgress", MODE_PRIVATE);
        String f = p.getString("favourite_problems", "");
        String entry = q + " | " + a + "###";
        if (f.contains(entry)) {
            p.edit().putString("favourite_problems", f.replace(entry, "")).apply();
            icon.setText("🤍");
        } else {
            p.edit().putString("favourite_problems", f + entry).apply();
            icon.setText("❤️");
        }
    }

    private boolean isFavourite(String q, String a) {
        return getSharedPreferences("UserProgress", MODE_PRIVATE).getString("favourite_problems", "").contains(q + " | " + a + "###");
    }
}