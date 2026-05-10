package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// Ավելացնում ենք MaterialButton-ը
import com.google.android.material.button.MaterialButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoListActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private SharedPreferences localPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        LinearLayout videoContainer = findViewById(R.id.videoContainer);
        dbRef = FirebaseDatabase.getInstance().getReference("video_ratings");
        localPrefs = getSharedPreferences("VideoStatus", MODE_PRIVATE);

        String[][] videos = {
                {"Video Lesson 1", "https://youtu.be/hEN6_v6gSEo?si=BnFMFdFLATvnldrC"},
                {"Video Lesson 2", "https://youtu.be/2LTtg3clc9g?si=d0cy8Q99akB2ac9Z"},
                {"Video Lesson 3", "https://youtu.be/P4LxK9Uek5U?si=oa5MEPXYRhjVhse-"},
                {"Video Lesson 4", "https://youtu.be/VHKZSugBzhA?si=8Ca17ikiIWm1RySn"},
                {"Video Lesson 5", "https://youtu.be/bVbtqn4-Df8?si=MMBiRfSpCDIW8HVO"},
                {"Video Lesson 6", "https://youtu.be/FFEtckUNtz0?si=vd8EQ5uyy2Wmdxhd"},
                {"Video Lesson 7", "https://youtu.be/FFEtckUNtz0?si=gSxRjAi9cLQm4MvG"},
                {"Video Lesson 8", "https://youtu.be/i5Tv1hlrwL8?si=o5auc_eYp3y13deo"},
                {"Video Lesson 9", "https://youtu.be/fNBzjgWzf_Y?si=RP7Gtj020Yp3gfYW"},
                {"Video Lesson 10", "https://youtu.be/LCPLWDlX1ZI?si=ekIWKQl9yTTywKxD"},
                {"Video Lesson 11", "https://youtu.be/qEyFpMBw3wA?si=eQn_pm_qDX7j218h"},
                {"Video Lesson 12", "https://youtu.be/mq4mHa2lBi8?si=euTt4uUydoyicuT8"},
                {"Video Lesson 13", "https://youtu.be/QRXtyVRlNqg?si=-rpCMSDAycHXSgZR"},
                {"Video Lesson 14", "https://youtu.be/TrDVV0xcACI?si=uUD8RMtvQvbeJ7tc"},
                {"Video Lesson 15", "https://youtu.be/T09Iq9Q61Wc?si=TlwoxuJtcA0iEyjo"},
                {"Video Lesson 16", "https://youtu.be/O3krHAFyxH4?si=m3ydIubuIHXb5CA5"}
        };

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String[] video : videos) {
            View card = inflater.inflate(R.layout.item_video, videoContainer, false);
            TextView tvTitle = card.findViewById(R.id.tvVideoTitle);
            TextView tvAvg = card.findViewById(R.id.tvAverageRating);
            RatingBar rbAvg = card.findViewById(R.id.rbVideoRating);

            // Փոխում ենք MaterialButton-ի
            MaterialButton btnWatch = card.findViewById(R.id.btnWatchVideo);

            String videoId = video[0].replace(" ", "_");
            tvTitle.setText(video[0]);

            // Ստուգում ենք արդյոք դիտված է, թե ոչ
            if (localPrefs.getBoolean(videoId + "_watched", false)) {
                btnWatch.setText("WATCHED BEFORE");
                btnWatch.setIconResource(R.drawable.check); // Դնում ենք check իկոնկան
                btnWatch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8D6E63")));
            } else {
                btnWatch.setText("WATCH VIDEO");
                btnWatch.setIconResource(R.drawable.clapper); // Դնում ենք clapper իկոնկան
                btnWatch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5D4037")));
            }

            dbRef.child(videoId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        double sum = snapshot.child("sum").getValue(Double.class);
                        int count = snapshot.child("count").getValue(Integer.class);
                        float avg = (float) (sum / count);
                        tvAvg.setText("Avg. Rating: " + String.format("%.1f", avg));
                        rbAvg.setRating(avg);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            btnWatch.setOnClickListener(v -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video[1])));

                // Սեղմելուց հետո դառնում է դիտված
                localPrefs.edit().putBoolean(videoId + "_watched", true).apply();
                btnWatch.setText("WATCHED BEFORE");
                btnWatch.setIconResource(R.drawable.check); // Փոխվում է check-ի
                btnWatch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8D6E63")));

                showRatingDialog(video[0], videoId);
            });

            videoContainer.addView(card);
        }
    }

    private void showRatingDialog(String title, String videoId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_video, null);
        RatingBar rbUser = dialogView.findViewById(R.id.rbUserRating);

        builder.setView(dialogView)
                .setTitle("Rate: " + title)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = rbUser.getRating();
                    updateGlobalRating(videoId, rating);
                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }

    private void updateGlobalRating(String videoId, float userRating) {
        dbRef.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double sum = userRating;
                int count = 1;
                if (snapshot.exists()) {
                    sum += snapshot.child("sum").getValue(Double.class);
                    count += snapshot.child("count").getValue(Integer.class);
                }
                dbRef.child(videoId).child("sum").setValue(sum);
                dbRef.child(videoId).child("count").setValue(count);
                Toast.makeText(VideoListActivity.this, "Rating Shared!", Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}