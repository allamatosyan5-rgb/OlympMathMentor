package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnTestStudent, btnTestTeacher;
    private TextView tvGoToSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);
        btnTestStudent = findViewById(R.id.btnTestStudent);
        btnTestTeacher = findViewById(R.id.btnTestTeacher);

        mAuth = FirebaseAuth.getInstance();

        // --- ԱՎԵԼԱՑՎԱԾ Է. Ավտոմատ մուտքի ստուգում ---
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRoleAndRedirect(currentUser.getUid(), currentUser.getEmail());
            return;
        }
        // ------------------------------------------

        btnLogin.setOnClickListener(v -> loginUser(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()));

        tvGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        btnTestStudent.setOnClickListener(v -> {
            loginUser("arpinem701@gmail.com", "ArturArpine2009");
        });

        btnTestTeacher.setOnClickListener(v -> {
            loginUser("samsung.campus.teacher2026@gmail.com", "Samsung2026");
        });
    }

    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnTestStudent.setEnabled(false);
        btnTestTeacher.setEnabled(false);
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified() || email.equals("arpinem701@gmail.com") || email.equals("samsung.campus.teacher2026@gmail.com")) {
                            checkUserRoleAndRedirect(user.getUid(), email);
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            enableButtons();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    enableButtons();
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void enableButtons() {
        btnLogin.setEnabled(true);
        btnTestStudent.setEnabled(true);
        btnTestTeacher.setEnabled(true);
    }

    private void checkUserRoleAndRedirect(String userId, String userEmail) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);

                    if (snapshot.hasChild("backup")) {
                        DataSnapshot backupSnapshot = snapshot.child("backup");

                        if (backupSnapshot.hasChild("profile")) {
                            SharedPreferences profilePrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor profileEditor = profilePrefs.edit();
                            DataSnapshot profileSnap = backupSnapshot.child("profile");

                            if(profileSnap.hasChild("name")) profileEditor.putString("name", profileSnap.child("name").getValue(String.class));
                            if(profileSnap.hasChild("surname")) profileEditor.putString("surname", profileSnap.child("surname").getValue(String.class));
                            if(profileSnap.hasChild("profile_image_uri")) profileEditor.putString("profile_image_uri", profileSnap.child("profile_image_uri").getValue(String.class));
                            if(profileSnap.hasChild("achievements")) profileEditor.putString("achievements", profileSnap.child("achievements").getValue(String.class));
                            if(profileSnap.hasChild("currentGoals")) profileEditor.putString("currentGoals", profileSnap.child("currentGoals").getValue(String.class));
                            if(profileSnap.hasChild("completedGoals")) profileEditor.putString("completedGoals", profileSnap.child("completedGoals").getValue(String.class));

                            profileEditor.apply();
                        }

                        if (backupSnapshot.hasChild("progress")) {
                            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
                            SharedPreferences.Editor progressEditor = progressPrefs.edit();
                            DataSnapshot progressSnap = backupSnapshot.child("progress");

                            for (int i = 1; i <= 60; i++) {
                                String key = "stars_lesson_" + i;
                                if (progressSnap.hasChild(key)) {
                                    Long val = progressSnap.child(key).getValue(Long.class);
                                    if (val != null) progressEditor.putInt(key, val.intValue());
                                }
                            }
                            if(progressSnap.hasChild("extra_stars")) progressEditor.putInt("extra_stars", progressSnap.child("extra_stars").getValue(Long.class).intValue());
                            if(progressSnap.hasChild("current_streak")) progressEditor.putInt("current_streak", progressSnap.child("current_streak").getValue(Long.class).intValue());
                            if(progressSnap.hasChild("last_login_day")) progressEditor.putLong("last_login_day", progressSnap.child("last_login_day").getValue(Long.class));
                            if(progressSnap.hasChild("wrong_questions_list")) progressEditor.putString("wrong_questions_list", progressSnap.child("wrong_questions_list").getValue(String.class));
                            if(progressSnap.hasChild("favourite_problems")) progressEditor.putString("favourite_problems", progressSnap.child("favourite_problems").getValue(String.class));

                            progressEditor.apply();
                        }

                        if (backupSnapshot.hasChild("my_prefs")) {
                            SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
                            DataSnapshot myPrefsSnap = backupSnapshot.child("my_prefs");

                            for (DataSnapshot child : myPrefsSnap.getChildren()) {
                                Object val = child.getValue();
                                if (val instanceof Long) {
                                    myPrefsEditor.putInt(child.getKey(), ((Long) val).intValue());
                                } else if (val instanceof Boolean) {
                                    myPrefsEditor.putBoolean(child.getKey(), (Boolean) val);
                                }
                            }
                            myPrefsEditor.apply();
                        }

                        if (backupSnapshot.hasChild("login_history")) {
                            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
                            Set<String> historySet = new HashSet<>();
                            for (DataSnapshot daySnap : backupSnapshot.child("login_history").getChildren()) {
                                historySet.add(daySnap.getValue(String.class));
                            }
                            progressPrefs.edit().putStringSet("login_history", historySet).apply();
                        }
                    }

                    if (userEmail != null && userEmail.equals("arpinem701@gmail.com")) {
                        unlockAllLessons();
                    }

                    Intent intent;
                    if ("teacher".equals(role)) {
                        intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "User profile not found in database!", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    enableButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                enableButtons();
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlockAllLessons() {
        SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPrefs.edit();

        for (int i = 1; i <= 60; i++) {
            myEditor.putInt("test" + i + "_score", 10);
            myEditor.putInt("int_test" + i + "_score", 20);
            myEditor.putInt("adv_test" + i + "_score", 20);
        }

        myEditor.putBoolean("intermediate_unlocked", true);
        myEditor.putBoolean("advanced_unlocked", true);
        myEditor.putBoolean("adv_exam_passed", true);

        myEditor.apply();

        SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        SharedPreferences.Editor progEditor = progressPrefs.edit();

        for (int i = 1; i <= 60; i++) {
            progEditor.putInt("stars_lesson_" + i, 3);
        }
        progEditor.apply();
    }
}