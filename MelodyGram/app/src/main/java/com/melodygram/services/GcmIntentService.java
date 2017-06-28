package com.melodygram.services;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.melodygram.asyncTask.ForegroundCheckTask;
import com.melodygram.broadcastReceivers.GcmBroadcastReceiver;
import com.melodygram.R;
import com.melodygram.activity.ChatActivity;
import com.melodygram.activity.DashboardActivity;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.ContactDataSource;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.model.FriendsModel;
import com.melodygram.shortcutbadger.ShortcutBadger;
import com.melodygram.utils.CommonUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by LALIT on 15-06-2016.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    static final String senderID = "425432605160";

    public GcmIntentService() {
        super(senderID);
    }

    public static final String TAG = "GCM MelodyGram";
    private static final String GROUP_KEY_CHAT = "group_notifications";
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("GCM", intent.toString() + "-");
        Bundle extras = intent.getExtras();
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value
                    .getClass().getName()));
        }
        if (extras != null && !extras.isEmpty()) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            String badgeCount;
            if (messageType != null) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {
                     badgeCount = extras.getString("badge");
                    AppContactDataSource contactDataSource = new AppContactDataSource(
                            getApplicationContext());
                    contactDataSource.open();
                    if (badgeCount != null && !badgeCount.isEmpty()) {
                        try {
                            ShortcutBadger.with(getApplicationContext()).count(
                                   Integer.valueOf(badgeCount));
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                     sendNotification(extras);

                    Intent intentResponse = new Intent();
                    intentResponse.putExtra("count", badgeCount);
                    intentResponse.putExtra("iscount", false);
                    intentResponse.setAction(DashboardActivity.BadgeCountBroadcastReceiver.ACTION);
                    intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(intentResponse);


//                    Intent intentChatMessageResponse = new Intent();
//                    intentChatMessageResponse.putExtra("room_id", extras.getString("room_id"));
//                    intentChatMessageResponse.putExtra("firendsId",extras.getString("s_id"));
//                    intentChatMessageResponse.setAction(ChatActivity.UpdateNotificationMessage.ACTION);
//                    intentChatMessageResponse.addCategory(Intent.CATEGORY_DEFAULT);
//                    sendBroadcast( intentChatMessageResponse);


                    if (ChatGlobalStates.ROOM_ID_OPENDED != null && extras.getString("room_id") != null && !ChatGlobalStates.ROOM_ID_OPENDED.equalsIgnoreCase(extras.getString("room_id"))) {
                        buildNotification(extras.getString("room_id"), extras.getString("message"), extras.getString("s_mobile"));
                    } else if (ChatGlobalStates.ROOM_ID_OPENDED == null) {
                        buildNotification(extras.getString("room_id"), extras.getString("message"), extras.getString("s_mobile"));
                    }
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
   private void sendNotification(Bundle extras)
   {
       boolean foregroud = false;
       try {
           foregroud = new ForegroundCheckTask().execute(
                    getApplicationContext()).get();
           if(!GlobalState.IS_CHATSCREEN_VISIBLE) {
               Intent intentService = new Intent(getApplicationContext(), NotificationMessageUpdate.class);
               intentService.putExtra("room_id", extras.getString("room_id"));
               intentService.putExtra("frinedsId", extras.getString("s_id"));
               startService(intentService);
           }


       } catch (InterruptedException e) {
           e.printStackTrace();
           foregroud = false;
       } catch (ExecutionException e) {
           e.printStackTrace();
           foregroud = false;
       }

       if (!foregroud) {

       }else
       {

       }
   }

    private void buildNotification(String roomId, String message, String senderMobile) {
        long time = CommonUtil.convertUtcToLocalDate(this);
        CommonUtil.savNotificationTime(this);
        Intent intentResponse = new Intent();
        intentResponse.setAction(DashboardActivity.DashBoardBroadcastReceiver.ACTION);
        intentResponse.putExtra("broadcast_type", "chat");
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(intentResponse);
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalState.SHARED_PREF,
                Context.MODE_PRIVATE);
        long[] vibrateLong =  new long[] { 1000, 1000, 1000, 1000, 1000 };
       if(!sharedPreferences.getBoolean("vibrate", true))
       {
           vibrateLong = null;
       }
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (roomId != null && !roomId.equalsIgnoreCase("0")
                && !roomId.equalsIgnoreCase("-1")) {
            FriendsDataSource friendsDataSource = new FriendsDataSource(
                    getApplicationContext());
            friendsDataSource.open();
            FriendsModel friendsModel = friendsDataSource
                    .getFriendAvailable(roomId);
            if (friendsModel != null) {
                Intent intent = new Intent(this, ChatActivity.class);
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
                if (!friendsModel.getSeenCount().equals("0")) {
                    friendsDataSource.open();
                    friendsDataSource.updateBatchCount(friendsModel
                            .getFriendsRoomId());
                    friendsDataSource.close();
                }
                intent.putExtra("notificationMsg", message);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                ContactDataSource contactDataSource = new ContactDataSource(getApplicationContext());
                String name = null;
                if (senderMobile != null) {
                    name = contactDataSource.getContactName(senderMobile);
                    if (name == null) {
                        name = senderMobile;
                    }
                } else {
                    name = senderMobile;
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(
                                getResources().getString(R.string.app_name))
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(name + " : " + message))
                        .setAutoCancel(true)
                .setVibrate(vibrateLong)
                        .setContentText(name + " : " + message).setGroup(GROUP_KEY_CHAT);



                if (sharedPreferences.getBoolean("sound", true) && time>3) {
//                    Uri sound = Uri.parse("android.resource://" + getPackageName()
//                            + "/" + "raw" + "/" +  sharedPreferences.getInt("tone",R.raw.auto));
//
//                    mBuilder.setSound(sound);
                    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(uri);
                }
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID,
                        mBuilder.build());
            } else {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("notificationMsg", message);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                ContactDataSource contactDataSource = new ContactDataSource(getApplicationContext());
                String name = null;
                if (senderMobile != null) {
                    name = contactDataSource.getContactName(senderMobile);
                    if (name == null) {
                        name = senderMobile;
                    }
                } else {
                    name = senderMobile;
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(
                                getResources().getString(R.string.app_name))
                        .setVibrate(vibrateLong)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(name + " : " + message))
                        .setAutoCancel(true)
                        .setContentText(name + " : " + message).setGroup(GROUP_KEY_CHAT);
                if (sharedPreferences.getBoolean("sound", true) && time > 3) {
//                    Uri sound = Uri.parse("android.resource://" + getPackageName()
//                            + "/" + "raw" + "/" + sharedPreferences.getInt("tone",R.raw.auto));
//                    mBuilder =  mBuilder.setSound(sound);

                    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(uri);

                }
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID,
                        mBuilder.build());
            }
            friendsDataSource.close();
        } else {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("notificationMsg", message);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ContactDataSource contactDataSource = new ContactDataSource(getApplicationContext());
            String name = null;
            if (senderMobile != null) {
                name = contactDataSource.getContactName(senderMobile);
                if (name == null) {
                    name = senderMobile;
                }
            } else {
                name = senderMobile;
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(
                            getResources().getString(R.string.app_name))
                    .setVibrate(vibrateLong)
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(name + " : " + message))
                    .setAutoCancel(true)
                    .setContentText(name + " : " + message).setGroup(GROUP_KEY_CHAT);
            if (sharedPreferences.getBoolean("sound", true) && time > 3) {
//                Uri sound = Uri.parse("android.resource://" + getPackageName()
//                        + "/" + "raw" + "/" + sharedPreferences.getInt("tone",R.raw.auto));
//                mBuilder.setSound(sound);

                Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(uri);
            }
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID,
                    mBuilder.build());
        }
    }
    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }	}

}




