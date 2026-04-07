package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Lesson2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson2);

        // Գտնում ենք "TAKE TEST 2" կոճակը
        Button btnGoToTest2 = findViewById(R.id.btnGoToTest2);

        // Սեղմելիս բացում ենք Թեստ 2-ի էջը
        btnGoToTest2.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson2Activity.this, Test2Activity.class);
            startActivity(intent);
        });
    }
}