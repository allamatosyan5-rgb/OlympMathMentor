package lilit.hakobyan.olympmathmentor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private LinearLayout badgeContainer, containerAchievements, containerCurrentGoals, containerCompletedGoals;
    private TextView tvFullName, tvEmail, tvUserStatus;
    private TextView tvTotalStars, tvStreakDays, tvSolvedProblems, tvAccuracy, tvMistakesCount, tvFavsCount;
    private ImageView profileImage;
    private Button btnLogout, btnAddAchievement, btnAddGoal;
    private CardView cvMyMistakes, cvFavourites;

    private List<String> achievementsList = new ArrayList<>();
    private List<String> currentGoalsList = new ArrayList<>();
    private List<String> completedGoalsList = new ArrayList<>();

    private int totalEarnedStars = 0;
    private int currentStreakCount = 0;
    private final int TOTAL_LESSONS_IN_APP = 20;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}
                        profileImage.setImageURI(imageUri);
                        saveImageUriLocally(imageUri.toString());
                    }
                }
            }
    );

    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);
                    Uri tempUri = saveBitmapToLocalCache(bitmap);
                    if (tempUri != null) saveImageUriLocally(tempUri.toString());
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        badgeContainer = view.findViewById(R.id.badgeContainer);
        containerAchievements = view.findViewById(R.id.containerAchievements);
        containerCurrentGoals = view.findViewById(R.id.containerCurrentGoals);
        containerCompletedGoals = view.findViewById(R.id.containerCompletedGoals);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUserStatus = view.findViewById(R.id.tvUserStatus);
        profileImage = view.findViewById(R.id.profileImage);
        tvTotalStars = view.findViewById(R.id.tvTotalStars);
        tvStreakDays = view.findViewById(R.id.tvStreakDays);
        tvSolvedProblems = view.findViewById(R.id.tvSolvedProblems);
        tvAccuracy = view.findViewById(R.id.tvAccuracy);
        tvMistakesCount = view.findViewById(R.id.tvMistakesCount);
        tvFavsCount = view.findViewById(R.id.tvFavsCount);
        btnAddAchievement = view.findViewById(R.id.btnAddAchievement);
        btnAddGoal = view.findViewById(R.id.btnAddGoal);
        btnLogout = view.findViewById(R.id.btnLogout);
        cvMyMistakes = view.findViewById(R.id.cvMyMistakes);
        cvFavourites = view.findViewById(R.id.cvFavourites);

        if (cvMyMistakes != null) cvMyMistakes.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyMistakesActivity.class)));
        if (cvFavourites != null) cvFavourites.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavouritesActivity.class)));
        if (tvStreakDays != null) tvStreakDays.setOnClickListener(v -> showStreakCalendarDialog());

        profileImage.setOnClickListener(v -> showImageOptionsDialog());
        tvFullName.setOnClickListener(v -> showEditNameDialog());
        if (btnAddAchievement != null) btnAddAchievement.setOnClickListener(v -> showInputDialog("Add Achievement", true));
        if (btnAddGoal != null) btnAddGoal.setOnClickListener(v -> showInputDialog("Add Goal", false));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out? Your progress will be saved to the cloud.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (getContext() != null && currentUser != null) {
                                SharedPreferences profilePrefs = getContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                                SharedPreferences progressPrefs = getContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

                                HashMap<String, Object> backupData = new HashMap<>();

                                // Ամբողջ Պրոֆիլի և ՆԿԱՐԻ պահպանումը
                                HashMap<String, String> profileData = new HashMap<>();
                                profileData.put("name", profilePrefs.getString("name", ""));
                                profileData.put("surname", profilePrefs.getString("surname", ""));
                                profileData.put("profile_image_uri", profilePrefs.getString("profile_image_uri", ""));
                                profileData.put("achievements", profilePrefs.getString("achievements", ""));
                                profileData.put("currentGoals", profilePrefs.getString("currentGoals", ""));
                                profileData.put("completedGoals", profilePrefs.getString("completedGoals", ""));
                                backupData.put("profile", profileData);

                                // Ամբողջ Պրոգրեսի, Աստղերի, Սխալների և Ֆավորիտների պահպանումը
                                HashMap<String, Object> progressData = new HashMap<>();
                                for (int i = 1; i <= TOTAL_LESSONS_IN_APP; i++) {
                                    progressData.put("stars_lesson_" + i, progressPrefs.getInt("stars_lesson_" + i, 0));
                                }
                                progressData.put("extra_stars", progressPrefs.getInt("extra_stars", 0));
                                progressData.put("current_streak", progressPrefs.getInt("current_streak", 0));
                                progressData.put("last_login_day", progressPrefs.getLong("last_login_day", 0));
                                progressData.put("wrong_questions_list", progressPrefs.getString("wrong_questions_list", ""));
                                progressData.put("favourite_problems", progressPrefs.getString("favourite_problems", ""));
                                backupData.put("progress", progressData);

                                // Օրացույցի պահպանում
                                Set<String> history = progressPrefs.getStringSet("login_history", new HashSet<>());
                                backupData.put("login_history", new ArrayList<>(history));

                                Toast.makeText(getContext(), "Syncing data to Cloud...", Toast.LENGTH_SHORT).show();

                                // Ուղարկում ենք Firebase և հետո մաքրում հեռախոսը
                                FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("backup")
                                        .setValue(backupData).addOnCompleteListener(task -> {
                                            profilePrefs.edit().clear().apply();
                                            progressPrefs.edit().clear().apply();

                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            if (getActivity() != null) getActivity().finish();
                                        });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        loadAllLocalData();
        calculateStatsAndBadges();
        updateMistakesAndFavsCount();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        calculateStatsAndBadges();
        updateMistakesAndFavsCount();
    }

    private void updateMistakesAndFavsCount() {
        if (getContext() == null) return;
        SharedPreferences progressPrefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        String wrongList = progressPrefs.getString("wrong_questions_list", "");
        int mistakesCount = 0;
        if (!wrongList.isEmpty()) {
            mistakesCount = wrongList.split("###").length;
        }
        if (tvMistakesCount != null) {
            tvMistakesCount.setText("My Mistakes (" + mistakesCount + ")");
        }

        String favList = progressPrefs.getString("favourite_problems", "");
        int favsCount = 0;
        if (!favList.isEmpty()) {
            favsCount = favList.split("###").length;
        }
        if (tvFavsCount != null) {
            tvFavsCount.setText("Favourites (" + favsCount + ")");
        }
    }

    private void saveImageUriLocally(String uri) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        prefs.edit().putString("profile_image_uri", uri).apply();
    }

    private void saveNameLocally(String name, String surname) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        prefs.edit().putString("name", name).putString("surname", surname).apply();
    }

    private void saveListLocally(String key, List<String> list) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(";;;");
        }
        prefs.edit().putString(key, sb.toString()).apply();
    }

    private List<String> loadListLocally(String key) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String data = prefs.getString(key, "");
        List<String> list = new ArrayList<>();
        if (!data.isEmpty()) list.addAll(Arrays.asList(data.split(";;;")));
        return list;
    }

    private void loadAllLocalData() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        String name = prefs.getString("name", "");
        String surname = prefs.getString("surname", "");
        if (!name.isEmpty() && !surname.isEmpty()) tvFullName.setText(name + " " + surname);
        else tvFullName.setText("Full Name");

        String imageUrl = prefs.getString("profile_image_uri", "");
        if (!imageUrl.isEmpty()) {
            try {
                profileImage.setImageURI(Uri.parse(imageUrl));
            } catch (Exception e) {
                profileImage.setImageResource(android.R.drawable.ic_menu_camera);
            }
        } else {
            profileImage.setImageResource(android.R.drawable.ic_menu_camera);
        }

        if(containerAchievements != null) containerAchievements.removeAllViews();
        achievementsList = loadListLocally("achievements");
        for (String ach : achievementsList) addManualAchievementToView(ach);

        if(containerCurrentGoals != null) containerCurrentGoals.removeAllViews();
        if(containerCompletedGoals != null) containerCompletedGoals.removeAllViews();

        currentGoalsList = loadListLocally("currentGoals");
        for (String goal : currentGoalsList) addGoalToView(goal, false);

        completedGoalsList = loadListLocally("completedGoals");
        for (String goal : completedGoalsList) addGoalToView(goal, true);
    }

    private void calculateStatsAndBadges() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        totalEarnedStars = 0;
        int solvedCount = 0;

        for (int i = 1; i <= TOTAL_LESSONS_IN_APP; i++) {
            int lessonStars = prefs.getInt("stars_lesson_" + i, 0);
            if (lessonStars > 0) {
                totalEarnedStars += lessonStars;
                solvedCount++;
            }
        }

        totalEarnedStars += prefs.getInt("extra_stars", 0);
        tvTotalStars.setText(String.valueOf(totalEarnedStars));
        tvSolvedProblems.setText(String.valueOf(solvedCount));

        int completionPercentage = (int) (((float) solvedCount / TOTAL_LESSONS_IN_APP) * 100);
        tvAccuracy.setText(completionPercentage + "%");

        long currentTimeMillis = System.currentTimeMillis();
        long currentDay = currentTimeMillis / (1000 * 60 * 60 * 24);
        long lastLoginDay = prefs.getLong("last_login_day", 0);
        currentStreakCount = prefs.getInt("current_streak", 0);

        if (lastLoginDay == 0) {
            currentStreakCount = 1;
        } else if (currentDay == lastLoginDay + 1) {
            currentStreakCount++;
        } else if (currentDay > lastLoginDay + 1) {
            currentStreakCount = 1;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        Set<String> history = prefs.getStringSet("login_history", new HashSet<>());
        Set<String> updatedHistory = new HashSet<>(history);
        updatedHistory.add(todayStr);

        prefs.edit()
                .putLong("last_login_day", currentDay)
                .putInt("current_streak", currentStreakCount)
                .putStringSet("login_history", updatedHistory)
                .apply();

        tvStreakDays.setText(currentStreakCount + " Days");

        if (totalEarnedStars >= 50) tvUserStatus.setText("Math Olympian");
        else if (totalEarnedStars >= 25) tvUserStatus.setText("Advanced Thinker");
        else if (totalEarnedStars >= 10) tvUserStatus.setText("Intermediate Problem Solver");
        else tvUserStatus.setText("Beginner Explorer");

        generateBadges();
    }

    private void showStreakCalendarDialog() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        Set<String> loginHistory = prefs.getStringSet("login_history", new HashSet<>());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("Your Login History \uD83D\uDCC5");
        tvTitle.setTextSize(20f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(Color.parseColor("#3E2723"));
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setPadding(0, 0, 0, 30);
        mainLayout.addView(tvTitle);

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        TextView tvMonth = new TextView(getContext());
        tvMonth.setText(monthFormat.format(cal.getTime()));
        tvMonth.setTextSize(16f);
        tvMonth.setGravity(Gravity.CENTER);
        tvMonth.setPadding(0, 0, 0, 16);
        mainLayout.addView(tvMonth);

        LinearLayout daysHeaderRow = new LinearLayout(getContext());
        daysHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        daysHeaderRow.setWeightSum(7f);
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String d : days) {
            TextView tvDay = new TextView(getContext());
            tvDay.setText(d);
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
            tvDay.setTextColor(Color.GRAY);
            tvDay.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            daysHeaderRow.addView(tvDay);
        }
        mainLayout.addView(daysHeaderRow);

        int currentDay = 1;
        for (int i = 0; i < 6; i++) {
            LinearLayout weekRow = new LinearLayout(getContext());
            weekRow.setOrientation(LinearLayout.HORIZONTAL);
            weekRow.setWeightSum(7f);
            weekRow.setPadding(0, 8, 0, 8);

            for (int j = 0; j < 7; j++) {
                TextView dayView = new TextView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                params.setMargins(8, 8, 8, 8);
                dayView.setLayoutParams(params);
                dayView.setGravity(Gravity.CENTER);
                dayView.setPadding(0, 16, 0, 16);
                dayView.setTextSize(14f);

                if (i == 0 && j < startDayOfWeek) {
                    dayView.setText("");
                } else if (currentDay <= maxDays) {
                    dayView.setText(String.valueOf(currentDay));

                    String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", currentYear, currentMonth + 1, currentDay);

                    if (loginHistory.contains(dateStr)) {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setShape(GradientDrawable.OVAL);
                        gd.setColor(Color.parseColor("#FF9800"));
                        dayView.setBackground(gd);
                        dayView.setTextColor(Color.WHITE);
                        dayView.setTypeface(null, android.graphics.Typeface.BOLD);
                    } else {
                        dayView.setTextColor(Color.BLACK);
                    }
                    currentDay++;
                } else {
                    dayView.setText("");
                }
                weekRow.addView(dayView);
            }
            mainLayout.addView(weekRow);
            if (currentDay > maxDays) break;
        }

        builder.setView(mainLayout);
        builder.setPositiveButton("Awesome!", null);
        builder.show();
    }

    private void generateBadges() {
        if (badgeContainer == null) return;
        badgeContainer.removeAllViews();

        if (totalEarnedStars >= 1) createBadgeUI("\uD83C\uDF31", "Seed of Logic", true);
        else createBadgeUI("\uD83D\uDD12", "Seed of Logic", false);

        if (totalEarnedStars >= 3) createBadgeUI("\uD83D\uDD22", "Number Cruncher", true);
        else createBadgeUI("\uD83D\uDD12", "Number Cruncher", false);

        if (currentStreakCount >= 3) createBadgeUI("⚡", "Spark", true);
        else createBadgeUI("\uD83D\uDD12", "Spark", false);

        if (totalEarnedStars >= 6) createBadgeUI("\uD83D\uDCD0", "Geometry Novice", true);
        else createBadgeUI("\uD83D\uDD12", "Geometry Novice", false);

        if (currentStreakCount >= 7) createBadgeUI("\uD83D\uDD25", "7 Day Fire", true);
        else createBadgeUI("\uD83D\uDD12", "7 Day Fire", false);

        if (totalEarnedStars >= 12) createBadgeUI("\uD83E\uDDE9", "Puzzle Solver", true);
        else createBadgeUI("\uD83D\uDD12", "Puzzle Solver", false);

        if (currentStreakCount >= 14) createBadgeUI("\uD83D\uDDF3\uFE0F", "Consistency", true);
        else createBadgeUI("\uD83D\uDD12", "Consistency", false);

        if (totalEarnedStars >= 18) createBadgeUI("⚔\uFE0F", "Math Warrior", true);
        else createBadgeUI("\uD83D\uDD12", "Math Warrior", false);

        if (totalEarnedStars >= 25) createBadgeUI("➗", "Algebra Guru", true);
        else createBadgeUI("\uD83D\uDD12", "Algebra Guru", false);

        if (currentStreakCount >= 21) createBadgeUI("\uD83D\uDD52", "Habit Builder", true);
        else createBadgeUI("\uD83D\uDD12", "Habit Builder", false);

        if (totalEarnedStars >= 35) createBadgeUI("\uD83D\uDD2C", "Scientist", true);
        else createBadgeUI("\uD83D\uDD12", "Scientist", false);

        if (currentStreakCount >= 30) createBadgeUI("\uD83D\uDC8E", "Unstoppable", true);
        else createBadgeUI("\uD83D\uDD12", "Unstoppable", false);

        if (totalEarnedStars >= 45) createBadgeUI("\uD83E\uDDE0", "Brain Power", true);
        else createBadgeUI("\uD83D\uDD12", "Brain Power", false);

        if (totalEarnedStars >= 55) createBadgeUI("\uD83D\uDC51", "Math Royalty", true);
        else createBadgeUI("\uD83D\uDD12", "Math Royalty", false);

        if (totalEarnedStars == (TOTAL_LESSONS_IN_APP * 3)) createBadgeUI("\uD83C\uDFC6", "Olympian Legend", true);
        else createBadgeUI("\uD83D\uDD12", "Olympian Legend", false);
    }

    private void createBadgeUI(String emoji, String title, boolean isUnlocked) {
        LinearLayout badgeLayout = new LinearLayout(getContext());
        badgeLayout.setOrientation(LinearLayout.VERTICAL);
        badgeLayout.setGravity(Gravity.CENTER);
        badgeLayout.setPadding(30, 20, 30, 20);

        badgeLayout.setBackgroundResource(R.drawable.rounded_header_bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                180,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12, 0, 12, 0);
        badgeLayout.setLayoutParams(params);

        if (!isUnlocked) {
            badgeLayout.setAlpha(0.4f);
        }

        TextView tvEmoji = new TextView(getContext());
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(34);
        tvEmoji.setGravity(Gravity.CENTER);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextSize(11);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setMaxLines(2);

        badgeLayout.addView(tvEmoji);
        badgeLayout.addView(tvTitle);

        badgeContainer.addView(badgeLayout);
    }

    private void showImageOptionsDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Delete Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) cameraLauncher.launch(null);
            else if (which == 1) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            } else if (which == 2) {
                profileImage.setImageResource(android.R.drawable.ic_menu_camera);
                saveImageUriLocally("");
            }
        });
        builder.show();
    }

    private Uri saveBitmapToLocalCache(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File newFile = new File(cachePath, "profile_pic.png");
            FileOutputStream stream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return Uri.fromFile(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Profile Name");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        final EditText etName = new EditText(getContext());
        etName.setHint("First Name");
        layout.addView(etName);
        final EditText etSurname = new EditText(getContext());
        etSurname.setHint("Last Name");
        layout.addView(etSurname);
        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            if (!name.isEmpty() && !surname.isEmpty()) {
                tvFullName.setText(name + " " + surname);
                saveNameLocally(name, surname);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showInputDialog(String title, boolean isAchievement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        final EditText input = new EditText(getContext());
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                if (isAchievement) {
                    achievementsList.add(text);
                    addManualAchievementToView(text);
                    saveListLocally("achievements", achievementsList);
                } else {
                    currentGoalsList.add(text);
                    addGoalToView(text, false);
                    saveListLocally("currentGoals", currentGoalsList);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addManualAchievementToView(String text) {
        TextView newItem = new TextView(getContext());
        newItem.setText("🌟 " + text);
        newItem.setTextColor(Color.BLACK);
        newItem.setPadding(0, 15, 0, 15);
        newItem.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Delete Achievement?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    containerAchievements.removeView(newItem);
                    achievementsList.remove(text);
                    saveListLocally("achievements", achievementsList);
                }).setNegativeButton("Cancel", null).show());
        containerAchievements.addView(newItem);
    }

    private void addGoalToView(String text, boolean isCompleted) {
        TextView goalItem = new TextView(getContext());
        goalItem.setText(isCompleted ? "✅ " + text : "\uD83D\uDCCC " + text);
        goalItem.setTextColor(isCompleted ? Color.GRAY : Color.BLACK);
        goalItem.setPadding(0, 15, 0, 15);
        goalItem.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Goal Options");
            String[] options = isCompleted ? new String[]{"Delete"} : new String[]{"Mark Completed", "Delete"};
            builder.setItems(options, (dialog, which) -> {
                if (isCompleted) {
                    containerCompletedGoals.removeView(goalItem);
                    completedGoalsList.remove(text);
                    saveListLocally("completedGoals", completedGoalsList);
                } else {
                    if (which == 0) {
                        containerCurrentGoals.removeView(goalItem);
                        currentGoalsList.remove(text);
                        saveListLocally("currentGoals", currentGoalsList);

                        completedGoalsList.add(text);
                        addGoalToView(text, true);
                        saveListLocally("completedGoals", completedGoalsList);
                    } else if (which == 1) {
                        containerCurrentGoals.removeView(goalItem);
                        currentGoalsList.remove(text);
                        saveListLocally("currentGoals", currentGoalsList);
                    }
                }
            });
            builder.show();
        });
        if (isCompleted) containerCompletedGoals.addView(goalItem);
        else containerCurrentGoals.addView(goalItem);
    }
}