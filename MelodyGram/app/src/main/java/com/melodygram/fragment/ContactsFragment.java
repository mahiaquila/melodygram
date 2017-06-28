package com.melodygram.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.melodygram.activity.ChatActivity;
import com.melodygram.adapter.ContactAdapter;

import com.melodygram.chatinterface.InterfaceContact;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.database.MusicDataSource;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.model.ContactsModel;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;
import com.melodygram.services.AppUpdateIntentService;
import com.melodygram.singleton.AppController;
import com.melodygram.view.CustomEditText;
import com.melodygram.view.OverlayView;
import com.melodygram.view.SideBarView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by LALIT on 14-06-2016.
 */
public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, InterfaceContact {
    private ListView listView;
    private ContactAdapter contactAdapter;
    private Activity activity;
    private ArrayList<ContactsModel> contactsArrayList;
    private CustomEditText searchText;
    private AppContactDataSource appContactDataSource;
    private View view;
    private RelativeLayout searchParent;
    private SideBarView letterSideBarView;
    private TextView overlayTextView;
    private ProgressBar progressBar;
    ImageView contacts_search_button;

    ProgressDialog progressDialog;
    JSONObject response;

    private String code="91";
    private List<MusicCategory> catList;
    String userId;
    MusicDataSource musicDataSourceDb ;
    MusicCategory musicCategory;

    ArrayList<MusicCategory> muslist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_fragment, null);
        initilizeView();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
            getAppContacts();

    }

    private void initilizeView() {
        activity = getActivity();
        appContactDataSource = new AppContactDataSource(activity);
        contactsArrayList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.contact_list);
        listView.setOnItemClickListener(this);
        searchText = (CustomEditText) view.findViewById(R.id.contacts_search);
        contacts_search_button = (ImageView) view.findViewById(R.id.contacts_search_button);
        contacts_search_button.setOnClickListener(this);
        searchParent = (RelativeLayout) view.findViewById(R.id.contacts_search_parent);
        musicDataSourceDb = new MusicDataSource(activity);
        musicDataSourceDb.open();
        muslist  = musicDataSourceDb.getAllCategory();
        musicDataSourceDb.close();
        progressBarInitialize();
        progressBar = (ProgressBar) view.findViewById(R.id.contacts_progressbar);
        //   (view.findViewById(R.id.contacts_search_button)).setOnClickListener(this);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = searchText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                if (contactAdapter != null)
                    contactAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {

            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        searchParent.setVisibility(View.INVISIBLE);
                        contacts_search_button.setVisibility(View.VISIBLE);
                        hideSoftInPutKey();
                        break;

                    default:
                        break;
                }
            }

        });
        final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        overlayTextView = (TextView) OverlayView.initOverlay(layoutInflater, (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE));
        overlayTextView.setVisibility(View.INVISIBLE);
        letterSideBarView = (SideBarView) view.findViewById(R.id.letterSideBarViewId);
        letterSideBarView.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = AppController.getInstance().getPrefs();
        boolean refreshContacts = sharedPreferences.getBoolean("contact_update", false);
        if (!refreshContacts) {
            // progressBar.setVisibility(View.VISIBLE);
//            if(muslist.size()>0)
//            {
//
//            }else {
//                getAllMusicList();
//            }
            progressDialog.show();
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("contact_update", true);
        }

            getAppContacts();

    }

    public void getAppContacts() {
        Log.d("ok", "Get App Contacts");
        appContactDataSource.open();
        LinkedHashMap<String, ContactsModel> contactsHashMap = appContactDataSource.getAllContacts();
        contactsArrayList.clear();
        contactsArrayList.addAll(contactsHashMap.values());
        appContactDataSource.close();
        refreshListView();
        Intent contactsIntentService = new Intent(activity, AppUpdateIntentService.class);
        contactsIntentService.putExtra("isStart", false);
        activity.startService(contactsIntentService);
    }

    public void getNewAppContacts() {
      //  progressBar.setVisibility(View.GONE);

        progressDialog.dismiss();
        appContactDataSource.open();
        LinkedHashMap<String, ContactsModel> contactsHashMap = appContactDataSource.getAllContacts();
        contactsArrayList.clear();
        contactsArrayList.addAll(contactsHashMap.values());
        appContactDataSource.close();
        //refreshListView();
    }

    private void refreshListView() {
        if (contactsArrayList.size() > 0) {
            contactAdapter = new ContactAdapter(activity, contactsArrayList, this);
            listView.setAdapter(contactAdapter);

            progressDialog.dismiss();
        } else if (letterSideBarView != null) {
            letterSideBarView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactsModel contactsModel = (ContactsModel) contactAdapter.getItem(position);
        String countryCode = contactsModel.getCountryCode();
        if (countryCode == null)
            countryCode = "";
        try {
            Intent chatScreenIntent = new Intent(activity, ChatActivity.class);
            String frendsName = contactsModel.getPhContactName();
            if (frendsName == null || frendsName.trim().length() == 0) {
                frendsName = contactsModel.getPhContactNumber();
            }
            FriendsDataSource friendsDataSource = new FriendsDataSource(activity);
            friendsDataSource.open();
            FriendsModel friendsModel = friendsDataSource.getFriendsDetails(contactsModel.getAppContactsUserId());
            friendsDataSource.close();
            if (friendsModel != null) {
                chatScreenIntent.putExtra("frendsName", friendsModel.getFriendName());
                chatScreenIntent.putExtra("frendsNo", friendsModel.getFriendsPhoneNumber());
                chatScreenIntent.putExtra("frendsAppName", friendsModel.getFriendAppName());
                chatScreenIntent.putExtra("countryCode",
                        friendsModel.getFriendsCountryCode());
                chatScreenIntent.putExtra("chatType",
                        friendsModel.getFriendsChatType());
                chatScreenIntent.putExtra(
                        "friendProfilePicUrl", friendsModel
                                .getFriendsPicIconUrl());

                System.out.println("@@@ photo url x2:"+friendsModel
                        .getFriendsPicIconUrl());
                chatScreenIntent.putExtra("contactsRoomId", friendsModel.getFriendsRoomId());
                chatScreenIntent.putExtra("friendsLastSeen", friendsModel.getFriendsLastSeen());
                chatScreenIntent.putExtra("userId", friendsModel.getFriendsUserId());
                chatScreenIntent.putExtra("groupUsers",
                        friendsModel.getGroupUsers());
                chatScreenIntent.putExtra("groupAdminUserId",
                        friendsModel.getFriendsGroupAdminUserId());
                chatScreenIntent.putExtra("friendsIsBlocked", friendsModel.getFriendsChatBlocked());
                chatScreenIntent.putExtra("friendsGroupAdminUserId", friendsModel.getFriendsGroupAdminUserId());
                chatScreenIntent.putExtra("mute", friendsModel.getMute());
                chatScreenIntent.putExtra("profile_privacy", contactsModel.getProfilePicPrivacy());
                chatScreenIntent.putExtra("lastseen_privacy",
                        contactsModel
                                .getLastSeenPrivacy());
                chatScreenIntent.putExtra("status_privacy",
                        contactsModel.getStatusPrivacy());
                chatScreenIntent.putExtra("status", friendsModel.getFriendsStatus());
            } else {
                chatScreenIntent.putExtra("frendsName", frendsName);
                chatScreenIntent.putExtra("frendsNo", contactsModel.getPhContactNumber());
                chatScreenIntent.putExtra("countryCode", countryCode);
                chatScreenIntent.putExtra("backText", "Back");
                chatScreenIntent.putExtra("chatType", contactsModel.getChatType());
                chatScreenIntent.putExtra("friendProfilePicUrl",
                        contactsModel
                                .getPhContactPicPath());

                System.out.println("@@@ photo url x1:"+contactsModel
                        .getPhContactPicPath());
                chatScreenIntent
                        .putExtra("roomId",
                                contactsModel
                                        .getContactsRoomId());
                chatScreenIntent.putExtra("friendsLastSeen",
                        contactsModel
                                .getContactsLastSeen());
                chatScreenIntent.putExtra("userId",
                        contactsModel
                                .getAppContactsUserId());
                chatScreenIntent.putExtra("mute",
                        contactsModel.getMute());
                chatScreenIntent.putExtra("profile_privacy", contactsModel.getProfilePicPrivacy());
                chatScreenIntent.putExtra("lastseen_privacy",
                        contactsModel
                                .getLastSeenPrivacy());
                chatScreenIntent.putExtra("status_privacy",
                        contactsModel.getStatusPrivacy());
                chatScreenIntent.putExtra("onlinePrivacy",
                        contactsModel.getOnlinePrivacy());
                chatScreenIntent.putExtra("receiptsPrivacy",
                        contactsModel
                                .getReadReceiptsPrivacy());
                chatScreenIntent.putExtra("friendsIsBlocked",
                        contactsModel
                                .getIsBlocked());
                chatScreenIntent.putExtra("friendBlocked",
                        contactsModel
                                .getBlocked());
            }


            startActivity(chatScreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_search_button:
                if (searchParent.getVisibility() == View.VISIBLE) {
                    searchParent.setVisibility(View.GONE);
                    contacts_search_button.setVisibility(View.VISIBLE);
                    searchText.setText("");
                    hideSoftInPutKey();

                } else {
                    searchParent.setVisibility(View.VISIBLE);
                    contacts_search_button.setVisibility(View.GONE);
                    searchText.setFocusable(true);
                    searchText.setCursorVisible(true);
                    showSoftInPutKey();

                }
        }
    }

    public void hideSoftInPutKey() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void response(String name, String appContactUserid, int position) {
        ContactsModel contactsModel = (ContactsModel) contactAdapter.getItem(position);
        String countryCode = contactsModel
                .getCountryCode();
        if (countryCode == null)
            countryCode = "";
        try {
            Intent chatScreenIntent = new Intent(activity, ChatActivity.class);
            String frendsName = contactsModel.getPhContactName();
            if (frendsName == null || frendsName.trim().length() == 0) {
                frendsName = contactsModel.getPhContactNumber();
            }
            FriendsDataSource friendsDataSource = new FriendsDataSource(activity);
            friendsDataSource.open();
            FriendsModel friendsModel = friendsDataSource.getFriendsDetails(contactsModel.getAppContactsUserId());
            friendsDataSource.close();
            if (friendsModel != null) {
                chatScreenIntent.putExtra("frendsName", friendsModel.getFriendName());
                chatScreenIntent.putExtra("frendsNo", friendsModel.getFriendsPhoneNumber());
                chatScreenIntent.putExtra("frendsAppName", friendsModel.getFriendAppName());
                chatScreenIntent.putExtra("countryCode",
                        friendsModel.getFriendsCountryCode());
                chatScreenIntent.putExtra("chatType",
                        friendsModel.getFriendsChatType());
                chatScreenIntent.putExtra(
                        "friendProfilePicUrl", friendsModel
                                .getFriendsPicIconUrl());
                chatScreenIntent.putExtra("contactsRoomId", friendsModel.getFriendsRoomId());
                chatScreenIntent.putExtra("friendsLastSeen", friendsModel.getFriendsLastSeen());
                chatScreenIntent.putExtra("userId", friendsModel.getFriendsUserId());
                chatScreenIntent.putExtra("groupUsers",
                        friendsModel.getGroupUsers());
                chatScreenIntent.putExtra("groupAdminUserId",
                        friendsModel.getFriendsGroupAdminUserId());
                chatScreenIntent.putExtra("friendsIsBlocked", friendsModel.getFriendsChatBlocked());
                chatScreenIntent.putExtra("friendsGroupAdminUserId", friendsModel.getFriendsGroupAdminUserId());
                chatScreenIntent.putExtra("mute", friendsModel.getMute());
                chatScreenIntent.putExtra("profile_privacy", contactsModel.getProfilePicPrivacy());
                chatScreenIntent.putExtra("lastseen_privacy",
                        contactsModel
                                .getLastSeenPrivacy());
                chatScreenIntent.putExtra("status_privacy",
                        contactsModel.getStatusPrivacy());
                chatScreenIntent.putExtra("status", friendsModel.getFriendsStatus());
            } else {
                chatScreenIntent.putExtra("frendsName", frendsName);
                chatScreenIntent.putExtra("frendsNo", contactsModel.getPhContactNumber());
                chatScreenIntent.putExtra("countryCode", countryCode);
                chatScreenIntent.putExtra("backText", "Back");
                chatScreenIntent.putExtra("chatType", contactsModel.getChatType());
                chatScreenIntent.putExtra("friendProfilePicUrl",
                        contactsModel
                                .getPhContactPicPath());
                chatScreenIntent
                        .putExtra("roomId",
                                contactsModel
                                        .getContactsRoomId());
                chatScreenIntent.putExtra("friendsLastSeen",
                        contactsModel
                                .getContactsLastSeen());
                chatScreenIntent.putExtra("userId",
                        contactsModel
                                .getAppContactsUserId());
                chatScreenIntent.putExtra("mute",
                        contactsModel.getMute());
                chatScreenIntent.putExtra("profile_privacy", contactsModel.getProfilePicPrivacy());
                chatScreenIntent.putExtra("lastseen_privacy",
                        contactsModel
                                .getLastSeenPrivacy());
                chatScreenIntent.putExtra("status_privacy",
                        contactsModel.getStatusPrivacy());
                chatScreenIntent.putExtra("onlinePrivacy",
                        contactsModel.getOnlinePrivacy());
                chatScreenIntent.putExtra("receiptsPrivacy",
                        contactsModel
                                .getReadReceiptsPrivacy());
                chatScreenIntent.putExtra("friendsIsBlocked",
                        contactsModel
                                .getIsBlocked());
                chatScreenIntent.putExtra("friendBlocked",
                        contactsModel
                                .getBlocked());
            }

            startActivity(chatScreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSoftInPutKey() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(searchText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        searchText.requestFocus();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (searchParent != null && contacts_search_button != null) {
            try {
                searchParent.setVisibility(View.INVISIBLE);
                contacts_search_button.setVisibility(View.VISIBLE);
                hideSoftInPutKey();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    private void progressBarInitialize(){
        //initialize the progress dialog during the start of network operation
        progressDialog = ProgressDialog.show(getActivity(), "Loading...", "Fetching the data");
        //set the progress dialog to indeterminate state
        progressDialog.setIndeterminate(true);
        //make the progress dialog not cancellable so that user cannot cancel the network operation
        //or you can set it cancellable but for that you need to set the cancel mechanism for the network operation
        progressDialog.setCancelable(false);

    }

    private void getAllMusicList() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("userid", SharedPreferenceDB.defaultInstance().getUserid(activity));
        } catch (JSONException e) {
            e.printStackTrace();
        }


     //   final Dialog dialog = AppController.getInstance().getLoaderDialog(activity);
      //  dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.GET_MUSIC, objJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonresponse) {

                        Log.v("Login response", jsonresponse.toString());
                        if (jsonresponse != null) {
                            response =jsonresponse;
                            new MusicDownloadAsync().execute();
                            // parseMusicResponse(response);
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public class MusicDownloadAsync extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (response != null && response.has("Result")) {
                try {
                    JSONArray catArray = response.getJSONArray("Result");
                    catList = new ArrayList<>();
                    for (int i = 0; i < catArray.length(); i++) {
                        JSONObject catObj = (JSONObject) catArray.get(i);
                        musicCategory = new MusicCategory();
                        musicCategory.setCatId(catObj.getString("category_id"));
                        musicCategory.setCatName(catObj.getString("cat_name"));
                        musicCategory.setTumbImage(catObj.getString("thumbimage"));
                        musicCategory.setIsFree(catObj.getString("isfree"));
                        musicCategory.setIsPurchased(catObj.getString("ispurchased"));
                        musicCategory.setCost(catObj.getString("cost"));
                        JSONArray musicArray = catObj.getJSONArray("music");
                        ArrayList<Music> musicList = new ArrayList<>();
                        for (int j = 0; j < musicArray.length(); j++) {
                            JSONObject musicObj = (JSONObject) musicArray.get(j);
                            Music music = new Music();
                            music.setMusic("id");
                            music.setCatId(musicObj.getString("category_id"));
                            music.setName(musicObj.getString("name"));
                            music.setPhoto(musicObj.getString("photo"));
                            music.setMusic(musicObj.getString("music"));
                            musicList.add(music);
                        }
                        musicCategory.setTotalTracks(musicList.size() + "");
                        musicCategory.setMusicList(musicList);

                        ArrayList<Music> list = musicCategory.getMusicList();

                        MusicDataSource musicDataSource = new MusicDataSource(activity);
                        musicDataSource.open();
                        boolean flag = false;
                        for (int k = 0; k < list.size(); k++) {
                            Music music = list.get(k);
                            flag = downloadStickerToSdCard(music, musicDataSource);
                        }
                        if (flag) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_ID, musicCategory.getCatId());
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_NAME, musicCategory.getCatName());
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_THUMB, musicCategory.getTumbImage());
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_IS_FREE, musicCategory.getIsFree());
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_COST, musicCategory.getCost());
                            contentValues.put(DbSqliteHelper.MUSIC_CATEGORY_IS_PRUCHESED, musicCategory.getIsPurchased());
                            contentValues.put(DbSqliteHelper.MUSIC_SIZE, list.size());
                            musicDataSource.createMusicCategory(contentValues);
                        }
                        musicDataSource.close();
                        catList.add(musicCategory);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    private boolean downloadStickerToSdCard(Music music, MusicDataSource musicDataSource) {
        try {
            File file = new File(GlobalState.MUSIC_GALLERY + "/" + musicCategory.getCatName());
//            if (!file.exists()) {
//                file.mkdirs();
//              //  file.getParentFile().mkdirs();
//                File noMediaFile = new File(file, ".nomedia");
//                if (!noMediaFile.isFile())
//                    noMediaFile.createNewFile();
//            }
            URL url = new URL(APIConstant.BASE_URL + music.getMusic());
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            File outputFile = new File(file, music.getMusic().split("/")[2]);
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            is.close();
            fos.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbSqliteHelper.MUSIC_ID, music.getId());
            contentValues.put(DbSqliteHelper.CAT_ID, music.getCatId());
            contentValues.put(DbSqliteHelper.MUSIC_NAME, music.getName());
            contentValues.put(DbSqliteHelper.PIC, music.getPhoto());
            contentValues.put(DbSqliteHelper.MUSIC_LINK, music.getMusic());
            contentValues.put(DbSqliteHelper.MUSIC_LOCAL_PATH, outputFile.getAbsolutePath());
            musicDataSource.createMusic(contentValues);
            return true;
        } catch (IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
