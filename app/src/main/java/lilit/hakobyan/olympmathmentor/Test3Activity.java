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

public class Test3Activity extends AppCompatActivity {

    private String[] questions = {
            "1. The difference between any double-digit number and its reverse is always completely divisible by what number?",
            "2. The sum of any double-digit number and its reverse is always a multiple of what prime number?",
            "3. The difference between a three-digit number and its reverse is always divisible by what largest two-digit number?",
            "4. A number leaves a remainder of 7 when divided by 9. What is the remainder when the sum of its digits is divided by 9?",
            "5. A digit '2' is appended to the right of a two-digit number 'x', increasing its value by 317. Find the original number 'x'.",
            "6. The sum of a two-digit number and its reverse is 143. What is the sum of its digits (a + b)?",
            "7. Find the smallest three-digit number whose sum of digits is exactly 21.",
            "8. A two-digit number is equal to 4 times the sum of its digits. What is the largest such two-digit number?",
            "9. What is the smallest two-digit number that is equal to exactly 3 times the product of its digits?",
            "10. If the 4-digit number 5A3B is completely divisible by 45, what is the maximum possible value for the digit B?"
    };

    private String[] correctAnswers = {"9", "11", "99", "7", "35", "13", "399", "48", "15", "5"};
    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

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

            // ՈՒՂՂՈՒՄ. ստուգում ենք ամբողջական գրառումը
            tvHeart.setText(isFavourite(questions[finalI], correctAnswers[finalI]) ? "❤️" : "🤍");

            // ՈՒՂՂՈՒՄ. 3 արգումենտով կանչ
            tvHeart.setOnClickListener(v -> toggleFavourite(questions[finalI], correctAnswers[finalI], tvHeart));

            questionHeader.addView(tvQuestion);
            questionHeader.addView(tvHeart);
            questionsContainer.addView(questionHeader);

            EditText etAnswer = new EditText(this);
            etAnswer.setHint("Type answer...");
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
            startActivity(new Intent(Test3Activity.this, Lesson4Activity.class));
            finish();
        });
        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
    }

    private void checkResults() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().toLowerCase();
            String correctAnswer = correctAnswers[i].toLowerCase();

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
        TextView tvFeedbackResult = findViewById(R.id.tvFeedback);
        tvScore.setText("Your Score: " + score + " / " + questions.length);

        if (score < 6) {
            tvFeedbackResult.setText("You need 6+ to unlock the next lesson. Try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(3, 0);
            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedbackResult.setText("Congratulations! Lesson 4 is now unlocked.");
            tvFeedbackResult.setTextColor(Color.parseColor("#2E7D32"));
            findViewById(R.id.medalsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.btnNextLesson).setVisibility(View.VISIBLE);

            int gold = Color.parseColor("#FFD700");
            ImageView m1 = findViewById(R.id.medal1);
            ImageView m2 = findViewById(R.id.medal2);
            ImageView m3 = findViewById(R.id.medal3);

            m1.setColorFilter(Color.LTGRAY);
            m2.setColorFilter(Color.LTGRAY);
            m3.setColorFilter(Color.LTGRAY);

            int earnedStars = 0;
            if (score >= 6 && score < 8) {
                m1.setColorFilter(gold);
                earnedStars = 1;
                MediaPlayer.create(this, R.raw.star1).start();
            } else if (score >= 8 && score < 10) {
                m1.setColorFilter(gold);
                m2.setColorFilter(gold);
                earnedStars = 2;
                MediaPlayer.create(this, R.raw.star2).start();
            } else if (score == 10) {
                m1.setColorFilter(gold);
                m2.setColorFilter(gold);
                m3.setColorFilter(gold);
                earnedStars = 3;
                MediaPlayer.create(this, R.raw.star3).start();
            }

            saveLessonStars(3, earnedStars);
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putBoolean("lesson4_unlocked", true)
                    .putInt("test3_score", score)
                    .apply();
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
            prefs.edit().putString("wrong_questions_list", existingErrors + question + " (Correct: " + correctAns + ")###").apply();
        }
    }

    private void toggleFavourite(String question, String correctAns, TextView heartIcon) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favourites = prefs.getString("favourite_problems", "");
        String entry = question + " \nCorrect Answer: " + correctAns + "###";

        if (favourites.contains(question)) {
            if (favourites.contains(entry)) {
                favourites = favourites.replace(entry, "");
            } else {
                favourites = favourites.replace(question + "###", "");
            }
            heartIcon.setText("🤍");
        } else {
            favourites += entry;
            heartIcon.setText("❤️");
        }
        prefs.edit().putString("favourite_problems", favourites).apply();
    }

    private boolean isFavourite(String question, String correctAns) {
        String entry = question + " \nCorrect Answer: " + correctAns + "###";
        return getSharedPreferences("UserProgress", Context.MODE_PRIVATE)
                .getString("favourite_problems", "").contains(entry);
    }
}