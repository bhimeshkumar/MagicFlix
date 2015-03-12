package com.magicflix.goog.app.activities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.CustomLogginRequest;
import com.magicflix.goog.app.api.requests.FavourireVideoRequest;
import com.magicflix.goog.app.api.requests.RecentVideoRequest;
import com.magicflix.goog.app.api.results.VideoInfo;
import com.magicflix.goog.app.api.results.VideoResult;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.utils.TrialExpiredBroadCastReceiver;
import com.magicflix.goog.utils.MLogger;

public class VideoPlayingActivity extends BaseActivity implements OnInitializedListener, PlayerStateChangeListener, PlaybackEventListener, OnClickListener{

	private static String TAG = VideoPlayingActivity.class.getName();
	private ArrayList<Videos> videosList;
	private String mVideoId;
	private ProgressBar mProgressBar;
	private Timer videoTimer;
	private int mSelectedPostion = -1;
	//	protected TextView mProgressMessage;
	private String mToken;
	private String mRecentVideoId = Constants.EMPTY_STRING;
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	private YouTubePlayerView myYouTubePlayerView;
	private YouTubePlayer YPlayer;
	private boolean mIsVideoPaused = false, mIsBackbtnPressed = false;
	private int mVideoPausePostion = 0;
	private RelativeLayout mTransparentLayout;
	private ImageView mPlayIV,mBackToGalleryIV,mFavIV;
	private TrialExpiredBroadCastReceiver mTrialExpiredBroadCastReceiver ;
	private IntentFilter mIntentFilter; 
	private TimerTask videoTimerTask ;
	private boolean mVideoIsStarted = false;

	private ArrayList<Integer> avgTimeVideoVatched = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_playing_screen);
		init();
		getVideoLink();
		setIdsToViews();
		setListnersToViews();
	}

	private void setListnersToViews() {
		myYouTubePlayerView.setOnClickListener(this);
		mTransparentLayout.setOnClickListener(this);
		mPlayIV.setOnClickListener(this);
		mBackToGalleryIV.setOnClickListener(this);
		mFavIV.setOnClickListener(this);
	}

	private void init() {
		getActionBar().hide();
		mTrialExpiredBroadCastReceiver = new TrialExpiredBroadCastReceiver() {
			@Override
			protected void onTrialExpired() {
				if(YPlayer != null){
					if(YPlayer.isPlaying()){
						mTransparentLayout.setVisibility(View.VISIBLE);
						YPlayer.pause();
					}
				}
				showTrialExpiredPopUp(getString(R.string.times_up_txt));
			}
		};
		mIntentFilter = new IntentFilter("com.magikflic.goog.TRIALEXPIRED");
	}

	/*private void showTrialExpiredDialog(String title,String message){
		AlertDialog.Builder timerBuilder = new AlertDialog.Builder(this);
		timerBuilder.setTitle(title);
		timerBuilder.setCancelable(false);
		timerBuilder.setMessage(message);
		timerBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				VideoPlayingActivity.this.finish();
			}
		});

		AlertDialog timerAlert = timerBuilder.create();
		timerAlert.show();
	}*/

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mTrialExpiredBroadCastReceiver, mIntentFilter);

	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mTrialExpiredBroadCastReceiver);
	}
	@SuppressWarnings("unchecked")
	private void getVideoLink() {
		Bundle bundle = getIntent().getExtras();
		videosList  = (ArrayList<Videos>) bundle.getSerializable("videosList");
		mVideoId = bundle.getString("videoId");
		mSelectedPostion = bundle.getInt("selectedPosition", 0);
		mToken = ((MagikFlix)getApplicationContext()).getToken();
	}

	private void selecetNextVideoToPlay(){
		if (mSelectedPostion < (videosList.size()-1)){
			mSelectedPostion = mSelectedPostion + 1;
			mVideoId = videosList.get(mSelectedPostion).videoId;
		}else{
			mSelectedPostion = 0;
			mVideoId = videosList.get(mSelectedPostion).videoId;
		}
		YPlayer.loadVideo(mVideoId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void setIdsToViews() {
		myYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		mProgressBar = (ProgressBar)findViewById(R.id.video_play_screen_pb);
		mTransparentLayout = (RelativeLayout)findViewById(R.id.transparent_root_layout);
		mPlayIV = (ImageView)findViewById(R.id.play_btn);
		mBackToGalleryIV = (ImageView)findViewById(R.id.back_to_main_gallery_btn);
		mFavIV = (ImageView)findViewById(R.id.favourite_btn);
		myYouTubePlayerView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, this);

		if(videosList.get(mSelectedPostion).isFavorite){
			mFavIV.setBackground(VideoPlayingActivity.this.getResources().getDrawable(R.drawable.icon_outline_color_selected));
		}else{
			mFavIV.setBackground(null);
		}
	}


	private void startVideoTimer(){
		try{
			if (null != videoTimer){
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
						if(Constants.IS_SUBSCRIPTION_ENABLED && !(app.isUserSubscribed())){
							if(YPlayer != null && YPlayer.isPlaying()){
								app.setVideoPlayTime(app.getVideoPlayTime()+1);
							}

							int trialPeriod = Constants.TIMERLIMIT;
							int trialPeriodinSecs = trialPeriod * 60;
							if(app.getVideoPlayTime() >= trialPeriodinSecs){
								app.setIsTrialPeriod(true);
								this.cancel();
								Intent intnet = new Intent("com.magikflic.goog.TRIALEXPIRED");
								sendBroadcast(intnet);
							}
						}
					}
					catch (Exception e){
						// L.fe(activity,"", "startVideoTimer : Exception " + e.getMessage());
					}

				}
			};
			videoTimer.schedule(videoTimerTask, delayTime, period);
		}
		catch (Exception e){
			Log.e(TAG, "Exception :: startVideoTimer");
		}
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


	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}
	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		mProgressBar.setVisibility(View.GONE);

		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this,RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(
					getString(
							R.string.youtube_player_initialization_error_msg),
							errorReason.toString());

			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}

	}
	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		mProgressBar.setVisibility(View.GONE);
		if (!wasRestored) {
			YPlayer = player;
			YPlayer.setPlayerStyle(PlayerStyle.CHROMELESS);
			YPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
			YPlayer.setFullscreen(true);
			YPlayer.setShowFullscreenButton(true);
			//			YPlayer.setOnFullscreenListener(this);
			/*
			 * Now that this variable YPlayer is global you can access it
			 * throughout the activity, and perform all the player actions like
			 * play, pause and seeking to a position by code.
			 */
			YPlayer.loadVideo(mVideoId);
			YPlayer.setPlaybackEventListener(this);
			YPlayer.setPlayerStateChangeListener(this);
		}

	}

	@Override
	public void onAdStarted() {

	}

	@Override
	public void onError(ErrorReason arg0) {
		mVideoIsStarted = false;
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 4;
		videoInfo.video_id = mVideoId;
		videoInfo.watched_time = 0;
		//		videoInfo.isComplete = false;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		postCustomLogging(videoInfo);

	}

	@Override
	public void onLoaded(String arg0) {

	}

	@Override
	public void onLoading() {

	}

	@Override
	public void onVideoEnded() {
		mVideoIsStarted = false;
		if(YPlayer != null){
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 2;
			videoInfo.video_id = mVideoId;
			//			videoInfo.isComplete = true;
			int totalTimeWatched=0;
			if(mVideoPausePostion > 0){
				totalTimeWatched = (YPlayer.getDurationMillis()/1000 )-mVideoPausePostion;
			}else{
				totalTimeWatched  = (YPlayer.getDurationMillis()/1000 )+1; // YPlayer is giving 1 sec less to total time so adding +1
			}

			mVideoPausePostion = 0;

			videoInfo.watched_time = totalTimeWatched;
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
			postCustomLogging(videoInfo);
		}
		selecetNextVideoToPlay();


	}

	@Override
	public void onVideoStarted() {
		startVideoTimer();
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 0;
		videoInfo.video_id = mVideoId;
		videoInfo.watched_time = 0;
		//		videoInfo.isComplete = false;
		videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		postCustomLogging(videoInfo);
		//mVideoIsStarted = true;

	}



	@Override
	public void onBuffering(boolean arg0) {
	}

	@Override
	public void onPaused() {
		if(!mIsBackbtnPressed)
			mTransparentLayout.setVisibility(View.VISIBLE);
		mIsVideoPaused = true;
		if(YPlayer != null){
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 1;
			videoInfo.video_id = mVideoId;
			if(mVideoPausePostion > 0)
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000 - mVideoPausePostion);
			else
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000);
			avgTimeVideoVatched.add(videoInfo.watched_time);

			mVideoPausePostion = (YPlayer.getCurrentTimeMillis()/1000);
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			//			videoInfo.isComplete = false;
			videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
			if(videoInfo.watched_time > 0 && mVideoIsStarted)
				postCustomLogging(videoInfo);
		}

	}

	@Override
	public void onPlaying() {
		mVideoIsStarted = true;

	}

	@Override
	public void onSeekTo(int arg0) {
	}



	@Override
	public void onStopped() {

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
				//String loggingResult = obj.entity;
			}catch (Exception e) {
				Log.e(TAG,"Exception :: processRecentVideoResults"+e);
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		sendExitLogs();
	}

	private void sendExitLogs() {
		MLogger.logInfo(TAG, "Video Started :: "+mVideoIsStarted +"\n" + "Back btn prssed :: "+mIsBackbtnPressed);
		if(mVideoIsStarted && mIsBackbtnPressed && YPlayer != null){//onBackPress,this will be called

			int avgTime = 0;
			if(YPlayer != null && YPlayer.isPlaying()){
				avgTimeVideoVatched.clear();
				avgTimeVideoVatched.add(YPlayer.getCurrentTimeMillis()/1000);
			}
			for (Integer watchedtime : avgTimeVideoVatched) {
				avgTime  = avgTime +watchedtime;

			}
			System.out.println("avg watched time ::: "+avgTime);


			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 3;
			videoInfo.video_id = mVideoId;
			if(mVideoPausePostion > 0)
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000) - mVideoPausePostion;
			else
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000);
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			//			videoInfo.isComplete = false;
			videoInfo.user_id = ((MagikFlix)getApplicationContext()).getUserId();
			postCustomLogging(videoInfo);
		}else{
			mIsBackbtnPressed = false;
		}
		mVideoIsStarted = false;
	}

	@Override
	public void onBackPressed() {

		mIsBackbtnPressed = true;
		Intent returnIntent = new Intent();
		returnIntent.putExtra("recentVideoId",mRecentVideoId);
		setResult(RESULT_OK,returnIntent);
		finish();

		super.onBackPressed();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.youtube_view:
			if(YPlayer != null){
				if(YPlayer.isPlaying()){
					mTransparentLayout.setVisibility(View.VISIBLE);
					YPlayer.pause();
				}
			}

			break;
		case R.id.transparent_root_layout:
			break;
		case R.id.play_btn:
			if(YPlayer != null){
				if(((MagikFlix)getApplicationContext()).isTrialPeriodExpired()){
					showTrialExpiredPopUp(getString(R.string.times_up_txt));
					return;
				}
				mTransparentLayout.setVisibility(View.GONE);
				YPlayer.play();
			}
			break;
		case R.id.back_to_main_gallery_btn:
			//			this.finish();
			onBackPressed();
			break;
		case R.id.favourite_btn:

			if(!videosList.get(mSelectedPostion).isFavorite){
				videosList.get(mSelectedPostion).isFavorite = true;
				mFavIV.setBackground(VideoPlayingActivity.this.getResources().getDrawable(R.drawable.icon_outline_color_selected));
			}else{
				videosList.get(mSelectedPostion).isFavorite = false;
				mFavIV.setBackground(null);
			}
			postFavouriteVideos(videosList.get(mSelectedPostion),videosList.get(mSelectedPostion).isFavorite);
			break;
		default:
			break;
		}
	}


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
		Intent intnet = new Intent("com.magikflic.goog.FAVOURITE_CHANGE");
		sendBroadcast(intnet);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(videoTimerTask != null)
			videoTimerTask.cancel();

	}

}
