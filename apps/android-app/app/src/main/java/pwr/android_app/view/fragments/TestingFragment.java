package pwr.android_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pwr.android_app.R;
import pwr.android_app.network.rest.ApiService;
import pwr.android_app.network.rest.ServiceGenerator;

public class TestingFragment extends Fragment {

    /* ========================================== DATA ========================================== */

    private ApiService client = ServiceGenerator.createService(ApiService.class);

    @BindView(R.id.test_json_view)
    TextView testJsonView;

    /* ====================================== CONSTRUCTORS ====================================== */

    public TestingFragment() {
        // Required empty public constructor
    }

    /* ========================================= METHODS ======================================== */

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_testing, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    /* ======================================== LISTENERS ======================================= */

    @OnClick(R.id.clear_cookies_button)
    void clearCookies() {
        SharedPreferences preferences =
                getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookie", null);
        editor.commit();
    }

    @OnClick(R.id.test_json_button)
    void TestJson() {

    }

    /* ========================================================================================== */
}
