package com.melodygram.model;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * Created by LALIT on 15-06-2016.
 */
public class UploadDownloadViewsBean {
	String messageId;
	ImageView mediaDownloadCloud;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public ImageView getMediaDownloadCloud() {
		return mediaDownloadCloud;
	}
	public void setMediaDownloadCloud(ImageView mediaDownloadCloud) {
		this.mediaDownloadCloud = mediaDownloadCloud;
	}
	public RelativeLayout getViewsParrentLayout() {
		return viewsParrentLayout;
	}
	public void setViewsParrentLayout(RelativeLayout viewsParrentLayout) {
		this.viewsParrentLayout = viewsParrentLayout;
	}
	public ProgressBar getMediaProcessProgress() {
		return mediaProcessProgress;
	}
	public void setMediaProcessProgress(ProgressBar mediaProcessProgress) {
		this.mediaProcessProgress = mediaProcessProgress;
	}
	public TextView getMediaProcessPercentage() {
		return mediaProcessPercentage;
	}
	public void setMediaProcessPercentage(TextView mediaProcessPercentage) {
		this.mediaProcessPercentage = mediaProcessPercentage;
	}
	RelativeLayout viewsParrentLayout;
	ProgressBar mediaProcessProgress;
	TextView mediaProcessPercentage;
}
