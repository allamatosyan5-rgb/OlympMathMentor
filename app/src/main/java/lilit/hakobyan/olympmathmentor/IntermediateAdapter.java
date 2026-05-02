package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IntermediateAdapter extends RecyclerView.Adapter<IntermediateAdapter.ViewHolder> {

    private List<CourseModel> courseList;
    private Context context;

    private final int[] intermediateColors = {
            Color.parseColor("#D1C4E9"),
            Color.parseColor("#B2DFDB"),
            Color.parseColor("#FFCC80"),
            Color.parseColor("#9FA8DA")
    };

    public IntermediateAdapter(Context context, List<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseModel course = courseList.get(position);
        holder.tvTitle.setText(course.getTitle());

        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        boolean isUnlocked = false;

        if (course.getId() == 1) {
            isUnlocked = true;
        } else {
            int previousLessonId = course.getId() - 1;
            int previousTestScore = prefs.getInt("int_test" + previousLessonId + "_score", 0);

            if (previousTestScore >= 6) {
                isUnlocked = true;
            } else {
                isUnlocked = false;
            }
        }

        course.setLocked(!isUnlocked);

        int unlockedColor = intermediateColors[position % 4];

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);

        if (course.isLocked()) {
            shape.setColor(Color.parseColor("#F5F5F5"));
            shape.setStroke(6, Color.parseColor("#E0E0E0"));
            holder.tvGo.setTextColor(Color.parseColor("#BDBDBD"));
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.tvGo.setVisibility(View.GONE);
        } else {
            shape.setColor(unlockedColor);
            shape.setStroke(10, Color.parseColor("#3E2723"));
            holder.tvGo.setTextColor(Color.parseColor("#3E2723"));
            holder.ivLock.setVisibility(View.GONE);
            holder.tvGo.setVisibility(View.VISIBLE);
        }
        holder.courseCircle.setBackground(shape);

        int score = prefs.getInt("int_test" + course.getId() + "_score", 0);
        holder.ivStar1.setVisibility(View.GONE);
        holder.ivStar2.setVisibility(View.GONE);
        holder.ivStar3.setVisibility(View.GONE);

        if (score >= 9) {
            int goldColor = Color.parseColor("#FFC107");
            holder.ivStar1.setVisibility(View.VISIBLE);
            holder.ivStar1.setColorFilter(goldColor);

            if (score >= 12) {
                holder.ivStar2.setVisibility(View.VISIBLE);
                holder.ivStar2.setColorFilter(goldColor);
            }
            if (score >= 14) {
                holder.ivStar3.setVisibility(View.VISIBLE);
                holder.ivStar3.setColorFilter(goldColor);
            }
        }

        float offset = (float) Math.sin(position * 1.2) * 280f;
        holder.itemView.setTranslationX(offset);

        holder.courseCircle.setOnClickListener(v -> {
            if (course.isLocked()) {
                if (course.getId() == 21) {
                    Toast.makeText(context, "Locked! Score 6+ on Lesson 20 to unlock the Final Exam.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Locked! Score 6+ on Lesson " + (course.getId() - 1) + " to unlock.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (course.getId() == 21) {

                    Toast.makeText(context, "Intermediate Final Exam coming soon!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        String className = "lilit.hakobyan.olympmathmentor.IntLesson" + course.getId() + "Activity";
                        Class<?> activityClass = Class.forName(className);
                        Intent intent = new Intent(context, activityClass);
                        context.startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(context, "Intermediate Lesson " + course.getId() + " is coming soon!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvGo;
        FrameLayout courseCircle;
        ImageView ivLock, ivStar1, ivStar2, ivStar3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCourseTitle);
            courseCircle = itemView.findViewById(R.id.courseCircle);
            ivLock = itemView.findViewById(R.id.ivLock);
            tvGo = itemView.findViewById(R.id.tvGo);
            ivStar1 = itemView.findViewById(R.id.ivStar1);
            ivStar2 = itemView.findViewById(R.id.ivStar2);
            ivStar3 = itemView.findViewById(R.id.ivStar3);
        }
    }
}