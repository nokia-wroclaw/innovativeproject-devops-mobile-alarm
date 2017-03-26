package pwr.android_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebBrowserFragment extends Fragment {

    /* --- DATA --- */
    WebView myWebView = null;
    View view = null;

    public WebBrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_web_browser, container, false);
        myWebView = (WebView) view.findViewById(R.id.webBrowserView);
        myWebView.loadUrl("https://hidden-coast-29149.herokuapp.com/");


        // Inflate the layout for this fragment
        return view;
    }


    @Override public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("URL",myWebView.getUrl());
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            myWebView.loadUrl(savedInstanceState.getString("URL"));
        }
    }

    @Override public void onResume(){
        super.onResume();

    }
}
