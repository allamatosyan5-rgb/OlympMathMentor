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

public class Test18Activity extends AppCompatActivity {

    private String[] questions = {
            "1. Using Heron's formula, find the Area of a triangle with sides 3, 4, and 5.",
            "2. Find the Area of a triangle with sides 13, 14, and 15.",
            "3. On a grid, a polygon has 10 points INSIDE and 6 points ON THE BOUNDARY. What is its Area (using Pick's Theorem)?",
            "4. On a grid, a polygon has 5 points INSIDE and 10 points ON THE BOUNDARY. What is its Area?",
            "5. In Ceva's Theorem, the product of the three ratios around the triangle is always equal to what number?",
            "6. In Menelaus's Theorem, the line must cut through how many sides of the actual triangle (not counting extensions)?",
            "7. Cevians intersect. AF=2, FB=1, BD=4, DC=2. Find the ratio CE/EA. (Format: decimal)",
            "8. Find the semi-perimeter 's' of a triangle with sides 7, 8, and 9.",
            "9. Two sides of a triangle are 10 and 8, and the angle between them is 30 degrees. Find the area. (Hint: Area = 0.5*a*b*sin30, sin30=0.5)",
            "10. In Pick's theorem formula (Area = I + B/2 - x), what number is 'x'?"
    };

    // Q1: s=6. Area = sqrt(6*3*2*1) = 6.
    // Q2: s=21. Area = 84.
    // Q3: I=10, B=6. Area = 10 + 6/2 - 1 = 10 + 3 - 1 = 12.
    // Q4: I=5, B=10. Area = 5 + 10/2 - 1 = 5 + 5 - 1 = 9.
    // Q5: 1.
    // Q6: 2 sides. The third intersection is on the extension.
    // Q7: (2/1) * (4/2) * (x) = 1 -> 2 * 2 * x = 1 -> 4x = 1 -> x = 0.25.
    // Q8: (7+8+9)/2 = 24/2 = 12.
    // Q9: 0.5 * 10 * 8 * 0.5 = 20.
    // Q10: 1.
    private String[] correctAnswers = {
            "6", "84", "12", "9", "1", "2", "0.25", "12", "20", "1"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test18);

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
            etAnswer.setHint("Type your answer...");
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
        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());

        // --- NEXT LESSON LOGIC: OPENS LESSON 19 ---
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            try {
                Class<?> lesson19Class = Class.forName("lilit.hakobyan.olympmathmentor.Lesson19Activity");
                Intent intent = new Intent(Test18Activity.this, lesson19Class);
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                finish();
            }
        });
    }

    private void checkResults() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().replaceAll("\\s+", "");
            String correctAnswer = correctAnswers[i];

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
            tvFeedbackResult.setText("Theorems require practice. Review Heron and Pick's formula!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("Masterful! You conquered Advanced Area Theorems.");
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
                    .putBoolean("lesson19_unlocked", true)
                    .putInt("test18_score", score)
                    .apply();
        }
    }
}