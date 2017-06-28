package com.melodygram.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.singleton.AppController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LALIT on 25-06-2016.
 */
public class OtherUserProfileActivity extends MelodyGramActivity implements View.OnClickListener {
    private Bundle bundle;
    private TextView blockText;
    private AppController appController;
    private String blockValue;
    AppContactDataSource appContactDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_profile_layout);
        initView();

    }

    private void initView() {
        appContactDataSource = new AppContactDataSource(getApplicationContext());
        // TextView userName = (TextView) findViewById(R.id.header_name);
        TextView appName = (TextView) findViewById(R.id.name_edit_text);
        TextView statusText = (TextView) findViewById(R.id.status_edit_text);
        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        ImageView imageHolder = (ImageView) findViewById(R.id.profile_image);
        imageHolder.setOnClickListener(this);
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        blockText = (TextView) findViewById(R.id.block_text);
        blockText.setOnClickListener(this);
        bundle = getIntent().getExtras().getBundle("bundle");
        appName.setText(bundle.getString("frendsAppName"));
        String statusPrivacy = bundle.getString("status_privacy");

        if (statusPrivacy != null && statusPrivacy.equalsIgnoreCase("0")) {
            statusText.setText(bundle.getString("status"));
        }else if (statusPrivacy != null && statusPrivacy.equalsIgnoreCase("2"))
        {
            appContactDataSource.open();
            if(appContactDataSource.isContactAvailableOrNot(bundle.getString("frendsNo")))
            {
                statusText.setText(bundle.getString("status"));
            }else
            {
                statusText.setText(bundle.getString(""));
            }
            appContactDataSource.close();
        }else
        {
            statusText.setText(bundle.getString(""));
        }
        String profilePicPrivacy = bundle.getString("profile_privacy");
        FriendsDataSource friendsDataSource = new FriendsDataSource(OtherUserProfileActivity.this);
        friendsDataSource.open();
        blockValue = friendsDataSource.getBlockValue(bundle.getString("contactsRoomId"));
        friendsDataSource.close();
        phoneNumber.setText("+" + bundle.getString("countryCode") + bundle.getString("frendsNo"));
        appController = AppController.getInstance();
        if (profilePicPrivacy != null && profilePicPrivacy.equalsIgnoreCase("0")) {
            appController.displayUrlImage(imageHolder, bundle.getString("friendProfilePicUrl"), null);
        }  if (profilePicPrivacy != null && profilePicPrivacy.equalsIgnoreCase("2"))
        {
             appContactDataSource.open();
            if(appContactDataSource.isContactAvailableOrNot(bundle.getString("frendsNo")))
            {
                appController.displayUrlImage(imageHolder, bundle.getString("friendProfilePicUrl"), null);
            }else
            {
               // imageHolder.setBackgroundResource(R.drawable.default_profile_pic);
            }
            appContactDataSource.close();
        }else
        {
           // imageHolder.setBackgroundResource(R.drawable.default_profile_pic);
        }
        setBlockValue();
    }
    private void setBlockValue() {
        if (blockValue != null && blockValue.equalsIgnoreCase("1")) {
            blockText.setText(getResources().getString(R.string.unblock));
        } else {
            blockText.setText(getResources().getString(R.string.block));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;
            case R.id.profile_image:
                Intent intent = new Intent(this, PicViewerActivity.class);
                intent.putExtra("imageURL", bundle.getString("friendProfilePicUrl"));
                startActivity(intent);
                break;
            case R.id.block_text:
                if (blockValue != null && blockValue.equalsIgnoreCase("1")) {
                    blockUser("0");
                } else {
                    blockUser("1");
                }
                break;
        }
    }

    private void blockUser(final String value) {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("value", value);
            objJson.accumulate("userid", appController.getUserId());
            objJson.accumulate("type", "block");
            objJson.accumulate("to_userid", bundle.getString("userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.BLOCK_USER, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        dialog.dismiss();
                        if (response != null) {
                            try {
                                if (response.getString("status").equalsIgnoreCase("success")) {
                                    blockValue = value;
                                    setBlockValue();
                                    FriendsDataSource friendsDataSource = new FriendsDataSource(OtherUserProfileActivity.this);
                                    friendsDataSource.open();
                                    friendsDataSource.updateBlock(bundle.getString("userId"), value);
                                    friendsDataSource.close();
                                } else {
                                    Toast.makeText(OtherUserProfileActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(OtherUserProfileActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(OtherUserProfileActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(OtherUserProfileActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
        appController.addToRequestQueue(jsonObjReq);
    }
}
