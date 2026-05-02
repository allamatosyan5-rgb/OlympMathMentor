package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IntLesson1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_lesson1);

        Button btnGoToTest = findViewById(R.id.btnGoToTest);
        btnGoToTest.setOnClickListener(v -> {
            Intent intent = new Intent(IntLesson1Activity.this, IntTest1Activity.class);
            startActivity(intent);
            finish();
        });
    }
}