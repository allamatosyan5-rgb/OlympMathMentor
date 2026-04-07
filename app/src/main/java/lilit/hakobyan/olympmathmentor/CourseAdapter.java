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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<CourseModel> courseList;
    private Context context;

    public CourseAdapter(Context context, List<CourseModel> courseList) {
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

        // --- 1. ԿՈՂՊԵՔԻ ՍՏՈՒԳՈՒՄ ---
        boolean isUnlocked = false;

        // 1, 3, 4, 5, 6, 7 և 8-րդ դասերը ՄԻՇՏ ԲԱՑ ԵՆ
        if (course.getId() == 1 || course.getId() == 3 || course.getId() == 4 || course.getId() == 5 || course.getId() == 6 || course.getId() == 7 || course.getId() == 8) {
            isUnlocked = true;
        } else {
            isUnlocked = prefs.getBoolean("lesson" + course.getId() + "_unlocked", false);
        }

        course.setLocked(!isUnlocked);

        // --- 2. ԳՈՒՅՆԵՐԻ ՀԱՋՈՐԴԱԿԱՆՈՒԹՅՈՒՆԸ ---
        int unlockedColor;

        if (position == 0) {
            unlockedColor = Color.parseColor("#C9ADA3"); // Lesson 1 (Pastel Brown)
        } else {
            // Բաժանում ենք 7-ի վրա, որպեսզի ունենանք 7 տարբեր պաստելային գույներ
            int colorSequence = position % 7;
            switch (colorSequence) {
                case 1:
                    unlockedColor = Color.parseColor("#BBDEFB"); // Lesson 2 (Pastel Blue)
                    break;
                case 2:
                    unlockedColor = Color.parseColor("#FCE4EC"); // Lesson 3 (Pastel Pink)
                    break;
                case 3:
                    unlockedColor = Color.parseColor("#C8E6C9"); // Lesson 4 (Pastel Green)
                    break;
                case 4:
                    unlockedColor = Color.parseColor("#FFF9C4"); // Lesson 5 (Pastel Yellow)
                    break;
                case 5:
                    unlockedColor = Color.parseColor("#E1BEE7"); // Lesson 6 (Pastel Purple)
                    break;
                case 6:
                    unlockedColor = Color.parseColor("#FFE0B2"); // Lesson 7 (Pastel Orange)
                    break;
                case 0:
                default:
                    unlockedColor = Color.parseColor("#B2DFDB"); // Lesson 8 (Pastel Teal/Cyan)
                    break;
            }
        }

        // --- 3. ՎԻԶՈՒԱԼ ՁԵՎԱՎՈՐՈՒՄ ---
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

        // --- 4. ԱՍՏՂԻԿՆԵՐԻ ՑՈՒՑԱԴՐՈՒՄ ---
        int score = prefs.getInt("test" + course.getId() + "_score", 0);
        holder.ivStar1.setVisibility(View.GONE);
        holder.ivStar2.setVisibility(View.GONE);
        holder.ivStar3.setVisibility(View.GONE);

        if (score >= 6) {
            int goldColor = Color.parseColor("#FFC107");
            holder.ivStar1.setVisibility(View.VISIBLE);
            holder.ivStar1.setColorFilter(goldColor);
            if (score >= 8) {
                holder.ivStar2.setVisibility(View.VISIBLE);
                holder.ivStar2.setColorFilter(goldColor);
            }
            if (score == 10) {
                holder.ivStar3.setVisibility(View.VISIBLE);
                holder.ivStar3.setColorFilter(goldColor);
            }
        }

        // Ալիքաձև դիրքավորում
        float offset = (float) Math.sin(position * 1.2) * 280f;
        holder.itemView.setTranslationX(offset);

        // --- 5. ՍԵՂՄԵԼՈՒ ՏՐԱՄԱԲԱՆՈՒԹՅՈՒՆ ---
        holder.courseCircle.setOnClickListener(v -> {
            if (course.isLocked()) {
                Toast.makeText(context, "Lesson is locked!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = null;

                if (course.getId() == 1) {
                    intent = new Intent(context, Lesson1Activity.class);
                } else if (course.getId() == 2) {
                    intent = new Intent(context, Lesson2Activity.class);
                } else if (course.getId() == 3) {
                    intent = new Intent(context, Lesson3Activity.class);
                } else if (course.getId() == 4) {
                    intent = new Intent(context, Lesson4Activity.class);
                } else if (course.getId() == 5) {
                    intent = new Intent(context, Lesson5Activity.class);
                } else if (course.getId() == 6) {
                    intent = new Intent(context, Lesson6Activity.class);
                } else if (course.getId() == 7) {
                    intent = new Intent(context, Lesson7Activity.class);
                } else if (course.getId() == 8) {
                    intent = new Intent(context, Lesson8Activity.class); // Բացում է Դաս 8-ը
                }

                if (intent != null) {
                    context.startActivity(intent);
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