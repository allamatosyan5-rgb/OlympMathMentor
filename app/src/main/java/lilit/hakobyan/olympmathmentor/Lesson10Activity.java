package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Lesson10Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson10);

        Button btnGoToTest10 = findViewById(R.id.btnGoToTest10);
        btnGoToTest10.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson10Activity.this, Test10Activity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}