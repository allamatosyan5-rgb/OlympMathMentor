package lilit.hakobyan.olympmathmentor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    // 💡 ԱՅՍՏԵՂ Է ԳԱՂՏՆԻՔԸ. մեծ տվյալներ փոխանցելու համար օգտագործում ենք static փոփոխական
    public static String currentBase64Image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        PhotoView photoView = findViewById(R.id.photo_view);
        ImageView btnClose = findViewById(R.id.btnCloseFullImage);

        // Կարդում ենք ոչ թե Intent-ից, այլ մեր ստատիկ փոփոխականից
        if (currentBase64Image != null && !currentBase64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(currentBase64Image, Base64.DEFAULT);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

                options.inSampleSize = calculateInSampleSize(options, screenWidth, screenHeight);
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = false;

                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

                if (decodedByte != null && photoView != null) {
                    photoView.setImageBitmap(decodedByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> {
                currentBase64Image = null; // Մաքրում ենք հիշողությունը
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentBase64Image = null; // Ապահովության համար, որ RAM-ը ազատվի
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight || (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        if (inSampleSize < 2 && (height > 2000 || width > 2000)) {
            inSampleSize = 2;
        }
        return inSampleSize;
    }
}