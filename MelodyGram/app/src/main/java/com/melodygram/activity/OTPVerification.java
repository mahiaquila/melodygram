package com.melodygram.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.services.AppUpdateIntentService;
import com.melodygram.services.ContactUpdateSyncService;
import com.melodygram.services.GCMHelper;
import com.melodygram.services.UpdateMusicServices;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.FileUtil;
import com.melodygram.utils.LogD;

import org.json.JSONException;
import org.json.JSONObject;


public class OTPVerification extends MelodyGramActivity implements View.OnClickListener
{
    private EditText otpNumber;
    private final boolean printLog = true;
    private static final String TAG = "OTPVerification";
    private String userId, verificationId, mobile, code;
    private Dialog dialog;
    private JSONObject response;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verfication);
        initialization();
    }
    private void initialization()
    {

        Bundle bundle = getIntent().getExtras();
        dialog = AppController.getInstance().getLoaderDialog(this);
        verificationId = SharedPreferenceDB.defaultInstance().getVerificationId(OTPVerification.this);
        userId = bundle.getString("user_id");
        mobile = bundle.getString("mobile");
        code = bundle.getString("code");
        otpNumber = (EditText) findViewById(R.id.otp);
        otpNumber.setText(verificationId);
        Button verifyButton = (Button) findViewById(R.id.verify_button);
        TextView backLogin = (TextView) findViewById(R.id.back_login);
        backLogin.setText(Html.fromHtml("<u>Back to Login</u>"));
        TextView resendOTP = (TextView) findViewById(R.id.resend_otp);
        resendOTP.setText(Html.fromHtml("<u>Resend OTP</u>"));
        verifyButton.setOnClickListener(this);
        backLogin.setOnClickListener(this);
        resendOTP.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_button:
                validateOTP();
                break;
            case R.id.back_login:
                Intent intent = new Intent(OTPVerification.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.resend_otp:
                reSendOTP();
                break;
        }
    }
    private void validateOTP()
    {
        String otp = otpNumber.getText().toString();
        verificationId = SharedPreferenceDB.defaultInstance().getVerificationId(OTPVerification.this);
        if (otp.equalsIgnoreCase(verificationId)) {
            getVerfied();

        } else {
            otpNumber.setError(getString(R.string.not_valid_otp));
        }

    }

    private void getVerfied()
    {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("verification", otpNumber.getText().toString());
            objJson.accumulate("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.OTP_VERIFY, objJson,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        LogD.i(TAG, response.toString(), printLog);
                        if (response != null) {
                            OTPVerification.this.response =response;
                            parseResponse();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(OTPVerification.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(OTPVerification.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();

                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void parseResponse()
    {
        try {
            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveLoginFirtTimeFromSplas(OTPVerification.this,true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent contactsUpdateService = new Intent(this, ContactUpdateSyncService.class);
                            startService(contactsUpdateService);
                            updateSharePref();
                        } else {
                            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                            }

                        }
                    } else {
                        updateSharePref();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void reSendOTP() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("country_code",SharedPreferenceDB.defaultInstance().getCountryCode(OTPVerification.this));
            objJson.accumulate("mobile", mobile);
            objJson.accumulate("user_id", new SharedPreferenceDB().getUserid(OTPVerification.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.RESENT_OTP, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.v("Login response", response.toString());
                        if (response != null) {
                            parseResendOTPResponse(response);
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(OTPVerification.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(OTPVerification.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();

                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void parseResendOTPResponse(JSONObject response)
    {
        try {
            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    verificationId = response.getJSONObject("Result").getString("verification_id");

                    SharedPreferenceDB.defaultInstance().saveVerificationId(OTPVerification.this,verificationId);
                   // otpNumber.setText(verificationId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateSharePref();
                } else {

                    Toast.makeText(OTPVerification.this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                return;


        }
    }

    private void updateSharePref() {
        try {
            SharedPreferences sharedPreferences = AppController.getInstance().getPrefs();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONObject resultobj = response.getJSONObject("Result");
            editor.putString("user_id", resultobj.getString("user_id"));
            editor.putString("profile_name", resultobj.getString("profile_name"));
            editor.putString("mobile", resultobj.getString("mobile"));
            editor.putString("status", resultobj.getString("status"));
            editor.putString("country_code", resultobj.getString("country_code"));
            editor.putString("lastseen", resultobj.getString("lastseen"));
            editor.putString("pic", resultobj.getString("pic"));
            editor.putBoolean("isLogged", true);
            editor.commit();
            GCMHelper gcmHelper = new GCMHelper(OTPVerification.this, resultobj.getString("user_id"));
            gcmHelper.gcmRegisteration();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FileUtil.createFolder(GlobalState.PARENT_FOLDER);
        FileUtil.createFolder(GlobalState.PROFILE_PIC);
        FileUtil.createFolder(GlobalState.PROFILE_PIC_CAMERA);
        FileUtil.createFolder(GlobalState.ONE_TO_ONE_VIDEO);
        FileUtil.createFolder(GlobalState.CAMERA_ONE_TO_ONE_PHOTO);
        FileUtil.createFolder(GlobalState.ONE_TO_ONE_AUDIO);
        FileUtil.createFolder(GlobalState.RECORDED_AUDIO);
        FileUtil.createVideoFolder();


        Intent intent = new Intent(OTPVerification.this, DashboardActivity.class);
        startActivity(intent);
        finish();

    }


}
