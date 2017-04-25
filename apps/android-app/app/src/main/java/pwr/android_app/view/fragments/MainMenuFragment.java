package pwr.android_app.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pwr.android_app.R;

public class MainMenuFragment extends Fragment {

    /* ========================================== DATA ========================================== */



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

        return view;
    }

    /* ========================================================================================== */
}
