package com.melodygram.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.singleton.AppController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LALIT on 15-07-2016.
 */
public class PrivacyActivity extends MelodyGramActivity implements View.OnClickListener {


    private Dialog commonDialog;
    private TextView dialogHeader, lastSeenStatus, profilePhotoStatus, statusStatus,readReceiptStatus;
    private String DIALOG_OPERATION, privacyValue;
    private RadioButton everybodyButton, nobodyButton,contactOnly;
    private SharedPreferences sharedPreferences;
    private AppController appController;
    String readReceiptValue;
    boolean isCheckReadReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_layout);
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        ((TextView) findViewById(R.id.header_name)).setText(R.string.settings);
        lastSeenStatus = (TextView) findViewById(R.id.last_seen_status);
        profilePhotoStatus = (TextView) findViewById(R.id.profile_photo_status);
        statusStatus = (TextView) findViewById(R.id.status_status);
        readReceiptStatus=(TextView)findViewById(R.id.read_receipt_status);

        findViewById(R.id.last_seen_parent).setOnClickListener(this);
        findViewById(R.id.profile_parent).setOnClickListener(this);
        findViewById(R.id.status_parent).setOnClickListener(this);
        findViewById(R.id.read_receipt_parrent).setOnClickListener(this);
        appController = AppController.getInstance();
        sharedPreferences = appController.getPrefs();
        initPrivacy();
        initPopupDialog();
    }


    private void initPrivacy() {

        if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0"))
        {
            readReceiptStatus.setText("off");
            isCheckReadReceipt=true;
        }else
        {
            readReceiptStatus.setText("on");
            isCheckReadReceipt=false;
        }


        if(sharedPreferences.getString("last_seen_privacy", "0").equalsIgnoreCase("0"))
        {
            lastSeenStatus.setText(getResources().getString(R.string.everybody));
        }
        else if(sharedPreferences.getString("last_seen_privacy", "0").equalsIgnoreCase("1"))
        {
            lastSeenStatus.setText(getResources().getString(R.string.nobody));
        }else
        {
            lastSeenStatus.setText(getResources().getString(R.string.contact));
        }

        if(sharedPreferences.getString("profile_photo_privacy", "0").equalsIgnoreCase("0"))
        {
            profilePhotoStatus.setText(getResources().getString(R.string.everybody));
        }
        else   if(sharedPreferences.getString("profile_photo_privacy", "0").equalsIgnoreCase("1"))
        {
            profilePhotoStatus.setText(getResources().getString(R.string.nobody));
        }
        else
        {
            profilePhotoStatus.setText(getResources().getString(R.string.contact));
        }

        if(sharedPreferences.getString("status_privacy", "0").equalsIgnoreCase("0"))
        {
            statusStatus.setText(getResources().getString(R.string.everybody));
        }
        else   if(sharedPreferences.getString("status_privacy", "0").equalsIgnoreCase("1"))
        {    statusStatus.setText(getResources().getString(R.string.nobody));
        } else
        {
            statusStatus.setText(getResources().getString(R.string.contact));
        }
    }

    private void initPopupDialog() {
        commonDialog = new Dialog(PrivacyActivity.this);
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commonDialog.setContentView(R.layout.privacy_dialog);
        dialogHeader = (TextView) commonDialog.findViewById(R.id.header_name);
        TextView dialogOkButton = (TextView) commonDialog.findViewById(R.id.dialog_ok_button);
        dialogOkButton.setOnClickListener(this);
        TextView dialogCancelButton = (TextView) commonDialog.findViewById(R.id.dialog_cancel_button);
        dialogCancelButton.setOnClickListener(this);
        everybodyButton = (RadioButton) commonDialog.findViewById(R.id.everybody_button);
        everybodyButton.setOnClickListener(this);
        nobodyButton = (RadioButton) commonDialog.findViewById(R.id.nobody_button);
        nobodyButton.setOnClickListener(this);
        contactOnly=(RadioButton)commonDialog.findViewById(R.id.contact_only_button);
        contactOnly.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;
            case R.id.read_receipt_parrent:
                DIALOG_OPERATION="READRECEIPT";
                 if(isCheckReadReceipt)
                 {
                     isCheckReadReceipt=false;
                     readReceiptStatus.setText("off");
                     privacyValue ="1";
                 }else
                 {
                     isCheckReadReceipt=true;
                      privacyValue ="0";
                     readReceiptStatus.setText("on");
                 }
                updatePrivacy("read_receipts");
                break;

            case R.id.last_seen_parent:
                DIALOG_OPERATION = "LASTSEEN";
                dialogHeader.setText(R.string.last_seen);
                commonDialog.show();

                if (sharedPreferences.getString("last_seen_privacy", "0").equalsIgnoreCase("0")) {
                    everybodyButton.setChecked(true);
                    nobodyButton.setChecked(false);
                    contactOnly.setChecked(false);
                } else  if (sharedPreferences.getString("last_seen_privacy", "0").equalsIgnoreCase("1")) {
                    nobodyButton.setChecked(true);
                    everybodyButton.setChecked(false);
                    contactOnly.setChecked(false);
                }else
                {
                    nobodyButton.setChecked(false);
                    everybodyButton.setChecked(false);
                    contactOnly.setChecked(true);
                }
                break;
            case R.id.profile_parent:
                if (sharedPreferences.getString("profile_photo_privacy", "0").equalsIgnoreCase("0")) {
                    everybodyButton.setChecked(true);
                    nobodyButton.setChecked(false);
                    contactOnly.setChecked(false);
                } else if (sharedPreferences.getString("profile_photo_privacy", "0").equalsIgnoreCase("1")){
                    everybodyButton.setChecked(false);
                    nobodyButton.setChecked(true);
                    contactOnly.setChecked(false);
                }else
                {
                    nobodyButton.setChecked(false);
                    everybodyButton.setChecked(false);
                    contactOnly.setChecked(true);
                }
                DIALOG_OPERATION = "PROFILEPIC";
                dialogHeader.setText(R.string.profile_photo);
                commonDialog.show();
                break;
            case R.id.status_parent:
                if (sharedPreferences.getString("status_privacy", "0").equalsIgnoreCase("0")) {
                    everybodyButton.setChecked(true);
                    nobodyButton.setChecked(false);
                    contactOnly.setChecked(false);
                } else  if (sharedPreferences.getString("status_privacy", "0").equalsIgnoreCase("1")) {
                    nobodyButton.setChecked(true);
                    everybodyButton.setChecked(false);
                    contactOnly.setChecked(false);
                }else
                {
                    nobodyButton.setChecked(false);
                    everybodyButton.setChecked(false);
                    contactOnly.setChecked(true);
                }
                DIALOG_OPERATION = "STATUS";
                dialogHeader.setText(R.string.status);
                commonDialog.show();
                break;
            case R.id.dialog_ok_button:
                if (DIALOG_OPERATION.equalsIgnoreCase("LASTSEEN")) {
                    if (everybodyButton.isChecked()) {
                        privacyValue = "0";
                    } else if(nobodyButton.isChecked()) {
                        privacyValue = "1";
                    }else
                    {
                        privacyValue = "2";
                    }

                    updatePrivacy("last_seen");

                    //sharedPreferences.edit().putString("last_seen_privacy", privacyValue).commit();
                } else if (DIALOG_OPERATION.equalsIgnoreCase("PROFILEPIC")) {
                    if (everybodyButton.isChecked()) {
                        privacyValue = "0";
                    } else  if(nobodyButton.isChecked()) {
                        privacyValue = "1";
                    }else
                    {
                        privacyValue = "2";
                    }
                    updatePrivacy("profile_pic");
                   // sharedPreferences.edit().putString("profile_photo_privacy", privacyValue).commit();
                } else if (DIALOG_OPERATION.equalsIgnoreCase("STATUS")) {
                    if (everybodyButton.isChecked()) {
                        privacyValue = "0";
                    } else if(nobodyButton.isChecked()) {
                        privacyValue = "1";
                    }else
                    {
                        privacyValue = "2";
                    }
                    updatePrivacy("status");
                //    sharedPreferences.edit().putString("status_privacy", privacyValue).commit();
                }
                commonDialog.dismiss();
                break;

            case R.id.dialog_cancel_button:
                commonDialog.dismiss();
                break;
            case R.id.everybody_button:
                everybodyButton.setChecked(true);
                nobodyButton.setChecked(false);
                contactOnly.setChecked(false);
                break;
            case R.id.nobody_button:
                nobodyButton.setChecked(true);
                everybodyButton.setChecked(false);
                contactOnly.setChecked(false);
                break;
            case R.id.contact_only_button:
                nobodyButton.setChecked(false);
                everybodyButton.setChecked(false);
                contactOnly.setChecked(true);
                break;
        }
    }

    private void updatePrivacy(String type) {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("value", privacyValue);
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("type",type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();

        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.PRIVACY_SETTINGS, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());

                        dialog.dismiss();
                        if (response != null) {
                            try {
                                if (response.getString("status").equalsIgnoreCase("success")) {
                                    if (DIALOG_OPERATION.equalsIgnoreCase("LASTSEEN")) {
                                        sharedPreferences.edit().putString("last_seen_privacy", privacyValue).commit();
                                    } else if (DIALOG_OPERATION.equalsIgnoreCase("PROFILEPIC")) {
                                        sharedPreferences.edit().putString("profile_photo_privacy", privacyValue).commit();
                                    } else if (DIALOG_OPERATION.equalsIgnoreCase("STATUS")) {
                                        sharedPreferences.edit().putString("status_privacy", privacyValue).commit();
                                    }else  if (DIALOG_OPERATION.equalsIgnoreCase("READRECEIPT"))
                                    {
                                        sharedPreferences.edit().putString("read_receipt", privacyValue).commit();
                                    }
                                    initPrivacy();
                                } else {
                                    Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
        appController.addToRequestQueue(jsonObjReq);
    }
}
