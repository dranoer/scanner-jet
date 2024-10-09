package com.gulofy.scannerjet.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.ads.AdView;
import com.gulofy.scannerjet.R;
import com.gulofy.scannerjet.utils.AdsUtils;


public class PrivacyPolicyActivity extends BaseActivity {
    protected WebView web;
    private AdView adView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_privacy_policy);
//        adView = findViewById(R.id.adView);
//        AdsUtils.showGoogleBannerAd(this, adView);

        this.web = (WebView) findViewById(R.id.webView);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.web.loadUrl("https://gulo.carrd.co/");
    }
}
