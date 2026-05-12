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
            "Last digit of 2^100?",
            "Trailing zeros in 50!?",
            "Max pieces of a pizza with 4 straight cuts?",
            "How many diagonals in a decagon (10 sides)?",
            "Sum of the first 100 positive integers?",
            "Missing number: 2, 6, 12, 20, 30, ?",
            "Smallest number divisible by 1 through 6?",
            "5 machines take 5 mins for 5 shirts. Mins for 100 machines to make 100 shirts?",
            "LCM of 12 and 18?",
            "GCD of consecutive Fibonacci numbers 55 and 89?",
            "Number of subsets of a set with 6 elements?",
            "Sum of internal angles of a hexagon?",
            "Value of 3^4?",
            "Number of edges in a cube?",
            "Probability of flipping 3 heads in a row with a fair coin? (Format: x/y)",
            "What is 0! + 1! + 2! + 3!?",
            "How many primes are between 1 and 20?",
            "What is the 10th Fibonacci number? (F1=1, F2=1)",
            "Solve for x: 3x - 7 = 14",
            "Area of a triangle with base 10 and height 12?",
            "If a=3, b=4, what is a^2 + b^2?",
            "Evaluate log2(128).",
            "What is the square root of 225?",
            "How many vertices does a dodecahedron have?",
            "Volume of a cube with side length 4?",
            "How many ways to arrange 4 distinct books on a shelf?",
            "Calculate 15% of 200.",
            "What is the next prime after 31?",
            "Remainder when 100 is divided by 7?",
            "Sum of the first 5 prime numbers?",
            "Evaluate: 7! / 5!",
            "Evaluate 5^3.",
            "What is 144 divided by 12?",
            "Surface area of a cube with side length 3?",
            "Calculate 25 * 25.",
            "What is the square root of 10000?",
            "Evaluate log10(1000).",
            "What is 12^2?",
            "Perimeter of a square with area 49?",
            "Number of days in a leap year?",
            "What is 13^2?",
            "Median of 1, 3, 5, 7, 9?",
            "Evaluate 10^4.",
            "What is 2^8?",
            "Evaluate 4! + 5!.",
            "What is 1/2 + 1/4 as a decimal?",
            "Calculate 7 * 8.",
            "What is 0!?",
            "If a polygon has 14 diagonals, how many sides does it have?",
            "Number of sides in a heptagon?"
    };
    private String[] answers = {"6","12", "11", "35", "5050", "42", "60", "5", "36", "1", "64", "720", "81", "12", "1/8",
            "10",
            "8",
            "55",
            "7",
            "60",
            "25",
            "7",
            "15",
            "20",
            "64",
            "24",
            "30",
            "37",     // Q28: Prime after 31
            "2",      // Q29: 100 % 7
            "28",     // Q30: 2+3+5+7+11
            "42",     // Q31: 7!/5! = 7*6
            "125",    // Q32: 5^3
            "12",     // Q33: 144/12
            "54",     // Q34: 6*(3^2)
            "625",    // Q35: 25^2
            "100",    // Q36: sqrt(10000)
            "3",      // Q37: log10(1000)
            "144",    // Q38: 12^2
            "28",     // Q39: 4*7
            "366",    // Q40: Leap year
            "169",    // Q41: 13^2
            "5",      // Q42: Median
            "10000",  // Q43: 10^4
            "256",    // Q44: 2^8
            "144",    // Q45: 24+120
            "0.75",   // Q46: 1/2+1/4
            "56",     // Q47: 7*8
            "1",      // Q48: 0!
            "7",      // Q49: n(n-3)/2 = 14 -> n=7
            "7"       // Q50: Heptagon
     };

    private boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_battle);

        roomCode = getIntent().getStringExtra("ROOM_CODE");
        playerNum = getIntent().getIntExtra("PLAYER_NUM", 1);

        // 👇 ԱՀԱ ՃԻՇՏ ՀՂՈՒՄՈՎ ՏՈՂԸ 👇
        roomRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles").child(roomCode);

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