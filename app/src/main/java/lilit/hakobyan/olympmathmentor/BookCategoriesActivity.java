package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BookCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categories);

        findViewById(R.id.btnAlgebra).setOnClickListener(v -> openBookList("Algebra"));
        findViewById(R.id.btnGeometry).setOnClickListener(v -> openBookList("Geometry"));
        findViewById(R.id.btnNumberTheory).setOnClickListener(v -> openBookList("Number Theory"));
        findViewById(R.id.btnCombinatorics).setOnClickListener(v -> openBookList("Combinatorics"));
    }

    private void openBookList(String categoryName) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.putExtra("CATEGORY_NAME", categoryName); // Փոխանցում ենք, թե որ բաժինն է ընտրել
        startActivity(intent);
    }
}