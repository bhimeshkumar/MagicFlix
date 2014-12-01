package com.magicflix.goog.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magicflix.goog.app.api.results.Videos;

public abstract class MFlixTileVideo extends RelativeLayout
{
	protected Videos		  video;
	//
	protected ImageView		 background;
	protected MFlixVideoFavourite favourite;
	protected ImageView		 locked;
	protected TextView		  name;
	protected TextView		  cost_in_coins;
	//
	protected ImageView		 purchase_anim_start;
	protected ImageView		 purchase_anim_finish;
	//
	protected Animation		 slide_in_left, slide_out_left;
	protected int position;
	
	public void initilize(Videos _video, int position)
	{}
	
	public void hideChildren()
	{}
	
	public MFlixTileVideo(Context context)
	{
		this(context, null);
	}
	
	public MFlixTileVideo(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public String getVideoId()
	{
		return video.videoId;
	}
	
	protected void onClick(View v)
	{}


}
