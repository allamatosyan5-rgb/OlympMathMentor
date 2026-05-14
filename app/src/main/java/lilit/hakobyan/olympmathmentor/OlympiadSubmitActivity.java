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
                    url = (egmoNumber > 0) ? "https://www.egmo.org/egmos/egmo" + egmoNumber + "/" : "https://www.egmo.org/";
                } catch (Exception e) { url = "https://www.egmo.org/"; }
            } else if (olympiadName != null && olympiadName.equals("IZHO")) {
                url = "https://izho.kz/contest/problems/";
            } else if (olympiadName != null && olympiadName.equals("CMO")) {
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
            } else if (olympiadName != null && olympiadName.equals("BMO")) {
                try {
                    int year = Integer.parseInt(olympiadYear);
                    if (year >= 1984 && year <= 2009) url = "https://imomath.com/othercomp/B/Bmo" + year + ".pdf";
                    else {
                        switch (year) {
                            case 2010: url = "https://www.imo-register.org.uk/2010-balkan-report.pdf"; break;
                            case 2011: url = "https://imomath.com/srb/zadaci/2011_bmo_e.pdf"; break;
                            case 2024: url = "https://bmo2024.org/problems/"; break;
                            case 2025: url = "https://bmo2025.pmf.unsa.ba/wp-content/uploads/2025/05/Problems%20-%20Solutions.pdf"; break;
                            default: url = "https://artofproblemsolving.com/wiki/index.php/" + olympiadYear + "_BMO_Problems"; break;
                        }
                    }
                } catch (Exception e) { url = "https://artofproblemsolving.com/wiki/index.php/Balkan_Mathematical_Olympiad"; }
            } else if (olympiadName != null && olympiadName.equals("IMO")) {
                url = "https://artofproblemsolving.com/wiki/index.php/" + olympiadYear + "_IMO";
            } else {
                url = "https://artofproblemsolving.com/search/community?q=" + olympiadName + "+" + olympiadYear;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        etProblemNumber = findViewById(R.id.etProblemNumber);
        etSolutionText = findViewById(R.id.etSolutionText);
        ivSolutionImage = findViewById(R.id.ivSolutionImage);
        tvAiFeedback = findViewById(R.id.tvAiFeedback);
        lottieGrading = findViewById(R.id.lottieGrading);
        btnSubmitToAI = findViewById(R.id.btnSubmitToAI);
        findViewById(R.id.btnAttachImage).setOnClickListener(v -> imagePickerLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        btnSubmitToAI.setOnClickListener(v -> gradeSolution());
    }

    private void gradeSolution() {
        String problemNum = etProblemNumber.getText().toString().trim();
        String solutionText = etSolutionText.getText().toString().trim();

        if (problemNum.isEmpty()) { Toast.makeText(this, "Enter problem number!", Toast.LENGTH_SHORT).show(); return; }
        btnSubmitToAI.setEnabled(false);
        lottieGrading.setVisibility(View.VISIBLE);
        tvAiFeedback.setVisibility(View.GONE);

        String aiPrompt = "Judge this " + olympiadName + " " + olympiadYear + " problem " + problemNum + ". Solution: " + solutionText + ". Grade 0-3. Start with 'SCORE: X'.";
        String apiKey = "AIzaSyCne1J1x_bXT9kP2bj8h-SVdSMeYBHwMC8";
        // 💡 ՈՒՂՂՎԱԾ ՄՈԴԵԼԻ ԱՆՈՒՆԸ (1.5-flash)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONArray partsArray = new JSONArray();

            JSONObject textObj = new JSONObject();
            textObj.put("text", aiPrompt);
            partsArray.put(textObj);

            if (selectedImageBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
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
            jsonBody.put("contents", contents);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) { runOnUiThread(() -> showError("Network Error")); }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String aiText = new JSONObject(response.body().string()).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
                            runOnUiThread(() -> processAiFeedback(aiText));
                        } catch (Exception e) { runOnUiThread(() -> showError("Parse Error")); }
                    } else { runOnUiThread(() -> showError("API Error: " + response.code())); }
                }
            });
        } catch (Exception e) { showError("Error"); }
    }

    private void processAiFeedback(String aiResponse) {
        lottieGrading.setVisibility(View.GONE);
        tvAiFeedback.setVisibility(View.VISIBLE);
        btnSubmitToAI.setEnabled(true);
        int points = 0;
        if (aiResponse.toUpperCase().contains("SCORE:")) {
            try {
                String scorePart = aiResponse.substring(aiResponse.toUpperCase().indexOf("SCORE:") + 6).trim();
                points = Integer.parseInt(scorePart.substring(0, 1));
            } catch (Exception e) {}
        }
        if (points > 0) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putInt("total_stars", prefs.getInt("total_stars", 0) + points).apply();
            Toast.makeText(this, "Earned " + points + " stars!", Toast.LENGTH_SHORT).show();
        }
        tvAiFeedback.setText(aiResponse);
    }

    private void showError(String msg) {
        lottieGrading.setVisibility(View.GONE);
        tvAiFeedback.setVisibility(View.VISIBLE);
        tvAiFeedback.setText(msg);
        btnSubmitToAI.setEnabled(true);
    }
}