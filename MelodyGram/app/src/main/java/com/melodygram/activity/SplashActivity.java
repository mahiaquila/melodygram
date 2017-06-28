package com.melodygram.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.melodygram.R;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.services.ContactUpdateSyncService;
import com.melodygram.shortcutbadger.ShortcutBadger;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.JsonUtil;

import io.fabric.sdk.android.Fabric;

/**
 * Created by LALIT on 22-06-2016.
 */
public class SplashActivity extends MelodyGramActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen_layout);
        new CountryInit().execute();

        boolean flag = SharedPreferenceDB.defaultInstance().getCheckLoginFromSplash(SplashActivity.this);
        boolean isFirstTimeLogin = SharedPreferenceDB.defaultInstance().getCheckLogin(SplashActivity.this);

        try {

            if (flag) {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {


                  if(isFirstTimeLogin)
                  {
                       String mobileNumber = SharedPreferenceDB.defaultInstance().getMobileNumber(SplashActivity.this);
                       String verificationId = SharedPreferenceDB.defaultInstance().getVerificationId(SplashActivity.this);
                       String userID = SharedPreferenceDB.defaultInstance().getUserid(SplashActivity.this);

                      Intent intent = new Intent(this, OTPVerification.class);
                      intent.putExtra("verification_id",verificationId);
                      intent.putExtra("user_id", userID);
                      intent.putExtra("mobile", mobileNumber);
                      intent.putExtra("code", "");
                      startActivity(intent);

                      finish();

                  }else {

                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                              startActivity(intent);
                              finish();
                          }

                      }, 2000);
                  }

            }

        }catch (Exception e)
        {
            e.printStackTrace();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }, 2000);
        }
    }

    private class CountryInit extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            JsonUtil.initializeCountryArrayList(SplashActivity.this);
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }





}
