package pwr.android_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.dataStructures.SubscriptionData;
import pwr.android_app.network.rest.ApiService;
import pwr.android_app.network.rest.ServiceGenerator;
import pwr.android_app.view.activities.MainActivity;
import pwr.android_app.view.adapters.MyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorFragment extends Fragment {

    /* ========================================== DATA ========================================== */

    private ApiService client =
            ServiceGenerator.createService(ApiService.class);

    private SharedPreferences sharedPref;

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
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
        Call<List<SubscriptionData>> call = client.getSubscriptions(cookie);

        call.enqueue(new Callback<List<SubscriptionData>>() {
            @Override
            public void onResponse(Call<List<SubscriptionData>> call, Response<List<SubscriptionData>> response) {
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
            public void onFailure(Call<List<SubscriptionData>> call, Throwable t) {
                // ToDo: string resource
                ((MainActivity)getActivity()).showToast("Couldn't get subscriptions. Bad connection");
            }
        });
    }

    public void getServices() {
        // ToDo: string resource
        String cookie = sharedPref.getString("cookie",null);
        Call<List<ServiceData>> call = client.getServices(cookie);

        call.enqueue(new Callback<List<ServiceData>>() {
            @Override
            public void onResponse(Call<List<ServiceData>> call, Response<List<ServiceData>> response) {
                if (response.code() == 200) {

                    getAdapter().setNewServicesDataList(response.body());
                    getAdapter().notifyDataSetChanged();
                }
                else {
                    // ToDo: string resource
                    ((MainActivity)getActivity()).showToast("Problem has occured.");
                }
            }

            @Override
            public void onFailure(Call<List<ServiceData>> call, Throwable t) {
                // ToDo: string resource
                ((MainActivity)getActivity()).showToast("Failed to download data.");
            }
        });
    }

    // ----------------------------------- Fragment Lifecycle ----------------------------------- //
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
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
                adapter = new MyAdapter(new ArrayList<Service>(), mListener);
            }
            else {
                List<Service> sites;
                Type listType = new TypeToken<List<ServiceData>>(){}.getType();
                sites = new Gson().fromJson(savedInstanceState.getString("list"), listType);
                adapter = new MyAdapter(sites, mListener);
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
        mListener = null;
    }

    // ---------------------------------------- Listeners --------------------------------------- //
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Service item);
    }

    /* ========================================================================================== */
}

