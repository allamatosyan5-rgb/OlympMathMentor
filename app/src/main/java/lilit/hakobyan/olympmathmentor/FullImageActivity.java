package lilit.hakobyan.olympmathmentor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        PhotoView photoView = findViewById(R.id.photo_view);
        ImageView btnClose = findViewById(R.id.btnCloseFullImage); // Գտնում ենք X կոճակը

        String base64Image = getIntent().getStringExtra("IMAGE_BASE64");
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                photoView.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Եթե սեղմեն X կոճակին՝ փակում է էջը
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }
}