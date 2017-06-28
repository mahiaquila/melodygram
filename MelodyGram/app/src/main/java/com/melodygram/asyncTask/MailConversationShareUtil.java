package com.melodygram.asyncTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.database.ChatMessageDataSource;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.singleton.AppController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
/**
 * Created by LALIT on 29-06-2016.
 */
public class MailConversationShareUtil extends AsyncTask<Void, Void, Void> {
	private Dialog progressDialog = null;
	List<ChatMessageModel> chatingListItemBeanList;
	String conversationString = "";
	Activity activity;
	public MailConversationShareUtil(Activity activity) {
		this.activity =activity;
		fileUtilities = new FileUtilities(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = AppController.getInstance().getLoaderDialog(activity);
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		String chatMessageSenderName, chatMessage, chatMessageUserTime;
		try {
			ChatMessageDataSource moreChatMessageDatasource = new ChatMessageDataSource(
					activity);
			moreChatMessageDatasource.open();
			chatingListItemBeanList = moreChatMessageDatasource
					.getAllChatTextMessage(
							ChatGlobalStates.MESSAGE_TYPE);
			moreChatMessageDatasource.close();
			for (int j = 0; j < chatingListItemBeanList.size(); j++) {
				chatMessageSenderName =chatingListItemBeanList.get(j)
						.getSenderName();
				chatMessage = chatingListItemBeanList.get(j)
						.getActualMsg();
				chatMessageUserTime = chatingListItemBeanList.get(j)
						.getMsgTime();
				conversationString = conversationString + chatMessageSenderName
						+ "(" + chatMessageUserTime + "):" + chatMessage + "\n";
			}
			fileUtilities
					.write("meloygram_conversation.txt", conversationString);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (conversationString != null && conversationString.length() > 0) {
			if (outputFile != null) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
				emailIntent.setType("text/plain");
				emailIntent.putExtra(Intent.EXTRA_SUBJECT,
						"Melodygram chat Conversation");
				emailIntent.putExtra(Intent.EXTRA_TEXT,
						"Please find the attached chat conversation");
				emailIntent.putExtra(Intent.EXTRA_STREAM,
						Uri.parse("file://" + outputFile.getAbsolutePath()));
				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(emailIntent);
			}

		} else {
			Toast.makeText(activity, "No Conversatition Available",
					Toast.LENGTH_SHORT).show();
		}
		try {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

		} catch (Exception e) {

		}

	}
	FileUtilities fileUtilities;
	File outputFile;

	public class FileUtilities {
		private Writer writer;
		private String absolutePath;
		private final Context context;

		public FileUtilities(Context context) {
			super();
			this.context = context;
		}
		public void write(String fileName, String data) {
			File root = Environment.getExternalStorageDirectory();
			File outDir = new File(root.getAbsolutePath() + File.separator
					+ "Melodygram_Folder");
			if (!outDir.isDirectory()) {
				outDir.mkdir();
			}
			try {
				if (!outDir.isDirectory()) {
					throw new IOException(
							"Unable to create directory MoreChat_Folder. Maybe the SD card is mounted?");
				}
				outputFile = new File(outDir, fileName);
				writer = new BufferedWriter(new FileWriter(outputFile));
				writer.write(data);
				writer.close();
			
			} catch (IOException e) {

				Toast.makeText(
						context,
						e.getMessage()
								+ " Unable to write to external storage.",
						Toast.LENGTH_LONG).show();
			}

		}

		public Writer getWriter() {
			return writer;
		}
		public String getAbsolutePath() {
			return absolutePath;
		}
	}
}
