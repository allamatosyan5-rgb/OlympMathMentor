package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IntTest20Activity extends AppCompatActivity {

    private String[] questions = {
            "1. The set of all possible input values is called the ____.",
            "2. If every element in the codomain is an output, the function is ____. (Type: surjective or injective)",
            "3. A function that is both injective and surjective is ____.",
            "4. The equation f(x + y) = f(x) + f(y) is named after which mathematician?",
            "5. If f(x) = x^2, is it an injective function for all real numbers? (Yes or No)",
            "6. In the function f(x) = 2x, what is the inverse f⁻¹(x)? (Use x/2 format)",
            "7. The actual set of outputs produced by a function is called its ____.",
            "8. Does a surjective function always have an inverse? (Yes or No)",
            "9. If f(x)=x+1 and g(x)=2x, what is f(g(3))?",
            "10. If f(g(x)) = x for all x, then f and g are ____ functions.",
            "11. To have an inverse, a function must be a ____. (Starts with B)",
            "12. What is the value of f(0) for any linear function f(x) = cx satisfying f(x+y)=f(x)+f(y)?",
            "13. If f is injective, and f(a) = f(b), then a must equal ____.",
            "14. True or False: f(g(x)) is always equal to g(f(x)).",
            "15. If the domain has 5 elements and the range has 5 elements, can the function be surjective? (Yes or No)"
    };

    private String[] correctAnswers = {
            "domain", "surjective", "bijective", "cauchy", "no", "x/2", "range", "no", "7", "inverse", "bijection", "0", "b", "false", "yes"
    };

    private EditText[] answerInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test20);

        LinearLayout container = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];

        for (int i = 0; i < questions.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(questions[i]);
            tv.setPadding(0, 20, 0, 10);
            tv.setTextColor(Color.BLACK);
            container.addView(tv);

            EditText et = new EditText(this);
            et.setHint("Answer...");
            answerInputs[i] = et;
            container.addView(et);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkAnswers());
        findViewById(R.id.btnFinalExam).setOnClickListener(v -> {
            startActivity(new Intent(this, FinalExamActivity.class));
            finish();
        });
    }

    private void checkAnswers() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            String userAns = answerInputs[i].getText().toString().trim().toLowerCase();
            if (userAns.contains(correctAnswers[i].toLowerCase())) {
                score++;
            }
        }

        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvScore)).setText("Score: " + score + "/15");

        if (score < 12) {
            ((TextView) findViewById(R.id.tvFinalMessage)).setText("Review functions once more to reach the master exam!");
            ((TextView) findViewById(R.id.tvFinalMessage)).setTextColor(Color.RED);
            findViewById(R.id.btnFinalExam).setVisibility(View.GONE);
        }
    }
}