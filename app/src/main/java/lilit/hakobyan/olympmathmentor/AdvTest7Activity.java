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

public class AdvTest7Activity extends AppCompatActivity {

    private String[] questions = {
            "1. LTE stands for Lifting The what Lemma?",
            "2. The function v_p(n) denotes the highest what of p dividing n?",
            "3. In LTE for x^n-y^n, does p need to be prime? (yes/no)",
            "4. Is the standard LTE formula exactly the same for p=2 without extra conditions? (yes/no)",
            "5. For odd prime p, must p divide x-y to apply LTE to x^n-y^n? (yes/no)",
            "6. Must p divide x or y directly in LTE? (yes/no)",
            "7. If v_3(x-y) = 2 and v_3(n) = 4, what is v_3(x^n-y^n)?",
            "8. What is v_5(25)?",
            "9. The equation x^2 - dy^2 = 1 is called whose equation?",
            "10. In Pell's equation, d must not be a perfect what?",
            "11. Are there infinitely many integer solutions to Pell's equation if d>1 is not a square? (yes/no)",
            "12. The smallest positive solution to Pell's is called the what solution?",
            "13. If (x1, y1) is a solution, others are found by expanding (x1+y1*sqrt(d))^n. (true/false)",
            "14. Does LTE apply to x^n+y^n if n is an odd integer? (yes/no)",
            "15. Does LTE apply to x^n+y^n if n is an even integer? (yes/no)",
            "16. Is x=1, y=0 considered the trivial solution to Pell's equation? (yes/no)",
            "17. If v_2(x-y) = 3, v_2(x+y) = 1, what is v_2(x^2-y^2)?",
            "18. What is v_7(49)?",
            "19. Is Pell's equation an example of a Diophantine equation? (yes/no)",
            "20. If p=3, x=17, y=8, what is v_3(x-y)?"
    };

    private String[] correctAnswers = {
            "exponent", "power", "yes", "no", "yes", "no", "6", "2", "pell", "square", "yes", "fundamental",
            "true", "yes", "no", "yes", "4", "2", "yes", "2"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_test7);

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
            // Intent intent = new Intent(AdvTest7Activity.this, AdvLesson8Activity.class);
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
            tvFeedback.setText("Number theory is tough. Review the conditions of LTE and try again!");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(47, 0); // ID 47 = Advanced 7
            try { MediaPlayer.create(this, R.raw.sad).start(); } catch(Exception e){}
        } else {
            tvFeedback.setText("Outstanding! You have mastered the Lifting The Exponent Lemma!");
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

            saveLessonStars(47, earnedStars); // ID 47 = Advanced 7

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("adv_lesson8_unlocked", true).apply();
            prefs.edit().putInt("adv_test7_score", score).apply();
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