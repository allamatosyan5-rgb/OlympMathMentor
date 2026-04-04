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
        // Բացում ենք fragment_home դիզայնը
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Գտնում ենք RecyclerView-ն
        rvCourses = view.findViewById(R.id.rvCourses);

        // 2. Սահմանում ենք LayoutManager (ուղղահայաց ցուցակ)
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Պատրաստում ենք 20 դասերի տվյալները
        createCourseList();

        // 4. Ստեղծում ենք Adapter-ը և միացնում ցուցակին
        adapter = new CourseAdapter(getContext(), courseList);
        rvCourses.setAdapter(adapter);

        // 5. ԱՎԵԼԱՑՆՈՒՄ ԵՆՔ ԳԾԵՐԸ (PathDecoration)
        rvCourses.addItemDecoration(new PathDecoration());

        return view;
    }

    private void createCourseList() {
        courseList = new ArrayList<>();

        // Ստուգում ենք հեռախոսի հիշողությունը՝ տեսնելու համար արդյոք Դաս 2-ը բացվել է
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLesson2Unlocked = prefs.getBoolean("lesson2_unlocked", false);

        // Դաս 1: Միշտ ԲԱՑ Է (isLocked = false), Գույնը՝ Նարնջագույն
        courseList.add(new CourseModel(1, "Lesson 1:\nNatural Numbers", false, R.color.accent_tan));

        // Դաս 2: Կախված է թեստի արդյունքից (!isLesson2Unlocked նշանակում է՝ եթե բաց է, փակ չէ)
        courseList.add(new CourseModel(2, "Lesson 2:\nSimple Algebra", !isLesson2Unlocked, R.color.course_circle_blue));

        // Դաս 3: Փակ է, Գույնը՝ Մոխրագույն
        courseList.add(new CourseModel(3, "Lesson 3:\nIntro to Geometry", true, R.color.course_circle_grey));

        // Դաս 4: Փակ է, Գույնը՝ Մոխրագույն
        courseList.add(new CourseModel(4, "Lesson 4:\nCombinatorics", true, R.color.course_circle_grey));

        // Դաս 5-ից 20-ը ավտոմատ ստեղծում ենք որպես փակ դասեր
        for (int i = 5; i <= 20; i++) {
            courseList.add(new CourseModel(i, "Lesson " + i + ":\nAdvanced Topic", true, R.color.course_circle_grey));
        }
    }

    // Սա թարմացնում է էջը, երբ թեստից հետո հետ ենք գալիս Home
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            createCourseList(); // Նորից ենք կարդում հիշողությունը
            adapter = new CourseAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
        }
    }
}