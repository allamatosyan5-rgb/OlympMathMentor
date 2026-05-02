package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MyMistakesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mistakes);

        LinearLayout container = findViewById(R.id.errorsContainer);
        Button btnClear = findViewById(R.id.btnClearErrors);

        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String allErrors = prefs.getString("wrong_questions_list", "");

        if (!allErrors.isEmpty()) {
            String[] errorArray = allErrors.split("###");
            for (String error : errorArray) {
                TextView tv = new TextView(this);
                tv.setText("❓ " + error);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundResource(R.drawable.rounded_header_bg); // Կամ քո ունեցած դիզայնը
                tv.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 10, 0, 10);
                tv.setLayoutParams(params);

                container.addView(tv);
            }
        } else {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No mistakes yet! Keep up the good work.");
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            container.addView(tvEmpty);
        }

        btnClear.setOnClickListener(v -> {
            prefs.edit().remove("wrong_questions_list").apply();
            container.removeAllViews();
            recreate();
        });
    }
}