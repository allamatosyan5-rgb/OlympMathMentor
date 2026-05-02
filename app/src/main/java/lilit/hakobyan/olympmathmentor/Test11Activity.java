package lilit.hakobyan.olympmathmentor;

import android.content.Context;
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

public class Test11Activity extends AppCompatActivity {

    private String[] questions = {
            "1. The sum of 4 ODD numbers and 3 EVEN numbers is? (Type 1 for ODD, 2 for EVEN)",
            "2. The product of 99 ODD numbers and 1 EVEN number is? (Type 1 for ODD, 2 for EVEN)",
            "3. Can a knight start at a1 (black), visit every square on a chessboard exactly once, and end at h8 (black)? (Type 1 for Yes, 0 for No)",
            "4. A book has 100 pages. Is the sum of all page numbers Odd or Even? (Type 1 for Odd, 2 for Even)",
            "5. There are 5 coins Tails up. You can flip 2 coins at a time. Can you make all coins Heads up? (Type 1 for Yes, 0 for No)",
            "6. You have 15 Red apples and 10 Green. Eating 2 same gives Green. Eating 2 diff gives Red. The last apple is? (Type 1 for Red, 2 for Green)",
            "7. The product of 5 integers is an ODD number. What is the parity of their sum? (Type 1 for Odd, 2 for Even)",
            "8. If a + b = 25, what is the parity of a × b? (Type 1 for Odd, 2 for Even)",
            "9. A grasshopper jumps 1cm, then 2cm, then 3cm... up to 10cm on a line. Can it return to its starting point? (Type 1 for Yes, 0 for No)",
            "10. Is the result of 1² + 2² + 3² + ... + 10² Odd or Even? (Type 1 for Odd, 2 for Even)"
    };


    private String[] correctAnswers = {
            "2", "2", "0", "2", "0", "1", "1", "2", "0", "1"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test11);

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
            etAnswer.setHint("Type 1, 2, or 0...");
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


        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            try {
                Class<?> lesson12Class = Class.forName("lilit.hakobyan.olympmathmentor.Lesson12Activity");
                Intent intent = new Intent(Test11Activity.this, lesson12Class);
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
            tvFeedbackResult.setText("Logic problems can be confusing. Remember the rules of Invariants!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("Outstanding! You understand Parity flawlessly.");
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
                    .putBoolean("lesson12_unlocked", true)
                    .putInt("test11_score", score)
                    .apply();
        }
    }
    private void saveLessonStars(int lessonNumber, int earnedStars) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        int previousBest = prefs.getInt("stars_lesson_" + lessonNumber, 0);

        // Եթե այս անգամ հավաքած աստղերն ավելի շատ են, քան նախկինում հավաքածը, ապա պահպանել
        if (earnedStars > previousBest) {
            prefs.edit().putInt("stars_lesson_" + lessonNumber, earnedStars).apply();
        }
    }
}