package com.melodygram.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.activity.DashboardActivity;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.CategoryDataSource;
import com.melodygram.database.ChatStickersDataSource;
import com.melodygram.database.ContactDataSource;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.model.ContactsModel;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
/**
 * Created by LALIT on 15-06-2016.
 */

public class AppUpdateIntentService extends IntentService {

	private ContactDataSource contactDataSource;
	private Context context;

	public AppUpdateIntentService() {
		super(null);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		contactDataSource = new ContactDataSource(getApplicationContext());
		boolean isSpalsh = intent.getBooleanExtra("isStart", false);
		if (isSpalsh) {
			getAppContacts();
			getMyStickers();
			//getAudioBuzz();
		}
		else
		{
			getAppContacts();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private void getAppContacts() {
			final LinkedHashMap<String, ContactsModel> contactsHashMap = new LinkedHashMap<String, ContactsModel>();
			contactsHashMap.putAll(contactDataSource.getAllContacts());
		String userMobile = AppController.getInstance().getPrefs().getString("mobile", "");
			Object[] contactKeysSet = contactsHashMap.keySet().toArray();
			String joinedContacts = CommonUtil.stringArrayToStringJoin(
					contactKeysSet, ":", userMobile);
			JSONObject objJson = new JSONObject();
		try {
			objJson.accumulate("user_id", AppController.getInstance().getUserId());
				objJson.accumulate("contacts", joinedContacts);
			Log.v("Contacts ",objJson.toString());
				JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
						APIConstant.GET_APP_CONTACTS,objJson,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								Log.v("Contacts response", response.toString());
								if (response != null) {
									parseAppUsers(response,contactsHashMap);
									Intent intentResponse = new Intent();
									intentResponse.setAction(DashboardActivity.DashBoardBroadcastReceiver.ACTION);
									intentResponse.putExtra("broadcast_type","contacts");
									intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
									sendBroadcast(intentResponse);
								}
							}

						}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
				AppController.getInstance().addToRequestQueue(jsonObjReq);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		stopSelf();
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
							String contactsNameTemp = (contactsHashMap
									.get(mobileNo)).getPhContactName();
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

	private void getMyStickers() {
		JSONObject objJson = new JSONObject();
		try {
			objJson.accumulate("userid",AppController.getInstance().getUserId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.v("request", objJson.toString());
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIConstant.GET_MY_STICKERS_URL, objJson,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("response", response.toString());
						if (response != null) {
							try {
									if(response.has("Result"))
									{
										JSONArray resultArray = response
												.getJSONArray("Result");
										parseMyChatStickers(resultArray);
									}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}

	public void parseMyChatStickers(JSONArray resultArray) {
		CategoryDataSource categoryDataSource = new CategoryDataSource(context);
		categoryDataSource.open();
		try {
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject catObject = resultArray.getJSONObject(i);
				if (catObject.getString("ispurchased").equalsIgnoreCase("yes")
						|| (catObject.getString("isfree")
						.equalsIgnoreCase("yes"))) {
					ContentValues values = new ContentValues();
					values.put(DbSqliteHelper.CATEGORY_ID,
							catObject.getString("category_id"));
					values.put(DbSqliteHelper.CATEGORY_NAME,
							catObject.getString("cat_name"));
					values.put(DbSqliteHelper.CATEGORY_THUMB,
							catObject.getString("thumbimage"));
					values.put(DbSqliteHelper.CATEGORY_THUMB_INACTIVE,
							catObject.getString("thumbimage_inactive"));
					values.put(DbSqliteHelper.CATEGORY_IS_FREE,
							catObject.getString("isfree"));
					values.put(DbSqliteHelper.CATEGORY_COST,
							catObject.getString("cost"));
					values.put(DbSqliteHelper.CATEGORY_IS_PRUCHESED,
							catObject.getString("ispurchased"));
					Long value = categoryDataSource.createCategory(values);
					if (value > 0) {
						JSONArray stickersArray = catObject
								.getJSONArray("stickers");
						for (int j = 0; j < stickersArray.length(); j++) {
							final JSONObject stickerObject = stickersArray
									.getJSONObject(j);
							final ContentValues contentValues = new ContentValues();
							contentValues.put(DbSqliteHelper.STICKERS_ID,
									stickerObject.getString("id"));
							contentValues.put(DbSqliteHelper.STICKERS_NAME,
									stickerObject.getString("name"));
							contentValues.put(DbSqliteHelper.STICKERS_PIC,
									stickerObject.getString("photo"));
							contentValues.put(
									DbSqliteHelper.STICKERS_CATEGORY_ID,
									stickerObject.getString("category_id"));

							Thread thread = new Thread(new Runnable(){
								@Override
								public void run() {
									try {
										downloadStickerToSdCard(APIConstant.BASE_URL
                                                        + stickerObject.getString("photo"),
                                                stickerObject.getString("id"),
                                                contentValues);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});

thread.start();

						}
					}
				} else {
					continue;
				}
			}
			categoryDataSource.close();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void downloadStickerToSdCard(String downloadUrl, String filename,
										 ContentValues contentValues) {
			try {
			//String fileExtension = ".png";
			File file = new File(GlobalState.STICKERS_GALLERY);
			if (!file.exists()) {
				file.mkdirs();
				File noMediaFile = new File(file, ".nomedia");
				if (!noMediaFile.isFile())
					noMediaFile.createNewFile();
			}
			URL url = new URL(downloadUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			File outputFile = new File(file, downloadUrl.split("/")[2]);
			FileOutputStream fos = new FileOutputStream(outputFile);
			InputStream is = connection.getInputStream();
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
			}
			ChatStickersDataSource chatStickersDataSource = new ChatStickersDataSource(
					context);
			chatStickersDataSource.open();
			chatStickersDataSource.createStickers(contentValues);
			chatStickersDataSource.close();
			is.close();
			fos.close();

		} catch (IOException io) {
			io.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
