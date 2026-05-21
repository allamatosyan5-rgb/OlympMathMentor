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
            boolean openIntermediate = getIntent().getBooleanExtra("open_intermediate", false);

            if (openIntermediate) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new IntermediateFragment()).commit();
            } else {
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
                } else if (itemId == R.id.nav_progress) {
                    selectedFragment = new OlympiadsFragment();
                } else if (itemId == R.id.nav_ai) {
                    selectedFragment = new AIFragment();
                } else if (itemId == R.id.nav_student_classes) {
                    selectedFragment = new StudentClassesFragment();
                }
                else if (itemId == R.id.nav_challenge) {
                    Intent intent = new Intent(MainActivity.this, BattleLobbyActivity.class);
                    startActivity(intent);
                    return false;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };
}