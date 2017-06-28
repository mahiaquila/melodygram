package com.melodygram.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.melodygram.R;
import com.melodygram.singleton.AppController;

import java.io.File;

/**
 * Created by LALIT on 28-06-2016.
 */
public class PicViewerActivity extends MelodyGramActivity implements View.OnClickListener {
    ImageView pic;

    String disAppearId;
    String  senderseenId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.image_viewer_layout);
        findViewById(R.id.back_button).setOnClickListener(this);
         pic = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();

        String serverUrl = intent.getStringExtra("imageURL");
        String localUrl = intent.getStringExtra("localPath");
        String pathUrl =intent.getStringExtra("bitMabImage");
         disAppearId =intent.getStringExtra("disappear");
         senderseenId = intent.getStringExtra("senderSeen");



             if(pathUrl!=null)
             {
                 Bitmap bitmap = BitmapFactory.decodeFile(pathUrl);
                 if (bitmap != null) {
                     pic.setImageBitmap(bitmap);
                 }
             }else {
                 if (localUrl != null) {
                     File file = new File(localUrl);
                     if (file.isFile()) {
                         AppController.getInstance().displayFileImage(pic, localUrl, null);
                     } else {
                         AppController.getInstance().displayUrlImage(pic, serverUrl, null);
                     }
                 } else {
                     AppController.getInstance().displayUrlImage(pic, serverUrl, null);
                 }
             }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
     //   registerReceiver();
        try
        {
            if(disAppearId!=null && !disAppearId.equalsIgnoreCase("-1")   ) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        pic.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }




}
