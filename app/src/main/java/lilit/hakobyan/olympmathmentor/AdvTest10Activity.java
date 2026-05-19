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

public class AdvTest10Activity extends AppCompatActivity {

    private String[] questions = {
            "1. A network of vertices connected by lines is called a what? (Type: graph)",
            "2. A graph where every vertex is connected to every other is called a what graph? (Type: complete)",
            "3. What theory maximizes or minimizes edges under certain restrictions? (Type: extremal)",
            "4. Whose theorem maximizes edges in a triangle-free graph? (Type: turan)",
            "5. A graph whose vertices can be divided into two disjoint sets is called what? (Type: bipartite)",
            "6. What theory studies conditions where order must appear within chaos? (Type: ramsey)",
            "7. In Ramsey theory problems, we usually color the what of the graph? (Type: edges)",
            "8. What is the Ramsey number R(3,3)? (Type: 6)",
            "9. Does R(3,3)=6 mean any party of 6 has 3 mutual friends or 3 mutual strangers? (yes/no)",
            "10. Are Ramsey numbers easy to compute for large values like R(5,5)? (no)",
            "11. Paul Erdős pioneered the what method to prove existence? (Type: probabilistic)",
            "12. If the probability of a graph existing is greater than 0, does the graph exist? (yes/no)",
            "13. In a complete graph K_n, how many edges connect to each vertex? (Type: n-1)",
            "14. What principle is heavily used in proving Ramsey numbers? (Type: pigeonhole)",
            "15. Is a triangle exactly a complete graph on 3 vertices? (yes/no)",
            "16. Can a bipartite graph ever contain a triangle? (no)",
            "17. According to Turán, what is the maximum number of edges in a triangle-free graph on 10 vertices? (Type: 25)",
            "18. A monochromatic triangle has edges of how many colors? (Type: 1)",
            "19. Which mathematician famously offered cash prizes for solving math problems? (Type: erdos)",
            "20. Does every sufficiently large system contain an organized subsystem? (yes)"
    };

    private String[] correctAnswers = {
            "graph", "complete", "extremal", "turan", "bipartite", "ramsey", "edges", "6", "yes", "no",
            "probabilistic", "yes", "n-1", "pigeonhole", "yes", "no", "25", "1", "erdos", "yes"
    };

    private EditText[] answerInputs;
    private TextView[] feedbackViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_test10);

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
            // Intent intent = new Intent(AdvTest10Activity.this, AdvLesson11Activity.class);
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
            tvFeedback.setText("Graph theory is abstract. Review Ramsey numbers and try again!");
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));
            findViewById(R.id.medalsLayout).setVisibility(View.GONE);
            findViewById(R.id.btnNextLesson).setVisibility(View.GONE);
            saveLessonStars(50, 0); // ID 50 = Advanced 10
            try { MediaPlayer.create(this, R.raw.sad).start(); } catch(Exception e){}
        } else {
            tvFeedback.setText("Outstanding! You have mastered Ramsey Theory and reached the midpoint of the Advanced Course!");
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

            saveLessonStars(50, earnedStars); // ID 50 = Advanced 10

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("adv_lesson11_unlocked", true).apply();
            prefs.edit().putInt("adv_test10_score", score).apply();
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