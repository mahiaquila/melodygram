package com.melodygram.textview;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
/**
 * Created by LALIT on 15-06-2016.
 */

public class FontCache {

	private static HashMap<String, Typeface> fontCache = new HashMap<String, Typeface>();

	public static Typeface getTypeface(String fontname, Context context) {
		Typeface typeface = fontCache.get(fontname);

		if (typeface == null) {
			try {
				typeface = Typeface.createFromAsset(context.getAssets(),
						fontname);
			} catch (Exception e) {
				return null;
			}

			fontCache.put(fontname, typeface);
		}

		return typeface;
	}

}