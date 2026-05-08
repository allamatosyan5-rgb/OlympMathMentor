package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etSurname, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 ԱՎԵԼԱՑՎԱԾ Է ԽԻՍՏ ՍՏՈՒԳՈՒՄԸ 👇
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            Toast.makeText(this, "Password must have at least 8 chars, 1 uppercase, 1 lowercase, and 1 number.", Toast.LENGTH_LONG).show();
            return;
        }

        btnRegister.setEnabled(false); // Անջատում ենք կրկնակի սեղմումը

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        String userId = user.getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("surname", surname);
                        userMap.put("email", email);

                        ref.setValue(userMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            btnRegister.setEnabled(true);
                                            if (verificationTask.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Registration successful! Please check your email to verify.",
                                                        Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                btnRegister.setEnabled(true);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}