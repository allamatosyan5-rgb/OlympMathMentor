package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private LinearLayout containerAchievements, containerCurrentGoals, containerCompletedGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        containerAchievements = view.findViewById(R.id.containerAchievements);
        containerCurrentGoals = view.findViewById(R.id.containerCurrentGoals);
        containerCompletedGoals = view.findViewById(R.id.containerCompletedGoals);

        Button btnAddAchievement = view.findViewById(R.id.btnAddAchievement);
        Button btnAddGoal = view.findViewById(R.id.btnAddGoal);
        ImageView profileImage = view.findViewById(R.id.profileImage);

        // Achievement ավելացնելու տրամաբանություն
        btnAddAchievement.setOnClickListener(v -> {
            TextView newItem = new TextView(getContext());
            newItem.setText("🏆 New Olympiad Success");
            newItem.setTextColor(getResources().getColor(R.color.deep_brown));
            newItem.setPadding(0, 10, 0, 10);
            containerAchievements.addView(newItem);
        });

        // Նպատակ ավելացնելու և տեղափոխելու տրամաբանություն
        btnAddGoal.setOnClickListener(v -> {
            TextView goalItem = new TextView(getContext());
            goalItem.setText("📌 Current Goal (Click to Complete)");
            goalItem.setTextColor(getResources().getColor(R.color.earth_brown));
            goalItem.setPadding(0, 10, 0, 10);

            goalItem.setOnClickListener(viewGoal -> {
                containerCurrentGoals.removeView(viewGoal);
                ((TextView)viewGoal).setText("✅ Completed: " + ((TextView)viewGoal).getText().toString().replace("📌 ", ""));
                containerCompletedGoals.addView(viewGoal);
            });

            containerCurrentGoals.addView(goalItem);
        });

        // Նկարի վրա սեղմելու հնարավորություն (Gallery բացելու համար պետք կլինի Intent)
        profileImage.setOnClickListener(v -> {
            // Այստեղ կավելացնես Image Picker-ի կոդը
        });

        return view;
    }
}