package pwr.android_app.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pwr.android_app.R;

public class MainMenuFragment extends Fragment {

    /* ========================================== DATA ========================================== */

    @BindView(R.id.chart)
    PieChart chart;


    /* ====================================== CONSTRUCTORS ====================================== */

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /* ========================================= METHODS ======================================== */

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ButterKnife.bind(this,view);

//        displayChart(view);

        return view;
    }

    void displayChart(View view) {

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        float isUp = 3f;
        float isDown = 1f;
        float isUnspecified = 1f;

        String[] labels = { "Up", "Down", "Unspecified" };

        entries.add(new PieEntry(isUp, labels[0]));
        entries.add(new PieEntry(isDown, labels[1]));
        entries.add(new PieEntry(isUnspecified, labels[2]));

        colors.add(ContextCompat.getColor(getContext(), R.color.service_up_color));
        colors.add(ContextCompat.getColor(getContext(), R.color.service_down_color));
        colors.add(ContextCompat.getColor(getContext(), R.color.service_unspecified_color));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(10f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(18f);

        chart.setData(data);
        chart.animateY(1000);
        chart.setCenterText("STATES");
        chart.setHighlightPerTapEnabled(false);
        chart.setHoleColor(ContextCompat.getColor(getContext(), R.color.light_grey));

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
    }

    /* ========================================================================================== */
}
