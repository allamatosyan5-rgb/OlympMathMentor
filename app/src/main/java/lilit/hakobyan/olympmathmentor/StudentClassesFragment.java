package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class StudentClassesFragment extends Fragment {

    private RecyclerView rvClasses;
    private List<Classroom> classroomList;
    private ClassroomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_classes, container, false);

        try {
            rvClasses = view.findViewById(R.id.rvStudentClasses);
            FloatingActionButton fabJoinClass = view.findViewById(R.id.fabJoinClass);

            rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
            classroomList = new ArrayList<>();

            // Ադապտերը դնում ենք դատարկ ցուցակով սկզբում
            adapter = new ClassroomAdapter(classroomList, classroom -> {
                // Այստեղ կավելացնենք ClassChatActivity-ի կոդը հետո
            });
            rvClasses.setAdapter(adapter);

            loadJoinedClasses();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error in Fragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void loadJoinedClasses() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "test";
        DatabaseReference userJoinedRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(userId).child("joinedClasses");
        DatabaseReference classesRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes");

        userJoinedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classroomList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String classId = child.getKey();
                    classesRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot classSnap) {
                            Classroom classroom = classSnap.getValue(Classroom.class);
                            if (classroom != null) {
                                classroomList.add(classroom);
                                if (adapter != null) adapter.notifyDataSetChanged();
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}