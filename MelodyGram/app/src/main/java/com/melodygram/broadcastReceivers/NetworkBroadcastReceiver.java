package com.melodygram.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.melodygram.activity.ChatActivity;

/**
 * Created by LALIT on 27-07-2016.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
                    ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Intent intentResponse = new Intent();
                intentResponse.setAction(ChatActivity.ChatBroadcastReceiver.ACTION);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra("type", "Connected");
                context.sendBroadcast(intentResponse);
            }
            if (intent.getExtras().getBoolean(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                    Boolean.FALSE)) {
            }
        }
    }
}
