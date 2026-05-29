package lilit.hakobyan.olympmathmentor;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubmitHomeworkActivity extends AppCompatActivity {

    private String classId, hwId, currentUserId, currentUserName;
    private TextView tvTitle, tvDesc, tvTeacherImagesLabel;
    private LinearLayout layoutTeacherImages, layoutStudentImagePreview;
    private EditText etStudentAnswer;
    private Button btnTurnIn;
    private List<String> studentBase64ImagesList = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_homework);

        classId = getIntent().getStringExtra("CLASS_ID");
        hwId = getIntent().getStringExtra("HW_ID");
        currentUserId = FirebaseAuth.getInstance().getUid();

        tvTitle = findViewById(R.id.tvSubmitHwTitle);
        tvDesc = findViewById(R.id.tvSubmitHwDesc);
        tvTeacherImagesLabel = findViewById(R.id.tvTeacherImagesLabel);
        layoutTeacherImages = findViewById(R.id.layoutTeacherImages);
        layoutStudentImagePreview = findViewById(R.id.layoutStudentImagePreview);
        etStudentAnswer = findViewById(R.id.etStudentAnswer);
        btnTurnIn = findViewById(R.id.btnTurnIn);
        Button btnAttach = findViewById(R.id.btnAttachStudentWork);

        findViewById(R.id.btnBackFromSubmit).setOnClickListener(v -> finish());

        fetchStudentName();
        loadTeacherHomework();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                processImage(clipData.getItemAt(i).getUri());
                            }
                        } else if (data.getData() != null) {
                            processImage(data.getData());
                        }
                    }
                }
        );

        btnAttach.setOnClickListener(v -> openGalleryForMultipleImages());
        btnTurnIn.setOnClickListener(v -> submitHomework());
    }

    private void fetchStudentName() {
        if (currentUserId == null) return;
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("Users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    currentUserName = (name != null ? name : "") + " " + (surname != null ? surname : "");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadTeacherHomework() {
        DatabaseReference hwRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("classes").child(classId).child("homeworks").child(hwId);
        hwRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvTitle.setText(snapshot.child("title").getValue(String.class));
                    tvDesc.setText(snapshot.child("description").getValue(String.class));
                    if (snapshot.hasChild("images")) {
                        tvTeacherImagesLabel.setVisibility(View.VISIBLE);
                        List<String> teacherImages = snapshot.child("images").getValue(new GenericTypeIndicator<List<String>>() {});
                        if (teacherImages != null) {
                            for (String base64Img : teacherImages) {
                                displayTeacherImage(base64Img);
                            }
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayTeacherImage(String base64Str) {
        try {
            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
            params.setMargins(0, 0, 16, 0);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(decodedByte);
            iv.setOnClickListener(v -> openFullImage(base64Str));
            layoutTeacherImages.addView(iv);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openFullImage(String base64Str) {
        // 💡 ԼՈՒԾՈՒՄ. Նկարը փոխանցում ենք ստատիկ հիշողության միջոցով նախքան էջը բացելը
        FullImageActivity.currentBase64Image = base64Str;
        Intent intent = new Intent(this, FullImageActivity.class);
        startActivity(intent);
    }

    private void openGalleryForMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    private void processImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            String base64String = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            studentBase64ImagesList.add(base64String);

            ImageView previewIcon = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
            params.setMargins(0, 0, 16, 0);
            previewIcon.setLayoutParams(params);
            previewIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            previewIcon.setImageBitmap(selectedImage);
            previewIcon.setOnClickListener(v -> openFullImage(base64String));
            layoutStudentImagePreview.addView(previewIcon);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitHomework() {
        String answerText = etStudentAnswer.getText().toString().trim();
        if (answerText.isEmpty() && studentBase64ImagesList.isEmpty()) {
            Toast.makeText(this, "Please provide answer/work.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnTurnIn.setEnabled(false);
        DatabaseReference subRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("classes").child(classId).child("submissions").child(hwId).child(currentUserId);

        HashMap<String, Object> submissionData = new HashMap<>();
        submissionData.put("studentId", currentUserId);
        submissionData.put("studentName", currentUserName != null ? currentUserName : "Unknown");
        submissionData.put("answerText", answerText);
        submissionData.put("images", studentBase64ImagesList);
        submissionData.put("timestamp", System.currentTimeMillis());

        subRef.setValue(submissionData).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Homework Turned In!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}