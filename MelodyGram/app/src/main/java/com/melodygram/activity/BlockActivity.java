package com.melodygram.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.adapter.BlockAdapter;
import com.melodygram.chatinterface.FriendUpdateInterface;
import com.melodygram.constants.APIConstant;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.model.FriendsModel;
import com.melodygram.singleton.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LALIT on 15-07-2016.
 */
public class BlockActivity extends MelodyGramActivity implements View.OnClickListener ,FriendUpdateInterface{
private ListView blockListView;
    private List<FriendsModel> chatList;
    private  BlockAdapter blockAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.block_layout);
        chatList = new ArrayList<>();
        ((TextView) findViewById(R.id.header_name)).setText(R.string.blocked_list);
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        blockListView = (ListView) findViewById(R.id.block_list);
        getLocalList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.header_back_button_parent:
                finish();
                break;
        }
    }

    private void getLocalList() {
        FriendsDataSource friendsDataSource = new FriendsDataSource(BlockActivity.this);
        friendsDataSource.open();
        List localList = friendsDataSource.getAllBlockedFriends();
        chatList.clear();
        chatList.addAll(localList);
        refreshListView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void refreshListView() {
        if (blockAdapter == null) {
            blockAdapter = new BlockAdapter(chatList,BlockActivity.this);
            blockListView.setAdapter(blockAdapter);
        } else {
            blockAdapter.notifyDataSetChanged();
        }

        if(chatList.size() ==0)
        {
            Toast.makeText(BlockActivity.this, getResources().getString(R.string.not_blocked), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteFriend(int position) {
       FriendsModel friendsModel = chatList.get(position);
        unBlockUser(friendsModel.getFriendsUserId());
    }

    @Override
    public void updateTimestamp(int position) {

    }

    private void unBlockUser(final String friendId) {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("value", 0);
            objJson.accumulate("userid", AppController.getInstance().getUserId());
            objJson.accumulate("type", "block");
            objJson.accumulate("to_userid", friendId);
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
                                    FriendsDataSource friendsDataSource = new FriendsDataSource(BlockActivity.this);
                                    friendsDataSource.open();
                                    friendsDataSource.updateBlock(friendId,"0");
                                    friendsDataSource.close();
                                    getLocalList();
                                } else {
                                    Toast.makeText(BlockActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(BlockActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(BlockActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(BlockActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
