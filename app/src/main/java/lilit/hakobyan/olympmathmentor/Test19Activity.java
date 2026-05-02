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

public class Test19Activity extends AppCompatActivity {

    private String[] questions = {
            "1. In a cyclic quadrilateral, what is the sum of any two OPPOSITE angles (in degrees)?",
            "2. A cyclic quad has sides 3, 4, 5, and 6. What is its semi-perimeter (s)?",
            "3. Using Brahmagupta's formula, what is the SQUARE of the area (Area²) of the quadrilateral with sides 3, 4, 5, 6?",
            "4. In Ptolemy's theorem, if opposite sides are (AB=3, CD=4) and (BC=2, AD=6), what is the product of the diagonals AC × BD?",
            "5. What is the total sum of interior angles in a hexagon (6 sides)?",
            "6. What is the measure of ONE interior angle of a regular octagon (8 sides)?",
            "7. How many diagonals does a convex decagon (10 sides) have?",
            "8. A regular polygon has an interior angle of 144 degrees. How many sides does it have?",
            "9. A cyclic quad has Angle A = 70°. What is the measure of the opposite Angle C?",
            "10. Is every rectangle a cyclic quadrilateral? (Type 1 for YES, 0 for NO)"
    };


    private String[] correctAnswers = {
            "180", "9", "360", "24", "720", "135", "35", "10", "110", "1"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test19);

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
            etAnswer.setHint("Type a number...");
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

        // --- NEXT LESSON LOGIC: OPENS LESSON 20 ---
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            try {
                Class<?> lesson20Class = Class.forName("lilit.hakobyan.olympmathmentor.Lesson20Activity");
                Intent intent = new Intent(Test19Activity.this, lesson20Class);
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
            tvFeedbackResult.setText("Polygons require formula memorization. Review the diagonals formula!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("Stellar! You unlocked the secrets of Ptolemy and Polygons.");
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
                    .putBoolean("lesson20_unlocked", true)
                    .putInt("test19_score", score)
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