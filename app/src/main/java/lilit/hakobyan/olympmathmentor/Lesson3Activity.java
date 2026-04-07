package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Lesson3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Միացնում ենք քո պատրաստած անգլերեն XML դիզայնը
        setContentView(R.layout.activity_lesson3);

        // Գտնում ենք "TAKE TEST 3" կոճակը ըստ իր ID-ի
        Button btnGoToTest3 = findViewById(R.id.btnGoToTest3);

        // Սեղմելիս բացում ենք Test 3-ի էջը
        btnGoToTest3.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson3Activity.this, Test3Activity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Երբ օգտատերը սեղմում է "Back", նա վերադառնում է գլխավոր էջ (MainActivity)
        finish();
    }
}