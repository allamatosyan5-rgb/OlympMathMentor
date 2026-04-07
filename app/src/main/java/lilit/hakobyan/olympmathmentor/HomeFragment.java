package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCourses;
    private CourseAdapter adapter;
    private List<CourseModel> courseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCourses = view.findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        createCourseList();

        adapter = new CourseAdapter(getContext(), courseList);
        rvCourses.setAdapter(adapter);

        rvCourses.addItemDecoration(new PathDecoration());

        return view;
    }

    private void createCourseList() {
        courseList = new ArrayList<>();

        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Կարդում ենք 2-րդ դասի վիճակը
        boolean isLesson2Unlocked = prefs.getBoolean("lesson2_unlocked", false);

        // Lesson 1: Միշտ բաց է
        courseList.add(new CourseModel(1, "Lesson 1:\nNatural Numbers", false, R.color.accent_tan));

        // Lesson 2: Բացվում է, եթե Test 1-ը հանձնված է
        courseList.add(new CourseModel(2, "Lesson 2:\nDivision with Remainder", !isLesson2Unlocked, R.color.course_circle_blue));

        // Lesson 3: Միշտ բաց է դրված
        courseList.add(new CourseModel(3, "Lesson 3:\nPositional Notation", false, R.color.pastel_pink));

        // Lesson 4: Միշտ բաց է
        courseList.add(new CourseModel(4, "Lesson 4:\nAppending Digits", false, R.color.course_circle_grey));

        // ԱՅՍՏԵՂ ԱՎԵԼԱՑՆՈՒՄ ԵՆՔ 5-ՐԴ ԴԱՍԸ (Միշտ բաց)
        courseList.add(new CourseModel(5, "Lesson 5:\nDivisibility Rules", false, R.color.course_circle_grey));

        // Մնացած դասերը հիմա սկսվում են 6-ից (6-20)
        for (int i = 6; i <= 20; i++) {
            boolean isUnlocked = prefs.getBoolean("lesson" + i + "_unlocked", false);
            courseList.add(new CourseModel(i, "Lesson " + i + ":\nAdvanced Topic", !isUnlocked, R.color.course_circle_grey));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Այս հատվածը ապահովում է, որ էջը վերադառնալիս թարմանա
        if (adapter != null) {
            createCourseList();
            adapter = new CourseAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
        }
    }
}