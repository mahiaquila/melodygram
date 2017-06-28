package com.melodygram.activity;

import android.app.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.melodygram.R;




/**
 * Created by FuGenX-01 on 10-03-2017.
 */

public class PrivacyPolicyActivity extends Activity {

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        progressBarInitialize();
        WebView browser = (WebView) findViewById(R.id.webview);
        progressDialog.show();

        browser.setWebViewClient(new MyWebViewClient());
        browser.loadUrl("http://themelodygram.com/privacyPolicy.php");

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);



            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

              if(progressDialog!=null && progressDialog.isShowing())
              {
                  progressDialog.dismiss();
              }
        }
    }

    private void progressBarInitialize(){

        progressDialog = ProgressDialog.show(PrivacyPolicyActivity.this, "Loading...", "Please wait");

        progressDialog.setIndeterminate(true);

        progressDialog.setCancelable(false);

    }

}
