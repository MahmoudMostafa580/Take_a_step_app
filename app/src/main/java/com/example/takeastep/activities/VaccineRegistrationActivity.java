package com.example.takeastep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityVaccineRegistrationBinding;

public class VaccineRegistrationActivity extends AppCompatActivity {
    ActivityVaccineRegistrationBinding vaccineRegistrationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vaccineRegistrationBinding=ActivityVaccineRegistrationBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(vaccineRegistrationBinding.getRoot());

        WebSettings webSettings = vaccineRegistrationBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);

        vaccineRegistrationBinding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(VaccineRegistrationActivity.this,
                        error.getErrorCode() +"\n"+error.getDescription().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        final Activity activity = this;
        vaccineRegistrationBinding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                activity.setProgress(newProgress*1000);
            }
        });

        Intent intent=getIntent();
        String countryUrl=intent.getStringExtra("countryUrl");
        vaccineRegistrationBinding.webView.loadUrl(countryUrl);
        Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show();
    }
}