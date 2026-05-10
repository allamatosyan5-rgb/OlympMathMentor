package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class BattleLobbyActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private String userName;
    private String roomCode;
    private ValueEventListener waitingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_lobby);

        // Միանում ենք Firebase-ի նոր "battles" բաժնին
        dbRef = FirebaseDatabase.getInstance().getReference("battles");

        // Ապահով կերպով վերցնում ենք օգտատիրոջ անունը կամ Email-ը
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName = currentUser.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                if (currentUser.getEmail() != null) {
                    userName = currentUser.getEmail().split("@")[0]; // Վերցնում ենք email-ի առաջին մասը
                } else {
                    userName = "MathPlayer";
                }
            }
        } else {
            userName = "Guest_" + new Random().nextInt(1000);
        }

        MaterialButton btnCreate = findViewById(R.id.btnCreateRoom);
        MaterialButton btnJoin = findViewById(R.id.btnJoinRoom);
        TextView tvCode = findViewById(R.id.tvRoomCode);
        EditText etCode = findViewById(R.id.etJoinCode);

        // --- ՍԵՆՅԱԿԻ ՍՏԵՂԾՈՒՄ ---
        btnCreate.setOnClickListener(v -> {
            // Ստեղծում ենք պատահական 6-անիշ թիվ
            roomCode = String.valueOf(100000 + new Random().nextInt(900000));
            tvCode.setText("Your Code: " + roomCode);
            tvCode.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false); // Անջատում ենք կոճակը, որ 2-րդ անգամ չսեղմի
            btnCreate.setText("WAITING FOR FRIEND...");

            // Գրանցում ենք տվյալները Firebase-ում
            dbRef.child(roomCode).child("player1").setValue(userName);
            dbRef.child(roomCode).child("score1").setValue(0);
            dbRef.child(roomCode).child("status").setValue("waiting");

            Toast.makeText(this, "Room Created! Share the code.", Toast.LENGTH_LONG).show();

            // Սպասում ենք, որ Ընկերը միանա
            listenForPlayer2();
        });

        // --- ԸՆԿԵՐՈՋ ՍԵՆՅԱԿԻՆ ՄԻԱՆԱԼԸ ---
        btnJoin.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();

            if (code.length() == 6) {
                // Ստուգում ենք, արդյոք կա՞ նման սենյակ Firebase-ում
                dbRef.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && "waiting".equals(snapshot.child("status").getValue(String.class))) {
                            // Սենյակը գտնվեց, միանում ենք որպես Player 2
                            dbRef.child(code).child("player2").setValue(userName);
                            dbRef.child(code).child("score2").setValue(0);
                            dbRef.child(code).child("status").setValue("started");

                            // Անցնում ենք Խաղի էկրանին
                            startBattle(code, 2);
                        } else {
                            Toast.makeText(BattleLobbyActivity.this, "Room not found or already full!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            } else {
                Toast.makeText(this, "Enter a valid 6-digit code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Այս ֆունկցիան սպասում է (Player 1-ի համար), որպեսզի status-ը դառնա "started"
    private void listenForPlayer2() {
        waitingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "started".equals(snapshot.child("status").getValue(String.class))) {
                    // Երբ ընկերը միանում է, անցնում ենք Խաղի էկրանին
                    dbRef.child(roomCode).removeEventListener(waitingListener); // Անջատում ենք լսողը
                    startBattle(roomCode, 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        dbRef.child(roomCode).addValueEventListener(waitingListener);
    }

    private void startBattle(String code, int playerNumber) {
        Intent intent = new Intent(this, MultiplayerBattleActivity.class);
        intent.putExtra("ROOM_CODE", code);
        intent.putExtra("PLAYER_NUM", playerNumber); // Ասում ենք Player 1 է, թե Player 2
        startActivity(intent);
        finish(); // Փակում ենք Lobby-ն, որ "Back" տալիս այստեղ չգա
    }
}