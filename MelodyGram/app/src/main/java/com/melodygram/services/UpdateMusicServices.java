package com.melodygram.services;

import android.app.Activity;
import android.app.Dialog;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.activity.LoginActivity;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.MusicDataSource;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;
import com.melodygram.singleton.AppController;

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

/**
 * Created by FuGenX-01 on 06-04-2017.
 */

public class UpdateMusicServices extends IntentService {


    private List<MusicCategory> catList;
    JSONObject response;
    Context context;
    MusicDataSource musicDataSource ;
    MusicCategory musicCategory;
    public UpdateMusicServices()
    {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userId = new SharedPreferenceDB().getUserid(context);
        System.out.println("@@@ music services is loading");
         if(userId!=null) {
             getAllMusicList();
         }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
    private void getAllMusicList() {
        String userId = new SharedPreferenceDB().getUserid(context);
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("userid", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                    Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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

                        MusicDataSource musicDataSource = new MusicDataSource(context);
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

}
