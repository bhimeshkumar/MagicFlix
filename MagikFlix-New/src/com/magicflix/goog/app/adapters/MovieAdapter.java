package com.magicflix.goog.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

import com.magicflix.goog.R;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.utils.Constants;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends FancyCoverFlowAdapter{

	private Activity mContext;
	private ArrayList<Videos> mVideosList;
	private int mMovieItemWidth;
	private int mMovieItemHeight;

	public MovieAdapter(Activity context,ArrayList<Videos> videosList ) {
		mContext = context;
		mVideosList = videosList;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mMovieItemWidth = (int) (metrics.widthPixels/2.5);
		mMovieItemHeight = (metrics.heightPixels/3);
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
	public View getCoverFlowItem(int position, View reuseableView, ViewGroup viewGroup) {
		CustomViewGroup customViewGroup = null;
		if (reuseableView != null) {
			customViewGroup = (CustomViewGroup) reuseableView;
		} else {
			customViewGroup = new CustomViewGroup(viewGroup.getContext());

			//              int movieItemWidth = mMovieItemHeight;//(int) mContext.getResources().getDimension(R.dimen.movieItemWidth);

			//              if(CompatibilityUtil.isTablet(mContext))
			//            	  customViewGroup.setLayoutParams(new FancyCoverFlow.LayoutParams(dpToPx(380), FancyCoverFlow.LayoutParams.MATCH_PARENT));
			//              else
			customViewGroup.setLayoutParams(new FancyCoverFlow.LayoutParams(mMovieItemWidth, FancyCoverFlow.LayoutParams.MATCH_PARENT));
		}
		if(Constants.SHOW_TITLES){
			customViewGroup.getTitleTextView().setVisibility(View.VISIBLE);
		}else{
			customViewGroup.getTitleTextView().setVisibility(View.GONE);
		}
		customViewGroup.getDurationTextView().setText(Constants.formatDuration(mVideosList.get(position).duration));
		customViewGroup.getTitleTextView().setText(mVideosList.get(position).title);
		if(mVideosList.get(position).thumbnailUrl != null)
			Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(customViewGroup.getImageView());
		else
			customViewGroup.getImageView().setBackground(mContext.getResources().getDrawable(R.drawable.image_loading_icon));
		return customViewGroup;
	}


	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics =mContext.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
		return px;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		//notifyChanged = true;
	}

	static class ViewHolder {
		public TextView durationTV;
		public ImageView movieThumbnailTV;
		public TextView videoTitleTV;
	}

	private static class CustomViewGroup extends LinearLayout {

		private TextView duartionTextView,videotitleLabel;
		private ImageView imageView;

		private CustomViewGroup(Context context) {
			super(context);
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View  rowView = inflater.inflate(R.layout.movie_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.movieThumbnailTV = (ImageView) rowView.findViewById(R.id.movie_list_thumbnail_iv);
			viewHolder.durationTV = (TextView) rowView.findViewById(R.id.movie_list_duration_tv);
			viewHolder.videoTitleTV = (TextView) rowView.findViewById(R.id.movie_list_item_title_tv);
			imageView = viewHolder.movieThumbnailTV ;
			duartionTextView = viewHolder.durationTV;
			videotitleLabel = viewHolder.videoTitleTV;
			rowView.setTag(viewHolder);
			this.addView(rowView);
		}

		private ImageView getImageView() {
			return imageView;
		}

		private TextView getDurationTextView() {
			return duartionTextView;
		}
		
		private TextView getTitleTextView() {
			return videotitleLabel;
		}
	}


	public static int convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int dp = (int) (px / (metrics.densityDpi / 160f));
		return dp;
	}
	


}
