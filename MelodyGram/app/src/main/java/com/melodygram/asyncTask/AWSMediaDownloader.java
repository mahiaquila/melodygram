package com.melodygram.asyncTask;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.melodygram.adapter.ChatMessageAdapter;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.ChatMessageDataSource;
/**
 * Created by LALIT on 29-06-2016.
 */
public class AWSMediaDownloader extends AsyncTask<String, String, String> {

    Context mediaActivity;
    File mediaStorageDir, mediaFile;
    String downloadedPath;
    AWSCredentials credentialsAWS;
    String mediaStoragePath, msgId, type;
    ChatMessageAdapter.ViewHolder viewHolder;

    public AWSMediaDownloader(String mediaStoragePath, Context mediaActivity, String msgId, String type) {
        this.mediaActivity = mediaActivity;
        this.mediaStoragePath = mediaStoragePath;
        this.msgId = msgId;
        this.type = type;
        credentialsAWS = new BasicAWSCredentials(ChatGlobalStates.myAWSUserName, ChatGlobalStates.myAWSKey);
    }

    public interface InterfaceAsyncResponse {
        void asyncFinished(String response, int position);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... mediaURL) {
        try {
            // Set the path where we want to save the file in this case, going to save it on the root directory of the sd card & Create a new file, specifying the path, and the filename which we want to save the file as!
            mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data/" + mediaActivity.getPackageName());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mediaURL[0]);
            downloadedPath = mediaFile.getAbsolutePath();
            TransferManager tx = new TransferManager(credentialsAWS);
            Download download = tx.download(ChatGlobalStates.bucketName, mediaURL[0], mediaFile);
            double localPercentageValue = 0;
            double tempPercentageValue = -1;

            while (download.isDone() == false) {
                tempPercentageValue = download.getProgress().getPercentTransferred();
                if (tempPercentageValue != localPercentageValue)
                    updateProgress(""
                            + tempPercentageValue);
                localPercentageValue = tempPercentageValue;
            }
            tx.shutdownNow();
        } catch (AmazonServiceException e) {
            downloadedPath = mediaURL[0];
            e.printStackTrace();
        } catch (AmazonClientException e) {
            downloadedPath = mediaURL[0];
            e.printStackTrace();
        } catch (Exception e) {
            downloadedPath = mediaURL[0];
            e.printStackTrace();
        }
        return downloadedPath;
    }

    private void updateProgress(String downloadPercent) {

        if (downloadPercent.contains(".")) {
            if (ChatGlobalStates.mediasUploadOnProcess.containsKey(msgId)) {
                viewHolder = ChatGlobalStates.mediasUploadOnProcess
                        .get(msgId);
            }
            publishProgress(downloadPercent.substring(0,
                    downloadPercent.indexOf(".")));
        } else {
            if (ChatGlobalStates.mediasUploadOnProcess.containsKey(msgId)) {
                viewHolder = ChatGlobalStates.mediasUploadOnProcess
                        .get(msgId);
            }
            publishProgress(downloadPercent);
        }
    }

    @Override
    protected void onProgressUpdate(String... mediaProgress) {
        super.onProgressUpdate(mediaProgress);

            if (type.equalsIgnoreCase(ChatGlobalStates.FILE_TYPE)) {
                if (viewHolder.appUserFilePerText != null) {
                    viewHolder.appUserFileProgress.setProgress(Integer
                            .valueOf(mediaProgress[0]));
                    viewHolder.appUserFilePerText.setText(mediaProgress[0] + "%");
                }
            } else if (type.equalsIgnoreCase(ChatGlobalStates.AUDIO_TYPE)) {
                if (viewHolder.appUserProgressbarText != null) {
                    viewHolder.appUserProgressbarText
                            .setText(mediaProgress[0] + "%");
                }
            }

    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        if (result != null) {
            ChatMessageDataSource chatMessageDataSource = new ChatMessageDataSource(mediaActivity);
            chatMessageDataSource.open();
            chatMessageDataSource.updateMediaUrl(
                    msgId,
                    result);
            chatMessageDataSource.close();
        }
        if (ChatGlobalStates.mediasUploadOnProcess.containsKey(msgId))
        {
            viewHolder = ChatGlobalStates.mediasUploadOnProcess
                    .get(msgId);
            viewHolder.chatMessageAdapter.asyncFinished(result, viewHolder.position);
        }

    }
}
