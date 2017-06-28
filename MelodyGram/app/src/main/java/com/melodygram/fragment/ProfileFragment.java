package com.melodygram.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodygram.R;
import com.melodygram.activity.ChooseFolderActivity;
import com.melodygram.activity.PicViewerActivity;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CamClass;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.ImageCompressUtils;
import com.melodygram.utils.ReusableLogic;
import com.melodygram.utils.SharedPreferenceDB;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * Created by LALIT on 14-06-2016.
 */
public class ProfileFragment extends Fragment implements OnClickListener {
    private ImageView statusEditButton, userNameEditButton, profileImage, inviteButton, facebookButton, tweeterButton, googleButton, linkedinButton, emailButton, msgButton;
    private RelativeLayout galleryButton, cameraButton, existingPhotoButton, removePhotoButton;
    private Activity activity;
    private boolean socialNtkVisiblity;
    private boolean profileEditFlag;
    private EditText userNameText, statusText;
    private AppController appController;
    private boolean isStatusNameChanges;
    private TextView phoneNumber;
    private String profilePic;


    private File cameraFile;
    private String orientation = "";
    private Uri imageUriBg;

    ArrayList<String> arrayList_imagesPath;

    private static final int REQUEST_CODE_TAKE_PICTURE = 0x3;
    CamClass camClass;
    Uri picUri;
    private static String imageOrientation = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        activity = getActivity();
        arrayList_imagesPath = new ArrayList<String>();
        appController = AppController.getInstance();
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(this);
        facebookButton = (ImageView) view.findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(this);
        tweeterButton = (ImageView) view.findViewById(R.id.tweeter_button);
        tweeterButton.setOnClickListener(this);
        linkedinButton = (ImageView) view.findViewById(R.id.more_button);
        linkedinButton.setOnClickListener(this);
        googleButton = (ImageView) view.findViewById(R.id.google_button);
        googleButton.setOnClickListener(this);
        emailButton = (ImageView) view.findViewById(R.id.mail_button);
        emailButton.setOnClickListener(this);
        msgButton = (ImageView) view.findViewById(R.id.msg_button);
        msgButton.setOnClickListener(this);
        inviteButton = (ImageView) view.findViewById(R.id.invite_button);
        inviteButton.setOnClickListener(this);
        galleryButton = (RelativeLayout) view.findViewById(R.id.gallery_icon_parent);
        galleryButton.setOnClickListener(this);
        cameraButton = (RelativeLayout) view.findViewById(R.id.take_photo_parent);
        cameraButton.setOnClickListener(this);
        existingPhotoButton = (RelativeLayout) view.findViewById(R.id.existing_photo_parent);
        existingPhotoButton.setOnClickListener(this);
        removePhotoButton = (RelativeLayout) view.findViewById(R.id.remove_photo_parent);
        removePhotoButton.setOnClickListener(this);
        userNameText = (EditText) view.findViewById(R.id.name_edit_text);
        statusText = (EditText) view.findViewById(R.id.status_edit_text);
        userNameEditButton = (ImageView) view.findViewById(R.id.user_name_edit);
        userNameEditButton.setOnClickListener(this);
        statusEditButton = (ImageView) view.findViewById(R.id.status_edit);
        statusEditButton.setOnClickListener(this);
        phoneNumber = (TextView) view.findViewById(R.id.phone_number);
        view.findViewById(R.id.profile_image_edit).setOnClickListener(this);
        view.findViewById(R.id.options_parent).setOnClickListener(this);

        intitView();
    }


    private void intitView() {
        HashMap<String, String> map = appController.getUserDetails();
        profilePic = map.get("pic");
        String status = map.get("status");
        String profileName = map.get("profile_name");
        String phone = map.get("mobile");
        String code = map.get("country_code");
        SharedPreferenceDB.defaultInstance().saveAppProfilePic(activity, profilePic);
        if (profilePic.length() > 0)
            appController.displayUrlImage(profileImage, profilePic, null);
        if (status.length() > 0)
            statusText.setText(status);
        if (profileName.length() > 0)
            userNameText.setText(profileName);
         phoneNumber.setText("+" + code + phone);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                profilePic =SharedPreferenceDB.defaultInstance().getAppProfilePic(getActivity());
                Intent intent = new Intent(activity, PicViewerActivity.class);
                intent.putExtra("imageURL", profilePic);
                activity.startActivity(intent);
                break;

            case R.id.profile_image_edit:
                profilePicVisiblity();
                break;
            case R.id.options_parent:
                if (profileEditFlag) {
                    galleryButton.setVisibility(View.GONE);
                    cameraButton.setVisibility(View.GONE);
                    existingPhotoButton.setVisibility(View.GONE);
                    removePhotoButton.setVisibility(View.GONE);
                    profileEditFlag = false;
                }
                break;
            case R.id.invite_button:
                setSocialNtkVisiblity();
                break;
            case R.id.facebook_button:
                facebookshare();
                setSocialNtkVisiblity();
                break;

            case R.id.tweeter_button:
                tweeterShare();
                setSocialNtkVisiblity();
                break;

            case R.id.more_button:
                moreShare();
                setSocialNtkVisiblity();
                break;
           /* case R.id.linkedin_button:
                linkedinShare();
                setSocialNtkVisiblity();
                break;*/
            case R.id.google_button:
                googlePlusShare();
                setSocialNtkVisiblity();
                break;
            case R.id.mail_button:
                mailShare();
                setSocialNtkVisiblity();
                break;
            case R.id.msg_button:
                msgShare();
                setSocialNtkVisiblity();
                break;
            case R.id.gallery_icon_parent:
                profilePicVisiblity();
                openGallery();
                break;
            case R.id.existing_photo_parent:
                profilePicVisiblity();
                pickExistingImage();
                break;
            case R.id.remove_photo_parent:
                profilePicVisiblity();
                removeUserPic();
                break;

            case R.id.take_photo_parent:
                profilePicVisiblity();
                openCamera();
              //  cameraTakePic();
                break;
            case R.id.user_name_edit:
                userNameText.setClickable(true);
                userNameText.setCursorVisible(true);
                userNameText.setFocusable(true);
                isStatusNameChanges = true;
                break;
            case R.id.status_edit:
                statusText.setClickable(true);
                statusText.setFocusable(true);
                statusText.setCursorVisible(true);
                isStatusNameChanges = true;
                showDialog();
                break;
        }
    }


    private void setSocialNtkVisiblity() {
        if (socialNtkVisiblity) {
            facebookButton.setVisibility(View.GONE);
            tweeterButton.setVisibility(View.GONE);
            googleButton.setVisibility(View.GONE);
            linkedinButton.setVisibility(View.GONE);
            emailButton.setVisibility(View.GONE);
            msgButton.setVisibility(View.GONE);
            socialNtkVisiblity = false;

        } else {
            facebookButton.setVisibility(View.VISIBLE);
            tweeterButton.setVisibility(View.VISIBLE);
            googleButton.setVisibility(View.VISIBLE);
            linkedinButton.setVisibility(View.VISIBLE);
            emailButton.setVisibility(View.VISIBLE);
            msgButton.setVisibility(View.VISIBLE);
            socialNtkVisiblity = true;
        }
    }


    private void profilePicVisiblity() {
        if (profileEditFlag) {
            galleryButton.setVisibility(View.GONE);
            cameraButton.setVisibility(View.GONE);
            existingPhotoButton.setVisibility(View.GONE);
            removePhotoButton.setVisibility(View.GONE);
            profileEditFlag = false;
        } else {
            galleryButton.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.VISIBLE);
            existingPhotoButton.setVisibility(View.VISIBLE);
            removePhotoButton.setVisibility(View.VISIBLE);
            profileEditFlag = true;
        }
    }


    private void facebookshare() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
            boolean facebookAppFound = false;
            List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                    intent.setPackage(info.activityInfo.packageName);
                    facebookAppFound = true;
                    break;
                }
            }
            if (!facebookAppFound) {
              //  String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + getResources().getString(R.string.share_msg);
               // intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                Intent intentFB = new Intent(Intent.ACTION_SEND);
                String sharerUrl = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en" + getResources().getString(R.string.share_msg);
                intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                startActivity(intentFB);
            }else {
                startActivity(intent);
            }
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String sharerUrl = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en" + getResources().getString(R.string.share_msg);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }


    private void tweeterShare() {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        tweetIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
        tweetIntent.setType("text/plain");
        PackageManager packManager = getActivity().getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void googlePlusShare() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(getResources().getString(R.string.share_msg))
                .setStream(null)
                .getIntent()
                .setPackage("com.google.android.apps.plus");
        startActivity(shareIntent);
    }


    private void linkedinShare() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
            List<ResolveInfo> matches = getActivity().getPackageManager()
                    .queryIntentActivities(shareIntent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith(
                        "com.linkedin")) {
                    shareIntent.setPackage(info.activityInfo.packageName);
                    break;
                }
            }

            startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void mailShare() {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void msgShare() {
        String urlToShare = getResources().getString(R.string.share_msg);
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", urlToShare);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moreShare() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
               // imageFromCamera();
                cameraTakePic();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA}, GlobalState.CAMERA_REQUEST);
            }
        } else {
           // imageFromCamera();
            cameraTakePic();
        }
    }
    private void cameraTakePic() {
        CamClass camClass = new CamClass(getActivity(), REQUEST_CODE_TAKE_PICTURE, "Melodygram");
        imageUriBg = camClass.captureImage();
    }


    private void beginCropFromCameraNew(Uri source, Intent result) {

        String tempPath;
        if (source != null && source.getPath() != null) {
            tempPath = source.getPath();
        } else {
            tempPath = getRealPathFromURI(result.getData());
        }

        try {
            ExifInterface exif = new ExifInterface(tempPath);
            orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .start(activity);
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                imageFromGallery();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GlobalState.READ_EXTERNAL_STORAGE);
            }
        } else {
            imageFromGallery();
        }

    }


    private void imageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GlobalState.IMAGE_FROM_GALLERY);
    }

    private void imageFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    private void pickExistingImage() {

        initExistingImageDialog();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//
//            System.out.println("@@@ above  marshmallow  version");
//
//            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                requestPermissions(
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GlobalState.READ_EXTERNAL_STORAGE_FOLDER);
//            }
//        } else {
//
//            System.out.println("@@@ bloe masrhmallow");
//            initExistingImageDialog();
//        }
    }

    private void initExistingImageDialog() {
        Intent intent = new Intent(getActivity(), ChooseFolderActivity.class);
        startActivityForResult(intent, GlobalState.EXISTING_FOLDER);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GlobalState.CAMERA_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromCamera();
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;
            case GlobalState.READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromGallery();
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;

            case GlobalState.READ_EXTERNAL_STORAGE_FOLDER:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initExistingImageDialog();
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if (resultCode == AppCompatActivity.RESULT_OK) {
            System.out.println("resultCode : "+resultCode+",requestCode : "+requestCode);
            Log.d("result code", String.valueOf(resultCode));
            Log.d("request code", String.valueOf(requestCode));
            Log.d("result code",String.valueOf(REQUEST_CODE_TAKE_PICTURE));
            switch (requestCode) {
                case GlobalState.IMAGE_FROM_GALLERY:
                    imageUri = data.getData();
                    if (imageUri != null) {
                        String imagePath = CommonUtil.compressProfileImage(CommonUtil.getRealPathFromURI(imageUri, getActivity()), "midQuality");
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//                        if (bitmap != null) {
//                            profileImage.setImageBitmap(bitmap);
//                        }

                        beginCropFromGallery(data.getData(), data);
                       // updateProfilePic(imagePath);
                        break;
                    }
                case GlobalState.CAMERA_REQUEST:
                    String cameraPic = cameraFile.getAbsolutePath();
                  //  AppController.getInstance().displayFileImage(profileImage, cameraPic, null);
                     beginCropFromCameraNew(imageUriBg, data);
                   // updateProfilePic(cameraPic);
                    break;
                case GlobalState.EXISTING_FOLDER:
                    String path = data.getExtras().getString("path");
                    AppController.getInstance().displayFileImage(profileImage, path, null);
                    updateProfilePic(path);
                    break;
                case GlobalState.WRITE_PERMISSIONS:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraFile = getCaptureImagePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                  //  handleCrop(resultUri);
                    handleCrop(resultCode,resultUri);
                    break;
                case  REQUEST_CODE_TAKE_PICTURE:

                    beginCropFromCamera(imageUriBg, data);
                    break;
                }
            }

    }

    private void updateProfilePic(String path) {
        new UpdatePic().execute(path);
    }



    private void handleCrop(int resultCode, Uri result) {
        if (resultCode == getActivity().RESULT_OK) {
            String path = getRealPathFromURI(result);
            String comPath;
            Bitmap bitmap;
            //comPath = ReusableLogic.compressImage(path);


            comPath = new ImageCompressUtils(activity).compressImage(path);

            File file = new File(path);
            long length = file.length() / 1024;
            if (length > 1000) {
                comPath = new ImageCompressUtils(activity).compressImage(path);
            }

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inScaled = false;
            bitmap = BitmapFactory.decodeFile(comPath, bitmapOptions);
            if (bitmap != null) {
                try {
                    if (orientation.equalsIgnoreCase("6")) {
                        bitmap = rotate(bitmap, 90);
                    } else if (orientation.equalsIgnoreCase("8")) {
                        bitmap = rotate(bitmap, 270);
                    } else if (orientation.equalsIgnoreCase("3")) {
                        bitmap = rotate(bitmap, 180);
                    }

                    profileImage.setImageBitmap(bitmap);
                    profileImage.requestFocus();

//                    if (arrayList_imagesPath.size() < 1) {
//                        arrayList_imagesPath.add(comPath);
//
//                    } else {
//
//                    }

                  updateProfilePic(comPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI,
                null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void handleCrop(Uri result) {
        picUri = result;
        String path = CommonUtil.getRealPathFromURI(result, activity);
        String comPath;
        Bitmap bitmap;
        comPath = new ImageCompressUtils(activity).compressImage(path);
        updateProfilePic(comPath);
        File file = new File(comPath);
        long length = file.length() / 1024;
        if (length > 1000) {
            comPath = new ImageCompressUtils(activity).compressImage(comPath);
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bitmap = BitmapFactory.decodeFile(comPath, bitmapOptions);

        Log.v("ON_SETTING", "START");
        if (bitmap != null) {
            try {
                if (orientation.equalsIgnoreCase("6")) {
                    bitmap = rotate(bitmap, 0);
                } else if (orientation.equalsIgnoreCase("8")) {
                    bitmap = rotate(bitmap, 270);
                } else if (orientation.equalsIgnoreCase("3")) {
                    bitmap = rotate(bitmap, 180);
                }

                Log.v("ON_SETTING", "DOING");
                imageOrientation = orientation;

                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                Log.v("ON_SETTING", "DONE");

                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
    private void beginCropFromGallery(final Uri source, final Intent result) {

        String path = CommonUtil.getRealPathFromURI(result.getData(), activity);

        if (path == null) {
            path = source.getPath();
        }
        try {
            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(5, 5)
                .setAutoZoomEnabled(true)
                .setMaxZoom(4)
                .setFixAspectRatio(true)
                .getIntent(activity);
        startActivityForResult(intent,
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    private void beginCropFromCamera(final Uri source, Intent result) {
        String tempPath;
        if (source != null && source.getPath() != null) {
            tempPath = source.getPath();
        } else {
            tempPath = getRealPathFromURI(result.getData());
        }

        try {
            ExifInterface exif = new ExifInterface(tempPath);
            orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(5, 5)
                .setAutoZoomEnabled(true)
                .setMaxZoom(4)
                .setFixAspectRatio(true)
                .getIntent(activity);

        startActivityForResult(intent,
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }
    public void updateStatusUserName() {
        if (isStatusNameChanges) {
            JSONObject objJson = new JSONObject();
            try {
                objJson.accumulate("profile_name", userNameText.getText().toString());
                objJson.accumulate("status", statusText.getText().toString());
                objJson.accumulate("user_id", appController.getUserId());
                objJson.accumulate("profilepic", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("Login REQUEST", objJson.toString());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    APIConstant.UPDATE_PROFILE, objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("Login response", response.toString());
                            if (response != null) {
                                parseServerResponse(response);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(activity, getString(R.string.pic_upload_failed), Toast.LENGTH_SHORT).show();

                    }
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            isStatusNameChanges = false;
        }
    }


    public void removeUserPic() {
        JSONObject objJson = new JSONObject();
        try {
            objJson.accumulate("user_id", appController.getUserId());
            objJson.accumulate("pic", "uploads/no_image_found.png");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog = appController.getLoaderDialog(activity);
        dialog.show();
        Log.v("REQUEST", objJson.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIConstant.UPDATE_PROFILE, objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Log.v("response", response.toString());
                        if (response != null) {
                            try {
                                if (response != null) {
                                    SharedPreferences sharedPreferences = AppController.getInstance().getPrefs();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    if (response.getString("status").equalsIgnoreCase("success")) {
                                        JSONObject resultObj = response.getJSONObject("Result");
                                        String pic = resultObj.getString("pic");
                                        if (pic.length() > 0)
                                            appController.displayUrlImage(profileImage, pic, null);
                                        SharedPreferenceDB.defaultInstance().saveAppProfilePic(activity, resultObj.getString("pic"));
                                        appController.getPrefs().edit().putString("pic", pic).commit();
                                        editor.commit();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (error instanceof NoConnectionError) {

                    Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void parseServerResponse(JSONObject response) {
        try {
            if (response != null) {
                SharedPreferences sharedPreferences = AppController.getInstance().getPrefs();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (response.getString("status").equalsIgnoreCase("success")) {
                    JSONObject resultObj = response.getJSONObject("Result");
                    editor.putString("profile_name", resultObj.getString("profile_name"));
                    editor.putString("status", resultObj.getString("status"));
                    editor.commit();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class UpdatePic extends AsyncTask<String, Void, String> {
        Dialog dialog;
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = appController.getLoaderDialog(activity);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                RequestBody requestBody;
                MultipartBody.Builder body;
                okhttp3.Request request;
                body = new MultipartBody.Builder();
                body.setType(MultipartBody.FORM);
                body.addFormDataPart("user_id", appController.getUserId());
                body.addFormDataPart("pic", "pic", RequestBody.create(MEDIA_TYPE_PNG, new File(params[0])));
                requestBody = body.build();
                request = new okhttp3.Request.Builder()
                        .url(APIConstant.UPDATE_PROFILE_PIC)
                        .post(requestBody)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                if (result != null) {
                    JSONObject json = new JSONObject(result);
                    if (json != null && json.optString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(activity, getString(R.string.profile_pic_updated), Toast.LENGTH_SHORT).show();
                        JSONObject resultObj = json.getJSONObject("Result");
                        System.out.println("@@@ profile pic:"+resultObj.getString("pic"));

                        SharedPreferenceDB.defaultInstance().saveAppProfilePic(activity, resultObj.getString("pic"));
                        appController.getPrefs().edit().putString("pic", resultObj.getString("pic")).commit();
                    } else {
                        Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

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

    public void showDialog() {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View dialogView = factory.inflate(
                R.layout.activity_status, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(dialogView);

        ArrayAdapter adapter = new ArrayAdapter<String>(activity, R.layout.item_status_layout, getResources().getStringArray(R.array.status_array));
        ListView listView = (ListView) dialogView.findViewById(R.id.listViewId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedStatus = parent.getItemAtPosition(position).toString();
                statusText.setText(selectedStatus);
                dialog.dismiss();
            }
        });
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE); // for activity use context instead of getActivity()
        Display display = wm.getDefaultDisplay(); // getting the screen size of device
        Point size = new Point();
        display.getSize(size);
        int width = size.x - 100;  // Set your heights
        int height = size.y - 400; // set your widths

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.width = width;
        lp.height = height;

        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
