package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoListActivity extends AppCompatActivity {

    private String[] lessonNames = {
            "Lesson 1", "Lesson 2", "Lesson 3", "Lesson 4",
            "Lesson 5", "Lesson 6", "Lesson 7", "Lesson 8",
            "Lesson 9", "Lesson 10", "Lesson 11", "Lesson 12",
            "Lesson 13", "Lesson 14", "Lesson 15", "Lesson 16"
    };

    private String[] videoUrls = {
            "https://youtu.be/hEN6_v6gSEo?si=BnFMFdFLATvnldrC",
            "https://youtu.be/2LTtg3clc9g?si=d0cy8Q99akB2ac9Z",
            "https://youtu.be/P4LxK9Uek5U?si=oa5MEPXYRhjVhse-",
            "https://youtu.be/VHKZSugBzhA?si=8Ca17ikiIWm1RySn",
            "https://youtu.be/bVbtqn4-Df8?si=MMBiRfSpCDIW8HVO",
            "https://youtu.be/FFEtckUNtz0?si=vd8EQ5uyy2Wmdxhd",
            "https://youtu.be/FFEtckUNtz0?si=gSxRjAi9cLQm4MvG",
            "https://youtu.be/i5Tv1hlrwL8?si=o5auc_eYp3y13deo",
            "https://youtu.be/fNBzjgWzf_Y?si=RP7Gtj020Yp3gfYW",
            "https://youtu.be/LCPLWDlX1ZI?si=ekIWKQl9yTTywKxD",
            "https://youtu.be/qEyFpMBw3wA?si=eQn_pm_qDX7j218h",
            "https://youtu.be/mq4mHa2lBi8?si=euTt4uUydoyicuT8",
            "https://youtu.be/QRXtyVRlNqg?si=-rpCMSDAycHXSgZR",
            "https://youtu.be/TrDVV0xcACI?si=uUD8RMtvQvbeJ7tc",
            "https://youtu.be/T09Iq9Q61Wc?si=TlwoxuJtcA0iEyjo",
            "https://youtu.be/O3krHAFyxH4?si=m3ydIubuIHXb5CA5"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        ListView listView = findViewById(R.id.listViewVideos);

        // Ստեղծում ենք հատուկ Adapter, որը կփոխի տեքստի գույնը
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lessonNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                // ԱՀԱ ՓՐԿԻՉ ԿՈԴԸ. Տեքստը դարձնում ենք ՍԵՎ և մի փոքր ավելի մեծ
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18f);

                return view;
            }
        };

        listView.setAdapter(adapter);

        // Քլիքի հրամանը
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String url = videoUrls[position];
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}