package com.magicflix.goog.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ToggleButton;

public class MFlixVideoFavourite extends ToggleButton
{
	public MFlixVideoFavourite(Context context)
	{
		super(context);
	}
	
	public MFlixVideoFavourite(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}
}
