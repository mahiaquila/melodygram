package com.melodygram.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.adapter.MusicStoreAdapter;
import com.melodygram.chatinterface.MusicInterface;
import com.melodygram.constants.APIConstant;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;
import com.melodygram.singleton.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LALIT on 21-07-2016.
 */
public class MusicStoreActivity extends MelodyGramActivity implements View.OnClickListener, MusicInterface {
    private AppController appController;
    private MusicStoreAdapter musicStoreAdapter;
    private List<MusicCategory> catList;
    private ListView musicListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.music_store_layout);
        appController = AppController.getInstance();
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);

        musicListView = (ListView) findViewById(R.id.music_list);
        ((TextView) findViewById(R.id.header_name)).setText(R.string.music_store);
        getAllMusicList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                setResult(RESULT_OK);
                finish();

                break;
        }
    }

    @Override
    public void downloadMusic(int position) {
        intiAdapter();
    }

    private void intiAdapter() {
        List<MusicCategory> catListTemp = new ArrayList<>();
        catListTemp.addAll(catList);
        if (musicStoreAdapter == null) {
            musicStoreAdapter = new MusicStoreAdapter(catListTemp, MusicStoreActivity.this);
            musicListView.setAdapter(musicStoreAdapter);
        } else {
            musicStoreAdapter.notifyDataSetChanged();
        }
    }

    private void getAllMusicList() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("userid", appController.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = AppController.getInstance().getLoaderDialog(this);
        dialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.GET_MUSIC, objJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.v("Login response", response.toString());
                        if (response != null) {
                            parseMusicResponse(response);
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(MusicStoreActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MusicStoreActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void parseMusicResponse(JSONObject response) {
        if (response != null && response.has("Result")) {
            try {
                JSONArray catArray = response.getJSONArray("Result");
                catList = new ArrayList<>();
                for (int i = 0; i < catArray.length(); i++) {
                    JSONObject catObj = (JSONObject) catArray.get(i);
                    MusicCategory musicCategory = new MusicCategory();
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
                    musicCategory.setTotalTracks(musicList.size()+"");
                    musicCategory.setMusicList(musicList);
                    catList.add(musicCategory);
                }
                intiAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

    }
}

