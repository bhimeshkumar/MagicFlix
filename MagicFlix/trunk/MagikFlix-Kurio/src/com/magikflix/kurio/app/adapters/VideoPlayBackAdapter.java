package com.magikflix.kurio.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.magikflix.kurio.R;
import com.magikflix.kurio.app.api.results.Videos;
import com.magikflix.kurio.utils.CompatibilityUtil;

public class VideoPlayBackAdapter extends BaseAdapter implements OnClickListener{

	private Activity mContext;
	private ArrayList<Videos> mVideosList;
	static class ViewHolder {
		public ImageView videothumbnailIV;
	}

	public VideoPlayBackAdapter(Activity context, ArrayList<Videos> videosList){
		mContext = context;
		mVideosList = videosList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = null;
		convertView = null;
		if (convertView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			ViewHolder viewHolder = new ViewHolder();
			rowView = inflater.inflate(R.layout.video_play_back_list_item, null);
			viewHolder.videothumbnailIV = (ImageView)rowView.findViewById(R.id.video_play_back_list_iv);
			buildThumbnailImage(viewHolder.videothumbnailIV);
			rowView.setTag(viewHolder);
		}


		ViewHolder holder = (ViewHolder) rowView.getTag();

//		Picasso.with(mContext)
//		.load(mVideosList.get(position).tb)
//		.placeholder(R.color.light_red)
//		.error(R.color.light_red)
//		.into(holder.videothumbnailIV);
		return rowView;
	}



	@Override
	public int getCount() {
		return mVideosList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public void onClick(View v) {
		//		navigateToVideoPlayingScreen();

	}

	public void buildThumbnailImage(ImageView thumbnaiImage){
		LinearLayout.LayoutParams params;
		if(CompatibilityUtil.isTablet(mContext)){
			params = new LinearLayout.LayoutParams(
					convertDpToPixel(200),      
					convertDpToPixel(150));
		}else{
			params = new LinearLayout.LayoutParams(
					convertDpToPixel(150),      
					convertDpToPixel(100) );
		}
		
		params.setMargins(convertDpToPixel(5), convertDpToPixel(5), convertDpToPixel(5), convertDpToPixel(5));
		thumbnaiImage.setLayoutParams(params);
	}

	public int convertDpToPixel(int dpMeasure){
		Resources r = mContext.getResources();
		int px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dpMeasure, 
				r.getDisplayMetrics()
				);
		return px;
	}


}