package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFragment extends Fragment {

    private RecyclerView rvCourses;
    private AdvancedAdapter adapter;
    private List<CourseModel> courseList;
    private ImageView imgProfileTop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced, container, false);

        rvCourses = view.findViewById(R.id.rvAdvancedCourses);
        CardView cvProfileTop = view.findViewById(R.id.cvProfileTop);
        imgProfileTop = view.findViewById(R.id.imgProfileTop);

        // Պրոֆիլի անցման ֆունկցիոնալի ավելացում (և՛ նկարի, և՛ շրջանակի համար)
        View.OnClickListener profileClickListener = v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }
        };

        if (cvProfileTop != null) {
            cvProfileTop.setOnClickListener(profileClickListener);
        }
        if (imgProfileTop != null) {
            imgProfileTop.setOnClickListener(profileClickListener);
        }

        if (rvCourses != null) {
            rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        Button btnBack = view.findViewById(R.id.btnBackToInt);
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
        loadProfileImage();

        if (rvCourses != null && getView() != null) {
            while (rvCourses.getItemDecorationCount() > 0) {
                rvCourses.removeItemDecorationAt(0);
            }
            createCourseList();
            adapter = new AdvancedAdapter(getContext(), courseList);
            rvCourses.setAdapter(adapter);
            rvCourses.addItemDecoration(new PathDecoration());
        }
    }

    private void loadProfileImage() {
        if (getContext() != null && imgProfileTop != null) {
            SharedPreferences prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
            String imageUrl = prefs.getString("profile_image_uri", "");
            if (!imageUrl.isEmpty()) {
                try {
                    imgProfileTop.setImageURI(Uri.parse(imageUrl));
                } catch (Exception e) {
                    imgProfileTop.setImageResource(R.drawable.ic_profile);
                }
            } else {
                imgProfileTop.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    private void createCourseList() {
        courseList = new ArrayList<>();
        courseList.add(new CourseModel(1, "Lesson 1:\nSet Theory & Subsets", false, 0));
        courseList.add(new CourseModel(2, "Lesson 2:\nAlgebraic Inequalities 1", false, 0));
        courseList.add(new CourseModel(3, "Lesson 3:\nAlgebraic Inequalities 2", false, 0));
        courseList.add(new CourseModel(4, "Lesson 4:\nPolynomial Divisibility 1", false, 0));
        courseList.add(new CourseModel(5, "Lesson 5:\nPolynomial Divisibility 2", false, 0));
        courseList.add(new CourseModel(6, "Lesson 6:\nRational Equations", false, 0));
        courseList.add(new CourseModel(7, "Lesson 7:\nInteger & Fractional Parts 1", false, 0));
        courseList.add(new CourseModel(8, "Lesson 8:\nInteger & Fractional Parts 2", false, 0));
        courseList.add(new CourseModel(9, "Lesson 9:\nExtremum Problems", false, 0));
        courseList.add(new CourseModel(10, "Lesson 10:\nFunctional Equations", false, 0));
        courseList.add(new CourseModel(11, "Lesson 11:\nVectors in Geometry 1", false, 0));
        courseList.add(new CourseModel(12, "Lesson 12:\nVectors in Geometry 2", false, 0));
        courseList.add(new CourseModel(13, "Lesson 13:\nConstruction Problems 1", false, 0));
        courseList.add(new CourseModel(14, "Lesson 14:\nConstruction Problems 2", false, 0));
        courseList.add(new CourseModel(15, "Lesson 15:\nConstruction Problems 3", false, 0));
        courseList.add(new CourseModel(16, "Lesson 16:\nSequences & Limits", false, 0));
        courseList.add(new CourseModel(17, "Lesson 17:\nProgressions & Sums", false, 0));
        courseList.add(new CourseModel(18, "Lesson 18:\nSymmetric Polynomials", false, 0));
        courseList.add(new CourseModel(19, "Lesson 19:\nDerivatives in Problems 1", false, 0));
        courseList.add(new CourseModel(20, "Lesson 20:\nDerivatives in Problems 2", false, 0));
        courseList.add(new CourseModel(21, "Grand Final Exam\nAdvanced Level", false, 0));
    }
}