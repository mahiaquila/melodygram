package com.melodygram.constants;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import com.melodygram.model.CountryModel;

/**
 * Created by LALIT on 15-06-2016.
 */

public class GlobalState
{
	public static String lastestContactId;
	public static ArrayList<CountryModel> countryList = new ArrayList<CountryModel>();
	public static final String SHARED_PREF = "melodygramPref";

	public static final String ONE_TO_ONE_AUDIO = Environment
			.getExternalStorageDirectory().toString();

	public static final String PARENT_FOLDER = Environment.getExternalStorageDirectory() + "/MelodayGram";
	public static final String FILE_CACHE = PARENT_FOLDER + "/" + "FileCache";
	public static final String PROFILE_PIC = PARENT_FOLDER + "/" + "ProfilePicture";
	public static final String PROFILE_PIC_CAMERA = PARENT_FOLDER + "/" + "ProfilePicCamera";
	public static final String PROFILE_PIC_CAMERA_TEMP = PARENT_FOLDER + "/" + "ProfilePicCameraTemp";
	public static final String NOT_TO_DELETE = PARENT_FOLDER + "/" + "DoNotDelete";
	public static final String CAMERA_ONE_TO_ONE_PHOTO = PARENT_FOLDER + "/" + "CameraOneToOnePhoto";
	public static final String RECORDED_AUDIO = PARENT_FOLDER + "/" + "RecordOneToOneAudio";
//	public static final String ONE_TO_ONE_AUDIO = PARENT_FOLDER + "/" + "OneToOneAudio";
	public static final String CAMERA_VIDEO = PARENT_FOLDER + "/" + "CameraOneToOneVideo";
	public static final String ONE_TO_ONE_VIDEO = PARENT_FOLDER + "/" + "OneToOneVideo";
	public static final String ONE_TO_ONE_FILE = PARENT_FOLDER + "/" + "OneToOneFile";
	public static final String POST_TEMP_PICTURE = PARENT_FOLDER + "/" + "PostTempPicture";
	public static final String PROFILE_PICTURE = PARENT_FOLDER + "/" + "ProfilePic";
	public static final String PICTURE_GALLERY = PARENT_FOLDER + "/" + "PictureGallery";
	public static final String THEMES_GALLERY = PARENT_FOLDER + "/" + "ThemesGallery";
	public static final String STICKERS_GALLERY = PARENT_FOLDER + "/" + "StickersGallery";
	public static final String MUSIC_GALLERY = PARENT_FOLDER + "/" + "MusicGallery";
	public static final String LAZY_LIST = PARENT_FOLDER + "/" + "LazyCache";
	public static final String VIDEO_COMPRESS_DIR_NAME = PARENT_FOLDER+"/"+"VideoCompressor";
	public static final String VIDEO_COMPRESSED_VIDEOS_DIR ="/Compressed Videos";
	public static final String VIDEO_COMPRESS_TEMP_DIR ="/Temp";


	public static final int CAMERA_REQUEST = 201;
	public static final int IMAGE_FROM_GALLERY = 202;
	public static final int READ_EXTERNAL_STORAGE = 203;

	public static final int READ_EXTERNAL_STORAGE_FOLDER = 204;
	public static final int EXISTING_FOLDER = 205;

	public static final int VIDEO_CAMERA = 206;
	public static final int THEMES_REQUEST = 207;
	public static final int PLACE_PICKER = 208;


	public static final int REQUEST_CODE_DOC_FILES = 209;

	public static final int REQUEST_CODE_PDF_FILES = 210;
	public static final int REQUEST_CODE_ZIP_FILES = 211;
	public static final int REQUEST_MUSIC_FILE = 212;
	public static final int REQUEST_MUSIC_DOWNLOAD = 213;
	public static final int CAMERA_PERMISSIONS = 214;
	public static final int LOCATIONS_PERMISSIONS = 215;
	public static final int WRITE_PERMISSIONS = 216;
	public static  boolean IS_CHATSCREEN_VISIBLE;


	public static  String LAST_MESSAGE_ID=null;
	public static  String READ_RECEIPT=null;
	public static  boolean IS_CHAT_USER;
	public static  String RECEIVER_PROFILE_PIC;
	public static  boolean isCheckForegroundChat;
	public static File AUDIO_FILE=null ;
    public static boolean CHECK_BACK_PRESSE;
	public static boolean CHAT_BACK_BUTTON;

}


