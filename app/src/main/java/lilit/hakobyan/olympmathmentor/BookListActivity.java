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

        // Ստանում ենք նախորդ էջից եկած կատեգորիայի անունը
        String category = getIntent().getStringExtra("CATEGORY_NAME");
        tvCategoryTitle.setText(category + " Books");

        String[] links = new String[0];

        // Ըստ կատեգորիայի որոշում ենք, թե որ լինքերը ցույց տանք
        if (category.equals("Algebra")) {
            links = new String[]{
                    "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/inequalities-a-mathematical-olympiad-approach.pdf",
                    "https://www.imath.kiev.ua/~boyko/MAN-2019-2020/Literatura/Efthimiou%20C.,%20Introduction%20to%20functional%20equations,.pdf",
                    "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/101-problems-in-algebra.pdf",
                    "https://poincare.matf.bg.ac.rs/~zarkom/Polynomials_EJBarbeau.pdf",
                    "https://sotseurm.wordpress.com/wp-content/uploads/2012/08/pham-kim-hung-secrets-in-inequalities-volume-1.pdf",
                    "https://vkalessis.sites.sch.gr/wp-content/uploads/2020/12/Radmila-Bulajich-Manfrino-Jose%CC%81-Antonio-Go%CC%81mez-Ortega-Rogelio-Valdez-Delgado-Topics-in-Algebra-and-Analysis_-Preparing-for-the-Mathematical-Olympiad-2015-Birkha%CC%88user-libgen.lc_.pdf",
                    "https://www.ndl.ethernet.edu.et/bitstream/123456789/25248/1/Teodora-Liliana.pdf",
                    "https://www.academia.edu/42049506/Problem_Books_in_Mathematics_Algebraic_Inequalities",
                    "https://cut-the-knot.org/arithmetic/algebra/VasileCirtoaje.pdf"
                    // Նշում: Քո 10-րդ լինքը (file:///Users...) համակարգչի լոկալ ֆայլ է և հեռախոսում չի բացվի, դրա համար հանել եմ
            };
        } else if (category.equals("Geometry")) {
            links = new String[]{
                    "https://library.tsilikin.ru/%D0%95%D1%81%D1%82%D0%B5%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5%20%D0%BD%D0%B0%D1%83%D0%BA%D0%B8/%D0%9C%D0%B0%D1%82%D0%B5%D0%BC%D0%B0%D1%82%D0%B8%D0%BA%D0%B0/Chen%20Evan%20Euclidean%20Geometry%20In%20Mathematical%20Olympiads%202016.pdf",
                    "https://www.aproged.pt/biblioteca/geometryrevisited_coxetergreitzer.pdf",
                    "https://blngcc.wordpress.com/wp-content/uploads/2008/11/viktor-prasolov-problems-in-plane-and-solid-geometry.pdf",
                    "https://nzdr.ru/data/media/biblio/kolxoz/M/MSch/Andreescu%20T.,%20Mushkarov,%20Stoyanov.%20Geometric%20problems%20on%20maxima%20and%20minima%20(Birkhauser,%202006)(ISBN%200817635173)(272s)_MSch_.pdf",
                    "https://dn790000.ca.archive.org/0/items/complex-numbers-in-geometry/Complex-Numbers-in-Geometry-.pdf",
                    "https://archive.org/details/i.-m.-yaglom-geometric-transformations-1-1962",
                    "https://www.cimat.mx/~gil/docencia/2021/geometria2021/[Coxeter]Introduction%20to%20Geometry,2ndEd%281969%29.pdf"
            };
        } else if (category.equals("Number Theory")) {
            links = new String[]{
                    "https://blngcc.wordpress.com/wp-content/uploads/2008/11/andreescu-andrica-problems-on-number-theory.pdf",
                    "https://api.pageplace.de/preview/DT0400.9781292055411_A24570023/preview-9781292055411_A24570023.pdf",
                    "https://kvmwai.edu.in/upload/StudyMaterial/David_Burton_-_Elementary_number_theory-McGraw-Hill_Higher_Education_(2005).pdf",
                    "https://blngcc.wordpress.com/wp-content/uploads/2008/11/hardy-wright-theory_of_numbers.pdf"
            };
        } else if (category.equals("Combinatorics")) {
            links = new String[]{
                    "https://web.evanchen.cc/handouts/IntroComb/IntroComb.pdf",
                    "https://olympiads.mff.cuni.cz/prak/prak0809/comb_problems.pdf"
            };
        }

        // Ստեղծում ենք կոճակները ավտոմատ
        for (int i = 0; i < links.length; i++) {
            Button bookBtn = new Button(this);
            bookBtn.setText("📖 Book " + (i + 1));
            bookBtn.setBackgroundTintList(getResources().getColorStateList(R.color.deep_brown));
            bookBtn.setTextColor(getResources().getColor(android.R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    150 // Կոճակի բարձրությունը
            );
            params.setMargins(0, 0, 0, 20); // Հեռավորությունը իրարից
            bookBtn.setLayoutParams(params);

            String url = links[i];
            bookBtn.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show();
                }
            });

            booksContainer.addView(bookBtn);
        }
    }
}