package com.magicflix.goog.app.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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
		ViewHolder holder = (ViewHolder) rowView.getTag();
		if(Constants.SHOW_PLAY_LIST_TITLES){
			holder.mVideoTitleTV.setVisibility(View.VISIBLE);
		}else{
			holder.mVideoTitleTV.setVisibility(View.GONE);
		}
		if(!mVideosList.get(position).category.equalsIgnoreCase(mContext.getString(R.string.category_camera_roll_title))){
			holder.mVideoTitleTV.setText(mVideosList.get(position).title);
			Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(holder.movieThumbnailTV);
		}
		if(mVideosList.get(position).category.equalsIgnoreCase(mContext.getString(R.string.category_camera_roll_title))){
			BitmapWorkerTask task = new BitmapWorkerTask(holder.movieThumbnailTV);
		    task.execute(mVideosList.get(position));
		    holder.mVideoTitleTV.setText(mVideosList.get(position).title);
//			viewHolder.movieThumbnailTV.setImageBitmap(mCameraRollBitmapList.get(position));
		}
		holder.mVideoDurationTV.setText(Constants.formatDuration(mVideosList.get(position).duration));
		return rowView;
	}


	static class ViewHolder {
		public TextView mVideoDurationTV;
		public ImageView movieThumbnailTV;
		public TextView mVideoTitleTV;
	}
	
	class BitmapWorkerTask extends AsyncTask<Videos, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private Videos data ;
	    
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	if(imageViewReference != null){
	    		imageViewReference.get().setBackground(mContext.getResources().getDrawable(R.drawable.image_loading_icon));
	    	}
	    }

	    public BitmapWorkerTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	        
	    }

	    @Override
	    protected Bitmap doInBackground(Videos... params) {
	        data = params[0];
	        System.out.println("Video Path :: "+data.videoId);
	        return MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), data.bitmapId, MediaStore.Video.Thumbnails.MINI_KIND, null);//ThumbnailUtils.createVideoThumbnail(data,MediaStore.Video.Thumbnails.MINI_KIND);
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	            	imageView.setBackground(null);
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

}
