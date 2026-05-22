package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeworkFragment extends Fragment {

    private String classId;
    private boolean isTeacher = false;
    private RecyclerView rvHomeworks;
    private FloatingActionButton fabAddHw;

    private DatabaseReference hwRef;
    private HwAdapter adapter;
    private List<HomeworkItem> hwList;
    private String currentUserId;

    public static HomeworkFragment newInstance(String classId) {
        HomeworkFragment f = new HomeworkFragment();
        Bundle b = new Bundle();
        b.putString("CLASS_ID", classId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework, container, false);

        if (getArguments() != null) {
            classId = getArguments().getString("CLASS_ID");
        }

        currentUserId = FirebaseAuth.getInstance().getUid();

        rvHomeworks = view.findViewById(R.id.rvHomeworks);
        fabAddHw = view.findViewById(R.id.fabAddHomework);

        rvHomeworks.setLayoutManager(new LinearLayoutManager(getContext()));
        hwList = new ArrayList<>();
        adapter = new HwAdapter(hwList);
        rvHomeworks.setAdapter(adapter);

        hwRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("classes").child(classId).child("homeworks");

        checkUserRole();
        loadHomeworks();

        fabAddHw.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateHomeworkActivity.class);
            intent.putExtra("CLASS_ID", classId);
            startActivity(intent);
        });

        return view;
    }

    private void checkUserRole() {
        if (currentUserId == null) return;

        FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("Users").child(currentUserId).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        isTeacher = "teacher".equals(s.getValue(String.class));
                        if (isTeacher) {
                            fabAddHw.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void loadHomeworks() {
        hwRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hwList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    HomeworkItem item = s.getValue(HomeworkItem.class);
                    if (item != null) hwList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ================= MODEL =================
    public static class HomeworkItem {
        public String hwId, title, description, deadline;
        public List<String> images;
        public List<String> files;

        public HomeworkItem() {}
    }

    // ================= ADAPTER =================
    private class HwAdapter extends RecyclerView.Adapter<HwAdapter.HwViewHolder> {
        private List<HomeworkItem> list;
        public HwAdapter(List<HomeworkItem> list) { this.list = list; }

        @NonNull @Override
        public HwViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework, parent, false);
            return new HwViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HwViewHolder holder, int position) {
            HomeworkItem hw = list.get(position);
            holder.tvTitle.setText(hw.title);

            if (!isTeacher) {
                // 💡 Ստուգում ենք գնահատականը և կարգավիճակը աշակերտի համար
                DatabaseReference classRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                        .getReference("classes").child(classId);

                classRef.child("grades").child(hw.hwId).child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Ուսուցիչը գնահատել է
                            holder.dotUnfinished.setVisibility(View.GONE);
                            holder.tvDeadline.setText("Grade: " + snapshot.getValue(String.class));
                            holder.tvDeadline.setTextColor(Color.parseColor("#4CAF50")); // Կանաչ գույն
                            holder.tvDeadline.setTypeface(null, android.graphics.Typeface.BOLD);
                        } else {
                            // Եթե գնահատական չկա, ստուգում ենք՝ հանձնել է արդյոք
                            classRef.child("submissions").child(hw.hwId).child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot subSnap) {
                                    if (subSnap.exists()) {
                                        holder.dotUnfinished.setVisibility(View.GONE);
                                        holder.tvDeadline.setText("Turned In (Pending...)");
                                        holder.tvDeadline.setTextColor(Color.parseColor("#FF9800")); // Նարնջագույն
                                        holder.tvDeadline.setTypeface(null, android.graphics.Typeface.NORMAL);
                                    } else {
                                        holder.dotUnfinished.setVisibility(View.VISIBLE);
                                        holder.tvDeadline.setText("Due: " + hw.deadline);
                                        holder.tvDeadline.setTextColor(Color.parseColor("#D32F2F")); // Կարմիր
                                        holder.tvDeadline.setTypeface(null, android.graphics.Typeface.NORMAL);
                                    }
                                }
                                @Override public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), SubmitHomeworkActivity.class);
                    intent.putExtra("HW_ID", hw.hwId);
                    intent.putExtra("CLASS_ID", classId);
                    startActivity(intent);
                });
            } else {
                holder.dotUnfinished.setVisibility(View.GONE);
                holder.tvDeadline.setText("Due: " + hw.deadline);

                holder.itemView.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Check the Journal to see submissions.", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override public int getItemCount() { return list.size(); }

        class HwViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDeadline;
            View dotUnfinished;

            public HwViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvHwTitle);
                tvDeadline = itemView.findViewById(R.id.tvHwDeadline);
                dotUnfinished = itemView.findViewById(R.id.dotUnfinished);
            }
        }
    }
}