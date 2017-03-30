package pwr.android_app.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import pwr.android_app.R;
import pwr.android_app.model.ServiceGenerator;
import pwr.android_app.model.dataStructures.UserData;
import pwr.android_app.model.interfaces.DevOpsClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestingFragment extends Fragment {

    /* ========================================== DATA ========================================== */
    // Used in REST requests
    private DevOpsClient client = null;

    // UI references
    private Button testJsonButton = null;
    private TextView testJsonView = null;

    /* ========================================= METHODS ======================================== */
    // --- CONSTRUCTOR --- //
    public TestingFragment() {
        // Required empty public constructor
    }

    // --- ON CREATE VIEW --- //
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // [Retrofit]
        this.client = ServiceGenerator.createService(DevOpsClient.class);


        View view = inflater.inflate(R.layout.fragment_testing, container, false);

        // UI references
        testJsonButton = (Button) view.findViewById(R.id.test_json_button);
        testJsonView = (TextView) view.findViewById(R.id.test_json_view);


        // Test request
        testJsonButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Call<UserData> call = client.doTestJson();

                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {
                        testJsonView.setText(response.body().getUserEmail());
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        testJsonView.setText("ERROR");
                    }
                });
            }
        }));

        // Inflate the layout for this fragment
        return view;
    }
}
