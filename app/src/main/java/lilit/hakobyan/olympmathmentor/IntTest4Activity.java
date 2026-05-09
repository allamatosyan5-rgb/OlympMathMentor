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

public class IntTest4Activity extends AppCompatActivity {

    private String[] questions = {
            "1. Calculate Euler's Totient function for 10: φ(10) = ?",
            "2. Calculate φ(100).",
            "3. If p is a prime number, what is φ(p)? (Type your answer algebraically using 'p')",
            "4. According to Euler's Theorem, what is the remainder of a^(φ(n)) modulo n (assuming GCD(a,n)=1)?",
            "5. What are the last two digits of 3^40?",
            "6. Solve for x: 2x ≡ 1 (mod 5). What is the smallest positive integer x?",
            "7. What is the modular inverse of 3 modulo 7?",
            "8. Find the smallest positive x such that x ≡ 2 (mod 3) and x ≡ 3 (mod 5).",
            "9. True or False: The modular inverse of 4 modulo 6 exists. (Yes or No)",
            "10. Compute the remainder of 5^100 divided by 7.",
            "11. How many positive integers up to 30 are coprime to 30?",
            "12. Find the smallest positive integer x such that 5x ≡ 4 (mod 11).",
            "13. What is the remainder when 14^30 is divided by 31?",
            "14. How many distinct solutions does the congruence x^2 ≡ 1 (mod 8) have within the range 0 to 7?",
            "15. What is the remainder when 2^20 + 3^30 is divided by 5?"
    };

    private String[] correctAnswers = {
            "4", "40", "p-1", "1", "01", "3", "5", "8", "No", "2", "8", "3", "1", "4", "0"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test4);

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            LinearLayout questionHeader = new LinearLayout(this);
            questionHeader.setOrientation(LinearLayout.HORIZONTAL);
            questionHeader.setGravity(android.view.Gravity.CENTER_VERTICAL);
            questionHeader.setPadding(0, 30, 0, 10);

            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvHeart = new TextView(this);
            tvHeart.setPadding(20, 20, 20, 20);
            tvHeart.setTextSize(22f);
            int finalI = i;

            if (isFavourite(questions[i])) tvHeart.setText("❤️");
            else tvHeart.setText("🤍");

            tvHeart.setOnClickListener(v -> toggleFavourite(questions[finalI], correctAnswers[finalI], tvHeart));

            questionHeader.addView(tvQuestion);
            questionHeader.addView(tvHeart);
            questionsContainer.addView(questionHeader);

            EditText etAnswer = new EditText(this);
            etAnswer.setHint("Type your answer here...");
            etAnswer.setTextSize(16f);
            etAnswer.setTextColor(Color.parseColor("#212121"));
            answerInputs[i] = etAnswer;
            questionsContainer.addView(etAnswer);

            TextView tvFeedback = new TextView(this);
            tvFeedback.setTextSize(14f);
            tvFeedback.setVisibility(View.GONE);
            tvFeedback.setPadding(0, 10, 0, 20);
            feedbackViews[i] = tvFeedback;
            questionsContainer.addView(tvFeedback);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());

        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            Intent intent = new Intent(IntTest4Activity.this, IntLesson5Activity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
    }

    private void checkResults() {
        int score = 0;

        for (int i = 0; i < questions.length; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().toLowerCase();
            String correctAnswer = correctAnswers[i].toLowerCase();

            // Handle alternative acceptable format for 01 (just 1)
            if (i == 4 && userAnswer.equals("1")) userAnswer = "01";

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
                saveWrongQuestion(questions[i], correctAnswers[i]);
            } else if (userAnswer.equals(correctAnswer)) {
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
        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        tvScore.setText("Your Score: " + score + " / " + questions.length);

        if (score < 10) {
            tvFeedback.setText("Euler's Theorem and CRT are tough! Review the lesson and try again to hit 10 points.");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);

            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedback.setText("Masterful! You conquered the advanced congruences.");
            tvFeedback.setTextColor(Color.parseColor("#2E7D32"));
            findViewById(R.id.medalsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.btnNextLesson).setVisibility(View.VISIBLE);

            ImageView medal1 = findViewById(R.id.medal1);
            ImageView medal2 = findViewById(R.id.medal2);
            ImageView medal3 = findViewById(R.id.medal3);

            medal1.setColorFilter(Color.LTGRAY);
            medal2.setColorFilter(Color.LTGRAY);
            medal3.setColorFilter(Color.LTGRAY);

            int earnedStars = 0;

            if (score >= 10 && score <= 12) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 1;
                MediaPlayer.create(this, R.raw.star1).start();
            } else if (score == 13 || score == 14) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 2;
                MediaPlayer.create(this, R.raw.star2).start();
            } else if (score == 15) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                medal3.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 3;
                MediaPlayer.create(this, R.raw.star3).start();
            }

            // Intermediate Lesson 4 identifier is 24
            saveLessonStars(24, earnedStars);

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("int_lesson5_unlocked", true).apply();
            prefs.edit().putInt("int_test4_score", score).apply();
        }
    }

    private void saveLessonStars(int lessonNumber, int earnedStars) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        int previousBest = prefs.getInt("stars_lesson_" + lessonNumber, 0);
        if (earnedStars > previousBest) {
            prefs.edit().putInt("stars_lesson_" + lessonNumber, earnedStars).apply();
        }
    }

    private void saveWrongQuestion(String question, String correctAns) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String existingErrors = prefs.getString("wrong_questions_list", "");
        if (!existingErrors.contains(question)) {
            String newErrorEntry = question + " \nCorrect Answer: " + correctAns + "###";
            prefs.edit().putString("wrong_questions_list", existingErrors + newErrorEntry).apply();
        }
    }

    private void toggleFavourite(String question, String correctAns, TextView heartIcon) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favourites = prefs.getString("favourite_problems", "");
        String newEntry = question + " \nCorrect Answer: " + correctAns + "###";

        if (favourites.contains(question)) {
            if (favourites.contains(newEntry)) favourites = favourites.replace(newEntry, "");
            else favourites = favourites.replace(question + "###", "");
            heartIcon.setText("🤍");
        } else {
            favourites += newEntry;
            heartIcon.setText("❤️");
        }
        prefs.edit().putString("favourite_problems", favourites).apply();
    }

    private boolean isFavourite(String question) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        return prefs.getString("favourite_problems", "").contains(question);
    }
}