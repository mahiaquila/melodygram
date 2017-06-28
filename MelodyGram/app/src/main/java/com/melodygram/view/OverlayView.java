
package com.melodygram.view;


import com.melodygram.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * Created by LALIT on 15-06-2016.
 */
public class OverlayView {
		
	public static View initOverlay(LayoutInflater layoutInflater, WindowManager windowManager) {
		TextView overlayTextView = (TextView) layoutInflater.inflate(R.layout.select_overlay, null);
		overlayTextView.setVisibility(View.INVISIBLE);
		return overlayTextView;
	}
}
