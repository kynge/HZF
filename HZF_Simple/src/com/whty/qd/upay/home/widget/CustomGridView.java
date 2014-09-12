package com.whty.qd.upay.home.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.GridView;

public class CustomGridView extends GridView {
    private ViewPager viewPager;

	public CustomGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    
	public void setViewPager(ViewPager viewPager){
		this.viewPager=viewPager;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.HorizontalScrollView#dispatchKeyEvent(android.view.KeyEvent
	 * )
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (null != viewPager) {
			viewPager.requestDisallowInterceptTouchEvent(true);
		}
		return super.dispatchKeyEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.HorizontalScrollView#onInterceptTouchEvent(android.view
	 * .MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (null != viewPager) {
			viewPager.requestDisallowInterceptTouchEvent(true);
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.HorizontalScrollView#onTouchEvent(android.view.MotionEvent
	 * )
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (null != viewPager) {
			viewPager.requestDisallowInterceptTouchEvent(true);
		}
		return super.onTouchEvent(ev);
	}
}
