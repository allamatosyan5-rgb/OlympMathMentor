package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class EntryTestActivity extends AppCompatActivity {

    private TextView tvTimer, tvQuestion;
    private EditText etAnswer;
    private Button btnNext, btnBack, btnFinish;
    private CountDownTimer timer;

    private int currentIdx = 0;
    private long timeLeftInMillis = 120 * 60 * 1000; // 120 րոպե
    private Map<Integer, String> userAnswers = new HashMap<>();

    public static final String[] QUESTIONS = {
            "1. How many natural numbers can be formed using the digits 0, 1, 2, 5, and 7?",
            "2. Let I be the incenter of △ABC, and M be the midpoint of AB. Given AB=18, BC=21, and ∠AIM=90°, find the length of AC.",
            "3. In a football tournament, 28 matches were played. How many teams participated if each team played one match against every other team?",
            "4. How many zeros does the number 51! end with?",
            "5. One of the angles of a right triangle is 21°. Find the angle between the median and the angle bisector drawn from the vertex of the right angle.",
            "6. Chairs around a round table are numbered consecutively. The number of chairs between 16 and 37 equals the number between 45 and 6. Total chairs?",
            "7. Find the smallest natural number that leaves a remainder of 1 when divided by 2, 3, 5, 7, 11, and 13.",
            "8. Find the sum of all roots of the equation (x^2−1)(x^2−5x+1)=0.",
            "9. Ann thought of a number, multiplied it by 13, erased the last digit, multiplied by 7, erased the last digit, resulting in 21. What was the number?",
            "10. In isosceles △ABC (AC=10), altitude AD and bisector BE are drawn. Find DE.",
            "11. How many integer solutions does x^2+3xy+2x−3y=26 have?",
            "12. Find the number of natural numbers n < 2025 such that 1^n+2^n+3^n+4^n ends with 0."
    };

    public static final String[] CORRECT_ANSWERS = {"260", "24", "8", "12", "24", "60", "30031", "5", "24", "5", "2", "1518"};
    public static final int[] POINTS = {50, 50, 50, 50, 100, 100, 100, 100, 150, 150, 150, 150};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_test);

        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnFinish = findViewById(R.id.btnFinish);

        startTimer();
        displayQuestion();

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIdx < QUESTIONS.length - 1) {
                currentIdx++;
                displayQuestion();
            }
        });

        btnBack.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIdx > 0) {
                currentIdx--;
                displayQuestion();
            }
        });

        btnFinish.setOnClickListener(v -> finishTest());
    }

    private void displayQuestion() {
        tvQuestion.setText(QUESTIONS[currentIdx]);
        if (userAnswers.containsKey(currentIdx)) {
            etAnswer.setText(userAnswers.get(currentIdx));
        } else {
            etAnswer.setText("");
        }
    }

    private void saveCurrentAnswer() {
        String answer = etAnswer.getText().toString().trim();
        userAnswers.put(currentIdx, answer);
    }

    private void finishTest() {
        if (timer != null) timer.cancel();
        saveCurrentAnswer();

        int totalScore = 0;
        String[] userAnsArray = new String[QUESTIONS.length];

        for (int i = 0; i < QUESTIONS.length; i++) {
            String ans = userAnswers.containsKey(i) ? userAnswers.get(i) : "";
            userAnsArray[i] = ans;
            if (CORRECT_ANSWERS[i].equals(ans)) {
                totalScore += POINTS[i];
            }
        }

        String level = "Beginner";
        if (totalScore > 900) {
            level = "Advanced";
        } else if (totalScore > 400) {
            level = "Intermediate";
        }

        // 1. Պահում ենք պրոֆիլում (Մակարդակը)
        SharedPreferences profilePrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        profilePrefs.edit().putString("level", level).apply();

        // 2. Պահում ենք UserProgress-ում, որ հաջորդ անգամ հաստատ իմանա, որ թեստը հանձնված է
        SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        progressPrefs.edit().putBoolean("entry_test_done", true).apply();

        // 3. Ուղարկում ենք Firebase ԱՆՄԻՋԱՊԵՍ
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("backup").child("profile").child("level").setValue(level);
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("backup").child("progress").child("entry_test_done").setValue(true);
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", totalScore);
        intent.putExtra("LEVEL", level);
        intent.putExtra("USER_ANSWERS", userAnsArray);
        startActivity(intent);
        finish();
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }
            @Override
            public void onFinish() {
                finishTest();
            }
        }.start();
    }
}