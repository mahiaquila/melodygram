package com.melodygram.services;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.activity.ChatActivity;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.DateTimeUtil;
/**
 * Created by LALIT on 15-06-2016.
 */
public class FwdIntentService extends IntentService {
    int noOfRoom;
    public FwdIntentService() {
        super("com.melodygram.services.FwdIntentService");
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        noOfRoom = intent.getIntExtra("noOfRoom",0);
        ChatGlobalStates.chatItemSelectedMulti = new ArrayList<>(
                ChatGlobalStates.chatDetailsItemSelected.values());
        ChatGlobalStates.chatDetailsItemSelected.clear();
        for (ChatMessageModel chatMessageModel : ChatGlobalStates.chatItemSelectedMulti) {
            doInBackground(intent.getStringExtra("roomId"), chatMessageModel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ChatGlobalStates.chatItemSelectedMulti != null)
            ChatGlobalStates.chatItemSelectedMulti.clear();

    }

    protected void doInBackground(String roomId,
                                  ChatMessageModel fwdChatListItemBean) {
        String fwdDataType, lastMessageId, fwdFileUrl, stickerUrl, fwdThumbUrl;
        lastMessageId = "-1";
        String userId = AppController.getInstance().getUserId();
        String dummyMessageid = CommonUtil.generateTempMessageId(userId);
        for (int i = 0; i < (noOfRoom - 1); i++) {
            dummyMessageid = dummyMessageid
                    + ","
                    + CommonUtil.generateTempMessageId(userId);
        }
        fwdDataType = fwdChatListItemBean.getMessageType();
        try {
            JSONObject objJson = new JSONObject();
            objJson.accumulate("user_id", userId);
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("type", fwdDataType);
            objJson.accumulate("last_msgid", lastMessageId);
            objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
            if (dummyMessageid != null)
                objJson.accumulate("dummymsgid", dummyMessageid);
            if (fwdDataType.equals(ChatGlobalStates.MESSAGE_TYPE)) {
                objJson.accumulate("message", CommonUtil
                        .encodeTranslatedMessage(fwdChatListItemBean
                                .getActualMsg()));
            } else if (fwdDataType.equals(ChatGlobalStates.VIDEO_TYPE)) {
                fwdFileUrl = fwdChatListItemBean.getPostImgUrl();
                objJson.accumulate("fileurl", fwdFileUrl.replaceFirst(
                        APIConstant.SERVER_PATH, ""));
                objJson.accumulate("extn",
                        fwdFileUrl.substring(fwdFileUrl.lastIndexOf(".") + 1));
            } else if (fwdDataType.equals(ChatGlobalStates.AUDIO_TYPE)
                    || fwdDataType.equals(ChatGlobalStates.FILE_TYPE) || fwdDataType.equals(ChatGlobalStates.MUSIC_TYPE) ) {
                fwdFileUrl = fwdChatListItemBean.getPostImgUrl();



                objJson.accumulate("fileurl",
                        fwdFileUrl.substring(fwdFileUrl.lastIndexOf("/") + 1));
                objJson.accumulate("extn",
                        fwdFileUrl.substring(fwdFileUrl.lastIndexOf(".") + 1));
                if (fwdDataType.equals(ChatGlobalStates.VIDEO_TYPE)) {
                    fwdThumbUrl = fwdChatListItemBean.getChatFileThumbnail();
                    objJson.accumulate("videothumblink", fwdThumbUrl);
                }
            } else if (fwdDataType.equals(ChatGlobalStates.LOCATION_TYPE)) {
                objJson.accumulate("lat", fwdChatListItemBean.getLatitude());
                objJson.accumulate("lng", fwdChatListItemBean.getLangitude());
            } else if (fwdDataType.equals(ChatGlobalStates.STICKERS_TYPE)) {
                stickerUrl = fwdChatListItemBean.getPostImgUrl();
                stickerUrl = stickerUrl.replaceFirst(
                        APIConstant.SERVER_PATH, "");
                objJson.accumulate("stickerid", stickerUrl);
            } else if(fwdDataType.equals(ChatGlobalStates.PICTURE_TYPE))
            {
                fwdFileUrl = fwdChatListItemBean.getPostImgUrl();
                objJson.accumulate("fileurl", fwdFileUrl.replaceFirst(APIConstant.BASE_URL, ""));
                objJson.accumulate("extn", fwdFileUrl.substring(fwdFileUrl.lastIndexOf(".") + 1));
            }else if (fwdDataType.equals(ChatGlobalStates.STICKERS_TYPE))
            {
                stickerUrl = fwdChatListItemBean.getPostImgUrl();
                stickerUrl = stickerUrl.replaceFirst(APIConstant.SERVER_PATH, "");
                objJson.accumulate("stickerid", stickerUrl);
            }

            postFwdNW(objJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void postFwdNW(JSONObject objJson) {
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.SEND_LINK, objJson,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
