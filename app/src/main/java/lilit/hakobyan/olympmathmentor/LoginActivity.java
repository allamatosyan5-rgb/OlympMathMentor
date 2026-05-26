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
    private Button btnLogin;
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

        mAuth = FirebaseAuth.getInstance();

        // Ավտոմատ մուտք, եթե արդեն մուտք գործած է
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            checkUserRoleAndRedirect(currentUser.getUid());
            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            checkUserRoleAndRedirect(user.getUid());
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            btnLogin.setEnabled(true);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserRoleAndRedirect(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);

                    // 1. Ստուգում ենք՝ կա՞ն արդյոք պահված (backup) տվյալներ Firebase-ում
                    if (snapshot.hasChild("backup")) {
                        DataSnapshot backupSnapshot = snapshot.child("backup");

                        // Վերականգնում ենք Պրոֆիլի տվյալները
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

                        // Վերականգնում ենք Առաջընթացի տվյալները (Աստղեր, սթրիք, բացված դասեր և այլն)
                        if (backupSnapshot.hasChild("progress")) {
                            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
                            SharedPreferences.Editor progressEditor = progressPrefs.edit();
                            DataSnapshot progressSnap = backupSnapshot.child("progress");

                            // Վերականգնում ենք բոլոր հնարավոր 60 դասերի աստղերը
                            for (int i = 1; i <= 60; i++) {
                                String key = "stars_lesson_" + i;
                                if (progressSnap.hasChild(key)) {
                                    Long val = progressSnap.child(key).getValue(Long.class);
                                    if (val != null) {
                                        progressEditor.putInt(key, val.intValue());
                                    }
                                }
                            }

                            if(progressSnap.hasChild("extra_stars")) progressEditor.putInt("extra_stars", progressSnap.child("extra_stars").getValue(Long.class).intValue());
                            if(progressSnap.hasChild("current_streak")) progressEditor.putInt("current_streak", progressSnap.child("current_streak").getValue(Long.class).intValue());
                            if(progressSnap.hasChild("last_login_day")) progressEditor.putLong("last_login_day", progressSnap.child("last_login_day").getValue(Long.class));
                            if(progressSnap.hasChild("wrong_questions_list")) progressEditor.putString("wrong_questions_list", progressSnap.child("wrong_questions_list").getValue(String.class));
                            if(progressSnap.hasChild("favourite_problems")) progressEditor.putString("favourite_problems", progressSnap.child("favourite_problems").getValue(String.class));

                            progressEditor.apply();
                        }

                        // Վերականգնում ենք մուտքերի պատմությունը (Login History)
                        if (backupSnapshot.hasChild("login_history")) {
                            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
                            Set<String> historySet = new HashSet<>();
                            for (DataSnapshot daySnap : backupSnapshot.child("login_history").getChildren()) {
                                historySet.add(daySnap.getValue(String.class));
                            }
                            progressPrefs.edit().putStringSet("login_history", historySet).apply();
                        }
                    }

                    // 2. Ուղղորդում ենք ճիշտ էջ՝ կախված դերից (Teacher / Student)
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
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}