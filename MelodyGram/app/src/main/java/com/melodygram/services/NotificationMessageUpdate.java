package com.melodygram.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.model.EditedMsgBean;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by FuGenX-01 on 28-11-2016.
 */
public class NotificationMessageUpdate extends IntentService {

    ChatMessageDataSource moreChatMessageDatasource ;
    private AppController appController;
    String roomId, selectedImagePath;
    String friendsUserId;
    ChatMessageModel moreChatChatingListItemBeanForDB,moreChatChatingListItemBean;
    private ArrayList<ChatMessageModel>  chatDetailsItemBeansTemp, chatDetailsItemBeans,
            chatDetailsItemBeansForDB, chatDetailsItemBeansForDBTemp;
    private LinkedHashMap<String, String> receivedMessagesId;
    private HashMap<String, String> bitMapList = new HashMap<String, String>();
    private String chatContactName, chatContactNumber, dummyMessageid, chatIsSent,
            chatMessageTempId, chatMessageId,
            chatMessageSenderName, chatMessageSenderPic, chatMessageRoomId,
            chatMessageUserId, chatMessage, chatMessageType,
            chatMessageFileUrl, chatMessageLat, chatMessageLan,
            chatMessageUserTime, chatMessageUserDate, chatMessageToFromFlag,
            chatMessageSingleTickFlag, chatMessageSeen,
            chatMessageSeenTime, chatFileThumbnail, stickerId, chatDisappear;
    public NotificationMessageUpdate()
    {
        super(null);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        moreChatMessageDatasource = new ChatMessageDataSource(getApplicationContext());
        appController = AppController.getInstance();
        chatDetailsItemBeansForDBTemp = new ArrayList<>();
        chatDetailsItemBeansTemp = new ArrayList<>();
        chatDetailsItemBeans = new ArrayList<>();
        chatDetailsItemBeansForDB = new ArrayList<>();
        receivedMessagesId = new LinkedHashMap<>();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
           roomId =intent.getExtras().getString("room_id");
           friendsUserId =intent.getExtras().getString("frinedsId");
          getAllMassages();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getAllMassages() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("last_msgid", getLastMessageId());
            objJson.accumulate("unseen_msgid", getUnseenMsgId());
            objJson.accumulate("receivers", friendsUserId);
            objJson.accumulate("seen", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.GET_MESSAGE_URL, objJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject json = new JSONObject(response.toString());
                            JSONObject json_LL = json.getJSONObject("Result");
                            Log.v("response", response.toString());
                            if (response != null) {
                                parseGetAllMessagesResponse(response);
                            }
                            if(json_LL.has("edited_message")){
                                JSONArray editedMsgArray = json_LL.getJSONArray("edited_message");
                                parseEditedMsg(editedMsgArray);
                            }

                        }catch (JSONException e)
                        {

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    // Toast.makeText(ChatActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    // Toast.makeText(ChatActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void parseGetAllMessagesResponse(JSONObject jsonObject) {

        try {
            if (jsonObject != null) {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    JSONObject json_LL = jsonObject.getJSONObject("Result");
                    if (json_LL.has("typing")) {
                        JSONArray userTyping = json_LL.getJSONArray("typing");
                       // userIsTyping(userTyping);
                    }
                    if (json_LL.has("msgseentime")) {
                        JSONArray msgSeenTime = json_LL
                                .getJSONArray("msgseentime");
                     //   parseMsgSeen(msgSeenTime);
                    }
                    if (json_LL.has("lastseen")) {
                       // friendsLastSeen = json_LL.getString("lastseen");
                    }

                        JSONArray resultArray = json_LL.getJSONArray("Data");
                        int resultArraySize = resultArray.length();
                        if (resultArraySize > 0) {
                            parseResponseArray(resultArray, false, false, true);



                    } else {

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseResponseArray(JSONArray dataArrayResponse,
                                    boolean refreshChatListFlag, boolean veryFirstTimeFlag,
                                    boolean postReceiveFlag) {
        chatDetailsItemBeansForDBTemp.clear();

            int count = 0;
            String newMessages;
            for (int i = 0; i < dataArrayResponse.length(); i++) {
                try {
                    JSONObject dataObject = dataArrayResponse.getJSONObject(i);
                    if (dataObject.has("user_id")) {
                        String userid;
                        userid = dataObject.getString("user_id");
                        if (!userid.equals(appController.getUserId())) {
                            count++;
                            continue;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            if (count == 1) {
//                newMessages = getResources().getString(R.string.new_messages)
//                        + " " + count + " "
//                        + getResources().getString(R.string.one_message_text);
//                moreChatChatingListItemBean = new ChatMessageModel();
//                moreChatChatingListItemBean.setUserId(appController.getUserId());
//                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.COUNT_TYPE);
//                moreChatChatingListItemBean.setNewMessageCount(newMessages);
//                chatDetailsItemBeans.add(moreChatChatingListItemBean);
//
//
//            } else if (count > 0) {
//                newMessages = getResources().getString(R.string.new_messages)
//                        + " " + count + " "
//                        + getResources().getString(R.string.messages_text);
//                moreChatChatingListItemBean = new ChatMessageModel();
//                moreChatChatingListItemBean.setUserId(appController.getUserId());
//                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.COUNT_TYPE);
//                moreChatChatingListItemBean.setNewMessageCount(newMessages);
//                chatDetailsItemBeans.add(moreChatChatingListItemBean);
//
//            }
        try {
            boolean soundflag = true;
            for (int i = 0; i < dataArrayResponse.length(); i++) {
                JSONObject dataObject = dataArrayResponse.getJSONObject(i);
                parseNewOldMsgFromServerToLoad(dataObject, refreshChatListFlag,
                        true, veryFirstTimeFlag, soundflag);
                soundflag = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseNewOldMsgFromServerToLoad(JSONObject dataObject,
                                               boolean refreshChatListFlag, boolean serverNewOldMsgFlag,
                                               boolean veryFirstTimeFlag, boolean soundFlag) {
        try {

            if (!receivedMessagesId.containsValue(dataObject.getString("messageid"))) {
                receivedMessagesId.put(dataObject.getString("messageid"), dataObject.getString("messageid"));
                String dataType = dataObject.getString("type");
                String messageid = dataObject.getString("messageid");
                String disappear = dataObject.getString("disappear");

                String dummyMessageid = "";
                if (dataObject.has("dummymsgid")) {
                    dummyMessageid = dataObject.getString("dummymsgid");
                }
                String userid = dataObject.getString("user_id");
                String roomid = dataObject.getString("room_id");
                String sender = dataObject.getString("sender");
                String senderpic = dataObject.getString("senderpic");
                String message = "";
                if (dataObject.has("message")) {
                    message = dataObject.getString("message");
                }
                String time = dataObject.getString("time");
                String postPic = null;

                if (dataObject.has("fileurl")) {
                    postPic = dataObject.getString("fileurl");
                    // ... Written with Video And Audio download concept!!!
                    if (dataType.equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)
                            || dataType
                            .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE)|| dataType
                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)|| dataType
                            .equalsIgnoreCase(ChatGlobalStates.MESSAGE_TYPE)) {
                        String postPicTemp = postPic;
                        if (postPicTemp.contains("http")) {
                            postPicTemp = postPicTemp.substring(postPicTemp
                                    .lastIndexOf("/") + 1);
                        }
                        File mediaStorageDir = null;
                        if (userid.equals(appController.getUserId())) {
                            if (ChatGlobalStates.mediaPathSelected.size() > 0
                                    && ChatGlobalStates.mediaPathSelected
                                    .containsKey(postPicTemp)) {
                                postPic = ChatGlobalStates.mediaPathSelected
                                        .get(postPicTemp);
                                ChatGlobalStates.mediaPathSelected
                                        .remove(postPicTemp);
                            } else {
                                if (dataType
                                        .equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)) {
                                    mediaStorageDir = new File(
                                            GlobalState.CAMERA_ONE_TO_ONE_PHOTO);
                                } else if (dataType
                                        .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                                        .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
//                                    mediaStorageDir = new File(
//                                            GlobalState.ONE_TO_ONE_AUDIO);
                                    mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data/Melodygram" + getPackageName());
                                }
                            }
                        } else {
                            if (dataType
                                    .equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)) {
                                mediaStorageDir = new File(
                                        GlobalState.ONE_TO_ONE_VIDEO);
                            } else if (dataType
                                    .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                                    .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
//                                mediaStorageDir = new File(
//                                        GlobalState.ONE_TO_ONE_AUDIO);
                                mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data" + getPackageName());
                            }
                        }
                        if (mediaStorageDir != null) {
                            File mediaFile = null;
                            mediaFile = new File(mediaStorageDir.getPath()
                                    + File.separator + postPicTemp);
                            if (mediaFile != null && mediaFile.exists()) {
                                postPic = mediaFile.getAbsolutePath();



                            } else {
                                if (userid.equals(appController.getUserId())) {
                                    if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)) {
                                        mediaStorageDir = new File(
                                                GlobalState.ONE_TO_ONE_VIDEO);
                                    } else if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
//                                        mediaStorageDir = new File(
//                                                GlobalState.ONE_TO_ONE_AUDIO);
                                        mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data" + getPackageName());
                                    }
                                } else {
                                    if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)) {
                                        mediaStorageDir = new File(
                                                GlobalState.ONE_TO_ONE_VIDEO);
                                    } else if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {

                                        mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data" + getPackageName());
                                    }
                                }
                                if (mediaStorageDir != null) {
                                    mediaFile = new File(mediaStorageDir.getPath()
                                            + File.separator + postPicTemp);
                                    if (mediaFile != null && mediaFile.exists()) {
                                        postPic = mediaFile.getAbsolutePath();
                                    } else {
                                        postPic = postPicTemp;
                                    }


                                }
                            }
                        }
                    }
                }
                String videoThumbData = null;
                if (dataObject.has("videothumb")
                        && dataType.equals(ChatGlobalStates.VIDEO_TYPE)) {
                    videoThumbData = dataObject.getString("videothumb");
                } else if (appController.getUserId().equals(userid)) {
                    videoThumbData = selectedImagePath;
                }
                String stickerId = null;
                if (dataType.equals(ChatGlobalStates.STICKERS_TYPE)) {
                    if (dataObject.has("sticker")) {
                        postPic = dataObject.getString("sticker");
                        if (dataObject.has("stickerid")) {
                            stickerId = dataObject.getString("stickerid");
                        }
                    }
                }
                String lat = null, lng = null;
                if (dataObject.has("lat") && dataObject.has("lng")) {
                    lat = dataObject.getString("lat");
                    lng = dataObject.getString("lng");
                }

                String seen = "", seentime = "";
                if (dataObject.has("seen")) {
                    seen = dataObject.getString("seen");
                    if (!(seen != null && seen.trim().length() > 0)) {
                        seen = "";
                    }
                }
                if (dataObject.has("seentime")) {
                    seentime = dataObject.getString("seentime");
                    if (!(seentime != null && seentime.trim().length() > 0)) {
                        seentime = "";
                    }
                }

                moreChatChatingListItemBeanForDB = new ChatMessageModel();
                moreChatChatingListItemBeanForDB.setMessageType(dataType);
                moreChatChatingListItemBeanForDB.setMsgId(messageid);
                moreChatChatingListItemBeanForDB
                        .setChatTempMessageId(dummyMessageid);
                moreChatChatingListItemBeanForDB.setUserId(userid);
                moreChatChatingListItemBeanForDB.setMessageRoomId(roomid);
                moreChatChatingListItemBeanForDB.setSenderName(sender);
                moreChatChatingListItemBeanForDB.setProfileImgUrl(senderpic);
                moreChatChatingListItemBeanForDB.setActualMsg(message);
                moreChatChatingListItemBeanForDB.setMsgTime(time);
                moreChatChatingListItemBeanForDB
                        .setChatFileThumbnail(videoThumbData);
                moreChatChatingListItemBeanForDB.setChatDisappear(disappear);

                if (postPic != null) {
                    moreChatChatingListItemBeanForDB.setPostImgUrl(postPic);
                    if(dataType.equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE)) {
                        if(!userid.equalsIgnoreCase(AppController.getInstance().getUserId())) {

                            if(disappear.equalsIgnoreCase("-1"))
                            {
                                downloadFile(APIConstant.BASE_URL + postPic, postPic.replace("uploads/chatfiles/", ""));

                            }
                        }
                    }
                }
                moreChatChatingListItemBeanForDB.setLatitude(lat);
                moreChatChatingListItemBeanForDB.setLangitude(lng);
                moreChatChatingListItemBeanForDB.setChatMessageSeen(seen);
                moreChatChatingListItemBeanForDB.setChatMessageSeenTime(seentime);
                moreChatChatingListItemBeanForDB.setChatStickerAudioBuzzId(stickerId);
                if (serverNewOldMsgFlag) {
                    chatDetailsItemBeansForDBTemp
                            .add(moreChatChatingListItemBeanForDB);
                    chatDetailsItemBeansForDB.add(moreChatChatingListItemBeanForDB);
                } else {
                    chatDetailsItemBeansForDBTemp.add(0,
                            moreChatChatingListItemBeanForDB);
                    chatDetailsItemBeansForDB.add(0,
                            moreChatChatingListItemBeanForDB);
                }

                int offset = time.indexOf(" ");
                String dateStamp = time.substring(0, offset);
//                if (serverNewOldMsgFlag) {
//                    messageDateCompair(dateStamp, dummyMessageid);
//                } else {
//                    messageDateCompairOLD(dateStamp);
//                }
//                moreChatChatingListItemBean = new ChatMessageModel();
//                moreChatChatingListItemBean.setMessageType(dataType);
//                moreChatChatingListItemBean.setMsgId(messageid);
//                moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
//                moreChatChatingListItemBean.setUserId(userid);
//                moreChatChatingListItemBean.setMessageRoomId(roomid);
//                moreChatChatingListItemBean.setSenderName(sender);
//                moreChatChatingListItemBean.setProfileImgUrl(senderpic);
//                moreChatChatingListItemBean.setActualMsg(message);
//                moreChatChatingListItemBean.setMsgTime(time);
//                moreChatChatingListItemBean.setMsgDate(dateStamp);
//                moreChatChatingListItemBean.setChatStickerAudioBuzzId(stickerId);
//                moreChatChatingListItemBean.setChatDisappear(disappear);
//                if (videoThumbData != null) {
//                    moreChatChatingListItemBean
//                            .setChatFileThumbnail(videoThumbData);
//                }
//                if (postPic != null) {
//                    if (bitMapList.containsKey(dummyMessageid)) {
//                        moreChatChatingListItemBean.setChatFileThumbnail(bitMapList
//                                .get(dummyMessageid));
//                        bitMapList.remove(dummyMessageid);
//                    }
//                    moreChatChatingListItemBean.setPostImgUrl(postPic);
//                }
//                if (lat != null && lng != null) {
//                    moreChatChatingListItemBean.setLatitude(lat);
//                    moreChatChatingListItemBean.setLangitude(lng);
//                }
//                moreChatChatingListItemBean.setMsgToFromFlag(false);
//                moreChatChatingListItemBean.setSingleTickFlag(false);
//                moreChatChatingListItemBean.setChatMessageSeen(seen);
//                moreChatChatingListItemBean.setChatMessageSeenTime(seentime);
//                chatDetailsItemBeans.add(moreChatChatingListItemBean);

                insertChatMessageToDB(chatDetailsItemBeansForDBTemp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    public void insertChatMessageToDB(
            ArrayList<ChatMessageModel> chatDetailsItemBeansForDB
          ) {

        int size = chatDetailsItemBeansForDB.size();
        for (int i = 0; i < size; i++) {
            ChatMessageModel chatDBItemBeansDetails = chatDetailsItemBeansForDB
                    .get(i);
            chatMessageType = chatDBItemBeansDetails.getMessageType();
            chatMessageId = chatDBItemBeansDetails.getMsgId();
            chatMessageUserId = chatDBItemBeansDetails.getUserId();
            chatMessageRoomId = chatDBItemBeansDetails.getMessageRoomId();
            chatMessageSenderName = chatDBItemBeansDetails.getSenderName();
            chatMessageSenderPic = chatDBItemBeansDetails
                    .getRandomProfileImageUrl();
            chatMessage = chatDBItemBeansDetails.getActualMsg();
            chatMessageUserTime = chatDBItemBeansDetails.getMsgTime();
            chatMessageFileUrl = chatDBItemBeansDetails.getPostImgUrl();
            chatMessageLat = chatDBItemBeansDetails.getLatitude();
            chatMessageLan = chatDBItemBeansDetails.getLangitude();
            chatMessageSeen = chatDBItemBeansDetails.getChatMessageSeen();
            chatMessageSeenTime = chatDBItemBeansDetails
                    .getChatMessageSeenTime();
            chatFileThumbnail = chatDBItemBeansDetails.getChatFileThumbnail();
            chatMessageTempId = chatDBItemBeansDetails.getChatTempMessageId();
            chatDisappear = chatDBItemBeansDetails.getChatDisappear();
            stickerId = chatDBItemBeansDetails.getChatStickerAudioBuzzId();

            ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                    getApplicationContext());
            moreChatMessageDatasource.open();
            moreChatMessageDatasource.createChatMessage(chatMessageId,
                    chatMessageSenderName, chatMessageSenderPic,
                    chatMessageRoomId, chatMessageUserId, chatMessage,
                    chatMessageType, chatMessageFileUrl,
                    chatMessageLat, chatMessageLan,
                    chatMessageUserTime, chatMessageUserDate,
                    chatMessageToFromFlag, "0",
                   "0", chatMessageSeenTime,
                    chatFileThumbnail, "", "1",
                    chatMessageTempId, "", chatDisappear, stickerId, "1",1);
            moreChatMessageDatasource.close();


        }


    }


    private String getLastMessageId() {
        String messageId = "-1";
        moreChatMessageDatasource.open();
        messageId = moreChatMessageDatasource.getLatestMsgId(roomId);
        if (messageId != null) return messageId;
        moreChatMessageDatasource.close();
        return messageId;
    }

    private String getUnseenMsgId() {
        String messageId = "-1";
        moreChatMessageDatasource.open();
        messageId = moreChatMessageDatasource.getUnseenMsgId(roomId);
        if (messageId != null) return messageId;
        moreChatMessageDatasource.close();
        return messageId;
    }
    public void parseEditedMsg(JSONArray editedMsgArray) {

        for (int i = 0; i < editedMsgArray.length(); i++) {
            try {
                String messageId, actualMsg;
                JSONObject  editedMsgObject = editedMsgArray.getJSONObject(i);
                messageId = editedMsgObject.getString("message_id");
                actualMsg = editedMsgObject.getString("message");
                ChatMessageDataSource messageDatasource = new ChatMessageDataSource(getApplicationContext());
                messageDatasource.open();
                if(messageDatasource.isMsgAvailable(messageId))
                    messageDatasource.updateEditMsg(messageId, actualMsg,"2");// Last one "2" for edit icon on receiver end.  Replace with "1" if you dont want...
                messageDatasource.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void downloadFile(String uRl,String imageName) {


        try {
            DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
            String    sdCard= Environment.getExternalStorageDirectory().toString();
            File myDir = new File(sdCard,"MelodayGramImage");

                        /*  if specified not exist create new */
            if(!myDir.exists())
            {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

                        /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File (myDir, fname);
            if (file.exists ())
                file.delete ();
            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/MelodayGramImage",imageName.replace("uploads/chatfiles/",""));

            mgr.enqueue(request);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
