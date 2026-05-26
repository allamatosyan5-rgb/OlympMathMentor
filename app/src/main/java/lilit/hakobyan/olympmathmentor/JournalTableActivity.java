package lilit.hakobyan.olympmathmentor;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JournalTableActivity extends AppCompatActivity {

    private TableLayout tableJournal;
    private DatabaseReference classRef;
    private DatabaseReference usersRef;
    private String classId, className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_table);

        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");

        TextView tvTitle = findViewById(R.id.tvJournalClassName);
        if (className != null) tvTitle.setText(className + " - Journal");

        View btnBack = findViewById(R.id.btnBackFromJournal);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tableJournal = findViewById(R.id.tableJournal);

        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Class ID error!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        classRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes").child(classId);
        usersRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users");

        loadMatyanData();
    }

    private void loadMatyanData() {
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tableJournal.removeAllViews();

                List<String> studentIds = new ArrayList<>();
                if (snapshot.child("students").exists()) {
                    for (DataSnapshot s : snapshot.child("students").getChildren()) {
                        studentIds.add(s.getKey());
                    }
                } else if (snapshot.child("submissions").exists()) {
                    for (DataSnapshot hwSnap : snapshot.child("submissions").getChildren()) {
                        for (DataSnapshot studentSnap : hwSnap.getChildren()) {
                            if (!studentIds.contains(studentSnap.getKey())) {
                                studentIds.add(studentSnap.getKey());
                            }
                        }
                    }
                }

                List<DataSnapshot> homeworks = new ArrayList<>();
                if (snapshot.child("homeworks").exists()) {
                    for (DataSnapshot hw : snapshot.child("homeworks").getChildren()) {
                        homeworks.add(hw);
                    }
                }

                if (studentIds.isEmpty() || homeworks.isEmpty()) {
                    Toast.makeText(JournalTableActivity.this, "Not enough data to build the table yet.", Toast.LENGTH_LONG).show();
                    return;
                }

                TableRow headerRow = new TableRow(JournalTableActivity.this);
                headerRow.addView(createCell("Student", true, false));

                for (DataSnapshot hw : homeworks) {
                    String title = hw.child("title").getValue(String.class);
                    headerRow.addView(createCell(title != null ? title : "HW", true, false));
                }
                tableJournal.addView(headerRow);

                for (String sId : studentIds) {
                    TableRow row = new TableRow(JournalTableActivity.this);

                    TextView nameCell = createCell("Loading...", false, false);
                    row.addView(nameCell);
                    fetchStudentName(sId, nameCell);

                    for (DataSnapshot hw : homeworks) {
                        String hwId = hw.getKey();
                        String grade = snapshot.child("grades").child(hwId).child(sId).getValue(String.class);
                        DataSnapshot submissionSnap = snapshot.child("submissions").child(hwId).child(sId);
                        boolean isSubmitted = submissionSnap.exists();

                        TextView cell;
                        if (grade != null) {
                            cell = createCell(grade, false, false);
                            cell.setTypeface(null, Typeface.BOLD);
                            cell.setTextColor(Color.parseColor("#D32F2F"));
                            cell.setOnClickListener(v -> showGradeDialog(sId, hwId, submissionSnap));
                        } else if (isSubmitted) {
                            cell = createCell("●", false, true);
                            cell.setTextColor(Color.parseColor("#4CAF50"));
                            cell.setOnClickListener(v -> showGradeDialog(sId, hwId, submissionSnap));
                        } else {
                            cell = createCell("-", false, false);
                            cell.setTextColor(Color.LTGRAY);
                        }
                        row.addView(cell);
                    }
                    tableJournal.addView(row);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JournalTableActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudentName(String sId, TextView cell) {
        usersRef.child(sId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    cell.setText((name != null ? name : "") + " " + (surname != null ? surname.charAt(0) + "." : ""));
                } else {
                    cell.setText("Student");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private TextView createCell(String text, boolean isHeader, boolean isClickable) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(30, 25, 30, 25);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.cell_border);

        if (isHeader) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundColor(Color.parseColor("#D7CCC8"));
            tv.setTextColor(Color.parseColor("#3E2723"));
        } else {
            tv.setTextColor(Color.parseColor("#212121"));
        }

        if (isClickable) tv.setTextSize(24f);
        else tv.setTextSize(16f);

        return tv;
    }

    private void showGradeDialog(String studentId, String hwId, DataSnapshot submissionSnap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grade_submission, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView tvName = dialogView.findViewById(R.id.tvDialogStudentName);
        TextView tvText = dialogView.findViewById(R.id.tvDialogText);
        LinearLayout layoutImages = dialogView.findViewById(R.id.layoutDialogImages);
        EditText etGrade = dialogView.findViewById(R.id.etDialogGrade);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitGrade);

        String studentName = submissionSnap.child("studentName").getValue(String.class);
        tvName.setText(studentName != null ? studentName + "'s Work" : "Student's Work");

        String text = submissionSnap.child("answerText").getValue(String.class);
        if (text != null && !text.trim().isEmpty()) {
            tvText.setVisibility(View.VISIBLE);
            tvText.setText(text);
        }

        if (submissionSnap.hasChild("images")) {
            List<String> imagesList = submissionSnap.child("images").getValue(new GenericTypeIndicator<List<String>>() {});
            if (imagesList != null) {
                for (String base64Str : imagesList) {
                    try {
                        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);

                        // --- SAFE DECODING TO PREVENT OUT OF MEMORY ERROR ---
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true; // Only get dimensions
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                        // Target dimensions for the thumbnail in the dialog
                        int targetW = 500;
                        int targetH = 500;

                        options.inSampleSize = calculateInSampleSize(options, targetW, targetH);
                        options.inJustDecodeBounds = false; // Decode the actual image

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        // ---------------------------------------------------

                        if (bmp != null) {
                            ImageView iv = new ImageView(this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 500);
                            params.setMargins(0, 0, 16, 0);
                            iv.setLayoutParams(params);
                            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            iv.setImageBitmap(bmp);

                            iv.setOnClickListener(v -> {
                                // 💡 ԱՅՍՏԵՂ Է ՓՈՓՈԽՈՒԹՅՈՒՆԸ. Օգտագործում ենք ստատիկ փոփոխականը
                                FullImageActivity.currentBase64Image = base64Str;
                                Intent intent = new Intent(JournalTableActivity.this, FullImageActivity.class);
                                startActivity(intent);
                            });

                            layoutImages.addView(iv);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        classRef.child("grades").child(hwId).child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etGrade.setText(snapshot.getValue(String.class));
                    btnSubmit.setText("Update Grade");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSubmit.setOnClickListener(v -> {
            String gradeStr = etGrade.getText().toString().trim();
            if (!gradeStr.isEmpty()) {
                classRef.child("grades").child(hwId).child(studentId).setValue(gradeStr)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Graded!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter grade", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    // Utility method to calculate the scaling factor
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}