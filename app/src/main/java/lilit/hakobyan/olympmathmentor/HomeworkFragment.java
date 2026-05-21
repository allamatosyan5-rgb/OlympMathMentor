package lilit.hakobyan.olympmathmentor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HomeworkFragment extends Fragment {

    private String classId;
    private boolean isTeacher = false;
    private RecyclerView rvHomeworks;
    private FloatingActionButton fabAddHw;

    private DatabaseReference hwRef;
    private HwAdapter adapter;
    private List<HomeworkItem> hwList;

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

        rvHomeworks = view.findViewById(R.id.rvHomeworks);
        fabAddHw = view.findViewById(R.id.fabAddHomework);

        rvHomeworks.setLayoutManager(new LinearLayoutManager(getContext()));
        hwList = new ArrayList<>();
        adapter = new HwAdapter(hwList);
        rvHomeworks.setAdapter(adapter);

        hwRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes").child(classId).child("homeworks");

        checkUserRole();
        loadHomeworks();

        fabAddHw.setOnClickListener(v -> showAddHomeworkDialog());

        return view;
    }

    private void checkUserRole() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(uid).child("role")
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

    private void showAddHomeworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Assign Homework");

        // Ստեղծում ենք Input դաշտեր Ուսուցչի համար
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTitle = new EditText(getContext());
        etTitle.setHint("Title (e.g. IMO 2020 Problems)");
        layout.addView(etTitle);

        final EditText etDesc = new EditText(getContext());
        etDesc.setHint("Instructions...");
        layout.addView(etDesc);

        final TextView tvDeadline = new TextView(getContext());
        tvDeadline.setText("Select Deadline ->");
        tvDeadline.setPadding(0, 20, 0, 20);
        tvDeadline.setTextColor(android.graphics.Color.RED);
        tvDeadline.setTextSize(16f);
        layout.addView(tvDeadline);

        final String[] selectedDate = {"No Deadline"};

        tvDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                selectedDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                tvDeadline.setText("Deadline: " + selectedDate[0]);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        builder.setView(layout);

        builder.setPositiveButton("Assign", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!title.isEmpty()) {
                String hwId = hwRef.push().getKey();
                HashMap<String, Object> hwData = new HashMap<>();
                hwData.put("hwId", hwId);
                hwData.put("title", title);
                hwData.put("description", desc);
                hwData.put("deadline", selectedDate[0]);

                if (hwId != null) hwRef.child(hwId).setValue(hwData);
                Toast.makeText(getContext(), "Homework Assigned!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
            holder.tvDeadline.setText("Due: " + hw.deadline);

            // Աշակերտի մոտ դնում ենք ԿԱՐՄԻՐ ԿԵՏԻԿ (Քանի դեռ չի հանձնել)
            if (!isTeacher) {
                holder.dotUnfinished.setVisibility(View.VISIBLE);

                // Սեղմելիս տանում ենք «Տնայինը հանձնելու» էջ
                holder.itemView.setOnClickListener(v -> {
                    // Intent intent = new Intent(getContext(), SubmitHomeworkActivity.class);
                    // intent.putExtra("HW_ID", hw.hwId);
                    // startActivity(intent);
                    Toast.makeText(getContext(), "Opening Homework...", Toast.LENGTH_SHORT).show();
                });
            } else {
                holder.dotUnfinished.setVisibility(View.GONE);

                // Ուսուցչի մոտ սեղմելիս տանում ենք ՄԱՏՅԱՆ
                holder.itemView.setOnClickListener(v -> {
                    // Intent intent = new Intent(getContext(), MatyanActivity.class);
                    // intent.putExtra("HW_ID", hw.hwId);
                    // startActivity(intent);
                    Toast.makeText(getContext(), "Opening Matyan for this homework...", Toast.LENGTH_SHORT).show();
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