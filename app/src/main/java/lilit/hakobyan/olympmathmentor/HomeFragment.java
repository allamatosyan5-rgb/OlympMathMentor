package lilit.hakobyan.olympmathmentor;

import android.content.Context;
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


        courseList.add(new CourseModel(1, "Lesson 1:\nNatural Numbers", false, R.color.accent_tan));
        courseList.add(new CourseModel(2, "Lesson 2:\nDivision with Remainder", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(3, "Lesson 3:\nPositional Notation", false, R.color.pastel_pink));
        courseList.add(new CourseModel(4, "Lesson 4:\nAppending Digits", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(5, "Lesson 5:\nDivisibility Rules", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(6, "Lesson 6:\nMotion Problems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(7, "Lesson 7:\nWork & Time Problems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(8, "Lesson 8:\nMixtures & Solutions", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(9, "Lesson 9:\nBoats & Streams", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(10, "Lesson 10:\nJoint Work & Time", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(11, "Lesson 11:\nParity", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(12, "Lesson 12:\nDirichlet's Principle", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(13, "Lesson 13:\nGCD & LCM", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(14, "Lesson 14:\nAdvanced GCD & LCM", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(15, "Lesson 15:\nPrimes & Composites", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(16, "Lesson 16:\nTriangle Foundations", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(17, "Lesson 17:\nCenters & Circles", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(18, "Lesson 18:\nAreas & Adv. Theorems", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(19, "Lesson 19:\nPolygons & Ptolemy", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(20, "Lesson 20:\nAdvanced Circles", false, R.color.course_circle_grey));
        courseList.add(new CourseModel(21, "FINAL TEST 1", false, R.color.course_circle_grey));


    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            createCourseList();
            adapter = new CourseAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
        }
    }
}