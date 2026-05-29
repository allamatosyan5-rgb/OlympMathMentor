package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // Եթե օգտատերը կա, նախքան որևէ էջ գնալը, ստուգում ենք նրա դերը բազայից
                DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                        .getReference("Users").child(currentUser.getUid());

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String role = snapshot.child("role").getValue(String.class);

                            if ("teacher".equals(role)) {
                                startActivity(new Intent(SplashActivity.this, TeacherMainActivity.class));
                            }
                            else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            }
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        finish(); // Փակում ենք Splash էկրանը
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Եթե ինտերնետի խնդիր կա, լռելյայն տանում ենք աշակերտի էջ
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });

            } else {
                // Եթե ընդհանրապես մուտքագրված չէ՝ տանում ենք Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

        }, 2500); // 2.5 վայրկյան սպասելուց հետո
    }
}