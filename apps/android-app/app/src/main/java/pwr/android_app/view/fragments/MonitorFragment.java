package pwr.android_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import pwr.android_app.R;
import pwr.android_app.dataStructures.Service;
import pwr.android_app.dataStructures.ServiceResponse;
import pwr.android_app.dataStructures.SubscriptionRequest;
import pwr.android_app.dataStructures.SubscriptionResponse;
import pwr.android_app.network.rest.ApiService;
import pwr.android_app.network.rest.ServiceGenerator;
import pwr.android_app.view.activities.MainActivity;
import pwr.android_app.view.adapters.MyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorFragment extends Fragment implements MyAdapter.SubscriptionButtonListener {

    /* ========================================== DATA ========================================== */

    private ApiService client =
            ServiceGenerator.createService(ApiService.class);

    private SharedPreferences sharedPref;

    private int mColumnCount = 1;
    private MyAdapter adapter = null;

    // .......................................... STATIC ........................................ //
    private static final String ARG_COLUMN_COUNT = "column-count";

    /* ====================================== CONSTRUCTORS ====================================== */

    public MonitorFragment() { }

    /* ========================================= GETTERS ======================================== */

    public MyAdapter getAdapter() {
        return this.adapter;
    }

    // .......................................... STATIC ........................................ //
    @SuppressWarnings("unused")
    public static MonitorFragment newInstance(int columnCount) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    /* ========================================= METHODS ======================================== */

    // ---------------------------------------- Functions --------------------------------------- //
    public void getSubscriptions() {
        // ToDo: string resource
        String cookie = sharedPref.getString("cookie",null);
        Call<List<SubscriptionResponse>> call = client.getSubscriptions(cookie);

        call.enqueue(new Callback<List<SubscriptionResponse>>() {
            @Override
            public void onResponse(Call<List<SubscriptionResponse>> call, Response<List<SubscriptionResponse>> response) {
                if (response.code() == 200) {

                    getAdapter().addInformationAboutSubscriptions(response.body());
                    getAdapter().notifyDataSetChanged();
                }
                else {

                    // ToDo: string resource
                    ((MainActivity)getActivity()).showToast("Couldn't get subscriptions.");
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionResponse>> call, Throwable t) {
                // ToDo: string resource
                ((MainActivity)getActivity()).showToast("Couldn't get subscriptions. Bad connection");
            }
        });
    }

    public void getServices() {
        // ToDo: string resource
        String cookie = sharedPref.getString("cookie",null);
        Call<List<ServiceResponse>> call = client.getServices(cookie);

        call.enqueue(new Callback<List<ServiceResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceResponse>> call, Response<List<ServiceResponse>> response) {
                if (response.code() == 200) {

                    getAdapter().setNewServicesDataList(response.body());
                    getSubscriptions();
                }
                else {
                    // ToDo: string resource
                    ((MainActivity)getActivity()).showToast("Problem has occured.");
                }
            }

            @Override
            public void onFailure(Call<List<ServiceResponse>> call, Throwable t) {
                // ToDo: string resource
                ((MainActivity)getActivity()).showToast("Failed to download data.");
            }
        });
    }

    // ----------------------------------- Fragment Lifecycle ----------------------------------- //
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            if(savedInstanceState == null) {
                adapter = new MyAdapter(new ArrayList<Service>(), this);
            }
            else {
                List<Service> sites;
                Type listType = new TypeToken<List<Service>>(){}.getType();
                sites = new Gson().fromJson(savedInstanceState.getString("list"), listType);
                adapter = new MyAdapter(sites, this);
            }

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("list", new Gson().toJson(getAdapter().getList()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // ----------------------------------- Fragment Lifecycle ----------------------------------- //
    @Override
    public void onStartSubscribingButtonFired(int serviceId) {

        String cookie = sharedPref.getString("cookie", null);

        SubscriptionRequest requestBody =
                new SubscriptionRequest(serviceId, SubscriptionRequest.SubscriptionStatus.ADD);

        Call<Void> call = client.setSubscription("application/json", cookie, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Log.i("MonitorFragment", "SUCCESS");
                getSubscriptions();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("MonitorFragment", "FAILURE");
            }
        });
    }

    @Override
    public void onStopSubscribingButtonFired(int serviceId) {

        Log.i("Stop subs. service", String.valueOf(serviceId));

        String cookie = sharedPref.getString("cookie", null);

        SubscriptionRequest requestBody =
                new SubscriptionRequest(serviceId, SubscriptionRequest.SubscriptionStatus.REMOVE);

        Call<Void> call = client.setSubscription("application/json", cookie, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Log.i("MonitorFragment", "SUCCESS");
                getSubscriptions();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("MonitorFragment", "FAILURE");
            }
        });

    }

    @Override
    public void onStartRepairServiceButtonFired(int serviceId) {

        // ToDo: write code here
    }

    @Override
    public void onStopRepairServiceButtonFired(int serviceId) {

        // ToDo: write code here
    }

    /* ========================================================================================== */
}

