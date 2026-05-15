package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCourses;
    private CourseAdapter adapter;
    private List<CourseModel> courseList;
    private TextView tvDailyInsight, tvMotivation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCourses = view.findViewById(R.id.rvCourses);
        tvDailyInsight = view.findViewById(R.id.tvDailyInsight);
        tvMotivation = view.findViewById(R.id.tvMotivation);
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        setDailyInsight();
        setupIntermediateButton(view);

        createCourseList();
        adapter = new CourseAdapter(getContext(), courseList);
        rvCourses.setAdapter(adapter);
        rvCourses.addItemDecoration(new PathDecoration());

        return view;
    }

    private void setupIntermediateButton(View view) {
        Button btnGoToIntermediate = view.findViewById(R.id.btnGoToIntermediate);

        if (btnGoToIntermediate != null && getContext() != null) {
            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences profilePrefs = getContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

            boolean isUnlocked = myPrefs.getBoolean("intermediate_unlocked", false);
            String currentLevel = profilePrefs.getString("level", "Beginner");

            if (currentLevel.equals("Intermediate") || currentLevel.equals("Advanced") || isUnlocked) {
                btnGoToIntermediate.setVisibility(View.VISIBLE);
                btnGoToIntermediate.setAlpha(1.0f);
                btnGoToIntermediate.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new IntermediateFragment())
                            .addToBackStack(null)
                            .commit();
                });
            } else {
                btnGoToIntermediate.setVisibility(View.VISIBLE);
                btnGoToIntermediate.setAlpha(0.4f);
                btnGoToIntermediate.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "🔒 Locked! Pass the Final Exam (35+) to unlock.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    // 💡 ԱՐՀԵՍՏԱԿԱՆ ԲԱՆԱԿԱՆՈՒԹՅԱՆ ԻՆՏԵԳՐՈՒՄԸ ГЛАВНЫЙ ԷՋՈՒՄ
    private void setDailyInsight() {
        if (getContext() == null) return;

        SharedPreferences prefs = requireContext().getSharedPreferences("DailyAIPrefs", Context.MODE_PRIVATE);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String savedDate = prefs.getString("saved_date", "");

        // Ստուգում ենք՝ արդյոք այսօր արդեն բեռնել ենք AI-ից տեքստ
        if (todayStr.equals(savedDate)) {
            // Եթե այո, ապա ցույց ենք տալիս հիշողության միջից (առանց ինտերնետ վատնելու)
            tvDailyInsight.setText(prefs.getString("daily_fact", "Mathematics is beautiful."));
            tvMotivation.setText(prefs.getString("daily_motivation", "Keep solving!"));
        } else {
            // Եթե նոր օր է, դնում ենք սպասման տեքստ և դիմում ենք AI-ին
            tvDailyInsight.setText("AI is thinking of a new math fact...");
            tvMotivation.setText("Finding motivation for you...");
            fetchDailyInsightFromAI(todayStr, prefs);
        }
    }

    private void fetchDailyInsightFromAI(String todayStr, SharedPreferences prefs) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONArray partsArray = new JSONArray();

            JSONObject textObj = new JSONObject();

            // 💡 ՓՈԽՎԱԾ ՀՐԱՀԱՆԳ. Խստորեն պահանջում ենք օգտագործել ՄԻԱՅՆ տառեր և թվեր
            String prompt = "Generate one fascinating, advanced Olympiad math fact, and one short motivational quote for a math student. " +
                    "CRITICAL INSTRUCTION: You must use ONLY plain letters and numbers. Do NOT use ANY mathematical symbols, equations, special characters, or formulas. " +
                    "Write everything out in plain words. " +
                    "Format exactly like this: 'Fact: [your fact] | Motivation: [your motivation]' without any bolding or extra text.";

            textObj.put("text", prompt);
            partsArray.put(textObj);
            part.put("parts", partsArray);
            contents.put(part);
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(url).post(body).build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showFallbackInsights(); // Ինտերնետ չկա
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String aiText = jsonResponse.getJSONArray("candidates")
                                    .getJSONObject(0).getJSONObject("content")
                                    .getJSONArray("parts").getJSONObject(0).getString("text");

                            String cleanText = aiText.replace("**", "").trim();

                            // Բաժանում ենք փաստն ու մոտիվացիան իրարից
                            if (cleanText.contains("|")) {
                                String[] parts = cleanText.split("\\|");
                                String fact = parts[0].replace("Fact:", "").trim();
                                String motivation = parts[1].replace("Motivation:", "").trim();

                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        tvDailyInsight.setText(fact);
                                        tvMotivation.setText(motivation);
                                        // Պահպանում ենք, որ այսօր էլ չզանգի API-ին
                                        prefs.edit()
                                                .putString("saved_date", todayStr)
                                                .putString("daily_fact", fact)
                                                .putString("daily_motivation", motivation)
                                                .apply();
                                    });
                                }
                            } else {
                                showFallbackInsights();
                            }
                        } catch (Exception e) {
                            showFallbackInsights(); // Սխալ ֆորմատ եկավ
                        }
                    } else {
                        showFallbackInsights(); // Սերվերի սխալ
                    }
                }
            });
        } catch (Exception e) {
            showFallbackInsights();
        }
    }

    // 💡 Ապահովության համար. Եթե ինտերնետ չկա կամ AI-ը խափանվել է
    private void showFallbackInsights() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            String[] insights = {
                    "Prime numbers are the atoms of mathematics. Everything is built from them.",
                    "Zero was invented in India and changed the world of math forever.",
                    "The Golden Ratio is found everywhere in nature, from shells to galaxies."
            };
            String[] motivations = {
                    "Success is a function of persistence. Keep solving!",
                    "Every complex problem is just a series of simple steps.",
                    "Don't fear mistakes; they are the path to discovery."
            };
            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            tvDailyInsight.setText(insights[dayOfYear % insights.length]);
            tvMotivation.setText(motivations[dayOfYear % motivations.length]);
        });
    }

    private void createCourseList() {
        courseList = new ArrayList<>();
        courseList.add(new CourseModel(1, "Lesson 1:\nNatural Numbers", false, R.color.accent_tan));
        courseList.add(new CourseModel(2, "Lesson 2:\nDivision with Remainder", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(3, "Lesson 3:\nPositional Notation", false, R.color.pastel_pink));
        courseList.add(new CourseModel(4, "Lesson 4:\nAppending Digits", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(5, "Lesson 5:\nDivisibility Rules", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(6, "Lesson 6:\nMotion Problems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(7, "Lesson 7:\nWork & Time Problems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(8, "Lesson 8:\nMixtures & Solutions", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(9, "Lesson 9:\nBoats & Streams", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(10, "Lesson 10:\nJoint Work & Time", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(11, "Lesson 11:\nParity", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(12, "Lesson 12:\nDirichlet's Principle", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(13, "Lesson 13:\nGCD & LCM", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(14, "Lesson 14:\nAdvanced GCD & LCM", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(15, "Lesson 15:\nPrimes & Composites", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(16, "Lesson 16:\nTriangle Foundations", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(17, "Lesson 17:\nCenters & Circles", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(18, "Lesson 18:\nAreas & Adv. Theorems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(19, "Lesson 19:\nPolygons & Ptolemy", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(20, "Lesson 20:\nAdvanced Circles", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(21, "Final Exam for\nBeginner Level", false, R.color.course_circle_grey));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rvCourses != null && getView() != null) {
            setupIntermediateButton(getView());

            while (rvCourses.getItemDecorationCount() > 0) {
                rvCourses.removeItemDecorationAt(0);
            }

            createCourseList();
            adapter = new CourseAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
            rvCourses.addItemDecoration(new PathDecoration());
        }
    }
}