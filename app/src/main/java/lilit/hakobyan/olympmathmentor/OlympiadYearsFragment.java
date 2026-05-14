package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class OlympiadYearsFragment extends Fragment {

    private String selectedOlympiad = "";
    private GridLayout gridYearsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_olympiad_years, container, false);

        if (getArguments() != null) {
            selectedOlympiad = getArguments().getString("OLYMPIAD_NAME", "Olympiad");
        }

        TextView tvTitle = view.findViewById(R.id.tvOlympiadTitle);
        tvTitle.setText(selectedOlympiad + " Past Papers");

        ImageButton btnBack = view.findViewById(R.id.btnBackToOlympiads);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        gridYearsContainer = view.findViewById(R.id.gridYearsContainer);

        List<Integer> years = getYearsForOlympiad(selectedOlympiad);

        populateYears(years);

        return view;
    }

    private List<Integer> getYearsForOlympiad(String olympiadName) {
        List<Integer> years = new ArrayList<>();
        int startYear = 0;
        int endYear = 0;

        switch (olympiadName) {
            case "IMO":
                startYear = 1959;
                endYear = 2025;
                break;
            case "IZHO":
                startYear = 2005;
                endYear = 2026;
                break;
            case "EGMO":
                startYear = 2012;
                endYear = 2026;
                break;
            case "BMO":
                startYear = 1984;
                endYear = 2025;
                break;
            case "CMO":
                startYear = 2015;
                endYear = 2025;
                break;
            default:
                startYear = 2020;
                endYear = 2024;
                break;
        }

        for (int i = endYear; i >= startYear; i--) {
            if (olympiadName.equals("CMO") && i == 2016) {
                continue;
            }
            years.add(i);
        }

        return years;
    }

    private void populateYears(List<Integer> years) {
        if (getContext() == null) return;

        for (int year : years) {
            CardView cardView = new CardView(getContext());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            );
            params.setMargins(16, 16, 16, 16);
            params.width = 0;
            params.height = 250;

            cardView.setLayoutParams(params);
            cardView.setRadius(40f);
            cardView.setCardElevation(12f);
            cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            cardView.setClickable(true);
            cardView.setFocusable(true);

            LinearLayout innerLayout = new LinearLayout(getContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setGravity(android.view.Gravity.CENTER);
            innerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            ImageView icon = new ImageView(getContext());
            icon.setImageResource(android.R.drawable.ic_menu_agenda);
            icon.setColorFilter(android.graphics.Color.parseColor("#5D4037"));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(80, 80);
            icon.setLayoutParams(iconParams);

            TextView tvYear = new TextView(getContext());
            tvYear.setText(String.valueOf(year));
            tvYear.setTextSize(20f);
            tvYear.setTypeface(null, android.graphics.Typeface.BOLD);
            tvYear.setTextColor(android.graphics.Color.parseColor("#212121"));
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textParams.setMargins(0, 16, 0, 0);
            tvYear.setLayoutParams(textParams);

            innerLayout.addView(icon);
            innerLayout.addView(tvYear);
            cardView.addView(innerLayout);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OlympiadSubmitActivity.class);
                intent.putExtra("OLYMPIAD_NAME", selectedOlympiad);
                intent.putExtra("OLYMPIAD_YEAR", String.valueOf(year));
                startActivity(intent);
            });

            gridYearsContainer.addView(cardView);
        }
    }
}