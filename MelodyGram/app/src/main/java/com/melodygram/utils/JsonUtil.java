package com.melodygram.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.melodygram.model.CountryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by LALIT on 15-06-2016.
 */
public class JsonUtil
{

	public static ArrayList<CountryModel> countryList = new ArrayList<CountryModel>();
	public static String AssetCountryJSONFile(String filename, Context context) throws IOException
	{
		AssetManager manager = context.getAssets();
		InputStream file = manager.open(filename);
		byte[] formArray = new byte[file.available()];
		file.read(formArray);
		file.close();

		return new String(formArray);
	}

	public static void initializeCountryArrayList(Activity moreChatActivity)
	{
		try
		{
			String jsonLocation = AssetCountryJSONFile("countries.json", moreChatActivity);
			JSONObject jsonobject = new JSONObject(jsonLocation);
			JSONArray jarray = (JSONArray) jsonobject.getJSONArray("items");
			for (int i = 0; i < jarray.length(); i++)
			{
				JSONObject jb = (JSONObject) jarray.get(i);
				String country = jb.getString("country");
				String code = jb.getString("code");

				CountryModel countryItemBean = new CountryModel();
				countryItemBean.setCountryName(country);
				countryItemBean.setCountryCode(code);
				countryList.add(countryItemBean);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}


}
