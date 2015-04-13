package com.magicflix.goog.app.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.api.Model.UserProfile;
import com.magicflix.goog.app.api.Model.VideoData;
import com.magicflix.goog.app.db.Db4oHelper;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.broadcasts.AppTimerBroadCastReceiver;
import com.magicflix.goog.broadcasts.PopUpDismissBroadCastReceiver;
import com.magicflix.goog.utils.MLogger;

public class VideoListActivity extends BaseActivity implements  OnClickListener{

	public static final String TAG = VideoListActivity.class.getName();

	private GridView  mVideoGrid;
	private TextView mNoVideoTV;
	private ArrayList<VideoData> mVideoList;
	private boolean[] thumbnailsselection;
	private MagikFlix mApplication;
	private Button mOkBtn;
	private Db4oHelper mDb4oProvider;
	private ArrayList<VideoData> mLocalPlayList = new ArrayList<VideoData>();
	private UserProfile mSlectedUserProfile;
	private boolean mIsBackbtnPressed = false;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow ;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_videos);
		init();
		setIdstoViews();
		initVideoGrid();
		setListnersToViews();
	}

	private void setListnersToViews() {
		mOkBtn.setOnClickListener(this);  

	}

	@Override
	protected void onResume() {
		super.onResume();
		((MagikFlix)getApplication()).setIsAppRunningBackground(false);
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
		registerReceiver(mPopUpDismissBroadCastReceiver, mPopUpDismissIntent);
	}

	@Override
	protected void onPause() {
		if(!mIsBackbtnPressed){
			((MagikFlix)getApplication()).setIsAppRunningBackground(true);
		}
		unregisterReceiver(mAppTimerBroadCastReceiver);
		unregisterReceiver(mPopUpDismissBroadCastReceiver);

		super.onPause();
	}

	private void init() {
		getActionBar().hide();
		mVideoList = new ArrayList<VideoData>();
		mApplication = (MagikFlix)getApplication();
		mDb4oProvider = new Db4oHelper(this);
		mSlectedUserProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());

		mAppTimerIntent = new IntentFilter(Constants.INTENT_APP_TIMER_EXPIRED);

		mAppTimerBroadCastReceiver = new AppTimerBroadCastReceiver() {

			@Override
			protected void onTimerExpired() {
				popupWindow = getTimerAlert();
				popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);

			}
		};

		mPopUpDismissIntent = new IntentFilter(Constants.INTENT_APP_ALERT_DISMISS);
		mPopUpDismissBroadCastReceiver = new PopUpDismissBroadCastReceiver() {

			@Override
			protected void dismissPopUp() {
				try {
					if(popupWindow != null && popupWindow.isShowing()){
						popupWindow.dismiss();
					}
				} catch (Exception e) {
					MLogger.logInfo(TAG, "Exception in mPopUpDismissBroadCastReceiver ::"+e.getLocalizedMessage());
				}
			}
		};

	}

	private void setIdstoViews() {
		mVideoGrid = (GridView)findViewById(R.id.video_list_grid);
		mOkBtn = (Button)findViewById(R.id.video_list_screen_ok_btn);
		mNoVideoTV = (TextView)findViewById(R.id.video_list_screen_no_videos_tv);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int localPlayListWidth = (int) getResources().getDimension(R.dimen.local_play_list_item_width);
		mVideoGrid.setNumColumns((metrics.widthPixels)/localPlayListWidth);
	}

	private void initVideoGrid() {
		try {

			ContentResolver cr = getContentResolver();

			String condition=  MediaStore.Video.Media.DATA +" like?";
			String[] selectionArguments=new String[]{"%DCIM/Camera/%"};
			String sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC";
			String[] proj = {MediaStore.Video.Media._ID,
					MediaStore.Video.Media.DATA,
					MediaStore.Video.Media.DURATION,
					MediaStore.Video.Media.DISPLAY_NAME,
					MediaStore.Video.Media.SIZE};
			Cursor c = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, condition, selectionArguments, sortOrder);
			if (c.moveToFirst()) {
				do{
					int id = c.getInt(0);
					int columnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					//Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
					String duration = c.getString(c.getColumnIndex("duration"));
					String name = c.getString(c.getColumnIndex("_display_name"));
					name = name.substring(0, name.lastIndexOf('.'));
					VideoData videoData = new VideoData();
					videoData.filePath = c.getString(columnIndex);
					videoData.id = id;
					videoData.name = name;
					videoData.columnIndex = columnIndex;
					videoData.duration = Integer.parseInt(duration)/1000;
					mVideoList.add(videoData);
					//mPlayListBitmap.add(b);
				}
				while( c.moveToNext() );
			}         
			c.close();

			setUpGridView();
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in initVideoGrid() :: "+e.getLocalizedMessage());
		}
	}

	private void setUpGridView() {

		this.thumbnailsselection = new boolean[mVideoList.size()];
		if(mSlectedUserProfile.playList != null){
			for (int i = 0; i < mVideoList.size(); i++) {
				for (int j = 0; j < mSlectedUserProfile.playList.size(); j++) {
					if(mVideoList.get(i).filePath.equalsIgnoreCase(mSlectedUserProfile.playList.get(j).filePath)){
						thumbnailsselection[i] = true;
					}
				}

			}
		}

		
		if(mVideoList.size() > 0){
			mNoVideoTV.setVisibility(View.GONE);
			mVideoGrid.setVisibility(View.VISIBLE);
			mVideoGrid.setAdapter(new VideoAdapter(getApplicationContext(),mVideoList));
		}else{
			mNoVideoTV.setVisibility(View.VISIBLE);
			mVideoGrid.setVisibility(View.GONE);
		}
		
	}


	public class VideoAdapter extends BaseAdapter {
		private ArrayList<VideoData> videoArrayList;

		public VideoAdapter(Context context, ArrayList<VideoData> videoList) {
			videoArrayList = videoList;
		}

		public int getCount() {
			return videoArrayList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView( final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater inflater = getLayoutInflater();
				rowView = inflater.inflate(R.layout.video_grid_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.movieThumbnailTV = (ImageView) rowView.findViewById(R.id.video_grid_iv);
				viewHolder.videoSelectIV = (ImageView) rowView.findViewById(R.id.video_grid_iv_select);
				viewHolder.mTitleTV = (TextView) rowView.findViewById(R.id.video_list_title_tv);
				rowView.setTag(viewHolder);
			}
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.movieThumbnailTV.setId(position);

			if(thumbnailsselection[position]){
				holder.videoSelectIV.setVisibility(View.VISIBLE);	
			}else{
				holder.videoSelectIV.setVisibility(View.GONE);
			}
			holder.mTitleTV.setText(mVideoList.get(position).name);
			BitmapWorkerTask task = new BitmapWorkerTask(holder.movieThumbnailTV);
			task.execute(videoArrayList.get(position));
			holder.movieThumbnailTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (thumbnailsselection[position]) {
						thumbnailsselection[position] = false;
					} else {
						thumbnailsselection[position] = true;
					}
					notifyDataSetChanged();

				}
			});

			return rowView;
		}
	}

	static class ViewHolder {
		public ImageView videoSelectIV;
		public ImageView movieThumbnailTV;
		public TextView mTitleTV;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.video_list_screen_ok_btn:
			addLocalPlayList();
			break;
		default:
			break;
		}

	}

	private void addLocalPlayList() {
		try {
			mLocalPlayList.clear();
			int selectedProfileIndex = mApplication.getSelectedProfileIndex();
			UserProfile userProfile = mDb4oProvider.getUserProfileById(selectedProfileIndex);
			for (int i = 0; i < thumbnailsselection.length; i++) {
				if(thumbnailsselection[i] ){
					mLocalPlayList.add(mVideoList.get(i));
				}
			}
			userProfile.playList = new ArrayList<VideoData>();
			if(mLocalPlayList.size() > 0){
				userProfile.playList = mLocalPlayList;
			}
			mDb4oProvider.store(userProfile);
			this.finish();

		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in addLocalPlayList :: "+e.getLocalizedMessage());
		}
	}

	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		addLocalPlayList();
		super.onBackPressed();

	}


	class BitmapWorkerTask extends AsyncTask<VideoData, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private VideoData data ;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(imageViewReference != null){
				imageViewReference.get().setBackground(getResources().getDrawable(R.drawable.image_loading_icon));
			}
		}

		public BitmapWorkerTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(VideoData... params) {
			data = params[0];
			return ThumbnailUtils.createVideoThumbnail(data.filePath,MediaStore.Video.Thumbnails.MINI_KIND);
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
