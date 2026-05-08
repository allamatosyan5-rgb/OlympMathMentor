package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CustomTestActivity extends AppCompatActivity {

    private ArrayList<String> questions;
    private ArrayList<String> correctAnswers;
    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    private CountDownTimer countDownTimer;
    private boolean isTestFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_test);

        questions = getIntent().getStringArrayListExtra("questions");
        correctAnswers = getIntent().getStringArrayListExtra("answers");
        int timeLimit = getIntent().getIntExtra("time_limit", 0);

        if (questions == null || questions.isEmpty()) {
            finish();
            return;
        }

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);

        // Եթե ժամանակ կա ընտրված, ստեղծում ենք Ժամացույցը էկրանի վերևում
        if (timeLimit > 0) {
            TextView tvTimer = new TextView(this);
            tvTimer.setTextSize(24f);
            tvTimer.setTextColor(Color.parseColor("#D32F2F")); // Կարմիր
            tvTimer.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTimer.setGravity(android.view.Gravity.CENTER);
            tvTimer.setPadding(0, 0, 0, 30);
            questionsContainer.addView(tvTimer);

            long millisInFuture = timeLimit * 60 * 1000L;
            countDownTimer = new CountDownTimer(millisInFuture, 1000) {
                public void onTick(long millisUntilFinished) {
                    long minutes = (millisUntilFinished / 1000) / 60;
                    long seconds = (millisUntilFinished / 1000) % 60;
                    tvTimer.setText(String.format("⏱️ %02d:%02d", minutes, seconds));
                }

                public void onFinish() {
                    if (!isTestFinished) {
                        tvTimer.setText("⏱️ 00:00");
                        Toast.makeText(CustomTestActivity.this, "Time is up!", Toast.LENGTH_LONG).show();
                        checkResults(); // Ավտոմատ ավարտում է
                    }
                }
            }.start();
        }

        answerInputs = new EditText[questions.size()];
        feedbackViews = new TextView[questions.size()];

        for (int i = 0; i < questions.size(); i++) {

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(30, 30, 30, 30);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 30);
            card.setLayoutParams(params);

            TextView tvQuestion = new TextView(this);
            tvQuestion.setText("Q" + (i + 1) + ": " + questions.get(i));
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setPadding(0, 0, 0, 20);
            card.addView(tvQuestion);

            EditText etAnswer = new EditText(this);
            etAnswer.setHint("Type your answer here...");
            etAnswer.setTextSize(16f);
            etAnswer.setTextColor(Color.parseColor("#212121"));
            answerInputs[i] = etAnswer;
            card.addView(etAnswer);

            TextView tvFeedback = new TextView(this);
            tvFeedback.setTextSize(14f);
            tvFeedback.setVisibility(View.GONE);
            tvFeedback.setPadding(0, 15, 0, 0);
            tvFeedback.setTypeface(null, android.graphics.Typeface.BOLD);
            feedbackViews[i] = tvFeedback;
            card.addView(tvFeedback);

            questionsContainer.addView(card);
        }

        Button btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(v -> checkResults());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkResults() {
        if (isTestFinished) return;
        isTestFinished = true;

        // Եթե մենք ինքներս ենք կոճակով ավարտել, անջատում ենք ժամացույցը, որպեսզի շարունակ չաշխատի
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int score = 0;
        int total = questions.size();

        for (int i = 0; i < total; i++) {
            String userAnswer = answerInputs[i].getText().toString().trim().replaceAll("\\s+", "");
            String correctAnswer = correctAnswers.get(i).trim().replaceAll("\\s+", "");

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer given. Correct: " + correctAnswer);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F"));
            } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#2E7D32"));
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct: " + correctAnswer);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F"));
            }
        }
        showFinalResult(score, total);
    }

    private void showFinalResult(int score, int total) {
        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvFeedbackResult = findViewById(R.id.tvFeedback);

        tvScore.setText("Final Score: " + score + " / " + total);

        float percentage = ((float) score / total) * 100;
        int earnedStars = 0;

        if (percentage == 100) {
            earnedStars = 3;
            tvFeedbackResult.setText("Perfect! 100% Accuracy!\nYou earned 3 Bonus Stars! 🌟🌟🌟");
            tvFeedbackResult.setTextColor(Color.parseColor("#2E7D32"));
        } else if (percentage >= 90) {
            earnedStars = 2;
            tvFeedbackResult.setText("Great job! 90%+ Accuracy!\nYou earned 2 Bonus Stars! 🌟🌟");
            tvFeedbackResult.setTextColor(Color.parseColor("#2E7D32"));
        } else if (percentage >= 80) {
            earnedStars = 1;
            tvFeedbackResult.setText("Good effort! 80%+ Accuracy.\nYou earned 1 Bonus Star! 🌟");
            tvFeedbackResult.setTextColor(Color.parseColor("#FF9800"));
        } else {
            tvFeedbackResult.setText("Keep practicing! Less than 80% accuracy.\nNo stars this time.");
            tvFeedbackResult.setTextColor(Color.parseColor("#D32F2F"));
        }

        if (earnedStars > 0) {
            SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
            int currentExtraStars = prefs.getInt("extra_stars", 0);
            prefs.edit().putInt("extra_stars", currentExtraStars + earnedStars).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Հիշողությունը մաքրելու համար՝ եթե դուրս ենք գալիս էջից, անջատում ենք ժամացույցը
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}