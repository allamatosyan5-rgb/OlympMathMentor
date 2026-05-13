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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MultiplayerBattleActivity extends AppCompatActivity {

    private DatabaseReference roomRef;
    private int playerNum;
    private String roomCode;

    private TextView tvScore1, tvScore2, tvQuestion, tvQNumber, tvP1Name, tvP2Name, tvBattleTimer;
    private EditText etAnswer;
    private int currentQIndex = 0;
    private int myCorrectAnswers = 0;

    private CountDownTimer battleTimer;
    // Set timer to 7 minutes (7 * 60 * 1000 milliseconds)
    private long timeLeftInMillis = 7 * 60 * 1000;

    // List to hold the randomized order of questions
    private List<Integer> questionOrder = new ArrayList<>();

    // 200 Questions
    private String[] questions = {
            "Last digit of 2^100?", "Trailing zeros in 50!?", "Max pieces of a pizza with 4 straight cuts?",
            "How many diagonals in a decagon (10 sides)?", "Sum of the first 100 positive integers?",
            "Missing number: 2, 6, 12, 20, 30, ?", "Smallest number divisible by 1 through 6?",
            "5 machines take 5 mins for 5 shirts. Mins for 100 machines to make 100 shirts?",
            "LCM of 12 and 18?", "GCD of consecutive Fibonacci numbers 55 and 89?",
            "Number of subsets of a set with 6 elements?", "Sum of internal angles of a hexagon?",
            "Value of 3^4?", "Number of edges in a cube?", "Probability of flipping 3 heads in a row with a fair coin? (Format: x/y)",
            "What is 0! + 1! + 2! + 3!?", "How many primes are between 1 and 20?",
            "What is the 10th Fibonacci number? (F1=1, F2=1)", "Solve for x: 3x - 7 = 14",
            "Area of a triangle with base 10 and height 12?", "If a=3, b=4, what is a^2 + b^2?",
            "Evaluate log2(128).", "What is the square root of 225?", "How many vertices does a dodecahedron have?",
            "Volume of a cube with side length 4?", "How many ways to arrange 4 distinct books on a shelf?",
            "Calculate 15% of 200.", "What is the next prime after 31?", "Remainder when 100 is divided by 7?",
            "Sum of the first 5 prime numbers?", "Evaluate: 7! / 5!", "Evaluate 5^3.",
            "What is 144 divided by 12?", "Surface area of a cube with side length 3?",
            "Calculate 25 * 25.", "What is the square root of 10000?", "Evaluate log10(1000).",
            "What is 12^2?", "Perimeter of a square with area 49?", "Number of days in a leap year?",
            "What is 13^2?", "Median of 1, 3, 5, 7, 9?", "Evaluate 10^4.", "What is 2^8?",
            "Evaluate 4! + 5!.", "What is 1/2 + 1/4 as a decimal?", "Calculate 7 * 8.",
            "What is 0!?", "If a polygon has 14 diagonals, how many sides does it have?",
            "Number of sides in a heptagon?",
            "How many edges does a tetrahedron have?", "Next number: 1, 4, 9, 16, 25, ?",
            "What is 20% of 350?", "How many degrees are in a right angle?", "What is 2^10?",
            "Greatest Common Divisor (GCD) of 100 and 75?", "Least Common Multiple (LCM) of 4, 5, and 6?",
            "Evaluate: 7! / 6!", "How many prime numbers are less than 10?", "Area of a circle with radius 10? (Use 3.14)",
            "Perimeter of a rectangle with sides 5 and 12?", "Hypotenuse of a right triangle with legs 6 and 8?",
            "Sum of internal angles in a regular pentagon?", "What is 11^2?", "What is 15^2?",
            "Cube root of 64?", "Volume of a box with dimensions 2x3x4?", "What is 50 divided by 0.5?",
            "What is 30% of 80?", "Sum of the first 5 positive integers?", "Calculate: 1000 - 345",
            "Product of the first 3 prime numbers?", "How many digits are in the number 10^5?",
            "Missing number: 3, 6, 12, 24, ?", "Evaluate 5! (5 factorial).", "How many weeks are in a standard year?",
            "How many sides does a nonagon have?", "Calculate 8 * 9.", "What is the 5th prime number?",
            "Calculate 99 + 99.", "What is 2 to the power of 6?", "Square root of 144?",
            "Evaluate log10(100).", "How many letters are in the word MATHEMATICS?", "LCM of 3 and 7?",
            "How many faces does a cube have?", "Is a triangle with sides 5, 5, 8 isosceles? (Type 1 for Yes, 0 for No)",
            "What is 3/4 as a percentage?", "Calculate 12 * 12.", "What is 100 / 4?",
            "What is 1/3 of 90?", "Sum of internal angles in a quadrilateral?", "Distance traveled in 3 hours at 60 km/h?",
            "What is 7 cubed (7^3)?", "GCD of 14 and 21?", "How many minutes are in a full day?",
            "Next in sequence: 2, 4, 8, 16, ?", "Calculate 0.1 * 1000.", "Area of a square with a perimeter of 20?",
            "Sum of integers from 1 to 10?",
            "What is 14^2?", "What is the square root of 169?", "What is 3^5?", "What is 10% of 500?",
            "What is 25% of 200?", "What is 3/5 as a percentage?", "Next prime after 13?", "Smallest prime number?",
            "Sum of angles in a triangle?", "Degrees in a full circle?", "Sides in a decagon?", "Faces on an octahedron?",
            "Edges on a cube?", "Vertices on a cube?", "Evaluate log2(8).", "Evaluate log3(81).",
            "Square root of 400?", "What is 16^2?", "What is 20^2?", "What is 5^4?",
            "What is 1/8 as a decimal?", "Solve for x: 2x = 18", "What is 4!?", "What is 6!?",
            "What is 0.5 * 0.5?", "What is 1 - 0.99?", "LCM of 2, 3, and 4?", "GCD of 100 and 10?",
            "Number of items in a dozen?", "Number of items in a baker's dozen?", "What is 1000 / 8?",
            "What is 7 * 13?", "What is 8 * 12?", "What is 9 * 15?", "Next: 1, 1, 2, 3, 5, 8, 13, ?",
            "Next: 2, 3, 5, 7, 11, ?", "Solve x/3 = 12", "Solve 5x - 5 = 20", "Solve x^2 = 81 (positive value)",
            "Solve sqrt(x) = 5", "Area of circle radius 1 (use 3.14)", "Perimeter of square side 10?",
            "Area of square side 11?", "Area of rectangle 8x9?", "Sum of angles in hexagon?",
            "Sum of angles in octagon?", "How many degrees is Pi radians?", "How many degrees is Pi/2 radians?",
            "sin(30 degrees) (decimal)?", "cos(60 degrees) (decimal)?",
            "tan(45 degrees)?", "Evaluate log10(10000).", "What is 2^7?", "What is 3^3?",
            "What is 4^3?", "What is 6^3?", "What is 8^3?", "What is 9^3?", "What is 10^3?",
            "Square root of 81?", "Square root of 121?", "Square root of 196?", "Square root of 225?",
            "Square root of 256?", "Square root of 289?", "Square root of 324?", "Square root of 361?",
            "What is 21^2?", "What is 25^2?", "What is 30^2?", "What is 1% of 1000?", "What is 5% of 200?",
            "What is 50% of 50?", "What is 200% of 15?", "0.2 as a fraction (1/x, find x)?",
            "0.125 as a fraction (1/x, find x)?", "Sum of 10+11+12?", "Average of 10, 20, 30?",
            "Average of 5, 10, 15, 20?", "Missing: 100, 90, 80, 70, ?", "Missing: 1, 8, 27, 64, ?",
            "Evaluate 10! / 9!", "Evaluate 8! / 6!", "What is 2+2*2?", "What is (2+2)*2?",
            "What is 10-3*3?", "What is 100/10/2?", "What is 100/(10/2)?", "What is 2^2^2?",
            "LCM of 10 and 15?", "GCD of 24 and 36?", "Number of primes less than 20?",
            "Smallest composite number?", "Is 51 prime? (1 for Yes, 0 for No)", "Is 91 prime? (1 for Yes, 0 for No)",
            "What is 7 * 12?", "What is 8 * 15?", "What is 11 * 12?", "Next prime after 19?", "What is 0^0? (conventionally)"
    };

    // 200 Answers
    private String[] answers = {
            "6", "12", "11", "35", "5050", "42", "60", "5", "36", "1", "64", "720", "81", "12", "1/8",
            "10", "8", "55", "7", "60", "25", "7", "15", "20", "64", "24", "30", "37", "2", "28", "42", "125",
            "12", "54", "625", "100", "3", "144", "28", "366", "169", "5", "10000", "256", "144", "0.75", "56",
            "1", "7", "7",
            "6", "36", "70", "90", "1024", "25", "60", "7", "4", "314", "34", "10", "540", "121", "225",
            "4", "24", "100", "24", "15", "655", "30", "6", "48", "120", "52", "9", "72", "11", "198", "64",
            "12", "2", "11", "21", "6", "1", "75", "144", "25", "30", "360", "180", "343", "7", "1440", "32",
            "100", "25", "55",
            "196", "13", "243", "50", "50", "60", "17", "2", "180", "360", "10", "8", "12", "8", "3", "4",
            "20", "256", "400", "625", "0.125", "9", "24", "720", "0.25", "0.01", "12", "10", "12", "13", "125",
            "91", "96", "135", "21", "13", "36", "5", "9", "25", "3.14", "40", "121", "72", "720", "1080", "180", "90",
            "0.5", "0.5",
            "1", "4", "128", "27", "64", "216", "512", "729", "1000", "9", "11", "14", "15", "16", "17", "18", "19",
            "441", "625", "900", "10", "10", "25", "30", "5", "8", "33", "20", "12.5", "60", "125", "10", "56",
            "6", "8", "1", "5", "20", "16", "30", "12", "8", "4", "0", "0", "84", "120", "132", "23", "1"
    };

    private boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_battle);

        roomCode = getIntent().getStringExtra("ROOM_CODE");
        playerNum = getIntent().getIntExtra("PLAYER_NUM", 1);

        roomRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles").child(roomCode);

        // Initialize randomized questions list based on the room code
        initRandomizer();

        initViews();
        startTimer();
        listenToBattle();

        findViewById(R.id.btnSubmitBattle).setOnClickListener(v -> checkAnswer());
    }

    // Function to shuffle the questions synchronously for both players
    private void initRandomizer() {
        for (int i = 0; i < questions.length; i++) {
            questionOrder.add(i);
        }
        // Using roomCode as the seed ensures both players get the exact same random order
        long seed = Long.parseLong(roomCode);
        Collections.shuffle(questionOrder, new Random(seed));
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
                // Update player 1 name area with timer
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
        // Limit the game to exactly 10 questions
        if (currentQIndex < 10) {
            int realIndex = questionOrder.get(currentQIndex); // Get the shuffled index
            tvQuestion.setText(questions[realIndex]);
            tvQNumber.setText("Question " + (currentQIndex + 1) + "/10");
            etAnswer.setText("");
        } else {
            if (!gameEnded) endGame("You finished all 10 questions!");
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

                    // End game if someone reaches 10 points
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

        int realIndex = questionOrder.get(currentQIndex); // Get the correct answer from the shuffled order

        if (userAns.equals(answers[realIndex])) {
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

                // Time spent out of 7 minutes
                long timeSpent = (7 * 60 * 1000) - timeLeftInMillis;

                if (myScore > oppScore) {
                    if (myScore == 10 && timeSpent < 60000) starsEarned = 3;
                    else if (myScore >= 8) starsEarned = 2;
                    else starsEarned = 1;

                    resultMsg = "YOU WON! \uD83C\uDFC6\nScore: " + myScore + " to " + oppScore + "\nStars Earned: " + starsEarned;
                    saveBattleStars(starsEarned);
                } else if (myScore == oppScore) {
                    resultMsg = "It's a TIE! \uD83E\uDD1D\nScore: " + myScore + " to " + oppScore;
                } else {
                    resultMsg = "YOU LOST! \uD83D\uDE22\nScore: " + myScore + " to " + oppScore;
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
                    // Only player 1 removes the room to clean up the database
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