package lilit.hakobyan.olympmathmentor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    private String classId;
    private String currentUserId;
    private boolean isCurrentUserTeacher = false;

    private TextView tvTeacherName;
    private ImageView btnRemoveTeacher;
    private RecyclerView rvStudents;

    private DatabaseReference classRef;
    private DatabaseReference usersRef;

    private StudentAdapter adapter;
    private List<StudentItem> studentList;

    public static PeopleFragment newInstance(String classId) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString("CLASS_ID", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getString("CLASS_ID");
        }
        currentUserId = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/");
        classRef = db.getReference("classes").child(classId);
        usersRef = db.getReference("Users");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        View teacherItem = view.findViewById(R.id.layoutTeacherItem);
        tvTeacherName = teacherItem.findViewById(R.id.tvPersonName);
        btnRemoveTeacher = teacherItem.findViewById(R.id.btnRemoveStudent);
        btnRemoveTeacher.setVisibility(View.GONE);

        rvStudents = view.findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList);
        rvStudents.setAdapter(adapter);

        loadPeopleData();

        return view;
    }

    private void loadPeopleData() {
        // 1. Ստանում ենք ուսուցչի տվյալները
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot classSnap) {
                if (classSnap.exists()) {
                    String teacherId = classSnap.child("teacherId").getValue(String.class);
                    if (teacherId != null && currentUserId != null) {
                        isCurrentUserTeacher = teacherId.equals(currentUserId);

                        usersRef.child(teacherId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot uSnap) {
                                if (uSnap.exists()) {
                                    String name = uSnap.child("name").getValue(String.class);
                                    String surname = uSnap.child("surname").getValue(String.class);
                                    tvTeacherName.setText(name + " " + (surname != null ? surname : ""));
                                }
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Որոնում ենք բոլոր աշակերտներին Users բազայից (ովքեր ունեն այս դասարանը)
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnap) {
                studentList.clear();
                for (DataSnapshot uSnap : usersSnap.getChildren()) {
                    // Ստուգում ենք արդյոք այս մարդու profile-ում կա այս classId-ն
                    if (uSnap.child("joinedClasses").child(classId).exists()) {
                        String studentId = uSnap.getKey();
                        String name = uSnap.child("name").getValue(String.class);
                        String surname = uSnap.child("surname").getValue(String.class);
                        String fullName = name + " " + (surname != null ? surname : "");

                        studentList.add(new StudentItem(studentId, fullName));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void removeStudentFromClass(String studentId, String studentName) {
        new AlertDialog.Builder(getContext())
                .setTitle("Remove Student")
                .setMessage("Are you sure you want to remove " + studentName + " from this class?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Ջնջում ենք աշակերտի պրոֆիլից (սա ավտոմատ կթարմացնի ցուցակը վերևի addValueEventListener-ի շնորհիվ)
                    usersRef.child(studentId).child("joinedClasses").child(classId).removeValue();
                    // Ամեն դեպքում մաքրում ենք նաև դասարանի միջից, եթե կա
                    classRef.child("students").child(studentId).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), studentName + " removed.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ================= MODEL =================
    public static class StudentItem {
        public String id;
        public String name;
        public StudentItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // ================= ADAPTER =================
    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
        private List<StudentItem> list;

        public StudentAdapter(List<StudentItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StudentItem student = list.get(position);
            holder.tvName.setText(student.name);

            if (isCurrentUserTeacher) {
                holder.btnRemove.setVisibility(View.VISIBLE);
                holder.btnRemove.setOnClickListener(v -> removeStudentFromClass(student.id, student.name));
            } else {
                holder.btnRemove.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView btnRemove;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvPersonName);
                btnRemove = itemView.findViewById(R.id.btnRemoveStudent);
            }
        }
    }
}