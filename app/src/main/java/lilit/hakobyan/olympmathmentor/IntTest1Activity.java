package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IntTest1Activity extends AppCompatActivity {

    private String[] questions = {
            "1. What is the value of digit 'd' if the 5-digit number 5d782 is perfectly divisible by 11?",
            "2. How many positive divisors does the number 360 have?",
            "3. What is the sum of all positive divisors of the perfect number 28?",
            "4. Using the Euclidean Algorithm, find the Greatest Common Divisor (GCD) of 1001 and 143.",
            "5. How many trailing zeros does the number 50! (50 factorial) end with?",
            "6. LCM(a,b)=120 and GCD(a,b)=4. If a=24, what is the value of b?",
            "7. The sum of two prime numbers is 85. What is their product?",
            "8. What is the remainder when 10^99 is divided by 9?",
            "9. How many ODD positive divisors does the number 120 have?",
            "10. What is the smallest positive integer that has exactly 6 positive divisors?",
            "11. When N is divided by 7, the remainder is 4. What is the remainder when 3*N is divided by 7?",
            "12. A number leaves a remainder of 2 when divided by 3, and 3 when divided by 5. What is the smallest positive such number?",
            "13. If a number has exactly 9 positive divisors, must it be a perfect square? (1 for YES, 0 for NO)",
            "14. What is the largest integer 'n' such that (n+10) perfectly divides (n^3 + 100)?",
            "15. What is the largest prime factor of the 6-digit number 456456?"
    };

    private String[] correctAnswers = {
            "6", "24", "56", "143", "12", "20", "166", "1", "4", "12", "5", "8", "1", "890", "13"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test1); // Օգտագործում ենք քո ստանդարտ թեստի դիզայնը

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setPadding(0, 30, 0, 10);
            tvQuestion.setTypeface(null, android.graphics.Typeface.BOLD);
            questionsContainer.addView(tvQuestion);

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

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
            questionsContainer.addView(divider);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());

        Button btnNextLesson = findViewById(R.id.btnNextLesson);
        if(btnNextLesson != null) {
            btnNextLesson.setOnClickListener(v -> {

            });
        }

        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
    }

    private void checkResults() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().replaceAll("\\s+", "").replace(",", ".");
            String correctAnswer = correctAnswers[i];

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
            } else if (userAnswer.equals(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#388E3C"));
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
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
            tvFeedbackResult.setText("You need at least 6 points to unlock the next advanced lesson. Try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);


            saveLessonStars(22, 0);
        } else {
            tvFeedbackResult.setText("Excellent job! You survived the first Intermediate test. Lesson 2 is unlocked.");
            tvFeedbackResult.setTextColor(Color.parseColor("#388E3C"));
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
            if (score >= 10) { m1.setColorFilter(gold); earnedStars = 1; }
            if (score >= 12) { m2.setColorFilter(gold); earnedStars = 2; }
            if (score >= 14) { m3.setColorFilter(gold); earnedStars = 3; }

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putInt("int_test1_score", score)
                    .apply();


            saveLessonStars(22, earnedStars);
        }
    }


    private void saveLessonStars(int lessonNumber, int earnedStars) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        int previousBest = prefs.getInt("stars_lesson_" + lessonNumber, 0);

        if (earnedStars > previousBest) {
            prefs.edit().putInt("stars_lesson_" + lessonNumber, earnedStars).apply();
        }
    }
}