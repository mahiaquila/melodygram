package com.melodygram.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.services.GCMHelper;
import com.melodygram.singleton.AppController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LALIT on 19-07-2016.
 */
public class ChangeNumberActivity extends MelodyGramActivity implements View.OnClickListener {

    private EditText oldNumber, newNumber, verficationCode;
    private TextView oldCode, newCode;
    private LinearLayout otpParent;
    private AppController appController;
    public final int COUNTRY_SELECTION_RESULT = 100;
    private String code,otpCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.change_number_layout);
        intitView();
    }

    private void intitView() {
        appController = AppController.getInstance();
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        ((TextView) findViewById(R.id.header_name)).setText(R.string.settings);
        oldNumber = (EditText) findViewById(R.id.old_number);
        oldCode = (TextView) findViewById(R.id.old_number_code);
        verficationCode = (EditText) findViewById(R.id.verification_code);
        newNumber = (EditText) findViewById(R.id.new_number);
        newCode = (TextView) findViewById(R.id.new_number_code);
        newCode.setOnClickListener(this);
        verficationCode = (EditText) findViewById(R.id.verification_code);
        findViewById(R.id.change_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);

        otpParent = (LinearLayout) findViewById(R.id.otp_parent);
        oldNumber.setText(appController.getPrefs().getString("mobile", ""));
        oldCode.setText(appController.getPrefs().getString("country_code", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;
            case R.id.change_button:
                String finalNumber = newNumber.getText().toString();
                String newCodeFinal = newCode.getText().toString();
                if(newCodeFinal.isEmpty() || !isValidMobileNo(finalNumber))
                {
                    if (!isValidMobileNo(finalNumber)) {
                        newNumber.setError("");
                    }
                    if (newCodeFinal.isEmpty() ) {
                        newCode.setError("");
                    }
                }
                else
                {
                    changeNumber();
                }
                break;
            case R.id.submit_button:
               String userVerficationCode  = verficationCode.getText().toString();
                if(userVerficationCode.equalsIgnoreCase(otpCode))
                {
                        SharedPreferences sharedPreferences = AppController.getInstance().getPrefs();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLogged", false);
                        editor.commit();

                    Toast.makeText(ChangeNumberActivity.this,"Sucess",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ChangeNumberActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(ChangeNumberActivity.this,getResources().getString(R.string.not_valid_otp),Toast.LENGTH_SHORT).show();
                   // verficationCode.setText(getResources().getString(R.string.not_valid_otp));
                }
                break;
            case R.id.new_number_code:
                Intent countryIntent = new Intent(this, CountryActivity.class);
                startActivityForResult(countryIntent, COUNTRY_SELECTION_RESULT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case COUNTRY_SELECTION_RESULT:
                    code = data.getExtras().getString("code");
                    newCode.setText("+" + code);
                    newCode.setError(null);
                    break;
            }
        }
    }


    private void changeNumber() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("mobile", newNumber.getText().toString());
            objJson.accumulate("country_code",  newCode.getText().toString());
            objJson.accumulate("user_id", new SharedPreferenceDB().getUserid(ChangeNumberActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.CHANGE_NUMBER, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        if (response != null) {
                            parseResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(ChangeNumberActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ChangeNumberActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();

                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }



    private void comfirmOtp() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("mobile", newNumber.getText().toString());
            objJson.accumulate("country_code", code);
            objJson.accumulate("user_id", new SharedPreferenceDB().getUserid(ChangeNumberActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.OTP_VERIFY_NUMBER_CHANGE, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        if (response != null) {
                            parseResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(ChangeNumberActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ChangeNumberActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();

                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }


    private void parseResponse(JSONObject response) {
        try {
            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    otpCode = response.getString("verification_id");
                  //  verficationCode.setText(otpCode);
                    findViewById(R.id.change_button).setVisibility(View.GONE);
                    otpParent.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidMobileNo(String phoneNo) {
        if (phoneNo != null) {
            int emailLength = phoneNo.length();
            if (emailLength < 8) {
                return false;
            }
        }
        return true;
    }



}
