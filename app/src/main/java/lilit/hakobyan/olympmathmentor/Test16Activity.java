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

public class Test16Activity extends AppCompatActivity {

    private String[] questions = {
            "1. In a right triangle, the legs are 6 and 8. What is the length of the hypotenuse?",
            "2. Two sides of a triangle are 10 and 15. What is the MAXIMUM possible INTEGER length for the third side?",
            "3. Two sides of a triangle are 10 and 15. What is the MINIMUM possible INTEGER length for the third side?",
            "4. Triangle A has sides 3, 4, 5. Triangle B has sides 9, 12, 15. What is the AREA of Triangle B?",
            "5. A 5m ladder leans against a vertical wall. The bottom of the ladder is 3m away from the wall. How high up the wall does it reach?",
            "6. A tree casts a 12m shadow. A 2m tall post casts a 3m shadow. How tall is the tree in meters?",
            "7. The area of a triangle is 64. If we connect the midpoints of all three sides to form a new smaller triangle, what is its area?",
            "8. The perimeter of a triangle is 30. We multiply all its sides by 3. What is the new perimeter?",
            "9. In triangle ABC, side AB=13, BC=13, and AC=10. What is the height (altitude) drawn to the side AC?",
            "10. Is it possible to form a triangle with sides 5, 9, and 14? (Type 1 for YES, 0 for NO)"
    };

    private String[] correctAnswers = {
            "10", "24", "6", "54", "4", "8", "16", "90", "12", "0"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test16);

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {

            // 1. Ստեղծում ենք հորիզոնական կոնտեյներ հարցի և սրտիկի համար
            LinearLayout questionHeader = new LinearLayout(this);
            questionHeader.setOrientation(LinearLayout.HORIZONTAL);
            questionHeader.setGravity(android.view.Gravity.CENTER_VERTICAL);
            questionHeader.setPadding(0, 30, 0, 10);

            // 2. Ստեղծում ենք Հարցի տեքստը
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // 3. Ստեղծում ենք Սրտիկը (էմոջիով) որպես TextView
            TextView tvHeart = new TextView(this);
            tvHeart.setPadding(20, 20, 20, 20);
            tvHeart.setTextSize(22f); // Էմոջիի չափսը
            final int finalI = i;

            // Ստուգում ենք արդեն պահպանված է ֆավորիտներում, թե ոչ
            if (isFavourite(questions[i])) {
                tvHeart.setText("❤️");
            } else {
                tvHeart.setText("🤍");
            }

            // Սրտիկի վրա սեղմելու ֆունկցիան
            tvHeart.setOnClickListener(v -> toggleFavourite(questions[finalI], tvHeart));

            // 4. Ավելացնում ենք երկուսն էլ հորիզոնական կոնտեյների մեջ, իսկ կոնտեյները՝ գլխավորին
            questionHeader.addView(tvQuestion);
            questionHeader.addView(tvHeart);
            questionsContainer.addView(questionHeader);

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
            Intent intent = new Intent(Test16Activity.this, Lesson17Activity.class);
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
                saveWrongQuestion(questions[i], correctAnswers[i]); // Ավելացված է
            } else if (userAnswer.equals(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#2E7D32"));
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
                saveWrongQuestion(questions[i], correctAnswers[i]); // Ավելացված է
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

        int earnedStars = 0; // Հաշվարկում ենք աստղերը

        if (score < 6) {
            tvFeedbackResult.setText("Geometry requires visual thinking. Draw it out and try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);

            saveLessonStars(16, 0); // Պահպանում ենք 0 աստղ
        } else {
            tvFeedbackResult.setText("Brilliant! You've built a strong geometric foundation.");
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

            if (score >= 6) { m1.setColorFilter(gold); earnedStars = 1; }
            if (score >= 8) { m2.setColorFilter(gold); earnedStars = 2; }
            if (score == 10) { m3.setColorFilter(gold); earnedStars = 3; }

            saveLessonStars(16, earnedStars); // Պահպանում ենք աստղերը

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("lesson17_unlocked", true)
                    .putInt("test16_score", score)
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

    private void toggleFavourite(String question, TextView heartIcon) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favourites = prefs.getString("favourite_problems", "");

        if (favourites.contains(question)) {
            favourites = favourites.replace(question + "###", "");
            heartIcon.setText("🤍");
        } else {
            favourites += question + "###";
            heartIcon.setText("❤️");
        }
        prefs.edit().putString("favourite_problems", favourites).apply();
    }

    private boolean isFavourite(String question) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        return prefs.getString("favourite_problems", "").contains(question);
    }
}