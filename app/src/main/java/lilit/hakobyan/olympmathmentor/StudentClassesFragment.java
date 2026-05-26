package lilit.hakobyan.olympmathmentor;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;

public class StudentClassesFragment extends Fragment {

    private RecyclerView rvClasses;
    private List<Classroom> classroomList;
    private ClassroomAdapter adapter;
    private DatabaseReference db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_classes, container, false);

        rvClasses = view.findViewById(R.id.rvStudentClasses);
        FloatingActionButton fabJoinClass = view.findViewById(R.id.fabJoinClass);

        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        classroomList = new ArrayList<>();

        db = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference();

        // 1. Ադապտերը հիմա ընդունում է նոր ինտերֆեյս՝ 2 տարբեր սեղմումներով
        adapter = new ClassroomAdapter(classroomList, new ClassroomAdapter.OnClassClickListener() {
            @Override
            public void onClick(Classroom classroom) {
                Intent intent = new Intent(getActivity(), ClassChatActivity.class);
                intent.putExtra("CLASS_ID", classroom.getClassId());
                intent.putExtra("CLASS_NAME", classroom.getClassName());
                intent.putExtra("CLASS_CODE", classroom.getClassCode());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Classroom classroom) {
                // Կանչվում է ջնջելու կոճակը սեղմելիս
                showLeaveClassDialog(classroom);
            }
        });

        rvClasses.setAdapter(adapter);

        fabJoinClass.setOnClickListener(v -> showJoinClassDialog());

        loadJoinedClasses();
        return view;
    }

    private void showJoinClassDialog() {
        EditText editText = new EditText(getContext());
        editText.setHint("Enter 6-digit Class Code");
        editText.setPadding(40,40,40,40);

        new AlertDialog.Builder(getContext())
                .setTitle("Join a Class")
                .setView(editText)
                .setPositiveButton("Join", (d, w) -> {
                    String code = editText.getText().toString().trim();
                    if (!code.isEmpty()) {
                        joinClassByCode(code);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void joinClassByCode(String code) {
        DatabaseReference classesRef = db.child("classes");

        classesRef.orderByChild("classCode").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot classSnap : snapshot.getChildren()) {
                        String classId = classSnap.getKey();
                        saveClassToStudentProfile(classId);
                        break;
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid Class Code!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveClassToStudentProfile(String classId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        db.child("Users").child(user.getUid()).child("joinedClasses").child(classId).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Joined successfully!", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic("class_" + classId);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // --- ԱՎԵԼԱՑՎԱԾ Է. Ջնջելու հաստատման պատուհան ---
    private void showLeaveClassDialog(Classroom classroom) {
        new AlertDialog.Builder(getContext())
                .setTitle("Leave Class")
                .setMessage("Are you sure you want to leave " + classroom.getClassName() + "?")
                .setPositiveButton("Yes, Leave", (dialog, which) -> {
                    leaveClass(classroom.getClassId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // --- ԱՎԵԼԱՑՎԱԾ Է. Ջնջելու ռեալ գործողությունը Firebase-ից ---
    private void leaveClass(String classId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Ջնջում ենք դասարանը աշակերտի joinedClasses ցուցակից
        db.child("Users").child(user.getUid()).child("joinedClasses").child(classId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "You left the class.", Toast.LENGTH_SHORT).show();
                    // Ապաբաժանորդագրվում ենք ծանուցումներից
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("class_" + classId);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadJoinedClasses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userJoinedRef = db.child("Users").child(user.getUid()).child("joinedClasses");

        // addValueEventListener-ը ավտոմատ կթարմացնի ցուցակը, երբ ջնջենք դասարանը
        userJoinedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classroomList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String classId = child.getKey();

                    db.child("classes").child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot classSnap) {
                            Classroom classroom = classSnap.getValue(Classroom.class);
                            if (classroom != null) {
                                boolean exists = false;
                                for (Classroom c : classroomList) {
                                    if (c.getClassId().equals(classroom.getClassId())) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
                                    classroomList.add(classroom);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}