package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
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

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Ավտոմատ մուտք գործելիս
            SharedPreferences progressPrefs = getSharedPreferences("UserProgress", MODE_PRIVATE);
            boolean isTestDone = progressPrefs.getBoolean("entry_test_done", false);

            if (isTestDone) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, EntryTestActivity.class));
            }
            finish();
            return;
        }

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

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
                            Toast.makeText(this, "Syncing data, please wait...", Toast.LENGTH_LONG).show();
                            downloadProgressFromFirebase(user.getUid());
                        } else {
                            user.sendEmailVerification().addOnCompleteListener(task -> {
                                Toast.makeText(LoginActivity.this, "Verify your email first!", Toast.LENGTH_LONG).show();
                            });
                            mAuth.signOut();
                            btnLogin.setEnabled(true);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                });
    }

    private void downloadProgressFromFirebase(String userId) {
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("backup")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        SharedPreferences profilePrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                        SharedPreferences progressPrefs = getSharedPreferences("UserProgress", MODE_PRIVATE);
                        SharedPreferences.Editor profEdit = profilePrefs.edit();
                        SharedPreferences.Editor progEdit = progressPrefs.edit();

                        DataSnapshot backup = task.getResult();

                        // Բեռնում ենք Profile
                        for (String key : new String[]{"name", "surname", "level", "achievements"}) {
                            String val = backup.child("profile").child(key).getValue(String.class);
                            if (val != null) profEdit.putString(key, val);
                        }

                        // Բեռնում ենք ԲՈԼՈՐ Աստղերը
                        for (int i = 1; i <= 21; i++) {
                            Integer stars = backup.child("progress").child("stars_lesson_" + i).getValue(Integer.class);
                            if (stars != null) progEdit.putInt("stars_lesson_" + i, stars);
                        }

                        // Բեռնում ենք Թեստի կարգավիճակը
                        Boolean testDone = backup.child("progress").child("entry_test_done").getValue(Boolean.class);
                        if (testDone != null) {
                            progEdit.putBoolean("entry_test_done", testDone);
                        }

                        profEdit.apply();
                        progEdit.apply();

                        // Որոշում ենք ուր տանել
                        if (testDone != null && testDone) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, EntryTestActivity.class));
                        }
                        finish();

                    } else {
                        // Նոր մարդ է, բազա չկա
                        startActivity(new Intent(LoginActivity.this, EntryTestActivity.class));
                        finish();
                    }
                });
    }
}