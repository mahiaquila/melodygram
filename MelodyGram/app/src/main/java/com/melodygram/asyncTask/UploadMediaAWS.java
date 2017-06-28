package com.melodygram.asyncTask;

import java.io.File;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.melodygram.adapter.ChatMessageAdapter;
import com.melodygram.chatinterface.InterfaceUpdateProgress;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;

/**
 * Created by LALIT on 29-06-2016.
 */
public class UploadMediaAWS extends AsyncTask<String, String, String> {

	File uploadFileAWS;
	String extention, msgId, type;
	boolean uploadSuccessFlag = false;
	ChatMessageAdapter.ViewHolder viewHolderItem;
	InterfaceAsyncResponse interfaceUploadResponse;
	InterfaceUpdateProgress interfaceUpdateProgress;

	public UploadMediaAWS(File uploadFileAWS,
			String extention, String msgId,
			String type, InterfaceAsyncResponse interfaceUploadResponse,InterfaceUpdateProgress interfaceUpdateProgress) {
		this.uploadFileAWS = uploadFileAWS;
		this.extention = extention;
		this.interfaceUploadResponse = interfaceUploadResponse;
		this.interfaceUpdateProgress=interfaceUpdateProgress;
		this.msgId = msgId;
		this.type = type;
	}
	public interface InterfaceAsyncResponse {
		void asyncFinished(String response);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected String doInBackground(String... param) {
		AWSCredentials credentialsAWS = new BasicAWSCredentials(
				ChatGlobalStates.myAWSUserName, ChatGlobalStates.myAWSKey);
		try {
			TransferManager tx = new TransferManager(credentialsAWS);
			Upload myUpload = tx.upload(ChatGlobalStates.bucketName,
					uploadFileAWS.getName(), uploadFileAWS);
			myUpload.getProgress();
			while (myUpload.isDone() == false) {
				updateProgress(""
						+ myUpload.getProgress().getPercentTransferred());
			}
			if (myUpload.isDone() == true) {
				ChatGlobalStates.mediaPathSelected.put(uploadFileAWS.getName(),
						uploadFileAWS.getAbsolutePath());
				uploadSuccessFlag = true;
			}
			tx.shutdownNow();
		} catch (AmazonServiceException e) {
			uploadSuccessFlag = false;
		} catch (AmazonClientException e) {
			uploadSuccessFlag = false;
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return String.valueOf(uploadSuccessFlag);
	}

	private void updateProgress(String downloadPercent) {

		if (downloadPercent.contains(".")) {
			if (ChatGlobalStates.mediasUploadOnProcess.containsKey(msgId)) {
				viewHolderItem = ChatGlobalStates.mediasUploadOnProcess
						.get(msgId);
			}
			publishProgress(downloadPercent.substring(0,
					downloadPercent.indexOf(".")));
		} else {
			if (ChatGlobalStates.mediasUploadOnProcess.containsKey(msgId)) {
				viewHolderItem = ChatGlobalStates.mediasUploadOnProcess
						.get(msgId);
			}
			publishProgress(downloadPercent);
		}
	}

	@Override
	protected void onProgressUpdate(String... mediaProgress) {
		super.onProgressUpdate(mediaProgress);

		 if (type.equalsIgnoreCase(ChatGlobalStates.FILE_TYPE)) {
			if (viewHolderItem !=null && viewHolderItem.appUserFilePerText != null) {
				viewHolderItem.appUserFileProgress.setProgress(Integer
						.valueOf(mediaProgress[0]));
				viewHolderItem.appUserFilePerText.setText(mediaProgress[0] + "%");
			}
		} else if (type.equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE) ||type.equalsIgnoreCase(ChatGlobalStates.MUSIC_TYPE)) {
			 interfaceUpdateProgress.updateProgressValue(mediaProgress[0]);
			if (viewHolderItem !=null && viewHolderItem.appUserProgressbarText != null) {

				viewHolderItem.appUserProgressbarText
						.setText(mediaProgress[0] + "%");
			}
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (interfaceUploadResponse != null) {
			interfaceUploadResponse.asyncFinished(result);
		}
	}
}
