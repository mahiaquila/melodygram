package com.melodygram.constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.widget.TextView;

import com.melodygram.adapter.ChatMessageAdapter;
import com.melodygram.adapter.MusicStoreAdapter;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.model.ContactsModel;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.UploadDownloadViewsBean;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatGlobalStates {

	public static String ROOM_ID_OPENDED = null;
	public static final String STICKERS_TYPE = "sticker" ,DATE_TYPE = "date",COUNT_TYPE = "count", MESSAGE_TYPE = "message", PICTURE_TYPE = "pic", VIDEO_TYPE = "video", AUDIO_TYPE = "audio", LOCATION_TYPE = "location", FILE_TYPE = "file", CONTACT_TYPE = "contact",MUSIC_TYPE ="music";

	public static LinkedHashMap<String, ChatMessageModel> mediaItemSelected = new LinkedHashMap<>();
	public static LinkedHashMap<String, ChatMessageModel> textItemSelected = new LinkedHashMap<>();
	public static LinkedHashMap<String, ChatMessageModel> stickerItemSelected = new LinkedHashMap<>();
	
	public static LinkedHashMap<String, ChatMessageModel> chatDetailsItemSelected = new LinkedHashMap<>();
	public static ArrayList<ChatMessageModel> chatListItemSelected = null;
	public static ArrayList<ChatMessageModel> chatItemSelectedMulti = null;
	public static ArrayList<FriendsModel> selectedFriends = null;
	public static String bucketName = "morechat";
	public static String myAWSUserName = "AKIAI5FT7IOIYMB7NY6Q";
	public static String myAWSKey = "PF+30MbPDL8CxuIz/cjVCtWAXC/M4SerEmx6T/41";
	public static LinkedHashMap<String, String> mediaPathSelected = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, UploadDownloadViewsBean> mediasOnProcess = new LinkedHashMap<>();
	public static LinkedHashMap<String, ChatMessageAdapter.ViewHolder> mediasUploadOnProcess = new LinkedHashMap<>();
	public static LinkedHashMap<String,MusicStoreAdapter.ViewHolder> musicDownload = new LinkedHashMap<>();
	public static LinkedHashMap<String, TextView> mediasDownloadOnProcess = new LinkedHashMap<String, TextView>();

    public static 	String saveReceiverDisapper;
    public static 	String selectedReceiverMsgId;
	public static 	int selectedPosition;
	public static 	boolean ISLONGPRESS;
	public static boolean isFromChat;
}
