package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

public class BattleResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_result);

        TextView tvTitle = findViewById(R.id.tvResultTitle);
        TextView tvMyName = findViewById(R.id.tvMyName);
        TextView tvOppName = findViewById(R.id.tvOppName);
        TextView tvMyScore = findViewById(R.id.tvMyScore);
        TextView tvOppScore = findViewById(R.id.tvOppScore);
        TextView tvStars = findViewById(R.id.tvEarnedStars);

        LinearLayout layoutMyAvatar = findViewById(R.id.layoutMyAvatar);
        LinearLayout layoutOppAvatar = findViewById(R.id.layoutOppAvatar);

        LottieAnimationView lottieResult = findViewById(R.id.lottieResult);
        MaterialButton btnBack = findViewById(R.id.btnBackToMenu);

        // Ստանում ենք ինֆորմացիան հին էջից
        int myScore = getIntent().getIntExtra("MY_SCORE", 0);
        int oppScore = getIntent().getIntExtra("OPP_SCORE", 0);
        String myName = getIntent().getStringExtra("MY_NAME");
        String oppName = getIntent().getStringExtra("OPP_NAME");
        int stars = getIntent().getIntExtra("STARS", 0);

        tvMyName.setText(myName != null ? myName : "Me");
        tvOppName.setText(oppName != null ? oppName : "Opponent");
        tvMyScore.setText("Score: " + myScore);
        tvOppScore.setText("Score: " + oppScore);
        tvStars.setText("+" + stars + " Stars");

        // ԱՆԻՄԱՑԻԱՆԵՐ ԵՎ ՄԵԾԱՑՈՒՄ/ՓՈՔՐԱՑՈՒՄ
        if (myScore > oppScore) {
            tvTitle.setText("YOU WON! 🏆");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            // Ես մեծանում եմ, նա փոքրանում է
            layoutMyAvatar.animate().scaleX(1.3f).scaleY(1.3f).setDuration(1000).start();
            layoutOppAvatar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(1000).start();

            lottieResult.setAnimation(R.raw.trophy);
        } else if (myScore < oppScore) {
            tvTitle.setText("YOU LOST! 😢");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            // Ես փոքրանում եմ, նա մեծանում է
            layoutMyAvatar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(1000).start();
            layoutOppAvatar.animate().scaleX(1.3f).scaleY(1.3f).setDuration(1000).start();

            lottieResult.setAnimation(R.raw.invalid);
        } else {
            tvTitle.setText("IT'S A TIE! 🤝");
            lottieResult.setAnimation(R.raw.trophy); // Կարող ես ոչ-ոքիի համար ուրիշ բան դնել
        }

        lottieResult.playAnimation();

        // Վերադարձ գլխավոր մենյու
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(BattleResultActivity.this, MainActivity.class));
            finish();
        });
    }
}