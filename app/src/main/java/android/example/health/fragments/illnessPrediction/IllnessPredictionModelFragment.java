package android.example.health.fragments.illnessPrediction;

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

public class IllnessPredictionModelFragment extends Fragment{

    private WebView illnessWebView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.illness_prediction_model_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        String doc_path = "<iframe width=\"100%\" height=\"100%\" allow=\"microphone;\" src=\"https://console.dialogflow.com/api-client/demo/embedded/3aa44934-3d00-4f42-8d52-22a3331fb444\"></iframe>";
        illnessWebView = rootView.findViewById(R.id.illness_webView);
        illnessWebView.getSettings().setJavaScriptEnabled(true);
        illnessWebView.getSettings().setAllowFileAccess(true);
        illnessWebView.getSettings().setDisplayZoomControls(true);
        illnessWebView.getSettings().setDomStorageEnabled(true);
        //webView.setHorizontalScrollBarEnabled(true);
        illnessWebView.getSettings().setSupportZoom(true);
        illnessWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        illnessWebView.getSettings().setBuiltInZoomControls(true);
        illnessWebView.getSettings().setLoadWithOverviewMode(true);
        illnessWebView.setWebViewClient(new AppWebViewClients());
        illnessWebView.loadData(doc_path, "text/html", "UTF-8");
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
