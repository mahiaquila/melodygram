package com.melodygram.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.activity.ChatActivity;
import com.melodygram.activity.DashboardActivity;
import com.melodygram.adapter.ChatListAdapter;
import com.melodygram.chatinterface.FriendUpdateInterface;
import com.melodygram.chatinterface.InterFaceBadgeCount;
import com.melodygram.chatinterface.InterfaceChatProfileClick;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.ContactsModel;
import com.melodygram.model.FriendsModel;
import com.melodygram.singleton.AppController;
import com.melodygram.swipelistview.BaseSwipeListViewListener;
import com.melodygram.swipelistview.SwipeListView;
import com.melodygram.utils.DateTimeUtil;
import com.melodygram.view.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by LALIT on 14-06-2016.
 */
public class ChatFragment extends Fragment implements View.OnClickListener, FriendUpdateInterface, InterFaceBadgeCount, InterfaceChatProfileClick {
    private SwipeListView chatListView;
    private List<FriendsModel> chatList;
    private RelativeLayout searchButtonParent;
    private CustomEditText searchText;
    private ChatListAdapter adapter;
    private Activity activity;
    private FriendsDataSource friendsDataSource;
    private AppController appController;
    ImageView chat_search_button;
    RelativeLayout rl_chat_search_button;
    View view;
    ChatMessageDataSource chatMessageDataSource;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_fragment, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {
        appController = AppController.getInstance();
        activity = getActivity();
        chatList = new ArrayList<>();
        friendsDataSource = new FriendsDataSource(activity);
        chatListView = (SwipeListView) view.findViewById(R.id.chat_list);
        chatMessageDataSource = new ChatMessageDataSource(getActivity());

        chat_search_button = (ImageView) view.findViewById(R.id.chat_search_button);
        rl_chat_search_button = (RelativeLayout) view.findViewById(R.id.rl_chat_search_button);
        chat_search_button.setOnClickListener(this);
        // (view.findViewById(R.id.chat_search_button)).setOnClickListener(this);

        searchButtonParent = (RelativeLayout) view.findViewById(R.id.chat_search_parent);
        searchText = (CustomEditText) view.findViewById(R.id.chat_search);
        searchText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String searchString = searchText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                if (adapter != null)
                    adapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chatListView.setSwipeListViewListener(new BaseSwipeListViewListener()
        {

            @Override
            public void onOpened(int position, boolean toRight)
            {

            }

            @Override
            public void onClosed(int position, boolean fromRight)
            {


            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action,
                                    boolean right) {
            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {

                moveToChat(position);
            }

            @Override
            public void onClickBackView(int position) {

            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        searchText.setDrawableClickListener(new CustomEditText.DrawableClickListener()
        {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        searchButtonParent.setVisibility(View.INVISIBLE);
                        chat_search_button.setVisibility(View.VISIBLE);
                        rl_chat_search_button.setVisibility(View.VISIBLE);
                        hideSoftInPutKey();
                        break;

                    default:
                        break;
                }
            }

        });
        getLocalList();
        getServerList();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void refreshListView() {
        if (adapter == null)
        {
            int index = chatListView.getFirstVisiblePosition();
            View v = chatListView.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            adapter = new ChatListAdapter(chatList, getActivity(), this, this, this);
            chatListView.setAdapter(adapter);
            chatListView.setSelectionFromTop(index, top);
        }
        else
        {
            int index = chatListView.getFirstVisiblePosition();
            View v = chatListView.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            adapter.notifyDataSetChanged();
            chatListView.setSelectionFromTop(index, top);

        }
    }
    private void moveToChat(int position)
    {
        Intent intentResponse = new Intent();
        intentResponse.putExtra("count", "0");
        intentResponse.setAction(DashboardActivity.BadgeCountBroadcastReceiver.ACTION);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().sendBroadcast(intentResponse);
        FriendsModel friendsModel = (FriendsModel) adapter.getItem(position);
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("frendsName", friendsModel.getFriendName());
        intent.putExtra("frendsNo", friendsModel.getFriendsPhoneNumber());
        intent.putExtra("frendsAppName", friendsModel.getFriendAppName());
        intent.putExtra("countryCode", friendsModel.getFriendsCountryCode());
        intent.putExtra("chatType", friendsModel.getFriendsChatType());
        intent.putExtra("friendProfilePicUrl", friendsModel.getFriendsPicIconUrl());
        intent.putExtra("contactsRoomId", friendsModel.getFriendsRoomId());
        intent.putExtra("friendsLastSeen", friendsModel.getFriendsLastSeen());
        intent.putExtra("userId", friendsModel.getFriendsUserId());
        intent.putExtra("groupUsers", friendsModel.getGroupUsers());
        intent.putExtra("groupAdminUserId", friendsModel.getFriendsGroupAdminUserId());
        intent.putExtra("friendsIsBlocked", friendsModel.getFriendsChatBlocked());
        intent.putExtra("friendsGroupAdminUserId", friendsModel.getFriendsGroupAdminUserId());
        intent.putExtra("mute", friendsModel.getMute());
        intent.putExtra("status", friendsModel.getFriendsStatus());
        intent.putExtra("profile_privacy", friendsModel.getProfilePicPrivacy());
        intent.putExtra("lastseen_privacy", friendsModel.getLastSeenPrivacy());
        intent.putExtra("status_privacy", friendsModel.getStatusPrivacy());
        intent.putExtra("readReceipt", friendsModel.getReadReceiptsPrivacy());
        GlobalState.READ_RECEIPT = friendsModel.getReadReceiptsPrivacy();


        if (!friendsModel.getSeenCount().equals("0")) {
            friendsDataSource.open();
            friendsDataSource.updateBatchCount(friendsModel.getFriendsRoomId());
            friendsDataSource.close();
        }
        startActivity(intent);
    }

    private void getLocalList() {
        friendsDataSource.open();
        List localList = friendsDataSource.getAllUnDeletedFriends();
        chatList.clear();
        chatList.addAll(localList);
        refreshListView();
    }

    public void getServerList() {
        Log.d("ok", "Get Server List");
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.GET_ALL_FRIENDS, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {
                            parseServerResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    //  Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    getLocalList();
                } else if (error instanceof TimeoutError) {
                    // Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }
    public class LongOperation extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            getServerList();
            return null;
        }
    }

    private void parseServerResponse(JSONObject response) {
        try {
            if (response != null && response.getString("status").equalsIgnoreCase("success") && !response.getString("message").equalsIgnoreCase("No Users")) {
                JSONArray dataList = response.getJSONObject("Result").getJSONArray("Data");
                FriendsDataSource friendsDataSource = new FriendsDataSource(activity);
                friendsDataSource.open();
                chatMessageDataSource.open();
                for (int i = 0; i < dataList.length(); i++) {
                    JSONObject dataObject = (JSONObject) dataList.get(i);
                    JSONObject privacyObject = dataObject.getJSONObject("privacy");
                    JSONObject profileObject = dataObject.getJSONObject("profile");

                      String lastMsgId  = chatMessageDataSource.getLatestMsgId(dataObject.getString("room_id")) ;
                      String lastServerMsgId = dataObject.getString("lastchat");
                    int lastServerMsgIdd=0;
                    int intLastMsgInd=0;
                      try {
                          String lastUndeletedMsgId = chatMessageDataSource.getLatestUnDeletedMsgId(dataObject.getString("room_id"));
                          if(lastServerMsgId.contains("/")) {
                              String lastArray[] = lastServerMsgId.split("/");
                              if (lastArray != null && lastArray[2] != null && lastArray[2].length() > 0) {
                                  lastServerMsgIdd = Integer.parseInt(lastArray[2]);
                                  intLastMsgInd = Integer.parseInt(lastMsgId);
                              }
                          }
                          if (intLastMsgInd < lastServerMsgIdd) {
                              friendsDataSource.createFriends(dataObject.getString("user_id"), dataObject.getString("profile_name"), dataObject.getString("mobile"), dataObject.getString("status"), dataObject.getString("profile_pic"), dataObject.getString("room_id"), dataObject.getString("lastchat"), dataObject.getString("timestamp"), dataObject.getString("type"), dataObject.getString("country_code"), dataObject.getString("lastseen"), dataObject.getString("isonline"), dataObject.getString("unseencount"), profileObject.getString("mutechat"), privacyObject.getString("profile_pic"), privacyObject.getString("last_seen"), privacyObject.getString("status"), privacyObject.getString("online"), dataObject.getString("block"), privacyObject.getString("read_receipts"), dataObject.getString("isblocked"));
                          } else {
                              if (lastUndeletedMsgId == "-1") {
                                  friendsDataSource.deleteRowMessage(dataObject.getString("room_id"));
                              } else {
                                  if (!lastUndeletedMsgId.equalsIgnoreCase(lastMsgId)) {
                                      ChatMessageModel chatMessageModel = chatMessageDataSource.getLatestMsg(dataObject.getString("room_id"));
                                      friendsDataSource.createFriends(dataObject.getString("user_id"), dataObject.getString("profile_name"),
                                              dataObject.getString("mobile"), dataObject.getString("status"),
                                              dataObject.getString("profile_pic"), dataObject.getString("room_id"),
                                              chatMessageModel.getActualMsg(), dataObject.getString("timestamp"),
                                              chatMessageModel.getMessageType(), dataObject.getString("country_code"),
                                              dataObject.getString("lastseen"), dataObject.getString("isonline"),
                                              dataObject.getString("unseencount"), profileObject.getString("mutechat"),
                                              privacyObject.getString("profile_pic"), privacyObject.getString("last_seen"),
                                              privacyObject.getString("status"), privacyObject.getString("online"),
                                              dataObject.getString("block"), privacyObject.getString("read_receipts"),
                                              dataObject.getString("isblocked"));


                                  } else {

                                      friendsDataSource.createFriends(dataObject.getString("user_id"), dataObject.getString("profile_name"), dataObject.getString("mobile"), dataObject.getString("status"), dataObject.getString("profile_pic"), dataObject.getString("room_id"), dataObject.getString("lastchat"), dataObject.getString("timestamp"), dataObject.getString("type"), dataObject.getString("country_code"), dataObject.getString("lastseen"), dataObject.getString("isonline"), dataObject.getString("unseencount"), profileObject.getString("mutechat"), privacyObject.getString("profile_pic"), privacyObject.getString("last_seen"), privacyObject.getString("status"), privacyObject.getString("online"), dataObject.getString("block"), privacyObject.getString("read_receipts"), dataObject.getString("isblocked"));

                                  }
                              }
                          }


                      }catch (Exception e)
                      {
                          e.printStackTrace();
                      }

                 //   friendsDataSource.createFriends(dataObject.getString("user_id"), dataObject.getString("profile_name"), dataObject.getString("mobile"), dataObject.getString("status"), dataObject.getString("profile_pic"), dataObject.getString("room_id"), dataObject.getString("lastchat"), dataObject.getString("timestamp"), dataObject.getString("type"), dataObject.getString("country_code"), dataObject.getString("lastseen"), dataObject.getString("isonline"), dataObject.getString("unseencount"), profileObject.getString("mutechat"), privacyObject.getString("profile_pic"), privacyObject.getString("last_seen"), privacyObject.getString("status"), privacyObject.getString("online"), dataObject.getString("block"), privacyObject.getString("read_receipts"), dataObject.getString("isblocked"));
                }
                 friendsDataSource.close();
                chatMessageDataSource.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getLocalList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_search_button:
                if (searchButtonParent.getVisibility() == View.VISIBLE) {
                    searchButtonParent.setVisibility(View.GONE);
                    (view.findViewById(R.id.chat_search_button)).setVisibility(View.VISIBLE);
                    rl_chat_search_button.setVisibility(View.VISIBLE);
                    hideSoftInPutKey();
                } else {
                    searchButtonParent.setVisibility(View.VISIBLE);
                    (view.findViewById(R.id.chat_search_button)).setVisibility(View.GONE);
                    rl_chat_search_button.setVisibility(View.GONE);
                    showSoftInPutKey();
                }
                break;
        }
    }


    @Override
    public void deleteFriend(int position) {
        chatListView.closeOpenedItems();
        FriendsDataSource friendsDataSource = new FriendsDataSource(activity);
        friendsDataSource.open();
        friendsDataSource.updateDeletedFriend(chatList.get(position).getFriendsUserId(), "1");
        friendsDataSource.close();
        ChatMessageDataSource messageDataSource = new ChatMessageDataSource(activity);
        messageDataSource.open();
        messageDataSource.deleteAllChatMessage(chatList.get(position).getFriendsRoomId());
        messageDataSource.close();
        getLocalList();
    }

    @Override
    public void updateTimestamp(int position) {
        chatListView.closeOpenedItems();
        FriendsDataSource friendsDataSource = new FriendsDataSource(activity);
        friendsDataSource.open();
        friendsDataSource.setLocate(chatList.get(position).getFriendsRoomId());
        friendsDataSource.close();
        getLocalList();
    }

    public void hideSoftInPutKey() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        }

    }

    public void showSoftInPutKey() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(searchText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        searchText.requestFocus();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            searchButtonParent.setVisibility(View.INVISIBLE);
            chat_search_button.setVisibility(View.VISIBLE);
            rl_chat_search_button.setVisibility(View.VISIBLE);
            hideSoftInPutKey();

//            if(isVisibleToUser) {
//            new LongOperation().execute();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void badgeCountResponse(String isCount) {
//        Intent intentResponse = new Intent();
//        intentResponse.putExtra("iscount", isCount);
//        intentResponse.setAction(DashboardActivity.BadgeCountBroadcastReceiver.ACTION);
//        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
//        getActivity().sendBroadcast(intentResponse);


    }

    public void updateChatbackRound() {
        new LongOperation().execute();
    }

    @Override
    public void chatProfilePicClick(int position) {
        moveToChat(position);
    }
}
