package com.magikflix.kurio.app.activities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
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
import android.widget.TextView;
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
import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.app.api.MFlixJsonBuilder;
import com.magikflix.kurio.app.api.MFlixJsonBuilder.WebRequestType;
import com.magikflix.kurio.app.api.requests.CustomLogginRequest;
import com.magikflix.kurio.app.api.requests.FavourireVideoRequest;
import com.magikflix.kurio.app.api.requests.RecentVideoRequest;
import com.magikflix.kurio.app.api.results.VideoInfo;
import com.magikflix.kurio.app.api.results.VideoResult;
import com.magikflix.kurio.app.api.results.Videos;
import com.magikflix.kurio.app.asyntasks.DataApiAsyncTask;
import com.magikflix.kurio.app.utils.Constants;

public class VideoPlayingActivity extends BaseActivity implements OnInitializedListener, PlayerStateChangeListener, PlaybackEventListener,OnClickListener{
	private static String TAG = VideoPlayingActivity.class.getName();
	private boolean showingFullScreen = false;
	private ArrayList<Videos> videosList;
	private String mVideoId;
	private ProgressBar mProgressBar;
	private Timer videoTimer;
	private int mSelectedPostion = -1;
	public static final String SCHEME_YOUTUBE_VIDEO = "ytv";
	public static final String SCHEME_YOUTUBE_PLAYLIST = "ytpl";
	protected TextView    mProgressMessage;
	private String mToken,mCategoryName;
	private String mRecentVideoId = Constants.EMPTY_STRING;
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	private YouTubePlayerView myYouTubePlayerView;
	private YouTubePlayer YPlayer;
	private boolean mIsVideoPaused = false;
	private int mVideoPausePostion = 0;
	private RelativeLayout mTransparentLayout;
	private ImageView mPlayIV,mBackToGalleryIV,mFavIV;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_playing_screen);
		init();
		setIdsToViews();
		getVideoLink();
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

	}

	private void getVideoLink() {
		Bundle bundle = getIntent().getExtras();
		videosList  = (ArrayList<Videos>) bundle.getSerializable("videosList");
		mCategoryName = bundle.getString("categeoryName", "");
		mVideoId = bundle.getString("videoId");
		mSelectedPostion = bundle.getInt("selectedPosition", 0);

		System.out.println("Selected position :: onCreate---"+mSelectedPostion);
		mToken = ((MagikFlix)getApplicationContext()).getToken();
		//		mVideoListView .setAdapter(new VideoPlayBackAdapter(this,videosList));
		//		mVideoListView.setSelection(mSelectedPostion);
		myYouTubePlayerView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, this);
		//		VideoId lYouTubeId = new VideoId(mVideoId);
		//		new ProcesYouTubeTask().execute(lYouTubeId);

	}

	private void selecetNextVideoToPlay(){
		System.out.println("videosList.size()---"+videosList.size());

		if (mSelectedPostion < (videosList.size()-1)){
			mSelectedPostion = mSelectedPostion + 1;
			mVideoId = videosList.get(mSelectedPostion).videoId;
			System.out.println("Selected position :: increment---"+mSelectedPostion);

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
			TimerTask videoTimerTask = new TimerTask()
			{
				int videoPlayedSecs = 0;
				public void run(){

					try{
						videoPlayedSecs++;
						if(videoPlayedSecs == 10){
							postRecentVideos(mVideoId);
						}
					}
					catch (Exception e){
						// L.fe(activity,"", "startVideoTimer : Exception " + e.getMessage());
					}

				}
			};
			videoTimer.schedule(videoTimerTask, delayTime, period);
		}
		catch (Exception e)
		{}
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
		//		if(obj.entity != null && obj.entity != null ){
		try{
			//				System.out.println("Recent Result ::---->"+obj.entity);
			Videos mRecentVideo = null;
			for (Videos videos : videosList) {
				if(mRecentVideoId.equalsIgnoreCase(videos.videoId)){
					mRecentVideo = videos;
					videos.isRecent = true;
				}

			}
			//			HomeFragment homeFragment = ((MainActivity)getActivity()).getHomeFragment();
			//			homeFragment.updateRecentVideosAdapter(mRecentVideo);

		}catch (Exception e) {
			Log.e(TAG,"Exception :: processRecentVideoResults"+e);
		}


		//		}
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

		System.out.println("======ON Error======");
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 4;
		videoInfo.video_id = mVideoId;
		videoInfo.watched_time = 0;
		videoInfo.isComplete = false;
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
		System.out.println("======ON ENDED======");
		if(YPlayer != null){
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 2;
			videoInfo.video_id = mVideoId;
			videoInfo.isComplete = true;
			System.out.println("paused position--->"+mVideoPausePostion);
			System.out.println("current position-->"+(YPlayer.getDurationMillis()/1000 ));
			int totalTimeWatched=0;
			if(mVideoPausePostion > 0){
				totalTimeWatched = (YPlayer.getDurationMillis()/1000 )-mVideoPausePostion;
			}else{
				totalTimeWatched  = (YPlayer.getDurationMillis()/1000 );
			}
			mVideoPausePostion = 0;

			videoInfo.watched_time = totalTimeWatched;
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			postCustomLogging(videoInfo);
		}
		selecetNextVideoToPlay();


	}

	@Override
	public void onVideoStarted() {
		System.out.println("======ON STARTED======");
		startVideoTimer();
		Timestamp timeStamp = new Timestamp(new Date().getTime());
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.type = 0;
		videoInfo.video_id = mVideoId;
		videoInfo.watched_time = 0;
		videoInfo.isComplete = false;
		videoInfo.time_stamp = (timeStamp.getTime()/1000);
		postCustomLogging(videoInfo);

	}



	@Override
	public void onBuffering(boolean arg0) {
	}

	@Override
	public void onPaused() {
		mIsVideoPaused = true;
		System.out.println("======ON PAUSED======");
		if(YPlayer != null){
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 1;
			videoInfo.video_id = mVideoId;
			System.out.println("paused position ::--->"+mVideoPausePostion);
			if(mVideoPausePostion > 0)
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000 - mVideoPausePostion);
			else
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000);

			mVideoPausePostion = (YPlayer.getCurrentTimeMillis()/1000);

			System.out.println("current position--->"+mVideoPausePostion);

			System.out.println("Watched Time--->"+videoInfo.watched_time);

			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			videoInfo.isComplete = false;
			postCustomLogging(videoInfo);
		}

	}

	@Override
	public void onPlaying() {
		if(mIsVideoPaused){
			System.out.println("======ON Started after pause======");
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 0;
			videoInfo.video_id = mVideoId;
			videoInfo.watched_time = 0;
			videoInfo.isComplete = false;
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			postCustomLogging(videoInfo);

			mIsVideoPaused = false;
		}

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
				String loggingResult = obj.entity;
			}catch (Exception e) {
				Log.e(TAG,"Exception :: processRecentVideoResults"+e);
			}
		}
	}

	@Override
	public void onStop() {
		System.out.println("======ON STOP :: BACKPRESS======");
		super.onStop();

		if(YPlayer != null){//onBackPress,this will be called
			Timestamp timeStamp = new Timestamp(new Date().getTime());
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.type = 3;
			videoInfo.video_id = mVideoId;
			System.out.println("paused position--->"+mVideoPausePostion);
			System.out.println("current position-->"+(YPlayer.getCurrentTimeMillis()/1000));
			if(mVideoPausePostion > 0)
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000) - mVideoPausePostion;
			else
				videoInfo.watched_time = (YPlayer.getCurrentTimeMillis()/1000);
			videoInfo.time_stamp = (timeStamp.getTime()/1000);
			videoInfo.isComplete = false;
			postCustomLogging(videoInfo);
		}
	}

	@Override
	public void onBackPressed() {
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
			System.out.println("on click :: youtube");
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
				mTransparentLayout.setVisibility(View.GONE);
				YPlayer.play();
			}
			break;
		case R.id.back_to_main_gallery_btn:
			this.finish();
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
	}

}
