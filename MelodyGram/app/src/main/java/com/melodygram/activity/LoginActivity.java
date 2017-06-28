package com.melodygram.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;
import com.melodygram.services.ContactUpdateSyncService;
import com.melodygram.services.UpdateMusicServices;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.SharedPreferenceDB;

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
import java.util.List;

import static android.R.attr.name;


public class LoginActivity extends MelodyGramActivity implements View.OnClickListener
{
    public static final int COUNTRY_SELECTION_RESULT = 100;
    private TextView countryCode;
    private EditText mobileNumber;
    private String code="";
    private List<MusicCategory> catList;
    String userId;
    MusicDataSource musicDataSource ;
    MusicCategory musicCategory;
    private Tracker mTracker;

    JSONObject response;
    private static final int REQUEST_EXTERNAL_STORAGE = 112;
    private String TAG = LoginActivity.class.getName();
    private static final int  CAMERA=2;
    private static final int  READ_PHONE_STATE=11;
    private static final int  READ_CONTACTS=12;
    private static final int  READ_AUDIO=13;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        checkPermissions();
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        Intent contactsUpdateService = new Intent(this, ContactUpdateSyncService.class);
        startService(contactsUpdateService);
        initialization();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(TAG, "LogigActivity: " + name);
        mTracker.setScreenName("Login:");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initialization()
    {
      // verifyStoragePermissions(LoginActivity.this);
        int requestCode = 200;
        musicDataSource = new MusicDataSource(LoginActivity.this);
        musicDataSource.open();

        countryCode = (TextView) findViewById(R.id.country_code);

        code = new CommonUtil().GetCountryZipCode(LoginActivity.this);
        if(new CommonUtil().GetCountryZipCode(LoginActivity.this)!=null)
        {
            countryCode.setText("+" + new CommonUtil().GetCountryZipCode(LoginActivity.this));
        }
        else
        {
            countryCode.setText("+1");
        }
        mobileNumber = (EditText) findViewById(R.id.mobile_number);
        Button verifyButton = (Button) findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(this);
        countryCode.setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.verify_button:
                validateData();
                break;
            case R.id.country_code:
                Intent countryIntent = new Intent(this, CountryActivity.class);
                startActivityForResult(countryIntent, COUNTRY_SELECTION_RESULT);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case COUNTRY_SELECTION_RESULT:
                    code = data.getExtras().getString("code");
                    countryCode.setText("+" + code);
                    break;
            }
        }
    }
    private void validateData()
    {
       String mobileNumberStr = mobileNumber.getText().toString();
        if (isValidMobileNo(mobileNumberStr))
        {
            getRegistered();
        }
         else
        {
            mobileNumber.setError(getResources().getString(R.string.mobile_number_error));
        }
    }

    private void getRegistered()
    {

        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("country_code", code);
            objJson.accumulate("mobile", mobileNumber.getText().toString());
            objJson.accumulate("deviceid",new CommonUtil().getDeviceId(LoginActivity.this));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }


       dialog = AppController.getInstance().getLoaderDialog(LoginActivity.this);
       dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.LOG_IN_URL, objJson,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        dialog.dismiss();
                        Log.v("Login response", response.toString());
                        if (response != null)
                        {
                            parseResponse(response);
                        }
                    }

                },
                new Response.ErrorListener()
                {

            @Override
            public void onErrorResponse(VolleyError error)
            {
               dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();

                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    protected void onStop() {
        super.onStop();

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

    }
    private void parseResponse(JSONObject response)
    {
        try {
            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    userId = response.getString("user_id");
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveLoginFirstTime(LoginActivity.this,true);
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveMobileNumber(LoginActivity.this, mobileNumber.getText().toString());
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveUserId(LoginActivity.this, userId);
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveVerificationId(LoginActivity.this,response.getString("verification_id"));
                    com.melodygram.database.SharedPreferenceDB.defaultInstance().saveCountryCode(LoginActivity.this,code);

                    Intent intent = new Intent(this, OTPVerification.class);
                    intent.putExtra("verification_id",response.getString("verification_id"));
                    intent.putExtra("user_id", response.getString("user_id"));
                    intent.putExtra("mobile", mobileNumber.getText().toString());
                    intent.putExtra("code", code);
                    startActivity(intent);


                    finish();
                }else
                {
                    if(response!=null && response.has("message") && response.getString("message")!=null)
                    {
                        Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean isValidMobileNo(String phoneNo)
    {
        if (phoneNo != null) {
            int emailLength = phoneNo.length();
            if (emailLength < 8) {
                return false;
            }
        }
        return true;
    }

    private void getAllMusicList()
    {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("userid", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();
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
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
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

                        MusicDataSource musicDataSource = new MusicDataSource(LoginActivity.this);
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
            if (!file.exists()) {
                file.mkdirs();
                File noMediaFile = new File(file, ".nomedia");
                if (!noMediaFile.isFile())
                    noMediaFile.createNewFile();
            }
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
    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ) {

                if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ) {
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_CONTACTS ,  Manifest.permission.RECORD_AUDIO,  Manifest.permission.WRITE_EXTERNAL_STORAGE                         }, 100);
                    return;
                }

                if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
                }
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS}, 100);
                }
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                }
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }


            }

        }
    }


    class MusicDownloadService extends Service
    {
        @Nullable
        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
        }
    }



}
