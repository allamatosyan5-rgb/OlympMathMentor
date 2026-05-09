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

public class IntTest5Activity extends AppCompatActivity {

    private String[] questions = {
            "1. Calculate the permutation P(5, 2).",
            "2. Calculate the combination C(6, 3).",
            "3. How many distinct ways can the letters of the word 'MATH' be arranged?",
            "4. How many distinct ways can the letters of the word 'APPLE' be arranged?",
            "5. How many subsets does a set of 5 distinct elements have?",
            "6. In a room of 10 people, if everyone shakes hands with everyone exactly once, how many handshakes occur?",
            "7. Using 'Stars and Bars', how many non-negative integer solutions are there to x + y + z = 5?",
            "8. How many POSITIVE integer solutions exist for x + y + z = 5?",
            "9. How many diagonals are there in a regular octagon (8-sided polygon)?",
            "10. Using PIE, how many numbers from 1 to 100 are divisible by 2 or 5?",
            "11. In how many ways can 5 people be seated around a circular table?",
            "12. How many 3-digit numbers can be formed where all three digits are distinct (0 cannot be the first digit)?",
            "13. In how many ways can 3 boys and 3 girls be seated in a row if boys and girls must alternate?",
            "14. What is the number of derangements of 3 objects (D_3)?",
            "15. A pizza shop offers 4 types of toppings. You want to order a pizza with exactly 2 DIFFERENT toppings. How many combinations are possible?"
    };

    private String[] correctAnswers = {
            "20", "20", "24", "60", "32", "45", "21", "6", "20", "60", "24", "648", "72", "2", "6"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test5);

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
            Intent intent = new Intent(IntTest5Activity.this, IntLesson6Activity.class);
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
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        tvScore.setText("Your Score: " + score + " / " + questions.length);

        if (score < 10) {
            tvFeedback.setText("Combinatorics can be tricky! Review the formulas in the lesson and try again.");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);

            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedback.setText("Brilliant! You've mastered Advanced Combinatorics.");
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

            // Intermediate Lesson 5 identifier is 25
            saveLessonStars(25, earnedStars);

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("int_lesson6_unlocked", true).apply();
            prefs.edit().putInt("int_test5_score", score).apply();
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