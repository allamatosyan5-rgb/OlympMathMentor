package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

public class IntFinalExamActivity extends AppCompatActivity {

    private TextView tvTimer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = (long) (2.5 * 60 * 60 * 1000);

    private String[] questions = {
            "1. If a|b and a|c, does a always divide b-c? (yes/no)",
            "2. What is the GCD of 144 and 256?",
            "3. If x ≡ 4 (mod 7), what is the remainder of x^2 divided by 7?",
            "4. By Fermat's Little Theorem, 5^12 ≡ ? (mod 13)",
            "5. What is Euler's Totient function φ(100)?",
            "6. Find the modular inverse of 3 modulo 7.",
            "7. Solve x ≡ 2 (mod 3) and x ≡ 3 (mod 5). Smallest positive x?",
            "8. Calculate the combination C(7, 3).",
            "9. How many ways to arrange the letters in 'BOOK'?",
            "10. Number of non-negative integer solutions to x+y+z = 4?",
            "11. Pipe A fills a tank in 4h, B in 6h. How many hours together? (Use decimal)",
            "12. A boat travels 15 km/h downstream, 9 km/h upstream. Current speed?",
            "13. 20L of 40% acid is mixed with 30L of 20% acid. What is the final % concentration?",
            "14. A train 200m long passes a pole at 72 km/h. How many seconds?",
            "15. Person A is 3 times older than B. In 10 years, A will be 2 times older. B's current age?",
            "16. In logic, if P implies Q, what implies NOT P? (Type: NOT Q)",
            "17. Is the converse of a true statement always true? (yes/no)",
            "18. De Morgan's Law: NOT (A OR B) = (NOT A) ____ (NOT B). (Type: and / or)",
            "19. What is the nim-sum of 3 and 5?",
            "20. In the Josephus problem with 10 people, who survives?",
            "21. A right triangle has legs 8 and 15. Hypotenuse?",
            "22. Two similar triangles have side ratio 2:5. Area ratio? (Type: 4:25)",
            "23. Area of a triangle with sides 5, 12, 13?",
            "24. A regular polygon has interior angles of 144°. How many sides?",
            "25. In a cyclic quad, opposite angles sum to what?",
            "26. If a central angle is 120°, what is the corresponding inscribed angle?",
            "27. Power of a point (outside): secant PAB (PA=3, AB=5), tangent PT. PT=?",
            "28. Power of a point (inside): chords intersect. Parts: 2, 6 and 3, x. x=?",
            "29. Ceva's theorem product of ratios equals what?",
            "30. Menelaus's theorem product of ratios equals what?",
            "31. In any triangle, the Orthocenter, Centroid, and Circumcenter lie on the ____ Line.",
            "32. Distance from Centroid to Circumcenter is 4. Distance Orthocenter to Centroid?",
            "33. How many points are perfectly on the Feuerbach/Euler circle?",
            "34. Varignon's theorem forms what shape from midpoints of a quadrilateral?",
            "35. The intersection of symmedians is the ____ point.",
            "36. What is the name of the radical axis of two intersecting circles? (common ____)",
            "37. SFFT: xy + 2x + 3y + 6 factors to (x+3)(y+ ? )",
            "38. Does x^2 ≡ 2 (mod 4) have integer solutions? (yes/no)",
            "39. The equation 3x + 6y = 10 has no integer solutions because of what principle? (starts with B)",
            "40. How many positive integer solutions does x + y = 3 have?",
            "41. First step of mathematical induction is the ____ case.",
            "42. Sum of first n odd numbers is n^? (Type the exponent)",
            "43. The set of all subsets is called the ____ set.",
            "44. A ∩ A' (intersection with complement) equals the ____ set.",
            "45. The Cartesian product of sets with sizes 4 and 5 has how many elements?",
            "46. A function where every element in codomain is mapped is called ____.",
            "47. A function that is injective and surjective is called ____.",
            "48. The solution to Cauchy's equation f(x+y)=f(x)+f(y) is f(x) = c * ?",
            "49. What is the isogonal conjugate of the orthocenter?",
            "50. What is the maximum number of intersections of 4 distinct lines?"
    };

    private String[] correctAnswers = {
            "yes", "16", "2", "1", "40", "5", "8", "35", "12", "15",
            "2.4", "3", "28", "10", "10", "not q", "no", "and", "6", "5",
            "17", "4:25", "30", "10", "180", "60", "2", "4", "1", "-1",
            "euler", "8", "9", "parallelogram", "lemoine", "chord", "2", "no", "bezout", "2",
            "base", "2", "power", "empty", "20", "surjective", "bijective", "x", "circumcenter", "6"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_final_exam);

        tvTimer = findViewById(R.id.tvTimer);
        startTimer();

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
                Toast.makeText(IntFinalExamActivity.this, "Time is up! Auto-submitting...", Toast.LENGTH_LONG).show();
                checkResults();
            }
        }.start();
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        tvTimer.setText(timeFormatted);

        if (timeLeftInMillis < 15 * 60 * 1000) { // Վերջին 15 րոպեն՝ կարմիր
            tvTimer.setTextColor(Color.RED);
        }
    }

    private void checkResults() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            // Մաքրում ենք ավելորդ բացատները
            String userAnswer = answerInputs[i].getText().toString().trim().toLowerCase().replaceAll("\\s+", "");
            String correctAnswer = correctAnswers[i].toLowerCase().replaceAll("\\s+", "");

            answerInputs[i].setEnabled(false);
            feedbackViews[i].setVisibility(View.VISIBLE);

            if (userAnswer.isEmpty()) {
                feedbackViews[i].setText("❌ No answer. Correct: " + correctAnswers[i]);
                feedbackViews[i].setTextColor(Color.parseColor("#D32F2F"));
            } else if (userAnswer.equals(correctAnswer) || userAnswer.contains(correctAnswer)) {
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
        Button btnStartAdvanced = findViewById(R.id.btnStartAdvanced);
        Button btnRetry = findViewById(R.id.btnRetry);

        tvScore.setText("Final Score: " + score + " / " + questions.length);

        if (score < 40) {
            tvFeedbackResult.setText("You fought hard! But you need at least 40 correct answers to unlock the Advanced Level. Try again!");
            btnRetry.setVisibility(View.VISIBLE);
            btnStartAdvanced.setVisibility(View.GONE);
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);

            try { MediaPlayer.create(this, R.raw.sad).start(); } catch(Exception e){}
        } else {
            tvFeedbackResult.setText("🏆 INTERMEDIATE MASTER! 🏆\nYou have officially unlocked the Advanced Level!");
            tvFeedbackResult.setTextColor(Color.parseColor("#2E7D32"));
            btnRetry.setVisibility(View.GONE);
            btnStartAdvanced.setVisibility(View.VISIBLE);
            findViewById(R.id.medalsLayout).setVisibility(View.VISIBLE);

            ImageView medal1 = findViewById(R.id.medal1);
            ImageView medal2 = findViewById(R.id.medal2);
            ImageView medal3 = findViewById(R.id.medal3);
            medal1.setColorFilter(Color.LTGRAY);
            medal2.setColorFilter(Color.LTGRAY);
            medal3.setColorFilter(Color.LTGRAY);

            int earnedStars = 0;
            if (score >= 40 && score <= 44) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 1;
                try { MediaPlayer.create(this, R.raw.star1).start(); } catch(Exception e){}
            } else if (score >= 45 && score <= 49) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 2;
                try { MediaPlayer.create(this, R.raw.star2).start(); } catch(Exception e){}
            } else if (score == 50) {
                medal1.setColorFilter(Color.parseColor("#FFD700"));
                medal2.setColorFilter(Color.parseColor("#FFD700"));
                medal3.setColorFilter(Color.parseColor("#FFD700"));
                earnedStars = 3;
                try { MediaPlayer.create(this, R.raw.star3).start(); } catch(Exception e){}
            }

            // Պահպանում ենք անցումը (Intermediate քննությունը դնենք ID 40)
            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
            progressPrefs.edit().putInt("stars_lesson_40", earnedStars).apply();

            SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            myPrefs.edit().putBoolean("advanced_unlocked", true).apply();
        }

        btnStartAdvanced.setOnClickListener(v -> {
            Toast.makeText(this, "Welcome to the Advanced Level!", Toast.LENGTH_SHORT).show();
            // Սա կվերադարձնի Գլխավոր Էկրան, որտեղից կբացենք Advanced-ը
            Intent intent = new Intent(IntFinalExamActivity.this, MainActivity.class);
            intent.putExtra("open_advanced", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
}