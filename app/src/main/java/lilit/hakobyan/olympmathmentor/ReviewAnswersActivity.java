package lilit.hakobyan.olympmathmentor;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReviewAnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_answers);

        LinearLayout container = findViewById(R.id.answersContainer);
        String[] userAnswers = getIntent().getStringArrayExtra("USER_ANSWERS");

        for (int i = 0; i < EntryTestActivity.QUESTIONS.length; i++) {
            String userAnswer = (userAnswers != null && userAnswers[i] != null) ? userAnswers[i] : "";
            boolean isCorrect = EntryTestActivity.CORRECT_ANSWERS[i].equals(userAnswer);

            TextView tv = new TextView(this);
            String text = EntryTestActivity.QUESTIONS[i] + "\n\n" +
                    "Your Answer: " + (userAnswer.isEmpty() ? "(Empty)" : userAnswer) + "\n" +
                    "Correct Answer: " + EntryTestActivity.CORRECT_ANSWERS[i] + "\n" +
                    "Points: " + (isCorrect ? EntryTestActivity.POINTS[i] : 0) + " / " + EntryTestActivity.POINTS[i] + "\n\n" +
                    "-----------------------------------\n";

            tv.setText(text);
            tv.setTextSize(16);
            // Կանաչ եթե ճիշտ է, Կարմիր եթե սխալ է
            tv.setTextColor(isCorrect ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));

            container.addView(tv);
        }
    }
}