package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class LibraryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Համոզվիր, որ layout-ի անունը ճիշտ է (օրինակ՝ fragment_library)
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        MaterialCardView btnBooks = view.findViewById(R.id.btnBooks);
        MaterialCardView btnVideos = view.findViewById(R.id.btnVideos);

        btnBooks.setOnClickListener(v -> {
            // Այստեղ կավելացնենք գրքերի բացման կոդը
        });

        return view;
    }
}