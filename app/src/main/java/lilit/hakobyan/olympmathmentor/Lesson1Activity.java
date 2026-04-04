package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Lesson1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson1);


        Button btnStartTest = findViewById(R.id.btnStartTest);


        btnStartTest.setOnClickListener(v -> {

            Intent intent = new Intent(Lesson1Activity.this, Test1Activity.class);
            startActivity(intent);
        });
    }
}