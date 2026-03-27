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

        // ԱՀԱ ԱՎՏՈՄԱՏ ՄՈՒՏՔԻ ԿՈԴԸ (Թարմացված)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Ստուգում ենք՝ արդյոք նամակը հաստատված է նույնիսկ ավտոմատ մուտքի ժամանակ
            if (currentUser.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return; // Դադարեցնում ենք Login էջի բեռնումը
            } else {
                // Եթե մնացել է համակարգում, բայց չի հաստատել, հանում ենք (Sign Out)
                mAuth.signOut();
            }
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        // ԱՀԱ ԿԱՐԵՎՈՐ ՍՏՈՒԳՈՒՄԸ. Արդյո՞ք email-ը հաստատված է
                        if (user.isEmailVerified()) {
                            // Ամեն ինչ ճիշտ է, թողնում ենք գլխավոր էջ
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Գաղտնաբառը ճիշտ է, բայց նամակը հաստատված չէ
                            Toast.makeText(LoginActivity.this, "Please verify your email address to login.", Toast.LENGTH_LONG).show();
                            // Անմիջապես հանում ենք համակարգից
                            mAuth.signOut();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}