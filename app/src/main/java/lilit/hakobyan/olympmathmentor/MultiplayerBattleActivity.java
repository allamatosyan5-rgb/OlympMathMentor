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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MultiplayerBattleActivity extends AppCompatActivity {

    private DatabaseReference roomRef;
    private int playerNum;
    private String roomCode;

    // 💡 Ստանում ենք Lobby-ից եկած տվյալները
    private String myUserId;
    private String myUserNameStr;

    private TextView tvScore1, tvScore2, tvQuestion, tvQNumber, tvP1Name, tvP2Name, tvBattleTimer;
    private EditText etAnswer;

    private int currentQIndex = 0;
    private int myCorrectAnswers = 0;

    private CountDownTimer battleTimer;
    private long timeLeftInMillis = 7 * 60 * 1000;

    private List<Integer> questionOrder = new ArrayList<>();
    private boolean gameEnded = false;

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
            "Sum of the first 5 prime numbers?"
    };

    private String[] answers = {
            "6", "12", "11", "35", "5050", "42", "60", "5", "36", "1", "64", "720", "81", "12", "1/8",
            "10", "8", "55", "7", "60", "25", "7", "15", "20", "64", "24", "30", "37", "2", "28"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_battle);

        roomCode = getIntent().getStringExtra("ROOM_CODE");
        playerNum = getIntent().getIntExtra("PLAYER_NUM", 1);
        myUserId = getIntent().getStringExtra("USER_ID");
        myUserNameStr = getIntent().getStringExtra("USER_NAME");

        roomRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles").child(roomCode);

        initRandomizer();
        initViews();
        startTimer();
        listenToBattle();

        findViewById(R.id.btnSubmitBattle).setOnClickListener(v -> checkAnswer());
    }

    private void initRandomizer() {
        for (int i = 0; i < questions.length; i++) {
            questionOrder.add(i);
        }
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
                tvP1Name.setText("P1 \n" + String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                if (!gameEnded) endGame("Time's Up!");
            }
        }.start();
    }

    private void updateQuestionUI() {
        if (currentQIndex < 10 && currentQIndex < questions.length) {
            int realIndex = questionOrder.get(currentQIndex);
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

                    if (s1 != null && s2 != null && !gameEnded) {
                        if (s1 >= 10 || s2 >= 10) endGame("Someone reached 10 points!");
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

        int realIndex = questionOrder.get(currentQIndex);
        String questionText = questions[realIndex];
        String correctAnswer = answers[realIndex];

        if (userAns.equalsIgnoreCase(correctAnswer)) {
            processCorrectAnswer();
        } else {
            verifyWithAIJudge(questionText, correctAnswer, userAns);
        }
    }

    private void processCorrectAnswer() {
        myCorrectAnswers++;
        String scoreKey = "score" + playerNum;
        roomRef.child(scoreKey).setValue(myCorrectAnswers);
        Toast.makeText(this, "Correct! +1", Toast.LENGTH_SHORT).show();
        currentQIndex++;
        updateQuestionUI();
    }

    private void verifyWithAIJudge(String question, String officialAnswer, String userAnswer) {
        findViewById(R.id.btnSubmitBattle).setEnabled(false);

        String aiPrompt = "Determine if these two math answers are equivalent. " +
                "Official: '" + officialAnswer + "'. User: '" + userAnswer + "'. " +
                "Respond ONLY with 'YES' or 'NO'.";

        String apiKey = BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject textObj = new JSONObject();
            textObj.put("text", aiPrompt);
            partsArray.put(textObj);
            part.put("parts", partsArray);
            contents.put(part);
            jsonBody.put("contents", contents);

            JSONObject config = new JSONObject();
            config.put("temperature", 0.1);
            jsonBody.put("generationConfig", config);
        } catch (Exception e) { e.printStackTrace(); }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    findViewById(R.id.btnSubmitBattle).setEnabled(true);
                    Toast.makeText(MultiplayerBattleActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String aiResponse = jsonResponse.getJSONArray("candidates")
                                .getJSONObject(0).getJSONObject("content")
                                .getJSONArray("parts").getJSONObject(0).getString("text").trim().toUpperCase();

                        runOnUiThread(() -> {
                            findViewById(R.id.btnSubmitBattle).setEnabled(true);
                            if (aiResponse.contains("YES")) processCorrectAnswer();
                            else {
                                Toast.makeText(MultiplayerBattleActivity.this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
                                etAnswer.setText("");
                            }
                        });
                    } catch (Exception e) { e.printStackTrace(); }
                } else {
                    runOnUiThread(() -> findViewById(R.id.btnSubmitBattle).setEnabled(true));
                }
            }
        });
    }

    private void endGame(String reason) {
        gameEnded = true;
        if (battleTimer != null) battleTimer.cancel();

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer s1 = snapshot.child("score1").getValue(Integer.class);
                Integer s2 = snapshot.child("score2").getValue(Integer.class);
                String p1Name = snapshot.child("player1").getValue(String.class);
                String p2Name = snapshot.child("player2").getValue(String.class);

                if (s1 == null) s1 = 0; if (s2 == null) s2 = 0;
                if (p1Name == null) p1Name = "Player 1"; if (p2Name == null) p2Name = "Player 2";

                int myScore = (playerNum == 1) ? s1 : s2;
                int oppScore = (playerNum == 1) ? s2 : s1;

                String myName = (playerNum == 1) ? p1Name : p2Name;
                String oppName = (playerNum == 1) ? p2Name : p1Name;

                // 💡 ՊԱՀՊԱՆՈՒՄ ԵՆՔ ԱՐԴՅՈՒՆՔՆԵՐԸ FIREBASE-ՈՒՄ 💡
                if (myUserId != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(myUserId);

                    SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    int currentTotalStars = myPrefs.getInt("total_stars", 0);
                    int newTotalStars = currentTotalStars + myScore; // Ավելանում են խաղում վաստակածը

                    myPrefs.edit().putInt("total_stars", newTotalStars).apply();

                    userRef.child("name").setValue(myName);
                    userRef.child("stars").setValue(newTotalStars);

                    java.util.HashMap<String, Object> historyMap = new java.util.HashMap<>();
                    historyMap.put("oppName", oppName);
                    historyMap.put("myScore", myScore);
                    historyMap.put("oppScore", oppScore);
                    userRef.child("history").push().setValue(historyMap);
                }

                Intent intent = new Intent(MultiplayerBattleActivity.this, BattleResultActivity.class);
                intent.putExtra("MY_SCORE", myScore);
                intent.putExtra("OPP_SCORE", oppScore);
                intent.putExtra("MY_NAME", myName);
                intent.putExtra("OPP_NAME", oppName);
                intent.putExtra("STARS", myScore);

                if (playerNum == 1) roomRef.removeValue();
                startActivity(intent);
                finish();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (battleTimer != null) battleTimer.cancel();
    }
}