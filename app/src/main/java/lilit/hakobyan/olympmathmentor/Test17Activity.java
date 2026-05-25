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

public class Test17Activity extends AppCompatActivity {

    private String[] questions = {
            "1. The centroid divides the median in what ratio? (e.g. if 5:1, type 5)",
            "2. In an obtuse triangle, does the orthocenter lie INSIDE the triangle? (1 for YES, 0 for NO)",
            "3. In a right-angled triangle, the circumcenter lies on the midpoint of the hypotenuse. If the hypotenuse is 10, what is the circumradius (R)?",
            "4. A triangle has Area = 24 and Perimeter = 24. What is the radius of the incircle (r)? (Hint: Area = r * s)",
            "5. An inscribed angle is 45°. What is the measure of the central angle standing on the same arc?",
            "6. A central angle is 120°. What is the measure of the inscribed angle standing on the same arc?",
            "7. An angle is inscribed in a semicircle. What is its measure in degrees?",
            "8. The total length of a median is 21cm. How far is the centroid from the vertex? (in cm)",
            "9. True or False: The INCENTER is always equidistant from the three vertices of the triangle. (1 for True, 0 for False)",
            "10. True or False: The incenter of ANY triangle is always inside the triangle. (1 for True, 0 for False)"
    };

    private String[] correctAnswers = {
            "2", "0", "5", "2", "90", "60", "90", "14", "0", "1"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test17);

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

            tvHeart.setText(isFavourite(questions[finalI], correctAnswers[finalI]) ? "❤️" : "🤍");
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
            startActivity(new Intent(Test17Activity.this, Lesson18Activity.class));
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
            tvFeedbackResult.setText("Geometry centers can be confusing. Review the 2:1 ratio and try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(17, 0);
            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedbackResult.setText("Fantastic! You have a great eye for geometric centers.");
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

            saveLessonStars(17, earnedStars);
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putBoolean("lesson18_unlocked", true)
                    .putInt("test17_score", score)
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