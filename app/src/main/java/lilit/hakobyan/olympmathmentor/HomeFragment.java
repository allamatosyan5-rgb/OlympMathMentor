package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCourses;
    private CourseAdapter adapter;
    private List<CourseModel> courseList;
    private TextView tvDailyInsight, tvMotivation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCourses = view.findViewById(R.id.rvCourses);
        tvDailyInsight = view.findViewById(R.id.tvDailyInsight);
        tvMotivation = view.findViewById(R.id.tvMotivation);
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        setDailyInsight();

        Button btnGoToIntermediate = view.findViewById(R.id.btnGoToIntermediate);

        if (btnGoToIntermediate != null && getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            boolean isIntermediateUnlocked = prefs.getBoolean("intermediate_unlocked", false);

            if (isIntermediateUnlocked) {
                btnGoToIntermediate.setVisibility(View.VISIBLE);
            } else {
                btnGoToIntermediate.setVisibility(View.GONE);
            }

            btnGoToIntermediate.setOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new IntermediateFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        createCourseList();
        adapter = new CourseAdapter(getContext(), courseList);
        rvCourses.setAdapter(adapter);
        rvCourses.addItemDecoration(new PathDecoration());

        return view;
    }

    private void setDailyInsight() {
        String[] insights = {
                "Prime numbers are the atoms of mathematics. Everything is built from them.",
                "Zero was invented in India and changed the world of math forever.",
                "The Golden Ratio (1.618) is found everywhere in nature, from shells to galaxies.",
                "A 'Googol' is 1 followed by 100 zeros. It's more than the atoms in the universe!",
                "Perfect numbers (like 6 and 28) are equal to the sum of their divisors.",
                "The Fibonacci sequence describes the arrangement of petals on most flowers.",
                "Mathematics is the language in which God has written the universe."
        };

        String[] motivations = {
                "Success is a function of persistence. Keep solving!",
                "Every complex problem is just a series of simple steps.",
                "A math genius is just a student who didn't give up on a hard problem.",
                "Believe in your logic. Your brain is a supercomputer!",
                "Don't fear mistakes; they are the path to discovery.",
                "The only way to learn math is to do math. Start now!",
                "Excellence is not a skill, it's an attitude. Be a champion!"
        };

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        tvDailyInsight.setText(insights[dayOfYear % insights.length]);
        tvMotivation.setText("✨ " + motivations[dayOfYear % motivations.length]);
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
        courseList.add(new CourseModel(21, "Final Test for\nBeginner Level", false, R.color.course_circle_grey));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            Button btnGoToIntermediate = getView().findViewById(R.id.btnGoToIntermediate);
            if (btnGoToIntermediate != null && getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                if (prefs.getBoolean("intermediate_unlocked", false)) {
                    btnGoToIntermediate.setVisibility(View.VISIBLE);
                }
            }
            createCourseList();
            adapter = new CourseAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
        }
    }
}