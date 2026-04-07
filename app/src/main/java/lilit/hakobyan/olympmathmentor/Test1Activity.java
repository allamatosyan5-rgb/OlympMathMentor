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

public class Test1Activity extends AppCompatActivity {

    private String[] questions = {
            "1. What is the sum of the first five odd natural numbers?",
            "2. Is the sum of the first 100 odd natural numbers even or odd? (Answer: Even or Odd)",
            "3. If x and y are natural numbers, can the sum 6x + 8y ever equal 1,001? (Answer: Yes or No)",
            "4. If the product of three natural numbers is odd, how many of those numbers must be even?",
            "5. A magic square of size 3x3 is filled with odd numbers. Is the sum of the numbers in any row even or odd? (Answer: Even or Odd)",
            "6. What is the remainder when the sum of two consecutive odd numbers is divided by 4?",
            "7. Find the units digit (the last digit) of the product of any three consecutive natural numbers where the first number is 4.",
            "8. Can the sum of three consecutive natural numbers be equal to 40? (Answer: Yes or No)",
            "9. If n is a natural number, is the expression n(n+1) + 5 always even or always odd? (Answer: Even or Odd)",
            "10. How many even natural numbers exist between 1 and 11?"
    };

    private String[] correctAnswers = {
            "25",
            "Even",
            "No",
            "0",
            "Odd",
            "0",
            "0",
            "No",
            "Odd",
            "5"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {

            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723")); // Deep Brown
            tvQuestion.setPadding(0, 30, 0, 10);
            questionsContainer.addView(tvQuestion);

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

        // ԱՅՍՏԵՂ Է ՓՈՓՈԽՎԱԾ ՄԱՍԸ. Հաջորդ դասը բացելու տրամաբանությունը
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            Intent intent = new Intent(Test1Activity.this, Lesson2Activity.class);
            startActivity(intent);
            finish(); // Փակում ենք թեստի էջը
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
                feedbackViews[i].setText("❌ You didn't answer. Correct answer: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F")); // Red
            } else if (userAnswer.equals(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#2E7D32")); // Green
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct answer: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F")); // Red
            }
        }

        showFinalResult(score);
    }

    private void showFinalResult(int score) {
        // Hide Finish button, show results layout
        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        tvScore.setText("Your Score: " + score + " / " + questions.length);

        if (score <= 5) {
            tvFeedback.setText("You haven't fully mastered this topic yet. Try again, everything is ahead!");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedback.setText("Congratulations! You have successfully completed Lesson 1.");
            tvFeedback.setTextColor(Color.parseColor("#2E7D32"));
            findViewById(R.id.medalsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.btnNextLesson).setVisibility(View.VISIBLE);

            ImageView medal1 = findViewById(R.id.medal1);
            ImageView medal2 = findViewById(R.id.medal2);
            ImageView medal3 = findViewById(R.id.medal3);

            medal1.setColorFilter(Color.LTGRAY);
            medal2.setColorFilter(Color.LTGRAY);
            medal3.setColorFilter(Color.LTGRAY);

            if (score == 6 || score == 7) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
            } else if (score == 8 || score == 9) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
            } else if (score == 10) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                medal3.setColorFilter(Color.parseColor("#FFD700"));
            }

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("lesson2_unlocked", true).apply();
            prefs.edit().putInt("test1_score", score).apply(); // Պահպանում ենք նաև միավորը
        }
    }
}