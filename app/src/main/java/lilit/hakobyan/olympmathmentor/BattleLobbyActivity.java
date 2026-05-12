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

import java.util.HashMap;
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

        // 👇 ՄԱՔՈՒՐ ԵՎ ՃԻՇՏ ՀՂՈՒՄԸ (ԱՌԱՆՑ :null -ի) 👇
        dbRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName = currentUser.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                if (currentUser.getEmail() != null) {
                    userName = currentUser.getEmail().split("@")[0];
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
            roomCode = String.valueOf(100000 + new Random().nextInt(900000));
            tvCode.setText("Your Code: " + roomCode);
            tvCode.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);
            btnCreate.setText("SENDING TO DATABASE...");

            HashMap<String, Object> roomData = new HashMap<>();
            roomData.put("player1", userName);
            roomData.put("score1", 0);
            roomData.put("status", "waiting");

            dbRef.child(roomCode).setValue(roomData)
                    .addOnSuccessListener(aVoid -> {
                        btnCreate.setText("WAITING FOR FRIEND...");
                        Toast.makeText(this, "✅ Սենյակը ստեղծված է!", Toast.LENGTH_SHORT).show();
                        listenForPlayer2();
                    })
                    .addOnFailureListener(e -> {
                        btnCreate.setText("CREATE A ROOM");
                        btnCreate.setEnabled(true);
                        Toast.makeText(this, "❌ Սխալ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // --- ԸՆԿԵՐՈՋ ՍԵՆՅԱԿԻՆ ՄԻԱՆԱԼԸ ---
        btnJoin.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();

            if (code.length() == 6) {
                Toast.makeText(this, "Checking code...", Toast.LENGTH_SHORT).show();
                dbRef.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status = snapshot.child("status").getValue(String.class);

                            if ("waiting".equals(status)) {
                                HashMap<String, Object> updates = new HashMap<>();
                                updates.put("player2", userName);
                                updates.put("score2", 0);
                                updates.put("status", "started");

                                dbRef.child(code).updateChildren(updates).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(BattleLobbyActivity.this, "Joining...", Toast.LENGTH_SHORT).show();
                                    startBattle(code, 2);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(BattleLobbyActivity.this, "Write Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            } else {
                                Toast.makeText(BattleLobbyActivity.this, "Room is full or already started!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(BattleLobbyActivity.this, "Room not found! Did you type correctly?", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BattleLobbyActivity.this, "Firebase Access Denied: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(this, "Code must be exactly 6 digits!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void listenForPlayer2() {
        waitingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "started".equals(snapshot.child("status").getValue(String.class))) {
                    dbRef.child(roomCode).removeEventListener(waitingListener);
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
        intent.putExtra("PLAYER_NUM", playerNumber);
        startActivity(intent);
        finish();
    }
}