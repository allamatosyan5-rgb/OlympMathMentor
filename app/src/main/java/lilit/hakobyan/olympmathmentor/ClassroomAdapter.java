package lilit.hakobyan.olympmathmentor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ViewHolder> {

    private List<Classroom> classroomList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Classroom classroom);
    }

    public ClassroomAdapter(List<Classroom> classroomList, OnItemClickListener listener) {
        this.classroomList = classroomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Classroom cls = classroomList.get(position);
        holder.tvClassName.setText(cls.getClassName());
        holder.tvClassCode.setText("Class Code: " + cls.getClassCode());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(cls));
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassCode;

        public ViewHolder(View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassCode = itemView.findViewById(R.id.tvClassCode);
        }
    }
}