package com.melodygram.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.melodygram.constants.GlobalState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LALIT on 15-06-2016.
 */
public class FileUtil {
    public static void createFolder(String folderNameInit) {
        if (!(new File(folderNameInit)).exists()) {
            File f = new File(folderNameInit);
            f.mkdir();
        }
    }

    public static void createVideoFolder() {
        File f = new File(GlobalState.VIDEO_COMPRESS_DIR_NAME);
        f.mkdirs();
        f = new File(GlobalState.VIDEO_COMPRESS_DIR_NAME+GlobalState.VIDEO_COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(GlobalState.VIDEO_COMPRESS_DIR_NAME
                + GlobalState.VIDEO_COMPRESS_TEMP_DIR);
        f.mkdirs();

        try {
            File noMediaFile = new File(GlobalState.VIDEO_COMPRESS_DIR_NAME+GlobalState.VIDEO_COMPRESSED_VIDEOS_DIR, ".nomedia");
            if (!noMediaFile.isFile())
                noMediaFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
