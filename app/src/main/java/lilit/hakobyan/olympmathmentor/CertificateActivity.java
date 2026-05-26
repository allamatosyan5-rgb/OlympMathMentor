package lilit.hakobyan.olympmathmentor;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CertificateActivity extends AppCompatActivity {

    private ImageView ivCertificate;
    private Bitmap finalCertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        ivCertificate = findViewById(R.id.ivCertificate);
        Button btnSave = findViewById(R.id.btnSaveCertificate);

        // Հենց էջը բացվում է, հարցնում ենք անունը
        askForName();

        btnSave.setOnClickListener(v -> {
            if (finalCertificate != null) {
                saveCertificateToGallery(finalCertificate);
            }
        });
    }

    private void askForName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Details");
        builder.setMessage("How should your name appear on the certificate?");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etName = new EditText(this);
        etName.setHint("First Name");
        layout.addView(etName);

        final EditText etSurname = new EditText(this);
        etSurname.setHint("Last Name");
        layout.addView(etSurname);

        builder.setView(layout);
        builder.setCancelable(false); // Որպեսզի չկարողանա փակել առանց գրելու

        builder.setPositiveButton("Generate Certificate", (dialog, which) -> {
            String firstName = etName.getText().toString().trim();
            String lastName = etSurname.getText().toString().trim();
            String fullName = firstName + " " + lastName;

            if (fullName.trim().isEmpty()) {
                fullName = "Math Olympian"; // Default արժեք, եթե դատարկ թողնի
            }

            createCertificate(fullName);
        });

        builder.show();
    }

    private void createCertificate(String fullName) {
        // 1. Բեռնում ենք օրիգինալ դատարկ նկարը drawable-ից (certificate.png)
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.certificate);

        // 2. Ստեղծում ենք պատճեն, որի վրա կարող ենք նկարել (Mutable)
        Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // --- ԱՆՎԱՆ ԿԱՐԳԱՎՈՐՈՒՄՆԵՐ ---
        Paint namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setColor(Color.parseColor("#3E2723")); // Մուգ շագանակագույն
        namePaint.setTextSize(mutableBitmap.getHeight() * 0.08f); // Տառաչափը նկարի բարձրության 8%
        namePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        namePaint.setTextAlign(Paint.Align.CENTER); // Կենտրոնացված

        // Անվան կոորդինատները (Նկարի ուղիղ կենտրոնում)
        float xPosName = mutableBitmap.getWidth() / 2f;
        float yPosName = mutableBitmap.getHeight() * 0.49f; // 49% բարձրության վրա

        canvas.drawText(fullName, xPosName, yPosName, namePaint);

        // --- ԱՄՍԱԹՎԻ ԿԱՐԳԱՎՈՐՈՒՄՆԵՐ ---
        Paint datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datePaint.setColor(Color.parseColor("#3E2723"));
        datePaint.setTextSize(mutableBitmap.getHeight() * 0.04f); // Ավելի փոքր տառաչափ
        datePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        datePaint.setTextAlign(Paint.Align.LEFT);

        // Ստանում ենք այսօրվա ամսաթիվը "Օր / Ամիս / Տարի" ֆորմատով
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Ամսաթվի կոորդինատները (Ներքևի ձախ անկյունում՝ DATE: գրառման կողքը)
        float xPosDate = mutableBitmap.getWidth() * 0.22f; // Ձախից 22% հեռավորություն
        float yPosDate = mutableBitmap.getHeight() * 0.84f; // Վերևից 84% ներքև

        canvas.drawText(currentDate, xPosDate, yPosDate, datePaint);

        // 3. Պահպանում և ցուցադրում ենք արդյունքը
        finalCertificate = mutableBitmap;
        ivCertificate.setImageBitmap(finalCertificate);
    }

    private void saveCertificateToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "OlympMath_Certificate_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/OlympMathMentor");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(this, "Certificate Saved to Gallery!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving certificate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}