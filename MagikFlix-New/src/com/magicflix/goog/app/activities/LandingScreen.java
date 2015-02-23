package com.magicflix.goog.app.activities;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.adapters.LandingScreenAdapter;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.GuestRequest;
import com.magicflix.goog.app.api.results.AppConfigResult;
import com.magicflix.goog.app.api.results.GuestResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.utils.Utils;

public class LandingScreen extends BaseActivity{

	private ViewFlow mViewFlow;
	private ProgressBar mProgressBar;
	private CircleFlowIndicator mCicularIndicator;
	private String APP_VERSION ;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_landing);
		init();
		setAppIsInstalled();
		setIdsToViews();
		getActionBar().hide();
		getAppConfiguration();

	}
	private void init() {
		APP_VERSION = Utils.getAppVersion(this);
		
	}
	private void setAppIsInstalled() {
		MagikFlix app = ((MagikFlix)getApplicationContext());
		app.setIsAppInstalled(true);
		
	}
	private void setIdsToViews() {
		mProgressBar = (ProgressBar)findViewById(R.id.landing_screen_pb);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mCicularIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		
	}
	/* If your min SDK version is < 8 you need to trigger the onConfigurationChanged in ViewFlow manually, like this */	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mViewFlow.onConfigurationChanged(newConfig);
	}

	public void navigateToHomeScreen() {
		startActivity(new Intent(this,HomeActivity.class));
		this.finish();
	}

	public void setAdapterToViewFlow() {
		mViewFlow.setAdapter(new LandingScreenAdapter(this), 1);
	}

	private void getUserIdAndToken(){
		MagikFlix app = (MagikFlix) getApplicationContext();
		String token = app.getToken();
		if( token.length() <= 0){
			createGuestUser();
		}else{

			String  age = app.getDefaultAge();
			int page = TextUtils.isEmpty(age)?0:1;
			mProgressBar.setVisibility(View.GONE);
			mCicularIndicator.setVisibility(View.VISIBLE);
			mViewFlow.setAdapter(new LandingScreenAdapter(this), page);
			mViewFlow.setFlowIndicator(mCicularIndicator);
		}
	}

	private void createGuestUser() {
		GuestRequest  guestRequest = new GuestRequest();
		guestRequest.appid = getPackageName();
		guestRequest.appversion = APP_VERSION;
		guestRequest.locale = "en_US";
		guestRequest.platform = Constants.PLATFORM;
		guestRequest.device = Constants.getDeviceName();
		guestRequest.requestDelegate = new MFlixJsonBuilder();
		guestRequest.requestType =  WebRequestType.DO_GUEST_LOGIN;	
		new DataApiAsyncTask(true, this, guestHandler, null).execute(guestRequest);
	}

	private Handler guestHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processGuestResults((DataResult<GuestResult>) msg.obj);
		}
	};

	private void processGuestResults(DataResult<GuestResult> obj) {
		if(obj.entity != null){
			String userId = obj.entity.user;
			String token = obj.entity.token;
			saveUserIdAndToken(userId,token);
			mViewFlow.setAdapter(new LandingScreenAdapter(this), 0);
			mViewFlow.setFlowIndicator(mCicularIndicator);
			mCicularIndicator.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
	}

	private void saveUserIdAndToken(String userId, String token) {
		MagikFlix app = (MagikFlix) getApplication();
		app.setToken(token);
		app.setUserID(userId);
	}


	private void getAppConfiguration(){
		mProgressBar.setVisibility(View.VISIBLE);
		GuestRequest  guestRequest = new GuestRequest();
		guestRequest.appid = getPackageName();
		guestRequest.appversion = APP_VERSION;
		
		guestRequest.requestDelegate = new MFlixJsonBuilder();
		guestRequest.requestType =  WebRequestType.GET_APP_CONFIG;	
		new DataApiAsyncTask(true, this, appConfigHandler, null).execute(guestRequest);
	}

	private Handler appConfigHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processAppConfigResults((DataResult<AppConfigResult>) msg.obj);
		}
	};

	private void processAppConfigResults(DataResult<AppConfigResult> appConfigResult) {
		if(appConfigResult.entity != null){
			saveConfigSettings(appConfigResult.entity);
			getUserIdAndToken();
		}
	}
	private void saveConfigSettings(AppConfigResult entity) {
		MagikFlix app = (MagikFlix) getApplication();
		app.setIsEmailOptional(entity.appConfig.Settings[3].SettingValue);
		app.setDefaultAge(entity.appConfig.Settings[2].SettingValue);
		app.setMinAge(entity.appConfig.Settings[0].SettingValue);
		app.setMaxAge(entity.appConfig.Settings[1].SettingValue);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this, Constants.FACEBOOK_APP_ID);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppEventsLogger.deactivateApp(this, Constants.FACEBOOK_APP_ID);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_api_key));
	}

	public void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
