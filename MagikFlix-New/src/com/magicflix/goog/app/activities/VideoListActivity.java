package com.magicflix.goog.app.activities;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.magicflix.goog.R;
import com.magicflix.goog.app.api.Model.VideoData;

public class VideoListActivity extends BaseActivity{

	private GridView  mVideoGrid;
	private Cursor videocursor;
	private int video_column_index;
	private int count;
	private ArrayList<VideoData> mVideoList;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_videos);
		init();
		setIdstoViews();
		initVideoGrid();
	}

	private void init() {
		getActionBar().hide();
		mVideoList = new ArrayList<VideoData>();
		
	}

	private void setIdstoViews() {
		mVideoGrid = (GridView)findViewById(R.id.video_list_grid);

	}

	private void initVideoGrid() {
		ContentResolver cr = getContentResolver();
		String[] proj = {MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE};
		Cursor c = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
		if (c.moveToFirst()) {
			do{
				int id = c.getInt(0);
				int columnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
				Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
				VideoData videoData = new VideoData();
				videoData.filePath = c.getString(columnIndex);
				videoData.bitmap = b;
				mVideoList.add(videoData);
			}
			while( c.moveToNext() );
		}         
		c.close();
		mVideoGrid.setAdapter(new VideoAdapter(getApplicationContext(),mVideoList));
	}


	public class VideoAdapter extends BaseAdapter {
		private Context vContext;
		private ArrayList<VideoData> mVideoList;

		public VideoAdapter(Context c, ArrayList<VideoData> videoList) {
			vContext = c;
			mVideoList = videoList;
		}

		public int getCount() {
			return mVideoList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater inflater = getLayoutInflater();
				rowView = inflater.inflate(R.layout.video_grid_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.movieThumbnailTV = (ImageView) rowView
						.findViewById(R.id.video_grid_iv);
				rowView.setTag(viewHolder);
				 
			}
			ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.movieThumbnailTV.setImageBitmap(mVideoList.get(position).bitmap);
			
			return rowView;
		}
	}

	static class ViewHolder {
		public ImageView movieThumbnailTV;
	}

}
