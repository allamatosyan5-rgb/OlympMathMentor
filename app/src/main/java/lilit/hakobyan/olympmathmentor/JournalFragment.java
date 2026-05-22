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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalFragment extends Fragment {

    private TableLayout tableJournal;
    private DatabaseReference classRef;
    private DatabaseReference usersRef;
    private String classId;

    public static JournalFragment newInstance(String classId) {
        JournalFragment fragment = new JournalFragment();
        Bundle args = new Bundle();
        args.putString("CLASS_ID", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        tableJournal = view.findViewById(R.id.tableJournal);

        if (getArguments() != null) {
            classId = getArguments().getString("CLASS_ID");
        } else {
            classId = "UNKNOWN_CLASS";
        }

        classRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("classes").child(classId);
        usersRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users");

        loadMatyanData();

        return view;
    }

    private void loadMatyanData() {
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tableJournal.removeAllViews();

                // 1. Ստանում ենք այս դասարանին միացած աշակերտների ID-ները
                // Քանի որ մենք "students" ցուցակը կարող է չունենանք class-ի մեջ հստակ,
                // կստանանք աշակերտներին "submissions" կամ նախօրոք պահված "students" բազայից:
                // Ենթադրենք աշակերտները գրանցված են "students" ճյուղում.
                List<String> studentIds = new ArrayList<>();
                if (snapshot.child("students").exists()) {
                    for (DataSnapshot s : snapshot.child("students").getChildren()) {
                        studentIds.add(s.getKey());
                    }
                } else {
                    // Եթե հատուկ "students" ճյուղ չկա, հավաքում ենք բոլոր նրանց, ովքեր գոնե 1 տնային են հանձնել
                    for (DataSnapshot hwSnap : snapshot.child("submissions").getChildren()) {
                        for (DataSnapshot studentSnap : hwSnap.getChildren()) {
                            if (!studentIds.contains(studentSnap.getKey())) {
                                studentIds.add(studentSnap.getKey());
                            }
                        }
                    }
                }

                // 2. Ստանում ենք տնայինների ցուցակը
                List<DataSnapshot> homeworks = new ArrayList<>();
                for (DataSnapshot hw : snapshot.child("homeworks").getChildren()) {
                    homeworks.add(hw);
                }

                // 3. ԿԱՌՈՒՑՈՒՄ ԵՆՔ ՎԵՐՆԱԳՐԵՐԻ ՏՈՂԸ
                TableRow headerRow = new TableRow(getContext());
                headerRow.addView(createCell("Student", true, false));

                for (DataSnapshot hw : homeworks) {
                    String title = hw.child("title").getValue(String.class);
                    headerRow.addView(createCell(title != null ? title : "HW", true, false));
                }
                tableJournal.addView(headerRow);

                // 4. ԿԱՌՈՒՑՈՒՄ ԵՆՔ ԱՇԱԿԵՐՏՆԵՐԻ ՏՈՂԵՐԸ
                for (String sId : studentIds) {
                    TableRow row = new TableRow(getContext());

                    // Վերցնում ենք աշակերտի անունը
                    TextView nameCell = createCell("Loading...", false, false);
                    row.addView(nameCell);
                    fetchStudentName(sId, nameCell);

                    // Լրացնում ենք գնահատականները / կետիկները
                    for (DataSnapshot hw : homeworks) {
                        String hwId = hw.getKey();

                        String grade = snapshot.child("grades").child(hwId).child(sId).getValue(String.class);
                        DataSnapshot submissionSnap = snapshot.child("submissions").child(hwId).child(sId);
                        boolean isSubmitted = submissionSnap.exists();

                        TextView cell;
                        if (grade != null) {
                            cell = createCell(grade, false, false); // Արդեն գնահատված է
                            cell.setTypeface(null, Typeface.BOLD);
                        } else if (isSubmitted) {
                            cell = createCell("●", false, true); // Հանձնել է, սպասում է ստուգման
                            cell.setTextColor(Color.parseColor("#4CAF50")); // Կանաչ կետիկ
                            cell.setOnClickListener(v -> showGradeDialog(sId, hwId, submissionSnap));
                        } else {
                            cell = createCell("-", false, false); // Դեռ չի արել
                            cell.setTextColor(Color.LTGRAY);
                        }

                        row.addView(cell);
                    }
                    tableJournal.addView(row);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchStudentName(String sId, TextView cell) {
        usersRef.child(sId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    cell.setText(name + " " + (surname != null ? surname.charAt(0) + "." : ""));
                } else {
                    cell.setText("Student");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private TextView createCell(String text, boolean isHeader, boolean isClickable) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(35, 25, 35, 25);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.cell_border);

        if (isHeader) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundColor(Color.parseColor("#D7CCC8"));
            tv.setTextColor(Color.parseColor("#3E2723"));
        } else {
            tv.setTextColor(Color.parseColor("#212121"));
        }

        if (isClickable) {
            tv.setTextSize(22f); // Կետիկը խոշորացնում ենք
        } else {
            tv.setTextSize(16f);
        }

        return tv;
    }

    private void showGradeDialog(String studentId, String hwId, DataSnapshot submissionSnap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grade_submission, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView tvName = dialogView.findViewById(R.id.tvDialogStudentName);
        TextView tvText = dialogView.findViewById(R.id.tvDialogText);
        LinearLayout layoutImages = dialogView.findViewById(R.id.layoutDialogImages);
        EditText etGrade = dialogView.findViewById(R.id.etDialogGrade);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitGrade);

        // Անունը
        String studentName = submissionSnap.child("studentName").getValue(String.class);
        tvName.setText(studentName != null ? studentName + "'s Work" : "Student's Work");

        // Տեքստը
        String text = submissionSnap.child("answerText").getValue(String.class);
        if (text != null && !text.trim().isEmpty()) {
            tvText.setVisibility(View.VISIBLE);
            tvText.setText(text);
        }

        // Նկարները (List<String>)
        if (submissionSnap.hasChild("images")) {
            List<String> imagesList = submissionSnap.child("images").getValue(new GenericTypeIndicator<List<String>>() {});
            if (imagesList != null) {
                for (String base64Str : imagesList) {
                    try {
                        byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ImageView iv = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
                        params.setMargins(0, 0, 16, 0);
                        iv.setLayoutParams(params);
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv.setImageBitmap(decodedByte);

                        // Սեղմելիս նկարը մեծացնելու հնարավորություն
                        iv.setOnClickListener(v -> {
                            Intent intent = new Intent(getContext(), FullImageActivity.class);
                            intent.putExtra("IMAGE_BASE64", base64Str);
                            startActivity(intent);
                        });

                        layoutImages.addView(iv);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }

        // Գնահատելը
        btnSubmit.setOnClickListener(v -> {
            String gradeStr = etGrade.getText().toString().trim();
            if (!gradeStr.isEmpty()) {
                classRef.child("grades").child(hwId).child(studentId).setValue(gradeStr)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Graded successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(getContext(), "Please enter a grade", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}