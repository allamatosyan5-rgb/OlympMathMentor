package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClassChatActivity extends AppCompatActivity {

    private String classId, className, classCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chat);

        // 1. Ստանում ենք նախորդ էջից ուղարկված տվյալները
        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");
        classCode = getIntent().getStringExtra("CLASS_CODE");

        // 2. Գտնում և տեղադրում ենք վերնագիրը (Դասարանի անունը)
        TextView tvTitle = findViewById(R.id.tvChatTitle);
        if (className != null) {
            tvTitle.setText(className);
        }

        // 3. Հետ գնալու կոճակի աշխատանքը (վերևի ձախ անկյուն)
        ImageView btnBack = findViewById(R.id.btnBackFromChat);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 4. Գտնում ենք Tabs-ը և ViewPager-ը
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // 5. Ստեղծում ենք Adapter էջերի (Fragments) համար
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return ChatFragment.newInstance(classId); // Էջ 1: Չաթ (Stream)
                    case 1:
                        return HomeworkFragment.newInstance(classId); // Էջ 2: Տնայիններ (Classwork)
                    case 2:
                        return PeopleFragment.newInstance(classId); // Էջ 3: Մարդիկ (People)
                    default:
                        return ChatFragment.newInstance(classId);
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Ունենք 3 էջ (Tabs)
            }
        });

        // 6. Կապում ենք TabLayout-ը ViewPager2-ի հետ և դնում անունները
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Stream"); break;
                case 1: tab.setText("Classwork"); break;
                case 2: tab.setText("People"); break;
            }
        }).attach();
    }
}