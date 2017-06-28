package com.melodygram.asyncTask;

/**
 * Created by LALIT on 27-07-2016.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.melodygram.adapter.ChatMessageAdapter;
import com.melodygram.adapter.MusicStoreAdapter;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.database.ChatStickersDataSource;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MusicDownloadAsync extends AsyncTask<String, Integer, String> {

    private MusicCategory musicCategory;
    private MusicInterfaceAsyncResponse musicInterfaceAsyncResponse;
    private Activity activity;
    private  TextView tvProgressUpdateValue;

    public MusicDownloadAsync(MusicCategory musicCategory, Activity activity,TextView tvProgressUpdateValue) {
        this.activity = activity;
        this.musicCategory = musicCategory;
        this.musicInterfaceAsyncResponse = musicInterfaceAsyncResponse;
        this.tvProgressUpdateValue =tvProgressUpdateValue;

    }

    public interface MusicInterfaceAsyncResponse {
        void asyncFinished(String response);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        tvProgressUpdateValue.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... mediaURL) {
        ArrayList<Music> list = musicCategory.getMusicList();
        MusicDataSource musicDataSource = new MusicDataSource(activity);
        musicDataSource.open();
        boolean flag = false;
        long total = 0;
        for (int i = 0; i < list.size(); i++) {

            total += i;
            // publishing the progress....
            publishProgress((int)(total*100/list.size()));
            Music music = list.get(i);
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
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (ChatGlobalStates.musicDownload.containsKey(musicCategory.getCatId())) {
            tvProgressUpdateValue.setVisibility(View.GONE);
            MusicStoreAdapter.ViewHolder listener = ChatGlobalStates.musicDownload.get(musicCategory.getCatId());
            listener.musicStoreAdapter.asyncFinished(musicCategory.getCatId());
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        tvProgressUpdateValue.setText(String.valueOf(values[0]));
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
