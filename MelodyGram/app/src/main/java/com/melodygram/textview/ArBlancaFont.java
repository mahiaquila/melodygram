package com.melodygram.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by LALIT on 15-06-2016.
 */

public class ArBlancaFont extends TextView {

	public ArBlancaFont(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 
	}
	public ArBlancaFont(Context context, AttributeSet attrs) {
		super(context, attrs);

		applyCustomFont(context);
	}

	public ArBlancaFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		applyCustomFont(context);
	}

	private void applyCustomFont(Context context) {
		Typeface customFont = FontCache.getTypeface(
				"ARBLANCA.ttf", context);
		setTypeface(customFont);
	}

}
