package lilit.hakobyan.olympmathmentor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OlympiadSubmitActivity extends AppCompatActivity {

    private String olympiadName = "";
    private String olympiadYear = "";
    private Bitmap selectedImageBitmap = null;

    private EditText etProblemNumber, etSolutionText;
    private ImageView ivSolutionImage;
    private TextView tvAiFeedback;
    private LottieAnimationView lottieGrading;
    private MaterialButton btnSubmitToAI;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ivSolutionImage.setImageBitmap(selectedImageBitmap);
                        ivSolutionImage.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olympiad_submit);

        olympiadName = getIntent().getStringExtra("OLYMPIAD_NAME");
        olympiadYear = getIntent().getStringExtra("OLYMPIAD_YEAR");

        TextView tvSubmitTitle = findViewById(R.id.tvSubmitTitle);
        tvSubmitTitle.setText(olympiadName + " " + olympiadYear);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        MaterialButton btnSeeProblems = findViewById(R.id.btnSeeProblems);
        btnSeeProblems.setText("SEE PROBLEMS & SOLUTIONS");
        btnSeeProblems.setOnClickListener(v -> {
            String url = "";

            if (olympiadName != null && olympiadName.equals("EGMO")) {
                try {
                    int year = Integer.parseInt(olympiadYear);
                    int egmoNumber = year - 2011;
                    if (egmoNumber > 0) {
                        url = "https://www.egmo.org/egmos/egmo" + egmoNumber + "/";
                    } else {
                        url = "https://www.egmo.org/";
                    }
                } catch (NumberFormatException e) {
                    url = "https://www.egmo.org/";
                }
            }
            else if (olympiadName != null && olympiadName.equals("IZHO")) {
                url = "https://izho.kz/contest/problems/";
            }
            else if (olympiadName != null && olympiadName.equals("CMO")) {
                switch (olympiadYear) {
                    case "2025": url = "https://cmo.adygmath.ru/ru/node/253"; break;
                    case "2024": url = "https://cmo.adygmath.ru/ru/node/242"; break;
                    case "2023": url = "https://cmo.adygmath.ru/ru/node/227"; break;
                    case "2022": url = "https://cmo.adygmath.ru/ru/node/211"; break;
                    case "2021": url = "https://cmo.adygmath.ru/ru/node/176"; break;
                    case "2020": url = "https://cmo.adygmath.ru/ru/node/144"; break;
                    case "2019": url = "https://cmo.adygmath.ru/ru/node/95"; break;
                    case "2018": url = "https://cmo.adygmath.ru/node/51"; break;
                    case "2017": url = "https://cmo.adygmath.ru/node/21"; break;
                    case "2015": url = "https://cmo.adygmath.ru/node/34"; break;
                    default: url = "https://cmo.adygmath.ru/"; break;
                }
            }
            else if (olympiadName != null && olympiadName.equals("BMO")) {
                try {
                    int year = Integer.parseInt(olympiadYear);
                    if (year >= 1984 && year <= 2009) {
                        url = "https://imomath.com/othercomp/B/Bmo" + year + ".pdf";
                    } else {
                        switch (year) {
                            case 2010: url = "https://www.imo-register.org.uk/2010-balkan-report.pdf"; break;
                            case 2011: url = "https://imomath.com/srb/zadaci/2011_bmo_e.pdf"; break;
                            case 2012: url = "https://bmo2012.tubitak.gov.tr/sites/default/files/bmo2012solutions.pdf"; break;
                            case 2013: url = "https://www.scribd.com/document/486849698/2013-pdf"; break;
                            case 2014: url = "https://rg.edu.rs/wp-content/uploads/2021/04/zadaci_bmo_2014.pdf"; break;
                            case 2015: url = "https://www.hms.gr/32bmo2015/sols.pdf"; break;
                            case 2016: url = "https://www.scribd.com/document/416182434/BMO-2016-Problems-and-Solutions"; break;
                            case 2017: url = "https://artofproblemsolving.com/downloads/printable_post_collections/914510.pdf?srsltid=AfmBOorNZ2kWhBhLKqRE936bkUtDPUdY0ilYg2QJJMEq1YFahejxsJ-X"; break;
                            case 2018: url = "https://bmo2018.dms.rs/wp-content/uploads/2018/05/Solutions.pdf"; break;
                            case 2019: url = "https://www.scribd.com/document/411417727/Problems"; break;
                            case 2020: url = "https://bmo2020.ssmr.ro/problems"; break;
                            case 2021: url = "https://www.scribd.com/document/596816041/2021-BMO-Shortlist"; break;
                            case 2022: url = "https://www.scribd.com/document/670082886/3039147"; break;
                            case 2023: url = "https://www.scribd.com/document/670082884/3345386"; break;
                            case 2024: url = "https://bmo2024.org/problems/"; break;
                            case 2025: url = "https://bmo2025.pmf.unsa.ba/wp-content/uploads/2025/05/Problems%20-%20Solutions.pdf"; break;
                            default: url = "https://artofproblemsolving.com/wiki/index.php/" + olympiadYear + "_BMO_Problems"; break;
                        }
                    }
                } catch (NumberFormatException e) {
                    url = "https://artofproblemsolving.com/wiki/index.php/Balkan_Mathematical_Olympiad";
                }
            }
            else if (olympiadName != null && olympiadName.equals("IMO")) {
                url = "https://artofproblemsolving.com/wiki/index.php/" + olympiadYear + "_IMO";
            }
            else {
                url = "https://artofproblemsolving.com/search/community?q=" + olympiadName + "+" + olympiadYear;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        etProblemNumber = findViewById(R.id.etProblemNumber);
        etSolutionText = findViewById(R.id.etSolutionText);
        ivSolutionImage = findViewById(R.id.ivSolutionImage);
        tvAiFeedback = findViewById(R.id.tvAiFeedback);
        lottieGrading = findViewById(R.id.lottieGrading);
        btnSubmitToAI = findViewById(R.id.btnSubmitToAI);
        MaterialButton btnAttachImage = findViewById(R.id.btnAttachImage);

        btnAttachImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSubmitToAI.setOnClickListener(v -> gradeSolution());
    }

    private void gradeSolution() {
        String problemNum = etProblemNumber.getText().toString().trim();
        String solutionText = etSolutionText.getText().toString().trim();

        if (problemNum.isEmpty()) {
            Toast.makeText(this, "Please enter the problem number!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (solutionText.isEmpty() && selectedImageBitmap == null) {
            Toast.makeText(this, "Please provide a text solution or attach an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitToAI.setEnabled(false);
        lottieGrading.setVisibility(View.VISIBLE);
        tvAiFeedback.setVisibility(View.GONE);

        // Խստացված հրահանգ, որ միանշանակ ճիշտ ֆորմատով պատասխանի և վերևում գրի SCORE
        String aiPrompt = "You are a strict, expert Mathematics Olympiad Judge. " +
                "Evaluate this student's solution for the " + olympiadName + " " + olympiadYear + ", Problem Number " + problemNum + ". " +
                "Student's explanation: '" + solutionText + "'. " +
                "Analyze the logic rigorously. Tell them what is correct and what is flawed. " +
                "Finally, you MUST grade this strictly from 0 to 3 stars (0=completely wrong, 1=some valid ideas, 2=minor flaws, 3=perfect proof). " +
                "CRITICAL: The VERY FIRST LINE of your response MUST be exactly 'SCORE: X' (where X is a single digit 0, 1, 2, or 3). Do not add any extra words on this first line. Write your detailed feedback starting from the second line.";

        String apiKey = BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONArray partsArray = new JSONArray();

            JSONObject textObj = new JSONObject();
            textObj.put("text", aiPrompt);
            partsArray.put(textObj);

            if (selectedImageBitmap != null) {
                // ՆԿԱՐԻ ՓՈՔՐԱՑՈՒՄ ՈՐՊԵՍԶԻ TIMEOUT ՉՏԱ
                int maxDim = 800;
                int width = selectedImageBitmap.getWidth();
                int height = selectedImageBitmap.getHeight();
                if (width > maxDim || height > maxDim) {
                    float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                    width = Math.round((float) ratio * width);
                    height = Math.round((float) ratio * height);
                    selectedImageBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, width, height, false);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                JSONObject inlineDataObj = new JSONObject();
                JSONObject inlineData = new JSONObject();
                inlineData.put("mimeType", "image/jpeg");
                inlineData.put("data", base64Image);
                inlineDataObj.put("inlineData", inlineData);
                partsArray.put(inlineDataObj);
            }

            part.put("parts", partsArray);
            contents.put(part);

            JSONObject config = new JSONObject();
            config.put("temperature", 0.7);
            jsonBody.put("generationConfig", config);

            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // TIMEOUT ԿԱՐԳԱՎՈՐՈՒՄՆԵՐԸ (60 վայրկյան)
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (isDestroyed()) return;
                    runOnUiThread(() -> showError("Network Error:\n" + e.getMessage() + "\nCheck internet connection."));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (isDestroyed()) return;

                    final String responseData = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);

                            if (jsonResponse.has("candidates")) {
                                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                                if (candidates.length() > 0) {
                                    JSONObject firstCandidate = candidates.getJSONObject(0);

                                    if (firstCandidate.has("content")) {
                                        String aiText = firstCandidate.getJSONObject("content")
                                                .getJSONArray("parts").getJSONObject(0).getString("text");

                                        final String cleanText = aiText.replace("**", "").trim();

                                        if (!cleanText.isEmpty()) {
                                            runOnUiThread(() -> processAiFeedback(cleanText));
                                            return;
                                        }
                                    }
                                }
                            }
                            runOnUiThread(() -> showError("Sorry, I received a blank response."));

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> showError("Error reading Mentor response."));
                        }
                    } else {
                        final int statusCode = response.code();
                        runOnUiThread(() -> showError("Google API Error (" + statusCode + "):\nDetails: " + responseData));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unexpected error preparing request.");
        }
    }

    private void processAiFeedback(String aiResponse) {
        lottieGrading.setVisibility(View.GONE);
        tvAiFeedback.setVisibility(View.VISIBLE);
        btnSubmitToAI.setEnabled(true);

        int pointsEarned = 0;
        String cleanResponse = aiResponse;
        String upperResponse = cleanResponse.toUpperCase();

        // Խելացիացված կարդալու մեխանիզմ։ Գտնում է SCORE բառը ցանկացած տեղում:
        if (upperResponse.contains("SCORE:")) {
            try {
                int scoreIndex = upperResponse.indexOf("SCORE:");
                int endOfLine = upperResponse.indexOf('\n', scoreIndex);
                if (endOfLine == -1) endOfLine = upperResponse.length();

                String scoreLine = upperResponse.substring(scoreIndex, endOfLine);
                String scoreStr = scoreLine.replaceAll("[^0-9]", ""); // Մաքրում ենք բոլոր տառերը

                if (!scoreStr.isEmpty()) {
                    // Վերցնում ենք միայն ամենաառաջին թիվը
                    pointsEarned = Integer.parseInt(String.valueOf(scoreStr.charAt(0)));
                    if (pointsEarned > 3) pointsEarned = 3;
                }
            } catch (Exception ignored) {}
        }

        // Գումարում ենք պրոֆիլի աստղերին (MyPrefs)
        if (pointsEarned > 0) {
            SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            int currentStars = myPrefs.getInt("total_stars", 0);
            myPrefs.edit().putInt("total_stars", currentStars + pointsEarned).apply();

            Toast.makeText(this, "Excellent! You earned +" + pointsEarned + " Stars!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No stars this time. Read feedback and try again!", Toast.LENGTH_LONG).show();
        }

        tvAiFeedback.setText(cleanResponse);
    }

    private void showError(String msg) {
        lottieGrading.setVisibility(View.GONE);
        tvAiFeedback.setVisibility(View.VISIBLE);
        tvAiFeedback.setText(msg);
        btnSubmitToAI.setEnabled(true);
    }
}