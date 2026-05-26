package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdvResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_result);

        // Ստանում ենք միավորները քննության էջից
        int score = getIntent().getIntExtra("total_score", 0);
        int maxScore = getIntent().getIntExtra("max_score", 100);

        TextView tvFinalResult = findViewById(R.id.tvFinalResult);
        TextView tvScoreDetail = findViewById(R.id.tvScoreDetail);
        Button btnGetCertificate = findViewById(R.id.btnGetCertificate);
        Button btnBack = findViewById(R.id.btnBackToAdvanced);

        tvScoreDetail.setText("Your Score: " + score + " / " + maxScore);

        float percentage = ((float) score / maxScore) * 100;

        if (percentage >= 80) {
            tvFinalResult.setText("Congratulations! You passed!");
            tvFinalResult.setTextColor(Color.parseColor("#2E7D32"));

            // Ցույց ենք տալիս կոճակը
            btnGetCertificate.setVisibility(View.VISIBLE);

            // Պահպանում ենք արդյունքը MyPrefs-ում, որպեսզի AdvancedAdapter-ը ճանաչի
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("adv_final_score", (int)percentage)
                    .apply();
        } else {
            tvFinalResult.setText("Keep Trying! Need 80% to certify.");
            tvFinalResult.setTextColor(Color.parseColor("#D32F2F"));
            btnGetCertificate.setVisibility(View.GONE);
        }

        btnGetCertificate.setOnClickListener(v -> {
            startActivity(new Intent(this, CertificateActivity.class));
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}