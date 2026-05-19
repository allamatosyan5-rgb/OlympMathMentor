package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BattleLobbyActivity extends AppCompatActivity {

    private DatabaseReference dbBattles;
    private DatabaseReference dbUsers;
    private String userName;
    private String userId;
    private String roomCode;
    private ValueEventListener waitingListener;

    private LottieAnimationView lottieWaiting;

    private TextView tabChallenge, tabLeaderboard, tabHistory;
    private LinearLayout layoutSectionChallenge, layoutSectionLeaderboard, layoutSectionHistory;
    private LinearLayout layoutHistory, layoutLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_lobby);

        dbBattles = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("battles");
        dbUsers = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userName = currentUser.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                userName = currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "MathPlayer";
            }
        } else {
            userId = "guest_" + System.currentTimeMillis();
            userName = "Guest_" + new Random().nextInt(1000);
        }

        tabChallenge = findViewById(R.id.tabChallenge);
        tabLeaderboard = findViewById(R.id.tabLeaderboard);
        tabHistory = findViewById(R.id.tabHistory);

        layoutSectionChallenge = findViewById(R.id.layoutSectionChallenge);
        layoutSectionLeaderboard = findViewById(R.id.layoutSectionLeaderboard);
        layoutSectionHistory = findViewById(R.id.layoutSectionHistory);

        MaterialButton btnCreate = findViewById(R.id.btnCreateRoom);
        MaterialButton btnJoin = findViewById(R.id.btnJoinRoom);
        TextView tvCode = findViewById(R.id.tvRoomCode);
        EditText etCode = findViewById(R.id.etJoinCode);

        lottieWaiting = findViewById(R.id.lottieWaiting);
        layoutHistory = findViewById(R.id.layoutHistory);
        layoutLeaderboard = findViewById(R.id.layoutLeaderboard);

        tabChallenge.setOnClickListener(v -> switchTab("CHALLENGE"));
        tabLeaderboard.setOnClickListener(v -> {
            switchTab("LEADERBOARD");
            loadLeaderboard();
        });
        tabHistory.setOnClickListener(v -> {
            switchTab("HISTORY");
            loadHistory();
        });

        btnCreate.setOnClickListener(v -> {
            roomCode = String.valueOf(100000 + new Random().nextInt(900000));
            tvCode.setText("Your Code: " + roomCode);
            tvCode.setVisibility(View.VISIBLE);

            if (lottieWaiting != null) lottieWaiting.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);
            btnCreate.setText("SENDING TO DATABASE...");

            HashMap<String, Object> roomData = new HashMap<>();
            roomData.put("player1", userName);
            roomData.put("score1", 0);
            roomData.put("status", "waiting");

            dbBattles.child(roomCode).setValue(roomData)
                    .addOnSuccessListener(aVoid -> {
                        btnCreate.setText("WAITING FOR FRIEND...");
                        listenForPlayer2();
                    })
                    .addOnFailureListener(e -> {
                        btnCreate.setText("CREATE A ROOM");
                        btnCreate.setEnabled(true);
                        if (lottieWaiting != null) lottieWaiting.setVisibility(View.GONE);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        btnJoin.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() == 6) {
                dbBattles.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && "waiting".equals(snapshot.child("status").getValue(String.class))) {
                            HashMap<String, Object> updates = new HashMap<>();
                            updates.put("player2", userName);
                            updates.put("score2", 0);
                            updates.put("status", "started");

                            dbBattles.child(code).updateChildren(updates).addOnSuccessListener(aVoid -> startBattle(code, 2));
                        } else {
                            Toast.makeText(BattleLobbyActivity.this, "Room full or invalid!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
    }

    private void switchTab(String tabName) {
        layoutSectionChallenge.setVisibility(View.GONE);
        layoutSectionLeaderboard.setVisibility(View.GONE);
        layoutSectionHistory.setVisibility(View.GONE);

        tabChallenge.setTextColor(Color.parseColor("#A1887F"));
        tabLeaderboard.setTextColor(Color.parseColor("#A1887F"));
        tabHistory.setTextColor(Color.parseColor("#A1887F"));

        if (tabName.equals("CHALLENGE")) {
            layoutSectionChallenge.setVisibility(View.VISIBLE);
            tabChallenge.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (tabName.equals("LEADERBOARD")) {
            layoutSectionLeaderboard.setVisibility(View.VISIBLE);
            tabLeaderboard.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (tabName.equals("HISTORY")) {
            layoutSectionHistory.setVisibility(View.VISIBLE);
            tabHistory.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void listenForPlayer2() {
        waitingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "started".equals(snapshot.child("status").getValue(String.class))) {
                    dbBattles.child(roomCode).removeEventListener(waitingListener);
                    startBattle(roomCode, 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        dbBattles.child(roomCode).addValueEventListener(waitingListener);
    }

    // 💡 ԱՅՍՏԵՂ ՈՒՂԱՐԿՈՒՄ ԵՆՔ ID-ն
    private void startBattle(String code, int playerNumber) {
        Intent intent = new Intent(this, MultiplayerBattleActivity.class);
        intent.putExtra("ROOM_CODE", code);
        intent.putExtra("PLAYER_NUM", playerNumber);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);
        finish();
    }

    private void loadHistory() {
        dbUsers.child(userId).child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layoutHistory.removeAllViews();
                if (!snapshot.exists()) {
                    TextView tvEmpty = new TextView(BattleLobbyActivity.this);
                    tvEmpty.setText("No battles yet.");
                    tvEmpty.setGravity(Gravity.CENTER);
                    layoutHistory.addView(tvEmpty);
                    return;
                }

                List<DataSnapshot> historyList = new ArrayList<>();
                for (DataSnapshot match : snapshot.getChildren()) historyList.add(match);
                Collections.reverse(historyList);

                for (DataSnapshot match : historyList) {
                    String oppName = match.child("oppName").getValue(String.class);
                    Integer myScore = match.child("myScore").getValue(Integer.class);
                    Integer oppScore = match.child("oppScore").getValue(Integer.class);

                    if(oppName == null) oppName = "Unknown";
                    if(myScore == null) myScore = 0;
                    if(oppScore == null) oppScore = 0;

                    LinearLayout row = new LinearLayout(BattleLobbyActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setPadding(0, 10, 0, 10);

                    CardView cvAvatar = new CardView(BattleLobbyActivity.this);
                    cvAvatar.setLayoutParams(new LinearLayout.LayoutParams(90, 90));
                    cvAvatar.setRadius(45f);
                    cvAvatar.setCardElevation(0f);
                    ImageView ivAvatar = new ImageView(BattleLobbyActivity.this);
                    ivAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
                    ivAvatar.setBackgroundColor(Color.parseColor("#D7CCC8"));
                    ivAvatar.setPadding(10,10,10,10);
                    cvAvatar.addView(ivAvatar);

                    LinearLayout textLayout = new LinearLayout(BattleLobbyActivity.this);
                    textLayout.setOrientation(LinearLayout.VERTICAL);
                    textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                    textLayout.setPadding(16, 0, 0, 0);

                    TextView nameView = new TextView(BattleLobbyActivity.this);
                    nameView.setText("vs " + oppName);
                    nameView.setTextSize(16f);
                    nameView.setTextColor(Color.parseColor("#3E2723"));
                    nameView.setMaxLines(1);

                    TextView scoreView = new TextView(BattleLobbyActivity.this);
                    scoreView.setText(myScore + " - " + oppScore);
                    scoreView.setTextSize(18f);
                    scoreView.setTypeface(null, android.graphics.Typeface.BOLD);
                    if (myScore > oppScore) scoreView.setTextColor(Color.parseColor("#388E3C"));
                    else if (myScore < oppScore) scoreView.setTextColor(Color.parseColor("#D32F2F"));
                    else scoreView.setTextColor(Color.parseColor("#757575"));

                    textLayout.addView(nameView);
                    textLayout.addView(scoreView);

                    row.addView(cvAvatar);
                    row.addView(textLayout);
                    layoutHistory.addView(row);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLeaderboard() {
        dbUsers.orderByChild("stars").limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layoutLeaderboard.removeAllViews();

                List<DataSnapshot> usersList = new ArrayList<>();
                for (DataSnapshot user : snapshot.getChildren()) {
                    if(user.hasChild("stars")) usersList.add(user);
                }
                Collections.reverse(usersList);

                int rank = 1;
                for (DataSnapshot user : usersList) {
                    String name = user.child("name").getValue(String.class);
                    Integer stars = user.child("stars").getValue(Integer.class);
                    if (name == null) name = "Player";
                    if (stars == null) stars = 0;

                    LinearLayout row = new LinearLayout(BattleLobbyActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setPadding(0, 16, 0, 16);

                    TextView tvRank = new TextView(BattleLobbyActivity.this);
                    tvRank.setText(rank + ".");
                    tvRank.setTextSize(18f);
                    tvRank.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvRank.setTextColor(Color.parseColor("#757575"));
                    tvRank.setPadding(0,0,16,0);

                    TextView tvName = new TextView(BattleLobbyActivity.this);
                    tvName.setText(name);
                    tvName.setTextSize(18f);
                    tvName.setTextColor(Color.parseColor("#3E2723"));
                    tvName.setMaxLines(1);
                    tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                    TextView tvStars = new TextView(BattleLobbyActivity.this);
                    tvStars.setText("⭐ " + stars);
                    tvStars.setTextSize(18f);
                    tvStars.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvStars.setTextColor(Color.parseColor("#F57C00"));

                    row.addView(tvRank);
                    row.addView(tvName);
                    row.addView(tvStars);
                    layoutLeaderboard.addView(row);

                    rank++;
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}