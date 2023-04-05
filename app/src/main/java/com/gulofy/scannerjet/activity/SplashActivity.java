package com.gulofy.scannerjet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.gulofy.scannerjet.R;
import com.gulofy.scannerjet.language.LocaleUtils;
import com.gulofy.scannerjet.main_utils.Constant;

public class SplashActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash_acvtivity);
        Constant.context=this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("MyLangPref", MODE_PRIVATE);
                boolean isFirstTime = sharedPreferences.getBoolean(LocaleUtils.LANGUAGE_SELECTED_FIRST_TIME, false);
                Intent intent;
                if (isFirstTime) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                    intent.putExtra("isFromMain",false);
                }
                startActivity(intent);
                finish();
            }
        },1500);
    }
}
