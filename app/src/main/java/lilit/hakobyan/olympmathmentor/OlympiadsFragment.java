package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class OlympiadsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_olympiads, container, false);

        CardView cvIMO = view.findViewById(R.id.cvIMO);
        CardView cvEGMO = view.findViewById(R.id.cvEGMO);
        CardView cvBMO = view.findViewById(R.id.cvBMO);
        CardView cvIZHO = view.findViewById(R.id.cvIZHO);
        CardView cvCMO = view.findViewById(R.id.cvCMO);

        cvIMO.setOnClickListener(v -> openYearsFragment("IMO"));
        cvEGMO.setOnClickListener(v -> openYearsFragment("EGMO"));
        cvBMO.setOnClickListener(v -> openYearsFragment("BMO"));
        cvIZHO.setOnClickListener(v -> openYearsFragment("IZHO"));
        cvCMO.setOnClickListener(v -> openYearsFragment("CMO"));

        return view;
    }

    private void openYearsFragment(String olympiadName) {
        OlympiadYearsFragment yearsFragment = new OlympiadYearsFragment();

        Bundle args = new Bundle();
        args.putString("OLYMPIAD_NAME", olympiadName);
        yearsFragment.setArguments(args);

        // 💡 ԽԵԼԱՑԻ ԼՈՒԾՈՒՄ. Ավտոմատ գտնում ենք, թե որ Activity-ի որ Container-ի մեջ ենք գտնվում
        if (getActivity() != null && getView() != null && getView().getParent() != null) {
            int containerId = ((ViewGroup) getView().getParent()).getId();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(containerId, yearsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}