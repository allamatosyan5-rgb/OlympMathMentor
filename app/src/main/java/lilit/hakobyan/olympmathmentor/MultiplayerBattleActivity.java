package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
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

    private TextView tvScore1, tvScore2, tvQuestion, tvQNumber, tvP1Name, tvP2Name;
    private EditText etAnswer;
    private int currentQIndex = 0;

    // Նախնական հարցեր Օլիմպիական ոճով
    private String[] questions = {
            "Find x: 2x + 5 = 13",
            "Smallest prime number greater than 10?",
            "Area of a square with side 6?",
            "GCD of 12 and 18?",
            "Value of 2^5?"
    };
    private String[] answers = {"4", "11", "36", "6", "32"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_battle);

        roomCode = getIntent().getStringExtra("ROOM_CODE");
        playerNum = getIntent().getIntExtra("PLAYER_NUM", 1);

        // ՄԻԱՑՈՒՄ ՔՈ ՆՈՐ ԲԱԶԱՅԻ ՀՍՏԱԿ ՀԱՍՑԵԻՆ
        roomRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles").child(roomCode);

        initViews();
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
        updateQuestionUI();
    }

    private void updateQuestionUI() {
        if (currentQIndex < questions.length) {
            tvQuestion.setText(questions[currentQIndex]);
            tvQNumber.setText("Question " + (currentQIndex + 1) + "/" + questions.length);
            etAnswer.setText("");
        } else {
            showEndGameDialog("No more questions!");
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

                    // Ապահով ստուգումներ
                    if (p1Name != null) tvP1Name.setText(p1Name);
                    if (p2Name != null) tvP2Name.setText(p2Name);
                    if (s1 != null) tvScore1.setText(String.valueOf(s1));
                    if (s2 != null) tvScore2.setText(String.valueOf(s2));

                    if (s1 != null && s2 != null) {
                        // Եթե խաղացողներից մեկը հասել է 5 միավորի
                        if (s1 >= 5 || s2 >= 5) {
                            String winner = (s1 >= 5) ? p1Name : p2Name;
                            showEndGameDialog(winner + " Wins!");
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkAnswer() {
        String userAns = etAnswer.getText().toString().trim();
        if (userAns.isEmpty()) return;

        if (userAns.equals(answers[currentQIndex])) {
            // ՃԻՇՏ Է: Ապահով ավելացնում ենք միավորը
            String scoreKey = "score" + playerNum;
            roomRef.child(scoreKey).runTransaction(new Transaction.Handler() {
                @NonNull @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer score = currentData.getValue(Integer.class);
                    if (score == null) currentData.setValue(1);
                    else currentData.setValue(score + 1);
                    return Transaction.success(currentData);
                }
                @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {}
            });

            currentQIndex++;
            updateQuestionUI();
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
            etAnswer.setText("");
        }
    }

    private void showEndGameDialog(String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Battle Finished! Check the final scores.")
                .setPositiveButton("Exit", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }
}