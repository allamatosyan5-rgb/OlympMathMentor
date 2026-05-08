package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Սպասում ենք 2.5 վայրկյան (2500 միլիվայրկյան)
        new Handler().postDelayed(() -> {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Եթե օգտատերն արդեն մուտք է գործել ու հաստատել է մեյլը՝ միանգամից Գլխավոր էջ
            if (currentUser != null && currentUser.isEmailVerified()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Եթե նոր մարդ է կամ դուրս է եկել հաշվից՝ գնում ենք Լոգինի էջ
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            // Փակում ենք նկարի էջը, որպեսզի հեռախոսի "Back" կոճակը սեղմելիս նորից հետ չգա այստեղ
            finish();

        }, 2500);
    }
}