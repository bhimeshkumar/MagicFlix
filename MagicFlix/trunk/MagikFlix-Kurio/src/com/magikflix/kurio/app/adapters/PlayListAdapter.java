package com.magikflix.kurio.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magikflix.kurio.R;
import com.magikflix.kurio.app.api.results.Videos;
import com.squareup.picasso.Picasso;

public class PlayListAdapter extends BaseAdapter{

	private Activity mContext;
	private ArrayList<Videos> mVideosList;

	public PlayListAdapter(Activity context, ArrayList<Videos> videosList) {
		mContext = context;
		mVideosList = videosList;
	}

	@Override
	public int getCount() {
		return mVideosList.size(); 
	}

	public Object getItem(int position) {
		
		return mVideosList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		 if (rowView == null) {
		      LayoutInflater inflater = mContext.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.main_activity_play_list_item, null);
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.movieThumbnailTV = (ImageView) rowView
		          .findViewById(R.id.play_list_thumbnail_iv);
		      rowView.setTag(viewHolder);
		    }
		    // fill data
		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(holder.movieThumbnailTV);
		return rowView;
	}

	
	static class ViewHolder {
		public TextView movieNameTV;
		public ImageView movieThumbnailTV;
	}


}
