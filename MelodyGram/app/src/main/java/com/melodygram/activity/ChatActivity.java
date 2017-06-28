package com.melodygram.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.melodygram.R;
import com.melodygram.adapter.ChatMessageAdapter;
import com.melodygram.asyncTask.PostEditedMessage;
import com.melodygram.asyncTask.UploadImage;
import com.melodygram.asyncTask.UploadMediaAWS;
import com.melodygram.chatinterface.EditeMessageResponse;
import com.melodygram.chatinterface.InterfaceEditMsg;
import com.melodygram.chatinterface.InterfacePicSelection;
import com.melodygram.chatinterface.InterfaceRefreshListview;
import com.melodygram.chatinterface.InterfaceUpdateProgress;
import com.melodygram.chatinterface.InterfaceUploadAudioFile;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.database.ContactDataSource;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.database.SharedPreferenceDB;
import com.melodygram.emoji.Emojicon;
import com.melodygram.emojicon.EmojiconEditText;
import com.melodygram.emojicon.EmojiconGridFragment;
import com.melodygram.emojicon.EmojiconsFragment;
import com.melodygram.fragment.StickersFragment;
import com.melodygram.fragment.StickersGridFragment;
import com.melodygram.fragment.VoiceRecordingFragment;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.EditedMsgBean;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.ImageDetails;
import com.melodygram.model.UnseenMessageBean;
import com.melodygram.shortcutbadger.ShortcutBadger;
import com.melodygram.singleton.AppController;
import com.melodygram.singleton.MsgResponseManager;
import com.melodygram.swipelistview.BaseSwipeListViewListener;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.DateTimeUtil;
import com.melodygram.view.CustomEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.name;
import static com.melodygram.view.CustomEditText.DrawableClickListener.DrawablePosition.RIGHT;

/**
 * Created by LALIT on 20-06-2016.
 */
public class ChatActivity extends FragmentActivity implements View.OnClickListener, StickersGridFragment.OnStickerClickedListener, EmojiconGridFragment.OnEmojiconClickedListener, StickersFragment.OnStickerButtonClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, VoiceRecordingFragment.AudioInterface, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, InterfaceEditMsg, EditeMessageResponse, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, InterfaceRefreshListview, InterfaceUploadAudioFile, InterfacePicSelection, InterfaceUpdateProgress {
    RelativeLayout chatListItemSelect;
    private PopupWindow menuPopup, attachmentPopup;
    public static ChatActivity chatActivity;
    private RelativeLayout menuButton;
    private CheckBox disappearCheck;
    private FrameLayout fragmentContainer;
    private boolean fragmentVisiblity, emojiconVisible, voiceRecordingVisible;
    private Fragment emojiconFragment, stickersFragment, voiceRecordingFragment;
    private RelativeLayout bottomLayout, bottomLayoutBlockParrentId;
    private ListView chatListView;
    private TextView lastSeenText, userNameText, chatSelectedText;
    private EmojiconEditText userText;
    private ImageView voiceButton, sendButton, userPicImage;
    private AppController appController;
    private String userId, selectedImagePath, groupMembers, lastSeenDateTime;
    private String roomId = null;
    private CustomEditText searchText;
    private String soundConfig, chatType;
    private ChatMessageAdapter chattingListAdapter;
    private List<ChatMessageModel> moreChatChatingListItemBeanList, chatingListItemOLD;
    private ArrayList<ChatMessageModel> chatDetailsItemBeansTemp, chatDetailsItemBeans,
            chatDetailsItemBeansForDB, chatDetailsItemBeansForDBTemp;
    private boolean isHeaderViewAvl, newMessageFlag = true,
            messageNewFlag, firstTimeFlag = false;
    private String chatContactName, chatContactNumber, dummyMessageid, chatIsSent,
            chatMessageTempId, chatMessageId,
            chatMessageSenderName, chatMessageSenderPic, chatMessageRoomId,
            chatMessageUserId, chatMessage, chatMessageType,
            chatMessageFileUrl, chatMessageLat, chatMessageLan,
            chatMessageUserTime, chatMessageUserDate, chatMessageToFromFlag,
            chatMessageSingleTickFlag, chatMessageSeen,
            chatMessageSeenTime, chatFileThumbnail, stickerId, chatDisappear;
    private ChatMessageModel moreChatChatingListItemBean,
            moreChatChatingListItemBeanForDB;

    private int messageTypeCounter = 0, newMessagePosition = 0,imageDownloadID;
    private MsgResponseManager msgResponseRef;
    private View headerView;
    private Bitmap capturePhotoBitmap;
    private static DevicesPinger devicesPinger;
    private boolean isStarted, isStopped, postMsgFlag;
    private HashMap<String, String> bitMapList = new HashMap<String, String>();
    private ChatMessageDataSource moreChatMessageDatasource;
    public static LinkedHashMap<String, UnseenMessageBean> unseenMessageHashList;
    private String groupData, frendsName, friendsLastSeen,
            frendsNo, friendProfilePicUrl, groupAdminUserId, friendsIsBlocked, frienddBlocked = "0", friendsUserId, profilePicprivacy, lastSeenPrivacy, statusPrivacy, readReceipt;
    public static HashMap<String, String> grpNameList = null;
    private LinkedHashMap<String, String> receivedMessagesId;
    private String groupProfilePic, groupProfileUserName, groupProfileUserId,
            groupProfilemobile;
    private ArrayList<FriendsModel> groupChatListItemArrayList;
    private ArrayList<ImageDetails> selectedImages;
    private int chatListSizeFromDB = -1;
    private Dialog commonDialog;
    private String DIALOG_OPERATION;
    private TextView dialogHeader, dialogMessage;
    private static final String disappearTime = "5000";
    private static final String audioDisappearTime = "120000";
    private static final String textDisappearTime = "30000";
    private static final String imageDisappearTime = "3000";
    private ImageView selectedImage, editButton, copyButton, forwardButton, deleteButton;
    private GridView imageSelectionGridView;
    private EditText selectedImageDescription;
    private ImageSelectionAdapter imageSelectionAdapter;
    private Dialog imageSelectionDialog;
    private static RelativeLayout optionsParent, chatOptionsParent;
    public static boolean copyForwardFlag,
            copyForwardFinishFlag = true;
    private ArrayList<ChatMessageModel> localPoolDataList;

    private File cameraFile;
    Dialog editMsgDialog;
    ImageView editMsgBack;
    TextView editMessageText;
    EditText editMessagePlace;
    RelativeLayout editDoneParrent;
    int editItemPos;
    int selectedposition;
    boolean isCheckContinousOrNot;
    boolean isChatLastUser;
    public static LinkedHashMap<String, EditedMsgBean> editedMsgHashList;
    public static String TEMP_PHOTO_FILE_NAME;
    AppContactDataSource appContactDataSource;
    private GoogleApiClient mGoogleApiClient;
    private Tracker mTracker;
    ChatMessageAdapter.ViewHolder viewHolderItem;
    String imageUrl, imageName;
    private String TAG = ChatActivity.class.getName();
    ImageView chat_search_button;
    RelativeLayout rl_chat_search_button;
    private RelativeLayout searchButtonParent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.chat_layout);
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        chatActivity = ChatActivity.this;
        initView();
        chatFunctionalities();
        initFragments();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView()
    {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        chat_search_button = (ImageView) findViewById(R.id.chat_search_button);
        rl_chat_search_button = (RelativeLayout) findViewById(R.id.rl_chat_search_button);
        chat_search_button.setOnClickListener(this);
        // (view.findViewById(R.id.chat_search_button)).setOnClickListener(this);

        searchButtonParent = (RelativeLayout) findViewById(R.id.chat_search_parent);

        selectedImages = new ArrayList<>();
        receivedMessagesId = new LinkedHashMap<>();
        editedMsgHashList = new LinkedHashMap<String, EditedMsgBean>();
        moreChatMessageDatasource = new ChatMessageDataSource(
                getApplicationContext());
        msgResponseRef = MsgResponseManager
                .initMsgResponseManager(ChatActivity.this);
        msgResponseRef.clearLocalPool();
        msgResponseRef.clearLocalPoolData();
        msgResponseRef.clearLocalPoolDataList();
        groupChatListItemArrayList = new ArrayList<>();
        unseenMessageHashList = new LinkedHashMap<>();
        chatDetailsItemBeansTemp = new ArrayList<>();
        chatDetailsItemBeans = new ArrayList<>();
        chatDetailsItemBeansForDB = new ArrayList<>();
        chatDetailsItemBeansForDBTemp = new ArrayList<>();
        localPoolDataList = new ArrayList<>();
        devicesPinger = new DevicesPinger();
        disappearCheck = (CheckBox) findViewById(R.id.disappear_checkbox);
        menuButton = (RelativeLayout) findViewById(R.id.chat_settings);
        menuButton.setOnClickListener(this);
        (findViewById(R.id.emojicon_button)).setOnClickListener(this);
        fragmentContainer = (FrameLayout) findViewById(R.id.container);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        bottomLayoutBlockParrentId = (RelativeLayout) findViewById(R.id.user_block_layout);
        voiceButton = (ImageView) findViewById(R.id.voice_button);
        voiceButton.setOnClickListener(this);

        editMsgDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        editMsgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editMsgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        editMsgDialog.setContentView(R.layout.activity_edit_message);
        editMsgDialog.setCancelable(true);

        editMsgBack = (ImageView) editMsgDialog.findViewById(R.id.editMsgBackId);
        editMessageText = (TextView) editMsgDialog.findViewById(R.id.editMessageTextId);
        editMessagePlace = (EditText) editMsgDialog.findViewById(R.id.editMessagePlaceId);
        editDoneParrent = (RelativeLayout) editMsgDialog.findViewById(R.id.editDoneParrentId);

        editMsgBack.setOnClickListener(this);
        editDoneParrent.setOnClickListener(this);
        sendButton = (ImageView) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
     // searchText = (EditText) findViewById(R.id.msg_search);
        searchText = (CustomEditText) findViewById(R.id.chat_search);

        searchText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {


            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String searchString = searchText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                chattingListAdapter.filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        searchText.setDrawableClickListener(new CustomEditText.DrawableClickListener()
        {
            public void onClick(DrawablePosition target)
            {
                switch (target)
                {
                    case RIGHT:
                        //Do something here
                        searchButtonParent.setVisibility(View.INVISIBLE);
                        chat_search_button.setVisibility(View.VISIBLE);
                        rl_chat_search_button.setVisibility(View.VISIBLE);
                        hideSoftInPutKey();
                        break;

                    default:
                        break;
                }
            }

        });

//        searchText.addTextChangedListener(new TextWatcher() {            @Override
//
//
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//        }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String searchString = searchText.getText().toString()
//                        .toLowerCase(Locale.getDefault());
//               // if (chattingListAdapter != null)
//                    chattingListAdapter.filter(searchString);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        userText = (EmojiconEditText) findViewById(R.id.user_text);
        userText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                fragmentVisiblity = false;
                fragmentContainer.setVisibility(View.GONE);
            }
        });

        userText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                voiceButton.setVisibility(View.GONE);
                sendButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (userText.getText().toString().length() > 0) {
                    if ((userText.getText().toString().length() == 1))
                        updateUserTyping("1");

                } else {
                    voiceButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    updateUserTyping("0");
                }
            }
        });

        findViewById(R.id.location_icon).setOnClickListener(this);
        findViewById(R.id.attach_parent).setOnClickListener(this);
        findViewById(R.id.back_button_parent).setOnClickListener(this);
        findViewById(R.id.header_name_parent).setOnClickListener(this);
        findViewById(R.id.edit_back).setOnClickListener(this);

        editButton = (ImageView) findViewById(R.id.edit_button);
        editButton.setOnClickListener(this);
        copyButton = (ImageView) findViewById(R.id.copy_button);
        copyButton.setOnClickListener(this);
        forwardButton = (ImageView) findViewById(R.id.forward_icon);
        forwardButton.setOnClickListener(this);
        deleteButton = (ImageView) findViewById(R.id.msg_delete_button);
        deleteButton.setOnClickListener(this);
        chatSelectedText = (TextView) findViewById(R.id.selected_count);
        optionsParent = (RelativeLayout) findViewById(R.id.options_parent);
        chatOptionsParent = (RelativeLayout) findViewById(R.id.chat_options_parent);
        userPicImage = (ImageView) findViewById(R.id.profile_image);
        userNameText = (TextView) findViewById(R.id.header_name);
        lastSeenText = (TextView) findViewById(R.id.last_seen);
        headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.load_earlier_msg_header, null, false);
        headerView.findViewById(R.id.loadEarlierHeaderParrentId).setOnClickListener(this);
        chatListView = (ListView) findViewById(R.id.chat_list);
        chatListView.setOnItemClickListener(this);
        chatListView.setOnItemLongClickListener(this);
        chatListView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (fragmentVisiblity) {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentVisiblity = false;
                }
//                if (searchText.getVisibility() == View.VISIBLE) {
//                    searchText.setVisibility(View.GONE);
//                    searchText.setText("");
//                }
                hideSoftInPutKey();
                return false;
            }
        });
        appController = AppController.getInstance();
        setThemeBg(appController.getPrefs().getInt("theme", 0));
        disappearCheck.setChecked(appController.isDisappearActivated());
        initPopupDialog();
        initImageAttachDialog();
        chatListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (firstVisibleItem == 0)
                {
                    // check if we reached the top or bottom of the list
                    View v = chatListView.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0)
                    {
                        // reached the top:

                        try {
                            int indexTopMsg = 0;
                            dateStampPrvsOLD = chatDetailsItemBeans.get(indexTopMsg)
                                    .getMsgDate();
                            String msgId = null;
                            boolean isDummyMsgId = false;
                            if (chatDetailsItemBeans.get(indexTopMsg).getUserId() != null)
                            {
                                if (chatDetailsItemBeans.get(indexTopMsg).getUserId()
                                        .equals(appController.getUserId()))
                                {
                                    msgId = chatDetailsItemBeans.get(indexTopMsg)
                                            .getChatTempMessageId();
                                    isDummyMsgId = true;
                                } else {
                                    msgId = chatDetailsItemBeans.get(indexTopMsg).getMsgId();
                                    isDummyMsgId = false;
                                }
                            }
                            if (msgId != null)
                                loadOlderChat(roomId, msgId, isDummyMsgId);
                            else {
                                //  chatListView.removeHeaderView(headerView);
                                isHeaderViewAvl = false;
                            }

                            return;
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if (totalItemCount - visibleItemCount == firstVisibleItem)
                {
                    View v = chatListView.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0)
                    {
                        // reached the top:
                        return;
                    }
                }
            }
        });


    }

    private void chatFunctionalities()
    {
        Bundle bundle = getIntent().getExtras();
        appContactDataSource = new AppContactDataSource(chatActivity);
        if (bundle != null) {
            frendsName = bundle.getString("frendsName");
            frendsNo = bundle.getString("frendsNo");
            groupAdminUserId = bundle.getString("groupAdminUserId");
            friendProfilePicUrl = bundle.getString("friendProfilePicUrl");
            profilePicprivacy = bundle.getString("profile_privacy");
            lastSeenPrivacy = bundle.getString("lastseen_privacy");
            statusPrivacy = bundle.getString("status_privacy");
            readReceipt = bundle.getString("readReceipt");

            GlobalState.RECEIVER_PROFILE_PIC = friendProfilePicUrl;


            GlobalState.READ_RECEIPT = readReceipt;
            if (profilePicprivacy != null && profilePicprivacy.equalsIgnoreCase("0"))
            {
               // appController.displayUrlImage(userPicImage, friendProfilePicUrl, null);
                Picasso.with(chatActivity).load(APIConstant.SERVER_PATH+friendProfilePicUrl).fit().centerCrop()
                        .placeholder(R.drawable.default_profile_pic)
                        .error(R.drawable.default_profile_pic)
                        .into(userPicImage);
            } else if (profilePicprivacy != null && profilePicprivacy.equalsIgnoreCase("2"))
            {
                appContactDataSource.open();
                if (appContactDataSource.isContactAvailableOrNot(frendsNo)) {
                  //  appController.displayUrlImage(userPicImage, friendProfilePicUrl, null);
                    Picasso.with(chatActivity).load(APIConstant.SERVER_PATH+friendProfilePicUrl).fit().centerCrop()
                            .placeholder(R.drawable.default_profile_pic)
                            .error(R.drawable.default_profile_pic)
                            .into(userPicImage);
                } else {
                    userPicImage.setImageResource(R.drawable.default_profile_pic);
                }
                appContactDataSource.close();
            } else {
                userPicImage.setImageResource(R.drawable.default_profile_pic);
            }
            chatType = bundle.getString("chatType");
            roomId = bundle.getString("contactsRoomId");
            groupData = bundle.getString("groupUsers");
            String adminId = bundle.getString("friendsGroupAdminUserId");
            friendsUserId = bundle.getString("userId");
            userNameText.setText(frendsName);
            if (adminId != null && !adminId.isEmpty())
            {
                friendsUserId += "," + adminId;
            }
            if (bundle.containsKey("friendsLastSeen"))
            {
                friendsLastSeen = bundle.getString("friendsLastSeen");
                lastSeenBeforeTyping();
            } else
            {
                friendsLastSeen = "";
            }

            if (roomId == null)
            {
                getRoomId();
            } else
            {
                initializeChatScreen();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    protected void onResume() {
        super.onResume();
        GlobalState.CHAT_BACK_BUTTON =true;

        Log.i(TAG, "ChatActivity: " + name);
        mTracker.setScreenName("chat");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        ChatGlobalStates.ROOM_ID_OPENDED = roomId;
        GlobalState.IS_CHATSCREEN_VISIBLE = true;

        ChatGlobalStates.ISLONGPRESS = false;

        searchButtonParent.setVisibility(View.GONE);
        chat_search_button.setVisibility(View.VISIBLE);
        NotificationManager notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        //  ShortcutBadger.removeCount(this);
        try {
            ShortcutBadger.with(getApplicationContext()).count(
                    Integer.valueOf(0));
        } catch (Exception e)
        {
            e.toString();
        }
        FriendsDataSource friendsDataSource = new FriendsDataSource(ChatActivity.this);
        friendsDataSource.open();
        friendsIsBlocked = friendsDataSource.getBlockValue(roomId);
        frienddBlocked = friendsDataSource.getISBlockValue(roomId);

        friendsDataSource.close();
        if (friendsIsBlocked != null
                && friendsIsBlocked.equalsIgnoreCase("1")) {
            bottomLayoutBlockParrentId.setVisibility(View.VISIBLE);
        } else {
            bottomLayoutBlockParrentId.setVisibility(View.GONE);
        }
        ((StickersFragment) stickersFragment).getAllStickers();
        if (isStarted && !isStopped) {
            startRepeatingTask();
        }
        startRepeatingTask();
        disableCopyForward();
        registerReceiver();
        registerChatMessage();
        GlobalState.isCheckForegroundChat = true;
        refreshChatList(false);


    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatGlobalStates.ROOM_ID_OPENDED = null;

        updateUserTyping("0");
        if (chattingListAdapter != null) {
            chattingListAdapter.stopVoicePlayer();
        }
        stopRepeatingTask();
        unRegisterReceiver();
        unregisterChatMessage();
        ChatGlobalStates.isFromChat = true;
        GlobalState.isCheckForegroundChat = false;

    }

    @Override
    public void onBackPressed()
    {
        disableCopyForward();
        super.onBackPressed();
    }

    @Override
    public void audioFileResponse(ChatMessageModel senderChatMessageModel)
    {

        postAudioMsgNW(senderChatMessageModel.getChatTempMessageId(), senderChatMessageModel.getFileAudio(), senderChatMessageModel.getMessageType(), senderChatMessageModel.getFileExtn(), senderChatMessageModel.getMessageRoomId());
    }


    @Override
    public void updateProgressValue(String progressValue)
    {
        moreChatChatingListItemBean.setProgressValue(progressValue);
        if (chattingListAdapter != null)
        {
            chattingListAdapter.notifyDataSetChanged();
        }
    }

    private void initializeChatScreen()
    {
        moreChatMessageDatasource.open();
        moreChatChatingListItemBeanList = moreChatMessageDatasource
                .getLimitedChatMessage(roomId);
        int totalMsgCount = moreChatMessageDatasource.noOfRows(roomId);
        int totalMsgCountInDb = moreChatMessageDatasource.noOfMessages(roomId);
        moreChatMessageDatasource.close();
        int listSize = moreChatChatingListItemBeanList.size();
        if (listSize > 0) {
            if (totalMsgCount > 20) {
                // chatListView.addHeaderView(headerView);
                //isHeaderViewAvl = true;
            } else {
                //chatListView.removeHeaderView(headerView);
                //  isHeaderViewAvl = false;
            }
            if (chatDetailsItemBeansForDB != null) {
                chatDetailsItemBeansForDB.clear();
            }
            if (chatDetailsItemBeans != null) {
                chatDetailsItemBeans.clear();
            }
            for (int j = 0; j < moreChatChatingListItemBeanList.size(); j++) {

                ChatMessageModel chatingListItemDataObject = moreChatChatingListItemBeanList
                        .get(j);
                parseLatestOldMsgFromDBToLoad(chatingListItemDataObject, true);
            }

            chattingListAdapter = new ChatMessageAdapter(ChatActivity.this, chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
            scrollMyListViewToBottom();
            getAllUnsentMsgDB();


        } else {
            startRepeatingTask();

        }
    }

    private void groupChatMemberInitialization(String groupData)
    {
        ContactDataSource contactDataSource = new ContactDataSource(this);
        groupMembers = "";
        try {
            grpNameList = new HashMap<String, String>();
            JSONArray groupDataArray = new JSONArray(groupData);
            for (int i = 0; i < groupDataArray.length(); i++)
            {
                groupProfilePic = null;
                groupProfileUserName = null;
                JSONArray groupEachDataArray = groupDataArray.getJSONArray(i);
                for (int j = 0; j < groupEachDataArray.length(); j++) {
                    JSONObject groupEachDataObject = groupEachDataArray
                            .getJSONObject(j);
                    if (groupProfilePic == null) {
                        groupProfilePic = groupEachDataObject
                                .getString("profile_pic");
                    } else {
                        groupProfilePic = groupProfilePic + ","
                                + groupEachDataObject.getString("profile_pic");
                    }
                    groupProfileUserName = groupEachDataObject
                            .getString("name");
                    groupProfileUserId = groupEachDataObject
                            .getString("userid");
                    if (groupEachDataObject.has("mobile")) {
                        groupProfilemobile = groupEachDataObject
                                .getString("mobile");

                    } else {
                        groupProfilemobile = null;
                    }

                }
                if (groupProfilePic != null) {
                    FriendsModel groupChatListItemBean = new FriendsModel();
                    groupChatListItemBean.setFriendsPicIconUrl(groupProfilePic);
                    if (groupProfileUserName == null)
                        groupProfileUserName = "";
                    if (!groupProfileUserId.equals(appController.getUserId())) {

                        if (groupProfilemobile != null) {
                            groupProfileUserName = contactDataSource
                                    .getContactName(groupProfilemobile);

                            if (groupProfileUserName != null) {
                                groupMembers = groupMembers + ", "
                                        + groupProfileUserName;
                                grpNameList.put(groupProfileUserId,
                                        groupProfileUserName);
                            } else {
                                groupMembers = groupMembers + ", "
                                        + groupProfilemobile;
                                grpNameList.put(groupProfileUserId,
                                        groupProfilemobile);
                            }
                        } else {
                            if (groupProfilemobile != null) {
                                groupMembers = groupMembers + ", "
                                        + groupProfilemobile;
                                grpNameList.put(groupProfileUserId,
                                        groupProfilemobile);
                            } else {
                                groupMembers = groupMembers + ", "
                                        + groupProfileUserName;
                                grpNameList.put(groupProfileUserId,
                                        groupProfileUserName);
                            }
                        }
                    }
                    if (frendsNo != null && groupAdminUserId != null) {

                        groupProfileUserName = contactDataSource
                                .getContactName(frendsNo);
                        if (groupProfileUserName != null) {
                            grpNameList.put(groupAdminUserId,
                                    groupProfileUserName);
                        } else {
                            grpNameList.put(groupAdminUserId, frendsNo);
                        }
                    }
                    groupChatListItemBean.setFriendName(groupProfileUserName);
                    groupChatListItemArrayList.add(groupChatListItemBean);
                }
            }

            if (groupMembers != null
                    && !groupMembers.isEmpty()
                    && !lastSeenText
                    .getText()
                    .toString()
                    .equals(groupMembers.substring(1)
                            + " "
                            + getResources()
                            .getString(R.string.and_you))) {
                lastSeenText.setText(groupMembers.substring(1)
                        + " " + getResources().getString(R.string.and_you));
                lastSeenText.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initPopupDialog() {
        commonDialog = CommonUtil.getDialog(ChatActivity.this);
        dialogHeader = (TextView) commonDialog.findViewById(R.id.header_name);
        dialogMessage = (TextView) commonDialog.findViewById(R.id.message);
        TextView dialogOkButton = (TextView) commonDialog.findViewById(R.id.dialog_ok_button);
        dialogOkButton.setOnClickListener(this);
        TextView dialogCancelButton = (TextView) commonDialog.findViewById(R.id.dialog_cancel_button);
        dialogCancelButton.setOnClickListener(this);
    }


    private void initImageAttachDialog()
    {
        imageSelectionDialog = new Dialog(ChatActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        imageSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageSelectionDialog.setContentView(R.layout.image_selection_layout);
        selectedImage = (ImageView) imageSelectionDialog.findViewById(R.id.selected_pic);
        selectedImageDescription = (EditText) imageSelectionDialog.findViewById(R.id.pic_description);
        imageSelectionDialog.findViewById(R.id.remove_pic).setOnClickListener(this);
        imageSelectionDialog.findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        imageSelectionAdapter = new ImageSelectionAdapter();
        imageSelectionGridView = (GridView) imageSelectionDialog.findViewById(R.id.image_grid);
        imageSelectionGridView.setAdapter(imageSelectionAdapter);
        imageSelectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int lastSelectedPosition = (int) selectedImage.getTag();
                ImageDetails imageDetails = selectedImages.get(lastSelectedPosition);
                imageDetails.setDescription(selectedImageDescription.getText().toString());
                selectedImages.set(lastSelectedPosition, imageDetails);
                appController.displayFileImage(selectedImage, selectedImages.get(position).getPath(), null);
                selectedImage.setTag(position);
                ImageDetails imageDetailsActual = selectedImages.get(position);
                selectedImageDescription.setText(imageDetailsActual.getDescription());
                selectedImageDescription.setSelection(imageDetailsActual.getDescription().length());
            }
        });
        imageSelectionDialog.findViewById(R.id.image_selection_parent).setVisibility(View.VISIBLE);
        imageSelectionDialog.findViewById(R.id.add_button).setOnClickListener(this);
        imageSelectionDialog.findViewById(R.id.done_button).setOnClickListener(this);
        imageSelectionDialog.findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        imageSelectionDialog.findViewById(R.id.header_name).setVisibility(View.INVISIBLE);
    }


    private void initMenuPopUpWindow() {
        if (menuPopup != null && menuPopup.isShowing())
        {
            menuPopup.dismiss();
        } else if (menuPopup != null)
        {
            menuPopup.showAsDropDown(menuButton, -135, -20);

        } else if (menuPopup == null)
        {
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(R.layout.chat_screen_popup_layout, null);
            popupView.findViewById(R.id.search_button).setOnClickListener(this);
            popupView.findViewById(R.id.clear_chat_button).setOnClickListener(this);
            popupView.findViewById(R.id.chat_theme_button).setOnClickListener(this);
            menuPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            menuPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            menuPopup.setOutsideTouchable(true);
            menuPopup.setFocusable(true);
            menuPopup.showAsDropDown(menuButton, -135, -20);
        }
    }

    private void initAttachmentPopUpWindow() {
        if (attachmentPopup != null && attachmentPopup.isShowing()) {
            attachmentPopup.dismiss();
        } else if (attachmentPopup != null) {
            attachmentPopup.showAtLocation(bottomLayout, Gravity.BOTTOM, 0, bottomLayout.getHeight());
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(R.layout.attachment_layout, null);
            popupView.findViewById(R.id.take_photo_button).setOnClickListener(this);
            popupView.findViewById(R.id.doc_file).setOnClickListener(this);
            popupView.findViewById(R.id.gallery_button).setOnClickListener(this);
            popupView.findViewById(R.id.pdf_file_button).setOnClickListener(this);
            popupView.findViewById(R.id.zip_button).setOnClickListener(this);
            attachmentPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            attachmentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            attachmentPopup.setOutsideTouchable(true);
            attachmentPopup.setFocusable(true);
            attachmentPopup.showAtLocation(bottomLayout, Gravity.BOTTOM, 0, bottomLayout.getHeight());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_name_parent:
                Intent intent = new Intent(ChatActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("bundle", getIntent().getExtras());
                startActivity(intent);
                hideSoftInPutKey();
                break;
            case R.id.chat_settings:
                initMenuPopUpWindow();
                hideSoftInPutKey();
                break;
            case R.id.loadEarlierHeaderParrentId:
                int indexTopMsg = 0;
                dateStampPrvsOLD = chatDetailsItemBeans.get(indexTopMsg)
                        .getMsgDate();
                String msgId = null;
                boolean isDummyMsgId = false;
                if (chatDetailsItemBeans.get(indexTopMsg).getUserId() != null) {
                    if (chatDetailsItemBeans.get(indexTopMsg).getUserId()
                            .equals(appController.getUserId())) {
                        msgId = chatDetailsItemBeans.get(indexTopMsg)
                                .getChatTempMessageId();
                        isDummyMsgId = true;
                    } else {
                        msgId = chatDetailsItemBeans.get(indexTopMsg).getMsgId();
                        isDummyMsgId = false;
                    }
                }
                if (msgId != null)
                    loadOlderChat(roomId, msgId, isDummyMsgId);
                else {
                    //  chatListView.removeHeaderView(headerView);
                    isHeaderViewAvl = false;
                }
                hideSoftInPutKey();


                break;

            case R.id.emojicon_button:
                if (!fragmentVisiblity) {
                    loadFragments(0);
                    //initFragments(0);
                    fragmentVisiblity = true;
                    fragmentContainer.setVisibility(View.VISIBLE);
                    emojiconVisible = true;
                } else if (voiceRecordingVisible) {
                    loadFragments(0);
                    emojiconVisible = true;
                } else {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentVisiblity = false;
                }
                hideSoftInPutKey();
                break;
            case R.id.attach_parent:
                selectedImages.clear();
                selectedImage.setTag("");
                selectedImageDescription.setText("");
                initAttachmentPopUpWindow();
                if (fragmentVisiblity) {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentVisiblity = false;
                }
                hideSoftInPutKey();
                break;
            case R.id.location_icon:
                if (fragmentVisiblity) {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentVisiblity = false;
                }
                pickLocation();
               /* DIALOG_OPERATION = "LOCATION";
                dialogHeader.setText(getResources().getString(R.string.location));
                dialogMessage.setText(getResources().getString(R.string.location_share));
                commonDialog.show();*/
                hideSoftInPutKey();
                break;
            case R.id.voice_button:
                if (!fragmentVisiblity) {
                    loadFragments(2);
                    fragmentVisiblity = true;
                    fragmentContainer.setVisibility(View.VISIBLE);
                    voiceRecordingVisible = true;
                    emojiconVisible = false;
                } else if (emojiconVisible) {
                    loadFragments(2);
                    voiceRecordingVisible = true;
                    emojiconVisible = false;
                } else {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentVisiblity = false;
                }
                hideSoftInPutKey();
                break;
            case R.id.send_button:
                if (userText != null
                        && userText.getText().toString().length() > 0) {
                    String msg = userText.getText().toString();
                    userText.setText("");
                    postMessage(msg);
                }
                break;

            case R.id.back_button_parent:
                finish();
                hideSoftInPutKey();
                break;
            case R.id.header_back_button_parent:
                imageSelectionDialog.dismiss();
                hideSoftInPutKey();
                break;

            case R.id.search_button:
                searchButtonParent.setVisibility(View.VISIBLE);
                chat_search_button.setVisibility(View.GONE);
//                if (searchText.getVisibility() == View.GONE) {
//                    searchText.setVisibility(View.VISIBLE);
//                    searchText.setFocusable(true);
//                } else
//                    searchText.setVisibility(View.GONE);
                if (menuPopup != null && menuPopup.isShowing())
                    menuPopup.dismiss();
                hideSoftInPutKey();
                break;
            case R.id.clear_chat_button:
                if (menuPopup != null && menuPopup.isShowing())
                    menuPopup.dismiss();
                DIALOG_OPERATION = "CLEARCHAT";
                dialogHeader.setText(getResources().getString(R.string.chat));
                dialogMessage.setText(getResources().getString(R.string.chat_message));
                commonDialog.show();
                hideSoftInPutKey();
                break;
            case R.id.chat_theme_button:
                if (menuPopup != null && menuPopup.isShowing())
                    menuPopup.dismiss();
                Intent themesIntent = new Intent(this, ThemesActivity.class);
                startActivityForResult(themesIntent, GlobalState.THEMES_REQUEST);
                hideSoftInPutKey();
                break;
            case R.id.take_photo_button:
                initAttachmentPopUpWindow();
                selectedImages.clear();
                takeCameraPhoto();
                hideSoftInPutKey();
                break;
            case R.id.gallery_button:
                initAttachmentPopUpWindow();
                selectedImages.clear();
                openGallery();
                hideSoftInPutKey();
                break;
            case R.id.doc_file:
                initAttachmentPopUpWindow();
                Intent docIntent = new Intent(this, FileChooser.class);
                ArrayList<String> docExtensions = new ArrayList<>();
                docExtensions.add(".doc");
                docIntent.putStringArrayListExtra("filterFileExtension", docExtensions);
                startActivityForResult(docIntent, GlobalState.REQUEST_CODE_DOC_FILES);
                hideSoftInPutKey();
                break;
            case R.id.pdf_file_button:
                initAttachmentPopUpWindow();
                Intent pdfIntent = new Intent(this, FileChooser.class);
                ArrayList<String> extensions = new ArrayList<>();
                extensions.add(".pdf");
                pdfIntent.putStringArrayListExtra("filterFileExtension", extensions);
                startActivityForResult(pdfIntent, GlobalState.REQUEST_CODE_PDF_FILES);
                hideSoftInPutKey();
                break;
            case R.id.zip_button:
                initAttachmentPopUpWindow();
                Intent zipIntent = new Intent(this, FileChooser.class);
                ArrayList<String> zipExtensions = new ArrayList<>();
                zipExtensions.add(".zip");
                zipIntent.putStringArrayListExtra("filterFileExtension", zipExtensions);
                startActivityForResult(zipIntent, GlobalState.REQUEST_CODE_ZIP_FILES);
                hideSoftInPutKey();
                break;
            case R.id.dialog_ok_button:
                if (DIALOG_OPERATION.equalsIgnoreCase("LOCATION")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            pickLocation();
                        } else {
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GlobalState.LOCATIONS_PERMISSIONS);
                        }
                    } else {
                        pickLocation();
                    }
                } else if (DIALOG_OPERATION.equalsIgnoreCase("CLEARCHAT")) {
                    clearAllChat();
                }
                commonDialog.dismiss();
                hideSoftInPutKey();
                break;
            case R.id.dialog_cancel_button:
                commonDialog.dismiss();
                hideSoftInPutKey();
                break;
            case R.id.add_button:
                try {
                    if (selectedImages.size() < 10) {
                        ImageDetails imageDetailsLast = selectedImages.get((int) selectedImage.getTag());
                        imageDetailsLast.setDescription(selectedImageDescription.getText().toString());
                        selectedImages.set(selectedImages.size() - 1, imageDetailsLast);
                        openGallery();
                    } else {
                        Toast.makeText(ChatActivity.this, getString(R.string.cant_select_images), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hideSoftInPutKey();
                break;
            case R.id.done_button:
                hideSoftInPutKey();
                try {
                    if (selectedImages.size() > 0)
                    {
                        ImageDetails imageDetailsLast = selectedImages.get((int) selectedImage.getTag());
                        imageDetailsLast.setDescription(selectedImageDescription.getText().toString());
                        selectedImages.set(selectedImages.size() - 1, imageDetailsLast);
                    }
                    for (int i = 0; i < selectedImages.size(); i++)
                    {
                        ImageDetails imageDetails = selectedImages.get(i);
                        postPicture(imageDetails.getPath(), i, imageDetails.getDescription());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageSelectionDialog.dismiss();
                break;
            case R.id.remove_pic:
                selectedImages.remove((int) selectedImage.getTag());
                if (selectedImages.size() > 0) {
                    appController.displayFileImage(selectedImage, selectedImages.get(selectedImages.size() - 1).getPath(), null);
                    selectedImage.setTag(selectedImages.size() - 1);
                } else {
                    selectedImage.setImageResource(android.R.color.transparent);
                }

                imageSelectionAdapter.notifyDataSetChanged();

                hideSoftInPutKey();
                break;
            case R.id.edit_back:
                ChatGlobalStates.ISLONGPRESS = false;
                GlobalState.CHECK_BACK_PRESSE = true;
                disableCopyForward();
                hideSoftInPutKey();
                break;
            case R.id.msg_delete_button:
                ChatGlobalStates.ISLONGPRESS = false;
                GlobalState.CHECK_BACK_PRESSE = true;
                deleteSelectedChatMSG();
                disableCopyForward();
                hideSoftInPutKey();
                break;
            case R.id.copy_button:
                copySelectedChatMSG();
                disableCopyForward();
                hideSoftInPutKey();
                break;
            case R.id.forward_icon:
                Intent selectFrndIntent = new Intent(ChatActivity.this,
                        FwdMultipleActivity.class);
                startActivity(selectFrndIntent);
                finish();
                hideSoftInPutKey();
                break;
            case R.id.edit_button:
                ArrayList<ChatMessageModel> chatListItemSelected = new ArrayList<>(
                        ChatGlobalStates.chatDetailsItemSelected.values());

                if (GlobalState.LAST_MESSAGE_ID != null) {
                    if (GlobalState.LAST_MESSAGE_ID.equalsIgnoreCase(chatListItemSelected.get(0).getMsgId())) {
                        onEditPressed(selectedposition);
                        disableCopyForward();
                    }
                }
                hideSoftInPutKey();
                break;
            case R.id.editDoneParrentId:
                //onEditDonePressed(editedItemBean, editItemPos);
                String actualMsg = editMessagePlace.getText().toString();
                updateEditMsgLocally(actualMsg, moreChatChatingListItemBean, (selectedposition));
                editMsgDialog.dismiss();
                hideSoftInPutKey();
                break;

            case R.id.chat_search_button:

                showSoftInPutKey();
                searchButtonParent.setVisibility(View.VISIBLE);
                chat_search_button.setVisibility(View.GONE);
//                if (searchButtonParent.getVisibility() == View.VISIBLE) {
//                    searchButtonParent.setVisibility(View.GONE);
//                    (findViewById(R.id.chat_search_button)).setVisibility(View.VISIBLE);
//                   // rl_chat_search_button.setVisibility(View.VISIBLE);
//                    showSoftInPutKey();
//                } else {
//                    searchButtonParent.setVisibility(View.VISIBLE);
//                    (findViewById(R.id.chat_search_button)).setVisibility(View.GONE);
//                   // rl_chat_search_button.setVisibility(View.GONE);
//                    hideSoftInPutKey();
//                }
                break;
        }
        //  hideSoftInPutKey();
    }




    public void loadOlderChat(String roomId, String fstMsgIdShown,
                              boolean isDummyMsgId) {
        isStopped = true;
        stopRepeatingTask();
        ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                getApplicationContext());
        moreChatMessageDatasource.open();
        chatingListItemOLD = moreChatMessageDatasource.getLimitedChatMessage(
                roomId, fstMsgIdShown, isDummyMsgId);
        moreChatMessageDatasource.close();
        if (chatingListItemOLD != null)
        {
            chatListSizeFromDB = chatingListItemOLD.size();
        }

        if (chatListSizeFromDB > 0) {
            for (int j = chatListSizeFromDB - 1; j >= 0; j--) {
                ChatMessageModel chatingListItemDataObject = chatingListItemOLD
                        .get(j);
                parseLatestOldMsgFromDBToLoad(chatingListItemDataObject, false);
            }
            refreshChatOnLoadingOldData(chatListSizeFromDB);
            long msgCount = 0;
            if (chatDetailsItemBeans != null && chatDetailsItemBeans.size() > 0) {
                moreChatMessageDatasource.open();
                msgCount = moreChatMessageDatasource.getNextChatMessageCount(roomId, chatDetailsItemBeans.get(0).getMsgId(), false);
                moreChatMessageDatasource.close();
                if (msgCount == 0) {

                    chatListView.removeHeaderView(headerView);
                    isHeaderViewAvl = false;
                }
            }
            startRepeatingTask();

        } else {
            chatListView.removeHeaderView(headerView);
            isHeaderViewAvl = false;
            startRepeatingTask();
        }
    }


    public void refreshChatOnLoadingOldData(int chatListSizeFromDBorServer) {
        // ... Added with old data featch from DB
        if (null != chatDetailsItemBeans.get(0).getMsgDateFlag()
                && chatDetailsItemBeans.get(0).getMsgDateFlag()) {
            chatDetailsItemBeans.remove(0);
        }
        // ...
        if (chattingListAdapter != null) {
            chattingListAdapter.notifyDataSetChanged();
        }
        chatListView.setSelection(chatListSizeFromDBorServer + 1);
    }

    private void initFragments() {
        emojiconFragment = new EmojiconsFragment();
        stickersFragment = new StickersFragment();
        voiceRecordingFragment = new VoiceRecordingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userName", frendsName);
        voiceRecordingFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(fragmentContainer.getId(), emojiconFragment, "").commit();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(fragmentContainer.getId(), stickersFragment, "").commit();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(fragmentContainer.getId(), voiceRecordingFragment, "").commit();

    }

    private void loadFragments(int value) {
        FragmentTransaction fragmentTransaction;
        switch (value) {
            case 0:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.show(emojiconFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(stickersFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(voiceRecordingFragment).commit();
                break;
            case 1:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.show(stickersFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(emojiconFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(voiceRecordingFragment).commit();
                break;
            case 2:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(stickersFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(emojiconFragment).commit();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.show(voiceRecordingFragment).commit();
                break;

        }
    }

    private void takeCameraPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                imageFromCamera();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA}, GlobalState.CAMERA_PERMISSIONS);
            }
        } else {
            imageFromCamera();
        }
    }

    @Override
    public void picSelectionResponse(RelativeLayout relativeLayout, int position, boolean picselection) {
        chatListItemSelect = relativeLayout;
        if (ChatGlobalStates.ISLONGPRESS) {
            if (isHeaderViewAvl)
                position = position - 1;
            String type = chatDetailsItemBeans.get(position)
                    .getMessageType();
            if (!type.equalsIgnoreCase(ChatGlobalStates.DATE_TYPE) && copyForwardFlag) {
                onListItemClick(relativeLayout, position);
            }

        } else {
            if (relativeLayout != null) {
                ChatGlobalStates.ISLONGPRESS = true;
                onListItemClick(relativeLayout, position);
            }
        }
    }

    private void imageFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraFile = getCaptureImagePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, GlobalState.WRITE_PERMISSIONS);
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraFile = getCaptureImagePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
        }
    }

    private void updateUserTyping(String value) {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("typing", value);
            objJson.accumulate("room_id", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.TYPING_FRIEND, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {
                            try {
                                if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(ChatActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ChatActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void recordVoice() {


    }

    @Override
    public void onStickerButtonClicked(View v) {
        loadFragments(1);
    }


    @Override
    public void onEmojiConButtonClicked() {
        loadFragments(0);

    }

    @Override
    public void onStickerClicked(ChatSticker sticker) {
        postSticker(sticker.getStickersId(), sticker.getStickersPic());
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(userText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(userText);
    }

    void startRepeatingTask() {
        devicesPinger.start_pinger();
    }

    void stopRepeatingTask() {
        devicesPinger.stop_pinger();
    }

    @Override
    public void stopStartPinger(int value) {
        /*switch (value) {
            case 0:
                stopRepeatingTask();
                break;
            case 1:
                startRepeatingTask();
                break;
        }*/
    }

    @Override
    public void sendAudioFile(File file, String type, String ext, String filePath) {
        fragmentVisiblity = false;
        fragmentContainer.setVisibility(View.GONE);
        postAudio(file, type,
                ext, filePath);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Chat Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    public class DevicesPinger implements Runnable {

        private int delay = 500;
        private int period = 2000;
        private boolean stop_timer = false;

        public DevicesPinger() {
        }

        @Override
        public void run() {
            run_pinger();

        }

        private void run_pinger() {
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    if (stop_timer == true) {
                        this.cancel();
                    } else {
                        if (roomId != null) {
                            if (friendsIsBlocked != null
                                    && !friendsIsBlocked.equalsIgnoreCase("1")) {
                                getAllMassages();
                            }

                        }

                    }
                }
            }, delay, period);
        }

        public void stop_pinger() {
            stop_timer = true;
        }

        public void start_pinger() {
            isStopped = false;
            isStarted = true;
            stop_timer = false;
            run_pinger();

        }
    }

    private void getRoomId() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("req_user_id", friendsUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.GET_CHAT_ROOM, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {
                            try {
                                if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                                    roomId = response.getJSONObject("Result").getString("room_id");
                                    initializeChatScreen();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(ChatActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ChatActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void getAllMassages() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("last_msgid", getLastMessageId());
            objJson.accumulate("unseen_msgid", getUnseenMsgId());
            objJson.accumulate("receivers", friendsUserId);
            objJson.accumulate("seen", "1");
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
                            if (json_LL.has("edited_message")) {

                                JSONArray editedMsgArray = json_LL.getJSONArray("edited_message");
                                parseEditedMsg(editedMsgArray);
                            }
                        } catch (JSONException e) {

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
                        userIsTyping(userTyping);
                    }
                    if (json_LL.has("msgseentime")) {
                        JSONArray msgSeenTime = json_LL
                                .getJSONArray("msgseentime");
                        parseMsgSeen(msgSeenTime);
                    }
                    if (json_LL.has("lastseen")) {
                        friendsLastSeen = json_LL.getString("lastseen");
                    }
                    if (!postMsgFlag) {
                        JSONArray resultArray = json_LL.getJSONArray("Data");
                        int resultArraySize = resultArray.length();
                        if (resultArraySize > 0) {
                            if (resultArraySize == 1) {
                                JSONObject noMsgObject = resultArray
                                        .getJSONObject(0);
                                if (noMsgObject.has("status")) {
                                    String noMsgStatus = noMsgObject
                                            .getString("status");
                                    if (noMsgStatus
                                            .equalsIgnoreCase("No Messages")) {
                                        newMessageFlag = false;
                                        // firstTimeFlag = false;
                                        return;
                                    }
                                }
                            }
                            parseResponseArray(resultArray, false, false, true);
                        }
                        if (resultArraySize > 0) {
                            refreshChatList(false);
                        }
                    } else {
                        postMsgFlag = false;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void userIsTyping(JSONArray userTyping) {
        ContactDataSource contactDataSource = new ContactDataSource(this);
        String groupUsers = null, name = null, mobile = null, userId = null, isTyping = null;
        JSONObject userTypingObject;
        for (int i = 0; i < userTyping.length(); i++) {
            try {
                userTypingObject = userTyping.getJSONObject(i);
                isTyping = userTypingObject.getString("typing");

                if (isTyping != null) {
                    if (isTyping.equals("1")) {
                        if (userTypingObject.has("profile_name"))
                            name = userTypingObject.getString("profile_name");
                        if (userTypingObject.has("mobile"))
                            mobile = userTypingObject.getString("mobile");
                        if (userTypingObject.has("user_id"))
                            userId = userTypingObject.getString("user_id");

                        if (name != null
                                && userId != null
                                && appController.getUserId() != null
                                && !userId.equalsIgnoreCase(appController.getUserId())) {
                            if (groupUsers == null) {
                                if (mobile != null) {
                                    String contactName = contactDataSource
                                            .getContactName(mobile);
                                    if (contactName != null)
                                        groupUsers = contactName;
                                    else
                                        groupUsers = mobile;
                                }
                            } else {

                                if (mobile != null) {
                                    String contactName = contactDataSource
                                            .getContactName(mobile);
                                    if (contactName != null)
                                        groupUsers = groupUsers + ","
                                                + contactName;
                                    else
                                        groupUsers = groupUsers + "," + mobile;
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (groupUsers != null) {
            if (chatType != null
                    && chatType.equalsIgnoreCase("group")
                    && !lastSeenText
                    .getText()
                    .toString()
                    .equalsIgnoreCase(
                            groupUsers
                                    + " "
                                    + getResources().getString(
                                    R.string.typing_text))) {
                lastSeenText.setVisibility(View.VISIBLE);
                lastSeenText.setText(groupUsers + " "
                        + getResources().getString(R.string.typing_text));
            } else if (!lastSeenText
                    .getText()
                    .toString()
                    .equalsIgnoreCase(
                            groupUsers
                                    + " "
                                    + getResources().getString(
                                    R.string.typing_text))) {
                lastSeenText.setVisibility(View.VISIBLE);
                lastSeenText.setText(getResources().getString(
                        R.string.typing_text));
            }

        } else {
            lastSeenBeforeTyping();
        }

    }


    public void parseMsgSeen(JSONArray messageUnseenArray) {
        ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                getApplicationContext());
        // if (messageUnseenArray.length() > 0)
        //     unseenMessageHashList.clear();
        boolean flag = false;
        for (int i = 0; i < messageUnseenArray.length(); i++) {
            try {
                JSONObject messageUnseenObject = messageUnseenArray
                        .getJSONObject(i);
                if (!unseenMessageHashList.containsKey(messageUnseenObject.getString("message_id"))) {
                    flag = true;
                    String chatMessageSeenIdTemp = messageUnseenObject
                            .getString("message_id");
                    String chatMessageSeenTimeTemp = messageUnseenObject
                            .getString("seentime");
                    UnseenMessageBean unseenMessageBean = new UnseenMessageBean();
                    unseenMessageBean.setChatMessageSeenId(chatMessageSeenIdTemp);
                    unseenMessageBean
                            .setChatMessageSeenTime(chatMessageSeenTimeTemp);
                    unseenMessageHashList.put(chatMessageSeenIdTemp,
                            unseenMessageBean);
                    moreChatMessageDatasource.open();
                    moreChatMessageDatasource.updateLastseen(chatMessageSeenIdTemp,
                            chatMessageSeenTimeTemp);
                    moreChatMessageDatasource.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (messageUnseenArray.length() > 0
                && chattingListAdapter != null && flag) {
            // CommonUtil.playRcvedMsgTune(oneToOneChatActivity);
            chattingListAdapter.notifyDataSetChanged();
        }
    }

    public void lastSeenBeforeTyping() {
        if (chatType != null && chatType.equals("group")) {
            if (groupMembers != null
                    && !groupMembers.isEmpty()
                    && !lastSeenText
                    .getText()
                    .toString()
                    .equals(groupMembers.substring(1)
                            + " "
                            + getResources()
                            .getString(R.string.and_you))) {
                lastSeenText.setText(groupMembers.substring(1)
                        + " " + getResources().getString(R.string.and_you));
                lastSeenText.setVisibility(View.VISIBLE);
            }
        } else {
            if (lastSeenPrivacy.equalsIgnoreCase("1") || appController.getPrefs().getString("last_seen_privacy", "0").equalsIgnoreCase("1")) {
                lastSeenText.setVisibility(View.GONE);
            } else if (lastSeenPrivacy.equalsIgnoreCase("2") || appController.getPrefs().getString("last_seen_privacy", "0").equalsIgnoreCase("2")) {
                appContactDataSource.open();
                if (appContactDataSource.isContactAvailableOrNot(frendsNo)) {
                    long onlineSec = DateTimeUtil.convertUtcToLocalDate(friendsLastSeen);
                    if (onlineSec < 60)
                    {

                        if (!lastSeenText.getText()
                                .toString().equalsIgnoreCase("online"))
                            lastSeenText
                                    .setText(getResources().getString(
                                            R.string.online_text));
                        lastSeenText
                                .setVisibility(View.VISIBLE);
                    } else {

                        if (friendsLastSeen != null
                                && !lastSeenText
                                .getText()
                                .toString()
                                .equalsIgnoreCase(
                                        getResources()
                                                .getString(
                                                        R.string.last_seen_text)
                                                + " "
                                                + lastSeenDateTime)) {

                            String lastSeenDateHolder = friendsLastSeen
                                    .substring(0,
                                            friendsLastSeen.indexOf(" "));
                            lastSeenDateTime = DateTimeUtil.lastSeenDate(lastSeenDateHolder, ChatActivity.this) + " " + getResources().getString(R.string.at_text) + " " + DateTimeUtil.LastSeenTweentyFourTo12(
                                    friendsLastSeen);
                            lastSeenText
                                    .setText(getResources().getString(
                                            R.string.last_seen_text)
                                            + " " + lastSeenDateTime);
                            lastSeenText
                                    .setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    lastSeenText.setVisibility(View.GONE);
                }
                appContactDataSource.close();
                lastSeenText.setVisibility(View.GONE);
            } else {
                long onlineSec = DateTimeUtil.convertUtcToLocalDate(friendsLastSeen);

                if (onlineSec < 60) {
                    if (!lastSeenText.getText()
                            .toString().equalsIgnoreCase("online"))
                        if(friendsLastSeen.length()>0)
                        lastSeenText.setText(getResources().getString(
                                        R.string.online_text));
                    lastSeenText
                            .setVisibility(View.VISIBLE);
                } else {
                    if (friendsLastSeen != null
                            && !lastSeenText
                            .getText()
                            .toString()
                            .equalsIgnoreCase(
                                    getResources()
                                            .getString(
                                                    R.string.last_seen_text)
                                            + " "
                                            + lastSeenDateTime))
                    {

                        String lastSeenDateHolder = friendsLastSeen
                                .substring(0,
                                        friendsLastSeen.indexOf(" "));
                        lastSeenDateTime = DateTimeUtil.lastSeenDate(lastSeenDateHolder, ChatActivity.this) + " " + getResources().getString(R.string.at_text) + " " + DateTimeUtil.LastSeenTweentyFourTo12(
                                friendsLastSeen);
                        lastSeenText
                                .setText(getResources().getString(
                                        R.string.last_seen_text)
                                        + " " + lastSeenDateTime);
                        lastSeenText
                                .setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }

    private void scrollMyListViewToBottom() {
        chatListView.setSelection(chattingListAdapter.getCount() - 1);
    }

    public void parseLatestOldMsgFromDBToLoad(
            ChatMessageModel chatingListItemDataObject, boolean latestOldMsgFlag)
    {
        try {
            if (latestOldMsgFlag) {
                chatDetailsItemBeansForDB.add(chatingListItemDataObject);
            } else {
                chatDetailsItemBeansForDB.add(0, chatingListItemDataObject);
            }
            chatMessageType = chatingListItemDataObject.getMessageType();
            chatMessageId = chatingListItemDataObject.getMsgId();
            chatMessageUserId = chatingListItemDataObject.getUserId();
            chatMessageRoomId = chatingListItemDataObject.getMessageRoomId();
            chatMessageSenderName = chatingListItemDataObject.getSenderName();
            chatMessageSenderPic = chatingListItemDataObject
                    .getRandomProfileImageUrl();
            chatMessage = chatingListItemDataObject.getActualMsg();
            if (chatMessage == null)
            {
                chatMessage = "";
            }
            chatMessageUserTime = chatingListItemDataObject.getMsgTime();
            chatMessageFileUrl = chatingListItemDataObject.getPostImgUrl();
            chatMessageLat = chatingListItemDataObject.getLatitude();
            chatMessageLan = chatingListItemDataObject.getLangitude();
            chatMessageSeen = chatingListItemDataObject.getChatMessageSeen();
            chatMessageSeenTime = chatingListItemDataObject
                    .getChatMessageSeenTime();
            chatFileThumbnail = chatingListItemDataObject
                    .getChatFileThumbnail();
            chatIsSent = chatingListItemDataObject.getChatIsSent();
            dummyMessageid = chatingListItemDataObject.getChatTempMessageId();
            stickerId = chatingListItemDataObject.getChatStickerAudioBuzzId();
            chatDisappear = chatingListItemDataObject.getChatDisappear();
            imageDownloadID = chatingListItemDataObject.getImageDownlaod();

            String dateStamp = DateTimeUtil.getDate(chatMessageUserTime);
            if (latestOldMsgFlag)
                messageDateCompair(dateStamp, null);
            else
                messageDateCompairOLD(dateStamp);
            moreChatChatingListItemBean = new ChatMessageModel();
            moreChatChatingListItemBean.setMessageType(chatMessageType);
            moreChatChatingListItemBean.setMsgId(chatMessageId);
            moreChatChatingListItemBean.setUserId(chatMessageUserId);
            moreChatChatingListItemBean.setMessageRoomId(chatMessageRoomId);
            moreChatChatingListItemBean.setSenderName(chatMessageSenderName);
            moreChatChatingListItemBean.setProfileImgUrl(chatMessageSenderPic);
            moreChatChatingListItemBean.setActualMsg(chatMessage);
            moreChatChatingListItemBean.setMsgTime(chatMessageUserTime);
            moreChatChatingListItemBean.setChatDisappear(chatDisappear);
            moreChatChatingListItemBean.setChatEditMsg(chatingListItemDataObject.getChatEditMsg());
            moreChatChatingListItemBean.setMsgDate(dateStamp);
            moreChatChatingListItemBean.setChatStickerAudioBuzzId(stickerId);
            moreChatChatingListItemBean.setImageDownlaod(imageDownloadID);
            if (chatFileThumbnail != null) {
                moreChatChatingListItemBean
                        .setChatFileThumbnail(chatFileThumbnail);
            }
            if (chatMessageFileUrl != null) {
                moreChatChatingListItemBean.setPostImgUrl(chatMessageFileUrl);
            }
            if (chatMessageLat != null && chatMessageLan != null) {
                moreChatChatingListItemBean.setLatitude(chatMessageLat);
                moreChatChatingListItemBean.setLangitude(chatMessageLan);
            }
            moreChatChatingListItemBean.setMsgToFromFlag(false);
            if (chatMessageUserId.equals(appController.getUserId())
                    && Integer.parseInt(chatMessageId) == -1
                    && chatIsSent.equals("0")) {
                moreChatChatingListItemBean.setSingleTickFlag(true);
                if (chatMessageType.equals(ChatGlobalStates.PICTURE_TYPE)) {
                    capturePhotoBitmap = BitmapFactory
                            .decodeFile(chatMessageFileUrl);
                    moreChatChatingListItemBean
                            .setFileBitmap(capturePhotoBitmap);
                } else if (chatMessageType
                        .equals(ChatGlobalStates.AUDIO_TYPE) || chatMessageType
                        .equals(ChatGlobalStates.MUSIC_TYPE)) {
                    File audioFile = new File(chatMessageFileUrl);
                    moreChatChatingListItemBean.setFileAudio(audioFile);
                } else if (chatMessageType
                        .equals(ChatGlobalStates.VIDEO_TYPE)) {
                    File videoFile = new File(chatMessageFileUrl);
                    Bitmap videoThumb = ThumbnailUtils.createVideoThumbnail(
                            chatMessageFileUrl,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    moreChatChatingListItemBean.setFileVideo(videoFile);
                    moreChatChatingListItemBean.setVideoThumb(videoThumb);
                } else if (chatMessageType
                        .equals(ChatGlobalStates.CONTACT_TYPE)) {
                    chatContactName = chatMessageFileUrl.substring(
                            chatMessageFileUrl.lastIndexOf("/") + 1,
                            chatMessageFileUrl.lastIndexOf("."));
                    chatContactNumber = "";
                    moreChatChatingListItemBean.setContactName(chatContactName);
                    moreChatChatingListItemBean
                            .setContactNumber(chatContactNumber);
                }
            } else {
                moreChatChatingListItemBean.setSingleTickFlag(false);
            }
            moreChatChatingListItemBean.setChatMessageSeen(chatMessageSeen);
            moreChatChatingListItemBean
                    .setChatMessageSeenTime(chatMessageSeenTime);
            moreChatChatingListItemBean.setChatIsSent(chatIsSent);
            moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
            if (latestOldMsgFlag) {
                chatDetailsItemBeans.add(moreChatChatingListItemBean);
                if (chatMessageUserId.equals(appController.getUserId())
                        && Integer.parseInt(chatMessageId) == -1
                        && chatIsSent.equals("0")) {
                    msgResponseRef.addLocalPool(dummyMessageid,
                            chatDetailsItemBeans.size() - 1);
                }
                // ....
            } else {
                chatDetailsItemBeans.add(0, moreChatChatingListItemBean);
                // For offline Chat....
                msgResponseRef.increaseLocalPoolValue();
                if (chatMessageUserId.equals(appController.getUserId())
                        && Integer.parseInt(chatMessageId) == -1
                        && chatIsSent.equals("0")) {
                    msgResponseRef.addLocalPool(dummyMessageid, 0);
                }
                // ....
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static String dateStampPrvs = null;

    public void messageDateCompair(String msgDate, String dummyMessageid) {

        if (dateStampPrvs != null) {
            if (!(dateStampPrvs.equals(msgDate))) {
                moreChatChatingListItemBean = new ChatMessageModel();
                moreChatChatingListItemBean.setMsgDate(msgDate);
                moreChatChatingListItemBean.setUserId(appController.getUserId());
                moreChatChatingListItemBean.setMsgDateFlag(true);
                moreChatChatingListItemBean.setActualMsg("");
                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.DATE_TYPE);
                if (dummyMessageid != null && dummyMessageid.length() > 0
                        && chatDetailsItemBeans.size() > 0) {
                    chatDetailsItemBeans.add(chatDetailsItemBeans.size() - 1,
                            moreChatChatingListItemBean);
                    msgResponseRef.addLocalPool(dummyMessageid,
                            chatDetailsItemBeans.size() - 1);
                } else {
                    chatDetailsItemBeans.add(moreChatChatingListItemBean);
                }

                dateStampPrvs = msgDate;
            }
        } else {
            dateStampPrvs = msgDate;
        }
    }

    static String dateStampPrvsOLD = null;

    public void messageDateCompairOLD(String msgDate) {
        if (dateStampPrvsOLD != null) {
            if (!(dateStampPrvsOLD.equals(msgDate))) {
                moreChatChatingListItemBean = new ChatMessageModel();
                moreChatChatingListItemBean.setMsgDate(dateStampPrvsOLD);
                moreChatChatingListItemBean.setMsgDateFlag(true);
                moreChatChatingListItemBean.setMsgDate(msgDate);
                moreChatChatingListItemBean.setActualMsg("");
                moreChatChatingListItemBean.setUserId(appController.getUserId());
                moreChatChatingListItemBean.setMsgDateFlag(true);
                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.DATE_TYPE);
                chatDetailsItemBeans.add(0, moreChatChatingListItemBean);
                dateStampPrvsOLD = msgDate;
            } else {
                dateStampPrvsOLD = msgDate;
            }
        } else {
            dateStampPrvsOLD = msgDate;
        }
    }


    private void parsePostResponseMSG(JSONObject jsonObjectResponse,
                                      String unsentMsgDataDummyId) {
        if (jsonObjectResponse != null) {
            try {
                if (jsonObjectResponse.getString("status").equalsIgnoreCase("success")) {

                    if (unsentMsgDataDummyId != null) {

                        msgResponseRef.clearLocalPoolData(unsentMsgDataDummyId);
                    }
                    JSONObject resultObjectResponse = jsonObjectResponse
                            .getJSONObject("Result");
                    JSONArray dataArrayResponse = resultObjectResponse
                            .getJSONArray("Data");
                    CommonUtil.playSentMsgTune(ChatActivity.this, appController);

                    parseResponseArray(dataArrayResponse, true, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startRepeatingTask();
    }


    private void parseResponseArray(JSONArray dataArrayResponse,
                                    boolean refreshChatListFlag, boolean veryFirstTimeFlag,
                                    boolean postReceiveFlag) {
        chatDetailsItemBeansForDBTemp.clear();
        if (newMessageFlag && !firstTimeFlag) {
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

            if (count == 1) {
                newMessages = getResources().getString(R.string.new_messages)
                        + " " + count + " "
                        + getResources().getString(R.string.one_message_text);
                moreChatChatingListItemBean = new ChatMessageModel();
                moreChatChatingListItemBean.setUserId(appController.getUserId());
                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.COUNT_TYPE);
                moreChatChatingListItemBean.setNewMessageCount(newMessages);
                chatDetailsItemBeans.add(moreChatChatingListItemBean);
                newMessagePosition = chatDetailsItemBeans.size() - 1;
                newMessageFlag = false;
                messageNewFlag = true;

            } else if (count > 0) {
                newMessages = getResources().getString(R.string.new_messages)
                        + " " + count + " "
                        + getResources().getString(R.string.messages_text);
                moreChatChatingListItemBean = new ChatMessageModel();
                moreChatChatingListItemBean.setUserId(appController.getUserId());
                moreChatChatingListItemBean.setMessageType(ChatGlobalStates.COUNT_TYPE);
                moreChatChatingListItemBean.setNewMessageCount(newMessages);
                chatDetailsItemBeans.add(moreChatChatingListItemBean);
                newMessagePosition = chatDetailsItemBeans.size() - 1;
                newMessageFlag = false;
                messageNewFlag = true;
            }


        }


        try {
            boolean soundflag = true;
            for (int i = 0; i < dataArrayResponse.length(); i++)
            {
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
                            .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE) || dataType
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
                                    mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO, "/Android/data/Melodygram" + getPackageName());
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
                                mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO, "/Android/data" + getPackageName());
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
                                        mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO, "/Android/data" + getPackageName());
                                    }
                                } else {
                                    if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.VIDEO_TYPE)) {
                                        mediaStorageDir = new File(
                                                GlobalState.ONE_TO_ONE_VIDEO);
                                    } else if (dataType
                                            .equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) || dataType
                                            .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {

                                        mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO, "/Android/data" + getPackageName());
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

                 int imageDownlaod=0;

                if (postPic != null) {
                    moreChatChatingListItemBeanForDB.setPostImgUrl(postPic);
                    if (dataType.equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE))
                    {
                        if (!userid.equalsIgnoreCase(AppController.getInstance().getUserId()))
                        {

                            if (disappear.equalsIgnoreCase("-1"))
                            {


                                if (!new CommonUtil().getPhotoAutoDownloadMobileData(ChatActivity.this) && !new CommonUtil().getPhotoAutoDownloadWifi(ChatActivity.this)) {

                                    imageDownlaod =0;

                                    downloadFile(APIConstant.BASE_URL + postPic, postPic.replace("uploads/chatfiles/", ""));


                                } else if (new CommonUtil().getPhotoAutoDownloadMobileData(ChatActivity.this) && new CommonUtil().getPhotoAutoDownloadWifi(ChatActivity.this)) {

                                    imageDownlaod =1;

                                    downloadFile(APIConstant.BASE_URL + postPic, postPic.replace("uploads/chatfiles/", ""));

                                } else if (new CommonUtil().getPhotoAutoDownloadMobileData(ChatActivity.this)) {

                                    if (new CommonUtil().chkMobileStatus(ChatActivity.this))
                                    {
                                        imageDownlaod =1;

                                        downloadFile(APIConstant.BASE_URL + postPic, postPic.replace("uploads/chatfiles/", ""));


                                    } else
                                    {
                                        imageDownlaod =0;


                                    }

                                } else if (new CommonUtil().getPhotoAutoDownloadWifi(ChatActivity.this))
                                {

                                    if (new CommonUtil().chkWifiStatus(ChatActivity.this))
                                    {
                                        imageDownlaod =1;

                                        downloadFile(APIConstant.BASE_URL + postPic, postPic.replace("uploads/chatfiles/", ""));

                                    } else {
                                        imageDownlaod =0;



                                    }

                                }


                                //  imageUrl = APIConstant.BASE_URL + postPic;
                                // imageName = postPic.replace("uploads/chatfiles/","");
                               //  new ImageDownloadAndSave().execute();


                                // downloadImagesToSdCard(APIConstant.BASE_URL + postPic,postPic.replace("uploads/chatfiles/",""));
                            }
                        }
                    }
                }

                moreChatChatingListItemBeanForDB.setImageDownlaod(imageDownlaod);
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
                if (serverNewOldMsgFlag) {
                    messageDateCompair(dateStamp, dummyMessageid);
                } else {
                    messageDateCompairOLD(dateStamp);
                }
                moreChatChatingListItemBean = new ChatMessageModel();
                moreChatChatingListItemBean.setMessageType(dataType);
                moreChatChatingListItemBean.setMsgId(messageid);
                moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
                moreChatChatingListItemBean.setUserId(userid);
                moreChatChatingListItemBean.setMessageRoomId(roomid);
                moreChatChatingListItemBean.setSenderName(sender);
                moreChatChatingListItemBean.setProfileImgUrl(senderpic);
                moreChatChatingListItemBean.setActualMsg(message);
                moreChatChatingListItemBean.setMsgTime(time);
                moreChatChatingListItemBean.setMsgDate(dateStamp);
                moreChatChatingListItemBean.setChatStickerAudioBuzzId(stickerId);
                moreChatChatingListItemBean.setChatDisappear(disappear);
                moreChatChatingListItemBean.setImageDownlaod(imageDownlaod);
                if (videoThumbData != null)
                {
                    moreChatChatingListItemBean
                            .setChatFileThumbnail(videoThumbData);
                }
                if (postPic != null) {
                    if (bitMapList.containsKey(dummyMessageid)) {
                        moreChatChatingListItemBean.setChatFileThumbnail(bitMapList
                                .get(dummyMessageid));
                        bitMapList.remove(dummyMessageid);
                    }
                    moreChatChatingListItemBean.setPostImgUrl(postPic);
                }
                if (lat != null && lng != null)
                {
                    moreChatChatingListItemBean.setLatitude(lat);
                    moreChatChatingListItemBean.setLangitude(lng);
                }
                moreChatChatingListItemBean.setMsgToFromFlag(false);
                moreChatChatingListItemBean.setSingleTickFlag(false);
                moreChatChatingListItemBean.setChatMessageSeen(seen);
                moreChatChatingListItemBean.setChatMessageSeenTime(seentime);
                if (serverNewOldMsgFlag) {
                    if (veryFirstTimeFlag) {
                        chatDetailsItemBeans.add(moreChatChatingListItemBean);
                    } else {

                        if (userid.equals(appController.getUserId())) {
                            if (dummyMessageid.trim().length() > 0) {
                                Integer chatListPosition = msgResponseRef
                                        .getPositionFromLocalPool(dummyMessageid);
                                if (chatListPosition != null) {
                                    if (chatListPosition < chatDetailsItemBeans
                                            .size()) {
                                        chatDetailsItemBeans.set(chatListPosition,
                                                moreChatChatingListItemBean);
                                    }
                                    msgResponseRef.clearLocalPool(dummyMessageid);
                                }
                            }
                        } else {
                            if (soundFlag)
                                CommonUtil.playRcvedMsgTune(ChatActivity.this, appController);
                            chatDetailsItemBeans
                                    .add(moreChatChatingListItemBean);
                            moreChatMessageDatasource.close();
                        }
                    }
                    if (refreshChatListFlag) {
                        refreshChatList(false);
                    }
                } else {
                    chatDetailsItemBeans.add(0, moreChatChatingListItemBean);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshChatList(boolean isFirstTimeInsert) {
        if (chattingListAdapter == null) {
            chattingListAdapter = new ChatMessageAdapter(ChatActivity.this, chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
        } else {
            chattingListAdapter = new ChatMessageAdapter(ChatActivity.this, chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
            // chattingListAdapter.notifyDataSetChanged();
        }
        scrollMyListViewToBottom();
        insertChatMessageToDB(chatDetailsItemBeansForDBTemp, true,
                isFirstTimeInsert);
    }

    public void insertChatMessageToDB(
            ArrayList<ChatMessageModel> chatDetailsItemBeansForDB,
            boolean isSentFlag, boolean isFirstTimeInsert)
    {

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
            int imageDownlaod = chatDBItemBeansDetails.getImageDownlaod();


            if (moreChatChatingListItemBeanList != null)
                moreChatChatingListItemBeanList.clear();
            if (isSentFlag) {
                if (isFirstTimeInsert) {
                    ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                            getApplicationContext());
                    moreChatMessageDatasource.open();
                    moreChatMessageDatasource.createChatMessage(chatMessageId,
                            chatMessageSenderName, chatMessageSenderPic,
                            chatMessageRoomId, chatMessageUserId, chatMessage,
                            chatMessageType, chatMessageFileUrl,
                            chatMessageLat, chatMessageLan,
                            chatMessageUserTime, chatMessageUserDate,
                            chatMessageToFromFlag, chatMessageSingleTickFlag,
                            chatMessageSeen, chatMessageSeenTime,
                            chatFileThumbnail, "", "1",
                            chatMessageTempId, "", chatDisappear, stickerId, "1",imageDownlaod);
                    moreChatMessageDatasource.close();
                } else {
                    if (chatMessageUserId.equals(appController.getUserId()))
                    {
                        ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                                getApplicationContext());
                        moreChatMessageDatasource.open();
                        moreChatChatingListItemBeanList = moreChatMessageDatasource
                                .getChatMessageAvailable(chatMessageUserId,
                                        chatMessageRoomId, chatMessageTempId);
                        moreChatMessageDatasource.close();
                        if (moreChatChatingListItemBeanList != null
                                && moreChatChatingListItemBeanList.size() > 0) {
                            String updateMsgId = moreChatChatingListItemBeanList
                                    .get(0).getMsgId();
                            String updateIssent = moreChatChatingListItemBeanList
                                    .get(0).getChatIsSent();
                            if (Integer.parseInt(updateMsgId) == -1
                                    && updateIssent.equals("0")) {
                                moreChatMessageDatasource.open();
                                moreChatMessageDatasource.updateChatMessage(
                                        chatMessageId, chatMessageSenderName,
                                        chatMessageSenderPic,
                                        chatMessageRoomId, chatMessageUserId,
                                        chatMessage, chatMessageType,
                                        chatMessageFileUrl, chatMessageLat,
                                        chatMessageLan, chatMessageUserTime,
                                        chatMessageUserDate,
                                        chatMessageToFromFlag,
                                        chatMessageSingleTickFlag,
                                        chatMessageSeen,
                                        chatMessageSeenTime, "",
                                        "1", chatMessageTempId, "", chatDisappear, stickerId, "1",imageDownlaod);


                                moreChatMessageDatasource.close();
                            } else {

                            }
                        } else {
                            moreChatMessageDatasource.open();
                            moreChatMessageDatasource.createChatMessage(
                                    chatMessageId, chatMessageSenderName,
                                    chatMessageSenderPic, chatMessageRoomId,
                                    chatMessageUserId, chatMessage,
                                    chatMessageType, chatMessageFileUrl,
                                    chatMessageLat, chatMessageLan,
                                    chatMessageUserTime, chatMessageUserDate,
                                    chatMessageToFromFlag,
                                    chatMessageSingleTickFlag,
                                    chatMessageSeen,
                                    chatMessageSeenTime, chatFileThumbnail, "", "1", chatMessageTempId,
                                    "", chatDisappear, stickerId, "1",imageDownlaod);
                            moreChatMessageDatasource.close();
                        }
                    } else {
                        ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                                getApplicationContext());
                        moreChatMessageDatasource.open();
                        moreChatChatingListItemBeanList = moreChatMessageDatasource
                                .getChatMessageAvailable(chatMessageUserId,
                                        chatMessageRoomId, chatMessageId);
                        if (moreChatChatingListItemBeanList != null
                                && moreChatChatingListItemBeanList.size() > 0) {
                            moreChatMessageDatasource.updateChatMessage(
                                    chatMessageId, chatMessageSenderName,
                                    chatMessageSenderPic, chatMessageRoomId,
                                    chatMessageUserId, chatMessage,
                                    chatMessageType, chatMessageFileUrl,
                                    chatMessageLat, chatMessageLan,
                                    chatMessageUserTime, chatMessageUserDate,
                                    chatMessageToFromFlag,
                                    chatMessageSingleTickFlag,
                                    chatMessageSeen,
                                    chatMessageSeenTime, "", "1",
                                    chatMessageTempId, "", chatDisappear, stickerId, "1",imageDownlaod);
                        } else {
                            moreChatMessageDatasource.createChatMessage(
                                    chatMessageId, chatMessageSenderName,
                                    chatMessageSenderPic, chatMessageRoomId,
                                    chatMessageUserId, chatMessage,
                                    chatMessageType, chatMessageFileUrl,
                                    chatMessageLat, chatMessageLan,
                                    chatMessageUserTime, chatMessageUserDate,
                                    chatMessageToFromFlag,
                                    chatMessageSingleTickFlag,
                                    chatMessageSeen,
                                    chatMessageSeenTime, chatFileThumbnail, "", "1", chatMessageTempId,
                                    "", chatDisappear, stickerId, "1",imageDownlaod);
                        }
                        moreChatMessageDatasource.close();
                    }
                }
            } else {
                ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
                        getApplicationContext());
                moreChatMessageDatasource.open();
                moreChatMessageDatasource.createChatMessage(chatMessageId,
                        chatMessageSenderName, chatMessageSenderPic,
                        chatMessageRoomId, chatMessageUserId, chatMessage,
                        chatMessageType, chatMessageFileUrl, chatMessageLat,
                        chatMessageLan, chatMessageUserTime,
                        chatMessageUserDate, chatMessageToFromFlag,
                        chatMessageSingleTickFlag,
                        chatMessageSeen,
                        chatMessageSeenTime, chatFileThumbnail,
                        "", "0", chatMessageTempId, "", chatDisappear, stickerId, "1",imageDownlaod);
                moreChatMessageDatasource.close();
            }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GlobalState.IMAGE_FROM_GALLERY:
//                    imageUri = data.getData();
//                    if (imageUri != null) {
//                        if (data.getClipData() != null)
//                        {
//                            ClipData mClipData = data.getClipData();
//
//                            for (int i = 0; i <mClipData.getItemCount(); i++)
//                            {
//                                ClipData.Item item = mClipData.getItemAt(i);
//                                 imageUri = item.getUri();
//                                String imagePath = CommonUtil.getRealPathFromURI(imageUri, ChatActivity.this);
//                                appController.displayFileImage(selectedImage, imagePath, null);
//                                ImageDetails imageDetails = new ImageDetails();
//                                imageDetails.setPath(imagePath);
//                                imageDetails.setDescription("");
//                                selectedImages.add(imageDetails);
//                                selectedImage.setTag(selectedImages.size() - 1);
//                                imageSelectionAdapter.notifyDataSetChanged();
//                                selectedImageDescription.setText("");
//                            }
//
//                        }

                          //String[] imagesPath = data.getStringExtra("data").split("\\|");
                    String[] imagesPath=null;

                          if(data.getStringExtra("data").contains(","))
                          {
                               imagesPath = data.getStringExtra("data").split(",");
                              for (int i=0;i<imagesPath.length;i++)
                              {

                                  ImageDetails imageDetails = new ImageDetails();
                                  if(imagesPath[i].contains("]"))
                                  {
                                      appController.displayFileImage(selectedImage, imagesPath[i].replace("]","").trim(), null);
                                      imageDetails.setPath(imagesPath[i].replace("]","").trim());
                                  }else
                                  {
                                      appController.displayFileImage(selectedImage, imagesPath[i], null);
                                      imageDetails.setPath(imagesPath[i].trim());
                                  }
                                  imageDetails.setDescription("");
                                  selectedImages.add(imageDetails);
                                  selectedImage.setTag(selectedImages.size() - 1);
                                  imageSelectionAdapter.notifyDataSetChanged();
                                  selectedImageDescription.setText("");
                              }
                          }else
                          {
                              String   singleimagesPath= data.getStringExtra("data");
                              appController.displayFileImage(selectedImage, singleimagesPath, null);
                              ImageDetails imageDetails = new ImageDetails();
                              imageDetails.setPath(singleimagesPath.trim());
                              imageDetails.setDescription("");
                              selectedImages.add(imageDetails);
                              selectedImage.setTag(selectedImages.size() - 1);
                              imageSelectionAdapter.notifyDataSetChanged();
                              selectedImageDescription.setText("");
                          }

                        imageSelectionDialog.show();
                        break;
                  //  }
                case GlobalState.CAMERA_REQUEST:
                    String cameraPic = cameraFile.getAbsolutePath();
                    if (cameraPic != null) {
                        appController.displayFileImage(selectedImage, cameraPic, null);
                        ImageDetails imageDetails = new ImageDetails();
                        imageDetails.setPath(cameraPic);
                        imageDetails.setDescription("");
                        selectedImageDescription.setText("");
                        selectedImages.add(imageDetails);
                        selectedImage.setTag(selectedImages.size() - 1);
                        imageSelectionAdapter.notifyDataSetChanged();
                        selectedImageDescription.setText("");
                        imageSelectionDialog.show();
                        break;
                    }
                case GlobalState.THEMES_REQUEST:
                    int result = data.getIntExtra("result", 0);
                    setThemeBg(result);
                    break;


                case GlobalState.PLACE_PICKER:
                    final Place place = PlacePicker.getPlace(data, ChatActivity.this);
                    final LatLng latLng = place.getLatLng();
                    //final CharSequence name = place.getName();
                    //final CharSequence address = place.getAddress();
                    String attributions = PlacePicker.getAttributions(data);
                    postLocation(ChatGlobalStates.LOCATION_TYPE, latLng.latitude, latLng.longitude);
                    break;
                case GlobalState.REQUEST_CODE_PDF_FILES:
                    String filePath = data.getStringExtra("fileSelected");
                    File pdfFile = new File(filePath);
                    long fileSize = pdfFile.length();
                    long check = (fileSize / (1024 * 1024));
                    if (check <= 10) {
                        if (pdfFile != null) {
                            postFilesData(
                                    data.getStringExtra("fileSelected"),
                                    pdfFile,
                                    ChatGlobalStates.FILE_TYPE,
                                    filePath.substring(filePath.lastIndexOf(".") + 1),
                                    data.getStringExtra("fileName"));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_size_error),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;


                case GlobalState.REQUEST_CODE_DOC_FILES:
                    String docFilePath = data.getStringExtra("fileSelected");
                    File docFile = new File(docFilePath);
                    long docFileSize = docFile.length();
                    long dockCheck = (docFileSize / (1024 * 1024));
                    if (dockCheck <= 10) {
                        if (docFile != null) {
                            postFilesData(
                                    data.getStringExtra("fileSelected"),
                                    docFile,
                                    ChatGlobalStates.FILE_TYPE,
                                    docFilePath.substring(docFilePath.lastIndexOf(".") + 1),
                                    data.getStringExtra("fileName"));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_size_error),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

                case GlobalState.REQUEST_CODE_ZIP_FILES:
                    String zipFilePath = data.getStringExtra("fileSelected");
                    File zipFile = new File(zipFilePath);
                    long zipFileSize = zipFile.length();
                    long zipCheck = (zipFileSize / (1024 * 1024));
                    if (zipCheck <= 10) {
                        if (zipFile != null) {
                            postFilesData(
                                    data.getStringExtra("fileSelected"),
                                    zipFile,
                                    ChatGlobalStates.FILE_TYPE,
                                    zipFilePath.substring(zipFilePath.lastIndexOf(".") + 1),
                                    data.getStringExtra("fileName"));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_size_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    break;

            }
        }
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageFromGallery();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GlobalState.READ_EXTERNAL_STORAGE);
            }
        } else {
            imageFromGallery();
        }
    }

    private void imageFromGallery()
    {

        Intent intent = new Intent(ChatActivity.this,MultiPhotoSelectActivity.class);
        startActivityForResult(intent,GlobalState.IMAGE_FROM_GALLERY);


//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, GlobalState.IMAGE_FROM_GALLERY);

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/jpeg");
//        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(Intent.createChooser(intent, "Complete action using"),GlobalState.IMAGE_FROM_GALLERY);
    }


    private void postFilesData(String filePath, File file, String type,
                               String extension, String fileName) {
        isStopped = true;
        stopRepeatingTask();
        postMsgFlag = true;
        moreChatChatingListItemBean = new ChatMessageModel();
        final String dummyMessageid = CommonUtil
                .generateTempMessageId(appController.getUserId());
        moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
        moreChatChatingListItemBean.setChatIsSent("0");
        moreChatChatingListItemBean.setMessageRoomId(roomId);
        moreChatChatingListItemBean.setMessageType(type);
        moreChatChatingListItemBean.setMsgId("-1");
        moreChatChatingListItemBean.setPostImgUrl(filePath);
        moreChatChatingListItemBean.setProfileImgUrl("");
        moreChatChatingListItemBean.setUserId(appController.getUserId());
        moreChatChatingListItemBean.setSenderName("");
        moreChatChatingListItemBean.setActualMsg("");
        moreChatChatingListItemBean.setFileAudio(file);
        moreChatChatingListItemBean.setFileData("");
        bitMapList.put(dummyMessageid, filePath);
        moreChatChatingListItemBean.setChatFileThumbnail(filePath);
        moreChatChatingListItemBean.setFileExtn(extension);
        moreChatChatingListItemBean.setFileName(fileName);
        moreChatChatingListItemBean.setMsgTime(DateTimeUtil.morechatTimeFormat());
        moreChatChatingListItemBean.setMsgDate(DateTimeUtil.morechatDateFormat());
        moreChatChatingListItemBean.setMsgToFromFlag(false);
        moreChatChatingListItemBean.setSingleTickFlag(true);

        // Adding to Database.....
        ArrayList<ChatMessageModel> chatListItem = new ArrayList<>();
        chatListItem.add(moreChatChatingListItemBean);
        insertChatMessageToDB(chatListItem, false, false);
        chatDetailsItemBeans.add(moreChatChatingListItemBean);

        // Adding to LocalPool.....
        msgResponseRef.addLocalPool(dummyMessageid,
                chatDetailsItemBeans.size() - 1);
        if (chattingListAdapter == null) {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            if (chattingListAdapter == null) {
                chattingListAdapter = new ChatMessageAdapter(ChatActivity.this,
                        chatDetailsItemBeans, this, this, this);
                chatListView.setAdapter(chattingListAdapter);
            } else {
                chattingListAdapter.notifyDataSetChanged();
            }
        } else {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter.notifyDataSetChanged();
        }
        scrollMyListViewToBottom();
        postFileNW(dummyMessageid, file, extension, roomId);
    }

    public void postFileNW(final String dummyMessageId, final File docFile, final String extention, final String roomId) {
        new UploadMediaAWS(docFile, extention,
                dummyMessageId, ChatGlobalStates.FILE_TYPE,
                new UploadMediaAWS.InterfaceAsyncResponse() {
                    @Override
                    public void asyncFinished(String response) {
                        isStopped = true;
                        stopRepeatingTask();
                        newMessageFlag = false;
                        if (Boolean.valueOf(response)) {

                            isStopped = true;
                            stopRepeatingTask();
                            newMessageFlag = false;
                            JSONObject objJson = new JSONObject();
                            try {
                                objJson.accumulate("user_id", appController.getUserId());
                                objJson.accumulate("room_id", roomId);
                                objJson.accumulate("type", ChatGlobalStates.FILE_TYPE);
                                objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
                                objJson.accumulate("last_msgid", getLastMessageId());
                                objJson.accumulate("needresponse", "1");
                                objJson.accumulate("extn", extention);
                                objJson.accumulate("dummymsgid", dummyMessageId);
                                objJson.accumulate("fileurl", docFile.getName());
                                if (disappearCheck.isChecked()) {
                                    objJson.accumulate("disappear", disappearTime);
                                } else {
                                    objJson.accumulate("disappear", "-1");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.v("req", objJson.toString());
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    APIConstant.SEND_LINK, objJson,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if (ChatGlobalStates.mediasOnProcess
                                                    .containsKey(dummyMessageId)) {
                                                ChatGlobalStates.mediasOnProcess
                                                        .remove(dummyMessageId);
                                            }
                                            Log.v("response", response.toString());
                                            if (response != null) {
                                                parsePostResponseMSG(response, dummyMessageId);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            AppController.getInstance().addToRequestQueue(jsonObjReq);

                        } else {
                            chatDetailsItemBeans.remove(chatDetailsItemBeans
                                    .size() - 1);
                            chattingListAdapter.notifyDataSetChanged();
                            startRepeatingTask();
                        }
                    }
                }, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
    }


    public void postMessage(String message) {
        String newMessage = message.trim();


        if (newMessage != null && newMessage.length() > 0) {
            postMsgFlag = true;
            GlobalState.IS_CHAT_USER = true;
            moreChatChatingListItemBean = new ChatMessageModel();
            String dummyMessageid = CommonUtil
                    .generateTempMessageId(appController.getUserId());
            moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
            moreChatChatingListItemBean.setChatIsSent("0");
            moreChatChatingListItemBean.setMessageRoomId(roomId);
            moreChatChatingListItemBean.setMessageType(ChatGlobalStates.MESSAGE_TYPE);
            moreChatChatingListItemBean.setMsgId("-1");
            moreChatChatingListItemBean.setUserId(appController.getUserId());
            moreChatChatingListItemBean.setSenderName("");
            moreChatChatingListItemBean.setProfileImgUrl("");
            moreChatChatingListItemBean.setActualMsg(newMessage);
            moreChatChatingListItemBean.setMsgTime(DateTimeUtil
                    .morechatTimeFormat());
            moreChatChatingListItemBean.setMsgDate(DateTimeUtil
                    .morechatDateFormat());
            moreChatChatingListItemBean.setMsgToFromFlag(false);
            moreChatChatingListItemBean.setSingleTickFlag(true);
            moreChatChatingListItemBean.setChatDisappear(textDisappearTime);
            ArrayList<ChatMessageModel> chatListItem = new ArrayList<>();
            chatListItem.add(moreChatChatingListItemBean);
            if (frienddBlocked != null
                    && !frienddBlocked.equalsIgnoreCase("1")) {
                insertChatMessageToDB(chatListItem, false, false);
            }

            chatDetailsItemBeans.add(moreChatChatingListItemBean);

            msgResponseRef.addLocalPool(dummyMessageid,
                    chatDetailsItemBeans.size() - 1);
            msgResponseRef.addOneToLocalPoolData(dummyMessageid,
                    moreChatChatingListItemBean);
            if (chattingListAdapter == null) {
                chattingListAdapter = new ChatMessageAdapter(ChatActivity.this,
                        chatDetailsItemBeans, this, this, this);
                chatListView.setAdapter(chattingListAdapter);
            } else {
                chattingListAdapter.notifyDataSetChanged();
            }
            scrollMyListViewToBottom();

            if (frienddBlocked != null
                    && !frienddBlocked.equalsIgnoreCase("1")) {
                postTextMsgNW(newMessage, dummyMessageid, roomId);
            }
        }
    }

    public void postTextMsgNW(String newMessage, final String dummyMessageId, String roomId) {
        isStopped = true;
        stopRepeatingTask();
        newMessageFlag = false;

        newMessage = CommonUtil.encodeTranslatedMessage(newMessage);

        int lastMessageId = Integer.parseInt(getLastMessageId()) + 1;

        GlobalState.LAST_MESSAGE_ID = String.valueOf(lastMessageId);
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("message", newMessage);
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
            objJson.accumulate("last_msgid", getLastMessageId());
            objJson.accumulate("type", ChatGlobalStates.MESSAGE_TYPE);
            objJson.accumulate("dummymsgid", dummyMessageId);
            if (disappearCheck.isChecked()) {
                objJson.accumulate("disappear", textDisappearTime);
            } else {
                objJson.accumulate("disappear", "-1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.POST_MESSAGE_URL, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.v("response", response.toString());
                        if (response != null) {
                            parsePostResponseMSG(response, dummyMessageId);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void postPicture(String selectedImagePath, int position, final String picMsg) {
        isStopped = true;
        stopRepeatingTask();
        final String dummyMessageid = CommonUtil
                .generateTempMessageId(appController.getUserId());
        bitMapList.put(dummyMessageid, selectedImagePath);
        postMsgFlag = true;
        moreChatChatingListItemBean = new ChatMessageModel();
        moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
        moreChatChatingListItemBean.setChatIsSent("0");
        moreChatChatingListItemBean.setMessageRoomId(roomId);
        moreChatChatingListItemBean.setMessageType(ChatGlobalStates.PICTURE_TYPE);
        moreChatChatingListItemBean.setMsgId("-1");
        moreChatChatingListItemBean.setUserId(appController.getUserId());
        moreChatChatingListItemBean.setSenderName("");
        moreChatChatingListItemBean.setProfileImgUrl("");
        moreChatChatingListItemBean.setActualMsg(picMsg);
        moreChatChatingListItemBean.setChatFileThumbnail(selectedImagePath);


        moreChatChatingListItemBean.setMsgTime(DateTimeUtil
                .morechatTimeFormat());
        moreChatChatingListItemBean.setMsgDate(DateTimeUtil
                .morechatDateFormat());
        moreChatChatingListItemBean.setMsgToFromFlag(false);
        moreChatChatingListItemBean.setSingleTickFlag(true);
        moreChatChatingListItemBean.setPostImgUrl(selectedImagePath);
        // Adding to Database.....
        ArrayList<ChatMessageModel> chatListItem = new ArrayList<ChatMessageModel>();
        chatListItem.add(moreChatChatingListItemBean);
        insertChatMessageToDB(chatListItem, false, false);
        // .....
        chatDetailsItemBeans.add(moreChatChatingListItemBean);
        // Adding to LocalPool.....
        msgResponseRef.addLocalPool(dummyMessageid,
                chatDetailsItemBeans.size() - 1);
        // .....
        if (chattingListAdapter == null) {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter = new ChatMessageAdapter(ChatActivity.this,
                    chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
        } else {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter.notifyDataSetChanged();
        }
        scrollMyListViewToBottom();
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                postPictureMsgNW(dummyMessageid, message.getData().getString("imagePath"), roomId, picMsg);
            }
        };
        CommonUtil.compressImage(selectedImagePath, "midQuality", mHandler, position);
    }

    public void postPictureMsgNW(final String dummyMessageid, String newPath, String roomId, final String picMsg) {
        newMessageFlag = false;
        String picMessage = CommonUtil.encodeTranslatedMessage(picMsg);
        String disappear = "-1";
        if (disappearCheck.isChecked()) {
            disappear = "10000";

        } else {
            disappear = "-1";
        }
        new UploadImage(new File(newPath), ChatActivity.this, new UploadImage.InterfaceAsyncResponse() {
            @Override
            public void asyncFinished(String response) {

                Log.v("response", response + "-");
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        parsePostResponseMSG(jsonObj, dummyMessageid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(roomId, ChatGlobalStates.PICTURE_TYPE, dummyMessageid, disappear, picMessage);
    }


    private void pickLocation() {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();

            Intent intent = intentBuilder.build(ChatActivity.this);

            startActivityForResult(intent, GlobalState.PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            // ...
        }
    }

    public void postLocation(String type, double lat, double lan) {
        isStopped = true;
        stopRepeatingTask();
        String latitude = String.valueOf(lat);
        String longitude = String.valueOf(lan);
        if (latitude != null && longitude != null) {
            postMsgFlag = true;
            moreChatChatingListItemBean = new ChatMessageModel();
            final String dummyMessageid = CommonUtil
                    .generateTempMessageId(appController.getUserId());
            moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
            moreChatChatingListItemBean.setChatIsSent("0");
            moreChatChatingListItemBean.setMessageRoomId(roomId);
            moreChatChatingListItemBean.setMessageType(type);
            moreChatChatingListItemBean.setMsgId("-1");
            moreChatChatingListItemBean.setUserId(appController.getUserId());
            moreChatChatingListItemBean.setSenderName("");
            moreChatChatingListItemBean.setProfileImgUrl("");
            moreChatChatingListItemBean.setLatitude(latitude);
            moreChatChatingListItemBean.setLangitude(longitude);
            moreChatChatingListItemBean.setMsgTime(DateTimeUtil
                    .morechatTimeFormat());
            moreChatChatingListItemBean.setMsgDate(DateTimeUtil
                    .morechatDateFormat());
            moreChatChatingListItemBean.setMsgToFromFlag(false);
            moreChatChatingListItemBean.setSingleTickFlag(true);
            // Adding to Database.....
            ArrayList<ChatMessageModel> chatListItem = new ArrayList<>();
            chatListItem.add(moreChatChatingListItemBean);
            insertChatMessageToDB(chatListItem, false, false);
            chatDetailsItemBeans.add(moreChatChatingListItemBean);
            msgResponseRef.addLocalPool(dummyMessageid,
                    chatDetailsItemBeans.size() - 1);
            if (chattingListAdapter == null) {
                if (chatDetailsItemBeans.size() < 20) {
                    chatListView.removeHeaderView(headerView);
                    isHeaderViewAvl = false;
                }
                chattingListAdapter = new ChatMessageAdapter(this,
                        chatDetailsItemBeans, this, this, this);
                chatListView.setAdapter(chattingListAdapter);
            } else {
                if (chatDetailsItemBeans.size() < 20) {
                    chatListView.removeHeaderView(headerView);
                    isHeaderViewAvl = false;
                }
                chattingListAdapter.notifyDataSetChanged();
            }
            scrollMyListViewToBottom();
            postLocationNW(dummyMessageid, roomId, lat, lan);
        }
    }

    private void postLocationNW(final String dummyMessageId, String roomId, double lat, double lan) {
        isStopped = true;
        stopRepeatingTask();
        newMessageFlag = false;
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
            objJson.accumulate("last_msgid", getLastMessageId());
            objJson.accumulate("lat", lat);
            objJson.accumulate("lng", lan);
            objJson.accumulate("dummymsgid", dummyMessageId);
            if (disappearCheck.isChecked()) {
                objJson.accumulate("disappear", disappearTime);
            } else {
                objJson.accumulate("disappear", "-1");
            }
            objJson.accumulate("type", ChatGlobalStates.LOCATION_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.POST_MESSAGE_URL, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {
                            parsePostResponseMSG(response, dummyMessageId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    public void postAudio(final File audioFile, final String type,
                          final String extention, final String selectedAudioPath) {

        isStopped = true;
        stopRepeatingTask();
        postMsgFlag = true;
        moreChatChatingListItemBean = new ChatMessageModel();
        final String dummyMessageid = CommonUtil
                .generateTempMessageId(appController.getUserId());
        moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
        moreChatChatingListItemBean.setChatIsSent("0");
        moreChatChatingListItemBean.setMessageRoomId(roomId);
        moreChatChatingListItemBean.setMessageType(type);
        moreChatChatingListItemBean.setMsgId("-1");
        moreChatChatingListItemBean.setPostImgUrl(selectedAudioPath);
        moreChatChatingListItemBean.setUserId(appController.getUserId());
        moreChatChatingListItemBean.setSenderName("");
        moreChatChatingListItemBean.setProfileImgUrl("");
        moreChatChatingListItemBean.setActualMsg("");
        moreChatChatingListItemBean.setFileAudio(audioFile);
        moreChatChatingListItemBean.setChatFileThumbnail(selectedAudioPath);
        // moreChatChatingListItemBean.setFileData(encodedAudio);
        moreChatChatingListItemBean.setFileData("");
        moreChatChatingListItemBean.setFileExtn(extention);
        moreChatChatingListItemBean.setMsgTime(DateTimeUtil
                .morechatTimeFormat());

        moreChatChatingListItemBean.setMsgDate(DateTimeUtil
                .morechatDateFormat());
        moreChatChatingListItemBean.setMsgToFromFlag(false);
        moreChatChatingListItemBean.setSingleTickFlag(true);
        // Adding to Database.....
        ArrayList<ChatMessageModel> chatListItem = new ArrayList<>();
        chatListItem.add(moreChatChatingListItemBean);
        insertChatMessageToDB(chatListItem, false, false);
        chatDetailsItemBeans.add(moreChatChatingListItemBean);
        // Adding to LocalPool.....
        msgResponseRef.addLocalPool(dummyMessageid,
                chatDetailsItemBeans.size() - 1);
        if (chattingListAdapter == null) {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter = new ChatMessageAdapter(this,
                    chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
        } else {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter.notifyDataSetChanged();
        }
        scrollMyListViewToBottom();
        postAudioMsgNW(dummyMessageid, audioFile, type, extention, roomId);
    }


    public void postAudioMsgNW(final String dummyMessageId,
                               final File audioFile, final String type, final String extention,
                               final String roomId) {
        new UploadMediaAWS(audioFile, extention, dummyMessageid, type
                , new UploadMediaAWS.InterfaceAsyncResponse() {
            @Override
            public void asyncFinished(String response) {
                isStopped = true;
                stopRepeatingTask();
                newMessageFlag = false;
                if (Boolean.valueOf(response)) {
                    isStopped = true;
                    stopRepeatingTask();
                    newMessageFlag = false;
                    JSONObject objJson = new JSONObject();
                    try {
                        objJson.accumulate("user_id", appController.getUserId());
                        objJson.accumulate("room_id", roomId);
                        objJson.accumulate("type", type);
                        objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
                        objJson.accumulate("last_msgid", getLastMessageId());
                        objJson.accumulate("needresponse", "1");
                        objJson.accumulate("extn", extention);
                        objJson.accumulate("dummymsgid", dummyMessageId);
                        objJson.accumulate("fileurl", audioFile.getName());

                        if (disappearCheck.isChecked()) {
                            objJson.accumulate("disappear", audioDisappearTime);
                        } else {
                            objJson.accumulate("disappear", "-1");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.v("request", objJson.toString());
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            APIConstant.SEND_LINK, objJson,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("response", response.toString());
                                    if (response != null) {
                                        if (ChatGlobalStates.mediasOnProcess
                                                .containsKey(dummyMessageId)) {
                                            ChatGlobalStates.mediasOnProcess
                                                    .remove(dummyMessageId);
                                        }
                                        parsePostResponseMSG(response, dummyMessageId);
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
        }, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
    }


    public void postSticker(String id, String path) {
        fragmentContainer.setVisibility(View.GONE);
        fragmentVisiblity = false;
        isStopped = true;
        stopRepeatingTask();
        postMsgFlag = true;
        moreChatChatingListItemBean = new ChatMessageModel();
        String dummyMessageid = CommonUtil.generateTempMessageId(appController.getUserId());
        moreChatChatingListItemBean.setChatTempMessageId(dummyMessageid);
        moreChatChatingListItemBean.setActualMsg(CommonUtil
                .encodeTranslatedMessage(id));
        moreChatChatingListItemBean.setChatIsSent("0");
        moreChatChatingListItemBean.setMessageRoomId(roomId);
        moreChatChatingListItemBean
                .setMessageType(ChatGlobalStates.STICKERS_TYPE);
        moreChatChatingListItemBean.setMsgId("-1");
        moreChatChatingListItemBean.setProfileImgUrl("");
        moreChatChatingListItemBean.setUserId(appController.getUserId());
        moreChatChatingListItemBean.setPostImgUrl(path);
        moreChatChatingListItemBean.setMsgTime(DateTimeUtil.morechatTimeFormat());
        moreChatChatingListItemBean.setMsgDate(DateTimeUtil.morechatDateFormat());
        moreChatChatingListItemBean.setChatStickerAudioBuzzId(id);
        moreChatChatingListItemBean.setMsgToFromFlag(false);
        moreChatChatingListItemBean.setSingleTickFlag(true);

        ArrayList<ChatMessageModel> chatListItem = new ArrayList<>();
        chatListItem.add(moreChatChatingListItemBean);
        insertChatMessageToDB(chatListItem, false, false);
        chatDetailsItemBeans.add(moreChatChatingListItemBean);
        msgResponseRef.addLocalPool(dummyMessageid,
                chatDetailsItemBeans.size() - 1);
        if (chattingListAdapter == null) {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter = new ChatMessageAdapter(this,
                    chatDetailsItemBeans, this, this, this);
            chatListView.setAdapter(chattingListAdapter);
        } else {
            if (chatDetailsItemBeans.size() < 20) {
                chatListView.removeHeaderView(headerView);
                isHeaderViewAvl = false;
            }
            chattingListAdapter.notifyDataSetChanged();
        }
        scrollMyListViewToBottom();
        postStickerMsgNW(dummyMessageid,
                id, roomId);
    }

    private void postStickerMsgNW(final String dummyMessageId,
                                  String stickersId, String roomId) {
        isStopped = true;
        stopRepeatingTask();
        newMessageFlag = false;
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("room_id", roomId);
            objJson.accumulate("time", DateTimeUtil.morechatTimeFormat());
            objJson.accumulate("last_msgid", getLastMessageId());
            objJson.accumulate("dummymsgid", dummyMessageId);
            objJson.accumulate("stickerid", stickersId);
            if (disappearCheck.isChecked()) {
                objJson.accumulate("disappear", disappearTime);
            } else {
                objJson.accumulate("disappear", "-1");
            }
            objJson.accumulate("type", ChatGlobalStates.STICKERS_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("request", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.POST_MESSAGE_URL, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        if (response != null) {
                            parsePostResponseMSG(response, dummyMessageId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public void hideSoftInPutKey() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(userText.getWindowToken(), 0);
        }
    }


    public void showSoftInPutKey() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(searchText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        searchText.requestFocus();
    }

    public class ImageSelectionAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ImageSelectionAdapter() {

            mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return selectedImages.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.selected_image_item, null);
                viewHolder.userPic = (ImageView) view.findViewById(R.id.thumbImage);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.userPic.setImageResource(android.R.color.white);
            appController.displayFileImage(viewHolder.userPic, selectedImages.get(position).getPath(), null);
            return view;
        }

        class ViewHolder {
            ImageView userPic;
        }
    }

    private void clearAllChat() {
        isStopped = true;
        stopRepeatingTask();
        ChatMessageDataSource messageDataSource = new ChatMessageDataSource(ChatActivity.this);
        messageDataSource.open();
        messageDataSource.deleteAllChatMessage(roomId);
        messageDataSource.close();
        chatDetailsItemBeans.clear();
        if (chattingListAdapter != null) {
            chatListView.removeHeaderView(headerView);
            isHeaderViewAvl = false;
            chattingListAdapter.notifyDataSetChanged();
        }
        startRepeatingTask();
    }

    private void setThemeBg(int value) {
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.chat_parent);
        switch (value) {
//            case 0:
//                parentLayout.setBackgroundResource(R.drawable.chat_default_bg);
//                break;
//            case 1:
//                parentLayout.setBackgroundResource(R.drawable.one_theme);
//                break;
//            case 2:
//                parentLayout.setBackgroundResource(R.drawable.two_theme);
//                break;
//            case 3:
//                parentLayout.setBackgroundResource(R.drawable.three_theme);
//                break;
//            case 4:
//                parentLayout.setBackgroundResource(R.drawable.four_theme);
//                break;
//            case 5:
//                parentLayout.setBackgroundResource(R.drawable.five_theme);
//                break;
//            case 6:
//                parentLayout.setBackgroundResource(R.drawable.six_theme);
//                break;
//            case 7:
//                parentLayout.setBackgroundResource(R.drawable.seven_theme);
//                break;
//            case 8:
//                parentLayout.setBackgroundResource(R.drawable.eight_theme);
//                break;
//            case 9:
//                parentLayout.setBackgroundResource(R.drawable.nine_theme);
//                break;
//            case 10:
//                parentLayout.setBackgroundResource(R.drawable.ten_theme);
//                break;
//            case 11:
//                parentLayout.setBackgroundResource(R.drawable.eleven_theme);
//                break;
//            case 12:
//                parentLayout.setBackgroundResource(R.drawable.twelve_theme);
//                break;
//            case 13:
//                parentLayout.setBackgroundResource(R.drawable.threeten_theme);
//                break;
//            case 14:
//                parentLayout.setBackgroundResource(R.drawable.fourteen_theme);
//                break;
//            case 15:
//                parentLayout.setBackgroundResource(R.drawable.fifteen_theme);
//                break;
//            case 16:
//                parentLayout.setBackgroundResource(R.drawable.sixteen_theme);
//                break;
//            case 17:
//                parentLayout.setBackgroundResource(R.drawable.seventeen_theme);
//                break;
//            case 18:
//                parentLayout.setBackgroundResource(R.drawable.eighteen_theme);
//                break;
//            case 19:
//                parentLayout.setBackgroundResource(R.drawable.nineteen_theme);
//                break;
            case 20:
                break;


        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GlobalState.CHECK_BACK_PRESSE = false;
        if (ChatGlobalStates.ISLONGPRESS) {
            if (isHeaderViewAvl)
                position = position - 1;
            String type = chatDetailsItemBeans.get(position)
                    .getMessageType();
            if (!type.equalsIgnoreCase(ChatGlobalStates.DATE_TYPE) && copyForwardFlag) {

                chatListItemSelect = (RelativeLayout) view
                        .findViewById(R.id.chat_item_parent);
                onListItemClick(chatListItemSelect, position);
            }
        } else {
            ChatMessageModel messageList = chatDetailsItemBeans.get(position);
            if (messageList.
                    getMessageType().equalsIgnoreCase(ChatGlobalStates.PICTURE_TYPE)) {
                String postImgUrl = messageList.getPostImgUrl();
                Intent intent = new Intent(
                        getApplicationContext(),
                        PicViewerActivity.class);
                intent.putExtra("imageURL", postImgUrl);
                intent.putExtra("disappear", "0");
                startActivity(intent);
            } else if (messageList.
                    getMessageType().equalsIgnoreCase(ChatGlobalStates.LOCATION_TYPE)) {
                String locationLat = messageList.getLatitude();
                String locationLan = messageList.getLangitude();

                String coordinates = "http://maps.google.com/maps?daddr=" + locationLat + "," + locationLan;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(coordinates));
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        GlobalState.CHECK_BACK_PRESSE = false;
        ChatGlobalStates.ISLONGPRESS = true;
        selectedposition = position;
        chatListItemSelect = (RelativeLayout) view
                .findViewById(R.id.chat_item_parent);
        if (isHeaderViewAvl)
            position = position - 1;
        String type = chatDetailsItemBeans.get(position)
                .getMessageType();
        if (!type.equalsIgnoreCase(ChatGlobalStates.DATE_TYPE) && !copyForwardFlag) {
            copyForwardFlag = true;
            copyForwardFinishFlag = true;
            optionsParent.setVisibility(View.VISIBLE);
            chatOptionsParent.setVisibility(View.INVISIBLE);
            if (chatListItemSelect != null) {
                onListItemClick(chatListItemSelect, position);
            }
        }
        return true;
    }

    private int selectedListSize, selectedTextSize, selectedStickerSize,
            selectedMediaSize;

    public void onListItemClick(RelativeLayout chatListItemSelect, int position) {
        String msgIdSelected = chatDetailsItemBeans.get(position).getMsgId();
        if (!chatListItemSelect.isShown()) {
            chatListItemSelect.setVisibility(View.VISIBLE);
            ChatMessageModel itemBean = chatDetailsItemBeans.get(position);
            itemBean.setChatMsgPosition(position);
            ChatGlobalStates.chatDetailsItemSelected.put(msgIdSelected,
                    itemBean);
            String msgType = itemBean.getMessageType();
            if (msgType.equals(ChatGlobalStates.MESSAGE_TYPE)) {
                ChatGlobalStates.textItemSelected.put(msgIdSelected, itemBean);
            } else if (msgType.equals(ChatGlobalStates.STICKERS_TYPE)) {
                ChatGlobalStates.stickerItemSelected.put(msgIdSelected,
                        itemBean);
            } else if (msgType.equals(ChatGlobalStates.PICTURE_TYPE)
                    || msgType.equals(ChatGlobalStates.VIDEO_TYPE)
                    || msgType.equals(ChatGlobalStates.AUDIO_TYPE)) {
                ChatGlobalStates.mediaItemSelected.put(msgIdSelected, itemBean);
            }
        } else {
            chatListItemSelect.setVisibility(View.GONE);
            ChatGlobalStates.chatDetailsItemSelected.remove(msgIdSelected);
            ChatGlobalStates.textItemSelected.remove(msgIdSelected);
            ChatGlobalStates.stickerItemSelected.remove(msgIdSelected);
            ChatGlobalStates.mediaItemSelected.remove(msgIdSelected);
        }
        selectedListSize = ChatGlobalStates.chatDetailsItemSelected.size();
        chatSelectedText.setText("" + selectedListSize);
        if (selectedListSize > 0) {
            editButton.setClickable(true);
            copyButton.setClickable(true);
            forwardButton.setClickable(true);
            deleteButton.setClickable(true);
            editButton.setVisibility(View.VISIBLE);
            copyButton.setVisibility(View.GONE);
            forwardButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            selectedTextSize = ChatGlobalStates.textItemSelected.size();
            selectedStickerSize = ChatGlobalStates.stickerItemSelected.size();
            selectedMediaSize = ChatGlobalStates.mediaItemSelected.size();
            if (selectedStickerSize > 0 || selectedMediaSize > 0) {
                copyButton.setVisibility(View.GONE);
            } else {
                if (selectedTextSize > 0) {
                    copyButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            chatSelectedText.setText("" + 0);
            editButton.setClickable(false);
            copyButton.setClickable(false);
            forwardButton.setClickable(false);
            deleteButton.setClickable(false);
            editButton.setVisibility(View.VISIBLE);
            copyButton.setVisibility(View.VISIBLE);
            forwardButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

    }

    public void deleteSelectedChatMSG() {


        ChatGlobalStates.chatListItemSelected = new ArrayList<>(
                ChatGlobalStates.chatDetailsItemSelected.values());
        ChatGlobalStates.chatDetailsItemSelected.clear();
        Collections.sort(ChatGlobalStates.chatListItemSelected,
                ChatMessageModel.compareByPosition);
        ChatMessageDataSource messageDatasource = new ChatMessageDataSource(
                ChatActivity.this);
        messageDatasource.open();
        for (int i = 0; i < ChatGlobalStates.chatListItemSelected.size(); i++)
        {
            ChatMessageModel itemBean = ChatGlobalStates.chatListItemSelected
                    .get(i);
            messageDatasource.deleteSelectedChatMessage(itemBean.getMsgId());
            chatDetailsItemBeans.remove(itemBean.getChatMsgPosition() - i);
            if (chattingListAdapter != null)
                chattingListAdapter.notifyDataSetChanged();
            if (chatListItemSelect != null) {
                chatListItemSelect.setVisibility(View.GONE);
            }
        }
        messageDatasource.close();
        ChatGlobalStates.chatListItemSelected.clear();
    }

    public void copySelectedChatMSG() {
        ArrayList<ChatMessageModel> chatListItemSelected = new ArrayList<>(
                ChatGlobalStates.chatDetailsItemSelected.values());
        ChatGlobalStates.chatDetailsItemSelected.clear();
        String msgToCopy = null;
        int selectedMsgSize = chatListItemSelected.size();
        if (selectedMsgSize == 1) {
            msgToCopy = chatListItemSelected.get(0).getActualMsg().trim();
        } else {
            for (int i = 0; i < selectedMsgSize; i++) {
                ChatMessageModel itemBean = chatListItemSelected.get(i);
                String message = itemBean.getActualMsg().trim();
                String messageDate = DateTimeUtil.dateToCopyDateFormat(itemBean
                        .getMsgDate());
                String messageTime = DateTimeUtil.TweentyFourTo12(
                        itemBean.getMsgTime(), ChatActivity.this);
                String senderName = itemBean.getSenderName();
                if (!((senderName != null) && (senderName.trim().length() > 0)))
                    senderName = itemBean.getContactNumber();

                message = "[" + messageDate + ", " + messageTime + "] "
                        + senderName + ": " + message;

                if (msgToCopy == null)
                    msgToCopy = message;
                else
                    msgToCopy = msgToCopy + "\n" + message;
            }
        }

        if (msgToCopy != null) {
            if (msgToCopy.length() > 0) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

                    ClipboardManager clipboardMgr = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardMgr.setText(msgToCopy);
                } else {
                    // this api requires SDK version 11 and above, so suppress
                    // warning for now
                    android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text", msgToCopy);
                    clipboardMgr.setPrimaryClip(clip);
                }

                Toast.makeText(ChatActivity.this, getResources().getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void disableCopyForward() {

        copyForwardFlag = false;
        copyForwardFinishFlag = false;
        if (chattingListAdapter != null) {
            chattingListAdapter.notifyDataSetChanged();
        }
        chatOptionsParent.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        optionsParent.setVisibility(View.GONE);
        ChatGlobalStates.chatDetailsItemSelected.clear();
        ChatGlobalStates.textItemSelected.clear();
        ChatGlobalStates.stickerItemSelected.clear();
        ChatGlobalStates.mediaItemSelected.clear();
        // GlobalState.CHECK_BACK_PRESSE =false;
    }

    public void disableEdit() {
        copyForwardFlag = false;
        copyForwardFinishFlag = false;
        if (chattingListAdapter != null)
            chattingListAdapter.notifyDataSetChanged();
        chatOptionsParent.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        optionsParent.setVisibility(View.GONE);
        ChatGlobalStates.chatDetailsItemSelected.clear();
        ChatGlobalStates.textItemSelected.clear();
        ChatGlobalStates.stickerItemSelected.clear();
        ChatGlobalStates.mediaItemSelected.clear();
    }


    public void disableForward() {
        copyForwardFlag = false;
        copyForwardFinishFlag = false;
        if (chattingListAdapter != null)
            chattingListAdapter.notifyDataSetChanged();
        chatOptionsParent.setVisibility(View.VISIBLE);
        optionsParent.setVisibility(View.GONE);

        ChatGlobalStates.textItemSelected.clear();
        ChatGlobalStates.stickerItemSelected.clear();
        ChatGlobalStates.mediaItemSelected.clear();
    }


    @Override
    protected void onStop() {
        chattingListAdapter = null;
        GlobalState.IS_CHATSCREEN_VISIBLE = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }


    public void getAllUnsentMsgDB()
    {

        ChatMessageDataSource chatMessageDatasource = new ChatMessageDataSource(
                getApplicationContext());
        chatMessageDatasource.open();
        LinkedHashMap<String, ChatMessageModel> unsentItemList = chatMessageDatasource
                .getAllUnsentMsg(roomId);
        chatMessageDatasource.close();
        msgResponseRef.addLocalPoolData(unsentItemList);
        localPoolDataList = msgResponseRef.getLocalPoolDataList();

        if (localPoolDataList.size() > 0) {
            for (int i = 0; i < localPoolDataList.size(); i++) {
                postUnsentMessages(localPoolDataList.get(i));
            }
        }

    }

    public void postUnsentMessages(ChatMessageModel unsentMsgData) {
        String unsentMsgType = unsentMsgData.getMessageType();
        if (unsentMsgType.equals(ChatGlobalStates.MESSAGE_TYPE)) {

            postTextMsgNW(unsentMsgData.getActualMsg(),
                    unsentMsgData.getChatTempMessageId(),
                    unsentMsgData.getMessageRoomId());
            chattingListAdapter.notifyDataSetChanged();

        } else if (unsentMsgType.equals(ChatGlobalStates.STICKERS_TYPE)) {
            postStickerMsgNW(unsentMsgData.getChatTempMessageId(),
                    unsentMsgData.getChatStickerAudioBuzzId(), roomId);
        } else if (unsentMsgType.equals(ChatGlobalStates.PICTURE_TYPE)) {
            String unsentPicPath = unsentMsgData.getPostImgUrl();
            if (unsentPicPath != null)
                postPictureMsgNW(unsentMsgData.getChatTempMessageId(), unsentPicPath, unsentMsgData.getMessageRoomId(), unsentMsgData.getActualMsg());
        } else if (unsentMsgType.equals(ChatGlobalStates.AUDIO_TYPE)
                || unsentMsgType
                .equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
            if (!ChatGlobalStates.mediasOnProcess.containsKey(unsentMsgData
                    .getChatTempMessageId())) {
                String unsentAudioPath = unsentMsgData.getPostImgUrl();
                File audioFile = new File(unsentAudioPath);
                postAudioMsgNW(unsentMsgData.getChatTempMessageId(),
                        audioFile, unsentMsgType,
                        unsentAudioPath.substring(unsentAudioPath
                                .lastIndexOf(".") + 1),
                        unsentMsgData.getMessageRoomId());
            }
        } else if (unsentMsgType.equals(ChatGlobalStates.FILE_TYPE)) {

            if (!ChatGlobalStates.mediasOnProcess.containsKey(unsentMsgData
                    .getChatTempMessageId())) {

                postFileNW(unsentMsgData.getChatTempMessageId(), new File(unsentMsgData.getPostImgUrl()),
                        unsentMsgData.getFileExtn(),
                        unsentMsgData.getMessageRoomId());
            }
        } else if (unsentMsgType.equals(ChatGlobalStates.LOCATION_TYPE)) {
            postLocationNW(unsentMsgData.getChatTempMessageId(), unsentMsgData.getMessageRoomId(), Double.valueOf(unsentMsgData.getLatitude()), Double.valueOf(unsentMsgData.getLangitude()));
        }

    }

    private ChatBroadcastReceiver chatBroadcastReceiver;

    private void registerReceiver() {
        chatBroadcastReceiver = new ChatBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                ChatBroadcastReceiver.ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(chatBroadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        unregisterReceiver(chatBroadcastReceiver);
    }

    public class ChatBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.melodygram.activity.ChatBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("type").equalsIgnoreCase("Connected")) {
                if (localPoolDataList.size() == 0) {
                    stopRepeatingTask();
                    getAllUnsentMsgDB();
                } else {
                    startRepeatingTask();
                }
            } else {
                localPoolDataList.clear();
                // stop repeatinng task
                stopRepeatingTask();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GlobalState.READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromGallery();
                } else {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;
            case GlobalState.CAMERA_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromCamera();
                } else {

                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;

            case GlobalState.LOCATIONS_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickLocation();
                } else {

                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;
            case GlobalState.WRITE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraFile = getCaptureImagePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFile);
                    startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
                } else {

                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private File getCaptureImagePath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), GlobalState.POST_TEMP_PICTURE);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".png");
        return mediaFile;
    }

    @Override
    public void onEditPressed(int itemPosition) {

        ArrayList<ChatMessageModel> chatListItemSelected = new ArrayList<>(
                ChatGlobalStates.chatDetailsItemSelected.values());
        // editItemPos = itemPosition;
        // moreChatChatingListItemBean = chatDetailsItemBeans.get(itemPosition-1);
        editMessagePlace.setText(chatListItemSelected.get(0).getActualMsg());
        editMsgDialog.show();

    }


    public void updateEditMsgLocally(String actualMsg, ChatMessageModel editedItemBean, int editItemPos) {
        chatDetailsItemBeans.get(editItemPos).setActualMsg(actualMsg);
        chatDetailsItemBeans.get(editItemPos).setChatEditMsg("2");
        if (chattingListAdapter != null)
            chattingListAdapter.notifyDataSetChanged();
        ChatMessageDataSource messageDatasource = new ChatMessageDataSource(this);
        messageDatasource.open();
        messageDatasource.updateEditMsg(editedItemBean.getMsgId(), actualMsg, "2");
        messageDatasource.close();
        doUpdateeditMsg(editedItemBean, true);
    }

    public void doUpdateeditMsg(ChatMessageModel editedItemBean, final boolean imidiateActFlag) {
        final String messageId, message, transMessage, fromLanguage, toLanguage;
        messageId = editedItemBean.getMsgId();
        message = editedItemBean.getActualMsg();

        PostEditedMessage.getInstance(getApplicationContext()).editMessageUpdate(getApplicationContext(), messageId, encodeTranslatedMessage(message), this);
    }

    @Override
    public void editResponse(String respons) {


    }

    public void parseEditedMsg(JSONArray editedMsgArray) {
        if (editedMsgArray.length() > 0)
            //  editedMsgHashList.clear();

            for (int i = 0; i < editedMsgArray.length(); i++) {
                try {
                    String messageId, actualMsg;
                    JSONObject editedMsgObject = editedMsgArray.getJSONObject(i);
                    messageId = editedMsgObject.getString("message_id");
                    actualMsg = editedMsgObject.getString("message");
                    EditedMsgBean editedMsgBean = new EditedMsgBean();
                    editedMsgBean.setMessageId(messageId);
                    editedMsgBean.setActualMsg(actualMsg);
                    editedMsgBean.setEditId("2");
                    editedMsgHashList.put(messageId, editedMsgBean);
                    ChatMessageDataSource messageDatasource = new ChatMessageDataSource(getApplicationContext());
                    messageDatasource.open();
                    if (messageDatasource.isMsgAvailable(messageId))
                        messageDatasource.updateEditMsg(messageId, actualMsg, "2");// Last one "2" for edit icon on receiver end.  Replace with "1" if you dont want...
                    messageDatasource.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        if (editedMsgArray.length() > 0 && chattingListAdapter != null) {
            // MoreChatCommonUtil.playRcvedMsgTune(oneToOneChatActivity);
            {

                // chattingListAdapter.notifyDataSetChanged();

            }
        }
    }

    public static String encodeTranslatedMessage(String translatedMessage) {

        String encodedMessage = null;

        byte[] data = null;
        try {
            data = translatedMessage.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        encodedMessage = Base64.encodeToString(data, Base64.DEFAULT);

        return encodedMessage;

    }

    public static String decodeTranslatedMessage(String encodedMessage) {

        String decodedMessage = null;

        byte[] data1 = Base64.decode(encodedMessage, Base64.DEFAULT);
        try {
            decodedMessage = new String(data1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decodedMessage;

    }

    public void downloadFile(String uRl, String imageName) {


        try {
            DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(sdCard, "MelodayGramImage");

                        /*  if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

                        /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/MelodayGramImage", imageName.replace("uploads/chatfiles/", ""));

            mgr.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void refreshList() {
        if (chattingListAdapter != null) {
            chattingListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void callRefreshListner() {
        initializeChatScreen();
    }

    UpdateNotificationMessage updateNotificationMessage;

    private void registerChatMessage() {
        updateNotificationMessage = new UpdateNotificationMessage();
        IntentFilter intentFilter = new IntentFilter(UpdateNotificationMessage.ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(updateNotificationMessage, intentFilter);
    }

    private void unregisterChatMessage() {
        unregisterReceiver(updateNotificationMessage);
    }

    public class UpdateNotificationMessage extends BroadcastReceiver {
        public static final String ACTION = "com.melodygram.activity.chatUpdateMessage";

        @Override
        public void onReceive(Context context, Intent intent) {

            roomId = intent.getExtras().getString("room_id");
            friendsUserId = intent.getExtras().getString("firendsId");
            //     getAllMassages();
        }
    }


}
