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

public class Test5Activity extends AppCompatActivity {

    private String[] questions = {
            "1. The 5-digit number 73A45 is completely divisible by 11. Find the digit A.",
            "2. The number 3X4Y is divisible by 36. What is the LARGEST possible value for the digit X?",
            "3. Find the smallest positive integer consisting ONLY of digits 0 and 1 that is divisible by 225.",
            "4. A 6-digit number formed by repeating a 3-digit block (e.g., 256256) is ALWAYS divisible by 7, 11, and what other prime number?",
            "5. If a number is simultaneously divisible by 12 and 15, what is the smallest positive integer it MUST also be divisible by?",
            "6. What is the remainder when the number 1000...002 (with twenty zeros) is divided by 3?",
            "7. The alternating sum of the digits of a number is exactly 22. What prime number is this guaranteed to be divisible by?",
            "8. If the 4-digit number 5A3B is divisible by 45, what is the maximum possible value for the digit B?",
            "9. The number 1A2B3C4D is a multiple of 9. What is the remainder when the sum (A+B+C+D) is divided by 9?",
            "10. The last three digits of a large number are 048. Which of these is it definitely divisible by: '4', '8', or 'both'?"
    };

    private String[] correctAnswers = {
            "6", "7", "11111111100", "13", "60", "0", "11", "5", "8", "both"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);

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
            Intent intent = new Intent(Test5Activity.this, Lesson6Activity.class);
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
            tvFeedbackResult.setText("These questions were tough! Review the lesson and try again.");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(5, 0);
            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedbackResult.setText("Outstanding! You passed the Advanced Divisibility Test.");
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

            saveLessonStars(5, earnedStars);
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("lesson6_unlocked", true).putInt("test5_score", score).apply();
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
            prefs.edit().putString("wrong_questions_list", existingErrors + question + " \nCorrect Answer: " + correctAns + "###").apply();
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