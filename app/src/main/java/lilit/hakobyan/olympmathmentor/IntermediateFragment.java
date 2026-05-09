package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IntermediateFragment extends Fragment {

    private RecyclerView rvCourses;
    private IntermediateAdapter adapter;
    private List<CourseModel> courseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intermediate, container, false);

        rvCourses = view.findViewById(R.id.rvIntermediateCourses);

        // Ապահովագրում ենք, որ եթե XML-ում ID-ն սխալ է գրված, ծրագիրը չփակվի
        if (rvCourses != null) {
            rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

            createCourseList();
            adapter = new IntermediateAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);

            // Ավելացնում ենք ոսկեգույն/մոխրագույն միացնող գծերը (որոնք կային Beginner-ում)
            rvCourses.addItemDecoration(new PathDecoration());
        }

        Button btnBack = view.findViewById(R.id.btnBackToBeginner);
        // Ապահովագրում ենք Back կոճակը
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        return view;
    }

    private void createCourseList() {
        courseList = new ArrayList<>();
        courseList.add(new CourseModel(1, "Lesson 1: Divisibility of Integers 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(2, "Lesson 2: Divisibility of Integers 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(3, "Lesson 3: Congruences & Fermat 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(4, "Lesson 4: Congruences & Fermat 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(5, "Lesson 5: Combinatorics Elements", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(6, "Lesson 6: Word Problems 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(7, "Lesson 7: Word Problems 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(8, "Lesson 8: Logic Puzzles 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(9, "Lesson 9: Logic Puzzles 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(10, "Lesson 10: Logic Puzzles 3", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(11, "Lesson 11: Geometry 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(12, "Lesson 12: Geometry 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(13, "Lesson 13: Geometry 3", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(14, "Lesson 14: Notable Points 1", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(15, "Lesson 15: Notable Points 2", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(16, "Lesson 16: Diophantine Equations", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(17, "Lesson 17: Math Induction", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(18, "Lesson 18: Logic Propositions", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(19, "Lesson 19: Set Theory", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(20, "Lesson 20: Theory of Functions", false, R.color.course_circle_blue));
        courseList.add(new CourseModel(21, "Final Intermediate Test", false, R.color.accent_tan));
    }
}