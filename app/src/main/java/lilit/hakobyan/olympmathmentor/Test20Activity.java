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

public class Test20Activity extends AppCompatActivity {

    private String[] questions = {
            "1. Two chords intersect. PA=4, PB=6, PC=3. Find PD.",
            "2. Secant external PA=4, internal AB=5. Find tangent PT.",
            "3. The Centroid divides the distance between the Orthocenter and Circumcenter in what ratio? (Format: if 5:1 type 5)",
            "4. If the circumradius of a triangle is 10, what is the radius of the Nine-Point Circle?",
            "5. The Euler line connects Orthocenter, Centroid, and which other center? (1 for Incenter, 2 for Circumcenter)",
            "6. Two chords intersect. PA=2, PB=10, PC=4. Find PD.",
            "7. Does the Incenter always lie on the Euler Line? (1 for YES, 0 for NO)",
            "8. Does Stewart's Theorem apply to a right-angled triangle? (1 for YES, 0 for NO)",
            "9. How many distinct points precisely define the 'Feuerbach's Circle' (Nine-Point Circle)?",
            "10. If the distance from Orthocenter (H) to Centroid (G) is 12, what is the distance from Centroid (G) to Circumcenter (O)?"
    };


    private String[] correctAnswers = {
            "8", "6", "2", "5", "2", "5", "0", "1", "9", "6"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test20);

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


        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            Intent intent = new Intent(Test20Activity.this, FinalExamActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
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
            tvFeedbackResult.setText("You are so close to the end! Review the Euler Line and try again.");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("🏆 YOU DID IT! You unlocked the Grand Final Exam.");
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

            // Save score
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putInt("test20_score", score)
                    .putBoolean("course_completed", true)
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
            String newErrorEntry = question + " \nCorrect Answer: " + correctAns + "###";
            prefs.edit().putString("wrong_questions_list", existingErrors + newErrorEntry).apply();
        }
    }
}