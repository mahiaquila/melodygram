package com.melodygram.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ZoomableRelativeLayout extends RelativeLayout {
	// ScalingFactor i.e. Amount of Zoom
	static float mScaleFactor = 1.0f;
	private static float MIN_ZOOM = 1.0f;
	private static float MAX_ZOOM = 4.0f;
	public boolean isScaling;

	// Track the Bound of the Image after zoom to calculate the offset
	static Rect mClipBound;

	// mDetector to detect the scaleGesture for the pinch Zoom
	private ScaleGestureDetector mScaleDetector;

	// mDoubleTapDetector to detect the double tap
	private GestureDetector mGestureDetector;

	public float _distanceX;

	public float _distanceY;
	
	private static final int INVALID_POINTER_ID = -1;

	private int mActivePointerId = INVALID_POINTER_ID;
	
	private float mPosX;
    private float mPosY;
    
    private float mLastTouchX;
    private float mLastTouchY;

	// Pivot point for Scaling
	static float gx = 0, gy = 0;
	
	int screenWidth;
	int screenHeight;

	public ZoomableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			setWillNotDraw(false);
			mClipBound = new Rect();
			mScaleFactor = 1.0f;
			mScaleDetector = new ScaleGestureDetector(getContext(),
					new ZoomListener());
			mGestureDetector = new GestureDetector(getContext(), new MySimpleGestureListener());
		}
	}

	
	public ZoomableRelativeLayout(Context context) {
		super(context);
		if (!isInEditMode()) {
			setWillNotDraw(false);
			mClipBound = new Rect();
			mScaleFactor = 1.0f;
			mScaleDetector = new ScaleGestureDetector(getContext(),	new ZoomListener());
			mGestureDetector = new GestureDetector(getContext(), new MySimpleGestureListener());
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		mScaleDetector.onTouchEvent(ev);
		mGestureDetector.onTouchEvent(ev);
		// Handles all type of motion-events possible
		final int action = ev.getAction();
	    switch (action & MotionEvent.ACTION_MASK) {
	    case MotionEvent.ACTION_DOWN: {
	        final float x = ev.getX();
	        final float y = ev.getY();
	        mLastTouchX = x;
	        mLastTouchY = y;
	        mActivePointerId = ev.getPointerId(0);
	        break;
	    }

	    case MotionEvent.ACTION_MOVE: {
	    	if (mScaleFactor == 1.0f)
	    		break;
	        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
	        final float x = ev.getX(pointerIndex);
	        final float y = ev.getY(pointerIndex);

	        if (!mScaleDetector.isInProgress()) {
	        	
	            float dx = x - mLastTouchX;
	            float dy = y - mLastTouchY;
	            

		            if (mClipBound.top < 104 && dy > 0)
		            	dy = 0;
		            
		            if (mClipBound.bottom > (screenHeight - 77) && dy < 0)
		            	dy = 0;
	            
	            if (mClipBound.left < 30 && dx > 0)
	            	dx = 0;
	            
	            if (mClipBound.right > screenWidth && dx < 0)
	            	dx = 0;

	            mPosX += dx;
	            mPosY += dy;

	            if (mScaleFactor != 1.0f)
	            	invalidate();
	        }

	        mLastTouchX = x;
	        mLastTouchY = y;

	        break;
	    }
	    
	    case MotionEvent.ACTION_CANCEL: {
	    	//startInterceptEvent();
	        mActivePointerId = INVALID_POINTER_ID;
	        break;
	    }
		case MotionEvent.ACTION_POINTER_DOWN:
			// Event occurs when the second finger is pressed down
			//startInterceptEvent();
			isScaling = true;
			break;

		case MotionEvent.ACTION_UP:
			//startInterceptEvent();
			mActivePointerId = INVALID_POINTER_ID;
			//startInterceptEvent();
	        break;
	   

		case MotionEvent.ACTION_POINTER_UP: {
	        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = ev.getPointerId(pointerIndex);
	        if (pointerId == mActivePointerId) {
	            // This was our active pointer going up. Choose a new
	            // active pointer and adjust accordingly.
	            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	            mLastTouchX = ev.getX(newPointerIndex);
	            mLastTouchY = ev.getY(newPointerIndex);
	            mActivePointerId = ev.getPointerId(newPointerIndex);
	        }
	        break;
	    }
	    }
		
		return true;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return false;
	}

	
	@Override
	public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
		return super.invalidateChildInParent(location, dirty);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				LayoutParams params = (LayoutParams) child
						.getLayoutParams();
				child.layout((int) (params.leftMargin),
							(int) (params.topMargin),
							(int) (params.leftMargin + child.getMeasuredWidth()),
							(int) (params.topMargin + child.getMeasuredHeight()));
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	    screenWidth = widthSize;
	    screenHeight = heightSize;
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    setMeasuredDimension((int) (widthSize), (int) (heightSize ));
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// Save the canvas to set the scaling factor returned from detector
		canvas.save();

		if (mScaleFactor == 1.0f) {
			mPosX = 0.0f;
			mPosY = 0.0f;
		}
			
		canvas.translate(mPosX, mPosY);
	    //canvas.scale(mScaleFactor, mScaleFactor);
	    
		canvas.scale(mScaleFactor, mScaleFactor, gx, gy);
		
		//GlobalState.doubleTapFlag = false;
		super.dispatchDraw(canvas);

		mClipBound = canvas.getClipBounds();
		canvas.restore();
	}

	
	private class ZoomListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// getting the scaleFactor from the detector
			//startInterceptEvent();
			mScaleFactor *= detector.getScaleFactor();  // Gives the scaling
														// factor from the
														// previous scaling to
														// the current
			// Log.d("Print", "detector scaling Factor" + mScaleFactor);

			gx = detector.getFocusX();
			gy = detector.getFocusY();
			// Limit the scale factor in the MIN and MAX bound
			mScaleFactor = Math.max(Math.min(mScaleFactor, MAX_ZOOM), MIN_ZOOM);
			
			// Here we are only zooming so invalidate has to be done
			invalidate();
			requestLayout();

			// GlobalState.isPageScrolled = false;
			// we have handle the onScale
			return true;
		}
	}
	
	class MySimpleGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			_distanceX = distanceX;
			_distanceY = distanceY;
			return true;
		}
	}
	
}
