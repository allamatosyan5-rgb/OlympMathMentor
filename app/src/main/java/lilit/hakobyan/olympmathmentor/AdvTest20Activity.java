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

public class AdvTest20Activity extends AppCompatActivity {

    private String[] questions = {
            "1. How many ways to choose k items from n is denoted as n choose what? (Type: k)",
            "2. The formula for n choose k is n! divided by k! times what? (Type: (n-k)!)",
            "3. Binomial coefficients form a famous triangle named after whom? (Type: pascal)",
            "4. What is 5 choose 2? (Type: 10)",
            "5. What is n choose 0? (Type: 1)",
            "6. In the expansion of (x+y)^n, the coefficients are what? (Type: binomial)",
            "7. The sum of all n choose k for k=0 to n is equal to what power of 2? (Type: 2^n)",
            "8. Does n choose k equal n choose (n-k)? (yes)",
            "9. The Vandermonde's Identity relates the sum of products of what? (Type: binomials)",
            "10. Is Pascal's triangle symmetric? (yes)",
            "11. What is 4 choose 1? (Type: 4)",
            "12. Does n choose k grow very fast as n increases? (yes)",
            "13. Binomial theorem is the foundation of what? (Type: combinatorics)",
            "14. What is the coefficient of x in (x+1)^n? (Type: n)",
            "15. What is 10 choose 1? (Type: 10)",
            "16. Binomial coefficients are always integers. (true)",
            "17. Is n choose k defined for k > n? (no)",
            "18. In the expansion (x+y)^n, how many terms are there? (Type: n+1)",
            "19. What is 3 choose 3? (Type: 1)",
            "20. Did you enjoy the Advanced Olympiad course? (yes)"
    };

    private String[] correctAnswers = {
            "k", "(n-k)!", "pascal", "10", "1", "binomial", "2^n", "yes", "binomials", "yes",
            "4", "yes", "combinatorics", "n", "10", "true", "no", "n+1", "1", "yes"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_test20);

        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];
        feedbackViews = new TextView[questions.length];

        for (int i = 0; i < questions.length; i++) {
            // ... (դիզայնը նույնն է, ինչ նախորդ թեստերում)
        }
        findViewById(R.id.btnFinish).setOnClickListener(v -> checkResults());
    }

    private void checkResults() {
        int score = 0;
        // ... (ստուգման տրամաբանությունը նույնն է)
        showFinalResult(score);
    }

    private void showFinalResult(int score) {
        // ... (հաշվետվություն, աստղեր և Next Lesson - 21-րդ դաս չկա, ավարտել)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("advanced_course_completed", true).apply();
    }
}