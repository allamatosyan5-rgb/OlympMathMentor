package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        LinearLayout booksContainer = findViewById(R.id.booksContainer);

        String category = getIntent().getStringExtra("CATEGORY_NAME");
        tvCategoryTitle.setText(category + " Books");

        // 2D Զանգված՝ { "Վերնագիր", "Հեղինակ", "Լինք" } ձևաչափով
        String[][] booksData = new String[0][3];

        if (category.equals("Algebra")) {
            booksData = new String[][]{
                    {"Inequalities: A Math Olympiad Approach", "Radmila Bulajich", "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/inequalities-a-mathematical-olympiad-approach.pdf"},
                    {"Introduction to Functional Equations", "Costas Efthimiou", "https://www.imath.kiev.ua/~boyko/MAN-2019-2020/Literatura/Efthimiou%20C.,%20Introduction%20to%20functional%20equations,.pdf"},
                    {"101 Problems in Algebra", "Titu Andreescu", "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/101-problems-in-algebra.pdf"}
                    // Կարող ես ավելացնել մնացածը նույն տրամաբանությամբ...
            };
        } else if (category.equals("Geometry")) {
            booksData = new String[][]{
                    {"Euclidean Geometry In Math Olympiads", "Evan Chen", "https://library.tsilikin.ru/..."},
                    {"Geometry Revisited", "H. S. M. Coxeter", "https://www.aproged.pt/... "}
            };
        }
        // Ավելացրու Number Theory և Combinatorics բաժինները

        // Ստեղծում ենք կոճակները ավտոմատ
        for (int i = 0; i < booksData.length; i++) {
            Button bookBtn = new Button(this);
            // Տեքստը դառնում է՝ Վերնագիր \n Հեղինակ
            bookBtn.setText("📖 " + booksData[i][0] + "\n✍️ " + booksData[i][1]);
            bookBtn.setBackgroundTintList(getResources().getColorStateList(R.color.deep_brown));
            bookBtn.setTextColor(getResources().getColor(android.R.color.white));
            bookBtn.setAllCaps(false); // Որպեսզի տառերը մեծատառ չսարքի ավտոմատ

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200 // Բարձրությունը մի փոքր մեծացրինք երկու տողի համար
            );
            params.setMargins(0, 0, 0, 20);
            bookBtn.setLayoutParams(params);

            String url = booksData[i][2];
            bookBtn.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show();
                }
            });

            booksContainer.addView(bookBtn);
        }
    }
}