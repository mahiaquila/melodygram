package com.melodygram.activity;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProfileActivity extends Activity implements View.OnClickListener {
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

    ImageView profile_image_edit;
    private File cameraFile;
    RelativeLayout  header_back_button_parent;
    ImageView musicDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile);
        activity = this;
        initialization();
    }

    private void initialization() {

        appController = AppController.getInstance();
        header_back_button_parent=(RelativeLayout)findViewById(R.id.header_back_button_parent);
        header_back_button_parent.setOnClickListener(this);
        musicDownload = (ImageView) findViewById(R.id.settings_button_image);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        profileImage.setOnClickListener(this);
        facebookButton = (ImageView) findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(this);
        tweeterButton = (ImageView) findViewById(R.id.tweeter_button);
        tweeterButton.setOnClickListener(this);
        linkedinButton = (ImageView)findViewById(R.id.more_button);
        linkedinButton.setOnClickListener(this);
        googleButton = (ImageView) findViewById(R.id.google_button);
        googleButton.setOnClickListener(this);
        emailButton = (ImageView) findViewById(R.id.mail_button);
        emailButton.setOnClickListener(this);
        msgButton = (ImageView) findViewById(R.id.msg_button);
        msgButton.setOnClickListener(this);
        inviteButton = (ImageView) findViewById(R.id.invite_button);
        inviteButton.setOnClickListener(this);
        galleryButton = (RelativeLayout) findViewById(R.id.gallery_icon_parent);
        galleryButton.setOnClickListener(this);
        cameraButton = (RelativeLayout) findViewById(R.id.take_photo_parent);
        cameraButton.setOnClickListener(this);
        existingPhotoButton = (RelativeLayout) findViewById(R.id.existing_photo_parent);
        existingPhotoButton.setOnClickListener(this);
        removePhotoButton = (RelativeLayout) findViewById(R.id.remove_photo_parent);
        removePhotoButton.setOnClickListener(this);
        userNameText = (EditText) findViewById(R.id.name_edit_text);
        statusText = (EditText) findViewById(R.id.status_edit_text);
        userNameEditButton = (ImageView) findViewById(R.id.user_name_edit);
        userNameEditButton.setOnClickListener(this);
        statusEditButton = (ImageView) findViewById(R.id.status_edit);
        statusEditButton.setOnClickListener(this);
        phoneNumber = (TextView) findViewById(R.id.phone_number);

        profile_image_edit=(ImageView)findViewById(R.id.profile_image_edit);
        profile_image_edit.setOnClickListener(this);
        //  view.findViewById(R.id.profile_image_edit).setOnClickListener(this);
         findViewById(R.id.options_parent).setOnClickListener(this);
         musicDownload.setVisibility(View.GONE);
        intitView();
    }


    private void intitView() {
        HashMap<String, String> map = appController.getUserDetails();
        profilePic = map.get("pic");
        String status = map.get("status");
        String profileName = map.get("profile_name");
        String phone = map.get("mobile");
        String code = map.get("country_code");

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
                    profile_image_edit.setVisibility(View.VISIBLE);
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
            //    removeUserPic();
                break;

            case R.id.take_photo_parent:
                profilePicVisiblity();
                openCamera();
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
            case R.id. header_back_button_parent:
                finish();
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
            profile_image_edit.setVisibility(View.VISIBLE);
            profileEditFlag = false;
        } else {
            galleryButton.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.VISIBLE);
            existingPhotoButton.setVisibility(View.VISIBLE);
            removePhotoButton.setVisibility(View.VISIBLE);
            profile_image_edit.setVisibility(View.GONE);
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
            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                    intent.setPackage(info.activityInfo.packageName);
                    facebookAppFound = true;
                    break;
                }
            }
            if (!facebookAppFound) {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + getResources().getString(R.string.share_msg);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            }
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String sharerUrl = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en" + getResources().getString(R.string.share_msg);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }


    private void tweeterShare() {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        tweetIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
        tweetIntent.setType("text/plain");
        PackageManager packManager = getPackageManager();
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
            Toast.makeText(activity, getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void googlePlusShare() {
        if(isGooglePlusInstalled()) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(getResources().getString(R.string.share_msg))
                    .setStream(null)
                    .getIntent()
                    .setPackage("com.google.android.apps.plus");
            startActivity(shareIntent);
        }else
        {
            Toast.makeText(activity, getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }

    }

    public static void filterByPackageName(Context context, Intent intent, String prefix) {
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(prefix)) {
                intent.setPackage(info.activityInfo.packageName);
                return;
            }
        }
    }
    private void linkedinShare() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
            List<ResolveInfo> matches = getPackageManager()
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
            Toast.makeText(activity, getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
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
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
//        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        sharingIntent.setType("text/html");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
//        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }


    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                imageFromCamera();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA}, GlobalState.CAMERA_REQUEST);
            }
        } else {
            imageFromCamera();
        }
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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


    private void imageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GlobalState.IMAGE_FROM_GALLERY);
    }

    private void imageFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraFile = getCaptureImagePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, GlobalState.WRITE_PERMISSIONS);
            }
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraFile = getCaptureImagePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
        }
    }

    private void pickExistingImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GlobalState.READ_EXTERNAL_STORAGE_FOLDER);
            }
        } else {
            initExistingImageDialog();
        }
    }


    private void initExistingImageDialog() {
        Intent intent = new Intent(activity, ChooseFolderActivity.class);
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
            switch (requestCode) {
                case GlobalState.IMAGE_FROM_GALLERY:
                    imageUri = data.getData();
                    if (imageUri != null) {
                        String imagePath = CommonUtil.compressProfileImage(CommonUtil.getRealPathFromURI(imageUri, activity), "midQuality");
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        if (bitmap != null) {
                            profileImage.setImageBitmap(bitmap);
                        }
                        updateProfilePic(imagePath);
                        break;
                    }
                case GlobalState.CAMERA_REQUEST:
                    String cameraPic = cameraFile.getAbsolutePath();
                    AppController.getInstance().displayFileImage(profileImage, cameraPic, null);
                    updateProfilePic(cameraPic);
                    break;
                case GlobalState.EXISTING_FOLDER:
                    String path = data.getExtras().getString("path");
                    AppController.getInstance().displayFileImage(profileImage, path, null);
                    updateProfilePic(path);
                    break;
                case GlobalState.WRITE_PERMISSIONS:
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraFile = getCaptureImagePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    startActivityForResult(intent, GlobalState.CAMERA_REQUEST);
                    break;
            }
        }
    }

    private void updateProfilePic(String path) {
        new UpdatePic().execute(path);
    }


    private void startCropImage() {

//        Intent intent = new Intent(getActivity(), CropImage.class);
//        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
//        intent.putExtra(CropImage.SCALE, true);
//
//        intent.putExtra(CropImage.ASPECT_X, 3);
//        intent.putExtra(CropImage.ASPECT_Y, 2);
//
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
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



    public boolean isGooglePlusInstalled()
    {
        try
        {
            getPackageManager().getApplicationInfo("com.google.android.apps.plus", 0);
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
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
    public void showDialog(){
        LayoutInflater factory = LayoutInflater.from(activity);
        final View dialogView = factory.inflate(
                R.layout.activity_status, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(dialogView);

        ArrayAdapter adapter = new ArrayAdapter<String>(activity, R.layout.item_status_layout, getResources().getStringArray(R.array.status_array));
        ListView listView=(ListView)dialogView.findViewById(R.id.listViewId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedStatus = parent.getItemAtPosition(position).toString();
                statusText.setText(selectedStatus);
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
