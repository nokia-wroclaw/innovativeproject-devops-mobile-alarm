package pwr.android_app.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pwr.android_app.R;
import pwr.android_app.dataStructures.DummyContent;
import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.view.adapters.MyItemRecyclerViewAdapter;

public class MonitorFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public MonitorFragment() { }

    @SuppressWarnings("unused")
    public static MonitorFragment newInstance(int columnCount) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));

            Handler mleko = new Handler();
            mleko.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Test funkcji edytujących listę

                    //for (int i = 0; i < 2; i++) {
                    //    ((MyItemRecyclerViewAdapter)recyclerView.getAdapter()).addService(new ServiceData(i+5, "a", "b", 0));
                    //}

                    for (int i = 2; i < 4; i++) {
                        ((MyItemRecyclerViewAdapter) recyclerView.getAdapter()).removeService(i);
                    }
                }
            }, 3000);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ServiceData item);
    }
}
