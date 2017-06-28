package com.melodygram.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.ContactDataSource;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.model.ContactsModel;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ContactUpdateSyncService extends Service {
	private Context context;
	private LinkedHashMap<String, ContactsModel> contactsHashMap;
	private String joinedContacts;
	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			getAppContacts();

		}
	};
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		this.getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, mObserver);
	}

	private ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("Log contacts", "update contacts");
			updateContacts();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	public void updateContacts() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ContactDataSource contactDataSource = new ContactDataSource(
							context);
					contactsHashMap = new LinkedHashMap<String, ContactsModel>();
					contactsHashMap.putAll(contactDataSource.getAllContacts());
					Object[] contactKeysSet = contactsHashMap.keySet().toArray();
					String userMobile = AppController.getInstance().getPrefs().getString("mobile", "");
				    joinedContacts = CommonUtil.stringArrayToStringJoin(
							contactKeysSet, ":", userMobile);
					Message msgObj = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("message", "");
					msgObj.setData(b);
					handler.sendMessage(msgObj);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	private void getAppContacts() {
		JSONObject objJson = new JSONObject();

		try {
			String userId = AppController.getInstance().getUserId();
			if (userId != null) {
				objJson.accumulate("user_id", AppController.getInstance().getUserId());
				objJson.accumulate("contacts", joinedContacts);
				JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
						APIConstant.GET_APP_CONTACTS,objJson,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								Log.v("Contacts response", response.toString());
								if (response != null) {
									parseAppUsers(response,contactsHashMap);
								}
							}

						}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
				AppController.getInstance().addToRequestQueue(jsonObjReq);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parseAppUsers(JSONObject responseObject,
							   LinkedHashMap<String, ContactsModel> contactsHashMap) {
		if (responseObject != null) {
			AppContactDataSource appContactDataSource = new AppContactDataSource(
					context);
			String gender, userId, mobileNo, localContactsName, profilePic, countryCode, appName, lastSeen, profilePicPrivacy = "", lastSeenPrivacy = "", statusPrivacy = "", onlinePrivacy = "", readReceiptsPrivacy = "", notfication = "", muteChat = "";
			try {
				if (responseObject.has("status") && responseObject.has("Result")) {

					String status = responseObject.getString("status");

					if (status.equals("success")) {
						JSONArray resultArray = responseObject
								.getJSONArray("Result");
						appContactDataSource.open();
						appContactDataSource.updateChatDeleteFlag();
						for (int i = 0; i < resultArray.length(); i++) {
							userId = resultArray.getJSONObject(i).getString(
									"user_id");
							appName = resultArray.getJSONObject(i).getString(
									"profile_name");
							mobileNo = resultArray.getJSONObject(i).getString(
									"mobile");
							status = resultArray.getJSONObject(i).getString(
									"status");
							String contactsNameTemp = null;
							if(contactsHashMap !=null && contactsHashMap.size() > 0)
							{
								 contactsNameTemp = ( contactsHashMap
										.get(mobileNo)).getPhContactName();
							}
							if (contactsNameTemp != null
									&& !contactsNameTemp.isEmpty()) {
								localContactsName = contactsNameTemp;
							} else {
								localContactsName = mobileNo;
							}
							countryCode = resultArray.getJSONObject(i)
									.getString("country_code");
							lastSeen = resultArray.getJSONObject(i).getString(
									"lastseen");
							gender = resultArray.getJSONObject(i).getString(
									"gender");
							if (resultArray.getJSONObject(i).has("profile")) {
								JSONObject privacyObj = resultArray
										.getJSONObject(i).getJSONObject(
												"profile");
								notfication = privacyObj
										.getString("notification");
								muteChat = privacyObj.getString("mutechat");
							}
							if (resultArray.getJSONObject(i).has("privacy")) {
								JSONObject privacyObj = resultArray
										.getJSONObject(i).getJSONObject(
												"privacy");
								profilePicPrivacy = privacyObj
										.getString("profile_pic");
								lastSeenPrivacy = privacyObj
										.getString("last_seen");
								statusPrivacy = privacyObj.getString("status");
								onlinePrivacy = privacyObj.getString("online");
								readReceiptsPrivacy = privacyObj
										.getString("read_receipts");
							}
							profilePic = resultArray.getJSONObject(i).getString(
									"pic");
							boolean flagAvailable = appContactDataSource
									.isContactAvailableOrNot(userId);
							ContentValues values = new ContentValues();
							values.put(DbSqliteHelper.appContactsUserId, userId);
							values.put(DbSqliteHelper.appContactsName, appName);
							values.put(DbSqliteHelper.appLocalContactsName, localContactsName);
							values.put(DbSqliteHelper.appContactsMobileNo, mobileNo);
							values.put(DbSqliteHelper.appContactsCountry, countryCode);
							values.put(DbSqliteHelper.appContactsCountryCode, countryCode);
							values.put(DbSqliteHelper.appContactsLastSeen, lastSeen);
							values.put(DbSqliteHelper.appContactsProfilePic, profilePic);
							values.put(DbSqliteHelper.CONTACT_DELETE_FLAG, "1");
							values.put(DbSqliteHelper.appContactsStatus, status);
							values.put(DbSqliteHelper.appContactsGender, gender);
							values.put(DbSqliteHelper.appProfilePicPrivacy, profilePicPrivacy);
							values.put(DbSqliteHelper.appLastSeenPrivacy, lastSeenPrivacy);
							values.put(DbSqliteHelper.appStatusPrivacy, statusPrivacy);
							values.put(DbSqliteHelper.appOnlinePrivacy, onlinePrivacy);
							values.put(DbSqliteHelper.appReadReceiptsPrivacy, readReceiptsPrivacy);
							values.put(DbSqliteHelper.appNotfication, notfication);
							values.put(DbSqliteHelper.appMuteChat, muteChat);
							if (flagAvailable) {
								appContactDataSource.updateContacts(values,userId);
							} else {
								appContactDataSource.createContacts(values);
							}
						}
						appContactDataSource.deleteSeletedContactsList();
						appContactDataSource.close();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
