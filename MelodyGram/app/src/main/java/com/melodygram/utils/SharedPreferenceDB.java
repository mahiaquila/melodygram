package com.melodygram.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by FuGenX-01 on 16-06-2016.
 */
public class SharedPreferenceDB {
    private  static  SharedPreferenceDB instance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static  SharedPreferenceDB defaultInstance()
    {
        if(instance==null)
        {
            instance = new SharedPreferenceDB();
        }

        return instance;
    }

    public void saveAppProfilePic(Context context, String pic) {
     pref = context.getSharedPreferences(
                "Melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pic", pic);
        editor.commit();
    }

    public String getAppProfilePic(Context context) {
        pref = context.getSharedPreferences(
                "Melody", Context.MODE_PRIVATE);
        return pref.getString("pic", "0");

    }
    public void saveAudioAutoMobileDataDownload(Context context, boolean autoAudioMobileData) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autoAudioMobileData", autoAudioMobileData);
        editor.commit();
    }
    public boolean getAudioAutoMobileDataDownload(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autoAudioMobileData",true);
    }


    public void saveAudioAutoWifiDownload(Context context, boolean autoAudioWifi) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autoAudioWifi", autoAudioWifi);
        editor.commit();
    }
    public boolean getAudioAutoWifiDownload(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autoAudioWifi",true);
    }

    public void savePhotoAutoDoawnloadMobileData(Context context, boolean autoAudioWifi)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("photoAutomobile", autoAudioWifi);
        editor.commit();
    }
    public boolean getPhotoAutoDownloadMobileData(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("photoAutomobile",true);
    }
    public void savePhotoAutoDoawnloadWifi(Context context, boolean autoAudioWifi) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autophotoWifi", autoAudioWifi);
        editor.commit();
    }
    public boolean getPhotoAutoDownloadWifi(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autophotoWifi",true);
    }
}
