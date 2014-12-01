package com.magicflix.goog.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.magicflix.goog.R;
import com.magicflix.goog.app.api.results.Videos;

public class MFlixTileVideoSmall extends MFlixTileVideo
{
	public MFlixTileVideoSmall(Context context)
	{
		this(context, null);
	}
	
	public MFlixTileVideoSmall(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mflix_tile_video_small, this, true);
		//
		background = (ImageView) findViewById(R.id.background);
		background.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
			}
		});
		favourite = (MFlixVideoFavourite) findViewById(R.id.favourite);
		//
		locked = (ImageView) findViewById(R.id.locked);
//		locked.setVisibility(View.GONE);
//		favourite.setVisibility(View.VISIBLE);
		name = (TextView) findViewById(R.id.name);
		//

	}
	
	@Override
	public void hideChildren()
	{
		background.setVisibility(View.GONE);
		favourite.setVisibility(View.GONE);
		locked.setVisibility(View.GONE);
//		cost_in_coins.setVisibility(View.GONE);
	}
	
	@Override
	public void initilize(Videos _video, int positionV)
	{
		
	}
	private String getUnFavoritesPrefs(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		return sharedPreferences.getString("unFavs", "[]");			
	}
	
	private void saveUnFavoritesPrefs(String key,String value){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
		
	}
}
