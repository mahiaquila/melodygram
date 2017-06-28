package com.melodygram.database;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * Created by FuGenX-01 on 16-06-2016.
 */
public class SharedPreferenceDB {
    private static SharedPreferenceDB instance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static SharedPreferenceDB defaultInstance() {
        if (instance == null) {
            instance = new SharedPreferenceDB();
        }

        return instance;
    }

    public void saveUserId(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userId", userId);
        editor.commit();
    }
    public String getUserid(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getString("userId",null);
    }

    public void saveLoginFirstTime(Context context, boolean isFirstTimeLogin) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstTimeLoginDB", isFirstTimeLogin);
        editor.commit();
    }
    public boolean getCheckLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("isFirstTimeLoginDB", false);
    }


    public void saveLoginFirtTimeFromSplas(Context context, boolean isFirstTimeLogin) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstTimeLoginFromSplash", isFirstTimeLogin);
        editor.commit();
    }
    public boolean getCheckLoginFromSplash(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("isFirstTimeLoginFromSplash", false);
    }


    public void saveVerificationId(Context context, String veriicationId) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("verificationId", veriicationId);
        editor.commit();
    }




    public String getVerificationId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getString("verificationId", null);
    }
    public void saveMobileNumber(Context context, String mobilenNumber) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("mobileNumber", mobilenNumber);
        editor.commit();
    }
    public String getMobileNumber(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getString("mobileNumber",null);
    }

    public void saveCountryCode(Context context, String countryCode) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("countryCode", countryCode);
        editor.commit();
    }
    public String getCountryCode(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getString("countryCode",null);
    }
    public void saveChatBackPress(Context context, boolean chatBackPress) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isChatBackPress", chatBackPress);
        editor.commit();
    }
    public boolean getChatBackPress(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("isChatBackPress", false);
    }
}
