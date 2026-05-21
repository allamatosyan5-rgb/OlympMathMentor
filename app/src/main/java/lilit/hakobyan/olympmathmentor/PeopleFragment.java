package lilit.hakobyan.olympmathmentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    private String classId;

    // 💡 Ահա հենց այն ֆունկցիան, որը պահանջում էր քո սխալը
    public static PeopleFragment newInstance(String classId) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString("CLASS_ID", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getString("CLASS_ID");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        RecyclerView rvPeople = view.findViewById(R.id.rvPeople);
        rvPeople.setLayoutManager(new LinearLayoutManager(getContext()));

        // Սկզբնական շրջանում դնում ենք դատարկ ցուցակ, որպեսզի ծրագիրը չփակվի
        rvPeople.setAdapter(new PeopleAdapter(new ArrayList<>()));

        return view;
    }

    // Պարզ Ադապտեր մարդկանց ցուցակի համար
    private class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
        private List<String> peopleList;

        public PeopleAdapter(List<String> list) {
            this.peopleList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Օգտագործում ենք Android-ի ստանդարտ պարզ տեքստային դիզայնը՝ ժամանակ խնայելու համար
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(peopleList.get(position));
        }

        @Override
        public int getItemCount() {
            return peopleList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}