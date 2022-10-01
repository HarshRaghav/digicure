package android.example.health.fragments.covid19;

import android.app.ProgressDialog;
import android.example.health.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class Covid19FAQSFragment extends Fragment {

    private WebView covidWebView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.covid_19_faqs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String doc_path = "<iframe width=\"100%\" height=\"100%\" allow=\"microphone;\" src=\"https://console.dialogflow.com/api-client/demo/embedded/935fb421-b781-4e9f-96a2-ba28534b014f\"></iframe>";
        rootView = view;
        covidWebView = rootView.findViewById(R.id.covid_webView);
        covidWebView.clearCache(true);
        covidWebView.getSettings().setJavaScriptEnabled(true);
        covidWebView.getSettings().setAllowFileAccess(true);
        covidWebView.getSettings().setDisplayZoomControls(true);
        covidWebView.getSettings().setDomStorageEnabled(true);
        //webView.setHorizontalScrollBarEnabled(true);
        covidWebView.getSettings().setSupportZoom(true);
        covidWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        covidWebView.getSettings().setBuiltInZoomControls(true);
        covidWebView.getSettings().setLoadWithOverviewMode(true);
        covidWebView.setWebViewClient(new AppWebViewClients());
        covidWebView.loadData(doc_path, "text/html", "UTF-8");

    }

    public class AppWebViewClients extends WebViewClient {
        ProgressDialog progressDoalog;

        public AppWebViewClients() {
            progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Document is loading....");
            progressDoalog.setTitle("Please wait");
            progressDoalog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return super.shouldOverrideUrlLoading(view, url);
           /* view.loadUrl(url);
            return true;*/
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            if (progressDoalog != null && progressDoalog.isShowing()) {
                progressDoalog.dismiss();
            }
        }
    }

}
