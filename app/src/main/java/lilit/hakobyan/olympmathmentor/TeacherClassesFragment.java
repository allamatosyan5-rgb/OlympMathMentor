package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeacherClassesFragment extends Fragment {

    private RecyclerView rvClasses;
    private ClassroomAdapter adapter;
    private List<Classroom> classroomList;
    private FloatingActionButton fabAddClass;
    private TextView tvGreeting;

    private DatabaseReference classesRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_classes, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            currentUserId = "unknown_teacher";
        }

        classesRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes");

        tvGreeting = view.findViewById(R.id.tvTeacherGreeting);
        rvClasses = view.findViewById(R.id.rvClasses);
        fabAddClass = view.findViewById(R.id.fabAddClass);

        rvClasses.setHasFixedSize(true);
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));

        classroomList = new ArrayList<>();

        // Ճիշտ կանչ՝ OnClassClickListener-ով (onClick և onDeleteClick)
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
                showDeleteClassDialog(classroom);
            }
        });

        rvClasses.setAdapter(adapter);

        fabAddClass.setOnClickListener(v -> showCreateClassDialog());

        loadTeacherClasses();

        return view;
    }

    private void showCreateClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create New Class");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("e.g. Photon 11th Grade");
        input.setPadding(40, 40, 40, 40);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String className = input.getText().toString().trim();
            if (!className.isEmpty()) {
                createNewClass(className);
            } else {
                Toast.makeText(getContext(), "Class name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createNewClass(String className) {
        String classId = classesRef.push().getKey();
        String classCode = generateRandomCode();

        Classroom newClass = new Classroom(classId, className, classCode, currentUserId);

        if (classId != null) {
            classesRef.child(classId).setValue(newClass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Class Created! Code: " + classCode, Toast.LENGTH_LONG).show();
                        FirebaseMessaging.getInstance().subscribeToTopic("class_" + classId);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return code.toString();
    }

    private void loadTeacherClasses() {
        classesRef.orderByChild("teacherId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        classroomList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Classroom classroom = dataSnapshot.getValue(Classroom.class);
                            if (classroom != null) {
                                classroomList.add(classroom);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load classes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDeleteClassDialog(Classroom classroom) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to permanently delete " + classroom.getClassName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteClass(classroom.getClassId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass(String classId) {
        classesRef.child(classId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Class deleted successfully.", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("class_" + classId);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}