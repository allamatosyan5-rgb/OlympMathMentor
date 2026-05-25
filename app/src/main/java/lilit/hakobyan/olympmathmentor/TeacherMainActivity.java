package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main); // Համոզվիր որ R.layout.activity_teacher_main ֆայլը ունես

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_teacher);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_teacher, new TeacherClassesFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_teacher_classes) {
                selectedFragment = new TeacherClassesFragment();
            }
            else if (id == R.id.nav_teacher_matyan) {
                selectedFragment = new JournalClassListFragment();
            }
            else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment(); }

            else if (id == R.id.nav_progress) {
                    selectedFragment = new OlympiadsFragment();
            }
            else if (id == R.id.nav_ai) {
                selectedFragment = new AIFragment();
            }
            else if (id == R.id.nav_teacher_profile) {
                 selectedFragment = new TeacherProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_teacher, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}