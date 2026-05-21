package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
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