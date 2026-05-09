package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IntTest19Activity extends AppCompatActivity {

    private String[] questions = {
            "1. If set A has 5 elements, how many elements are in its Power Set?",
            "2. What is the intersection of {1, 2, 3} and {2, 3, 4}? (Format: {x,y})",
            "3. The set of all subsets of A is called the ____ Set.",
            "4. If |A|=3 and |B|=4, how many elements are in the Cartesian product A x B?",
            "5. The symbol ∅ represents the ____ set.",
            "6. (A ∪ B)ᶜ = Aᶜ ∩ Bᶜ is known as ____ Law.",
            "7. Is the set of Integers (Z) countable or uncountable?",
            "8. Does the empty set ∅ have a Power Set? (Yes or No)",
            "9. If A ⊆ B and B ⊆ A, then A and B are ____.",
            "10. What is the cardinality of the Power Set of an empty set?",
            "11. A ∩ Aᶜ results in which set?",
            "12. A ∪ Aᶜ results in which set? (Type: universal)",
            "13. If |A|=10, |B|=7, and |A ∩ B|=3, what is |A ∪ B|?",
            "14. Cantor's Theorem states |P(S)| is always ____ than |S|.",
            "15. A set with no elements is called a ____ set."
    };

    private String[] correctAnswers = {
            "32", "{2,3}", "power", "12", "empty", "de morgan", "countable", "yes", "equal", "1", "empty", "universal", "14", "greater", "null"
    };

    private EditText[] answerInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_test19);

        LinearLayout container = findViewById(R.id.questionsContainer);
        answerInputs = new EditText[questions.length];

        for (int i = 0; i < questions.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(questions[i]);
            tv.setPadding(0, 20, 0, 10);
            container.addView(tv);

            EditText et = new EditText(this);
            et.setHint("Answer...");
            answerInputs[i] = et;
            container.addView(et);
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> checkAnswers());
    }

    private void checkAnswers() {
        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            if (answerInputs[i].getText().toString().trim().toLowerCase().contains(correctAnswers[i])) {
                score++;
            }
        }

        findViewById(R.id.btnFinish).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvScore)).setText("Score: " + score + "/15");

        if (score >= 12) {
            findViewById(R.id.btnNextLesson).setVisibility(View.VISIBLE);
            findViewById(R.id.btnNextLesson).setOnClickListener(view -> {
                startActivity(new Intent(this, IntLesson20Activity.class));
                finish();
            });
        }
    }
}