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

        // 1. ՍԿԶԲՈՒՄ կապում ենք բոլոր էկրանի էլեմենտները
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        mAuth = FirebaseAuth.getInstance();

        // 2. ՆՈՐ ԴՐԱՆԻՑ ՀԵՏՈ ստուգում ենք օգտատիրոջը
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Այստեղ արդեն վտանգ չկա, որովհետև btnLogin-ը գոյություն ունի
            checkUserRoleAndRedirect(currentUser.getUid());
            return;
        }

        // 3. Կոճակների գործողությունները
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
                            Toast.makeText(LoginActivity.this, "Verify your email first!", Toast.LENGTH_LONG).show();
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

    // Այս ֆունկցիան որոշում է, թե ում ուր ուղարկել
    private void checkUserRoleAndRedirect(String userId) {
        android.util.Log.d("DEBUG_DB", "Looking for user ID: " + userId);

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(userId);

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
                    // Այժմ սա ապահով է, քանի որ btnLogin-ը initialize է արված
                    if(btnLogin != null) {
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(btnLogin != null) {
                    btnLogin.setEnabled(true);
                }
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}