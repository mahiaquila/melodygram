package com.melodygram.asyncTask;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.melodygram.R;
import com.melodygram.activity.ChatActivity;
import com.melodygram.constants.APIConstant;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.DateTimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by LALIT on 25-06-2016.
 */
public class UploadImage extends AsyncTask<String, Void, String> {
    MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    OkHttpClient client = new OkHttpClient();
    InterfaceAsyncResponse interfaceAsyncResponse;
    File file;
    ChatActivity activity;

    public interface InterfaceAsyncResponse {
        void asyncFinished(String response);
    }

    public UploadImage(File file, ChatActivity activity, InterfaceAsyncResponse interfaceAsyncResponse) {
        this.interfaceAsyncResponse = interfaceAsyncResponse;
        this.file = file;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            ChatMessageDataSource chatMessageDataSource = new ChatMessageDataSource(activity);
            chatMessageDataSource.open();
            String lastMessageId = chatMessageDataSource.getLatestMsgId(params[0]);
            if (lastMessageId == null) {
                lastMessageId = "-1";
            }
            RequestBody requestBody;
            MultipartBody.Builder body;
            okhttp3.Request request;
            body = new MultipartBody.Builder();
            body.setType(MultipartBody.FORM);
            body.addFormDataPart("user_id", AppController.getInstance().getUserId());
            body.addFormDataPart("room_id", params[0]);
            body.addFormDataPart("type", params[1]);
            body.addFormDataPart("time", DateTimeUtil.morechatTimeFormat());
            body.addFormDataPart("extn","png");
            body.addFormDataPart("last_msgid", lastMessageId);
            body.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            body.addFormDataPart("dummymsgid", params[2]);
            body.addFormDataPart("disappear", params[3]);
            body.addFormDataPart("message", params[4]);
            requestBody = body.build();
            request = new okhttp3.Request.Builder()
                    .url(APIConstant.POST_MSG_URL_NEW)
                    .post(requestBody)
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.v("Response",result+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        interfaceAsyncResponse.asyncFinished(result);
    }
}