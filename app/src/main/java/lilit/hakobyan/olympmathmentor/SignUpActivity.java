package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
    private RadioGroup rgRole;
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
        rgRole = findViewById(R.id.rgRole);
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

        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            Toast.makeText(this, "Password must have at least 8 chars, 1 uppercase, 1 lowercase, and 1 number.", Toast.LENGTH_LONG).show();
            return;
        }

        int selectedId = rgRole.getCheckedRadioButtonId();
        String role = (selectedId == R.id.rbTeacher) ? "teacher" : "student";

        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();

                        // Ճիշտ ուղին դեպի բազա
                        DatabaseReference ref = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                                .getReference("Users").child(userId);

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("surname", surname);
                        userMap.put("email", email);
                        userMap.put("role", role);

                        if (role.equals("student")) {
                            userMap.put("total_stars", 0);
                        }

                        ref.setValue(userMap).addOnCompleteListener(task -> {
                            btnRegister.setEnabled(true);
                            if (task.isSuccessful()) {
                                user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Registration successful! Please verify your email.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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