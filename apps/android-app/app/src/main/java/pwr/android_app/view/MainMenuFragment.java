package pwr.android_app.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pwr.android_app.R;

public class MainMenuFragment extends Fragment {

    /* ========================================== DATA ========================================== */

    /* ========================================= METHODS ======================================== */
    // --- CONSTRUCTOR --- //
    public MainMenuFragment() {
        // Required empty public constructor
    }

    // --- ON CREATE VIEW --- //
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        // Inflate the layout for this fragment
        return view;
    }

}
