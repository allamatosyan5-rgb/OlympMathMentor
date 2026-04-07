package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookListActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private SharedPreferences localPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        LinearLayout booksContainer = findViewById(R.id.booksContainer);

        dbRef = FirebaseDatabase.getInstance().getReference("book_ratings");
        localPrefs = getSharedPreferences("DownloadedBooks", MODE_PRIVATE);

        String category = getIntent().getStringExtra("CATEGORY_NAME");
        tvCategoryTitle.setText(category + " Library");

        String[][] booksData = new String[0][3];

        if ("Algebra".equals(category)) {
            booksData = new String[][]{
                    {"Inequalities: A Mathematical Olympiad Approach", "Radmila Bulajich Manfrino", "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/inequalities-a-mathematical-olympiad-approach.pdf"},
                    {"Introduction to Functional Equations", "Costas Efthimiou", "https://www.imath.kiev.ua/~boyko/MAN-2019-2020/Literatura/Efthimiou%20C.,%20Introduction%20to%20functional%20equations,.pdf"},
                    {"101 Problems in Algebra", "Titu Andreescu & Zuming Feng", "https://mathematicalolympiads.wordpress.com/wp-content/uploads/2012/08/101-problems-in-algebra.pdf"},
                    {"Polynomials", "Edward J. Barbeau", "https://poincare.matf.bg.ac.rs/~zarkom/Polynomials_EJBarbeau.pdf"},
                    {"Secrets in Inequalities (Volume 1)", "Pham Kim Hung", "https://sotseurm.wordpress.com/wp-content/uploads/2012/08/pham-kim-hung-secrets-in-inequalities-volume-1.pdf"},
                    {"Topics in Algebra and Analysis", "Radmila Bulajich Manfrino", "https://vkalessis.sites.sch.gr/wp-content/uploads/2020/12/Radmila-Bulajich-Manfrino-Jose%CC%81-Antonio-Go%CC%81mez-Ortega-Rogelio-Valdez-Delgado-Topics-in-Algebra-and-Analysis_-Preparing-for-the-Mathematical-Olympiad-2015-Birkha%CC%88user-libgen.lc_.pdf"},
                    {"Problems in Real Analysis", "Teodora-Liliana Rădulescu", "https://www.ndl.ethernet.edu.et/bitstream/123456789/25248/1/Teodora-Liliana.pdf"},
                    {"Algebraic Inequalities (Problem Books)", "Hayk & Nairi Sedrakyan", "https://www.academia.edu/42049506/Problem_Books_in_Mathematics_Algebraic_Inequalities"},
                    {"Algebraic Inequalities", "Vasile Cîrtoaje", "https://cut-the-knot.org/arithmetic/algebra/VasileCirtoaje.pdf"},
                    {"Mathematics for High School Teachers", "Zalman Usiskin et al.", "file:///Users/user/Downloads/Mathematics-for-high-school-teachers-textbook-chapters-5-8.pdf"}
            };
        } else if ("Geometry".equals(category)) {
            booksData = new String[][]{
                    {"Euclidean Geometry In Mathematical Olympiads", "Evan Chen", "https://library.tsilikin.ru/%D0%95%D1%81%D1%82%D0%B5%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5%20%D0%BD%D0%B0%D1%83%D0%BA%D0%B8/%D0%9C%D0%B0%D1%82%D0%B5%D0%BC%D0%B0%D1%82%D0%B8%D0%BA%D0%B0/Chen%20Evan%20Euclidean%20Geometry%20In%20Mathematical%20Olympiads%202016.pdf"},
                    {"Geometry Revisited", "H.S.M. Coxeter & S.L. Greitzer", "https://www.aproged.pt/biblioteca/geometryrevisited_coxetergreitzer.pdf"},
                    {"Problems in Plane and Solid Geometry", "Viktor Prasolov", "https://blngcc.wordpress.com/wp-content/uploads/2008/11/viktor-prasolov-problems-in-plane-and-solid-geometry.pdf"},
                    {"Geometric Problems on Maxima and Minima", "Titu Andreescu et al.", "https://nzdr.ru/data/media/biblio/kolxoz/M/MSch/Andreescu%20T.,%20Mushkarov,%20Stoyanov.%20Geometric%20problems%20on%20maxima%20and%20minima%20(Birkhauser,%202006)(ISBN%200817635173)(272s)_MSch_.pdf"},
                    {"Complex Numbers in Geometry", "I. M. Yaglom", "https://dn790000.ca.archive.org/0/items/complex-numbers-in-geometry/Complex-Numbers-in-Geometry-.pdf"},
                    {"Geometric Transformations I", "I. M. Yaglom", "https://archive.org/details/i.-m.-yaglom-geometric-transformations-1-1962"},
                    {"Introduction to Geometry", "H.S.M. Coxeter", "https://www.cimat.mx/~gil/docencia/2021/geometria2021/[Coxeter]Introduction%20to%20Geometry,2ndEd%281969%29.pdf"},
                    {"Geometric Inequalities: Methods of Proving", "Hayk & Nairi Sedrakyan", "https://www.scribd.com/document/371957710/Problem-Books-in-Mathematics-Hayk-Sedrakyan-Nairi-Sedrakyan-Auth-Geometric-Inequalities-Methods-of-Proving-Springer-International-Publishing-2"}
            };
        } else if ("Number Theory".equals(category)) {
            booksData = new String[][]{
                    {"Problems on Number Theory", "Titu Andreescu & Dorin Andrica", "https://blngcc.wordpress.com/wp-content/uploads/2008/11/andreescu-andrica-problems-on-number-theory.pdf"},
                    {"Elementary Number Theory and Its Applications", "Kenneth H. Rosen", "https://api.pageplace.de/preview/DT0400.9781292055411_A24570023/preview-9781292055411_A24570023.pdf"},
                    {"Basic Number Theory", "AoPS / Justin Stevens", "https://services.artofproblemsolving.com/download.php?id=YXR0YWNobWVudHMvMi9iL2FmZmMwNjRjOWMwOTQwMWI5NGM5OWExNjUzOThkYzI0MDBiYzI0LnBkZg==&rn=YmFzaWNOdC5wZGY="},
                    {"Number Theory Step-by-Step", "Olympiad Collection", "https://www.scribd.com/document/758083840/00-Number-Theory-Step-by-Step"},
                    {"Problems in Elementary Number Theory", "Hojoo Lee", "https://igor-kortchemski.perso.math.cnrs.fr/olympiades/Problemes/arithmetique/pen2005.pdf"},
                    {"Elementary Number Theory", "David M. Burton", "https://kvmwai.edu.in/upload/StudyMaterial/David_Burton_-_Elementary_number_theory-McGraw-Hill_Higher_Education_(2005).pdf"},
                    {"An Introduction to the Theory of Numbers", "G.H. Hardy & E.M. Wright", "https://blngcc.wordpress.com/wp-content/uploads/2008/11/hardy-wright-theory_of_numbers.pdf"}
            };
        } else if ("Combinatorics".equals(category)) {
            booksData = new String[][]{
                    {"Mathematical Olympiad Treasures", "Titu Andreescu & Bogdan Enescu", "https://artofmaths.wordpress.com/wp-content/uploads/2014/06/mathematical-olympiad-treasures-2ed-springer-2011.pdf"},
                    {"Combinatorics Reference", "Springer", "https://api.pageplace.de/preview/DT0400.9781420099836_A36331567/preview-9781420099836_A36331567.pdf"},
                    {"Introductory Combinatorics", "Richard A. Brualdi", "https://liutianren.com/discrete/ref/Combinatorics/Introductory%20Combinatorics.pdf"},
                    {"Combinatorics and Graph Theory", "Harris, Hirst & Mossinghoff", "https://data.fmipa.unand.ac.id/matematika/file_bahankuliah/ebooksclub.org__Combinatorics_and_Graph_Theory____2nd_Edition__Undergraduate_Texts_in_Mathematics_.pdf"},
                    {"generatingfunctionology", "Herbert S. Wilf", "https://www2.math.upenn.edu/~wilf/gfology2.pdf"},
                    {"Combinatorial Problems and Exercises", "László Lovász", "https://ia801900.us.archive.org/28/items/in.ernet.dli.2015.141538/2015.141538.Combinatorial-Problems-And-Exercises.pdf"},
                    {"Problems from the Book", "Titu Andreescu & Gabriel Dospinescu", "https://khmerknowledges.wordpress.com/wp-content/uploads/2012/04/problems-from-the-book-titu-andreescugabriel-donpinescu.pdf"}
            };
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String[] book : booksData) {
            View bookCard = inflater.inflate(R.layout.item_book, booksContainer, false);

            TextView tvTitle = bookCard.findViewById(R.id.tvBookTitle);
            TextView tvAuthor = bookCard.findViewById(R.id.tvBookAuthor);
            TextView tvRatingValue = bookCard.findViewById(R.id.tvRatingValue);
            RatingBar rbBookRating = bookCard.findViewById(R.id.rbBookRating);
            Button btnRead = bookCard.findViewById(R.id.btnReadBook);

            tvTitle.setText(book[0]);
            tvAuthor.setText(book[1]);
            String url = book[2];

            // Ստեղծում ենք ապահով բանալի Firebase-ի համար (մաքրում ենք հատուկ նշանները)
            String bookId = book[0].replaceAll("[^a-zA-Z0-9]", "_");

            // 1. ՍՏՈՒԳԵԼ ԵՎ ԲԵՌՆԵԼ ԳԼՈԲԱԼ ԳՆԱՀԱՏԱԿԱՆԸ
            dbRef.child(bookId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        double sum = snapshot.child("sum").getValue(Double.class);
                        int count = snapshot.child("count").getValue(Integer.class);
                        float avg = (float) (sum / count);
                        tvRatingValue.setText(String.format("(%.1f)", avg));
                        rbBookRating.setRating(avg);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            if (url.isEmpty()) {
                btnRead.setEnabled(false);
                btnRead.setText("COMING SOON");
                btnRead.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
            } else {
                // 2. ՍՏՈՒԳԵԼ ԱՐԴՅՈՔ ԲԱՑՎԵԼ Է ԱՌԱՋ
                boolean isOpenedBefore = localPrefs.getBoolean(bookId + "_opened", false);

                if (isOpenedBefore) {
                    btnRead.setText("✅ OPENED BEFORE");
                    btnRead.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8D6E63")));
                } else {
                    btnRead.setText("📖 READ BOOK");
                    btnRead.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5D4037")));
                }

                btnRead.setOnClickListener(v -> {
                    if (url.startsWith("file://")) {
                        Toast.makeText(this, "Այս գիրքը պահված է քո Mac-ի մեջ: Այն հեռախոսից բացելու համար նախ վերբեռնիր ինտերնետ:", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Պահպանում ենք, որ արդեն բացել է
                    localPrefs.edit().putBoolean(bookId + "_opened", true).apply();
                    btnRead.setText("✅ OPENED BEFORE");
                    btnRead.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8D6E63")));

                    // Բացում ենք հղումը
                    openLink(url);

                    // Բացում ենք Գնահատման Պատուհանը (որն ունի Maybe Later կոճակ)
                    showRatingDialog(book[0], bookId);
                });
            }

            booksContainer.addView(bookCard);
        }
    }

    private void showRatingDialog(String title, String bookId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_book, null);
        RatingBar rbUser = dialogView.findViewById(R.id.rbUserRating);

        builder.setView(dialogView)
                .setTitle("Rate Book")
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = rbUser.getRating();
                    if(rating > 0) {
                        updateGlobalRating(bookId, rating);
                    }
                })
                .setNegativeButton("Maybe Later", null) // Թույլ է տալիս հետաձգել գնահատումը
                .show();
    }

    private void updateGlobalRating(String bookId, float userRating) {
        dbRef.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double sum = userRating;
                int count = 1;
                if (snapshot.exists()) {
                    sum += snapshot.child("sum").getValue(Double.class);
                    count += snapshot.child("count").getValue(Integer.class);
                }
                dbRef.child(bookId).child("sum").setValue(sum);
                dbRef.child(bookId).child("count").setValue(count);
                Toast.makeText(BookListActivity.this, "Rating Shared!", Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void openLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open link.", Toast.LENGTH_SHORT).show();
        }
    }
}