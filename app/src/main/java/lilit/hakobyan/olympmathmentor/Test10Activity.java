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

public class Test10Activity extends AppCompatActivity {

    private String[] questions = {
            "1. A takes 10 days, B takes 15 days. They work together for 3 days, then B leaves. How many days will A take to finish the rest?",
            "2. 20 men can build a wall in 15 days. How many man-days does the wall require?",
            "3. X can do a job in 24 days. After he works for 8 days, Y joins him. They finish the rest in 4 days. How many days would Y alone take?",
            "4. A team of 5 workers can build a shed in 12 days. If 1 worker gets sick before starting, how many days will the 4 workers take?",
            "5. P and Q can do a job in 20 and 30 days respectively. P starts alone and Q joins him 5 days before the end. How many days did the work take in total?",
            "6. Machine A produces 100 units/hr. Machine B produces 150 units/hr. They need to produce 1000 units. A breaks down after 4 hours. How many hours will B take to finish?",
            "7. 10 men finish 50% of a job in 10 days. How many MORE men are needed to finish the rest in 5 days?",
            "8. A takes 12 days, B takes 16 days. They work together but A leaves 3 days before the work is done. How many days did the total work take?",
            "9. If 6 cats can catch 6 mice in 6 minutes, how many cats are needed to catch 100 mice in 100 minutes?",
            "10. A pool fills by Pipe 1 in 4 hrs, Pipe 2 in 6 hrs. Both are opened, but Pipe 1 is closed after 1 hour. How many hours will Pipe 2 take to finish filling the pool?"
    };

    private String[] correctAnswers = {
            "5", "300", "8", "15", "14", "4", "10", "8", "6", "4.5"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test10);

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
            startActivity(new Intent(Test10Activity.this, Lesson11Activity.class));
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
            tvFeedbackResult.setText("Joint work problems are tough. Review the man-days concept!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(10, 0);
            MediaPlayer.create(this, R.raw.sad).start();
        } else {
            tvFeedbackResult.setText("Incredible! You solved the complex work scenarios.");
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

            saveLessonStars(10, earnedStars);
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putBoolean("lesson11_unlocked", true)
                    .putInt("test10_score", score)
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