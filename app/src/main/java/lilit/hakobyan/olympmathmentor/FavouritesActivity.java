package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    private LinearLayout favContainer;
    private Button btnCreateTest;
    private boolean isTestMode = false;

    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<String> tempQuestions = new ArrayList<>();
    private List<String> tempAnswers = new ArrayList<>();

    private CheckBox cbSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        favContainer = findViewById(R.id.favContainer);
        btnCreateTest = findViewById(R.id.btnCreateTest);

        loadFavourites();

        if (btnCreateTest != null) {
            btnCreateTest.setOnClickListener(v -> {
                if (!isTestMode) {
                    isTestMode = true;
                    btnCreateTest.setText("Start Custom Test");
                    if (cbSelectAll != null) cbSelectAll.setVisibility(View.VISIBLE);
                    for (CheckBox cb : checkBoxes) {
                        cb.setVisibility(View.VISIBLE);
                    }
                } else {
                    startCustomTest();
                }
            });
        }
    }

    private void loadFavourites() {
        favContainer.removeAllViews();
        checkBoxes.clear();
        tempQuestions.clear();
        tempAnswers.clear();

        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favs = prefs.getString("favourite_problems", "");

        if (favs.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No favourite problems yet.");
            tvEmpty.setTextSize(18f);
            tvEmpty.setPadding(20, 20, 20, 20);
            tvEmpty.setTextColor(Color.parseColor("#3E2723"));
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            favContainer.addView(tvEmpty);

            if (btnCreateTest != null) btnCreateTest.setVisibility(View.GONE);
            return;
        }

        if (btnCreateTest != null) btnCreateTest.setVisibility(View.VISIBLE);

        // Select All կոճակ
        cbSelectAll = new CheckBox(this);
        cbSelectAll.setText("☑️ Select All Questions");
        cbSelectAll.setTextSize(16f);
        cbSelectAll.setTypeface(null, android.graphics.Typeface.BOLD);
        cbSelectAll.setTextColor(Color.parseColor("#E91E63")); // Վարդագույն երանգ
        cbSelectAll.setPadding(0, 0, 0, 20);
        cbSelectAll.setVisibility(isTestMode ? View.VISIBLE : View.GONE);
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : checkBoxes) {
                cb.setChecked(isChecked);
            }
        });
        favContainer.addView(cbSelectAll);

        String[] items = favs.split("###");

        for (String item : items) {
            if (item.trim().isEmpty()) continue;

            String qText = item;
            String aText = "N/A";

            if (item.contains(" \nCorrect Answer: ")) {
                String[] parts = item.split(" \nCorrect Answer: ");
                qText = parts[0];
                aText = parts.length > 1 ? parts[1] : "N/A";
            }

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(20, 20, 20, 30);

            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

            CheckBox cb = new CheckBox(this);
            cb.setVisibility(isTestMode ? View.VISIBLE : View.GONE);
            checkBoxes.add(cb);
            tempQuestions.add(qText);
            tempAnswers.add(aText);

            TextView tvQuestion = new TextView(this);
            tvQuestion.setText("❤️ " + qText);
            tvQuestion.setTextColor(Color.BLACK);
            tvQuestion.setTextSize(16f);
            tvQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvDelete = new TextView(this);
            tvDelete.setText("🗑️");
            tvDelete.setTextSize(24f);
            tvDelete.setPadding(20, 0, 0, 0);
            tvDelete.setOnClickListener(v -> deleteFavourite(item));

            headerLayout.addView(cb);
            headerLayout.addView(tvQuestion);
            headerLayout.addView(tvDelete);

            itemLayout.addView(headerLayout);

            if (!aText.equals("N/A")) {
                TextView tvSeeAnswer = new TextView(this);
                tvSeeAnswer.setText("👀 See Answer");
                tvSeeAnswer.setTextColor(Color.BLUE);
                tvSeeAnswer.setPadding(0, 15, 0, 5);
                tvSeeAnswer.setTextSize(14f);

                TextView tvAnswer = new TextView(this);
                tvAnswer.setText("Correct Answer: " + aText);
                tvAnswer.setTextColor(Color.parseColor("#2E7D32"));
                tvAnswer.setVisibility(View.GONE);
                tvAnswer.setPadding(0, 10, 0, 0);
                tvAnswer.setTextSize(16f);
                tvAnswer.setTypeface(null, android.graphics.Typeface.BOLD);

                tvSeeAnswer.setOnClickListener(v -> {
                    if (tvAnswer.getVisibility() == View.GONE) {
                        tvAnswer.setVisibility(View.VISIBLE);
                        tvSeeAnswer.setText("🙈 Hide Answer");
                    } else {
                        tvAnswer.setVisibility(View.GONE);
                        tvSeeAnswer.setText("👀 See Answer");
                    }
                });

                itemLayout.addView(tvSeeAnswer);
                itemLayout.addView(tvAnswer);
            }

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
            divider.setBackgroundColor(Color.LTGRAY);

            favContainer.addView(itemLayout);
            favContainer.addView(divider);
        }
    }

    private void deleteFavourite(String fullItem) {
        SharedPreferences prefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String favs = prefs.getString("favourite_problems", "");

        favs = favs.replace(fullItem + "###", "");
        prefs.edit().putString("favourite_problems", favs).apply();

        Toast.makeText(this, "Removed from Favourites!", Toast.LENGTH_SHORT).show();
        loadFavourites();
    }

    private void startCustomTest() {
        ArrayList<String> selectedQs = new ArrayList<>();
        ArrayList<String> selectedAs = new ArrayList<>();

        boolean hasOldQuestions = false;

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                if (tempAnswers.get(i).equals("N/A")) {
                    hasOldQuestions = true;
                } else {
                    selectedQs.add(tempQuestions.get(i));
                    selectedAs.add(tempAnswers.get(i));
                }
            }
        }

        if (hasOldQuestions) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Oops! ⚠️")
                    .setMessage("Some of the questions you selected are from the old version and don't have saved answers.\n\nPlease delete them (🗑️) and re-add them to your Favourites from the test pages!")
                    .setPositiveButton("Got it", null)
                    .show();
            return;
        }

        if (selectedQs.isEmpty()) {
            Toast.makeText(this, "Please select at least one valid question!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ժամանակի հարցումը
        new AlertDialog.Builder(this)
                .setTitle("⏱️ Timer Option")
                .setMessage("Do you want to take this test with a timer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setHint("Minutes (e.g., 10)");

                    LinearLayout layout = new LinearLayout(this);
                    layout.setPadding(50, 20, 50, 20);
                    layout.addView(input);

                    new AlertDialog.Builder(this)
                            .setTitle("Set Time Limit")
                            .setView(layout)
                            .setPositiveButton("Start", (d, w) -> {
                                String minStr = input.getText().toString();
                                int mins = minStr.isEmpty() ? 0 : Integer.parseInt(minStr);
                                launchTest(selectedQs, selectedAs, mins);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    launchTest(selectedQs, selectedAs, 0);
                })
                .show();
    }

    private void launchTest(ArrayList<String> qs, ArrayList<String> as, int timeLimit) {
        Intent intent = new Intent(this, CustomTestActivity.class);
        intent.putStringArrayListExtra("questions", qs);
        intent.putStringArrayListExtra("answers", as);
        intent.putExtra("time_limit", timeLimit);
        startActivity(intent);

        isTestMode = false;
        if (btnCreateTest != null) btnCreateTest.setText("Create Custom Test");
        loadFavourites();
    }
}