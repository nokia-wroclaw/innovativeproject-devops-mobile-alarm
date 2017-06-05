package pwr.android_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import pwr.android_app.dataStructures.StatsResponse;
import pwr.android_app.network.rest.ServerApi;
import pwr.android_app.network.rest.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuFragment extends Fragment {

    /* ========================================== DATA ========================================== */

    private SharedPreferences sharedPref;

    private ServerApi client =
            ServiceGenerator.createService(ServerApi.class);

    @BindView(R.id.chart)
    PieChart chart;


    /* ====================================== CONSTRUCTORS ====================================== */

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /* ========================================= METHODS ======================================== */

    // ----------------------------------- Fragment Lifecycle ----------------------------------- //
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ButterKnife.bind(this,view);

        getStats();

        return view;
    }

    // ----------------------------------------- Network ---------------------------------------- //
    public void getStats() {

        sharedPref = getContext().getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie),null);

        Call<StatsResponse> call = client.getStats(cookie);

        call.enqueue(new Callback<StatsResponse>() {
            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {
                displayChart(
                        getView(),
                        response.body().getUp(),
                        response.body().getDown(),
                        response.body().getUnspecified());
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {

            }
        });
    }

    // ---------------------------------------- Functions --------------------------------------- //
    void displayChart(View view, int up, int down, int unspecified) {

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        String[] labels = { "Up", "Down", "Unspecified" };

        entries.add(new PieEntry(up, labels[0]));
        entries.add(new PieEntry(down, labels[1]));
        entries.add(new PieEntry(unspecified, labels[2]));

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
        chart.setCenterText("All Services");
        chart.setHighlightPerTapEnabled(false);
        chart.setHoleColor(ContextCompat.getColor(getContext(), R.color.light_grey));
        chart.getLegend().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
    }

    /* ========================================================================================== */
}
