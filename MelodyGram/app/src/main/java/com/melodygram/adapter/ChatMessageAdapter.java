package com.melodygram.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melodygram.R;
import com.melodygram.activity.ChatActivity;
import com.melodygram.activity.DashboardActivity;
import com.melodygram.activity.OtherUserProfileActivity;
import com.melodygram.activity.PicViewerActivity;
import com.melodygram.activity.ProfileActivity;
import com.melodygram.asyncTask.AWSMediaDownloader.InterfaceAsyncResponse;
import com.melodygram.asyncTask.AWSMediaDownloader;
import com.melodygram.asyncTask.AWSMediaDownloaderSender;
import com.melodygram.chatinterface.InterfacePicSelection;
import com.melodygram.chatinterface.InterfaceRefreshListview;
import com.melodygram.chatinterface.InterfaceUploadAudioFile;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.emojicon.EmojiconTextView;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.model.EditedMsgBean;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.MediaPlayerTag;
import com.melodygram.shortcutbadger.CloseHelper;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.DateTimeUtil;
import com.melodygram.utils.SharedPreferenceDB;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by LALIT on 25-06-2016.
 */
public class ChatMessageAdapter extends BaseAdapter implements Filterable, InterfaceAsyncResponse , SeekBar.OnSeekBarChangeListener {
    private ArrayList<ChatMessageModel> messageList;
    private ArrayList<ChatMessageModel> fixedMessageList;
    private Context context;
    private LayoutInflater inflater;
    private AppController appController;
    private ChatMessageDataSource chatMessageDataSource;
    private Bitmap fileBitmap;
    private HashMap<String, String> disAppearList;
    private FilterName filterName;
    private boolean notified;
    String receiverDisapper;
    private FriendsDataSource friendsDataSource;
    String receiverMsgId;
    boolean isAPPUser;
    Handler handler;
    Runnable  myRunnableDisappearAudioFIle;
    private SharedPreferences sharedPreferences;
    String lastUserid,lastBeforeUserid;
    boolean isCheckMediapLayerPlaying;
    int increaMentCount=0;
    int length;
    RelativeLayout parrentLayout;
    boolean isCheckNetworkConnectionMusicUpload;
    boolean isFromImage;
    boolean isAudioImagePlay;
    RelativeLayout senderProgressbarLayout;
    InterfaceRefreshListview interfaceRefreshListview;
    InterfaceUploadAudioFile interfaceUploadAudioFile;
    InterfacePicSelection interfacePicSelection;
    String searchstring="";
    public ChatMessageAdapter(Context context, ArrayList<ChatMessageModel> messageList,InterfaceRefreshListview interfaceRefreshListview,InterfaceUploadAudioFile interfaceUploadAudioFile,InterfacePicSelection interfacePicSelection) {
        this.context = context;
        this.messageList = messageList;
        fixedMessageList = new ArrayList<>();
        handler = new Handler();
        fixedMessageList.addAll(messageList);
        this.inflater = LayoutInflater.from(context);

        this.chatMessageDataSource = new ChatMessageDataSource(context);
        friendsDataSource = new FriendsDataSource(context);
//        inflater = (LayoutInflater) context
//                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        appController = AppController.getInstance();
        sharedPreferences = appController.getPrefs();
        this.interfaceRefreshListview = interfaceRefreshListview;
        disAppearList = new HashMap<>();
        this.interfaceUploadAudioFile = interfaceUploadAudioFile;
        this.interfacePicSelection = interfacePicSelection;

    }

    @Override
    public int getCount()
    {
        return messageList.size();
    }
    @Override
    public Object getItem(int position)
    {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        try
        {
            if (messageList.get(position).getUserId().equals(appController.getUserId())) {
                return 0;
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            if (messageList.get(position-1).getUserId().equals(appController.getUserId())) {
                return 0;
            }
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    private void moveToOtherProfile(String userId,boolean isAPPUser)
    {
        if(userId!=null) {
            FriendsModel friendsModel;
            friendsDataSource.open();
            if(isAPPUser)
            {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }else {
                friendsModel = friendsDataSource.getFriendsDetails(userId);
                if(friendsModel!=null) {
                    Intent intent = new Intent(context, OtherUserProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("frendsNo", friendsModel.getFriendsPhoneNumber());
                    bundle.putString("frendsAppName", friendsModel.getFriendAppName());
                    bundle.putString("friendProfilePicUrl", friendsModel
                            .getFriendsPicIconUrl());
                    bundle.putString("countryCode",
                            friendsModel.getFriendsCountryCode());
                    bundle.putString("contactsRoomId", friendsModel.getFriendsRoomId());
                    bundle.putString("userId", friendsModel.getFriendsUserId());
                    bundle.putString("friendsIsBlocked", friendsModel.getFriendsChatBlocked());
                    bundle.putString("status", friendsModel.getFriendsStatus());
                    bundle.putString("profile_privacy", friendsModel.getProfilePicPrivacy());
                    bundle.putString("lastseen_privacy", friendsModel.getLastSeenPrivacy());
                    bundle.putString("status_privacy", friendsModel.getStatusPrivacy());
                    bundle.putString("status_privacy", friendsModel.getStatusPrivacy());
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                }
            }
            friendsDataSource.close();

        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case 0:
                final ViewHolder senderViewHolder;
                if (convertView == null) {
                    senderViewHolder = new ViewHolder();
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(
                            R.layout.app_user_chat_item, parent,false);
                    senderViewHolder.parentLayout = (RelativeLayout) convertView
                            .findViewById(R.id.chat_item_parent);

//                    parrentLayout = senderViewHolder.parentLayout;
                    // parent time
                    senderViewHolder.appUserTime = (TextView) convertView.findViewById(R.id.app_user_time);
                    //Text Content init
                    senderViewHolder.appUserTextParent = (RelativeLayout) convertView.findViewById(R.id.app_user_text_parent);
                    senderViewHolder.appUserTextTime = (TextView) convertView.findViewById(R.id.app_user_text_time);
                    senderViewHolder.appUserText = (EmojiconTextView) convertView.findViewById(R.id.app_user_text);
                    senderViewHolder.appUserText.setEmojiconSize(70);
                    senderViewHolder.appUserTextProfilePic = (ImageView) convertView.findViewById(R.id.app_user_text_image);
                    senderViewHolder.appUserTextTick = (ImageView) convertView.findViewById(R.id.app_user_text_tick);
                    senderViewHolder.appUserEdit=(ImageView)convertView.findViewById(R.id.iv_edit);
                    //Pic Content init
                    senderViewHolder.appUserPicParent = (RelativeLayout) convertView.findViewById(R.id.app_user_pic_parent);
                    senderViewHolder.appUserPicTime = (TextView) convertView.findViewById(R.id.app_user_pic_time);
                    senderViewHolder.appUserPicProfilePic = (ImageView) convertView.findViewById(R.id.app_user_pic_image);
                    senderViewHolder.appUserPicTick = (ImageView) convertView.findViewById(R.id.app_user_pic_tick);
                    senderViewHolder.appUserPic = (ImageView) convertView.findViewById(R.id.app_user_image);
                    senderViewHolder.appUserImageProgress = (ProgressBar) convertView.findViewById(R.id.app_user_image_progress);
                    senderViewHolder.appUserDescription = (TextView) convertView.findViewById(R.id.app_user_description);

                    //Audio Content init
                    senderViewHolder.appUserMusicParent = (RelativeLayout) convertView.findViewById(R.id.app_user_music_parent);
                    senderViewHolder.appUserMusicPlayStop = (RelativeLayout) convertView.findViewById(R.id.app_user_music_play_stop);
                    senderViewHolder.appUserPlayButton = (ImageView) convertView.findViewById(R.id.app_user_play_button);
                    senderViewHolder.appUserStopButton = (ImageView) convertView.findViewById(R.id.app_user_stop_button);
                    senderViewHolder.appUserMusicWave = (ImageView) convertView.findViewById(R.id.app_user_music_wave);
                    senderViewHolder.appUserProgressbarParent = (RelativeLayout) convertView.findViewById(R.id.app_user_progress_bar_parent);
                    senderViewHolder.appUserMusicSeekBar = (SeekBar) convertView.findViewById(R.id.app_user_music_seekBar);
                    senderViewHolder.appUserMusicDuration = (TextView) convertView.findViewById(R.id.app_user_music_duration);
                    senderViewHolder.appUserMusicTick = (ImageView) convertView.findViewById(R.id.app_user_music_tick);
                    senderViewHolder.appUserMusicTime = (TextView) convertView.findViewById(R.id.app_user_music_time);
                    senderViewHolder.appUserProgressbarText = (TextView) convertView.findViewById(R.id.app_user_progress_bar_text);
                    senderViewHolder.appUserMusicProfileImage = (ImageView) convertView.findViewById(R.id.app_user_music_image);
                    senderViewHolder.appUserProgressBar =(ProgressBar)convertView.findViewById(R.id.app_user_sender_progress);
                    senderViewHolder.appUserUploadImage =(ImageView)convertView.findViewById(R.id.app_user_upload_button);

                    //Sticker content init
                    senderViewHolder.appUserStickerParent = (RelativeLayout) convertView.findViewById(R.id.app_user_sticker_parent);
                    senderViewHolder.appUserStickerTime = (TextView) convertView.findViewById(R.id.app_user_sticker_time);
                    senderViewHolder.appStickerPic = (ImageView) convertView.findViewById(R.id.app_sticker_image);
                    senderViewHolder.appUserStickerTick = (ImageView) convertView.findViewById(R.id.app_user_sticker_tick);
                    senderViewHolder.appUserStickerProfilePic = (ImageView) convertView.findViewById(R.id.app_user_sticker_image);
                    senderViewHolder.appUserStickerProgress = (ProgressBar) convertView.findViewById(R.id.app_user_sticker_progress);

                    //File content init
                    senderViewHolder.appUserFileParent = (RelativeLayout) convertView.findViewById(R.id.app_user_file_parent);
                    senderViewHolder.appUserProgressParent = (RelativeLayout)convertView.findViewById(R.id.app_user_file_progress_parent);
                    senderViewHolder.appUserFileTime = (TextView) convertView.findViewById(R.id.app_user_file_time);
                    senderViewHolder.appUserFileName = (TextView) convertView.findViewById(R.id.app_user_file_text);
                    senderViewHolder.appUserFileProfilePic = (ImageView) convertView.findViewById(R.id.app_user_file_image);
                    senderViewHolder.appUserFileTick = (ImageView) convertView.findViewById(R.id.app_user_file_tick);
                    senderViewHolder.appUserFilePerText = (TextView) convertView.findViewById(R.id.app_user_percentage_text);
                    senderViewHolder.appUserFileProgress = (ProgressBar) convertView.findViewById(R.id.app_user_file_progressbar);
                    senderViewHolder.appUserDocImage = (ImageView) convertView.findViewById(R.id.app_user_doc_image);
                    convertView.setTag(senderViewHolder);
                } else
                {
                    senderViewHolder = (ViewHolder) convertView.getTag();
                }
                if( GlobalState.CHECK_BACK_PRESSE)
                {

                    senderViewHolder.parentLayout.setVisibility(View.GONE);
                }
                senderViewHolder.chatMessageAdapter = this;
                senderViewHolder.position = position;
                final ChatMessageModel senderChatMessageModel = messageList.get(position);
                String senderSeen = senderChatMessageModel.getChatMessageSeen();
                String tempMsgId = senderChatMessageModel.getChatTempMessageId();
                final  String msgId = senderChatMessageModel.getMsgId();

                String senderDisappear = senderChatMessageModel.getChatDisappear();
                if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.COUNT_TYPE))
                {
                    if (notified)
                    {
                        senderViewHolder.appUserTime.setVisibility(View.GONE);
                        senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                        senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                        senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                    } else
                    {
                        senderViewHolder.appUserTime.setVisibility(View.VISIBLE);
                        senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                        senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                        senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                        senderViewHolder.appUserTime.setText(senderChatMessageModel.getNewMessageCount());
                    }
                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.DATE_TYPE))
                {
                    if( senderViewHolder.appUserTime!=null)
                    {
                        senderViewHolder.appUserTime.setVisibility(View.VISIBLE);
                    }
                    senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                    senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                    senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                    senderViewHolder.appUserTime.setText(CommonUtil.floatingNewDate(
                            senderChatMessageModel.getMsgDate()));
                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.MESSAGE_TYPE))
                {
                    try {
                        senderViewHolder.appUserTextParent.setVisibility(View.VISIBLE);
                        senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                        senderViewHolder.appUserTime.setVisibility(View.GONE);
                        senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                        senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    String msgTime = DateTimeUtil.tweentyFourTo12New(senderChatMessageModel.getMsgTime());
                    senderViewHolder.appUserTextTime.setText(msgTime);
                  //  senderViewHolder.appUserText.setText(senderChatMessageModel.getActualMsg());



                  String  faqsearchstr=senderChatMessageModel.getActualMsg().toLowerCase(Locale.getDefault());
                    if (faqsearchstr.contains(searchstring)) {
                        int startPos = faqsearchstr.indexOf(searchstring);
                        int endPos = startPos + searchstring.length();

                        Spannable spanText = Spannable.Factory.getInstance().newSpannable(senderChatMessageModel.getActualMsg()); // <- EDITED: Use the original string, as `country` has been converted to lowercase.
                        spanText.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        senderViewHolder.appUserText.setText(spanText, TextView.BufferType.SPANNABLE);
                    }
                    else
                    {
                        senderViewHolder.appUserText.setText(senderChatMessageModel.getActualMsg());
                    }

                    if(senderChatMessageModel.getChatEditMsg()!=null && senderChatMessageModel.getChatEditMsg().equalsIgnoreCase("2"))
                    {
                        senderViewHolder.appUserEdit.setVisibility(View.VISIBLE);
                    }else
                    {
                        senderViewHolder.appUserEdit.setVisibility(View.INVISIBLE);
                    }
                    try {
                        ChatMessageModel senderChatMessageModelNew = messageList.get(position - 1);
                        if (senderChatMessageModel.getUserId().equalsIgnoreCase(senderChatMessageModelNew.getUserId()))
                        {
                            senderViewHolder.appUserTextProfilePic.setVisibility(View.INVISIBLE);
                        } else
                        {
                            senderViewHolder.appUserTextProfilePic.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    boolean singleTickFlag = senderChatMessageModel.isSingleTickFlag();

                    if (singleTickFlag) {
                        senderViewHolder.appUserTextTick.setImageResource(R.drawable.single_tick);
                    } else {
                        // condition for
                        if ((senderSeen != null && senderSeen.equalsIgnoreCase("1")) || ChatActivity.unseenMessageHashList
                                .containsKey(msgId)) {
                            if (senderDisappear != null && !senderDisappear.equalsIgnoreCase("0") && !senderDisappear.equalsIgnoreCase("-1") && !disAppearList.containsValue(tempMsgId)) {
                                disAppearList.put(tempMsgId, tempMsgId);
                                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(senderDisappear), 15000, position);
                                myCountDownTimer.start();
                            }
                            if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0") ) {

                                senderViewHolder.appUserTextTick.setImageResource(R.drawable.double_tick_seen_white);
                            }else
                            {
                                senderViewHolder.appUserTextTick.setImageResource(R.drawable.double_tick);
                            }
                        }
                        else
                        {
                            senderViewHolder.appUserTextTick.setImageResource(R.drawable.double_tick);
                        }
                    }
                    if (SharedPreferenceDB.defaultInstance().getAppProfilePic(context).length() > 0)
                        appController.displayUrlImage(senderViewHolder.appUserTextProfilePic, SharedPreferenceDB.defaultInstance().getAppProfilePic(context), null);

                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE) || senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.LOCATION_TYPE)) {

                    isFromImage = true;

                    senderViewHolder.appUserPicParent.setVisibility(View.VISIBLE);
                    senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                    senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                    senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                    String picLocalPath = senderChatMessageModel.getChatFileThumbnail();
                    String msgTime = DateTimeUtil.tweentyFourTo12New(senderChatMessageModel.getMsgTime());

                    senderViewHolder.appUserPic.setImageResource(android.R.color.transparent);
                    senderViewHolder.appUserPicTime
                            .setText(msgTime);
                    Boolean singleTickFlag = senderChatMessageModel.isSingleTickFlag();
                    if (singleTickFlag) {
                        senderViewHolder.appUserPicTick.setImageResource(R.drawable.single_tick);
                        senderViewHolder.appUserImageProgress.setVisibility(View.VISIBLE);
                    } else {
                        if ((senderSeen != null && senderSeen.equalsIgnoreCase("1")) || ChatActivity.unseenMessageHashList
                                .containsKey(msgId)) {
                            if (senderDisappear != null && !senderDisappear.equalsIgnoreCase("0") && !senderDisappear.equalsIgnoreCase("-1") && !disAppearList.containsValue(tempMsgId)) {
                                disAppearList.put(tempMsgId, tempMsgId);
                                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(senderDisappear), 1000, position);
                                myCountDownTimer.start();
                            }
                            senderViewHolder.appUserImageProgress.setVisibility(View.GONE);
                            if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0") )  {
                                senderViewHolder.appUserPicTick.setImageResource(R.drawable.double_tick_seen_white);
                            }else
                            {
                                senderViewHolder.appUserPicTick.setImageResource(R.drawable.double_tick);
                            }
                        }
                        else
                        {
                            senderViewHolder.appUserImageProgress.setVisibility(View.GONE);
                            senderViewHolder.appUserPicTick.setImageResource(R.drawable.double_tick);
                        }

                    }
                    if (senderChatMessageModel.getMessageType()
                            .equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE))
                    {
                        String picMsg = senderChatMessageModel.getActualMsg();
                        if (picMsg != null && picMsg.trim().length() > 0) {
                            senderViewHolder.appUserDescription.setVisibility(View.VISIBLE);
                            senderViewHolder.appUserDescription.setText(picMsg);
                        } else {
                            senderViewHolder.appUserDescription.setVisibility(View.GONE);
                        }

                        if (fileBitmap != null) {
                            senderViewHolder.appUserPic.setImageBitmap(fileBitmap);
                        } else {
                            String postImgUrl = senderChatMessageModel.getPostImgUrl();
                            if (picLocalPath != null) {
                                File file = null;
                                try {
                                    file = new File(picLocalPath);
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                                if (file != null && file.isFile()) {

                                    appController.displayFileImage(senderViewHolder.appUserPic, picLocalPath, senderViewHolder.appUserImageProgress);
                                } else {
                                    appController.displayUrlImage(senderViewHolder.appUserPic, postImgUrl, senderViewHolder.appUserImageProgress);
                                }
                            } else {
                                appController.displayUrlImage(senderViewHolder.appUserPic, postImgUrl, senderViewHolder.appUserImageProgress);
                            }
                        }
                        senderViewHolder.appUserPic.setOnLongClickListener(new View.OnLongClickListener()
                        {
                            @Override
                            public boolean onLongClick(View v) {
                                GlobalState.CHECK_BACK_PRESSE=false;
                                interfacePicSelection.picSelectionResponse(senderViewHolder.parentLayout,position,false);
                                return false;
                            }
                        });
                        senderViewHolder.appUserPic
                                .setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            GlobalState.CHECK_BACK_PRESSE = false;
                                            if (ChatGlobalStates.ISLONGPRESS) {
                                                interfacePicSelection.picSelectionResponse(senderViewHolder.parentLayout, position, true);
                                            } else {
                                                if (messageList.get(
                                                        position).getMessageType().equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE)) {
                                                    String postImgUrl = messageList.get(
                                                            position).getPostImgUrl();
                                                    String picLocalPath = messageList.get(
                                                            position).getChatFileThumbnail();
                                                    Intent intent = new Intent(
                                                            context.getApplicationContext(),
                                                            PicViewerActivity.class);
                                                    intent.putExtra("imageURL", postImgUrl);
                                                    intent.putExtra("localPath", picLocalPath);
                                                    intent.putExtra("disappear", senderChatMessageModel.getChatDisappear());
                                                    intent.putExtra("senderSeen", senderChatMessageModel.getChatMessageSeen());
                                                    context.startActivity(intent);
                                                } else {
                                                    String locationLat = messageList
                                                            .get(position)
                                                            .getLatitude();
                                                    String locationLan = messageList
                                                            .get(position)
                                                            .getLangitude();
                                                    String coordinates = "http://maps.google.com/maps?daddr=" + locationLat + "," + locationLan;
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(coordinates));
                                                    context.startActivity(intent);
                                                }
                                            }
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                    } else {
                        senderViewHolder.appUserPic
                                .setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        if (messageList.get(
                                                position).getMessageType().equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE))
                                        {
                                            String postImgUrl = messageList.get(
                                                    position).getPostImgUrl();
                                            String picLocalPath = messageList.get(
                                                    position).getChatFileThumbnail();
                                            Intent intent = new Intent(
                                                    context.getApplicationContext(),
                                                    PicViewerActivity.class);
                                            intent.putExtra("imageURL", postImgUrl);
                                            intent.putExtra("localPath", picLocalPath);
                                            intent.putExtra("disappear",senderChatMessageModel.getChatDisappear());
                                            intent.putExtra("senderSeen",senderChatMessageModel.getChatMessageSeen());
                                            context.startActivity(intent);
                                        } else
                                        {
                                            String locationLat = messageList
                                                    .get(position)
                                                    .getLatitude();
                                            String locationLan = messageList
                                                    .get(position)
                                                    .getLangitude();
                                            String coordinates = "http://maps.google.com/maps?daddr=" + locationLat + "," + locationLan;
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(coordinates));
                                            context.startActivity(intent);
                                        }
                                    }
                                });
                        senderViewHolder.appUserDescription.setVisibility(View.GONE);
                        String lat = senderChatMessageModel.getLatitude();
                        String lan = senderChatMessageModel.getLangitude();
                        String locationUrl = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lan + "&zoom=18&size=450x400&sensor=false&markers=color:red|label:|" + lat + "," + lan;
                        appController.displayGoogleUrlImage(senderViewHolder.appUserPic, locationUrl, senderViewHolder.appUserImageProgress);
                    }
                    try {
                        ChatMessageModel senderChatMessageModelNew = messageList.get(position - 1);
                        if (senderChatMessageModel.getUserId().equalsIgnoreCase(senderChatMessageModelNew.getUserId())) {
                            senderViewHolder.appUserPicProfilePic.setVisibility(View.INVISIBLE);
                        } else {
                            senderViewHolder.appUserPicProfilePic.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if (SharedPreferenceDB.defaultInstance().getAppProfilePic(context).length() > 0)
                        appController.displayUrlImage(senderViewHolder.appUserPicProfilePic, SharedPreferenceDB.defaultInstance().getAppProfilePic(context), null);
                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.STICKERS_TYPE)) {
                    isFromImage = false;
                    senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                    senderViewHolder.appUserStickerParent.setVisibility(View.VISIBLE);
                    senderViewHolder.appUserFileParent.setVisibility(View.GONE);
                    String msgTime = DateTimeUtil.tweentyFourTo12New(senderChatMessageModel.getMsgTime());
                    String postImgUrl = senderChatMessageModel.getPostImgUrl();
                    String stickerId = senderChatMessageModel.getChatStickerAudioBuzzId();
                    senderViewHolder.appUserStickerTime
                            .setText(msgTime);
                    Boolean singleTickFlag = senderChatMessageModel.isSingleTickFlag();
                    if (singleTickFlag) {
                        senderViewHolder.appUserStickerTick.setImageResource(R.drawable.single_tick);
                        senderViewHolder.appUserStickerProgress.setVisibility(View.VISIBLE);
                    } else {
                        if ((senderSeen != null && !senderDisappear.equalsIgnoreCase("0") && senderSeen.equalsIgnoreCase("1")) || ChatActivity.unseenMessageHashList
                                .containsKey(msgId)) {
                            if (senderDisappear != null && !senderDisappear.equalsIgnoreCase("0") && !senderDisappear.equalsIgnoreCase("-1") && !disAppearList.containsValue(tempMsgId)) {
                                disAppearList.put(tempMsgId, tempMsgId);
                                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(senderDisappear), 1000, position);
                                myCountDownTimer.start();
                            }
                            senderViewHolder.appUserStickerProgress.setVisibility(View.GONE);
                            if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0") )  {
                                senderViewHolder.appUserStickerTick.setImageResource(R.drawable.double_tick_seen_white);
                            }else
                            {
                                senderViewHolder.appUserStickerTick.setImageResource(R.drawable.double_tick);
                            }
                        }
                        else
                        {
                            senderViewHolder.appUserStickerTick.setImageResource(R.drawable.double_tick);
                            senderViewHolder.appUserStickerProgress.setVisibility(View.GONE);
                        }
                    }

                    if (stickerId != null && !stickerId.isEmpty()) {
                        String localPath = GlobalState.STICKERS_GALLERY + "/" + stickerId + ".png";
                        File file = null;
                        try {
                            file = new File(localPath);
                        } catch (Exception e) {
                            e.getStackTrace();
                            appController.displayUrlImage(senderViewHolder.appStickerPic, postImgUrl, senderViewHolder.appUserStickerProgress);
                        }
                        if (file != null && file.isFile()) {

                            appController.displayFileImage(senderViewHolder.appStickerPic, localPath, senderViewHolder.appUserStickerProgress);
                        } else {
                            appController.displayUrlImage(senderViewHolder.appStickerPic, postImgUrl, senderViewHolder.appUserStickerProgress);
                        }
                    } else {
                        appController.displayUrlImage(senderViewHolder.appStickerPic, postImgUrl, senderViewHolder.appUserStickerProgress);

                    }

                    try {
                        ChatMessageModel senderChatMessageModelNew = messageList.get(position - 1);
                        if (senderChatMessageModel.getUserId().equalsIgnoreCase(senderChatMessageModelNew.getUserId())) {
                            senderViewHolder.appUserStickerProfilePic.setVisibility(View.INVISIBLE);
                        } else {
                            senderViewHolder.appUserStickerProfilePic.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if (SharedPreferenceDB.defaultInstance().getAppProfilePic(context).length() > 0)
                        appController.displayUrlImage(senderViewHolder.appUserStickerProfilePic, SharedPreferenceDB.defaultInstance().getAppProfilePic(context), null);

                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
                    senderViewHolder.appUserMusicParent.setVisibility(View.VISIBLE);
                    senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                    senderViewHolder.appUserFileParent.setVisibility(View.GONE);

                    senderViewHolder.appUserMusicSeekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    if (senderChatMessageModel.getMessageType()
                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
                        senderViewHolder.appUserMusicWave.setVisibility(View.VISIBLE);
                    } else {
                        senderViewHolder.appUserMusicWave.setVisibility(View.INVISIBLE);
                    }
                    boolean singleTickFlag = senderChatMessageModel
                            .isSingleTickFlag();
                    String postAudioUrl = senderChatMessageModel.getPostImgUrl();

                    File audioFileLocalCheck = new File(postAudioUrl);
                    if (!ChatGlobalStates.mediasUploadOnProcess
                            .containsKey(senderChatMessageModel.getMsgId())) {
                        ChatGlobalStates.mediasUploadOnProcess
                                .put(senderChatMessageModel
                                        .getMsgId(), senderViewHolder);

                    } else {
                        ChatGlobalStates.mediasUploadOnProcess
                                .put(senderChatMessageModel.getMsgId(),  senderViewHolder);
                    }
                    if (singleTickFlag) {
                        senderViewHolder.appUserProgressParent
                                .setVisibility(View.VISIBLE);
                        if (!ChatGlobalStates.mediasUploadOnProcess
                                .containsKey(senderChatMessageModel.getChatTempMessageId())) {
                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(senderChatMessageModel
                                            .getChatTempMessageId(), senderViewHolder);
                        } else {
                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(senderChatMessageModel.getChatTempMessageId(), senderViewHolder);
                        }
                        isCheckMediapLayerPlaying=false;
                        isAudioImagePlay =false;
                        senderViewHolder.appUserProgressbarParent
                                .setVisibility(View.VISIBLE);
                        senderViewHolder.appUserMusicPlayStop.setVisibility(View.INVISIBLE);
                        senderViewHolder.appUserProgressBar.setVisibility(View.VISIBLE);
                        senderViewHolder.appUserMusicTick.setImageResource(R.drawable.single_tick);
                        File audioFileLocal = new File(postAudioUrl);
                        final MediaPlayerTag mediaPlayerTag = new MediaPlayerTag();
                        mediaPlayerTag.setPlayPauseParent(senderViewHolder.appUserProgressbarParent);
                        senderViewHolder.appUserProgressParent.setTag(mediaPlayerTag);

                        senderViewHolder.appUserUploadImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                senderViewHolder.appUserProgressbarParent
                                        .setVisibility(View.VISIBLE);
                                senderViewHolder.appUserProgressbarParent
                                        .setVisibility(View.VISIBLE);
                                senderViewHolder.appUserProgressBar.setVisibility(View.VISIBLE);
                                senderViewHolder.appUserUploadImage.setVisibility(View.INVISIBLE);
                                senderViewHolder.appUserMusicPlayStop.setVisibility(View.INVISIBLE);
                                interfaceUploadAudioFile.audioFileResponse(senderChatMessageModel);

                            }
                        });

                        senderViewHolder.appUserProgressBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                senderViewHolder.appUserProgressBar.setVisibility(View.GONE);
                                senderViewHolder.appUserUploadImage.setVisibility(View.VISIBLE);
                                senderViewHolder.appUserProgressbarParent
                                        .setVisibility(View.INVISIBLE);
                                senderViewHolder.appUserMusicPlayStop.setVisibility(View.VISIBLE);


                            }
                        });

                        if(senderChatMessageModel.getProgressValue()!=null) {
                            senderViewHolder.appUserProgressbarText
                                    .setText(senderChatMessageModel.getProgressValue() + "%");

                        }
                        if (audioFileLocal != null)
                            senderViewHolder.appUserMusicDuration.setText(CommonUtil
                                    .mediaDurationFormat(CommonUtil
                                            .mediaToDurationPath(audioFileLocal.getAbsolutePath())));

                    } else {
                        File audioFileLocal = new File(postAudioUrl);

                        if (postAudioUrl.contains("/"))
                        {
                            senderViewHolder.appUserProgressbarParent
                                    .setVisibility(View.GONE);
                            senderViewHolder.appUserMusicPlayStop
                                    .setVisibility(View.VISIBLE);
                            senderViewHolder.appUserProgressBar.setVisibility(View.INVISIBLE);
                            playAudio(senderViewHolder.appUserMusicPlayStop,
                                    senderViewHolder.appUserStopButton,
                                    senderViewHolder.appUserPlayButton, senderViewHolder.appUserMusicDuration, senderViewHolder.appUserMusicSeekBar, audioFileLocal);

                        }
                        else
                        {
                            if (audioFileLocalCheck.exists()) {

                            } else {
                                senderViewHolder.appUserProgressbarParent
                                        .setVisibility(View.VISIBLE);
                                senderViewHolder.appUserMusicPlayStop
                                        .setVisibility(View.INVISIBLE);
                                senderViewHolder.appUserProgressBar.setVisibility(View.VISIBLE);
                                new AWSMediaDownloaderSender(GlobalState.ONE_TO_ONE_AUDIO,
                                        context, senderChatMessageModel.getMsgId(), senderChatMessageModel.getMessageType()).execute(senderChatMessageModel.getPostImgUrl());
                            }
                        }
//                        senderViewHolder.appUserProgressbarParent
//                                .setVisibility(View.GONE);
//                        senderViewHolder.appUserMusicPlayStop
//                                .setVisibility(View.VISIBLE);
                        if ((senderSeen != null && senderSeen.equalsIgnoreCase("1")) || ChatActivity.unseenMessageHashList
                                .containsKey(msgId)) {
                            if (senderDisappear != null && !senderDisappear.equalsIgnoreCase("0") && !senderDisappear.equalsIgnoreCase("-1") && !disAppearList.containsValue(tempMsgId)) {
                                disAppearList.put(tempMsgId, tempMsgId);
                                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(senderDisappear), 1000, position);
                                myCountDownTimer.start();
                            }
                            if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0") )  {
                                senderViewHolder.appUserMusicTick.setImageResource(R.drawable.double_tick_seen_white);
                            }else
                            {
                                senderViewHolder.appUserMusicTick.setImageResource(R.drawable.double_tick);
                            }
                        }
                        else
                        {
                            senderViewHolder.appUserMusicTick.setImageResource(R.drawable.double_tick);

                        }
                    }
                    String msgTime = DateTimeUtil.tweentyFourTo12New(senderChatMessageModel.getMsgTime());
                    senderViewHolder.appUserMusicTime
                            .setText(msgTime);
                    if (playingAudioId != null) {
                        if (!playingAudioId.equals("-1")
                                && playingAudioId.equals(msgId)) {
                            audioStopGlobal = senderViewHolder.appUserStopButton;
                            audioPlayGlobal = senderViewHolder.appUserPlayButton;
                            globalMusicTime = senderViewHolder.appUserTime;
                            senderViewHolder.appUserPlayButton
                                    .setVisibility(View.GONE);
                            senderViewHolder.appUserStopButton
                                    .setVisibility(View.VISIBLE);
                        } else {
                            senderViewHolder.appUserPlayButton
                                    .setVisibility(View.VISIBLE);
                            senderViewHolder.appUserStopButton
                                    .setVisibility(View.GONE);
                        }
                    } else {
                        senderViewHolder.appUserPlayButton
                                .setVisibility(View.VISIBLE);
                        senderViewHolder.appUserStopButton
                                .setVisibility(View.GONE);
                    }

                    try {
                        ChatMessageModel senderChatMessageModelNew = messageList.get(position - 1);
                        if (senderChatMessageModel.getUserId().equalsIgnoreCase(senderChatMessageModelNew.getUserId())) {
                            senderViewHolder.appUserMusicProfileImage.setVisibility(View.INVISIBLE);
                        } else {
                            senderViewHolder.appUserMusicProfileImage.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if (SharedPreferenceDB.defaultInstance().getAppProfilePic(context).length() > 0)
                        appController.displayUrlImage(senderViewHolder.appUserMusicProfileImage, SharedPreferenceDB.defaultInstance().getAppProfilePic(context), null);
                } else if (senderChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.FILE_TYPE)) {
                    isFromImage = false;
                    senderViewHolder.appUserMusicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserPicParent.setVisibility(View.GONE);
                    senderViewHolder.appUserTextParent.setVisibility(View.GONE);
                    senderViewHolder.appUserStickerParent.setVisibility(View.GONE);
                    senderViewHolder.appUserFileParent.setVisibility(View.VISIBLE);
                    String msgTime = DateTimeUtil.tweentyFourTo12New(senderChatMessageModel.getMsgTime());
                    senderViewHolder.appUserFileTime.setText(msgTime);

                    boolean singleTickFlag = senderChatMessageModel.isSingleTickFlag();
                    if (singleTickFlag) {
                        senderViewHolder.appUserProgressParent
                                .setVisibility(View.VISIBLE);
                        if (!ChatGlobalStates.mediasUploadOnProcess
                                .containsKey(senderChatMessageModel.getChatTempMessageId())) {
                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(senderChatMessageModel
                                            .getChatTempMessageId(), senderViewHolder);
                        } else {
                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(senderChatMessageModel.getChatTempMessageId(), senderViewHolder);
                        }
                        senderViewHolder.appUserFileTick.setImageResource(R.drawable.single_tick);
                    } else {

                        if ((senderSeen != null && senderSeen.equalsIgnoreCase("1")) || ChatActivity.unseenMessageHashList
                                .containsKey(msgId)) {
                            if (senderDisappear != null && !senderDisappear.equalsIgnoreCase("-1") && !disAppearList.containsValue(tempMsgId)) {
                                disAppearList.put(tempMsgId, tempMsgId);
                                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(senderDisappear), 1000, position);
                                myCountDownTimer.start();
                            }
                            senderViewHolder.appUserProgressParent.setVisibility(View.GONE);
                            if(sharedPreferences.getString("read_receipt", "0").equalsIgnoreCase("0") )  {
                                senderViewHolder.appUserFileTick.setImageResource(R.drawable.double_tick_seen_white);
                            }else
                            {
                                senderViewHolder.appUserFileTick.setImageResource(R.drawable.double_tick);
                            }
                        }
                        else
                        {
                            senderViewHolder.appUserProgressParent.setVisibility(View.GONE);
                            senderViewHolder.appUserFileTick.setImageResource(R.drawable.double_tick);
                        }
                    }
                    String fileUrl = senderChatMessageModel.getPostImgUrl();
                    if (fileUrl != null) {
                        String[] nameArray = fileUrl.split("/");
                        String fileName = nameArray[nameArray.length - 1];
                        senderViewHolder.appUserFileName.setText(fileName);
                        if (fileUrl.endsWith(".doc"))
                            senderViewHolder.appUserDocImage.setImageResource(R.drawable.doc);
                        else if (fileUrl.endsWith(".zip"))
                            senderViewHolder.appUserDocImage.setImageResource(R.drawable.zip);
                        else if (fileUrl.endsWith(".pdf"))
                            senderViewHolder.appUserDocImage.setImageResource(R.drawable.pdf);
                    }
                    senderViewHolder.appUserFileName
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String localFilePath = messageList.get(position).getChatFileThumbnail();
                                    if (localFilePath != null) {
                                        File mediaFile = new File(localFilePath);
                                        Intent intent = new Intent(
                                                Intent.ACTION_VIEW);
                                        intent.setDataAndType(
                                                Uri.fromFile(mediaFile),
                                                "application/"
                                                        + localFilePath
                                                        .substring(localFilePath.lastIndexOf(".") + 1));
                                        try {
                                            context
                                                    .startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(
                                                    context, context.getResources().getString(R.string.no_application),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                context, context.getResources().getString(R.string.file_missing),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    if (SharedPreferenceDB.defaultInstance().getAppProfilePic(context).length() > 0)
                        appController.displayUrlImage(senderViewHolder.appUserFileProfilePic, SharedPreferenceDB.defaultInstance().getAppProfilePic(context), null);
                }

                senderViewHolder.appUserTextProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(senderChatMessageModel.getUserId(),true);
                    }
                });
                senderViewHolder.appUserFileProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(senderChatMessageModel.getUserId(),true);
                    }
                });
                senderViewHolder.appUserMusicProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(senderChatMessageModel.getUserId(),true);
                    }
                });
                senderViewHolder.appUserStickerProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(senderChatMessageModel.getUserId(), true);
                    }
                });
                return convertView;
            case 1:
                final ViewHolder receiverViewHolder;
                if (convertView == null) {
                    receiverViewHolder = new ViewHolder();

                    convertView = inflater.inflate(
                            R.layout.other_user_chat_item, null);
                    receiverViewHolder.parentLayout = (RelativeLayout) convertView
                            .findViewById(R.id.chat_item_parent);
                    // parent time
                    receiverViewHolder.otherUserTextParent = (RelativeLayout) convertView.findViewById(R.id.other_user_text_parent);
                    receiverViewHolder.otherUserTime = (TextView) convertView.findViewById(R.id.other_user_time);
                    receiverViewHolder.otherUserIvEdit=(ImageView)convertView.findViewById(R.id.iv_receiver_edit);
                    //Text Content init
                    receiverViewHolder.otherUserTextTime = (TextView) convertView.findViewById(R.id.other_user_text_time);
                    receiverViewHolder.otherUserText = (EmojiconTextView) convertView.findViewById(R.id.other_user_text);
                    receiverViewHolder.otherUserText.setEmojiconSize(70);
                    receiverViewHolder.otherUserTextImage = (ImageView) convertView.findViewById(R.id.other_user_text_profile_image);


                    //Pic Content init
                    receiverViewHolder.otherUserPicParent = (RelativeLayout) convertView.findViewById(R.id.other_user_pic_parent);
                    receiverViewHolder.otherUserPicTime = (TextView) convertView.findViewById(R.id.other_user_pic_time);
                    receiverViewHolder.otherUserPic = (ImageView) convertView.findViewById(R.id.other_user_image);
                    receiverViewHolder.otherUserProfilePic = (ImageView) convertView.findViewById(R.id.other_user_pic_profile_image);
                    receiverViewHolder.otherUserImageProgress = (ProgressBar) convertView.findViewById(R.id.other_user_image_progress);
                    receiverViewHolder.otherUserDescription = (TextView) convertView.findViewById(R.id.other_user_description);
                    receiverViewHolder.otherUserImageFrontTransparent =(RelativeLayout)convertView.findViewById(R.id.rl_tranparent_img);
                    receiverViewHolder.otherUserDownload =(ImageView)convertView.findViewById(R.id.other_user_image_download);


                    //Sticker Content init
                    receiverViewHolder.otherUserStickerParent = (RelativeLayout) convertView.findViewById(R.id.other_user_sticker_parent);
                    receiverViewHolder.otherUserStickerTime = (TextView) convertView.findViewById(R.id.other_user_sticker_time);
                    receiverViewHolder.otherUserStickerProfile = (ImageView) convertView.findViewById(R.id.other_user_sticker_profile_image);
                    receiverViewHolder.otherUserSticker = (ImageView) convertView.findViewById(R.id.other_user_sticker);
                    receiverViewHolder.otherUserStickerProgress = (ProgressBar) convertView.findViewById(R.id.other_user_sticker_progress);


                    //Audio Content init
                    receiverViewHolder.otherUserMusicParent = (RelativeLayout) convertView.findViewById(R.id.other_user_music_parent);
                    receiverViewHolder.otherUserMusicPlayStop = (RelativeLayout) convertView.findViewById(R.id.other_user_music_play_stop);
                    receiverViewHolder.otherUserPlayButton = (ImageView) convertView.findViewById(R.id.other_user_play_button);
                    receiverViewHolder.otherUserMusicDownload = (ImageView) convertView.findViewById(R.id.other_user_download__music_button);

                    receiverViewHolder.otherUserStopButton = (ImageView) convertView.findViewById(R.id.other_user_stop_button);
                    receiverViewHolder.otherUserMusicWave = (ImageView) convertView.findViewById(R.id.other_user_music_wave);
                    receiverViewHolder.otherUserProgressbarParent = (RelativeLayout) convertView.findViewById(R.id.other_user_progress_bar_parent);
                    receiverViewHolder.otherUserMusicSeekBar = (SeekBar) convertView.findViewById(R.id.other_user_music_seekBar);
                    receiverViewHolder.otherUserMusicDuration = (TextView) convertView.findViewById(R.id.other_user_music_duration);
                    receiverViewHolder.otherUserMusicTime = (TextView) convertView.findViewById(R.id.other_user_music_time);
                    receiverViewHolder.appUserProgressbarText = (TextView) convertView.findViewById(R.id.other_user_progress_bar_text);
                    receiverViewHolder.otherUserMusicProfileImage = (ImageView) convertView.findViewById(R.id.other_user_music_profile_image);


                    //file Content init
                    receiverViewHolder.otherUserFileParent = (RelativeLayout) convertView.findViewById(R.id.other_user_file_parent);
                    receiverViewHolder.otherUserProgressParent = (RelativeLayout) convertView.findViewById(R.id.other_user_file_progress_parent);
                    receiverViewHolder.otherUserFileTime = (TextView) convertView.findViewById(R.id.other_user_file_time);
                    receiverViewHolder.otherUserFileName = (EmojiconTextView) convertView.findViewById(R.id.other_user_file_text);
                    receiverViewHolder.otherUserFileName.setEmojiconSize(40);
                    receiverViewHolder.otherUserFileProfilePic = (ImageView) convertView.findViewById(R.id.other_user_file_profile_image);
                    receiverViewHolder.appUserFilePerText = (TextView) convertView.findViewById(R.id.other_user_percentage_text);
                    receiverViewHolder.appUserFileProgress = (ProgressBar) convertView.findViewById(R.id.other_user_file_progressbar);
                    receiverViewHolder.otherUserDocImage = (ImageView) convertView.findViewById(R.id.other_user_doc_image);
                    convertView.setTag(receiverViewHolder);
                } else {
                    receiverViewHolder = (ViewHolder) convertView.getTag();
                }
                receiverViewHolder.chatMessageAdapter = this;
                receiverViewHolder.position = position;

                if( GlobalState.CHECK_BACK_PRESSE)
                {

                    receiverViewHolder.parentLayout.setVisibility(View.GONE);
                }

                final   ChatMessageModel receiverChatMessageModel = messageList.get(position);
                String msgReceiverTime = DateTimeUtil.tweentyFourTo12New(receiverChatMessageModel.getMsgTime());
                String msgReceiverId = receiverChatMessageModel.getMsgId();
                //    String receiverProfilePic = receiverChatMessageModel.getFriendsZeroIndexPicUrl();
                if(receiverChatMessageModel.getChatEditMsg()!=null && receiverChatMessageModel.getChatEditMsg().equalsIgnoreCase("2"))
                {
                    receiverViewHolder.otherUserIvEdit.setVisibility(View.VISIBLE);
                }else
                {
                    receiverViewHolder.otherUserIvEdit.setVisibility(View.GONE);
                }
                String receiverProfilePic =GlobalState.RECEIVER_PROFILE_PIC;
                receiverDisapper = receiverChatMessageModel.getChatDisappear();
                receiverMsgId = receiverChatMessageModel.getMsgId();

                if (receiverChatMessageModel.getMessageType().equalsIgnoreCase(ChatGlobalStates.MESSAGE_TYPE))
                {
                    receiverViewHolder.otherUserTextParent.setVisibility(View.VISIBLE);
                    receiverViewHolder.otherUserPicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserMusicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserStickerParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserTextTime.setText(msgReceiverTime);
                    if (ChatActivity.editedMsgHashList
                            .containsKey(receiverMsgId))
                    {
                        String actualMsg,editId;
                        EditedMsgBean editedMsgBean = ChatActivity.editedMsgHashList
                                .get(receiverMsgId);
                        actualMsg = editedMsgBean.getActualMsg();
                        editId   =editedMsgBean.getEditId();
                        messageList.get(position).setActualMsg(
                                actualMsg);

                        if(editId!=null ) {
                            if (editId.equalsIgnoreCase("2")) {
                                receiverViewHolder.otherUserIvEdit.setVisibility(View.VISIBLE);
                            } else {
                                receiverViewHolder.otherUserIvEdit.setVisibility(View.GONE);
                            }
                        }
                    }

                    try
                    {
                        ChatMessageModel receiverChatMessageModelNew = messageList.get(position - 1);
                        if (receiverChatMessageModel.getUserId().equalsIgnoreCase(receiverChatMessageModelNew.getUserId())) {
                            receiverViewHolder.otherUserTextImage.setVisibility(View.INVISIBLE);
                        } else {
                            receiverViewHolder.otherUserTextImage.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                 //   receiverViewHolder.otherUserText.setText(receiverChatMessageModel.getActualMsg());

                    String  faqsearchstr=receiverChatMessageModel.getActualMsg().toLowerCase(Locale.getDefault());
                    if (faqsearchstr.contains(searchstring))
                    {
                        int startPos = faqsearchstr.indexOf(searchstring);
                        int endPos = startPos + searchstring.length();

                        Spannable spanText = Spannable.Factory.getInstance().newSpannable(receiverChatMessageModel.getActualMsg()); // <- EDITED: Use the original string, as `country` has been converted to lowercase.
                        spanText.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        receiverViewHolder.otherUserText.setText(spanText, TextView.BufferType.SPANNABLE);
                    }
                    else
                    {
                        receiverViewHolder.otherUserText.setText(receiverChatMessageModel.getActualMsg());
                    }
                    if (receiverDisapper != null && !receiverDisapper.equalsIgnoreCase("0") &&  !receiverDisapper.equalsIgnoreCase("-1") && !disAppearList.containsValue(receiverMsgId))
                    {
                        disAppearList.put(receiverMsgId, receiverMsgId);
                        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(receiverDisapper), 15000, position);
                        myCountDownTimer.start();
                    }
                    appController.displayUrlImage(receiverViewHolder.otherUserTextImage, receiverProfilePic, null);
                } else if (receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE) || receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.LOCATION_TYPE))
                {
                    isFromImage = true;
                    receiverViewHolder.otherUserPicParent.setVisibility(View.VISIBLE);
                    receiverViewHolder.otherUserTextParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserMusicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserStickerParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserPicTime
                            .setText(msgReceiverTime);
                    receiverViewHolder.otherUserPic.setImageResource(android.R.color.transparent);
                    if (receiverChatMessageModel.getMessageType()
                            .equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE))
                    {
                        String picPsg = receiverChatMessageModel.getActualMsg();
                        if (picPsg != null && picPsg.trim().length() > 0)
                        {
                            receiverViewHolder.otherUserDescription.setText(receiverChatMessageModel.getActualMsg());
                        }
                        String postImgUrl = receiverChatMessageModel.getPostImgUrl();

                        if(receiverChatMessageModel.getImageDownlaod()==0)
                        {
                            receiverViewHolder.otherUserImageFrontTransparent.setVisibility(View.VISIBLE);
                            receiverViewHolder.otherUserDownload.setVisibility(View.VISIBLE);
                            appController.displayUrlImage(receiverViewHolder.otherUserPic, postImgUrl, receiverViewHolder.otherUserImageProgress);

                        }else
                        {
                            receiverViewHolder.otherUserImageFrontTransparent.setVisibility(View.GONE);
                            receiverViewHolder.otherUserDownload.setVisibility(View.GONE);
                            appController.displayUrlImage(receiverViewHolder.otherUserPic, postImgUrl, receiverViewHolder.otherUserImageProgress);

                        }
                       final  Handler handler = new Handler();
                       final Runnable runnable = new Runnable()
                       {
                            public void run()
                            {
                                receiverViewHolder.otherUserImageProgress.setVisibility(View.GONE);
                                receiverViewHolder.otherUserImageFrontTransparent.setVisibility(View.GONE);
                                receiverViewHolder.otherUserDownload.setVisibility(View.GONE);

                            }
                        };
                        receiverViewHolder.otherUserDownload.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                chatMessageDataSource.open();
                                receiverViewHolder.otherUserImageProgress.setVisibility(View.VISIBLE);
                                receiverViewHolder.otherUserDownload.setVisibility(View.GONE);
                                chatMessageDataSource.updateDownloadImage(receiverChatMessageModel.getMsgId(),1);
                                handler.postDelayed(runnable, 2000);
                                interfaceRefreshListview.callRefreshListner();

                            }
                        });

                    } else
                    {
                        String lat = receiverChatMessageModel.getLatitude();
                        String lan = receiverChatMessageModel.getLangitude();
                        String locationUrl = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lan + "&zoom=18&size=550x400&sensor=false&markers=color:red|label:|" + lat + "," + lan;
                        appController.displayGoogleUrlImage(receiverViewHolder.otherUserPic, locationUrl, receiverViewHolder.otherUserImageProgress);
                    }
                    appController.displayUrlImage(receiverViewHolder.otherUserProfilePic, receiverProfilePic, null);

                    receiverViewHolder.otherUserPic.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            GlobalState.CHECK_BACK_PRESSE=false;
                            interfacePicSelection.picSelectionResponse(receiverViewHolder.parentLayout,position,false);
                            return false;
                        }
                    });
                    receiverViewHolder.otherUserPic
                            .setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    GlobalState.CHECK_BACK_PRESSE=false;
                                    if(ChatGlobalStates.ISLONGPRESS)
                                    {
                                        interfacePicSelection.picSelectionResponse(receiverViewHolder.parentLayout,position,true);
                                    }
                                    else
                                    if (messageList.get(
                                            position).getMessageType().equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE))
                                    {
                                        if(receiverChatMessageModel.getImageDownlaod()==1)
                                        {
                                            String postImgUrl = messageList.get(
                                                    position).getPostImgUrl();
                                            Intent intent = new Intent(
                                                    context.getApplicationContext(),
                                                    PicViewerActivity.class);
                                            intent.putExtra("imageURL", postImgUrl);
                                            intent.putExtra("disappear", receiverChatMessageModel.getChatDisappear());
                                            context.startActivity(intent);
                                        }
                                    } else
                                    {
                                        String locationLat = messageList
                                                .get(position)
                                                .getLatitude();
                                        String locationLan = messageList
                                                .get(position)
                                                .getLangitude();
                                        String coordinates = "http://maps.google.com/maps?daddr=" + locationLat + "," + locationLan;
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(coordinates));
                                        context.startActivity(intent);
                                    }
                                }
                            });


                    try {
                        ChatMessageModel receiverChatMessageModelNew = messageList.get(position - 1);
                        if (receiverChatMessageModel.getUserId().equalsIgnoreCase(receiverChatMessageModelNew.getUserId())) {
                            receiverViewHolder.otherUserProfilePic.setVisibility(View.INVISIBLE);
                        } else {
                            receiverViewHolder.otherUserProfilePic.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

//                    final  MediaPlayerTag parrentLayoutReceiver = new MediaPlayerTag();
//                    parrentLayoutReceiver.setPlayPauseParent(receiverViewHolder.parentLayout);
//                    receiverViewHolder.parentLayout.setTag(parrentLayoutReceiver);
//                    receiverViewHolder.otherUserPic.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View v) {
//                            interfacePicSelection.picSelectionResponse(parrentLayoutReceiver.getPlayPauseParent(), position);
//                            return false;
//                        }
//                    });
                    if (receiverDisapper != null && !receiverDisapper.equalsIgnoreCase("0") && !receiverDisapper.equalsIgnoreCase("0")  && !receiverDisapper.equalsIgnoreCase("-1") && !disAppearList.containsValue(receiverMsgId)) {
                        disAppearList.put(receiverMsgId, receiverMsgId);
                        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(receiverDisapper), 5000, position);
                        myCountDownTimer.start();
                    }

                } else if (receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.STICKERS_TYPE)) {
                    receiverViewHolder.otherUserStickerParent.setVisibility(View.VISIBLE);
                    receiverViewHolder.otherUserPicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserTextParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserMusicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserStickerTime
                            .setText(msgReceiverTime);
                    receiverViewHolder.otherUserSticker.setImageResource(android.R.color.transparent);
                    String postImgUrl = receiverChatMessageModel.getPostImgUrl();
                    String stickerId = receiverChatMessageModel.getChatStickerAudioBuzzId();
                    if (stickerId != null && !stickerId.isEmpty()) {
                        String localPath = GlobalState.STICKERS_GALLERY + "/" + stickerId + ".png";
                        File file = null;
                        try {
                            file = new File(localPath);
                        } catch (Exception e) {
                            e.getStackTrace();
                            appController.displayUrlImage(receiverViewHolder.otherUserSticker, postImgUrl, receiverViewHolder.otherUserStickerProgress);
                        }
                        if (file != null && file.isFile()) {
                            appController.displayFileImage(receiverViewHolder.otherUserSticker, postImgUrl, receiverViewHolder.otherUserStickerProgress);
                        } else {
                            appController.displayUrlImage(receiverViewHolder.otherUserSticker, postImgUrl, receiverViewHolder.otherUserStickerProgress);
                        }
                    } else {
                        appController.displayUrlImage(receiverViewHolder.otherUserSticker, postImgUrl, receiverViewHolder.otherUserStickerProgress);
                    }

                    try {
                        ChatMessageModel receiverChatMessageModelNew = messageList.get(position - 1);
                        if (receiverChatMessageModel.getUserId().equalsIgnoreCase(receiverChatMessageModelNew.getUserId())) {
                            receiverViewHolder.otherUserStickerProfile.setVisibility(View.INVISIBLE);
                        } else {
                            receiverViewHolder.otherUserStickerProfile.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    appController.displayUrlImage(receiverViewHolder.otherUserSticker, postImgUrl, receiverViewHolder.otherUserStickerProgress);
                    appController.displayUrlImage(receiverViewHolder.otherUserStickerProfile, receiverProfilePic, null);
                    if (receiverDisapper != null && !receiverDisapper.equalsIgnoreCase("0") && !receiverDisapper.equalsIgnoreCase("-1") && !disAppearList.containsValue(receiverMsgId)) {
                        disAppearList.put(receiverMsgId, receiverMsgId);
                        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(receiverDisapper), 1000, position);
                        myCountDownTimer.start();
                    }
                } else if (receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE))
                {
                    receiverViewHolder.otherUserPicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserTextParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserMusicParent.setVisibility(View.VISIBLE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserStickerParent.setVisibility(View.GONE);

                    receiverViewHolder.otherUserMusicSeekBar.setOnTouchListener(new View.OnTouchListener()
                    {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });

                    if (receiverChatMessageModel.getMessageType()
                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
                        receiverViewHolder.otherUserMusicWave.setVisibility(View.VISIBLE);
                    } else {
                        receiverViewHolder.otherUserMusicWave.setVisibility(View.GONE);
                    }
                    if (playingAudioId != null) {
                        if (!playingAudioId.equals("-1")
                                && playingAudioId.equals(msgReceiverId))
                        {
                            audioStopGlobal = receiverViewHolder.otherUserStopButton;
                            audioPlayGlobal = receiverViewHolder.otherUserPlayButton;
                            globalMusicTime = receiverViewHolder.otherUserTime;
                            receiverViewHolder.otherUserPlayButton
                                    .setVisibility(View.GONE);
                            receiverViewHolder.otherUserStopButton
                                    .setVisibility(View.VISIBLE);
                        } else {
                            receiverViewHolder.otherUserPlayButton
                                    .setVisibility(View.VISIBLE);
                            receiverViewHolder.otherUserStopButton
                                    .setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        receiverViewHolder.otherUserPlayButton
                                .setVisibility(View.VISIBLE);
                        receiverViewHolder.otherUserStopButton
                                .setVisibility(View.GONE);
                    }
                    receiverViewHolder.otherUserMusicTime
                            .setText(msgReceiverTime);
                    String postAudioUrl = receiverChatMessageModel.getPostImgUrl();

                    if (postAudioUrl.contains("/"))
                    {
                        receiverViewHolder.otherUserProgressbarParent
                                .setVisibility(View.GONE);
                        receiverViewHolder.otherUserPlayButton
                                .setVisibility(View.VISIBLE);
                        receiverViewHolder.otherUserMusicDownload.setVisibility(View.GONE);
                        File audioFileLocal = new File(postAudioUrl);
                        playAudio(receiverViewHolder.otherUserMusicPlayStop,
                                receiverViewHolder.otherUserStopButton,
                                receiverViewHolder.otherUserPlayButton, receiverViewHolder.otherUserMusicDuration, receiverViewHolder.otherUserMusicSeekBar, audioFileLocal);
                    } else
                    {
                       // receiverViewHolder.otherUserProgressbarParent
                             //   .setVisibility(View.VISIBLE);
                        receiverViewHolder.otherUserPlayButton
                                .setVisibility(View.INVISIBLE);
                        if (!ChatGlobalStates.mediasUploadOnProcess
                                .containsKey(receiverChatMessageModel.getMsgId()))
                        {

                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(receiverChatMessageModel
                                           .getMsgId(), receiverViewHolder);

                            if(!new CommonUtil().getAudioAutoMobileDataDownload(context) && !new CommonUtil().getAudioAutoWifiDownload(context))
                             {

                                 receiverViewHolder.otherUserPlayButton.setVisibility(View.GONE);
                                 receiverViewHolder.otherUserMusicDownload.setVisibility(View.VISIBLE);

                              }
                            else if(new CommonUtil().getAudioAutoMobileDataDownload(context) && new CommonUtil().getAudioAutoWifiDownload(context))
                            {
                                   receiverViewHolder.otherUserProgressbarParent
                                     .setVisibility(View.VISIBLE);
                                new AWSMediaDownloader(GlobalState.ONE_TO_ONE_AUDIO,
                                        context, receiverChatMessageModel.getMsgId(), receiverChatMessageModel.getMessageType()).execute(receiverChatMessageModel.getPostImgUrl());

                             }
                            else if(new CommonUtil().getAudioAutoMobileDataDownload(context))
                            {

                                if(new CommonUtil().chkMobileStatus(context))
                                {
                                     receiverViewHolder.otherUserProgressbarParent
                                     .setVisibility(View.VISIBLE);
                                    new AWSMediaDownloader(GlobalState.ONE_TO_ONE_AUDIO,
                                            context, receiverChatMessageModel.getMsgId(), receiverChatMessageModel.getMessageType()).execute(receiverChatMessageModel.getPostImgUrl());

                                }else
                                {
                                    receiverViewHolder.otherUserPlayButton.setVisibility(View.GONE);
                                    receiverViewHolder.otherUserMusicDownload.setVisibility(View.VISIBLE);
                                }

                            }
                            else if(new CommonUtil().getAudioAutoWifiDownload(context))
                            {

                                if(new CommonUtil().chkWifiStatus(context))
                                {
                                     receiverViewHolder.otherUserProgressbarParent
                                    .setVisibility(View.VISIBLE);
                                    new AWSMediaDownloader(GlobalState.ONE_TO_ONE_AUDIO,
                                            context, receiverChatMessageModel.getMsgId(), receiverChatMessageModel.getMessageType()).execute(receiverChatMessageModel.getPostImgUrl());
                                }else
                                {
                                    receiverViewHolder.otherUserPlayButton.setVisibility(View.GONE);
                                    receiverViewHolder.otherUserMusicDownload.setVisibility(View.VISIBLE);
                                }
                            }
                            }
                        else
                        {

                            receiverViewHolder.otherUserPlayButton.setVisibility(View.GONE);
                            receiverViewHolder.otherUserMusicDownload.setVisibility(View.VISIBLE);
                            ChatGlobalStates.mediasUploadOnProcess
                                    .put(receiverChatMessageModel.getMsgId(), receiverViewHolder);
                        }
                        receiverViewHolder.otherUserMusicDownload.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                receiverViewHolder.otherUserMusicDownload.setVisibility(View.INVISIBLE);
                                receiverViewHolder.otherUserProgressbarParent
                                        .setVisibility(View.VISIBLE);
                                new AWSMediaDownloader(GlobalState.ONE_TO_ONE_AUDIO,
                                        context, receiverChatMessageModel.getMsgId(), receiverChatMessageModel.getMessageType()).execute(receiverChatMessageModel.getPostImgUrl());

                            }
                        });
                    }
                    try {
                        ChatMessageModel receiverChatMessageModelNew = messageList.get(position - 1);
                        if (receiverChatMessageModel.getUserId().equalsIgnoreCase(receiverChatMessageModelNew.getUserId()))
                        {
                            receiverViewHolder.otherUserMusicProfileImage.setVisibility(View.INVISIBLE);
                        } else
                        {
                            receiverViewHolder.otherUserMusicProfileImage.setVisibility(View.VISIBLE);

                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    appController.displayUrlImage(receiverViewHolder.otherUserMusicProfileImage, receiverProfilePic, null);
                    if (receiverDisapper != null && !receiverDisapper.equalsIgnoreCase("-1") && !disAppearList.containsValue(receiverMsgId)) {
                        ChatGlobalStates.saveReceiverDisapper=receiverDisapper;
                        ChatGlobalStates.selectedReceiverMsgId = receiverMsgId;
                        ChatGlobalStates.selectedPosition =position;
                    }
                } else if (receiverChatMessageModel.getMessageType()
                        .equalsIgnoreCase(ChatGlobalStates.FILE_TYPE))
                {
                    receiverViewHolder.otherUserPicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserTextParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserMusicParent.setVisibility(View.GONE);
                    receiverViewHolder.otherUserFileParent.setVisibility(View.VISIBLE);
                    if (receiverDisapper != null && !receiverDisapper.equalsIgnoreCase("-1") && !disAppearList.containsValue(receiverMsgId)) {
                        disAppearList.put(receiverMsgId, receiverMsgId);
                        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(Integer.valueOf(receiverDisapper), 1000, position);
                        myCountDownTimer.start();
                    }
                    String fileUrl = receiverChatMessageModel.getPostImgUrl();
                    if (fileUrl != null) {
                        String[] nameArray = fileUrl.split("/");
                        String fileName = nameArray[nameArray.length - 1];
                        receiverViewHolder.otherUserFileName.setText(fileName);
                        if (!fileUrl.contains("/")) {
                            if (!ChatGlobalStates.mediasUploadOnProcess
                                    .containsKey(receiverChatMessageModel.getMsgId())) {
                                if (CommonUtil.isInternetConnected(context)) {
                                    downloadFiles(receiverViewHolder, position);
                                }
                            } else {
                                ChatGlobalStates.mediasUploadOnProcess
                                        .put(receiverChatMessageModel.getMsgId(), receiverViewHolder);
                            }
                        }
                        if (fileUrl.contains("/")) {
                            receiverViewHolder.otherUserProgressParent.setVisibility(View.GONE);
                        }
                        if (fileUrl != null) {
                            if (fileUrl.endsWith(".doc"))
                                receiverViewHolder.otherUserDocImage.setImageResource(R.drawable.doc);
                            else if (fileUrl.endsWith(".zip"))
                                receiverViewHolder.otherUserDocImage.setImageResource(R.drawable.zip);
                            else if (fileUrl.endsWith(".pdf"))
                                receiverViewHolder.otherUserDocImage.setImageResource(R.drawable.pdf);
                        }
                        receiverViewHolder.otherUserFileName
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String fileUrl = messageList.get(position).getPostImgUrl();
                                        if (fileUrl != null && fileUrl.contains("/")) {
                                            File mediaFile = new File(fileUrl);
                                            Intent intent = new Intent(
                                                    Intent.ACTION_VIEW);
                                            intent.setDataAndType(
                                                    Uri.fromFile(mediaFile),
                                                    "application/"
                                                            + fileUrl
                                                            .substring(fileUrl
                                                                    .lastIndexOf(".") + 1));
                                            try {
                                                context
                                                        .startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(
                                                        context, context.getResources().getString(R.string.no_application),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(
                                                    context, context.getResources().getString(R.string.file_missing),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    receiverViewHolder.otherUserFileTime.setText(msgReceiverTime);
                    appController.displayUrlImage(receiverViewHolder.otherUserFileProfilePic, receiverProfilePic, null);
                }

                receiverViewHolder.otherUserTextImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(receiverChatMessageModel.getUserId(),false);
                    }
                });
                receiverViewHolder.otherUserProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(receiverChatMessageModel.getUserId(),false);
                    }
                });
                receiverViewHolder.otherUserMusicProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(receiverChatMessageModel.getUserId(),false);
                    }
                });
                receiverViewHolder.otherUserStickerProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToOtherProfile(receiverChatMessageModel.getUserId(),false);
                    }
                });
                return convertView;
        }
        return null;
    }


    private void downloadFiles(ViewHolder viewHolder, final int position) {
        final ChatMessageModel chatMessageModel = messageList.get(position);
        if (!ChatGlobalStates.mediasUploadOnProcess
                .containsKey(chatMessageModel.getMsgId())) {
            ChatGlobalStates.mediasUploadOnProcess
                    .put(chatMessageModel
                            .getMsgId(), viewHolder);
        } else {
            ChatGlobalStates.mediasUploadOnProcess
                    .put(chatMessageModel.getMsgId(), viewHolder);
        }
        new AWSMediaDownloader(GlobalState.ONE_TO_ONE_FILE,
                context, chatMessageModel.getMsgId(), chatMessageModel.getMessageType()).execute(chatMessageModel.getPostImgUrl());
    }

    @Override
    public void asyncFinished(String response, int position) {
        final ChatMessageModel chatMessageModel = messageList.get(position);
        if (ChatGlobalStates.mediasOnProcess
                .containsKey(chatMessageModel
                        .getMsgId())) {
            ChatGlobalStates.mediasOnProcess
                    .remove(chatMessageModel
                            .getMsgId());
        }
        messageList.get(position).setPostImgUrl(
                response);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public RelativeLayout parentLayout;
        public RelativeLayout receiverparentLayout;
        public TextView appUserTime, appUserTextTime;
        public EmojiconTextView appUserText;
        public ImageView appUserTextProfilePic, appUserTextTick,appUserEdit;
        public RelativeLayout appUserTextParent;

        public RelativeLayout appUserPicParent;
        public TextView appUserPicTime, appUserDescription;
        public ProgressBar appUserImageProgress;
        public ImageView appUserPicProfilePic, appUserPicTick, appUserPic;

        public RelativeLayout appUserStickerParent;
        public TextView appUserStickerTime;
        public ImageView appUserStickerProfilePic, appUserStickerTick, appStickerPic;
        public ProgressBar appUserStickerProgress;

        public RelativeLayout appUserMusicParent, appUserMusicPlayStop, appUserProgressbarParent;
        public ImageView appUserPlayButton, appUserStopButton, appUserMusicWave, appUserMusicTick, appUserMusicProfileImage;
        public SeekBar appUserMusicSeekBar;
        public TextView appUserMusicDuration, appUserMusicTime, appUserProgressbarText;
        public  ProgressBar appUserProgressBar;
        public ImageView appUserUploadImage;

        public TextView otherUserTime, otherUserTextTime;
        public EmojiconTextView otherUserText;
        public ImageView otherUserTextImage;
        public RelativeLayout otherUserTextParent;

        public RelativeLayout otherUserPicParent;
        public TextView otherUserPicTime, otherUserDescription;
        public RelativeLayout otherUserImageFrontTransparent;
        public ImageView otherUserDownload;
        public ProgressBar otherUserImageProgress;
        public ImageView otherUserProfilePic, otherUserPic;

        public RelativeLayout otherUserStickerParent;
        public TextView otherUserStickerTime;
        public ImageView otherUserStickerProfile, otherUserSticker;
        public ProgressBar otherUserStickerProgress;

        public RelativeLayout otherUserMusicParent, otherUserMusicPlayStop, otherUserProgressbarParent;
        public ImageView otherUserPlayButton, otherUserStopButton, otherUserMusicWave, otherUserMusicProfileImage,otherUserMusicDownload;
        public SeekBar otherUserMusicSeekBar;
        public TextView otherUserMusicDuration, otherUserMusicTime;


        public RelativeLayout appUserFileParent;
        public RelativeLayout appUserProgressParent;
        public TextView appUserFileTime, appUserFilePerText;
        public TextView appUserFileName;
        public ImageView appUserFileProfilePic, appUserFileTick, appUserDocImage;
        public ProgressBar appUserFileProgress;


        public RelativeLayout otherUserFileParent, otherUserProgressParent;
        public TextView otherUserFileTime;
        public EmojiconTextView otherUserFileName;
        public ImageView otherUserFileProfilePic, otherUserDocImage;


        public ChatMessageAdapter chatMessageAdapter;
        public int position;
        public ImageView otherUserIvEdit;

    }

    @Override
    public Filter getFilter() {
        if (filterName == null)
            filterName = new FilterName();
        return filterName;
    }


    private class FilterName extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList<ChatMessageModel> list = new ArrayList<>();
            for (int i = 0; i < fixedMessageList.size(); i++) {
                if (fixedMessageList.get(i).getActualMsg()
                        .toLowerCase(Locale.getDefault()).contains(constraint)) {
                    list.add(fixedMessageList.get(i));
                }
            }
            results.count = list.size();
            results.values = list;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            messageList = (ArrayList<ChatMessageModel>)
                    results.values;notifyDataSetChanged();
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        int position;

        public MyCountDownTimer(long startTime, long interval, int position) {
            super(startTime, interval);
            this.position = position;
        }

        @Override
        public void onFinish() {
            try {
                ChatMessageDataSource chatMessageDataSource = new ChatMessageDataSource(context);
                chatMessageDataSource.open();
                chatMessageDataSource.deleteSelectedChatMessage(messageList.get(position).getMsgId());
                chatMessageDataSource.close();
                messageList.remove(position);
                notified = true;
                interfaceRefreshListview.refreshList();
                notifyDataSetChanged();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onTick(long milliseconds) {

        }

    }

    public MediaPlayer mediaPlayer;
    // private double startTime = 0;
    // private double finalTime = 0;
    private Handler myHandler = new Handler();
    private SeekBar globalSeekbar;
    private TextView globalMusicTime;
    // private int audioPlayContinue = -1;
    private ImageView audioPlayGlobal, audioStopGlobal;
    private RelativeLayout playPauseGlobalParent;
    private String playingAudioId = null;
    private File globalAudioFile;

    @SuppressWarnings("static-access")

    public void playAudio(final RelativeLayout playPauseParent,
                          final ImageView audioStop,
                          final ImageView audioPlay, final TextView audioTime, final SeekBar seekBar,
                          final File audioFile) {

        if (audioFile != null)
            audioTime.setText(CommonUtil
                    .mediaDurationFormat(CommonUtil
                            .mediaToDurationPath(audioFile.getAbsolutePath())));
        MediaPlayerTag mediaPlayerTag = new MediaPlayerTag();
        mediaPlayerTag.setAudioTime(audioTime);
        mediaPlayerTag.setPlayImage(audioPlay);
        mediaPlayerTag.setStopImage(audioStop);
        mediaPlayerTag.setSeekBar(seekBar);
        mediaPlayerTag.setFile(audioFile);
        mediaPlayerTag.setPlayPauseParent(playPauseParent);
        playPauseParent.setTag(mediaPlayerTag);

        audioStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isCheckMediapLayerPlaying = true;
                    audioPlay.setVisibility(View.VISIBLE);
                    audioStop.setVisibility(View.GONE);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        playPauseParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AudioManager audioManager =  (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
                audioManager.setSpeakerphoneOn(true);
                MediaPlayerTag mediaPlayerTag = (MediaPlayerTag) view.getTag();
                if (mediaPlayerTag.getFile() != null && mediaPlayerTag.getFile().isFile()) {
                    if(!isCheckMediapLayerPlaying) {
                        if (audioPlayGlobal == null
                                || audioStopGlobal == null) {
                            audioPlayGlobal = mediaPlayerTag.getPlayImage();
                            audioStopGlobal = mediaPlayerTag.getStopImage();
                            playPauseGlobalParent = mediaPlayerTag.getPlayPauseParent();
                            globalSeekbar = mediaPlayerTag.getSeekBar();
                            globalMusicTime = mediaPlayerTag.getAudioTime();
                            globalAudioFile = mediaPlayerTag.getFile();
                        } else {
                            if (!playPauseGlobalParent
                                    .equals(mediaPlayerTag.getPlayPauseParent())) {
                                audioStopGlobal.setVisibility(View.INVISIBLE);
                                audioPlayGlobal.setVisibility(View.VISIBLE);
                                globalSeekbar.setProgress(0);
                                globalMusicTime.setText(CommonUtil
                                        .mediaDurationFormat(CommonUtil
                                                .mediaToDurationPath(globalAudioFile
                                                        .getAbsolutePath())));
                            }
                            playPauseGlobalParent = mediaPlayerTag.getPlayPauseParent();
                            audioPlayGlobal = mediaPlayerTag.getPlayImage();
                            audioStopGlobal = mediaPlayerTag.getStopImage();
                            globalSeekbar = mediaPlayerTag.getSeekBar();
                            globalMusicTime = mediaPlayerTag.getAudioTime();
                            globalAudioFile = mediaPlayerTag.getFile();
                        }
                        if (mediaPlayerTag.getPlayImage().isShown()) {
                            audioPlay.setVisibility(View.GONE);
                            audioStop.setVisibility(View.VISIBLE);
                            playingAudioId = "-1";
                            isCheckMediapLayerPlaying=false;
                            GlobalState.AUDIO_FILE=globalAudioFile;
                            eventOfPlayBtn();
                            touchlisterFalse( globalSeekbar);
                        } else {
                            playingAudioId = null;
                            mediaPlayerTag.getStopImage().setVisibility(View.GONE);
                            mediaPlayerTag.getPlayImage().setVisibility(View.VISIBLE);
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                    myHandler.removeCallbacks(updateSongTime);
                                    globalSeekbar.setProgress(0);
                                    globalMusicTime.setText(CommonUtil
                                            .mediaDurationFormat(CommonUtil
                                                    .mediaToDurationPath(globalAudioFile
                                                            .getAbsolutePath())));
                                }
                            }
                        }
                    }else
                    {
                        if(GlobalState.AUDIO_FILE.equals(mediaPlayerTag.getFile())) {
                            isCheckMediapLayerPlaying = false;
                            audioPlay.setVisibility(View.GONE);
                            audioStop.setVisibility(View.VISIBLE);
                            mediaPlayer.start();
                        }else
                        {
                            if (audioPlayGlobal == null
                                    || audioStopGlobal == null) {
                                audioPlayGlobal = mediaPlayerTag.getPlayImage();
                                audioStopGlobal = mediaPlayerTag.getStopImage();
                                playPauseGlobalParent = mediaPlayerTag.getPlayPauseParent();
                                globalSeekbar = mediaPlayerTag.getSeekBar();
                                globalMusicTime = mediaPlayerTag.getAudioTime();
                                globalAudioFile = mediaPlayerTag.getFile();
                            } else {
                                if (!playPauseGlobalParent
                                        .equals(mediaPlayerTag.getPlayPauseParent())) {
                                    audioStopGlobal.setVisibility(View.INVISIBLE);
                                    audioPlayGlobal.setVisibility(View.VISIBLE);
                                    globalSeekbar.setProgress(0);
                                    globalMusicTime.setText(CommonUtil
                                            .mediaDurationFormat(CommonUtil
                                                    .mediaToDurationPath(globalAudioFile
                                                            .getAbsolutePath())));
                                }
                                playPauseGlobalParent = mediaPlayerTag.getPlayPauseParent();
                                audioPlayGlobal = mediaPlayerTag.getPlayImage();
                                audioStopGlobal = mediaPlayerTag.getStopImage();
                                globalSeekbar = mediaPlayerTag.getSeekBar();
                                globalMusicTime = mediaPlayerTag.getAudioTime();
                                globalAudioFile = mediaPlayerTag.getFile();
                            }
                            if (mediaPlayerTag.getPlayImage().isShown()) {
                                audioPlay.setVisibility(View.GONE);
                                audioStop.setVisibility(View.VISIBLE);
                                playingAudioId = "-1";
                                isCheckMediapLayerPlaying=false;
                                GlobalState.AUDIO_FILE=globalAudioFile;
                                eventOfPlayBtn();
                                touchlisterFalse( globalSeekbar);
                            } else {
                                playingAudioId = null;
                                mediaPlayerTag.getStopImage().setVisibility(View.GONE);
                                mediaPlayerTag.getPlayImage().setVisibility(View.VISIBLE);
                                if (mediaPlayer != null) {
                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.stop();
                                        myHandler.removeCallbacks(updateSongTime);
                                        globalSeekbar.setProgress(0);
                                        globalMusicTime.setText(CommonUtil
                                                .mediaDurationFormat(CommonUtil
                                                        .mediaToDurationPath(globalAudioFile
                                                                .getAbsolutePath())));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                            context, context.getResources().getString(R.string.file_missing),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void eventOfPlayBtn() {



        if (globalAudioFile != null) {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer = MediaPlayer.create(context,
                            Uri.fromFile(globalAudioFile));
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = null;
                    mediaPlayer = MediaPlayer.create(context,
                            Uri.fromFile(globalAudioFile));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                mediaPlayerPlay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public  void touchlisterFalse(SeekBar seekBar)
    {
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }
    public void mediaPlayerPlay() {
        try {
            //mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    globalSeekbar.setMax(100);
                    myHandler.postDelayed(updateSongTime, 100);
                    globalSeekbar.setEnabled(true);
                    globalSeekbar.setOnSeekBarChangeListener(ChatMessageAdapter.this);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            globalMusicTime.setText(CommonUtil
                                    .mediaDurationFormat(CommonUtil
                                            .mediaToDurationPath(globalAudioFile
                                                    .getAbsolutePath())));
                            isCheckMediapLayerPlaying =false;
                            if (audioPlayGlobal != null
                                    && audioStopGlobal != null) {
                                audioStopGlobal.setVisibility(View.GONE);
                                audioPlayGlobal.setVisibility(View.VISIBLE);
                                disAppearList.put(ChatGlobalStates.selectedReceiverMsgId, ChatGlobalStates.selectedReceiverMsgId);
                                try {
                                    int receiverDisapper = Integer.valueOf(ChatGlobalStates.saveReceiverDisapper);
                                    MyCountDownTimer myCountDownTimer = new MyCountDownTimer(receiverDisapper, 2000, ChatGlobalStates.selectedPosition);
                                    myCountDownTimer.start();
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            playingAudioId = null;
                            myHandler.removeCallbacks(updateSongTime);
                            globalSeekbar.setProgress(0);
                        }
                    });
                }
            });

        } catch (Exception e) {
            Log.e("Audio Exception", e.toString());
        }
    }

    private Runnable updateSongTime = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
       /*     musicTotalTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                    TimeUnit.MILLISECONDS.toSeconds(totalDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(totalDuration))));*/
            globalMusicTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(currentDuration),
                    TimeUnit.MILLISECONDS.toSeconds(currentDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(currentDuration))));
            int progress = (CommonUtil.getProgressPercentage(currentDuration, totalDuration));

            globalSeekbar.setProgress(progress);
            myHandler.postDelayed(this, 100);
        }
    };

    /* private Runnable updateSongTime = new Runnable() {
         @SuppressLint("NewApi")
         public void run() {
             startTime = mediaPlayer.getCurrentPosition();
             globalMusicTime.setText(String.format("%02d:%02d",
                     TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                     TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                             TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                     toMinutes((long) startTime))));
             globalSeekbar.setProgress((int) startTime);
             myHandler.postDelayed(this, 100);
         }
     };
 */
    public void stopVoicePlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        //  myHandler.removeCallbacks(updateSongTime);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        // myHandler.removeCallbacks(updateSongTime);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = CommonUtil.progressToTimer(seekBar.getProgress(), totalDuration);
        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
    }


    // Filter Class
    public void filter(String charText)
    {
        this.searchstring=charText;
        charText = charText.toLowerCase(Locale.getDefault());
        messageList.clear();
        if (charText.length() == 0)
        {
            System.out.println("inside filter if");
            messageList.addAll(fixedMessageList);
        }
        else
        {
            for (ChatMessageModel wp : fixedMessageList)
            {
                if (wp.getActualMsg().toLowerCase(Locale.getDefault()).contains(charText))
                {

                    messageList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
