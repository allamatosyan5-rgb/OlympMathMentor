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
        findViewById(R.id.btnNextLesson).setOnClickListener(v -> {
            startActivity(new Intent(Test18Activity.this, Lesson19Activity.class));
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
            tvFeedbackResult.setText("Theorems require practice. Review Heron and Pick's formula!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(18, 0);
            MediaPlayer.create(this, R.raw.sad).start();
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

            int earnedStars = (score == 10) ? 3 : (score >= 8) ? 2 : 1;
            if (earnedStars >= 1) m1.setColorFilter(gold);
            if (earnedStars >= 2) m2.setColorFilter(gold);
            if (earnedStars == 3) m3.setColorFilter(gold);

            if (earnedStars == 1) MediaPlayer.create(this, R.raw.star1).start();
            else if (earnedStars == 2) MediaPlayer.create(this, R.raw.star2).start();
            else MediaPlayer.create(this, R.raw.star3).start();

            saveLessonStars(18, earnedStars);
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putBoolean("lesson19_unlocked", true)
                    .putInt("test18_score", score)
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