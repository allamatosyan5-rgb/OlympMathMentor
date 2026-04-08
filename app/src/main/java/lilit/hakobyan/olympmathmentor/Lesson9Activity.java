package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Lesson9Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson9);

        Button btnGoToTest9 = findViewById(R.id.btnGoToTest9);
        btnGoToTest9.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson9Activity.this, Test9Activity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}