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

public class Test8Activity extends AppCompatActivity {

    private String[] questions = {
            "1. You have 200g of a 10% salt solution. If you add 50g of pure water, what is the new concentration? (%)",
            "2. You mix 100g of a 10% solution with 100g of a 30% solution. What is the concentration of the new mixture? (%)",
            "3. How many liters of pure water must be added to 50 liters of an 8% acid solution to drop the concentration to 5%?",
            "4. Fresh watermelon contains 99% water. After sitting in the sun, it contains 98% water. If it originally weighed 100kg, what is its new weight in kg?",
            "5. A 500g copper-zinc alloy contains 40% copper. How many grams of pure copper must be added so the new alloy contains 50% copper?",
            "6. You have a 10% solution and a 25% solution. You want 150g of a 20% solution. How many grams of the 25% solution do you need?",
            "7. Sea water contains 5% salt. How many kg of sea water must be evaporated to obtain exactly 20 kg of pure salt?",
            "8. 300g of a 20% sugar solution is left in the sun. How many grams of water must evaporate for the concentration to become 30%?",
            "9. Alloy A has 40% gold. Alloy B has 60% gold. You want an alloy with 55% gold. If you use 100g of Alloy A, how many grams of Alloy B must you use?",
            "10. If you mix equal masses of a 20% solution and an 80% solution, what is the final concentration? (%)"
    };

    private String[] correctAnswers = {
            "8", "20", "30", "50", "100", "100", "400", "100", "300", "50"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test8);

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

            // 3. Ստեղծում ենք Սրտիկը (էմոջիով)
            TextView tvHeart = new TextView(this);
            tvHeart.setPadding(20, 20, 20, 20);
            tvHeart.setTextSize(22f);
            int finalI = i;

            // Ստուգում ենք արդեն պահպանված է, թե չէ
            if (isFavourite(questions[i])) {
                tvHeart.setText("❤️");
            } else {
                tvHeart.setText("🤍");
            }

            // Սրտիկի վրա սեղմելու ֆունկցիան
            tvHeart.setOnClickListener(v -> toggleFavourite(questions[finalI], tvHeart));

            // 4. Ավելացնում ենք երկուսն էլ մեր հորիզոնական արկղիկի մեջ, իսկ արկղիկը՝ էկրանին
            questionHeader.addView(tvQuestion);
            questionHeader.addView(tvHeart);
            questionsContainer.addView(questionHeader);

            EditText etAnswer = new EditText(this);
            etAnswer.setHint("Type a number (e.g. 8)...");
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
            Intent intent = new Intent(Test8Activity.this, Lesson9Activity.class);
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

        int earnedStars = 0;

        if (score < 6) {
            tvFeedbackResult.setText("Mixture problems require patience. Review the formulas and try again!");
            tvFeedbackResult.setTextColor(Color.RED);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);

            saveLessonStars(8, 0);
        } else {
            tvFeedbackResult.setText("Amazing! You are a master of Solutions and Alloys.");
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

            saveLessonStars(8, earnedStars);

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("lesson9_unlocked", true)
                    .putInt("test8_score", score)
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