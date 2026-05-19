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

        // 1. Ստուգում ենք՝ եթե արդեն լոգին եղած է, միանգամից բացում ենք հաջորդ էջը
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            goToNextPage();
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

        // 2. Մուտք ենք գործում Firebase-ով
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            // 💡 EMERGENCY BYPASS ՎԱՂՎԱ ՀԱՄԱՐ: Բազա չենք քաշում, միանգամից բացում ենք ծրագիրը
                            Toast.makeText(LoginActivity.this, "Welcome to OlympMath Mentor!", Toast.LENGTH_SHORT).show();
                            goToNextPage();
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

    private void goToNextPage() {
        // 💡 ՎԱՅՐԿՅԱՆԱԿԱՆ ԲԱՑՈՒՄ ԵՆՔ ԳԼԽԱՎՈՐ ԷՋԸ (Անտեսում ենք մուտքային թեստը)
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}