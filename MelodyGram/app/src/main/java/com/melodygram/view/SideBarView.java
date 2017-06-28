package com.melodygram.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.melodygram.R;
/**
 * Created by LALIT on 15-06-2016.
 */
public class SideBarView extends View {
	private ListView list;
	private SectionIndexer sectionIndexter;
	private TextView mDialogText;
	@SuppressWarnings("unused")
	private Context context;
	private String[] sections;	
	private String selectedLetter;

	public static final char[] mLetter = 
    new char[] {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

	/*public static final char[] mLetterRtl = 
    new char[] {'ي','و','ه','ن','م','ل','ك','ق','ف','غ','ع','ظ','ط','ض','ص','ش','س','ز','ر','ذ','د','خ','ح','ج','ث','ت','ب','ا'};*/
	@SuppressWarnings("unused")
	private Canvas canvas;

	public SideBarView(Context context) {
		super(context);
		this.context=context;
	}
	public SideBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}

	public SideBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		try {
			 this.canvas = canvas;
				Paint paint = new Paint();	
				Paint circlePaint = new Paint();
				final int textHeight = getHeight() / mLetter.length;
				final int width = getWidth();
				for (int i = 0; i < mLetter.length; i++) {
					paint.setAntiAlias(true);
					circlePaint.setAntiAlias(true);
					paint.setTextSize(23);
					final float xPos = width / 2 - paint.measureText(String.valueOf(mLetter[i])) / 2;
					final float yPos = textHeight * i + textHeight;
					if(selectedLetter != null && selectedLetter.equalsIgnoreCase(String.valueOf(mLetter[i]))){
						paint.setColor(Color.TRANSPARENT);
						paint.setColor(getResources().getColor(R.color.app_color));
						paint.setStrokeWidth(20);
						canvas.drawText(String.valueOf(mLetter[i]), xPos, yPos, paint);
					}
					else {
						if(i < 6){
							paint.setColor(Color.parseColor("#000000"));	
						}
						else {
							paint.setColor(Color.parseColor("#000000"));	
						}
						paint.setStrokeWidth(20);					
						canvas.drawText(String.valueOf(mLetter[i]), xPos, yPos, paint);
					}
						paint.reset();
						circlePaint.reset();
				}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		final float y = (int) event.getY();

		int idx = (int) (y / getHeight() * mLetter.length);
		if (idx >= mLetter.length) {
			idx = mLetter.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE && mLetter !=null) {
			mDialogText.setText(String.valueOf(mLetter[idx]));
			selectedLetter = String.valueOf( mLetter[idx]);
			invalidate();
		
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(mLetter[idx]);
			if (position == -1) {
				return true;
			}
		
			list.setSelection(position);
			
		} else {
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}


	public void setListView(ListView _list) {
		list = _list;
		sectionIndexter = (SectionIndexer) _list.getAdapter();

		Object[] sectionsArr = sectionIndexter.getSections();
		sections = new String[sectionsArr.length];
		for (int i = 0; i < sectionsArr.length; i++) {
			sections[i] = sectionsArr[i].toString();
		}
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}
}
