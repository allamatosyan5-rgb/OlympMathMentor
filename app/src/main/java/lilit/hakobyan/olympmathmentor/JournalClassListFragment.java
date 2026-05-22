package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class JournalClassListFragment extends Fragment {

    private RecyclerView rvClasses;
    private ClassroomAdapter adapter;
    private List<Classroom> classroomList;
    private DatabaseReference classesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Օգտագործում ենք նույն դիզայնը, ինչ սովորական դասարանների ցուցակն է
        View view = inflater.inflate(R.layout.fragment_teacher_classes, container, false);

        // Թաքցնում ենք + կոճակը, քանի որ այստեղից նոր դասարան չենք ստեղծում, միայն մատյան ենք նայում
        View fabAddClass = view.findViewById(R.id.fabAddClass);
        if (fabAddClass != null) fabAddClass.setVisibility(View.GONE);

        rvClasses = view.findViewById(R.id.rvClasses);
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        classroomList = new ArrayList<>();

        // Սեղմելիս բացում ենք ԲՈՒՆ ՄԱՏՅԱՆԻ ԱՂՅՈՒՍԱԿԸ (JournalTableActivity)
        adapter = new ClassroomAdapter(classroomList, classroom -> {
            Intent intent = new Intent(getActivity(), JournalTableActivity.class);
            intent.putExtra("CLASS_ID", classroom.getClassId());
            intent.putExtra("CLASS_NAME", classroom.getClassName());
            startActivity(intent);
        });

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
}