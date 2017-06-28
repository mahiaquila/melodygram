package com.melodygram.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.melodygram.R;
import com.melodygram.adapter.FwdMultipleAdapter;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.model.FriendsModel;
import com.melodygram.services.FwdIntentService;
import com.melodygram.singleton.AppController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by LALIT on 18-07-2016.
 */
public class FwdMultipleActivity extends MelodyGramActivity implements View.OnClickListener {

    private Activity activityRef;
    private ListView appUsersList;
    private LinkedHashMap<String, FriendsModel> toBeSelectList;
    private List<FriendsModel> friendsArrayList;
    private FriendsDataSource friendsDataSource;
    private FwdMultipleAdapter friendsAdapter;

    class AllFriendsTask extends AsyncTask<Void, Void, String> {
        Dialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = AppController.getInstance().getLoaderDialog(FwdMultipleActivity.this);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            friendsDataSource.open();
            friendsArrayList = friendsDataSource.getAllUnDeletedFriends();
            friendsDataSource.close();
            int frndListSize = friendsArrayList.size();
            return "" + frndListSize;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null)
                progressDialog.dismiss();
            refreshFriendsList();
        }
    }

    private void initializeFriendsList() {
        new AllFriendsTask().execute();
    }

    private void refreshFriendsList() {
        if (friendsArrayList != null && friendsArrayList.size() > 0) {
            if (friendsAdapter == null) {
                friendsAdapter = new FwdMultipleAdapter(activityRef,
                        friendsArrayList, toBeSelectList);
                appUsersList.setAdapter(friendsAdapter);
            } else {
                friendsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_forward_layout);
        activityRef = this;
        fwdMultiInitialization();
        fwdMultiFunctionality();
    }

    private void fwdMultiInitialization() {
        findViewById(R.id.multi_done_parent).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        appUsersList = (ListView) findViewById(R.id.appUsersListId);
    }

    private void fwdMultiFunctionality() {
        friendsDataSource = new FriendsDataSource(activityRef);
        toBeSelectList = new LinkedHashMap<>();
        initializeFriendsList();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.multi_done_parent:
                ChatGlobalStates.selectedFriends = new ArrayList<>(
                        toBeSelectList.values());
                if (ChatGlobalStates.selectedFriends.size() > 0) {
                    List<String> keys = new ArrayList<>(
                            toBeSelectList.keySet());
                    if (keys.size() == 1) {
                        String value = keys.get(0);
                        int noOfRoom = 1;
                        String[] roomId = value.split(",");
                        if (roomId.length == 1) {
                            noOfRoom = keys.size();
                        } else {
                            noOfRoom = roomId.length;
                        }
                        Intent fwdIntentService = new Intent(this,
                                FwdIntentService.class);
                        fwdIntentService.putExtra("roomId", value);
                        fwdIntentService.putExtra("noOfRoom", noOfRoom);
                        startService(fwdIntentService);
                        moveToChattingActivity(ChatGlobalStates.selectedFriends.get(0).getFriendsUserId());
                        ChatActivity.chatActivity.disableForward();
                        finish();

                    } else {
                        String joined = TextUtils.join(",", keys);
                        int noOfRoom = keys.size();
                        Intent fwdIntentService = new Intent(this,
                                FwdIntentService.class);
                        fwdIntentService.putExtra("roomId", joined);
                        fwdIntentService.putExtra("noOfRoom",noOfRoom);
                        startService(fwdIntentService);
                        ChatActivity.chatActivity.disableForward();
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }


    private void moveToChattingActivity(String roomId)
    {
        FriendsModel friendsModel;
        FriendsDataSource friendsDataSource = new FriendsDataSource(getApplicationContext());
        friendsDataSource.open();
        friendsModel = friendsDataSource.getFriendsDetails(roomId) ;
        friendsDataSource.close();
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("frendsName", friendsModel.getFriendName());
        intent.putExtra("frendsNo", friendsModel.getFriendsPhoneNumber());
        intent.putExtra("frendsAppName", friendsModel.getFriendAppName());
        intent.putExtra("countryCode",
                friendsModel.getFriendsCountryCode());
        intent.putExtra("chatType",
                friendsModel.getFriendsChatType());
        intent.putExtra(
                "friendProfilePicUrl", friendsModel
                        .getFriendsPicIconUrl());
        intent.putExtra("contactsRoomId", friendsModel.getFriendsRoomId());
        intent.putExtra("friendsLastSeen", friendsModel.getFriendsLastSeen());
        intent.putExtra("userId", friendsModel.getFriendsUserId());
        intent.putExtra("groupUsers",
                friendsModel.getGroupUsers());
        intent.putExtra("groupAdminUserId",
                friendsModel.getFriendsGroupAdminUserId());
        intent.putExtra("friendsIsBlocked", friendsModel.getFriendsChatBlocked());
        intent.putExtra("friendsGroupAdminUserId", friendsModel.getFriendsGroupAdminUserId());
        intent.putExtra("mute", friendsModel.getMute());
        intent.putExtra("status", friendsModel.getFriendsStatus());
        intent.putExtra("profile_privacy", friendsModel.getProfilePicPrivacy());
        intent.putExtra("lastseen_privacy", friendsModel.getLastSeenPrivacy());
        intent.putExtra("status_privacy", friendsModel.getStatusPrivacy());
        intent.putExtra("readReceipt", friendsModel.getReadReceiptsPrivacy());
        GlobalState.READ_RECEIPT=friendsModel.getReadReceiptsPrivacy();

//        if (!friendsModel.getSeenCount().equals("0")) {
//            friendsDataSource.open();
//            friendsDataSource.updateBatchCount(friendsModel.getFriendsRoomId());
//            friendsDataSource.close();
//        }
        startActivity(intent);
    }


}
