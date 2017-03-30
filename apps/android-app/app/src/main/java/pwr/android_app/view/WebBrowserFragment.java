package pwr.android_app.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import pwr.android_app.R;

public class WebBrowserFragment extends Fragment {

    /* ========================================== DATA ========================================== */
    // UI references
    WebView myWebView = null;
    View view = null;

    /* ========================================= METHODS ======================================== */
    // --- CONSTRUCTOR --- //
    public WebBrowserFragment() {
        // Required empty public constructor
    }

    // --- ON CREATE VIEW --- //
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_web_browser, container, false);
        myWebView = (WebView) view.findViewById(R.id.webBrowserView);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(getString(R.string.base_url));

        // Inflate the layout for this fragment
        return view;
    }

    // --- ON SAVE INSTANCE STATE --- //
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("URL",myWebView.getUrl());
    }

    // --- ON ACTIVITY CREATED --- //
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            myWebView.loadUrl(savedInstanceState.getString("URL"));
        }
    }

    // --- ON RESUME --- //
    @Override
    public void onResume(){
        super.onResume();
    }
}
