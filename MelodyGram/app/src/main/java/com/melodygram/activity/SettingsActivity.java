package com.melodygram.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.asyncTask.MailConversationShareUtil;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LALIT on 14-06-2016.
 */
public class SettingsActivity extends MelodyGramActivity implements View.OnClickListener {

    private Dialog commonDialog, disappear;
    private Dialog mediaAutoDownlaod;
    private TextView dialogHeader, dialogMessage;
    private String DIALOG_OPERATION;
    private RadioButton onButton, offButton;
    private AppController appController;
    private RadioButton chkConnectToMobileData;
    private RadioButton chkConnectToWifi;
    private RelativeLayout rlayoutMediaAutoDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.settings_layout);
        appController = AppController.getInstance();
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        ((TextView) findViewById(R.id.header_name)).setText(R.string.settings);
        findViewById(R.id.chat_disappear_button).setOnClickListener(this);
        findViewById(R.id.clear_chat_button).setOnClickListener(this);
        findViewById(R.id.email_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
        findViewById(R.id.notification_button).setOnClickListener(this);
        findViewById(R.id.block_button).setOnClickListener(this);
        findViewById(R.id.privacy_button).setOnClickListener(this);
        findViewById(R.id.change_button).setOnClickListener(this);
        findViewById(R.id.deactivate_button).setOnClickListener(this);
        rlayoutMediaAutoDownload=(RelativeLayout)findViewById(R.id.rlayout_media_auto_download);
        rlayoutMediaAutoDownload.setOnClickListener(this);
        initPopupDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;
            case R.id.chat_disappear_button:
                disappear = new Dialog(SettingsActivity.this);
                disappear.requestWindowFeature(Window.FEATURE_NO_TITLE);
                disappear.setContentView(R.layout.disapper_popup_layout);
                onButton = (RadioButton) disappear.findViewById(R.id.on_button);
                onButton.setChecked(appController.isDisappearActivated());
                onButton.setOnClickListener(this);
                offButton = (RadioButton) disappear.findViewById(R.id.off_button);
                offButton.setOnClickListener(this);

                if (appController.isDisappearActivated())
                    onButton.setChecked(true);
                else
                    offButton.setChecked(true);
                disappear.show();
                break;
            case R.id.on_button:
                offButton.setChecked(false);
                setDisappear(true);
                break;
            case R.id.off_button:
                onButton.setChecked(false);
                setDisappear(false);
                break;

            case R.id.clear_chat_button:
                DIALOG_OPERATION = "CLEARCHAT";
                dialogHeader.setText(getResources().getString(R.string.clear_chat_history));
                dialogMessage.setText(getResources().getString(R.string.chat_message));
                commonDialog.show();
                break;
            case R.id.dialog_ok_button:
                if (DIALOG_OPERATION.equalsIgnoreCase("CLEARCHAT")) {
                    commonDialog.dismiss();
                    new BackGroundAsyncTask().execute();
                } else if (DIALOG_OPERATION.equalsIgnoreCase("DEACTIVATEACCOUNT")) {
                    commonDialog.dismiss();
                    deActivateAccount();
                }
                break;
            case R.id.dialog_cancel_button:
                commonDialog.dismiss();
                break;
            case R.id.email_button:
                new MailConversationShareUtil(SettingsActivity.this).execute();
                break;
            case R.id.about_button:
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                break;
            case R.id.block_button:
                startActivity(new Intent(SettingsActivity.this, BlockActivity.class));
                break;
            case R.id.notification_button:
                startActivity(new Intent(SettingsActivity.this, NotificationSettingsActivity.class));
                break;
            case R.id.privacy_button:
                startActivity(new Intent(SettingsActivity.this, PrivacyActivity.class));
                break;
            case R.id.change_button:
                startActivity(new Intent(SettingsActivity.this, ChangeNumberActivity.class));
                break;
            case R.id.deactivate_button:
                DIALOG_OPERATION = "DEACTIVATEACCOUNT";
                SharedPreferenceDB.defaultInstance().saveLoginFirstTime(SettingsActivity.this,false);
                dialogHeader.setText(getResources().getString(R.string.deactivate_number));
                dialogMessage.setText(getResources().getString(R.string.deactivate_message));
                commonDialog.show();

                break;
            case R.id.rlayout_media_auto_download:

                  startActivity(new Intent(SettingsActivity.this,MediaAutoDownload.class));


                break;

            case R.id.rlayout_theme:
                startActivity(new Intent(SettingsActivity.this,ThemesActivity.class));
                break;

        }
    }

    private void initPopupDialog()
    {
        commonDialog = CommonUtil.getDialog(SettingsActivity.this);
        dialogHeader = (TextView) commonDialog.findViewById(R.id.header_name);
        dialogMessage = (TextView) commonDialog.findViewById(R.id.message);
        TextView dialogOkButton = (TextView) commonDialog.findViewById(R.id.dialog_ok_button);
        dialogOkButton.setOnClickListener(this);
        TextView dialogCancelButton = (TextView) commonDialog.findViewById(R.id.dialog_cancel_button);
        dialogCancelButton.setOnClickListener(this);
    }

    private void clearChat() {
        FriendsDataSource friendsDataSource = new FriendsDataSource(SettingsActivity.this);
        friendsDataSource.open();
        ArrayList<String> roomList = friendsDataSource.getAllRoomId();
        friendsDataSource.close();
        ChatMessageDataSource messageDataSource = new ChatMessageDataSource(SettingsActivity.this);
        for (int i = 0; i < roomList.size(); i++)
        {
            messageDataSource.open();
            messageDataSource.deleteAllChatMessage(roomList.get(i));
            messageDataSource.close();
        }
    }

    private class BackGroundAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            clearChat();
            return null;
        }
    }

    private void setDisappear(boolean value) {
        appController.getPrefs().edit().putBoolean("diappear", value).commit();
    }


    private void deActivateAccount() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", new SharedPreferenceDB().getUserid(SettingsActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.DELETE_ACCOUNT, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            if (response != null && response.getString("status").equalsIgnoreCase("success")) {
                                SharedPreferences.Editor editor = getSharedPreferences(GlobalState.SHARED_PREF,
                                        Context.MODE_PRIVATE).edit();
                                editor.clear();
                                editor.putString("loginSuccess", null);
                                editor.putBoolean("cleardata", true);
                                editor.commit();
                                deleteDatabase(DbSqliteHelper.DATABASE_NAME);
                                finish();
                                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
