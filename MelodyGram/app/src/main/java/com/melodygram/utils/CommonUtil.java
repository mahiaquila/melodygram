package com.melodygram.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.model.CountryModel;
import com.melodygram.model.UploadDownloadViewsBean;
import com.melodygram.singleton.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by LALIT on 15-06-2016.
 */
public class CommonUtil {


    public static boolean isInternetConnected(Context activityRef) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activityRef
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager
                    .getAllNetworkInfo();
            if (networkInfos != null)
                for (int i = 0; i < networkInfos.length; i++)
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 170;
        int targetHeight = 170;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),

                Path.Direction.CW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        if (scaleBitmapImage.getWidth() >= scaleBitmapImage.getHeight()) {

            sourceBitmap = Bitmap.createBitmap(
                    scaleBitmapImage,
                    scaleBitmapImage.getWidth() / 2
                            - scaleBitmapImage.getHeight() / 2, 0,
                    scaleBitmapImage.getHeight(), scaleBitmapImage.getHeight());

        } else {

            sourceBitmap = Bitmap.createBitmap(
                    scaleBitmapImage,
                    0,
                    scaleBitmapImage.getHeight() / 2
                            - scaleBitmapImage.getWidth() / 2,
                    scaleBitmapImage.getWidth(), scaleBitmapImage.getWidth());
        }
        if (sourceBitmap != null) {
            canvas.drawBitmap(
                    sourceBitmap,
                    new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
                            .getHeight()), new Rect(0, 0, targetWidth,
                            targetHeight), null);
        }

        return targetBitmap;
    }

    public static Bitmap openPhoto(Activity context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null,
                null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(
                            data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static Bitmap openPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null,
                null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(
                            data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static String nonDigitsPhNum;

    public static String removeNonDigits(final String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return "";
        }
        nonDigitsPhNum = phoneNumber.replaceAll("\\D+", "");
        return (nonDigitsPhNum.charAt(0) == '0') ? removeZeroDigits(nonDigitsPhNum
                .substring(1)) : nonDigitsPhNum;
    }

    public static String removeZeroDigits(String zeroAtIndexZeroNum) {
        return (zeroAtIndexZeroNum.charAt(0) == '0') ? zeroAtIndexZeroNum
                .substring(1) : zeroAtIndexZeroNum;
    }

    static String countryCode;

    public static String removeCountryCode(String phoneNumber) {
        if (GlobalState.countryList.size() > 0) {
            for (int position = 0; position < GlobalState.countryList.size(); position++) {
                countryCode = GlobalState.countryList.get(position)
                        .getCountryCode();
                countryCode = removeNonDigits(countryCode);

                if (countryCode.equals(phoneNumber.substring(0,
                        countryCode.length()))) {
                    phoneNumber = phoneNumber.substring(countryCode.length());
                    break;
                }
            }
        } else {
            return phoneNumber;
        }
        return phoneNumber;
    }

    static CountryModel countryItemBean;

    public static void initializeCountryArrayList(Activity activityRef) {
        try {
            String jsonLocation = AssetCountryJSONFile("countries.json",
                    activityRef);
            JSONObject jsonobject = new JSONObject(jsonLocation);
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("items");
            if (GlobalState.countryList != null)
                GlobalState.countryList.clear();
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jb = (JSONObject) jarray.get(i);
                String country = jb.getString("country");
                String code = jb.getString("code");

                countryItemBean = new CountryModel();
                countryItemBean.setCountryName(country);
                countryItemBean.setCountryCode(code);

                GlobalState.countryList.add(countryItemBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String AssetCountryJSONFile(String filename, Context context)
            throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    public static String stringArrayToStringJoin(Object[] aArr, String sSep,
                                                 String userMobile) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (!userMobile.equalsIgnoreCase((String) aArr[i])) {
                if (i > 0)
                    sbStr.append(sSep);

                sbStr.append(aArr[i]);
            }

        }
        return sbStr.toString();
    }

    public static boolean subStringInStringArray(String playerNumber,
                                                 String searchString, int textLength) {
        for (String s : playerNumber.split(";")) {
            s = CommonUtil.removeNonDigits(s);
            s = (s.length() > 10) ? CommonUtil.removeCountryCode(s) : s;
            if ((textLength <= s.length())
                    && searchString
                    .equalsIgnoreCase(s.substring(0, textLength))) {
                return true;
            }
        }
        return false;
    }






    static String address = "", country;


    static String todaysDateFloat, chatDateTempFloat, todaysTempFloat,
            finalDate;
    static int day, month, year;

    public static String floatingDate(String chatDate, Activity activityRef)
    {
        todaysDateFloat = null;
        chatDateTempFloat = null;
        todaysTempFloat = null;
        finalDate = null;

        SharedPreferences freakPreferences = activityRef.getSharedPreferences(
                GlobalState.SHARED_PREF, 0);
        String currLng = freakPreferences.getString("selectedAppLanguage", "");
        if (currLng != null && currLng.equalsIgnoreCase("ku")
                || currLng.equalsIgnoreCase("fa")) {
            CommonUtil.setLocale(activityRef.getApplicationContext(), "ar");
        }

        todaysDateFloat = DateTimeUtil.morechatDateFormat();

        if (chatDate != null && todaysDateFloat != null
                && chatDate.equals(todaysDateFloat)) {

            finalDate = activityRef.getResources().getString(R.string.today);

            return finalDate;
        } else if (chatDate.substring(chatDate.indexOf("-")).equals(
                todaysDateFloat.substring(todaysDateFloat.indexOf("-")))) {
            chatDateTempFloat = chatDate.substring(0, chatDate.indexOf("-"));
            todaysTempFloat = todaysDateFloat.substring(0,
                    todaysDateFloat.indexOf("-"));
            if ((Integer.parseInt(chatDateTempFloat) + 1) == Integer
                    .parseInt(todaysTempFloat)) {

                finalDate = activityRef.getResources().getString(
                        R.string.yesterday);

                return finalDate;
            }
        }
        day = Integer.parseInt(chatDate.substring(0, chatDate.indexOf("-")));
        month = Integer.parseInt(chatDate.substring(chatDate.indexOf("-") + 1,
                chatDate.lastIndexOf("-")));
        year = Integer
                .parseInt(chatDate.substring(chatDate.lastIndexOf("-") + 1));

        finalDate = new SimpleDateFormat("dd, MM, yyyy")
                .format(new GregorianCalendar(year, month - 1, day).getTime());

        return finalDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String floatingNewDate(String chatDate)
    {
        finalDate = null;
        String todaysDateFloat, chatDateTempFloat, todaysTempFloat;
        int day = 0, month = 0, year = 0;

        todaysDateFloat = DateTimeUtil.morechatDateFormat();
        if (chatDate.equals(todaysDateFloat)) {
            finalDate = "Today";
            return finalDate;
        } else if (chatDate.substring(chatDate.indexOf("-")).equals(
                todaysDateFloat.substring(todaysDateFloat.indexOf("-")))) {
            chatDateTempFloat = chatDate.substring(0, chatDate.indexOf("-"));
            todaysTempFloat = todaysDateFloat.substring(0,
                    todaysDateFloat.indexOf("-"));
            if ((Integer.parseInt(chatDateTempFloat)) + 1 == Integer
                    .parseInt(todaysTempFloat)) {
                finalDate = "Yesterday";
                return finalDate;
            }
        }
        try {
            day = Integer
                    .parseInt(chatDate.substring(0, chatDate.indexOf("-")));
            month = Integer.parseInt(chatDate.substring(
                    chatDate.indexOf("-") + 1, chatDate.lastIndexOf("-")));
            year = Integer.parseInt(chatDate.substring(chatDate
                    .lastIndexOf("-") + 1));

            SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inFormat.parse(chatDate);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEE");
            String goal = outFormat.format(date);
            String fianlDate = new SimpleDateFormat("MMM dd, yyyy")
                    .format(new GregorianCalendar(year, month - 1, day)
                            .getTime());
           // return goal + " " + fianlDate;
            return  " " + fianlDate;
        } catch (NumberFormatException num) {
            num.printStackTrace();
            return "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalDate = new SimpleDateFormat("MMM dd, yyyy")
                .format(new GregorianCalendar(year, month - 1, day).getTime());
        return finalDate;
    }

    public static String mediaToDurationPath(String filePath)
    {
        String fileDuration = "0";
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

            metaRetriever.setDataSource(filePath);
            fileDuration = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            metaRetriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileDuration;
    }

    public static String mediaDurationFormat(String duration)
    {
        String fileDuration = "00:00:00";

        try {
            long dur = Long.parseLong(duration);
            String seconds = String.valueOf((dur % 60000) / 1000);
            String minutes = String.valueOf(dur / 60000);
            if (seconds.length() == 1) {
                fileDuration = "0" + minutes + ":0" + seconds;
            } else {
                fileDuration = "0" + minutes + ":" + seconds;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileDuration;
    }







    public static String capitalize(String s)
    {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    static String videoPath;

    public static String getRealPathFromURI(Uri contentUri, Activity context)
    {
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri,
                    proj, null, null, null);
            int position = 0;
            if (cursor != null && cursor.moveToPosition(position)) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                videoPath = cursor.getString(column_index); // I got a null
                // pointer exception
                // here.(But cursor
                // hreturns saome
                // value)
                cursor.close();

            }
        } catch (Exception e) {
            return contentUri.getPath();
        }
        return videoPath;

    }

    public static void playSentMsgTune(Activity activityRef,AppController appController)
    {
      ;
        boolean audioFlag = true;
        AudioManager audio = (AudioManager) activityRef
                .getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                audioFlag = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audioFlag = false;
                break;
            default:
                audioFlag = true;
                break;
        }
        if (audioFlag && appController.getPrefs().getBoolean("sound",true)) {
            MediaPlayer mPlayerSent = MediaPlayer.create(activityRef,
                    R.raw.sent);
            mPlayerSent.start();

        }
    }

    public static void playRcvedMsgTune(Activity activityRef, AppController appController)
    {
        boolean audioFlag = true;
        AudioManager audio = (AudioManager) activityRef
                .getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                audioFlag = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audioFlag = false;
                break;
            default:
                audioFlag = true;
                break;
        }
        if (audioFlag && appController.getPrefs().getBoolean("sound",true)) {
            MediaPlayer mPlayerRcved = MediaPlayer.create(activityRef,
                    R.raw.got);
            mPlayerRcved.start();
        }

    }



    public static SharedPreferences getSharedPreferences(Context activityRef)
    {
        return activityRef.getSharedPreferences(GlobalState.SHARED_PREF,
                0);
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



    public static String compressProfileImage(String imageUriPath, String imgQuality)
    {
        String filePath = imageUriPath;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not
        // loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as
        // 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;

        if (imgQuality.equalsIgnoreCase("highQuality")) {
            maxHeight = 2244.0f;
            maxWidth = 2448.0f;
        } else if (imgQuality.equalsIgnoreCase("midQuality")) {
            maxHeight = 1622.0f;
            maxWidth = 1224.0f;
        } else if (imgQuality.equalsIgnoreCase("lowQuality")) {
            maxHeight = 816.0f;
            maxWidth = 612.0f;
        }

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the
        // image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        // setting inSampleSize value allows to load a scaled down version of
        // the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        // this options allow android to claim the bitmap memory if it runs low
        // on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                middleY - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);

            } else if (orientation == 3) {
                matrix.postRotate(180);

            } else if (orientation == 8) {
                matrix.postRotate(270);

            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getProfileFilename();
        try {
            out = new FileOutputStream(filename);

            // write the compressed bitmap at the destination specified by
            // filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }


    public static String compressImage(final String imageUriPath,
                                       final String imgQuality, final Handler handler,final int position) {
        File file = new File(GlobalState.POST_TEMP_PICTURE);
        if (!file.exists()) {
            file.mkdirs();
            try {
                File noMediaFile = new File(GlobalState.POST_TEMP_PICTURE, ".nomedia");
                if (!noMediaFile.isFile())
                    noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
      final String uriSting = (file.getAbsolutePath() + "/" + "Img_" + timeStamp + position+"_melodygram.png");
        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    int imageQuality = 0;
                    File f = new File(imageUriPath);
                    long length = f.length();
                    length = length / 1024;

                    if (length > 5) {
                        imageQuality = 50;
                    } else {
                        imageQuality = 80;
                    }

                    String filePath = imageUriPath;
                    Bitmap scaledBitmap = null;

                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // by setting this field as true, the actual bitmap pixels
                    // are
                    // not
                    // loaded in the memory. Just the bounds are loaded. If
                    // you try the use the bitmap here, you will get null.
                    options.inJustDecodeBounds = true;
                    Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

                    int actualHeight = options.outHeight;
                    int actualWidth = options.outWidth;

                    // max Height and width values of the compressed image is
                    // taken
                    // as
                    // 816x612

                    float maxHeight = 816.0f;
                    float maxWidth = 612.0f;
                    if (imgQuality.equalsIgnoreCase("highQuality")) {
                        maxHeight = 2244.0f;
                        maxWidth = 2448.0f;
                    } else if (imgQuality.equalsIgnoreCase("midQuality")) {
                        maxHeight = 1622.0f;
                        maxWidth = 1224.0f;
                    } else if (imgQuality.equalsIgnoreCase("lowQuality")) {
                        maxHeight = 816.0f;
                        maxWidth = 612.0f;
                    }

                    float imgRatio = actualWidth / actualHeight;
                    float maxRatio = maxWidth / maxHeight;

                    // width and height values are set maintaining the aspect
                    // ratio
                    // of the
                    // image

                    if (actualHeight > maxHeight || actualWidth > maxWidth) {
                        if (imgRatio < maxRatio) {
                            imgRatio = maxHeight / actualHeight;
                            actualWidth = (int) (imgRatio * actualWidth);
                            actualHeight = (int) maxHeight;
                        } else if (imgRatio > maxRatio) {
                            imgRatio = maxWidth / actualWidth;
                            actualHeight = (int) (imgRatio * actualHeight);
                            actualWidth = (int) maxWidth;
                        } else {
                            actualHeight = (int) maxHeight;
                            actualWidth = (int) maxWidth;

                        }
                    }

                    // setting inSampleSize value allows to load a scaled down
                    // version of
                    // the original image

                    options.inSampleSize = calculateInSampleSize(options,
                            actualWidth, actualHeight);

                    // inJustDecodeBounds set to false to load the actual bitmap
                    options.inJustDecodeBounds = false;

                    // this options allow android to claim the bitmap memory if
                    // it
                    // runs low
                    // on memory
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inTempStorage = new byte[16 * 1024];

                    try {
                        // load the bitmap from its path
                        bmp = BitmapFactory.decodeFile(filePath, options);
                    } catch (OutOfMemoryError exception) {
                        exception.printStackTrace();

                    }
                    try {
                        scaledBitmap = Bitmap.createBitmap(actualWidth,
                                actualHeight, Bitmap.Config.ARGB_8888);
                    } catch (OutOfMemoryError exception) {
                        exception.printStackTrace();
                    }

                    float ratioX = actualWidth / (float) options.outWidth;
                    float ratioY = actualHeight / (float) options.outHeight;
                    float middleX = actualWidth / 2.0f;
                    float middleY = actualHeight / 2.0f;

                    Matrix scaleMatrix = new Matrix();
                    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

                    Canvas canvas = new Canvas(scaledBitmap);
                    canvas.setMatrix(scaleMatrix);
                    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                            middleY - bmp.getHeight() / 2, new Paint(
                                    Paint.FILTER_BITMAP_FLAG));

                    // check the rotation of the image and display it properly
                    ExifInterface exif;
                    try {
                        exif = new ExifInterface(filePath);

                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, 0);

                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(0);

                        } else if (orientation == 3) {
                            matrix.postRotate(180);

                        } else if (orientation == 8) {
                            matrix.postRotate(270);

                        }
                        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                                scaledBitmap.getWidth(),
                                scaledBitmap.getHeight(), matrix, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    FileOutputStream out = null;

                    out = new FileOutputStream(uriSting);

                    // write the compressed bitmap at the destination specified
                    // by
                    // filename.
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out);

                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("imagePath", uriSting);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        background.start();
        return uriSting;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getProfileFilename() {
        File file = new File(GlobalState.PROFILE_PICTURE);
        if (!file.exists()) {
            file.mkdirs();
            try {
                File noMediaFile = new File(GlobalState.PROFILE_PICTURE, ".nomedia");
                if (!noMediaFile.isFile())
                    noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String uriSting = (file.getAbsolutePath() + "/" + "Img_" + timeStamp + "_melodygram.png");
        return uriSting;

    }

    @SuppressLint("SimpleDateFormat")
    public static String getFilename() {
        File file = new File(GlobalState.POST_TEMP_PICTURE);
        if (!file.exists()) {
            file.mkdirs();
            try {
                File noMediaFile = new File(GlobalState.POST_TEMP_PICTURE, ".nomedia");
                if (!noMediaFile.isFile())
                    noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String uriSting = (file.getAbsolutePath() + "/" + "Img_" + timeStamp + "_melodygram.png");
        return uriSting;

    }


    @SuppressLint("SimpleDateFormat")
    public static String generateTempMessageId(String userId) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "dd:MM:yy:HH:mm:ss", new Locale("en"));
        String stringDateTime = simpleDateFormat.format(calendar.getTime());

        return userId + stringDateTime.replaceAll(":", "")
                + calendar.get(Calendar.MILLISECOND);
    }

    public static void showSoftKey(Activity activityRef, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftKey(Activity activityRef, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static boolean saveArray(String[] array, String arrayName,
                                    Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                GlobalState.SHARED_PREF, 0);
        Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public static String[] loadArray(String arrayName, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(
                GlobalState.SHARED_PREF, 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }





    public static float convertPixelsToDp(float px, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }




















    private String saveImageToSD(File file, Bitmap profilePicBitmap,
                                 String picName) {
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        profilePicBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public String getThemeStatus(Activity act, String id) {
        SharedPreferences pref = act.getSharedPreferences(
                GlobalState.SHARED_PREF, Context.MODE_PRIVATE);
        return pref.getString(id, null);
    }

    public void setThemeStatus(Activity act, String theme, String id) {

        SharedPreferences pref = act.getSharedPreferences(
                GlobalState.SHARED_PREF, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(id, theme);
        editor.commit();
    }

    public Drawable getDrawable(Activity act, String path) {
        Bitmap bitmapImage = BitmapFactory.decodeFile(path);

        return new BitmapDrawable(act.getResources(), bitmapImage);
    }



    public static Configuration setLocale(final Context ctx, final String lang) {
        final Locale loc = new Locale(lang);
        Locale.setDefault(loc);
        final Configuration cfg = new Configuration();
        cfg.locale = loc;
        ctx.getResources().updateConfiguration(cfg, null);
        ctx.getResources().getConfiguration();

        return cfg;
    }




    public static Dialog getDialog(Activity act) {
        final Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.common_dialog_layout);
        return dialog;
    }

    public static Long convertUtcToLocalDate(Context context) {
        long localTime = 4;
        try {
            SharedPreferences pref = context.getSharedPreferences(
                    GlobalState.SHARED_PREF, Context.MODE_PRIVATE);
            long last = pref.getLong("notificationTime", 0);
            if(last !=0) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                long now = c.getTimeInMillis();
                long difference = now - last;
                long differenceInSeconds = difference / DateUtils.SECOND_IN_MILLIS;
                localTime = differenceInSeconds;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localTime;
    }

    public static void savNotificationTime(Context context) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        long now = c.getTimeInMillis();
        SharedPreferences pref = context.getSharedPreferences(
                GlobalState.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("notificationTime", now);
        editor.commit();

    }



    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }


    private void saveURLToImage(String imageUrl)
    {
        System.out.println("@@@ image url:"+imageUrl);
        try {
            URL url = new URL(imageUrl);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            FileOutputStream fos = new FileOutputStream(GlobalState.POST_TEMP_PICTURE);
            fos.write(response);
            fos.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public String GetCountryZipCode(Context context){
        System.out.println("@@@ service");
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public String getDeviceId(Context context)
    {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return deviceId;
    }

   public  boolean chkMobileStatus(Context context) {

        boolean status=false;
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile.isConnectedOrConnecting ())
        {
            status =true;
        }else
        {
            status=false;
        }

       return status;
    }
    public  boolean chkWifiStatus(Context context) {

        boolean status=false;
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnectedOrConnecting ())
        {
            status =true;
        }else
        {
            status=false;
        }

        return status;
    }
    public void saveAudioAutoMobileDataDownload(Context context, boolean autoAudioMobileData) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autoAudioMobileData", autoAudioMobileData);
        editor.commit();
    }
    public boolean getAudioAutoMobileDataDownload(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autoAudioMobileData",true);
    }


    public void saveAudioAutoWifiDownload(Context context, boolean autoAudioWifi) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autoAudioWifi", autoAudioWifi);
        editor.commit();
    }
    public boolean getAudioAutoWifiDownload(Context context) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autoAudioWifi",true);
    }
    public void savePhotoAutoDoawnloadMobileData(Context context, boolean autoAudioWifi) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("photoAutomobile", autoAudioWifi);
        editor.commit();
    }
    public boolean getPhotoAutoDownloadMobileData(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("photoAutomobile",true);
    }
    public void savePhotoAutoDoawnloadWifi(Context context, boolean autoAudioWifi) {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autophotoWifi", autoAudioWifi);
        editor.commit();
    }
    public boolean getPhotoAutoDownloadWifi(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("melody", Context.MODE_PRIVATE);
        return pref.getBoolean("autophotoWifi",true);
    }
}
