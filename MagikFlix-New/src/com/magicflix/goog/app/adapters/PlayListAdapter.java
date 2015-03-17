package com.magicflix.goog.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magicflix.goog.R;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.utils.Constants;
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
		      viewHolder.mVideoDurationTV = (TextView) rowView
			          .findViewById(R.id.play_list_duration_tv);
		      viewHolder.mVideoTitleTV = (TextView) rowView
			          .findViewById(R.id.play_list_title_tv);
		      rowView.setTag(viewHolder);
		    }
		    // fill data
		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    holder.mVideoTitleTV.setText(mVideosList.get(position).title);
		    holder.mVideoDurationTV.setText(Constants.formatDuration(mVideosList.get(position).duration));
		    Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(holder.movieThumbnailTV);
		return rowView;
	}

	
	static class ViewHolder {
		public TextView mVideoDurationTV;
		public ImageView movieThumbnailTV;
		public TextView mVideoTitleTV;
	}


}
