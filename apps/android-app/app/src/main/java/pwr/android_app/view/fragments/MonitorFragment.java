package pwr.android_app.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import pwr.android_app.dataStructures.FixRequest;
import pwr.android_app.dataStructures.UserData;
import pwr.android_app.interfaces.ServiceButtonsListeners;
import pwr.android_app.interfaces.ToastMessenger;
import pwr.android_app.dataStructures.Service;
import pwr.android_app.dataStructures.ServiceResponse;
import pwr.android_app.dataStructures.SubscriptionRequest;
import pwr.android_app.dataStructures.SubscriptionResponse;
import pwr.android_app.network.rest.ServerApi;
import pwr.android_app.network.rest.ServiceGenerator;
import pwr.android_app.view.adapters.MyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorFragment
        extends Fragment
        implements ServiceButtonsListeners {

    /* ========================================== DATA ========================================== */

    private ServerApi client =
            ServiceGenerator.createService(ServerApi.class);

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

    // ----------------------------------------- Network ---------------------------------------- //
    // Synchronizing services info and subscriptions info.
    public void synchronizeServices() {

        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie),null);
        Call<List<ServiceResponse>> call = client.getServices(cookie);

        call.enqueue(new Callback<List<ServiceResponse>>() {

            @Override
            public void onResponse(Call<List<ServiceResponse>> call, Response<List<ServiceResponse>> response) {

                if (response.code() == 200) {
                    getAdapter().setNewServicesList(response.body());
                    synchronizeSubscriptions();
                }
                else {
                    ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_server_connection));
                }
            }

            @Override
            public void onFailure(Call<List<ServiceResponse>> call, Throwable t) {

                ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_internet_connection));
            }
        });
    }

    // Synchronizing subscriptions info only.
    public void synchronizeSubscriptions() {

        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie),null);
        Call<List<SubscriptionResponse>> call = client.getSubscriptions(cookie);

        call.enqueue(new Callback<List<SubscriptionResponse>>() {

            @Override
            public void onResponse(Call<List<SubscriptionResponse>> call, Response<List<SubscriptionResponse>> response) {

                if (response.code() == 200) {
                    getAdapter().refreshSubscriptionsInfo(response.body());
                }
                else {
                    ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_server_connection));
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionResponse>> call, Throwable t) {
                // ToDo: string resource
                ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_internet_connection));
            }
        });
    }

    @Override
    public void onStartSubscribingButtonFired(final int serviceId) {

        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie), null);

        final SubscriptionRequest requestBody =
                new SubscriptionRequest(serviceId, SubscriptionRequest.SubscriptionStatus.ADD);

        // ToDo: string resource
        Call<Void> call = client.setSubscription("application/json", cookie, requestBody);

            call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    synchronizeSubscriptions();
                }
                else {
                    ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_server_connection));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_internet_connection));
            }
        });
    }

    @Override
    public void onStopSubscribingButtonFired(int serviceId) {

        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie), null);

        SubscriptionRequest requestBody =
                new SubscriptionRequest(serviceId, SubscriptionRequest.SubscriptionStatus.REMOVE);

        Call<Void> call = client.setSubscription("application/json", cookie, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    synchronizeSubscriptions();
                }
                else {
                    ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_server_connection));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_internet_connection));
            }
        });

    }

    @Override
    public void onStartRepairServiceButtonFired(final int serviceId) {

        new AlertDialog.Builder(getContext())
            .setIcon(R.drawable.wrench_icon)
            .setTitle(R.string.start_alert_title)
            .setMessage(R.string.start_alert_message)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    {
                        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie), null);

                        FixRequest requestBody =
                                new FixRequest(serviceId, true);

                        Call<Void> call = client.setFix("application/json", cookie, requestBody);

                        call.enqueue(new Callback<Void>() {

                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                if (response.code() == 200) {
                                    synchronizeSubscriptions();
                                } else {
                                    ((ToastMessenger) getActivity()).showToast(getString(R.string.bad_server_connection));
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                ((ToastMessenger) getActivity()).showToast(getString(R.string.bad_internet_connection));
                            }
                        });
                    }
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
    @Override
    public void onStopRepairServiceButtonFired(final int serviceId) {

        new AlertDialog.Builder(getContext())
            .setIcon(R.drawable.wrench_2_icon)
            .setTitle(R.string.stop_alert_title)
            .setMessage(R.string.stop_alert_message)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    {
                        String cookie = sharedPref.getString(getString(R.string.shared_preferences_cookie), null);

                        FixRequest requestBody =
                                new FixRequest(serviceId,false);

                        Call<Void> call = client.setFix("application/json", cookie, requestBody);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                if (response.code() == 200) {
                                    synchronizeSubscriptions();
                                }
                                else {
                                    ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_server_connection));
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                ((ToastMessenger)getActivity()).showToast(getString(R.string.bad_internet_connection));
                            }
                        });
                    }
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }

    // ----------------------------------- Fragment Lifecycle ----------------------------------- //
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getContext().getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

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
                adapter = new MyAdapter(new ArrayList<Service>(), this, (new Gson().fromJson(sharedPref.getString("user_data",null), UserData.class)).getUid());
            }
            else {
                List<Service> sites;
                Type listType = new TypeToken<List<Service>>(){}.getType();
                sites = new Gson().fromJson(savedInstanceState.getString("list"), listType);
                adapter = new MyAdapter(sites, this, (new Gson().fromJson(sharedPref.getString("user_data",null), UserData.class)).getUid());

            }

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        synchronizeServices();

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

    /* ========================================================================================== */
}

