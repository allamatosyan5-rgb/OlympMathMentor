package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
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

        if (rvCourses != null) {
            rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        Button btnBack = view.findViewById(R.id.btnBackToBeginner);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rvCourses != null && getView() != null) {
            setupAdvancedButton(getView());

            while (rvCourses.getItemDecorationCount() > 0) {
                rvCourses.removeItemDecorationAt(0);
            }

            createCourseList();
            adapter = new IntermediateAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
            rvCourses.addItemDecoration(new PathDecoration());
        }
    }

    private void setupAdvancedButton(View view) {
        Button btnGoToAdvanced = view.findViewById(R.id.btnGoToAdvanced);

        if (btnGoToAdvanced != null && getContext() != null) {
            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences profilePrefs = getContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

            boolean isAdvancedUnlocked = myPrefs.getBoolean("advanced_unlocked", false);
            String currentLevel = profilePrefs.getString("level", "Beginner");

            if (currentLevel.equals("Advanced") || isAdvancedUnlocked) {
                btnGoToAdvanced.setVisibility(View.VISIBLE);
                btnGoToAdvanced.setAlpha(1.0f);
                btnGoToAdvanced.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new AdvancedFragment())
                            .addToBackStack(null)
                            .commit();
                });
            } else {
                btnGoToAdvanced.setVisibility(View.VISIBLE);
                btnGoToAdvanced.setAlpha(0.4f);
                btnGoToAdvanced.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "🔒 Locked! Score 40+ on Int. Final Exam to unlock.", Toast.LENGTH_LONG).show();
                });
            }
        }
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
        courseList.add(new CourseModel(19, "Lesson 19:\nSet Theory", false, 0));
        courseList.add(new CourseModel(20, "Lesson 20:\nTheory of Functions", false, 0));
        courseList.add(new CourseModel(21, "Final Test for\nIntermediate Level", false, 0));
    }
}