package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // Make sure your XML is named exactly this

        TextView tvFinalScore = findViewById(R.id.tvFinalScore);
        TextView tvLevelName = findViewById(R.id.tvLevelName);
        Button btnStartLearning = findViewById(R.id.btnStartLearning);

        // Receive the score from EntryTestActivity
        int score = getIntent().getIntExtra("SCORE", 0);
        String level = getIntent().getStringExtra("LEVEL");

        tvFinalScore.setText(String.valueOf(score));
        if (level != null) {
            tvLevelName.setText(level);
        }

        // Move to the Main Dashboard!
        btnStartLearning.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        });
    }
}