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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdvTest11Activity extends AppCompatActivity {

    private String[] questions = {
            "1. Vieta Jumping is a technique primarily used in what theory? (Type: number)",
            "2. It relies on the formulas of which mathematician? (Type: vieta)",
            "3. Vieta's formulas deal with the sum and product of what? (Type: roots)",
            "4. For equation x^2 - px + q = 0, the sum of roots is what? (Type: p)",
            "5. And the product of roots is what? (Type: q)",
            "6. The technique is often combined with the method of infinite what? (Type: descent)",
            "7. We assume by contradiction that a solution exists which is the strictly what? (Type: smallest)",
            "8. Vieta Jumping usually turns an equation into a polynomial of what degree? (Type: quadratic)",
            "9. If x1 is an integer root, and coefficients are integers, is the second root x2 an integer? (yes/no)",
            "10. Is the second root found using x2 = p - x1? (yes)",
            "11. Another name for Vieta Jumping is Root what? (Type: flipping)",
            "12. Was this method made famous by the IMO in 1988? (yes)",
            "13. Does Vieta Jumping fix one variable as a constant to create a quadratic? (yes)",
            "14. If a^2 - k*a*b + b^2 = k, what is the sum of roots a1 and a2? (Type: kb)",
            "15. What is the product of roots a1 and a2 in that equation? (Type: b^2-k)",
            "16. If we prove the new root a2 is valid and strictly smaller than a1, what have we found? (Type: contradiction)",
            "17. The contradiction proves our initial assumption (e.g. k is not a perfect square) is what? (Type: false)",
            "18. Can this method also be used to generate infinitely many solutions? (yes)",
            "19. François Viète was a mathematician from which country? (Type: france)",
            "20. Is Vieta Jumping considered an advanced Olympiad weapon? (yes)"
    };

    private String[] correctAnswers = {
            "number", "vieta", "roots", "p", "q", "descent", "smallest", "quadratic", "yes", "yes",
            "flipping", "yes", "yes", "kb", "b^2-k", "contradiction", "false", "yes", "france", "yes"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_test11);

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
            etAnswer.setHint("Type answer...");
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
            Toast.makeText(this, "Next Lesson Coming Soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(AdvTest11Activity.this, AdvLesson12Activity.class);
            // startActivity(intent);
            finish();
        });

        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());
    }

    private void checkResults() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().toLowerCase().replace(" ", "");
            String correctAnswer = correctAnswers[i].toLowerCase().replace(" ", "");

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.RED);
                saveWrongQuestion(questions[i], correctAnswers[i]);
            } else if (userAnswer.equals(correctAnswer) || userAnswer.contains(correctAnswer)) {
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
        tvScore.setText("Your Score: " + score + " / 20");

        if (score < 15) {
            tvFeedback.setText("Vieta Jumping is a masterpiece technique! Review the logic and try again.");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(51, 0); // ID 51 = Advanced 11
            try { MediaPlayer.create(this, R.raw.sad).start(); } catch(Exception e){}
        } else {
            tvFeedback.setText("MASTERPIECE! You have conquered Vieta Jumping and unlocked Lesson 12!");
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

            if (score == 15 || score == 16) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 1;
                try { MediaPlayer.create(this, R.raw.star1).start(); } catch(Exception e){}
            } else if (score >= 17 && score <= 19) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 2;
                try { MediaPlayer.create(this, R.raw.star2).start(); } catch(Exception e){}
            } else if (score == 20) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                medal3.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 3;
                try { MediaPlayer.create(this, R.raw.star3).start(); } catch(Exception e){}
            }

            saveLessonStars(51, earnedStars); // ID 51 = Advanced 11

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("adv_lesson12_unlocked", true).apply();
            prefs.edit().putInt("adv_test11_score", score).apply();
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