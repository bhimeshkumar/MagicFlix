package com.magikflix.kurio.app.activities;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener;
import it.sephiroth.android.library.widget.HListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.app.adapters.CategoryAdapter;
import com.magikflix.kurio.app.adapters.MovieAdapter;
import com.magikflix.kurio.app.adapters.PlayListAdapter;
import com.magikflix.kurio.app.api.MFlixJsonBuilder;
import com.magikflix.kurio.app.api.MFlixJsonBuilder.WebRequestType;
import com.magikflix.kurio.app.api.requests.VideoRequest;
import com.magikflix.kurio.app.api.results.Playlists;
import com.magikflix.kurio.app.api.results.VideoResult;
import com.magikflix.kurio.app.api.results.Videos;
import com.magikflix.kurio.app.asyntasks.DataApiAsyncTask;
import com.magikflix.kurio.app.utils.Constants;

public class HomeActivity extends BaseActivity implements OnItemSelectedListener, OnItemClickListener,  OnClickListener, android.widget.AdapterView.OnItemClickListener {

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
	private ImageView mTermsOfUseIV ;
	private TextView mCategoryNameTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpActionBar();
		init();
		setIdsToViews();
		setListnersToViews();
		getVideos(((MagikFlix)getApplicationContext()).getToken());

	}

	private void init() {
		this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.mainActivity_movie_list);
		filteredVideoList = new ArrayList<Videos>();
		filteredVideoList.clear();
	}

	private void setUpActionBar() {
		try {
			mActionBar = setUpCustomActionBar(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setListnersToViews() {
		mPlayListListView.setOnItemClickListener(this);
		mTermsOfUseIV.setOnClickListener(this);
	}

	private void setMovieAdapter() {
		movieVideos = new ArrayList<Videos>();
		movieVideos.clear();
		movieVideos = getRecommendedVideos();
		this.fancyCoverFlow.setAdapter(new MovieAdapter(this,movieVideos));
		this.fancyCoverFlow.setUnselectedAlpha(0.9f);
		//		this.fancyCoverFlow.setUnselectedSaturation(0.8f);
		this.fancyCoverFlow.setUnselectedScale(0.84f);
		this.fancyCoverFlow.setSpacing(-10);
		this.fancyCoverFlow.setMaxRotation(0);
		this.fancyCoverFlow.setScaleDownGravity(0.5f);
		//		this.fancyCoverFlow.setActionDistance(FancyCoverFlow.DRAWING_CACHE_QUALITY_LOW);
		fancyCoverFlow.setAnimationDuration(500);
		fancyCoverFlow.setOnItemClickListener(this);
	}

	private ArrayList<Videos>  getRecommendedVideos() {
		ArrayList<Videos> recommendedVideos = new ArrayList<Videos>();
		for (Playlists playlist : ((MagikFlix)getApplicationContext()).getVideoResult().playlists) {
			if(playlist.playlistType.equalsIgnoreCase("Recommended")){
				for (String videoId : playlist.videoIds) {
					for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
						if(videoId.equalsIgnoreCase(videos.videoId)){
							recommendedVideos.add(videos);
						}
					}
				}
			}
		}
		return recommendedVideos;
	}

	private void setIdsToViews() {
		mCategoryNameTv = (TextView)findViewById(R.id.category_name_tv);
		mCategoryListView = (HListView)findViewById(R.id.main_screen_category_list);
		mPlayListListView = (HListView)findViewById(R.id.main_screen_play_list);
		mProgressBar = (ProgressBar)findViewById(R.id.home_screen_pb);
		mTermsOfUseIV = (ImageView)mActionBar.getCustomView().findViewById(R.id.actionBar_terms_of_use_iv);
	}


	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onItemClick(
			it.sephiroth.android.library.widget.AdapterView<?> parent,
			View view, int position, long id) {
		checkYouTubAppAvailability(position,videosList);

	}

	private void checkYouTubAppAvailability(int position, ArrayList<Videos> videosList) {
		if(!(ConnectionResult.SERVICE_MISSING == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this))){
			String packageName = "com.google.android.youtube";
			boolean isYoutubeInstalled = isYouTubeAppInstalled(packageName);
			if(isYoutubeInstalled){
				navigateToVideoPlayScreen(position, videosList);
			}else{
				navigateToVideoViewScreen(position, videosList);
			}
		}else{
			navigateToVideoViewScreen(position, videosList);
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

	private void navigateToVideoViewScreen(int position, ArrayList<Videos> videoList) {
		Intent intent = new Intent(this,VideoViewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("videosList", videoList);
		bundle.putString("videoId", videoList.get(position).videoId);
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
			
		}
	}


	private void getVideos(String token) {
		VideoRequest  loginRequest = new VideoRequest();
		loginRequest.appid = getPackageName();
		loginRequest.token = token;
		loginRequest.appversion = Constants.APP_VERSION;
		loginRequest.requestDelegate = new MFlixJsonBuilder();
		loginRequest.requestType =  WebRequestType.GET_VIDEOS;	
		new DataApiAsyncTask(true, this, videosHandler, null).execute(loginRequest);

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
			Playlists[] playLists = obj.entity.playlists;
			ArrayList<Playlists> categoriesList = new ArrayList<Playlists>();
			for (Playlists playList : playLists) {
				if(playList.playlistType.equalsIgnoreCase(this.getResources().getString(R.string.category_header))){
					categoriesList.add(playList);
				}

			}
			mCategoryPlayLists = categoriesList.toArray(new Playlists[categoriesList.size()]);
			
		    ((MagikFlix)getApplicationContext()).setVideosResult(obj.entity);
			setCategoryAdapter();
			setFavoriteVideos();
		}
	}

	private void setCategoryAdapter() {
		mCategoryListView.setAdapter(new CategoryAdapter(this,mCategoryPlayLists));
		setPlayList(3);
		setMovieAdapter();
		mProgressBar.setVisibility(View.GONE);

	}

	public void setPlayList(int position) {
		videosList = new ArrayList<Videos>();
		videosList.clear();
		String categoryName = mCategoryPlayLists[position].name;
		Constants.CATEGORY_SELECTED_POSITION = position;
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
					videosList.add(video);
				}
			}
		}

		mPlayListListView.setAdapter(new PlayListAdapter(this,videosList));
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
					favVideosList.add(videos);
				}
		}
		HashSet<Videos> hashSet = new HashSet<Videos>(favVideosList);
		return new ArrayList<Videos>(hashSet);
	}

	private ArrayList<Videos> getRecentPlayList(String categoryName) {
		for (Playlists playlist : ((MagikFlix)getApplicationContext()).getVideoResult().playlists) {
			if(playlist.name.equalsIgnoreCase(categoryName)){
				for (String videoId : playlist.videoIds) {
					for (Videos videos : ((MagikFlix)getApplicationContext()).getVideoResult().videos) {
						if(videoId.equalsIgnoreCase(videos.videoId)){
							filteredVideoList.add(videos);
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

	}

	@Override
	public void onItemClick(android.widget.AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		checkYouTubAppAvailability(position,movieVideos);

	}

	@Override
	public void onClick(View view) {
		startActivity(new Intent(this,TermsOfUseActivity.class));

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mConnReceiver,  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(((MagikFlix)getApplicationContext()).getVideoResult() != null ){
			setPlayList(Constants.CATEGORY_SELECTED_POSITION);
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mConnReceiver);
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(!currentNetworkInfo.isConnected()){
				mProgressBar.setVisibility(View.GONE);
			}else{
				if(((MagikFlix)getApplicationContext()).getVideoResult() == null){
					mProgressBar.setVisibility(View.VISIBLE);
					getVideos(((MagikFlix)getApplicationContext()).getToken());
				}
			}
		}
	};

}
