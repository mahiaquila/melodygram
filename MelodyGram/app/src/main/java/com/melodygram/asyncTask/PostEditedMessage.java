package com.melodygram.asyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.http.HttpClient;
import com.amazonaws.http.HttpResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.melodygram.chatinterface.EditeMessageResponse;


public class PostEditedMessage
{
	private  static  PostEditedMessage instance=null;
	private Context context;


	PostEditedMessage(Context context)
	{
		this.context =context;
	}

	public static PostEditedMessage getInstance(Context context) {

		if(instance==null) {
			instance = new PostEditedMessage(context);
		}
		return instance;
	}

		public void editMessageUpdate(Context context,String messageID, String actualMessage, final EditeMessageResponse editeMessageResponse)

		{
			String url = "http://52.76.195.179/chat/editmessage";
			JSONObject obj = new JSONObject();
			try {

				obj.put("message_id", messageID);
				obj.put("message", actualMessage);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			RequestQueue queue = Volley.newRequestQueue(context);
			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, url, obj,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {

                               editeMessageResponse.editResponse(response.toString());

						}
					}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("Response exit", error.toString());
				}
			});
			queue.add(jsObjRequest);
			jsObjRequest.setRetryPolicy(new RetryPolicy() {
				@Override
				public int getCurrentTimeout() {
					return 5000;
				}

				@Override
				public int getCurrentRetryCount() {
					return 5000;
				}

				@Override
				public void retry(VolleyError error) throws VolleyError {

				}
			});

		}

}
