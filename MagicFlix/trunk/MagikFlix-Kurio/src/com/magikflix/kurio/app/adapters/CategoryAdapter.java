package com.magikflix.kurio.app.adapters;

import java.util.Vector;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magikflix.kurio.R;
import com.magikflix.kurio.app.activities.HomeActivity;
import com.magikflix.kurio.app.api.results.Playlists;
import com.magikflix.kurio.utils.CompatibilityUtil;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends BaseAdapter{

	private Activity mContext;
	private String[] mList;
	int m_w, m_h;
	protected Vector<Boolean> selectedStates;
	private Playlists[] mPlayLists;

	public CategoryAdapter(Activity context, Playlists[] playLists) {
		mContext = context;
		selectedStates = new Vector<Boolean>();
		mPlayLists = playLists;
		clearSelectedState();
		selectedStates.set(3, new Boolean(true));
	}

	@Override
	public int getCount() {
		return mPlayLists.length; 
	}

	public Object getItem(int position) {

		return mPlayLists[position];
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			rowView = inflater.inflate(R.layout.category_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.movieThumbnailTV = (ImageView) rowView
					.findViewById(R.id.category_thumbnail_iv);
			viewHolder.category_title_tv = (TextView) rowView
					.findViewById(R.id.category_title_tv);

//			viewHolder.mOuterCircleLayout = (RelativeLayout) rowView
//					.findViewById(R.id.category_outline_layout);
			viewHolder.mInnerCircleLayout = (RelativeLayout) rowView
					.findViewById(R.id.category_inline_layout);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.category_title_tv.setText("Category "+position);
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearSelectedState();
				selectedStates.set(position, new Boolean(true));
				((HomeActivity)mContext).setPlayList(position);
				CategoryAdapter.this.notifyDataSetChanged();

			}
		});

		if(mPlayLists[position].thumbnailUrl != null && mPlayLists[position].thumbnailUrl.length() > 0)
			Picasso.with(mContext).load(mPlayLists[position].thumbnailUrl).into(holder.movieThumbnailTV);
		else
			holder.movieThumbnailTV .setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_outline_bg));

		if (selectedStates.get(position)){
			holder.movieThumbnailTV.requestLayout();
			holder.mInnerCircleLayout.requestLayout();
			if(CompatibilityUtil.isTablet(mContext)){
				holder.movieThumbnailTV.requestLayout();
				holder.movieThumbnailTV.getLayoutParams().height = dpToPx(90);
				holder.movieThumbnailTV.getLayoutParams().width = dpToPx(90);
				holder.mInnerCircleLayout .getLayoutParams().height = dpToPx(90);
				holder.mInnerCircleLayout .getLayoutParams().width = dpToPx(90);
			}else{
				holder.movieThumbnailTV.requestLayout();
				holder.movieThumbnailTV.getLayoutParams().height = dpToPx(60);
				holder.movieThumbnailTV.getLayoutParams().width = dpToPx(60);
				holder.mInnerCircleLayout .getLayoutParams().height = dpToPx(60);
				holder.mInnerCircleLayout .getLayoutParams().width = dpToPx(60);
			}
			holder.mInnerCircleLayout.setBackground(mContext.getResources().getDrawable(R.drawable.icon_outline_color_selected));
		}
		else {
			holder.mInnerCircleLayout.requestLayout();
			if(CompatibilityUtil.isTablet(mContext)){
				holder.movieThumbnailTV.requestLayout();
				holder.movieThumbnailTV.getLayoutParams().height = dpToPx(80);
				holder.movieThumbnailTV.getLayoutParams().width = dpToPx(80);
				holder.mInnerCircleLayout .getLayoutParams().height = dpToPx(80);
				holder.mInnerCircleLayout .getLayoutParams().width = dpToPx(80);
			}else{
				holder.movieThumbnailTV.requestLayout();
				holder.movieThumbnailTV.getLayoutParams().height = dpToPx(50);
				holder.movieThumbnailTV.getLayoutParams().width = dpToPx(50);
				holder.mInnerCircleLayout .getLayoutParams().height = dpToPx(50);
				holder.mInnerCircleLayout .getLayoutParams().width = dpToPx(50);
			}
			
			holder.mInnerCircleLayout.setBackground(mContext.getResources().getDrawable(R.drawable.icon_outline_color_unselected));
		}
		holder.category_title_tv.setVisibility(View.GONE);

		holder.category_title_tv.setText(mPlayLists[position].name);
		return rowView;
	}

	public int checkPosition(int position) { 
		if (position >= mList.length) { 
			position = position % mList.length; 
		} 
		return position; 
	} 

	static class ViewHolder {
		public TextView category_title_tv;
		public TextView movieNameTV;
		public ImageView movieThumbnailTV;
//		public RelativeLayout mOuterCircleLayout;
		public RelativeLayout mInnerCircleLayout;
	}

	private void clearSelectedState () {
		selectedStates.clear();  
		for (int i = 0 ; i < 30; i++) {
			selectedStates.add(new Boolean(false));
		} 
	}

	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics =mContext.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
		return px;
	}


}
