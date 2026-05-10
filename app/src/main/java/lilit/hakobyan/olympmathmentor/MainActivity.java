package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            // Ստուգում ենք՝ արդյոք եկել ենք FinalExamActivity-ից
            boolean openIntermediate = getIntent().getBooleanExtra("open_intermediate", false);

            if (openIntermediate) {
                // Եթե եկել ենք քննությունը 35+ հանձնելուց հետո, բացում ենք IntermediateFragment-ը
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new IntermediateFragment()).commit();
            } else {
                // Սովորական դեպքերում բացում ենք HomeFragment-ը (Beginner)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        }
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_library) {
                    selectedFragment = new LibraryFragment();
                } else if (itemId == R.id.nav_ai) {
                    selectedFragment = new AIFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_challenge) {
                    // ԱՎԵԼԱՑՎԱԾ Է. Բացում ենք Մրցույթի (Lobby) էջը
                    Intent intent = new Intent(MainActivity.this, BattleLobbyActivity.class);
                    startActivity(intent);

                    // Վերադարձնում ենք false, որպեսզի ներքևի մենյուն չփորձի գունավորել մեդալը,
                    // քանի որ մենք լրիվ ուրիշ Activity ենք բացում:
                    return false;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };
}