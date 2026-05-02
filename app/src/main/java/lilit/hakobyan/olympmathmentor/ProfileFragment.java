package lilit.hakobyan.olympmathmentor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout badgeContainer, containerAchievements, containerCurrentGoals, containerCompletedGoals;
    private TextView tvFullName, tvEmail, tvUserStatus;
    private TextView tvTotalStars, tvStreakDays, tvSolvedProblems, tvAccuracy;
    private ImageView profileImage;
    private Button btnLogout, btnAddAchievement, btnAddGoal;

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

        btnAddAchievement = view.findViewById(R.id.btnAddAchievement);
        btnAddGoal = view.findViewById(R.id.btnAddGoal);
        btnLogout = view.findViewById(R.id.btnLogout);

        view.findViewById(R.id.btnViewMistakes).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyMistakesActivity.class);
            startActivity(intent);
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
        }

        profileImage.setOnClickListener(v -> showImageOptionsDialog());
        tvFullName.setOnClickListener(v -> showEditNameDialog());

        if(btnAddAchievement != null) btnAddAchievement.setOnClickListener(v -> showInputDialog("Add Achievement", true));
        if(btnAddGoal != null) btnAddGoal.setOnClickListener(v -> showInputDialog("Add Goal", false));

        if(btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) getActivity().finish();
            });
        }

        loadAllLocalData();
        calculateStatsAndBadges();

        return view;
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

        String imageUrl = prefs.getString("profile_image_uri", "");
        if (!imageUrl.isEmpty()) {
            try {
                profileImage.setImageURI(Uri.parse(imageUrl));
            } catch (Exception e) {
                profileImage.setImageResource(android.R.drawable.ic_menu_camera);
            }
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

        prefs.edit().putLong("last_login_day", currentDay).putInt("current_streak", currentStreakCount).apply();
        tvStreakDays.setText(currentStreakCount + " Days");

        if (totalEarnedStars >= 50) tvUserStatus.setText("Math Olympian");
        else if (totalEarnedStars >= 25) tvUserStatus.setText("Advanced Thinker");
        else if (totalEarnedStars >= 10) tvUserStatus.setText("Intermediate Problem Solver");
        else tvUserStatus.setText("Beginner Explorer");

        generateBadges();
    }

    private void generateBadges() {
        if (badgeContainer == null) return;
        badgeContainer.removeAllViews();


        if (totalEarnedStars >= 1) createBadgeUI("🌱", "Seed of Logic", true);
        else createBadgeUI("🔒", "Seed of Logic", false);


        if (totalEarnedStars >= 3) createBadgeUI("🔢", "Number Cruncher", true);
        else createBadgeUI("🔒", "Number Cruncher", false);

        if (currentStreakCount >= 3) createBadgeUI("⚡", "Spark", true);
        else createBadgeUI("🔒", "Spark", false);

        if (totalEarnedStars >= 6) createBadgeUI("📐", "Geometry Novice", true);
        else createBadgeUI("🔒", "Geometry Novice", false);

        if (currentStreakCount >= 7) createBadgeUI("🔥", "7 Day Fire", true);
        else createBadgeUI("🔒", "7 Day Fire", false);

        if (totalEarnedStars >= 12) createBadgeUI("🧩", "Puzzle Solver", true);
        else createBadgeUI("🔒", "Puzzle Solver", false);

        if (currentStreakCount >= 14) createBadgeUI("🗓️", "Consistency", true);
        else createBadgeUI("🔒", "Consistency", false);

        if (totalEarnedStars >= 18) createBadgeUI("⚔️", "Math Warrior", true);
        else createBadgeUI("🔒", "Math Warrior", false);

        if (totalEarnedStars >= 25) createBadgeUI("➗", "Algebra Guru", true);
        else createBadgeUI("🔒", "Algebra Guru", false);

        if (currentStreakCount >= 21) createBadgeUI("🕒", "Habit Builder", true);
        else createBadgeUI("🔒", "Habit Builder", false);

        if (totalEarnedStars >= 35) createBadgeUI("🔬", "Scientist", true);
        else createBadgeUI("🔒", "Scientist", false);

        if (currentStreakCount >= 30) createBadgeUI("💎", "Unstoppable", true);
        else createBadgeUI("🔒", "Unstoppable", false);

        if (totalEarnedStars >= 45) createBadgeUI("🧠", "Brain Power", true);
        else createBadgeUI("🔒", "Brain Power", false);

        if (totalEarnedStars >= 55) createBadgeUI("👑", "Math Royalty", true);
        else createBadgeUI("🔒", "Math Royalty", false);

        if (totalEarnedStars == (TOTAL_LESSONS_IN_APP * 3)) createBadgeUI("🏆", "Olympian Legend", true);
        else createBadgeUI("🔒", "Olympian Legend", false);
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
        goalItem.setText(isCompleted ? "✅ " + text : "📌 " + text);
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