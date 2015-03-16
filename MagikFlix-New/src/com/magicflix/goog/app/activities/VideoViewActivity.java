package com.magicflix.goog.app.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.keyes.youtube.VideoId;
import com.keyes.youtube.YouTubeId;
import com.keyes.youtube.YouTubeUtility;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.CustomLogginRequest;
import com.magicflix.goog.app.api.requests.FavourireVideoRequest;
import com.magicflix.goog.app.api.requests.RecentVideoRequest;
import com.magicflix.goog.app.api.requests.VimeoVideoRequest;
import com.magicflix.goog.app.api.results.VideoInfo;
import com.magicflix.goog.app.api.results.VideoResult;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.api.results.VimeoVideoResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.views.CustomVideoView;
import com.magicflix.goog.app.views.CustomVideoView.PlayPauseListener;
import com.magicflix.goog.medialplayer.CustomMediaController;
import com.magicflix.goog.utils.MLogger;

public class VideoViewActivity extends BaseActivity implements PlayPauseListener, OnClickListener, OnItemClickListener, OnSeekBarChangeListener, OnPreparedListener, OnCompletionListener, OnErrorListener{
	private static String TAG = VideoPlayingActivity.class.getName();
	private ArrayList<Videos> videosList;
	private String mVideoId;
	private ProgressBar mProgressBar;
	private Timer videoTimer;
	private int mSelectedPostion = -1;
	public static final String SCHEME_YOUTUBE_VIDEO = "ytv";
	public static final String SCHEME_YOUTUBE_PLAYLIST = "ytpl";
	protected CustomVideoView   mVideoView;
	private String mToken,mCategoryName;
	private String mRecentVideoId = Constants.EMPTY_STRING;
	private CustomMediaController controller;
	private int mVideoPausedPosition = 0;
	private RelativeLayout mTransparentLayout;
	private ImageView mPlayIV,mBackToGalleryIV,mFavIV;
	private TimerTask videoTimerTask;

	private boolean mVideoIsStarted = false, mIsBackbtnPressed = false;
	private boolean mIsVideoPaused = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.magik_flix_video_play_screen_videoview);
		hideActionBar();
		setIdsToViews();
		getVideoLink();
		setListnersToViews();
	}

	private void getVideoLink() {
		Bundle bundle =getIntent().getExtras();
		videosList  = (ArrayList<Videos>) bundle.getSerializable("videosList");
		mCategoryName = bundle.getString("categoryName", "");
		mVideoId = bundle.getString("videoId");
		mSelectedPostion = bundle.getInt("selectedPosition", 0);
		mToken = ((MagikFlix) getApplication()).getToken();
		if(videosList.get(mSelectedPostion).isFavorite){
			mFavIV.setBackground(VideoViewActivity.this.getResources().getDrawable(R.drawable.icon_outline_color_selected));
		}else{
			mFavIV.setBackground(null);
		}

		if(mCategoryName.equalsIgnoreCase("Vimeo")){
			getVimeoVideoURL(mVideoId);
		}else{
			VideoId lYouTubeId = new VideoId(mVideoId);
			new ProcesYouTubeTask().execute(lYouTubeId);
		}
	}

	private void selecetNextVideoToPlay(){
		if (mSelectedPostion < (videosList.size() -1)){
			mSelectedPostion = mSelectedPostion + 1;
			mVideoId = videosList.get(mSelectedPostion).videoId;
		}else{
			mSelectedPostion = 0;
			mVideoId = videosList.get(mSelectedPostion).videoId;
		}

		if(mCategoryName.equalsIgnoreCase("Vimeo")){
			getVimeoVideoURL(mVideoId);
		}else{

			VideoId lYouTubeId = new VideoId(mVideoId);
			new ProcesYouTubeTask().execute(lYouTubeId);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setListnersToViews() {
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setPlayPauseListener(this);
		mTransparentLayout.setOnClickListener(this);
		mPlayIV.setOnClickListener(this);
		mBackToGalleryIV.setOnClickListener(this);
		mFavIV.setOnClickListener(this);
		mVideoView.setOnClickListener(this);
		//		mVideoView.setMediaContrllerListener(new MediaControllerEventListener() {
		//
		//			@Override
		//			public void controllerVisible() {
		//				//	showActionBar();
		//			}
		//
		//			@Override
		//			public void controllerHiden() {
		//				//hideActionBar();
		//			}
		//		});
	}

	private void showActionBar(){
		getActionBar().show();
	}

	private void hideActionBar(){
		getActionBar().hide();
	}
	private void setIdsToViews() {
		mTransparentLayout = (RelativeLayout)findViewById(R.id.transparent_root_layout);
		mPlayIV = (ImageView)findViewById(R.id.play_btn);
		mBackToGalleryIV = (ImageView)findViewById(R.id.back_to_main_gallery_btn);
		mFavIV = (ImageView)findViewById(R.id.favourite_btn);
		mVideoView = (CustomVideoView)findViewById(R.id.video_view);
		mProgressBar = (ProgressBar)findViewById(R.id.video_play_screen_pb);
		//		controller = new CustomMediaController(this,null,this);
		//		controller.setAnchorView(mVideoView);
		//		mVideoView.setMediaController(controller);

	}

	private void playVideo(Uri videoUri){
		mVideoView.setVideoURI(videoUri);
		mVideoView.requestFocus();
		mVideoView.start();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.play_btn:
			if(mVideoView != null){
				if(((MagikFlix)getApplicationContext()).isTrialPeriodExpired()){
					PopupWindow popupWindow = getTrialExpiredPopUp(getString(R.string.times_up_txt));
					popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
					return;
				}
				mTransparentLayout.setVisibility(View.GONE);
				mVideoView.start();
			}
			break;
		case R.id.back_to_main_gallery_btn:
			this.finish();
			break;
		case R.id.favourite_btn:
			if(!videosList.get(mSelectedPostion).isFavorite){
				videosList.get(mSelectedPostion).isFavorite = true;
				mFavIV.setBackground(VideoViewActivity.this.getResources().getDrawable(R.drawable.icon_outline_color_selected));
			}else{
				videosList.get(mSelectedPostion).isFavorite = false;
				mFavIV.setBackground(null);
			}
			postFavouriteVideos(videosList.get(mSelectedPostion),videosList.get(mSelectedPostion).isFavorite);
			break;
		case R.id.video_view:
			if(mVideoView != null){
				if(mVideoView.isPlaying()){
					mTransparentLayout.setVisibility(View.VISIBLE);
					mVideoView.pause();
				}
			}
			break;
		default:
			break;
		}		
	}

	private void handlePlayAndPause() {
		if(mVideoView.isPlaying()){
			mVideoView.pause();
		}else{
			mVideoView.start();
		}
	}

	/*public void setUpVideoViewScreenMode() {
		if(!showingFullScreen){
			setUpFullScreenMode();
		}else{
			exitFullScreenMode();
		}
	}*/

	/*private void exitFullScreenMode() {
		//mVideoListView.setVisibility(View.VISIBLE);
		mSeekBarLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.7f));
		//mVideoListView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.3f));
		this.getActionBar().show();
		mFullScreenIV.setImageDrawable(getResources().getDrawable(R.drawable.img_video_fullscreen_off));
		showingFullScreen = false;
	}*/

	/*private void setUpFullScreenMode() {
		//mVideoListView.setVisibility(View.GONE);
		mSeekBarLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0 ,1f));
		this.getActionBar().hide();
		mFullScreenIV.setImageDrawable(getResources().getDrawable(R.drawable.img_video_fullscreen_on));
		showingFullScreen = true;
	}*/


	/*private void setHandsetLayout(){
		//mVideoListView.setVisibility(View.GONE);
		mSeekBarLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0 ,1f));
		this.getActionBar().hide();
	}*/




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {


	}

	public void setMarginsToListView(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.MATCH_PARENT
				);
		params.setMargins(convertDpToPixel(60), convertDpToPixel(10), convertDpToPixel(10), 0);
		//mVideoListView.setLayoutParams(params);
	}

	public int convertDpToPixel(int dpMeasure){
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dpMeasure, 
				r.getDisplayMetrics()
				);
		return px;
	}

	public void setUpActionBar(boolean homeButtonEnabled){
		ActionBar actionBar = this.getActionBar();
		if(homeButtonEnabled)
			actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
	}



	private void startVideoTimer()
	{
		try
		{
			if (null != videoTimer)
			{
				videoTimer.cancel();
				videoTimer = null;
			}

			videoTimer = new Timer();
			final int delayTime = 1000;
			int period = 1000;
			videoTimerTask = new TimerTask(){
				int videoPlayedSecs = 0;
				public void run(){
					try{
						videoPlayedSecs++;
						if(videoPlayedSecs == 10){
							postRecentVideos(mVideoId);
						}

						MagikFlix app = (MagikFlix)getApplicationContext();
						if(mVideoView != null && mVideoView.isPlaying()){
							app.setVideoPlayTime(app.getVideoPlayTime()+1);
						}

						//						int trialPeriod = Constants.TIMERLIMIT;
						//						int trialPeriodinSecs = trialPeriod * 60;
						//						if(app.getVideoPlayTime() >= trialPeriodinSecs){
						//							app.setIsTrialPeriod(true);
						//							this.cancel();
						//							Intent intnet = new Intent("com.magikflic.goog.TRIALEXPIRED");
						//							sendBroadcast(intnet);
						//						}
						//						mVideoSeekBar.setProgress(mVideoView.getCurrentPosition());
					}
					catch (Exception e)
					{
						// L.fe(activity,"", "startVideoTimer : Exception " + e.getMessage());
					}

				}
			};
			videoTimer.schedule(videoTimerTask, delayTime, period);
		}
		catch (Exception e)
		{}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mVideoView.seekTo(seekBar.getProgress());

	}

	private void postRecentVideos(String videoId) {
		RecentVideoRequest  loginRequest = new RecentVideoRequest();
		loginRequest.requestDelegate = new MFlixJsonBuilder();
		loginRequest.token = mToken;
		mRecentVideoId = videoId;
		loginRequest.recent = new String[]{videoId};
		loginRequest.requestType =  WebRequestType.POST_RECENT_VIDEOS;
		new DataApiAsyncTask(true, this, recentHandler, null).execute(loginRequest);

	}

	private Handler recentHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processRecentVideoResults((DataResult<String>) msg.obj);
		}
	};

	private void processRecentVideoResults(DataResult<String> obj) {
		try{
			Videos mRecentVideo = null;
			for (Videos videos : videosList) {
				if(mRecentVideoId.equalsIgnoreCase(videos.videoId)){
					mRecentVideo = videos;
					videos.isRecent = true;
				}

			}

		}catch (Exception e) {
			Log.e(TAG,"Exception :: processRecentVideoResults"+e);
		}
	}

	private class ProcesYouTubeTask extends AsyncTask<YouTubeId, String, Uri>{
		String lUriStr = null;
		String lYouTubeFmtQuality = "18";//for high-quality , set to 17 for low quality
		String lYouTubeVideoId = null;
		@Override
		protected Uri doInBackground(YouTubeId... pParams) {

			if(pParams[0] instanceof VideoId){
				lYouTubeVideoId = pParams[0].getId();
			}
			mVideoId = lYouTubeVideoId;

			try {
				lUriStr = YouTubeUtility.calculateYouTubeUrl(lYouTubeFmtQuality, true, mVideoId);
			} catch (ClientProtocolException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				return null;
			} catch (IOException e) {
				return null;
			}catch (Exception e) {
				return null;
			}
			if(lUriStr != null){
				return Uri.parse(lUriStr);
			} else {
				return null;
			}
		}
		@Override
		protected void onPostExecute(Uri result) {
			super.onPostExecute(result);
			if(result != null)
				playVideo(result);
			else
				postLoggingOnVideoPlayerError();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		mProgressBar.setVisibility(View.GONE);
		//		mVideoSeekBar.setMax(mVideoView.getDuration());
		startVideoTimer();
		mVideoIsStarted = true;
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 0;
		videoInfo.video_id = mVideoId;
		videoInfo.watched_time = 0;
		//		videoInfo.isComplete = false;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		postCustomLogging(videoInfo);

	}

	public void performVideoPlay() {
		if(mVideoView.isPlaying()){
			mVideoView.pause();
		}

	}

	public void stopVideoPlay(){
		MLogger.logInfo(TAG, "is video playing :: "+mVideoView.isPlaying());
		if(mVideoView.isPlaying()){
			mVideoView.stopPlayback();
		}
		if(videoTimer != null){
			videoTimer.cancel();
		}
		
		postLoggingOnBackButtonPress();
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	private void postCustomLogging(VideoInfo videoInfo) {
		CustomLogginRequest  customLogginRequest = new CustomLogginRequest();
		customLogginRequest.requestDelegate = new MFlixJsonBuilder();
		customLogginRequest.token = mToken;
		customLogginRequest.videoInfo = videoInfo;
		customLogginRequest.requestType =  WebRequestType.POST_VIDEO_LOGGING;
		new DataApiAsyncTask(true, this, loggingHandler, null).execute(customLogginRequest);
	}

	private Handler loggingHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processLoggingResults((DataResult<String>) msg.obj);
		}
	};

	private void processLoggingResults(DataResult<String> obj) {
		if(obj.entity != null && obj.entity != null ){
			try{
				String loggingResult = obj.entity;
			}catch (Exception e) {
				Log.e(TAG,"Exception :: processRecentVideoResults"+e);
			}
		}
	}


	@Override
	public void onVideoPlay() {
		mVideoIsStarted = true;

	}

	@Override
	public void onVideoPause() {
		if(!mIsBackbtnPressed)
			mTransparentLayout.setVisibility(View.VISIBLE);
		mIsVideoPaused = true;
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 1;
		videoInfo.video_id = mVideoId;
		//		videoInfo.isComplete = false;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		if(mVideoPausedPosition > 0)
			videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000 - mVideoPausedPosition);
		else
			videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000);

		mVideoPausedPosition = (mVideoView.getCurrentPosition()/1000);

		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		if(videoInfo.watched_time > 0 && mVideoIsStarted)
			postCustomLogging(videoInfo);

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		postLoggingOnVideoPlayerError();

		return true;
	}


	private void postLoggingOnVideoPlayerError() {
		mVideoIsStarted = false;
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 4;
		videoInfo.video_id = mVideoId;
		//		videoInfo.isComplete = false;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		videoInfo.watched_time =0;
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		mVideoPausedPosition = 0;
		postCustomLogging(videoInfo);
		Toast.makeText(this, R.string.video_play_error_msg, Toast.LENGTH_SHORT).show();
		selecetNextVideoToPlay();

	}


	@Override
	public void onCompletion(MediaPlayer arg0) {
		mVideoIsStarted = false;
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 2;
		videoInfo.video_id = mVideoId;
		int totalTimeWatched = 0;
		if(mVideoPausedPosition > 0){
			totalTimeWatched = (mVideoView.getDuration()/1000 )-mVideoPausedPosition;
		}else{
			totalTimeWatched  = mVideoView.getDuration()/1000;
		}
		mVideoPausedPosition = 0;
		videoInfo.watched_time = totalTimeWatched;
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		//		videoInfo.isComplete = true;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		postCustomLogging(videoInfo);
		selecetNextVideoToPlay();
	}

	private void postLoggingOnBackButtonPress() {
		if(mVideoIsStarted && mIsBackbtnPressed  ){
			MLogger.logInfo(TAG, "onBack :: video is playing ::"+mVideoView.isPlaying());
			if(mVideoView.isPlaying()){
				onVideoPause();
			}
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 3;
			videoInfo.video_id = mVideoId;
			//		videoInfo.isComplete = false;
			videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
			if(mVideoPausedPosition > 0)
				videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000) - mVideoPausedPosition;
			else
				videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000);

			if(videoInfo.watched_time <= 0){
				videoInfo.watched_time = 0;
			}
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			postCustomLogging(videoInfo);
		}else{
			if(mVideoView.isPlaying()){
				mVideoView.stopPlayback();
			}
		}
		mIsBackbtnPressed = false;
		mVideoIsStarted = false;
	}


	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
	    postLoggingOnBackButtonPress();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("recentVideoId",mRecentVideoId);
		setResult(RESULT_OK,returnIntent);
		finish();

		super.onBackPressed();
	}

	/*
	@Override
	public void onItemClick(
			it.sephiroth.android.library.widget.AdapterView<?> parent,
			View view, int position, long id) {
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 3;
		videoInfo.video_id = mVideoId;
		if(mVideoPausedPosition > 0)
			videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000) - mVideoPausedPosition;
		else
			videoInfo.watched_time = (mVideoView.getCurrentPosition()/1000);
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		videoInfo.isComplete = false;
		postCustomLogging(videoInfo);
		mVideoPausedPosition = 0;
		mSelectedPostion = position;
		mVideoId = videosList.get(position).id;
		new ProcesYouTubeTask().execute(new VideoId(mVideoId));
	}*/


	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mConnReceiver,  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mVideoIsStarted && mVideoView.isPlaying() && !mIsBackbtnPressed){
			mVideoView.pause();
		}
		unregisterReceiver(mConnReceiver);
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(!currentNetworkInfo.isConnected()){
				handlePlayAndPause();
			}else{
				if(!mVideoView.isPlaying()){
					mVideoView.start();
				}
			}
		}
	};

	public void postFavouriteVideos(Videos video, boolean isFavorite) {
		FavourireVideoRequest  favVideoRequest = new FavourireVideoRequest();
		favVideoRequest.token =((MagikFlix)getApplicationContext()).getToken();
		favVideoRequest.videoId =video.videoId;
		favVideoRequest.isFavorite = isFavorite;
		favVideoRequest.requestDelegate = new MFlixJsonBuilder();
		favVideoRequest.requestType =  WebRequestType.POST_FAV_VIDEOS;
		new DataApiAsyncTask(true, this, favouriteHandler, null).execute(favVideoRequest);
	}

	private Handler favouriteHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processFavouriteVideoResults((DataResult<String>) msg.obj);
		}
	};

	private void processFavouriteVideoResults(DataResult<String> obj) {
		if(obj.entity != null && obj.entity.length() > 0 ){
			try{
				if(obj.entity.contains("added")){
					updateVideo(true);
				}else{
					updateVideo(false);
				}
			}catch (Exception e) {
				Log.e(TAG,"Exception :: processFavouriteVideoResults"+e);
			}
		}
	}

	private void updateVideo(boolean isFavorite) {
		VideoResult videoResult = ((MagikFlix)getApplicationContext()).getVideoResult();
		for (Videos video : videoResult.videos) {
			if(video.videoId.equalsIgnoreCase(mVideoId)){
				video.isFavorite = isFavorite;
			}	
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(videoTimerTask != null)
			videoTimerTask.cancel();

	}

	private void getVimeoVideoURL(String videoId) {
		mProgressBar.setVisibility(View.VISIBLE);
		VimeoVideoRequest  vimeoVideoRequest = new VimeoVideoRequest();
		vimeoVideoRequest.videoId = videoId;
		vimeoVideoRequest.requestDelegate = new MFlixJsonBuilder();
		vimeoVideoRequest.requestType =  WebRequestType.GET_VIMEO_VIDEO;	
		new DataApiAsyncTask(true, this, vimeoVideoHandler, null).execute(vimeoVideoRequest);
	}

	private Handler vimeoVideoHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processVideoResults((DataResult<VimeoVideoResult>) msg.obj);
		}
	};

	private void processVideoResults(DataResult<VimeoVideoResult> obj) {
		try {
			VimeoVideoResult result = obj.entity;
			if(result != null ){
				String videoURL = null ;
				if(result.request.files.h264.hd != null){
					videoURL = obj.entity.request.files.h264.hd.url;
				}else if(result.request.files.h264.sd != null){
					videoURL = obj.entity.request.files.h264.sd.url;
				}else if(result.request.files.h264.mobile != null){
					videoURL = obj.entity.request.files.h264.mobile.url;
				}else if(result.request.files.hls != null){
					videoURL = obj.entity.request.files.hls.all;
				}
				mVideoView.setVideoPath(videoURL);
				mVideoView.requestFocus();
				mVideoView.start();
			}

		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception while playing vimeo video");
		}
	}

}
