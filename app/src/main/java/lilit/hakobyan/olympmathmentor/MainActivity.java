package lilit.hakobyan.olympmathmentor;

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
            // ՆՈՐՈՒԹՅՈՒՆ. Ստուգում ենք՝ արդյոք եկել ենք FinalExamActivity-ից
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
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };
}