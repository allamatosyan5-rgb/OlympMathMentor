package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

        // Գտնում ենք View-երը
        final TextView tvTitle = findViewById(R.id.tvResultTitle);
        final TextView tvMyName = findViewById(R.id.tvMyName);
        final TextView tvOppName = findViewById(R.id.tvOppName);
        final TextView tvMyScore = findViewById(R.id.tvMyScore);
        final TextView tvOppScore = findViewById(R.id.tvOppScore);
        final TextView tvStars = findViewById(R.id.tvEarnedStars);

        final LinearLayout layoutMyAvatar = findViewById(R.id.layoutMyAvatar);
        final LinearLayout layoutOppAvatar = findViewById(R.id.layoutOppAvatar);
        final LinearLayout layoutMainContent = findViewById(R.id.layoutMainContent);

        final LottieAnimationView lottieResult = findViewById(R.id.lottieResult);
        MaterialButton btnBack = findViewById(R.id.btnBackToMenu);

        // Ստանում ենք տվյալները Intent-ից
        int myScore = getIntent().getIntExtra("MY_SCORE", 0);
        int oppScore = getIntent().getIntExtra("OPP_SCORE", 0);
        String myName = getIntent().getStringExtra("MY_NAME");
        String oppName = getIntent().getStringExtra("OPP_NAME");
        int stars = getIntent().getIntExtra("STARS", 0);

        // Տեղադրում ենք տեքստերը
        tvMyName.setText(myName != null ? myName : "Me");
        tvOppName.setText(oppName != null ? oppName : "Opponent");
        tvMyScore.setText("Score: " + myScore);
        tvOppScore.setText("Score: " + oppScore);
        tvStars.setText("+" + stars + " Stars");

        // Սահմանում ենք հաղթանակի/պարտության վիճակը
        if (myScore > oppScore) {
            tvTitle.setText("YOU WON! 🏆");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            lottieResult.setAnimation(R.raw.trophy); // Սա սկզբնական 7 վրկ-ի համար է
        } else if (myScore < oppScore) {
            tvTitle.setText("YOU LOST! 😢");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            lottieResult.setAnimation(R.raw.invalid);
        } else {
            tvTitle.setText("IT'S A TIE! 🤝");
            lottieResult.setAnimation(R.raw.trophy);
        }

        lottieResult.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1. Թաքցնում ենք մեծ անիմացիան
                lottieResult.setVisibility(View.GONE);
                lottieResult.cancelAnimation();

                // 2. Երևացնում ենք հիմնական բովանդակությունը
                layoutMainContent.setVisibility(View.VISIBLE);

                // 3. Միացնում ենք ավատարների մեծացման/փոքրացման էֆեկտը
                if (myScore > oppScore) {
                    layoutMyAvatar.animate().scaleX(1.2f).scaleY(1.2f).setDuration(800).start();
                    layoutOppAvatar.animate().scaleX(0.8f).scaleY(0.8f).setDuration(800).start();
                } else if (myScore < oppScore) {
                    layoutMyAvatar.animate().scaleX(0.8f).scaleY(0.8f).setDuration(800).start();
                    layoutOppAvatar.animate().scaleX(1.2f).scaleY(1.2f).setDuration(800).start();
                }
            }
        }, 7000); // 7000 միլիվայրկյան = 7 վայրկյան

        // Վերադարձ մենյու
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(BattleResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}