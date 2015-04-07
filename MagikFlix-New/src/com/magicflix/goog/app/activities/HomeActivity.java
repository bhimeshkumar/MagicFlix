package com.magicflix.goog.app.activities;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener;
import it.sephiroth.android.library.widget.HListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.localytics.android.LocalyticsAmpSession;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.AbstractDataRequest;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.adapters.CategoryAdapter;
import com.magicflix.goog.app.adapters.MovieAdapter;
import com.magicflix.goog.app.adapters.PlayListAdapter;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.Model.UserProfile;
import com.magicflix.goog.app.api.requests.AddSubscriptionrequest;
import com.magicflix.goog.app.api.requests.PromoCodeRequest;
import com.magicflix.goog.app.api.requests.RedeemCodeRequest;
import com.magicflix.goog.app.api.requests.SubscriptionModel;
import com.magicflix.goog.app.api.requests.VideoRequest;
import com.magicflix.goog.app.api.results.AddSubscriptionResult;
import com.magicflix.goog.app.api.results.PayLoad;
import com.magicflix.goog.app.api.results.Playlists;
import com.magicflix.goog.app.api.results.PromoCodeResult;
import com.magicflix.goog.app.api.results.SubscriptionResult;
import com.magicflix.goog.app.api.results.VideoResult;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.app.db.Db4oHelper;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.utils.Utils;
import com.magicflix.goog.broadcasts.FavouriteChangeListner;
import com.magicflix.goog.subscription.IabHelper;
import com.magicflix.goog.subscription.IabResult;
import com.magicflix.goog.subscription.Purchase;
import com.magicflix.goog.utils.CompatibilityUtil;
import com.magicflix.goog.utils.MLogger;
import com.magicflix.goog.utils.NetworkConnection;

public class HomeActivity extends BaseActivity implements OnItemSelectedListener, OnItemClickListener,  OnClickListener, android.widget.AdapterView.OnItemClickListener {

	private static String TAG = HomeActivity.class.getName();
	private FancyCoverFlow fancyCoverFlow;
	private HListView mCategoryListView;
	private HListView mPlayListListView;
	private Playlists[] mCategoryPlayLists;
	private ProgressBar mProgressBar;
	private ArrayList<Videos> videosList;
	private ArrayList<Videos> movieVideos;
	private String mRecentVideoId = null;
	private ArrayList<Videos> filteredVideoList;
	private ActionBar mActionBar;
	private RelativeLayout mSettingsLayout, mProfileLayout;
	private ImageView mActionBarPriofileIV;
	private TextView mCategoryNameTv , mNoVideoFoundTV, noNetworkTV;
	private FavouriteChangeListner mFavouriteChangeListner;
	private IntentFilter mFavIntentFilter; 
	private LocalyticsAmpSession mLocalyticsSession; 
	private Map<String, String> mLocaliticsAttributes ;
	private String mSelectedCategoryName;
	private String mPrice;
	private MagikFlix mApplication;
	private AppConuntDownTimer countDownTimer;
	private PopupWindow mAppTimerPopupWindow;

	//susbcsription 

	private IabHelper mHelper;
	private static final String PRODUCT = "com.magicflix.monthly.auto.test"; //TODO : Change to actual Product ID 
	private static final String FREE_PRODUCT = "com.magicflix.monthly.auto.freemonth.test";
	private IInAppBillingService mService;
	private boolean mIsUserSubscribed ;
	private  PopupWindow mSubscriptionPopUp , mCustomPopUp; Dialog trailDialog ;
	private  EditText promoCodeEt;
	private Button mTimerValueTV;
	private Button mActionBarSubscribeBtn ,mActionBarAppTimerBtn;
	private boolean mIsDataLoading = false;
	private VideoView mVideoView;

	private AsyncTask<AbstractDataRequest, Void, DataResult<?>> mVideoAsycTask;
	private UserProfile mUserProfile;
	private RelativeLayout mPlayListLayout;
	private Db4oHelper mDb4oProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		init();
		setIdsToViews();
		setListnersToViews();

		if(Constants.IS_SUBSCRIPTION_ENABLED){
			getUserSubscription();
		}else{
			mApplication.setVideosResult(null);
			getVideos(mApplication.getToken());
		}

		Bundle bundle = getIntent().getExtras();
		boolean isFromSplashScreen = false;
		if(bundle != null){
			isFromSplashScreen = bundle.getBoolean("isFromSplashScreen",false);
		}
		if(isFromSplashScreen){
			playVideo();
		}else{
			mVideoView.setVisibility(View.GONE);
		}
	}


	private void setUpProfileImage() {
		int selectedProfileIndex = mApplication.getSelectedProfileIndex();
		Db4oHelper db4oProvider  = new Db4oHelper(this);
		UserProfile userProfile = db4oProvider.getUserProfileById(selectedProfileIndex);
		if(userProfile != null && userProfile.avatar != null && userProfile.avatar.length() > 0){
			Bitmap avatarBitmap = BitmapFactory.decodeFile(userProfile.avatar);
			if(avatarBitmap != null){
				mActionBarPriofileIV.setImageBitmap(avatarBitmap);
			}else{
				setDefaulttImage(selectedProfileIndex);
			}
		}else{
			setDefaulttImage(selectedProfileIndex);
		}
	}


	private void setDefaulttImage(int selectedProfileIndex) {

		UserProfile userProfile = new Db4oHelper(this).getUserProfiles().get(mApplication.getSelectedProfileIndex());

		if(userProfile.avatar != null && userProfile.avatar.length() > 0){
			Bitmap myBitmap = BitmapFactory.decodeFile(userProfile.avatar);
			mActionBarPriofileIV.setImageBitmap(myBitmap);

		}else{
			mActionBarPriofileIV.setImageURI(Uri.parse(userProfile.deaultAvatar));
		}
	}


	private void playVideo() {
		mActionBar.hide();
		String path = "android.resource://" + getPackageName() + "/" + R.raw.splash_screen_video;
		mVideoView.setVideoURI(Uri.parse(path));
		mVideoView.start();
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mVideoView.setVisibility(View.GONE);
				mActionBar.show();

			}
		});

	}

	private void setUpUI() {
		if(Constants.IS_SUBSCRIPTION_ENABLED){
			mActionBarSubscribeBtn.setVisibility(View.VISIBLE);
			mTimerValueTV.setVisibility(View.VISIBLE);
		}else{
			mActionBarSubscribeBtn.setVisibility(View.GONE);
			mTimerValueTV.setVisibility(View.GONE);
		}
	}

	private void init() {
		try {
			mActionBar = setUpCustomActionBar(false);
		} catch (IOException e) {
			MLogger.logInfo(TAG, "Exception in setUpActionBar() :: "+e.getMessage());
		}

		mApplication = (MagikFlix)getApplication();
		mDb4oProvider = new Db4oHelper(this);
		if(mDb4oProvider.getUserProfiles().size() == 0){
			UserProfile userProfile = new UserProfile();
			userProfile.childName =  "";
			userProfile.id = 0;
			userProfile.age = String.valueOf(mApplication.getDefaultAge());
			userProfile.email = mApplication.getEmail();
			userProfile.deaultAvatar =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_blue;
			mDb4oProvider.store(userProfile);
			mApplication.setSelectedProfileIndex(0);
		}

		mUserProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());

		mLocalyticsSession = ((MagikFlix)getApplication()).getLocatyticsSession();
		mLocaliticsAttributes = new HashMap<String, String>();


		filteredVideoList = new ArrayList<Videos>();
		filteredVideoList.clear();
		mFavIntentFilter = new IntentFilter("com.magikflic.goog.FAVOURITE_CHANGE");
		mFavouriteChangeListner = new FavouriteChangeListner() {
			@Override
			protected void onFavoritedChange() {
				if(((MagikFlix)getApplicationContext()).getVideoResult() != null && mCategoryPlayLists !=null ){
					setPlayList(Constants.CATEGORY_SELECTED_POSITION);
				}
			}
		};

		if(Constants.IS_SUBSCRIPTION_ENABLED){
			Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
			serviceIntent.setPackage("com.android.vending");
			bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
		}
	}

	private void setListnersToViews() {
		mPlayListListView.setOnItemClickListener(this);
		mSettingsLayout.setOnClickListener(this);
		mProfileLayout.setOnClickListener(this);
		mActionBarSubscribeBtn.setOnClickListener(this);
		mActionBarAppTimerBtn.setOnClickListener(this);
		mActionBarPriofileIV.setOnClickListener(this);
		mTimerValueTV.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void setMovieAdapter() {
		movieVideos = new ArrayList<Videos>();
		movieVideos.clear();
		movieVideos = getRecommendedVideos();
		this.fancyCoverFlow.setAdapter(new MovieAdapter(this,movieVideos));
		this.fancyCoverFlow.setUnselectedAlpha(0.9f);
		this.fancyCoverFlow.setUnselectedScale(0.84f);
		this.fancyCoverFlow.setSpacing(-10);
		this.fancyCoverFlow.setMaxRotation(0);
		this.fancyCoverFlow.setScaleDownGravity(0.5f);
		fancyCoverFlow.setAnimationDuration(500);
		fancyCoverFlow.setOnItemClickListener(this);
		try {
			this.fancyCoverFlow.setSelection((movieVideos.size()) > 1 ? 1 :0, true);
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception :: "+e.getLocalizedMessage());
		}



	}

	private ArrayList<Videos>  getRecommendedVideos() {
		ArrayList<Videos> recommendedVideos = new ArrayList<Videos>();
		for (Playlists playlist : ((MagikFlix)getApplicationContext()).getVideoResult().playlists) {
			if(playlist.playlistType.equalsIgnoreCase("Recommended")){
				for (String videoId : playlist.videoIds) {
					for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
						if(videoId.equalsIgnoreCase(videos.videoId)){
							if((videos.minAge <= Integer.parseInt(mUserProfile.age)) && (videos.maxAge >= Integer.parseInt(mUserProfile.age)) ){
								recommendedVideos.add(videos);
							}
						}
					}
				}
			}
		}
		return recommendedVideos;
	}

	private void setIdsToViews() {
		mVideoView = (VideoView)findViewById(R.id.main_screen_video_view);
		mCategoryNameTv = (TextView)findViewById(R.id.category_name_tv);
		mCategoryListView = (HListView)findViewById(R.id.main_screen_category_list);
		mPlayListListView = (HListView)findViewById(R.id.main_screen_play_list);
		mProgressBar = (ProgressBar)findViewById(R.id.home_screen_pb);
		mSettingsLayout = (RelativeLayout)mActionBar.getCustomView().findViewById(R.id.actionBar_family_settings_layout);
		mProfileLayout = (RelativeLayout)mActionBar.getCustomView().findViewById(R.id.actionBar_profile_layout);
		mNoVideoFoundTV = (TextView)findViewById(R.id.main_screen_no_videos_tv);
		noNetworkTV  = (TextView)findViewById(R.id.main_screen_no_network_tv);
		mTimerValueTV = (Button)mActionBar.getCustomView().findViewById(R.id.actionBar_timer_value_tv);
		mActionBarSubscribeBtn = (Button)mActionBar.getCustomView().findViewById(R.id.actionBar_subscribe_btn);
		mActionBarAppTimerBtn = (Button)mActionBar.getCustomView().findViewById(R.id.actionBar_app_timer_btn);
		mActionBarPriofileIV =  (ImageView)mActionBar.getCustomView().findViewById(R.id.actionBar_profile_iv);
		if(CompatibilityUtil.isTablet(this)){
			this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.mainActivity_movie_list);
		}
		mPlayListLayout = (RelativeLayout)findViewById(R.id.play_list_layout);
	}


	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> parent,View view, int position, long id) {
		mLocaliticsAttributes.clear();
		mLocaliticsAttributes.put(Constants.CATEGORY, mSelectedCategoryName);
		mLocaliticsAttributes.put(Constants.INDEX, String.valueOf(position));
		mLocaliticsAttributes.put(Constants.VIDEO_ID, videosList.get(position).videoId);
		mLocalyticsSession.tagEvent(Constants.CATEGORY_VIDEO, mLocaliticsAttributes);
		checkSubscriptionAndNavigate(position,videosList);
	}

	private void checkSubscriptionAndNavigate(int position, ArrayList<Videos> videoList) {
		MagikFlix app = ((MagikFlix)getApplicationContext());
		if(Constants.IS_SUBSCRIPTION_ENABLED){
			if(mIsUserSubscribed || !(app.isTrialPeriodExpired()) || app.isSubscriptionRestored())   // || videoResult.userParent.isPromotionSubscribed || videoResult.userParent.isSubscribed  || mIsSubscsriptionResored)
				checkYouTubAppAvailability(position,videoList);
			else{
				PopupWindow popupWindow = getTrialExpiredPopUp(getString(R.string.times_up_txt));
				popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
			}
		}else{
			checkYouTubAppAvailability(position,videoList);
		}
	}

	private void checkYouTubAppAvailability(int position, ArrayList<Videos> videosList) {
		String videoCategory = videosList.get(position).category;
		if(!(ConnectionResult.SERVICE_MISSING == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this))){
			String packageName = "com.google.android.youtube";
			boolean isYoutubeInstalled = isYouTubeAppInstalled(packageName);
			if(isYoutubeInstalled && !videoCategory.equalsIgnoreCase(getString(R.string.category_vimeo_title))){
				navigateToVideoPlayScreen(position, videosList);
			}else{
				navigateToVideoViewScreen(position, videosList,videoCategory);
			}
		}else{
			navigateToVideoViewScreen(position, videosList,videoCategory);
		}
	}

	protected boolean isYouTubeAppInstalled(String packageName) {
		Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
		if (mIntent != null) {
			return true;
		}
		else {
			return false;
		}
	}


	private void navigateToVideoPlayScreen(int position, ArrayList<Videos> videoList) {
		Intent intent = new Intent(this,VideoPlayingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("videosList", videoList);
		bundle.putString("videoId", videoList.get(position).videoId);
		bundle.putInt("selectedPosition", position);
		bundle.putString("videoLink", "https://www.youtube.com/watch?v="+videoList.get(position).videoId);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}

	private void navigateToVideoViewScreen(int position, ArrayList<Videos> videoList, String videoCategory) {
		Intent intent = new Intent(this,VideoViewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("videosList", videoList);
		bundle.putString("videoId", videoList.get(position).videoId);
		bundle.putString("categoryName", videoCategory);
		bundle.putInt("selectedPosition", position);
		bundle.putString("videoLink", "https://www.youtube.com/watch?v="+videoList.get(position).videoId);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				mRecentVideoId =data.getStringExtra("recentVideoId");
				getRecentPlayList("Recent Videos");
			}
		}else if (requestCode == 10001) {           
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			if (resultCode == RESULT_OK) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					String product = jo.getString("productId");
					String token = jo.getString("purchaseToken");
					//					String productType = jo.getString("productType");
					//String purchaseInfo = "Product ID :: "+sku + "\n"+"Token :: "+token +"\n" ;
					//showAlertDialog(purchaseInfo);
					if(token != null && token.length() > 0){
						addSubscription(token,product);
					}	
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getVideos(String token) {
		mIsDataLoading = true;
		VideoRequest  loginRequest = new VideoRequest();
		loginRequest.appid = getPackageName();
		loginRequest.token = token;
		loginRequest.appversion = Utils.getAppVersion(this);
		loginRequest.requestDelegate = new MFlixJsonBuilder();
		loginRequest.requestType =  WebRequestType.GET_VIDEOS;	
		mVideoAsycTask = new DataApiAsyncTask(true, this, videosHandler, null).execute(loginRequest);
	}

	private Handler videosHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processVideoResults((DataResult<VideoResult>) msg.obj);
		}
	};

	private void processVideoResults(DataResult<VideoResult> obj) {
		if(obj.entity != null ){
			mPlayListLayout.setVisibility(View.VISIBLE);
			mCategoryNameTv.setVisibility(View.VISIBLE);
			VideoResult videoResult = obj.entity;
			mApplication.setVideosResult(videoResult);
			setUpUI(videoResult);
		}else{
			mIsDataLoading = false;
			((MagikFlix)getApplicationContext()).setVideosResult(null);
		}
	}

	private void setUpUI(VideoResult videoResult) {
		mSettingsLayout.setVisibility(View.VISIBLE);
		mProfileLayout.setVisibility(View.VISIBLE);
		Playlists[] playLists = videoResult.playlists;
		ArrayList<Playlists> categoriesList = new ArrayList<Playlists>();

		for (Playlists playList : playLists) {
			if(playList.playlistType.equalsIgnoreCase(this.getResources().getString(R.string.category_header))){
				categoriesList.add(playList);
			}
		}
		mCategoryPlayLists = categoriesList.toArray(new Playlists[categoriesList.size()]);
		setCategoryAdapter();
		setFavoriteVideos();

		setUpUI();

		if(Constants.IS_SUBSCRIPTION_ENABLED){
			boolean isSubscriptionRestored = ((MagikFlix)getApplicationContext()).isSubscriptionRestored();
			if (mTimerValueTV != null){
				mTimerValueTV.setVisibility((mIsUserSubscribed || isSubscriptionRestored) ? View.GONE :View.VISIBLE);
			}
			if (mActionBarSubscribeBtn != null){
				mActionBarSubscribeBtn.setVisibility((mIsUserSubscribed || isSubscriptionRestored) ? View.GONE :View.VISIBLE);
			}

			if(!mIsUserSubscribed || !(((MagikFlix)getApplicationContext()).isSubscriptionRestored())){
				Constants.TIMERLIMIT = Integer.parseInt(videoResult.appConfig.Settings[6].SettingValue);
				MagikFlix app = (MagikFlix)getApplicationContext();
				app.setFreeTrailPeriod(Constants.TIMERLIMIT);
				if(app.isTrialPeriodExpired()){
					PopupWindow popupWindow = getTrialExpiredPopUp(getString(R.string.times_up_txt));
					popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
				}else{
					mTimerValueTV.setVisibility(View.VISIBLE);
					setFreeTrialValue();
					showCustomAlert("Enjoy your free trial of " +Constants.TIMERLIMIT+" min(s) today.");
				}
			}
		}
		runAppTimer();
	}

	private void setFreeTrialValue() {
		MagikFlix app = ((MagikFlix)getApplicationContext());
		if(mTimerValueTV != null && (app.getFreeTrailPeriod() > 0) ){
			if(app.isTrialPeriodExpired()){
				mTimerValueTV.setTextColor(Color.RED);
				mTimerValueTV.setText("0");
				return;
			}
			int videoPlayTime = app.getVideoPlayTime();
			if(videoPlayTime > 0 ){
				int remianingVideoPlayTime = (Constants.TIMERLIMIT - (videoPlayTime/60));
				mTimerValueTV.setText(String.valueOf(remianingVideoPlayTime));
				if(remianingVideoPlayTime <=0){
					mTimerValueTV.setTextColor(Color.RED);
				}
			}else{
				mTimerValueTV.setText(String.valueOf(app.getFreeTrailPeriod()));
			}
		}else{
			mTimerValueTV.setText(String.valueOf(Constants.TIMERLIMIT));
		}
	}

	private void setCategoryAdapter() {
		mCategoryListView.setAdapter(new CategoryAdapter(this,mCategoryPlayLists));
		setPlayList(3);
		if(CompatibilityUtil.isTablet(this)){
			setMovieAdapter();
		}
		mProgressBar.setVisibility(View.GONE);
	}

	public void setPlayList(int position) {
		videosList = new ArrayList<Videos>();
		videosList.clear();
		String categoryName = mCategoryPlayLists[position].name;
		Constants.CATEGORY_SELECTED_POSITION = position;
		mSelectedCategoryName = categoryName;
		mCategoryNameTv.setText(categoryName);
		if(categoryName.equalsIgnoreCase("Recent Videos")){
			videosList = getRecentPlayList(categoryName);
		}else if(categoryName.equalsIgnoreCase("Recommended Videos")){
			videosList = getRecommendedVideos();
		}else if(categoryName.equalsIgnoreCase("Favorite Videos")){
			videosList = getFavoriteVideos();
		}else{
			for (Videos video : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
				if(video.category.equalsIgnoreCase(categoryName)){
					if((video.minAge <= Integer.parseInt(mUserProfile.age)) && (video.maxAge >= Integer.parseInt(mUserProfile.age)) ){
						videosList.add(video);
					}
				}
			}
		}

		if(videosList.size() > 0){
			mNoVideoFoundTV.setVisibility(View.GONE);
			mPlayListListView.setVisibility(View.VISIBLE);
			mPlayListListView.setAdapter(new PlayListAdapter(this,videosList));
		}else{
			mPlayListListView.setVisibility(View.GONE);
			mNoVideoFoundTV.setVisibility(View.VISIBLE);
			mNoVideoFoundTV.setMovementMethod(LinkMovementMethod.getInstance());
			if(categoryName.contains("My Videos")){
				mNoVideoFoundTV.setText(R.string.my_videos_empty_list_msg);
			}else if(categoryName.contains("Favorite Videos")){
				mNoVideoFoundTV.setText(R.string.favorite_videos_empty_list_msg);
			}else{
				mNoVideoFoundTV.setText(R.string.info_no_videos_found);
			}
		}

	}

	private void setFavoriteVideos() {
		Constants.FAV_VIDEOS_LIST.clear();
		for (Playlists playlist : ((MagikFlix)getApplicationContext()).getVideoResult().playlists) {
			if(playlist.name.equalsIgnoreCase("Favorite Videos")){
				for (String videoId : playlist.videoIds) {
					for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
						if(videoId.equalsIgnoreCase(videos.videoId)){
							videos.isFavorite = true;
						}
					}
				}
			}
		}
	}

	private ArrayList<Videos> getFavoriteVideos(){
		ArrayList<Videos> favVideosList = new ArrayList<Videos>();
		for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
			if(videos.isFavorite){
				videos.isFavorite = true;
				if((videos.minAge <= Integer.parseInt(mUserProfile.age)) && (videos.maxAge >= Integer.parseInt(mUserProfile.age)) ){
					favVideosList.add(videos);
				}
			}
		}
		HashSet<Videos> hashSet = new HashSet<Videos>(favVideosList);
		return new ArrayList<Videos>(hashSet);
	}

	private ArrayList<Videos> getRecentPlayList(String categoryName) {
		try {
			for (Playlists playlist : ((MagikFlix)getApplicationContext()).getVideoResult().playlists) {
				if(playlist.name.equalsIgnoreCase(categoryName)){
					for (String videoId : playlist.videoIds) {
						for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
							if(videoId.equalsIgnoreCase(videos.videoId)){
								if((videos.minAge <= Integer.parseInt(mUserProfile.age)) && (videos.maxAge >= Integer.parseInt(mUserProfile.age)) ){
									filteredVideoList.add(videos);
								}
							}
						}
					}
				}
			}
			Videos recentVideo = null;
			for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
				if(((mRecentVideoId != null && mRecentVideoId.length() > 0) && mRecentVideoId.equalsIgnoreCase(videos.videoId))){
					recentVideo = videos;
				}
			}
			if(recentVideo != null)
				filteredVideoList.add(recentVideo);

			HashSet<Videos> hashSet = new HashSet<Videos>(filteredVideoList);
			return new ArrayList<Videos>(hashSet);
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in getRecentPlayList() ");
			return null;
		}

	}

	@Override
	public void onItemClick(android.widget.AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		mLocaliticsAttributes.clear();
		mLocaliticsAttributes.put(Constants.INDEX, String.valueOf(position));
		mLocaliticsAttributes.put(Constants.VIDEO_ID, movieVideos.get(position).videoId);
		mLocalyticsSession.tagEvent(Constants.RECOMMENDED_VIDEO, mLocaliticsAttributes);
		checkSubscriptionAndNavigate(position,movieVideos);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.actionBar_family_settings_layout:
			startActivity(new Intent(this,OnBoardingScreen.class));
			break;
		case R.id.actionBar_subscribe_btn:
			showSubscriptionPopUp();
			break;
		case R.id.subscription_popup_promo_code_cancel_btn:
			if(mSubscriptionPopUp != null){
				mSubscriptionPopUp.dismiss();
			}
			break;
		case R.id.subscription_popup_promo_code_buy_btn:
			if(mSubscriptionPopUp != null){
				mSubscriptionPopUp.dismiss();
			}
			initIBapHelper(PRODUCT);
			break;
		case R.id.actionBar_timer_value_tv:
			//startActivity(new Intent(this,SettingsActivity.class));
			break;
		case R.id.actionBar_app_timer_btn:
			startActivity(new Intent(this,SettingsActivity.class));
			break;
		case R.id.actionBar_profile_iv:
			startActivity(new Intent(this,ProfileSelectionActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if(mVideoAsycTask != null){
			mVideoAsycTask.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			registerReceiver(mFavouriteChangeListner, mFavIntentFilter);
			registerReceiver(mConnReceiver,  new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

			if(Constants.IS_SUBSCRIPTION_ENABLED)
				setFreeTrialValue();

			dismissTimerPopUp();
			if(Constants.TIMER_LIMIT_UPDATED){
				Constants.TIMER_LIMIT_UPDATED = false;
				runAppTimer();
			}

			setUpProfileImage();
			doAgeFilter();

		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in onResume() :: "+e.getMessage());
		}
	}

	private void doAgeFilter() {
		if(Constants.IS_PROFILE_SELECTION_CHANGED){
			mUserProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());
			setPlayList(Constants.CATEGORY_SELECTED_POSITION);
			Constants.IS_PROFILE_SELECTION_CHANGED = false;

		}

	}


	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mConnReceiver);
	}

	/*private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(!currentNetworkInfo.isConnected()){
				mProgressBar.setVisibility(View.GONE);
				if(((MagikFlix)getApplicationContext()).getVideoResult() == null)
					noNetworkTV.setVisibility(View.VISIBLE);
			}else{
				noNetworkTV.setVisibility(View.GONE);
				if(((MagikFlix)getApplicationContext()).getVideoResult() == null && !mIsDataLoading){
					mProgressBar.setVisibility(View.VISIBLE);
					if(Constants.IS_SUBSCRIPTION_ENABLED){
						getUserSubscription();
					}else{
						getVideos(((MagikFlix)getApplicationContext()).getToken());
					}
				}
			}
		}
	};*/

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!NetworkConnection.isConnected(context)){
				mProgressBar.setVisibility(View.GONE);
				if(((MagikFlix)getApplicationContext()).getVideoResult() == null)
					noNetworkTV.setVisibility(View.VISIBLE);
				else
					noNetworkTV.setVisibility(View.GONE);
			}else{
				noNetworkTV.setVisibility(View.GONE);
				if(((MagikFlix)getApplicationContext()).getVideoResult() == null && !mIsDataLoading){
					mProgressBar.setVisibility(View.VISIBLE);
					if(Constants.IS_SUBSCRIPTION_ENABLED){
						getUserSubscription();
					}else{
						getVideos(((MagikFlix)getApplicationContext()).getToken());
					}
				}
			}

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(countDownTimer != null ){
				countDownTimer.cancel();
			}
			unregisterReceiver(mFavouriteChangeListner);
			if (mService != null) {
				unbindService(mServiceConn);
			} 
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in onDestroy() :: "+e.getMessage());
		}
	}


	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		@Override
		public void onServiceConnected(ComponentName name, 
				IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
			new GetProdcutDetails().execute();
		}
	};

	private void getUserSubscription() {
		AddSubscriptionrequest  request = new AddSubscriptionrequest();
		request.token = ((MagikFlix)getApplicationContext()).getToken();
		request.requestDelegate = new MFlixJsonBuilder();
		request.requestType =  WebRequestType.GET_USER_SUBSCRIPTION;	
		new DataApiAsyncTask(true, this, userSubscriptionHandler, null).execute(request);
	}

	private Handler userSubscriptionHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processUserSubscriptionResults((DataResult<SubscriptionResult>) msg.obj);
		}
	};

	private void processUserSubscriptionResults(DataResult<SubscriptionResult> obj) {
		if(obj.entity != null ){
			boolean isSubscribed = obj.entity.isSubscribed;
			mIsUserSubscribed = isSubscribed;
			((MagikFlix)getApplicationContext()).setIsUserSubscribed(mIsUserSubscribed);
			getVideos(((MagikFlix)getApplicationContext()).getToken());
		}
	}

	private void showSubscriptionPopUp(){
		LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
		View popupView = layoutInflater.inflate(R.layout.subscribe_popup, null);  
		mSubscriptionPopUp = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,true);
		mSubscriptionPopUp.setContentView(popupView);
		promoCodeEt = (EditText)popupView.findViewById(R.id.subscription_popup_promo_code_et);
		TextView priceTV = (TextView)popupView.findViewById(R.id.subscription_price_tv);
		if(mPrice != null && priceTV.length() > 0)
			priceTV.setText(mPrice);
		Button cancelBtn = (Button)popupView.findViewById(R.id.subscription_popup_promo_code_cancel_btn);
		cancelBtn.setOnClickListener(this);
		Button applyBtn = (Button)popupView.findViewById(R.id.subscription_popup_promo_code_apply_btn);
		applyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String promoCode = promoCodeEt.getText().toString();
				if(promoCode.length() > 0){
					verifyPromoCode(promoCode);
				}else{
					showCustomAlert(getString(R.string.please_enter_promo_code_msg));
				}
			}
		});
		Button buyBtn = (Button)popupView.findViewById(R.id.subscription_popup_promo_code_buy_btn);
		buyBtn.setOnClickListener(this);
		mSubscriptionPopUp.showAtLocation(popupView, Gravity.CENTER, 0, 0);

	}

	private void verifyPromoCode(String promoCode) {
		MagikFlix app = (MagikFlix)this.getApplicationContext();
		RedeemCodeRequest  request = new RedeemCodeRequest();
		request.token = app.getToken();
		request.promoCodeRequest = new PromoCodeRequest();
		request.promoCodeRequest.promoCode = promoCode;
		request.requestDelegate = new MFlixJsonBuilder();
		request.requestType =  WebRequestType.VERIFY_REDEEM;	
		new DataApiAsyncTask(true, this, promoCodeHandler, null).execute(request);

	}

	private Handler promoCodeHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processPromoCodeResults((DataResult<PromoCodeResult>) msg.obj);
		}
	};

	private void processPromoCodeResults(DataResult<PromoCodeResult> obj) {
		if(obj.entity != null ){
			PromoCodeResult proCodeResult = obj.entity;
			if(!(proCodeResult.isSuccess) && (proCodeResult.id != null) && (proCodeResult.id.length() > 0)){
				if(mSubscriptionPopUp != null){
					mSubscriptionPopUp.dismiss();
				}
				//				initIBapHelper(proCodeResult.id);
				initIBapHelper(FREE_PRODUCT);
			}else{
				showCustomAlert(obj.entity.message);
			}

		}
	}

	@SuppressLint("InflateParams") private void showCustomAlert(String title){
		if( mCustomPopUp != null && mCustomPopUp.isShowing()){
			return;
		}
		LayoutInflater inflater = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_alert,null);
		TextView titleTv = (TextView)layout.findViewById(R.id.custom_alert_title);
		titleTv.setText(title);
		mCustomPopUp = new PopupWindow(layout, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mCustomPopUp.setAnimationStyle(R.style.PopUpAnimation);
		mCustomPopUp.setFocusable(true);
		mCustomPopUp.setContentView(layout);
		mCustomPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
		mCustomPopUp.getContentView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCustomPopUp.dismiss();
			}
		});
	}

	public void initIBapHelper(final String productId){
		mHelper = new IabHelper(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh/ZIaQBmoGC1Rwu04RJhaNiCqFI/CNGgkH7oSBwcx2w8pDdrXMRz6g16hUhRWeDTnHBvmpPdgP9YSOrO/VpGEAI+tWY5aQKoyLlcQDrWa8r21KY4/HmTkKYr7JCDr5qQ6H6mipQbMZn1NPj2NzTcyEW3Qhr8SipD3ElVGIHmmENuRzeru9ul6MvaQ2ugVai/Nd9BiXnH8ZzCgKphkpK09J8gi7m+9xfeuQajz8t1q6pcx4FEAzw5fjh4IqpoxhdgWKCXj2kBxioI/he+U5mbDWd8P/BEg1DNWLEQhKoNvUsJgXc3sTZUgQ9kI5lx7wMOzrrqPRhFiCJ8tKWgtGLHmwIDAQAB");
		mHelper.startSetup(new 
				IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result){
				if (!result.isSuccess()) {
					MLogger.logError(TAG, "Error :: initIBapHelper");
				}else{  
					launchPurchaseFlow(productId);
				}
			}
		});
	}

	private void launchPurchaseFlow(String prodcutId){
		mHelper.launchSubscriptionPurchaseFlow(this, prodcutId, 10001,   
				mPurchaseFinishedListener, "mypurchasetoken");
		//		mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,   
		//				mPurchaseFinishedListener, "mypurchasetoken");
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
	= new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, 
				Purchase purchase) {
			if (result.isFailure()) {
				String errorMessage = result.getMessage();
				MLogger.logInfo(TAG, "Error ::OnIabPurchaseFinishedListener "+errorMessage);
				return;
			}      
		}

	};


	/*private void showAlertDialog(String message){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Purchase Info");
		builder1.setMessage(message);
		builder1.setCancelable(true);
		builder1.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}*/


	private void addSubscription(String receipt, String productId) {
		MagikFlix app = (MagikFlix)this.getApplicationContext();
		AddSubscriptionrequest  request = new AddSubscriptionrequest();
		request.token = app.getToken();
		request.subscriptionModel = new SubscriptionModel();
		request.subscriptionModel.isFree = 0;
		request.subscriptionModel.payload = new PayLoad();
		request.subscriptionModel.payload.packageName = getPackageName();
		request.subscriptionModel.payload.platform = Constants.PLATFORM;
		request.subscriptionModel.payload.token = receipt;
		request.subscriptionModel.payload.isIos6 = 0;
		request.subscriptionModel.product = productId;
		request.subscriptionModel.email = app.getEmail();
		request.subscriptionModel.isSubscription = true;
		request.requestDelegate = new MFlixJsonBuilder();
		request.requestType =  WebRequestType.ADD_SUBSCRIPTION;	
		new DataApiAsyncTask(true, this, addSubscriptionHandler, null).execute(request);

	}

	private Handler addSubscriptionHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processSubscriptionResults((DataResult<AddSubscriptionResult>) msg.obj);
		}
	};

	private void processSubscriptionResults(DataResult<AddSubscriptionResult> obj) {
		if(obj.entity != null ){
			mIsUserSubscribed = obj.entity.isSuccess;
			if(obj.entity.isSuccess){
				((MagikFlix)getApplicationContext()).setIsUserSubscribed(mIsUserSubscribed);
				mActionBarSubscribeBtn.setVisibility(View.GONE);
				mTimerValueTV.setVisibility(View.GONE);
				showShortToast(obj.entity.message);
			}
		}
	}

	class GetProdcutDetails extends AsyncTask<Void, Integer, Bundle>{

		protected Bundle doInBackground(Void...arg0) {
			return  getProdcutDetails();
		}
		protected void onPostExecute(Bundle result) {
			if(result != null){
				int response = result.getInt("RESPONSE_CODE");
				if (response == 0) {
					ArrayList<String> responseList = result.getStringArrayList("DETAILS_LIST");
					for (String thisResponse : responseList) {
						JSONObject object;
						try {
							object = new JSONObject(thisResponse);
							mPrice = object.getString("price");
						} catch (JSONException e) {
							MLogger.logInfo(TAG, "Error : getting prodcut details "+ e.getMessage());
						}
					}
				}
			}
		}
	}

	private Bundle getProdcutDetails(){
		Bundle skuDetails = null;
		ArrayList<String> skuList = new ArrayList<String> ();
		skuList.add(PRODUCT);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		try {
			skuDetails = mService.getSkuDetails(3, 
					getPackageName(), "subs", querySkus);
		} catch (RemoteException e) {
			MLogger.logError(TAG, "Exception in getProdcutDetails() :: "+ e.getMessage());
		}
		return skuDetails;
	}

	public class AppConuntDownTimer extends CountDownTimer{

		public AppConuntDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mActionBarAppTimerBtn.setTextColor(Color.RED);
			mActionBarAppTimerBtn.setText("0");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			setAppTimer(millisUntilFinished);
		}
	}


	private void setAppTimer(long millisUntilFinished) {
		int timerValue = (int) (millisUntilFinished / 1000);
		String timerString = String.valueOf((timerValue/60+1));
		mActionBarAppTimerBtn.setText(timerString);
		if(timerString.equalsIgnoreCase("0")){
			mActionBarAppTimerBtn.setTextColor(Color.RED);
		}else if(Integer.parseInt(timerString) < 5){
			mActionBarAppTimerBtn.setTextColor(HomeActivity.this.getResources().getColor(R.color.category_disable_color));
		}else if(Integer.parseInt(timerString) == 5){
			mAppTimerPopupWindow = getTrialExpiredPopUp(HomeActivity.this.getString(R.string.app_timer_msg));
			mAppTimerPopupWindow.showAtLocation(mAppTimerPopupWindow.getContentView(), Gravity.CENTER, 0, 0);
			mActionBarAppTimerBtn.setTextColor(HomeActivity.this.getResources().getColor(R.color.category_disable_color));
		}else{
			mActionBarAppTimerBtn.setTextColor(Color.WHITE);
		}
	}

	private void runAppTimer() {
		mActionBarAppTimerBtn.setVisibility(View.VISIBLE);
		int timerValue = Integer.valueOf(mApplication.getAppTimerValue());
		Constants.DEFAULT_APP_TIMER_LIMIT =  timerValue;
		handler.removeCallbacksAndMessages(null);
		Message message = new Message();
		message.what = 0;
		message.arg1 = (Constants.DEFAULT_APP_TIMER_LIMIT * 60 * 1000);
		handler.sendMessageDelayed(message, 1000);
	}
	private void updateTimer(Message msg) {
		int pending = msg.arg1 - (Constants.APP_TIMER_DELAY * 1000);
		Message m = new Message();
		m.what = 0;
		m.arg1 = pending;
		handler.sendMessageDelayed(m, (Constants.APP_TIMER_DELAY * 1000));
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					dismissTimerPopUp();
					int timerValue = (msg.arg1/60000);
					mActionBarAppTimerBtn.setText(String.valueOf(msg.arg1/60000));
					if (timerValue > 0){
						if(timerValue < 5){
							Constants.IS_APP_TIMER_SHOWN = true;
							dismissTimerPopUp();
							mActionBarAppTimerBtn.setTextColor(HomeActivity.this.getResources().getColor(R.color.category_disable_color));
							Intent intnet = new Intent(Constants.INTENT_APP_ALERT_DISMISS);
							sendBroadcast(intnet);
						}else if(timerValue == 5){
							Constants.APP_TIMER_VALUE = 5;
							Intent intnet = new Intent(Constants.INTENT_APP_TIMER_EXPIRED);
							sendBroadcast(intnet);
							String message ;
							if(Integer.parseInt(mApplication.getAppTimerValue()) == 5){
								message = HomeActivity.this.getString(R.string.app_timer_at_five_mins_msg);
							}else{
								message = HomeActivity.this.getString(R.string.app_timer_msg);
							}
							mAppTimerPopupWindow = getTrialExpiredPopUp(message);
							mAppTimerPopupWindow.showAtLocation(mAppTimerPopupWindow.getContentView(), Gravity.CENTER, 0, 0);
							mActionBarAppTimerBtn.setTextColor(HomeActivity.this.getResources().getColor(R.color.category_disable_color));
						}else{
							mActionBarAppTimerBtn.setTextColor(Color.WHITE);
						}

						updateTimer(msg);
					}else if(timerValue == 0){
						Constants.APP_TIMER_VALUE = 0;
						Intent intnet = new Intent(Constants.INTENT_APP_TIMER_EXPIRED);
						sendBroadcast(intnet);
						mActionBarAppTimerBtn.setText("0");
						mAppTimerPopupWindow = getTrialExpiredPopUp(getString(R.string.times_up_txt));
						mAppTimerPopupWindow.showAtLocation(mAppTimerPopupWindow.getContentView(), Gravity.CENTER, 0, 0);
						mActionBarAppTimerBtn.setTextColor(Color.RED);
						handler.removeCallbacksAndMessages(null);
					}

				} catch (Exception e) {
					MLogger.logInfo(TAG, "Exception :: Timer Handler "+ e.getMessage());
				}
				break;	
			default:
				break;
			}
		}
	};

	private void dismissTimerPopUp() {
		if(Constants.IS_APP_TIMER_SHOWN && mAppTimerPopupWindow != null && mAppTimerPopupWindow.isShowing()){
			mAppTimerPopupWindow.dismiss();
		}
	}

}