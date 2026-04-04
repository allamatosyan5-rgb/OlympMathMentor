package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
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

        // Շրջանակի դիզայնը
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(ContextCompat.getColor(context, course.getColorResId()));
        shape.setStroke(8, Color.parseColor("#BDBDBD")); // Հաստ եզրագիծ նկարի պես
        holder.courseCircle.setBackground(shape);

        if (course.isLocked()) {
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.tvGo.setVisibility(View.GONE);
        } else {
            holder.ivLock.setVisibility(View.GONE);
            holder.tvGo.setVisibility(View.VISIBLE);
        }

        // --- ՄԱԳԻԱՆ ԱՅՍՏԵՂ Է (Օձաձև դասավորում) ---
        // Օգտագործում ենք Sin ֆունկցիան՝ ալիքաձև շարժում ստանալու համար
        float offset = (float) Math.sin(position * 1.2) * 280f;
        holder.itemView.setTranslationX(offset);
        // Այս 1 տողը տարրերը կտանի՝ մեջտեղ, աջ, նորից մեջտեղ, ձախ... ստեղծելով իդեալական S-աձև ճանապարհ

        // Քլիքի գործողությունը
        holder.courseCircle.setOnClickListener(v -> {
            if (course.isLocked()) {
                Toast.makeText(context, "This lesson is locked.", Toast.LENGTH_SHORT).show();
            } else {
                if (course.getId() == 1) {
                    Intent intent = new Intent(context, Lesson1Activity.class);
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
        ImageView ivLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCourseTitle);
            courseCircle = itemView.findViewById(R.id.courseCircle);
            ivLock = itemView.findViewById(R.id.ivLock);
            tvGo = itemView.findViewById(R.id.tvGo);
        }
    }
}