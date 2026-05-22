package lilit.hakobyan.olympmathmentor;

import android.app.DatePickerDialog;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateHomeworkActivity extends AppCompatActivity {

    private String classId;
    private EditText etTitle, etDesc;
    private TextView tvDeadline;
    private LinearLayout layoutImagePreview;
    private Button btnAssign;

    private String selectedDeadline = "";
    private List<String> base64ImagesList = new ArrayList<>(); // Պահում ենք նկարները այստեղ

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_homework);

        classId = getIntent().getStringExtra("CLASS_ID");

        etTitle = findViewById(R.id.etHwTitle);
        etDesc = findViewById(R.id.etHwDesc);
        tvDeadline = findViewById(R.id.tvSelectDeadline);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        btnAssign = findViewById(R.id.btnAssignHomework);
        Button btnAttach = findViewById(R.id.btnAttachFiles);

        // 1. Վերջնաժամկետի ընտրություն (Date Picker)
        tvDeadline.setOnClickListener(v -> showDatePicker());

        // 2. Նկարներ ընտրելու տրամաբանությունը
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        // Եթե օգտատերը ընտրել է մի քանի նկար
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                processImage(imageUri);
                            }
                        }
                        // Եթե ընտրել է միայն մեկ նկար
                        else if (data.getData() != null) {
                            Uri imageUri = data.getData();
                            processImage(imageUri);
                        }
                    }
                }
        );

        btnAttach.setOnClickListener(v -> openGalleryForMultipleImages());

        // 3. Տնայինը բազայում պահելը
        btnAssign.setOnClickListener(v -> assignHomework());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDeadline = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvDeadline.setText("Deadline: " + selectedDeadline);
            tvDeadline.setTextColor(getResources().getColor(android.R.color.black));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openGalleryForMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Թույլ ենք տալիս ընտրել մի քանի նկար
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    // Նկարի մշակում՝ նախադիտում և Base64 կոդավորում
    private void processImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            // Փոքրացնում ենք որակը, որ բազան չկախի
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] b = baos.toByteArray();
            String base64String = Base64.encodeToString(b, Base64.NO_WRAP);

            base64ImagesList.add(base64String);

            // Ավելացնում ենք նկարը էկրանի վրա որպես նախադիտում
            ImageView previewIcon = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
            params.setMargins(0, 0, 16, 0);
            previewIcon.setLayoutParams(params);
            previewIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            previewIcon.setImageBitmap(selectedImage);

            layoutImagePreview.addView(previewIcon);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void assignHomework() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (title.isEmpty() || selectedDeadline.isEmpty()) {
            Toast.makeText(this, "Title and Deadline are required!", Toast.LENGTH_LONG).show();
            return;
        }

        btnAssign.setEnabled(false);
        btnAssign.setText("Assigning...");

        DatabaseReference hwRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/")
                .getReference("classes").child(classId).child("homeworks");

        String hwId = hwRef.push().getKey();

        HashMap<String, Object> hwData = new HashMap<>();
        hwData.put("hwId", hwId);
        hwData.put("title", title);
        hwData.put("description", desc);
        hwData.put("deadline", selectedDeadline);
        hwData.put("images", base64ImagesList); // Պահում ենք բոլոր նկարները ցուցակի տեսքով

        if (hwId != null) {
            hwRef.child(hwId).setValue(hwData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Homework successfully assigned!", Toast.LENGTH_SHORT).show();
                        finish(); // Փակում ենք էջը և վերադառնում նախորդին
                    })
                    .addOnFailureListener(e -> {
                        btnAssign.setEnabled(true);
                        btnAssign.setText("Assign Homework");
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}