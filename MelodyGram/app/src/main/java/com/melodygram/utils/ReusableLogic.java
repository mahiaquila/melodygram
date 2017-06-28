package com.melodygram.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;


/**
 * Created by FuGenX-03 on 11-02-2016.
 */
public class ReusableLogic {

    // Show Toast
    public static void toastMethod(Context activity, String Message) {
        Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
    }

    // For email validation
    public static Pattern getRfc2822() {
        final Pattern rfc2822 = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

        return rfc2822;
    }

    public static String capitalizeWord(String word) {
        if (word != null && word != "" && word.length() > 0) {
            StringBuilder sb = new StringBuilder(word);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        }
        return "";
    }

    // show Alert dialog
    public static void showOkAlertDialog(Activity activityRef, String mTitle, String mMessage) {
        new AlertDialog.Builder(activityRef)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // show Alert dialog
    public static void showOkAlertDialog(Context context, String mTitle, String mMessage) {
        new AlertDialog.Builder(context)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Used to convert 24hr format to 12hr format with AM/PM values

    public static String convert24HrFormateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }




    public static String compressImage(final String imageUriPath) {
        final String filename = getFilename();

        try {
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


            options.inSampleSize = calculateInSampleSize(options, actualWidth,
                    actualHeight);

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
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                        matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;

            out = new FileOutputStream(filename);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Bizzalley/" + "BizzalleyTempPic");
        if (!file.exists()) {
            file.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String uriSting = (file.getAbsolutePath() + "/" + "Img_" + timeStamp
                + "Bizzalley.png");
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

    public static String getBase64Pic(String picturePath) {
        String encodedBitmap = null;
        Bitmap newPictureBitmap = BitmapFactory.decodeFile(picturePath);
        if (newPictureBitmap != null) {
            String extention = picturePath.substring(picturePath.lastIndexOf(".") + 1);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (extention.equalsIgnoreCase("jpg") || extention.equalsIgnoreCase("jpeg")) {
                newPictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            } else if (extention.equalsIgnoreCase("png")) {
                newPictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            } else {
                newPictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return encodedBitmap;
    }

    public static String getBase64Pic(String picturePath, Bitmap newPictureBitmap) {
        String encodedBitmap = null;
        if (newPictureBitmap != null) {
            String extension = picturePath.substring(picturePath.lastIndexOf(".") + 1);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                newPictureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            } else if (extension.equalsIgnoreCase("png")) {
                newPictureBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            } else {
                newPictureBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return encodedBitmap;
    }

// split spacing form phone number

    public static String splitSpaceFromPhone(String number) {

        String[] array = number.split(" ");
        String spletedPhoneNumber = "";
        for (int i = 0; i < array.length; i++) {
            spletedPhoneNumber += array[i];
        }
        return number;
    }


    //convert 24 houres formate

    public static String convert24HrsFormate(String time) {

        String t = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("h:mm a");
        try {
            t = format2.format(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String convert12to24hrs(String time) {

        String t = null;
        SimpleDateFormat format2 = new SimpleDateFormat("h:mm a");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        try {
            t = format.format(format2.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    public static int v = 0, d = 1, i = 2, w = 3, e = 4;

    public static void printLog(String tag, String message, int level, boolean print) {
        if (print) {
            switch (level) {
                case 0:
                    Log.v(tag, message); // Verbose
                    break;
                case 1:
                    Log.d(tag, message); // Debug
                    break;
                case 2:
                    Log.i(tag, message); // Info
                    break;
                case 3:
                    Log.w(tag, message); // Warning
                    break;
                case 4:
                    Log.e(tag, message); // Error
                    break;
            }
        }

    }

    public static String generateTempMessageId(String userId) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "dd:MM:yy:HH:mm:ss", new Locale("en"));
        String stringDateTime = simpleDateFormat.format(calendar.getTime());

        return userId + stringDateTime.replaceAll(":", "")
                + calendar.get(Calendar.MILLISECOND);
    }

    public static String getFormattedTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
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


    public static String checkNull(String string) {
        if (string == null || string.equalsIgnoreCase("null")) {
            return "";
        }
        return string;
    }

    public static String getVendorFormattedTime(String format) {
        //Input date in String format
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date/time pattern of desired output date
        // if date is also required to show then us this formate
        // DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        // if only time is needed to show then use this formate
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(format);
            //old date format to new date format
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String getContactOpenCloseTime(String format) {
        //Input date in String format
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(format);
            //old date format to new date format
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }


    public static void closeKeyBoard(Activity mActivity, View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String timeDiff(String time) {
        SimpleDateFormat format;
        String currentDate;

        Calendar cal = Calendar.getInstance();
        Date d1 = null;
        Date d2 = null;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        currentDate = format.format(cal.getTime());
        System.out.println(currentDate);

        try {
            d1 = format.parse(time);
            d2 = format.parse(currentDate);

            // in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays >= 0) {
                if (diffDays == 0) {
                    format = new SimpleDateFormat("HH:mm aa");
                    System.out.println("Output : " + format.format(d1));
                    return format.format(d1);
                } else if (diffDays == 1) {
                    System.out.println("Output : Yesterday");
                    return "Yesterday";
                } else {
                    format = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println("Output : " + format.format(d1));
                    return format.format(d1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertUtcTime(String utcTime) {

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed = null; // => Date is in UTC now
        try {
            parsed = sourceFormat.parse(utcTime);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        destFormat.setTimeZone(tz);

        String result = destFormat.format(parsed);
        return result;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }



    // [END handleSignInResult]
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", "Key Hash= " + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    public Bitmap getBitmapFromUrl(String url) throws IOException {
        if (url != null && url.length() > 0) {
            URL imageURL = new URL(url);
            return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } else {
            return null;
        }
    }

    public static String convertDateFormate(String date, String inputFormat, String outputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
        String newFormat = formatter.format(testDate);
        System.out.println(".....Date..." + newFormat);
        return newFormat;
    }



    public static void showAlert(Context context, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(msg);
        alert.setPositiveButton("OK", listener);
        alert.show();
    }

}
