package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
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

public class FinalExamActivity extends AppCompatActivity {

    // --- ԹԱՅՄԵՐԻ ՓՈՓՈԽԱԿԱՆՆԵՐԸ ---
    private TextView tvTimer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 3 * 60 * 60 * 1000; // 3 ժամ

    // 50 EPIC OLYMPIAD QUESTIONS
    private String[] questions = {
            "1. What is the remainder when 245 is divided by 9?",
            "2. What is the smallest prime number strictly greater than 10?",
            "3. If the four-digit number 4A72 is perfectly divisible by 9, what is the digit A?",
            "4. What single digit should be appended to the end of 56 to make a three-digit number divisible by 9?",
            "5. What is the Least Common Multiple (LCM) of 12 and 18?",
            "6. A car travels at 60 km/h for 2.5 hours. What is the distance in km?",
            "7. Alice paints a wall in 6 hours. Bob does it in 3. How many hours will they take working together?",
            "8. A boat goes 20 km/h downstream and 10 km/h upstream. What is the speed of the stream?",
            "9. 100g of 10% salt solution is mixed with 100g of 30% salt solution. What is the total mass of salt in grams?",
            "10. If 3 pumps fill a tank in 4 hours, how many hours will 2 pumps take to fill the same tank?",
            "11. Is the sum of an odd number and an even number always ODD? (1 for YES, 0 for NO)",
            "12. There are 5 colors of balls in a bag. How many balls must you pick blindly to guarantee at least 3 of the same color?",
            "13. What is the Greatest Common Divisor (GCD) of 48 and 72?",
            "14. If GCD(a,b) = 4 and LCM(a,b) = 24, what is the product of a and b (a × b)?",
            "15. How many trailing zeros does 20! (20 factorial) have at the end?",
            "16. In the prime factorization of 100, what is the exponent of 2?",
            "17. How many positive divisors does the number 12 have in total?",
            "18. Dirichlet's Principle: 10 pigeons fly into 9 holes. At least one hole has a minimum of how many pigeons?",
            "19. What is the largest two-digit prime number?",
            "20. Are there infinitely many prime numbers? (1 for YES, 0 for NO)",
            "21. A right-angled triangle has legs of 5 and 12. What is the length of the hypotenuse?",
            "22. A triangle has sides of 7 and 10. What is the MAXIMUM possible integer length of the third side?",
            "23. Two similar triangles have a side ratio of 1:3. What is their area ratio? (1 to X. Type X)",
            "24. Thales Theorem: AD/DB = 2/3. If AE = 4, what is the length of EC?",
            "25. Using Heron's formula, what is the area of a triangle with sides 13, 14, and 15?",
            "26. Pick's Theorem: A polygon drawn on a grid has 5 internal dots and 4 boundary dots. What is its Area?",
            "27. In Ceva's Theorem, if the first two ratios are 2/1 and 1/4, what must the third ratio (CE/EA) be to equal 1?",
            "28. What is the radius of the incircle (r) of a right triangle with sides 3, 4, and 5?",
            "29. A median of a triangle is 18cm long. How far is the centroid from the vertex? (in cm)",
            "30. A right triangle has a hypotenuse of 10. What is its circumradius (R)?",
            "31. In a cyclic quadrilateral, Angle A is 80°. What is the measure of the opposite Angle C in degrees?",
            "32. Ptolemy's theorem: Opposite sides are (2 and 5) and (3 and 4). What is the product of the diagonals?",
            "33. What is the measure of ONE interior angle of a regular hexagon (in degrees)?",
            "34. How many diagonals does a convex pentagon (5 sides) have?",
            "35. Power of a Point (Inside): Chords intersect. PA=3, PB=8, PC=4. Find PD.",
            "36. Power of a Point (Outside): Tangent PT=6. External secant PA=4. What is the TOTAL length of the secant PB?",
            "37. On the Euler Line, if the distance from Orthocenter to Centroid is 8, what is the distance from Centroid to Circumcenter?",
            "38. If the circumradius of a triangle is 20, what is the radius of its Nine-Point Circle?",
            "39. How many distinct points precisely define Feuerbach's circle?",
            "40. In an acute triangle, does the orthocenter lie INSIDE the triangle? (1 for YES, 0 for NO)",
            "41. What is the remainder when 2^2024 is divided by 3? (Hint: Parity of powers)",
            "42. What is the total sum of all interior angles in a decagon (10 sides)?",
            "43. Brahmagupta's formula: A cyclic quadrilateral has sides 1, 2, 3, and 4. What is the SQUARE of its area?",
            "44. In Menelaus's Theorem, the product of the three segment ratios is always equal to what number?",
            "45. Is the number 123456 divisible by 9? (1 for YES, 0 for NO)",
            "46. A boat's speed downstream is 25 and upstream is 15. What is the speed of the current?",
            "47. A does a job in 2 days. B in 3 days. How many days for both together? (Use dot for decimal, e.g., 1.5)",
            "48. How many prime numbers are there strictly between 10 and 20?",
            "49. What is the absolute maximum number of pizza pieces you can get with exactly 3 straight cuts?",
            "50. Is the number 1 considered a prime number? (1 for YES, 0 for NO)"
    };

    private String[] correctAnswers = {
            "2", "11", "5", "7", "36",
            "150", "2", "5", "40", "6",
            "1", "11", "24", "96", "4",
            "2", "6", "2", "97", "1",
            "13", "16", "9", "6", "84",
            "6", "2", "1", "12", "5",
            "100", "22", "120", "5", "6",
            "9", "4", "10", "9", "1",
            "1", "1440", "24", "1", "0",
            "5", "1.2", "4", "7", "0"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_exam);

        tvTimer = findViewById(R.id.tvTimer);

        if (tvTimer != null) {
            startTimer();
        }

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questions[i]);
            tvQuestion.setTextSize(16f);
            tvQuestion.setTextColor(Color.parseColor("#3E2723"));
            tvQuestion.setPadding(0, 30, 0, 10);
            tvQuestion.setTypeface(null, android.graphics.Typeface.BOLD);

            questionsContainer.addView(tvQuestion);

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

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
            questionsContainer.addView(divider);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());

        findViewById(R.id.btnRetry).setOnClickListener(v -> recreate());

        findViewById(R.id.btnFinishApp).setOnClickListener(v -> {
            Toast.makeText(this, "🎉 APP COMPLETED! You are a Math Genius!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(FinalExamActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00:00");
                Toast.makeText(FinalExamActivity.this, "Time is up! Auto-submitting...", Toast.LENGTH_LONG).show();
                checkResults();
            }
        }.start();
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        tvTimer.setText(timeFormatted);

        if (timeLeftInMillis < 10 * 60 * 1000) {
            tvTimer.setTextColor(Color.RED);
        }
    }

    private void checkResults() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int score = 0;
        for (int i = 0; i < questions.length; i++) {

            String userAnswer = answerInputs[i].getText().toString().trim().replaceAll("\\s+", "").replace(",", ".");
            String correctAnswer = correctAnswers[i];

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F"));
            } else if (userAnswer.equals(correctAnswer)) {
                score++;
                feedbackViews[i].setText("✅ Correct!");
                feedbackViews[i].setTextColor(Color.parseColor("#388E3C"));
            } else {
                feedbackViews[i].setText("❌ Incorrect. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F"));
            }
        }
        showFinalResult(score);
    }
    private void showFinalResult(int score) {
        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvFeedbackResult = findViewById(R.id.tvFeedback);
        Button btnRetry = findViewById(R.id.btnRetry);
        Button btnStartIntermediate = findViewById(R.id.btnStartIntermediate);

        tvScore.setText("Final Score: " + score + " / " + questions.length);

        if (score < 35) {
            tvFeedbackResult.setText("You need at least 35 correct answers to unlock Intermediate Level. Try again!");
            btnRetry.setVisibility(View.VISIBLE);
            btnStartIntermediate.setVisibility(View.GONE);
        } else {
            tvFeedbackResult.setText("🏆 OLYMPIAD CHAMPION! 🏆\nIntermediate Level is now UNLOCKED!");
            btnRetry.setVisibility(View.GONE);
            btnStartIntermediate.setVisibility(View.VISIBLE);

            // --- ԱՅՍ ՄԱՍԸ ԿԱՐԵՎՈՐ Է ---
            SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            myPrefs.edit().putBoolean("intermediate_unlocked", true).apply();

            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
            progressPrefs.edit().putInt("stars_lesson_21", 6).apply(); // Պահում ենք որպես ավարտված
        }

        btnStartIntermediate.setOnClickListener(v -> {
            Intent intent = new Intent(FinalExamActivity.this, MainActivity.class);
            intent.putExtra("open_intermediate", true);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
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
}