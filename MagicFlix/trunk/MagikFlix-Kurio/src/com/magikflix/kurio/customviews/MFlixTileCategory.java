package com.magikflix.kurio.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.magikflix.kurio.R;
import com.magikflix.kurio.app.api.results.Categories;

public class MFlixTileCategory extends RelativeLayout
{
	//
	private ImageView	background;
	
	// private TextView name;
	public MFlixTileCategory(Context context)
	{
		this(context, null);
	}
	
	public MFlixTileCategory(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mflix_tile_category, this, true);
		//
		// name = (TextView) findViewById(R.id.display_name);
		// name.setTypeface(MagikFlix.proximaNovaBold);
		// name.setBackgroundColor(R.color.tomato_red);
		//
		background = (ImageView) findViewById(R.id.background);
		background.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
	}
	
	public void hideChildren()
	{
		background.setVisibility(View.GONE);
	}
	
	@SuppressLint("DefaultLocale")
	public void initilize(Categories _category)
	{
		
	}
}
