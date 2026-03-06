package lilit.hakobyan.olympmathmentor;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etEmail, etPassword, etName, etSurname;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();

        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*")) {
            Toast.makeText(this, "Password must be 8+ chars with upper, lower, and numbers.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", name);
                    user.put("lastName", surname);
                    user.put("role", "student");
                    user.put("score", 0);
                    user.put("level", "Pending");

                    db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                startActivity(new Intent(SignUpActivity.this, EntryTestActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}