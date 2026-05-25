package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class JournalClassListFragment extends Fragment {

    private RecyclerView rvClasses;
    private JournalSquareAdapter adapter;
    private List<Classroom> classroomList;
    private DatabaseReference classesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_classes, container, false);

        View fab = view.findViewById(R.id.fabAddClass);
        if (fab != null) fab.setVisibility(View.GONE);

        rvClasses = view.findViewById(R.id.rvClasses);

        rvClasses.setLayoutManager(new GridLayoutManager(getContext(), 2));

        classroomList = new ArrayList<>();
        adapter = new JournalSquareAdapter(classroomList);
        rvClasses.setAdapter(adapter);

        classesRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes");
        loadTeacherClasses();

        return view;
    }

    private void loadTeacherClasses() {
        String currentTeacherId = FirebaseAuth.getInstance().getUid();
        if (currentTeacherId == null) return;

        classesRef.orderByChild("teacherId").equalTo(currentTeacherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classroomList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Classroom classroom = child.getValue(Classroom.class);
                    if (classroom != null) {
                        classroomList.add(classroom);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 💡 ՆՈՐ ԱԴԱՊՏԵՐ՝ ՔԱՌԱԿՈՒՍԻՆԵՐԻ ՀԱՄԱՐ
    private class JournalSquareAdapter extends RecyclerView.Adapter<JournalSquareAdapter.ViewHolder> {
        private List<Classroom> list;

        public JournalSquareAdapter(List<Classroom> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal_class_square, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Classroom classroom = list.get(position);
            holder.tvName.setText(classroom.getClassName());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), JournalTableActivity.class);
                intent.putExtra("CLASS_ID", classroom.getClassId());
                intent.putExtra("CLASS_NAME", classroom.getClassName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvClassName);
            }
        }
    }
}