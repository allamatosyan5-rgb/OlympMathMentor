package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class MultiplayerBattleActivity extends AppCompatActivity {

    private DatabaseReference roomRef;
    private int playerNum;
    private String roomCode;

    private TextView tvScore1, tvScore2, tvQuestion, tvQNumber, tvP1Name, tvP2Name, tvBattleTimer;
    private EditText etAnswer;
    private int currentQIndex = 0;
    private int myCorrectAnswers = 0;

    private CountDownTimer battleTimer;
    private long timeLeftInMillis = 3 * 60 * 1000;

    private String[] questions = {
            "Find x: 2x + 5 = 13",
            "Smallest prime > 10?",
            "Area of a square with side 6?",
            "GCD of 12 and 18?",
            "Value of 2^5?",
            "LCM of 4 and 6?",
            "Interior angle of regular hexagon?",
            "7 * 8 - 15 = ?",
            "Is 51 prime? (Type 1 for yes, 0 for no)",
            "What is 30% of 200?"
    };
    private String[] answers = {"4", "11", "36", "6", "32", "12", "120", "41", "0", "60"};

    private boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_battle);

        roomCode = getIntent().getStringExtra("ROOM_CODE");
        playerNum = getIntent().getIntExtra("PLAYER_NUM", 1);

        // 👇 ՄԱՔՈՒՐ ԵՎ ՃԻՇՏ ՀՂՈՒՄԸ ԱՅՍՏԵՂ ՆՈՒՅՆՊԵՍ 👇
        roomRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("battles").child(roomCode);

        initViews();
        startTimer();
        listenToBattle();

        findViewById(R.id.btnSubmitBattle).setOnClickListener(v -> checkAnswer());
    }

    private void initViews() {
        tvScore1 = findViewById(R.id.tvScore1);
        tvScore2 = findViewById(R.id.tvScore2);
        tvQuestion = findViewById(R.id.tvBattleQuestion);
        tvQNumber = findViewById(R.id.tvQuestionNumber);
        tvP1Name = findViewById(R.id.tvPlayer1Name);
        tvP2Name = findViewById(R.id.tvPlayer2Name);
        etAnswer = findViewById(R.id.etBattleAnswer);

        tvBattleTimer = findViewById(R.id.tvQuestionNumber);

        updateQuestionUI();
    }

    private void startTimer() {
        battleTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                tvP1Name.setText("P1 \n" + String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                if (!gameEnded) {
                    endGame("Time's Up!");
                }
            }
        }.start();
    }

    private void updateQuestionUI() {
        if (currentQIndex < questions.length) {
            tvQuestion.setText(questions[currentQIndex]);
            tvQNumber.setText("Question " + (currentQIndex + 1) + "/10");
            etAnswer.setText("");
        } else {
            if (!gameEnded) endGame("You finished all questions!");
        }
    }

    private void listenToBattle() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String p1Name = snapshot.child("player1").getValue(String.class);
                    String p2Name = snapshot.child("player2").getValue(String.class);
                    Integer s1 = snapshot.child("score1").getValue(Integer.class);
                    Integer s2 = snapshot.child("score2").getValue(Integer.class);

                    if (p1Name != null) tvP1Name.setText(p1Name);
                    if (p2Name != null) tvP2Name.setText(p2Name);
                    if (s1 != null) tvScore1.setText(String.valueOf(s1));
                    if (s2 != null) tvScore2.setText(String.valueOf(s2));

                    if (s1 != null && s2 != null && !gameEnded) {
                        if (s1 >= 10 || s2 >= 10) {
                            String winner = (s1 > s2) ? p1Name : p2Name;
                            endGame(winner + " reached 10 points!");
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkAnswer() {
        if (gameEnded) return;

        String userAns = etAnswer.getText().toString().trim();
        if (userAns.isEmpty()) return;

        if (userAns.equals(answers[currentQIndex])) {
            myCorrectAnswers++;
            String scoreKey = "score" + playerNum;

            roomRef.child(scoreKey).setValue(myCorrectAnswers);

            Toast.makeText(this, "Correct! +1", Toast.LENGTH_SHORT).show();
            currentQIndex++;
            updateQuestionUI();
        } else {
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
            etAnswer.setText("");
        }
    }

    private void endGame(String reason) {
        gameEnded = true;
        if (battleTimer != null) battleTimer.cancel();

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer s1 = snapshot.child("score1").getValue(Integer.class);
                Integer s2 = snapshot.child("score2").getValue(Integer.class);
                if (s1 == null) s1 = 0;
                if (s2 == null) s2 = 0;

                int myScore = (playerNum == 1) ? s1 : s2;
                int oppScore = (playerNum == 1) ? s2 : s1;

                String resultMsg;
                int starsEarned = 0;

                if (myScore > oppScore) {
                    long timeSpent = (3 * 60 * 1000) - timeLeftInMillis;
                    if (myScore == 10 && timeSpent < 60000) starsEarned = 3;
                    else if (myScore >= 8) starsEarned = 2;
                    else starsEarned = 1;

                    resultMsg = "YOU WON! 🏆\nScore: " + myScore + " to " + oppScore + "\nStars Earned: " + starsEarned;
                    saveBattleStars(starsEarned);
                } else if (myScore == oppScore) {
                    resultMsg = "It's a TIE! 🤝\nScore: " + myScore + " to " + oppScore;
                } else {
                    resultMsg = "YOU LOST! 😢\nScore: " + myScore + " to " + oppScore;
                }

                showResultDialog(reason, resultMsg);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveBattleStars(int newStars) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        int currentBattleStars = prefs.getInt("battle_stars", 0);
        prefs.edit().putInt("battle_stars", currentBattleStars + newStars).apply();
    }

    private void showResultDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Back to Menu", (d, w) -> {
                    if (playerNum == 1) roomRef.removeValue();
                    startActivity(new Intent(MultiplayerBattleActivity.this, MainActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (battleTimer != null) battleTimer.cancel();
    }
}