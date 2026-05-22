package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.*;

public class TeacherMatyanActivity extends AppCompatActivity {
    private TableLayout tableMatyan;
    private DatabaseReference db;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_matyan);

        classId = getIntent().getStringExtra("CLASS_ID");
        tableMatyan = findViewById(R.id.tableMatyan);
        db = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference();

        loadData();
    }

    private void loadData() {
        // Կարդում ենք աշակերտներին, տնայինները և գնահատականները
        db.child("classes").child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Աշակերտների ցուցակ
                Map<String, String> students = (Map<String, String>) snapshot.child("students").getValue();
                // Տնայինների ցուցակ
                DataSnapshot homeworks = snapshot.child("homeworks");

                // Կառուցում ենք վերնագրի տողը
                TableRow header = new TableRow(TeacherMatyanActivity.this);
                header.addView(createCell("Աշակերտ"));
                for (DataSnapshot hw : homeworks.getChildren()) {
                    header.addView(createCell(hw.child("title").getValue(String.class)));
                }
                tableMatyan.addView(header);

                // Աշակերտների տողերը
                if (students != null) {
                    for (String studentId : students.keySet()) {
                        TableRow row = new TableRow(TeacherMatyanActivity.this);
                        row.addView(createCell(students.get(studentId))); // Աշակերտի անունը

                        for (DataSnapshot hw : homeworks.getChildren()) {
                            String hwId = hw.getKey();
                            // Ստուգում ենք՝ արդյոք աշակերտը ուղարկել է տնայինը կամ ստացել գնահատական
                            String grade = snapshot.child("grades").child(hwId).child(studentId).getValue(String.class);
                            boolean submitted = snapshot.child("submissions").child(hwId).hasChild(studentId);

                            TextView cell = createCell(grade != null ? grade : (submitted ? "●" : "-"));
                            if (submitted && grade == null) {
                                cell.setOnClickListener(v -> showGradeDialog(studentId, hwId, students.get(studentId)));
                            }
                            row.addView(cell);
                        }
                        tableMatyan.addView(row);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(20, 20, 20, 20);
        tv.setBackgroundResource(R.drawable.cell_border); // Ստեղծիր սա res/drawable-ում
        return tv;
    }

    private void showGradeDialog(String studentId, String hwId, String name) {
        // Այստեղ կբացես Dialog, որտեղ ուսուցիչը կգրի 1-10 թիվը
        // Երբ գրի, db.child("grades").child(hwId).child(studentId).setValue(grade) կանես
    }
}