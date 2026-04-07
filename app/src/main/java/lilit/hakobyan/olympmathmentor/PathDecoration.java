package lilit.hakobyan.olympmathmentor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PathDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;

    public PathDecoration() {
        paint = new Paint();
        // Գույնը հիմա դինամիկ է որոշվում, այստեղ միայն հաստությունն ու ոճն ենք տալիս
        paint.setStrokeWidth(20f); // Գծի հաստությունը
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); // Որպեսզի գիծը հարթ լինի
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            View nextChild = parent.getChildAt(i + 1);

            View circle1 = child.findViewById(R.id.courseCircle);
            View circle2 = nextChild.findViewById(R.id.courseCircle);
            // Գտնում ենք հաջորդ դասի կողպեքի նկարը
            ImageView nextLock = nextChild.findViewById(R.id.ivLock);

            if (circle1 != null && circle2 != null && nextLock != null) {

                // --- ԳՈՒՅՆԻ ՓՈՓՈԽՈՒԹՅԱՆ ՏՐԱՄԱԲԱՆՈՒԹՅՈՒՆԸ ---
                // Եթե հաջորդ դասի կողպեքը թաքցված է (GONE), ուրեմն դասը բաց է
                if (nextLock.getVisibility() == View.GONE) {
                    paint.setColor(Color.parseColor("#FFD700")); // Ոսկեգույն գիծ
                } else {
                    paint.setColor(Color.parseColor("#A6A6A6")); // Մոխրագույն գիծ
                }

                float startX = child.getX() + circle1.getX() + circle1.getWidth() / 2f;
                float startY = child.getY() + circle1.getY() + circle1.getHeight() / 2f;

                float endX = nextChild.getX() + circle2.getX() + circle2.getWidth() / 2f;
                float endY = nextChild.getY() + circle2.getY() + circle2.getHeight() / 2f;

                c.drawLine(startX, startY, endX, endY, paint);
            }
        }
    }
}