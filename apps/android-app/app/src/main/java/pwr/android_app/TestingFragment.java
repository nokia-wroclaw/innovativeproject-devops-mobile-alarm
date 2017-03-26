package pwr.android_app;

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

public class TestingFragment extends Fragment {

    // --- DATA --- //
    public Button test1Button = null;
    private TextView test1TextView = null;


    // --- METHODS --- //
    public TestingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_testing, container, false);

        test1Button = (Button) view.findViewById(R.id.test1_button);
        test1TextView = (TextView) view.findViewById(R.id.test1_textview);

        test1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // =============================================================================
                    RequestService rs = new RequestService("https://devops-nokia.herokuapp.com/test");

                    rs.execute((Void) null);
                    // ----
                    test1TextView.setText(rs.getOutputData());
                    String grzegorz = rs.getOutputData();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                // =================================================================================

            }
        });

        // Inflate the layout for this fragment
        return view;
    }



    // Testowy request do serwera (tez co zwraca kapibarę)
    private class RequestService extends AsyncTask<Void, Void, Boolean> {

        // --- DATA --- //
        private URL url;                        // Adres zapytania.
        private String outputData;              // Dane otrzymane po zapytaniu.

        // --- CONSTRUCTOR --- //
        RequestService(String url) throws MalformedURLException {
            this.url = new URL(url);            // Ustawia URL na który zostanie wysłane żadanie.
        }

        // --- GETTERS --- //
        public String getOutputData() {
            return this.outputData;
        }

        // --- METHODS --- //
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // === Ustanawianie połączenia === //
                HttpsURLConnection connection;
                connection = (HttpsURLConnection) this.url.openConnection();
                connection.connect();

                // === Pobranie odpowiedzi z serwera === //
                if (connection.getResponseCode() == 200) {
                    InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    this.outputData = buffer.toString();
                }
                else {
                    this.outputData = "ERROR";
                }

                // === Zamykanie połączenia === //
                if (connection != null) {
                    connection.disconnect();
                }

                // === TODO Obsługa wyjątów === //
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            test1TextView.setText(this.getOutputData());
        }
    }
}
