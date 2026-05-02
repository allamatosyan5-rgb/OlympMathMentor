package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCourses.addItemDecoration(new PathDecoration());

        view.findViewById(R.id.btnBackToBeginner).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());
            transaction.commit();
        });

        createCourseList();

        adapter = new IntermediateAdapter(getContext(), courseList);
        rvCourses.setAdapter(adapter);



        return view;
    }

    private void createCourseList() {
        courseList = new ArrayList<>();


        courseList.add(new CourseModel(1, "Lesson 1:\nDivisibility of Integers 1", false, 0));
        courseList.add(new CourseModel(2, "Lesson 2:\nDivisibility of Integers 2", false, 0));
        courseList.add(new CourseModel(3, "Lesson 3:\nCongruences & Fermat 1", false, 0));
        courseList.add(new CourseModel(4, "Lesson 4:\nCongruences & Fermat 2", false, 0));
        courseList.add(new CourseModel(5, "Lesson 5:\nElements of Combinatorics", false, 0));
        courseList.add(new CourseModel(6, "Lesson 6:\nFascinating Word Problems 1", false, 0));
        courseList.add(new CourseModel(7, "Lesson 7:\nFascinating Word Problems 2", false, 0));
        courseList.add(new CourseModel(8, "Lesson 8:\nLogic Puzzles 1", false, 0));
        courseList.add(new CourseModel(9, "Lesson 9:\nLogic Puzzles 2", false, 0));
        courseList.add(new CourseModel(10, "Lesson 10:\nLogic Puzzles 3", false, 0));
        courseList.add(new CourseModel(11, "Lesson 11:\nGeometric Problems 1", false, 0));
        courseList.add(new CourseModel(12, "Lesson 12:\nGeometric Problems 2", false, 0));
        courseList.add(new CourseModel(13, "Lesson 13:\nGeometric Problems 3", false, 0));
        courseList.add(new CourseModel(14, "Lesson 14:\nPoints & Lines in Triangles 1", false, 0));
        courseList.add(new CourseModel(15, "Lesson 15:\nPoints & Lines in Triangles 2", false, 0));
        courseList.add(new CourseModel(16, "Lesson 16:\nDiophantine Equations", false, 0));
        courseList.add(new CourseModel(17, "Lesson 17:\nMathematical Induction", false, 0));
        courseList.add(new CourseModel(18, "Lesson 18:\nPropositions & Statements", false, 0));
        courseList.add(new CourseModel(19, "Lesson 19:\nDirichlet's Principle 1", false, 0));
        courseList.add(new CourseModel(20, "Lesson 20:\nDirichlet's Principle 2", false, 0));
        courseList.add(new CourseModel(21, "Final Test for\nIntermediate Level", false, 0));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            createCourseList();
            adapter = new IntermediateAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
        }
    }
}