package com.melodygram.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.utils.CommonUtil;
import com.melodygram.utils.SharedPreferenceDB;

/**
 * Created by FuGenX-01 on 31-05-2017.
 */

public class MediaAutoDownload extends Activity implements View.OnClickListener
{

    RelativeLayout rlayoutUsingMobileData;
    RelativeLayout rlayoutConnectedWifi;
    private  Activity mActivity;
    Dialog mediaAutoDownlaod;
    private RadioButton chkPhotoSelected;
    private RadioButton chkAudiSelected;
    private ImageView ivBack;
    private  boolean isCheckMobileData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_auto_download);
        mActivity = MediaAutoDownload.this;
        viewInit();
    }
    private void viewInit()
    {
        rlayoutUsingMobileData =(RelativeLayout)findViewById(R.id.rlayout_using_mobile_data);
        rlayoutConnectedWifi=(RelativeLayout)findViewById(R.id.rlayout_connected_on_wifi);
        ivBack =(ImageView)findViewById(R.id.iv_back);
        rlayoutUsingMobileData.setOnClickListener(this);
        rlayoutConnectedWifi.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rlayout_connected_on_wifi:
                isCheckMobileData =false;
                showOptionType("When connected on Wi-Fi",isCheckMobileData);
                break;
            case R.id.rlayout_using_mobile_data:
                isCheckMobileData =true;
                showOptionType("When using mobile data",isCheckMobileData);
                break;
            case R.id.check_box_photo:
                   if(isCheckMobileData)
                   {
                        if(SharedPreferenceDB.defaultInstance().getPhotoAutoDownloadMobileData(mActivity))
                        {
                            chkPhotoSelected.setChecked(false);
                            SharedPreferenceDB.defaultInstance().savePhotoAutoDoawnloadMobileData(mActivity,false);
                        }else
                        {
                            chkPhotoSelected.setChecked(true);
                            SharedPreferenceDB.defaultInstance().savePhotoAutoDoawnloadMobileData(mActivity,true);
                        }

                   }else
                   {
                        if(SharedPreferenceDB.defaultInstance().getPhotoAutoDownloadWifi(mActivity))
                        {
                            chkPhotoSelected.setChecked(false);
                            SharedPreferenceDB.defaultInstance().savePhotoAutoDoawnloadWifi(mActivity,false);
                        }else
                        {
                            chkPhotoSelected.setChecked(true);
                            SharedPreferenceDB.defaultInstance().savePhotoAutoDoawnloadWifi(mActivity,true);
                        }

                   }

                break;
            case R.id.check_box_audio:
                  if(isCheckMobileData)
                  {

                       if(SharedPreferenceDB.defaultInstance().getAudioAutoMobileDataDownload(mActivity))
                       {
                           chkAudiSelected.setChecked(false);
                           SharedPreferenceDB.defaultInstance().saveAudioAutoMobileDataDownload(mActivity,false);
                       }else
                       {
                           chkAudiSelected.setChecked(true);
                           SharedPreferenceDB.defaultInstance().saveAudioAutoMobileDataDownload(mActivity,true);
                       }

                  }else
                  {
                       if(SharedPreferenceDB.defaultInstance().getAudioAutoWifiDownload(mActivity))
                       {
                           chkAudiSelected.setChecked(false);
                           SharedPreferenceDB.defaultInstance().saveAudioAutoWifiDownload(mActivity,false);
                       }else
                       {
                           chkAudiSelected.setChecked(true);
                           SharedPreferenceDB.defaultInstance().saveAudioAutoWifiDownload(mActivity,true);
                       }

                  }

                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showOptionType(String title,boolean isFromMobileData)
    {
                mediaAutoDownlaod = new Dialog(mActivity);
                mediaAutoDownlaod.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mediaAutoDownlaod.setContentView(R.layout.media_auto_download_popup_layout);
                TextView tvHeaderName =(TextView)mediaAutoDownlaod.findViewById(R.id.header_name);
                tvHeaderName.setText(title);
                chkPhotoSelected=(RadioButton)mediaAutoDownlaod.findViewById(R.id.check_box_photo);
                chkAudiSelected=(RadioButton)mediaAutoDownlaod.findViewById(R.id.check_box_audio);

                 if(isFromMobileData)
                 {
                     chkPhotoSelected.setChecked(SharedPreferenceDB.defaultInstance().getPhotoAutoDownloadMobileData(mActivity));
                     chkAudiSelected.setChecked(SharedPreferenceDB.defaultInstance().getAudioAutoMobileDataDownload(mActivity));
                 }else
                 {
                     chkPhotoSelected.setChecked(SharedPreferenceDB.defaultInstance().getPhotoAutoDownloadWifi(mActivity));
                     chkAudiSelected.setChecked(SharedPreferenceDB.defaultInstance().getAudioAutoWifiDownload(mActivity));

                 }
                chkAudiSelected.setOnClickListener(this);
                chkPhotoSelected.setOnClickListener(this);
                mediaAutoDownlaod.show();
    }
}
