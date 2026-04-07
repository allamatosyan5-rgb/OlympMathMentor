package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Test3Activity extends AppCompatActivity {

    // Օլիմպիական մակարդակի 10 հարցեր 3-րդ դասի համար
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

    // Ճշգրիտ պատասխանները
    private String[] correctAnswers = {
            "9",    // 10a+b - (10b+a) = 9(a-b)
            "11",   // 10a+b + 10b+a = 11(a+b)
            "99",   // 100a+10b+c - (100c+10b+a) = 99(a-c)
            "7",    // n ≡ S(n) (mod 9)
            "35",   // 10x + 2 = x + 317 => 9x = 315 => x = 35
            "13",   // 11(a+b) = 143 => a+b = 13
            "399",  // Smallest means first digit should be as small as possible: 3+9+9=21
            "48",   // 10a+b = 4(a+b) => 6a = 3b => 2a = b. Max is a=4, b=8.
            "15",   // 15 = 3 * (1 * 5)
            "5"     // Divisible by 5 means B is 0 or 5. Max is 5.
    };
    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Օգտագործում ենք test2-ի դիզայնը, քանի որ կառուցվածքը նույնն է
        setContentView(R.layout.activity_test2);

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setPadding(0, 30, 0, 10);
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
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());

        // Test 3-ից հետո փակում ենք, որ վերադառնանք գլխավոր էջ (կամ կարող ես դնել Lesson 4)
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> finish());
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
            } else if (userAnswer.equals(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#2E7D32"));
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
            tvFeedbackResult.setText("You need 6+ to unlock the next lesson. Try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("Congratulations! You passed Test 3.");
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

            if (score >= 6) m1.setColorFilter(gold);
            if (score >= 8) m2.setColorFilter(gold);
            if (score == 10) m3.setColorFilter(gold);

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("lesson4_unlocked", true) // Բացում ենք Դաս 4-ը
                    .putInt("test3_score", score)
                    .apply();
        }
    }
}