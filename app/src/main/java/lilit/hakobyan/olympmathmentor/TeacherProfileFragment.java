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
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TeacherProfileFragment extends Fragment {

    private LinearLayout badgeContainer, containerAchievements, containerCurrentGoals, containerCompletedGoals;
    private TextView tvFullName, tvEmail;
    private TextView tvTotalClasses, tvStreakDays, tvTotalStudents, tvTotalHomeworks;
    private ImageView profileImage;
    private Button btnLogout, btnAddAchievement, btnAddGoal;

    private LinearLayout btnShowNews;
    private View viewNewsDot;
    private String currentAdminNews = "";

    private List<String> achievementsList = new ArrayList<>();
    private List<String> currentGoalsList = new ArrayList<>();
    private List<String> completedGoalsList = new ArrayList<>();

    private int totalClassesCount = 0;
    private int totalStudentsCount = 0;
    private int totalHomeworksCount = 0;
    private int currentStreakCount = 0;

    private String currentUserId;

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
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        badgeContainer = view.findViewById(R.id.badgeContainer);
        containerAchievements = view.findViewById(R.id.containerAchievements);
        containerCurrentGoals = view.findViewById(R.id.containerCurrentGoals);
        containerCompletedGoals = view.findViewById(R.id.containerCompletedGoals);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        profileImage = view.findViewById(R.id.profileImage);
        tvTotalClasses = view.findViewById(R.id.tvTotalClasses);
        tvStreakDays = view.findViewById(R.id.tvStreakDays);
        tvTotalStudents = view.findViewById(R.id.tvTotalStudents);
        tvTotalHomeworks = view.findViewById(R.id.tvTotalHomeworks);
        btnAddAchievement = view.findViewById(R.id.btnAddAchievement);
        btnAddGoal = view.findViewById(R.id.btnAddGoal);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnShowNews = view.findViewById(R.id.btnShowNews);
        viewNewsDot = view.findViewById(R.id.viewNewsDot);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            tvEmail.setText(currentUser.getEmail());
        }

        btnShowNews.setOnClickListener(v -> {
            if (!currentAdminNews.isEmpty()) {
                showNewsDialog(currentAdminNews);
                viewNewsDot.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "No news for now!", Toast.LENGTH_SHORT).show();
            }
        });

        if (tvStreakDays != null) tvStreakDays.setOnClickListener(v -> showStreakCalendarDialog());

        profileImage.setOnClickListener(v -> showImageOptionsDialog());
        tvFullName.setOnClickListener(v -> showEditNameDialog());
        if (btnAddAchievement != null) btnAddAchievement.setOnClickListener(v -> showInputDialog("Add Achievement", true));
        if (btnAddGoal != null) btnAddGoal.setOnClickListener(v -> showInputDialog("Add Goal", false));

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (getContext() != null) {
                                requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE).edit().clear().apply();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                if (getActivity() != null) getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        loadAllLocalData();
        calculateStreak();
        fetchTeacherStats();
        listenForAdminNews();

        return view;
    }

    private void fetchTeacherStats() {
        if (currentUserId == null) return;
        DatabaseReference classesRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes");

        classesRef.orderByChild("teacherId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int classes = 0;
                int students = 0;
                int homeworks = 0;

                for (DataSnapshot cSnap : snapshot.getChildren()) {
                    classes++;
                    if (cSnap.hasChild("students")) {
                        students += cSnap.child("students").getChildrenCount();
                    }
                    if (cSnap.hasChild("homeworks")) {
                        homeworks += cSnap.child("homeworks").getChildrenCount();
                    }
                }

                totalClassesCount = classes;
                totalStudentsCount = students;
                totalHomeworksCount = homeworks;

                if (tvTotalClasses != null) tvTotalClasses.setText(String.valueOf(totalClassesCount));
                if (tvTotalStudents != null) tvTotalStudents.setText(String.valueOf(totalStudentsCount));
                if (tvTotalHomeworks != null) tvTotalHomeworks.setText(String.valueOf(totalHomeworksCount));

                generateBadges();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void generateBadges() {
        if (badgeContainer == null) return;
        badgeContainer.removeAllViews();

        if (totalClassesCount >= 1) createBadgeUI("\uD83C\uDFEB", "First Class", true);
        else createBadgeUI("\uD83D\uDD12", "First Class", false);

        if (totalStudentsCount >= 10) createBadgeUI("\uD83D\uDC68\u200D\uD83C\uDFEB", "Popular Mentor", true);
        else createBadgeUI("\uD83D\uDD12", "Popular Mentor", false);

        if (currentStreakCount >= 3) createBadgeUI("⚡", "Spark", true);
        else createBadgeUI("\uD83D\uDD12", "Spark", false);

        if (totalHomeworksCount >= 5) createBadgeUI("\uD83D\uDCDA", "Task Master", true);
        else createBadgeUI("\uD83D\uDD12", "Task Master", false);

        if (totalClassesCount >= 3) createBadgeUI("\uD83C\uDF1F", "Active Leader", true);
        else createBadgeUI("\uD83D\uDD12", "Active Leader", false);

        if (totalStudentsCount >= 50) createBadgeUI("\uD83C\uDFC6", "Grand Teacher", true);
        else createBadgeUI("\uD83D\uDD12", "Grand Teacher", false);
    }

    private void calculateStreak() {
        if (getContext() == null) return;
        SharedPreferences progressPrefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        long currentTimeMillis = System.currentTimeMillis();
        long currentDay = currentTimeMillis / (1000 * 60 * 60 * 24);
        long lastLoginDay = progressPrefs.getLong("last_login_day", 0);
        currentStreakCount = progressPrefs.getInt("current_streak", 0);

        if (lastLoginDay == 0 || currentDay > lastLoginDay + 1) {
            currentStreakCount = 1;
        } else if (currentDay == lastLoginDay + 1) {
            currentStreakCount++;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        Set<String> history = progressPrefs.getStringSet("login_history", new HashSet<>());
        Set<String> updatedHistory = new HashSet<>(history);
        updatedHistory.add(todayStr);

        progressPrefs.edit()
                .putLong("last_login_day", currentDay)
                .putInt("current_streak", currentStreakCount)
                .putStringSet("login_history", updatedHistory)
                .apply();

        if (tvStreakDays != null) tvStreakDays.setText(currentStreakCount + " Days");
    }

    private void createBadgeUI(String emoji, String title, boolean isUnlocked) {
        LinearLayout badgeLayout = new LinearLayout(getContext());
        badgeLayout.setOrientation(LinearLayout.VERTICAL);
        badgeLayout.setGravity(Gravity.CENTER);
        badgeLayout.setPadding(30, 20, 30, 20);

        badgeLayout.setBackgroundResource(R.drawable.rounded_header_bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(12, 0, 12, 0);
        badgeLayout.setLayoutParams(params);

        if (!isUnlocked) badgeLayout.setAlpha(0.4f);

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

    private void showNewsDialog(String message) {
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("📢 Latest News")
                    .setMessage(message)
                    .setPositiveButton("Got it!", null)
                    .show();
        }
    }

    private void listenForAdminNews() {
        DatabaseReference newsRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("app_news").child("latest_message");

        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String news = snapshot.getValue(String.class);
                    if (news != null && !news.trim().isEmpty()) {
                        currentAdminNews = news;
                        if (viewNewsDot != null) viewNewsDot.setVisibility(View.VISIBLE);
                    } else {
                        currentAdminNews = "";
                        if (viewNewsDot != null) viewNewsDot.setVisibility(View.GONE);
                    }
                } else {
                    currentAdminNews = "";
                    if (viewNewsDot != null) viewNewsDot.setVisibility(View.GONE);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
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