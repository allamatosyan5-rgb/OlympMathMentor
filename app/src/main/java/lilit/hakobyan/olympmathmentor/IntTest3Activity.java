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

public class IntTest3Activity extends AppCompatActivity {

    private String[] questions = {
            "1. What is the remainder when 45 is divided by 7?",
            "2. Calculate the remainder of 5^3 modulo 6.",
            "3. If x ≡ 4 (mod 9), what is the remainder of 2x + 5 divided by 9?",
            "4. According to Fermat's Little Theorem, what is the remainder of 2^12 divided by 13?",
            "5. Find the remainder when 3^50 is divided by 7.",
            "6. Fermat's Little Theorem a^(p-1) ≡ 1 (mod p) holds true only if 'a' and 'p' are what? (Type the exact word: coprime)",
            "7. What is the last digit of 7^2026?",
            "8. Solve for x (where 0 ≤ x < 5): 3x ≡ 1 (mod 5).",
            "9. According to Wilson's Theorem, what is the remainder of 4! divided by 5?",
            "10. Compute the remainder of 100^100 divided by 11.",
            "11. Find the remainder of 6^22 divided by 23.",
            "12. If x ≡ 2 (mod 5) and x ≡ 3 (mod 7), what is the smallest positive integer x?",
            "13. What is the remainder of 2^100 divided by 13?",
            "14. What is the remainder of (1! + 2! + 3! + ... + 100!) divided by 15?",
            "15. For which prime number 'p' does 2^(p-1) - 1 fail to be divisible by 'p'?"
    };

    private String[] correctAnswers = {
            "3", "5", "4", "1", "4", "coprime", "9", "2", "4", "1", "1", "17", "3", "3", "2"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test3);

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

            if (isFavourite(questions[i])) tvHeart.setText("❤️");
            else tvHeart.setText("🤍");

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
            Intent intent = new Intent(IntTest3Activity.this, IntLesson4Activity.class);
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

            // Special case for question 9 (Wilson's theorem: -1 mod 5 is 4)
            if (i == 8 && (userAnswer.equals("-1") || userAnswer.equals("4"))) {
                userAnswer = "4";
            }

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
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        tvScore.setText("Your Score: " + score + " / " + questions.length);

        if (score < 10) {
            tvFeedback.setText("Congruences require deep focus. Review the lesson and try again to get at least 10 points!");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);

            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedback.setText("Outstanding! You passed the Advanced Congruences Test.");
            tvFeedback.setTextColor(Color.parseColor("#2E7D32"));
            findViewById(R.id.medalsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.btnNextLesson).setVisibility(View.VISIBLE);

            ImageView medal1 = findViewById(R.id.medal1);
            ImageView medal2 = findViewById(R.id.medal2);
            ImageView medal3 = findViewById(R.id.medal3);

            medal1.setColorFilter(Color.LTGRAY);
            medal2.setColorFilter(Color.LTGRAY);
            medal3.setColorFilter(Color.LTGRAY);

            int earnedStars = 0;

            if (score >= 10 && score <= 12) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 1;
                MediaPlayer.create(this, R.raw.star1).start();
            } else if (score == 13 || score == 14) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 2;
                MediaPlayer.create(this, R.raw.star2).start();
            } else if (score == 15) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                medal3.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 3;
                MediaPlayer.create(this, R.raw.star3).start();
            }

            // Intermediate Lesson 3 identifier is 23
            saveLessonStars(23, earnedStars);

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("int_lesson4_unlocked", true).apply();
            prefs.edit().putInt("int_test3_score", score).apply();
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

    private void toggleFavourite(String question, String correctAns, TextView heartIcon) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favourites = prefs.getString("favourite_problems", "");
        String newEntry = question + " \nCorrect Answer: " + correctAns + "###";

        if (favourites.contains(question)) {
            if (favourites.contains(newEntry)) favourites = favourites.replace(newEntry, "");
            else favourites = favourites.replace(question + "###", "");
            heartIcon.setText("🤍");
        } else {
            favourites += newEntry;
            heartIcon.setText("❤️");
        }
        prefs.edit().putString("favourite_problems", favourites).apply();
    }

    private boolean isFavourite(String question) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        return prefs.getString("favourite_problems", "").contains(question);
    }
}