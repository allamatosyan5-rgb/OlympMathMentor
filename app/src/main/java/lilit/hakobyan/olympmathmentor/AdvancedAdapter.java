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

public class AdvancedAdapter extends RecyclerView.Adapter<AdvancedAdapter.ViewHolder> {

    private List<CourseModel> courseList;
    private Context context;

    private final int[] advancedColors = {
            Color.parseColor("#FFCC80"),
            Color.parseColor("#BBDEFB"),
            Color.parseColor("#C8E6C9"),
            Color.parseColor("#D1C4E9")
    };

    public AdvancedAdapter(Context context, List<CourseModel> courseList) {
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

        // Դասերի և սերտիֆիկատի բացման տրամաբանություն
        if (course.getId() == 1) {
            isUnlocked = prefs.getBoolean("advanced_unlocked", false);
        } else if (course.getId() == 22) {
            // Սերտիֆիկատը բացվում է միայն քննության 80%+ արդյունքից հետո
            isUnlocked = prefs.getBoolean("adv_exam_passed", false);
        } else {
            int previousLessonId = course.getId() - 1;
            int previousTestScore = prefs.getInt("adv_test" + previousLessonId + "_score", 0);
            isUnlocked = (previousTestScore >= 15);
        }

        course.setLocked(!isUnlocked);

        int unlockedColor = advancedColors[position % 4];

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);

        if (course.isLocked()) {
            shape.setColor(Color.parseColor("#E0E0E0"));
            shape.setStroke(6, Color.parseColor("#9E9E9E"));
            holder.tvGo.setTextColor(Color.parseColor("#757575"));
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.tvGo.setVisibility(View.GONE);
        } else {
            shape.setColor(unlockedColor);
            shape.setStroke(10, Color.parseColor("#000000")); // Սև եզրագիծ
            holder.tvGo.setTextColor(Color.parseColor("#000000")); // Սև տեքստ
            holder.ivLock.setVisibility(View.GONE);
            holder.tvGo.setVisibility(View.VISIBLE);
        }
        holder.courseCircle.setBackground(shape);

        // Աստղերի ցուցադրում
        holder.ivStar1.setVisibility(View.GONE);
        holder.ivStar2.setVisibility(View.GONE);
        holder.ivStar3.setVisibility(View.GONE);

        if (course.getId() < 22) {
            int score = prefs.getInt("adv_test" + course.getId() + "_score", 0);
            if (score >= 15) {
                int goldColor = Color.parseColor("#FFC107");
                holder.ivStar1.setVisibility(View.VISIBLE);
                holder.ivStar1.setColorFilter(goldColor);
                if (score >= 17) {
                    holder.ivStar2.setVisibility(View.VISIBLE);
                    holder.ivStar2.setColorFilter(goldColor);
                }
                if (score == 20) {
                    holder.ivStar3.setVisibility(View.VISIBLE);
                    holder.ivStar3.setColorFilter(goldColor);
                }
            }
        }

        float offset = (float) Math.sin(position * 1.2) * 280f;
        holder.itemView.setTranslationX(offset);

        holder.courseCircle.setOnClickListener(v -> {
            if (course.isLocked()) {
                if (course.getId() == 22) {
                    Toast.makeText(context, "Locked! Score 80+ on Grand Final Exam to get your Certificate.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Locked! Complete previous lesson first.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (course.getId() == 21) {
                    context.startActivity(new Intent(context, AdvFinalExamActivity.class));
                } else if (course.getId() == 22) {
                    context.startActivity(new Intent(context, CertificateActivity.class));
                } else {
                    try {
                        String className = "lilit.hakobyan.olympmathmentor.AdvLesson" + course.getId() + "Activity";
                        Class<?> activityClass = Class.forName(className);
                        context.startActivity(new Intent(context, activityClass));
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(context, "Lesson coming soon!", Toast.LENGTH_SHORT).show();
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