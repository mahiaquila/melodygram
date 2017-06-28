package com.melodygram.singleton;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.melodygram.R;
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.GlobalState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.HashMap;

/**
 * Created by LALIT on 14-06-2016.
 */
public class AppController extends Application {

    private SharedPreferences sharedPreferences;
    private static AppController appController;
    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String userId, userProfilePic;
    private Tracker mTracker ;
    private GoogleAnalytics analytics;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        appController = this;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        analytics = GoogleAnalytics.getInstance(this);

      //  mTracker = analytics.newTracker("UA-76256908-3");

        mTracker = analytics.newTracker("UA-93003247-1");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.enableAutoActivityTracking(true);
    }

    public static synchronized AppController getInstance()
    {
        return appController;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker()
    {
        if (mTracker == null) {
            analytics = GoogleAnalytics.getInstance(this);
          //  mTracker = analytics.newTracker(R.xml.global_tracke);
            mTracker = analytics.newTracker("UA-93003247-1");
            mTracker.enableExceptionReporting(true);
            mTracker.enableAdvertisingIdCollection(true);
            mTracker.enableAutoActivityTracking(true);
        }
        return mTracker;
    }

    public SharedPreferences getPrefs()
    {
        if (sharedPreferences == null)
        {
            sharedPreferences = getSharedPreferences(GlobalState.SHARED_PREF,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public String getUserId()
    {
        if (userId == null)
        {
            SharedPreferences sharedPreferences = getPrefs();
            userId = sharedPreferences.getString("user_id", "");
        }
        return userId;
    }



    public String getUserProfiePic()
    {
        if (userProfilePic == null)
        {
            SharedPreferences sharedPreferences = getPrefs();
            userProfilePic = sharedPreferences.getString("pic", "");
        }
        return userProfilePic;
    }

    public boolean isDisappearActivated()
    {
        SharedPreferences sharedPreferences = getPrefs();
        return sharedPreferences.getBoolean("diappear", false);
    }
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> userDetails = new HashMap<String, String>();
        SharedPreferences sharedPreferences = getPrefs();
        userDetails.put("user_id", sharedPreferences.getString("id", ""));
        userDetails.put("firstName",
                sharedPreferences.getString("firstName", ""));
        userDetails
                .put("lastName", sharedPreferences.getString("lastName", ""));
        userDetails
                .put("username", sharedPreferences.getString("username", ""));
        userDetails
                .put("username", sharedPreferences.getString("username", ""));
        userDetails
                .put("pic", sharedPreferences.getString("pic", ""));
        userDetails
                .put("status", sharedPreferences.getString("status", ""));
        userDetails
                .put("mobile", sharedPreferences.getString("mobile", ""));
        userDetails
                .put("country_code", sharedPreferences.getString("country_code", ""));
        userDetails.put("profile_name", sharedPreferences.getString("profile_name", ""));
        return userDetails;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    public void displayUrlImage(ImageView image, String path, final ProgressBar progressBar)
    {
        if (imageLoader == null)
        {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        imageLoader
                .displayImage(APIConstant.SERVER_PATH + path, image, options, new ImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view)
                    {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                    {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view)
                    {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener()
                {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total)
                    {

                    }
                });
    }

    public void displayGoogleUrlImage(ImageView image, String path, final ProgressBar progressBar)
    {
        if (imageLoader == null)
        {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        imageLoader
                .displayImage(path, image, options, new ImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
    }

    public void displayFileImage(final ImageView image, String path, final ProgressBar progressBar)
    {
        if (imageLoader == null)
        {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        imageLoader
                .displayImage("file://" + path, image, options, new ImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view)
                    {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener()
                {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total)
                    {

                    }
                });


    }

    public void displayDrawableImage(final ImageView image, String path)
    {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        imageLoader
                .displayImage("drawable//" + path, image, options, null);


    }

    public Dialog getLoaderDialog(Activity activity)
    {
        Dialog progressDialog = new Dialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.network_loader);
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}
