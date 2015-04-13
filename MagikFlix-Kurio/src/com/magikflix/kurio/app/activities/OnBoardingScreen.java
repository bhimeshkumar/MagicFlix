package com.magikflix.kurio.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.localytics.android.LocalyticsAmpSession;
import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.app.api.MFlixJsonBuilder;
import com.magikflix.kurio.app.api.MFlixJsonBuilder.WebRequestType;
import com.magikflix.kurio.app.api.requests.GuestRequest;
import com.magikflix.kurio.app.api.requests.RegisterEmailRequest;
import com.magikflix.kurio.app.api.results.AppConfigResult;
import com.magikflix.kurio.app.api.results.EmaiResult;
import com.magikflix.kurio.app.api.results.GuestResult;
import com.magikflix.kurio.app.asyntasks.DataApiAsyncTask;
import com.magikflix.kurio.app.utils.Constants;
import com.magikflix.kurio.app.utils.Utils;
import com.magikflix.kurio.broadcasts.AppTimerBroadCastReceiver;
import com.magikflix.kurio.broadcasts.PopUpDismissBroadCastReceiver;
import com.magikflix.kurio.utils.MLogger;
import com.magikflix.kurio.utils.NetworkConnection;

public class OnBoardingScreen extends BaseActivity implements OnClickListener, OnEditorActionListener{

	private static String TAG = HomeActivity.class.getName();
	private Button mParentControlBtn;
	private Handler handel = new Handler();
	private LinearLayout mEmailLayout;
	private EditText mEmailEt;
	private LocalyticsAmpSession mLocalyticsSession;
	private String APP_VERSION ;
	private ProgressBar mProgressBar;
	private RelativeLayout mParentGateWayLayout;
	private Button mSkipBtn;
	private TextView mNoNetworkTV;

	private boolean mIsDataLoaded = false;
	private MagikFlix mApplication;
	private boolean isFromSplashScreen = false;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow;
	private boolean mIsBackbtnPressed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);
		init();
		setIdsToViews();
		setListnersToViews();
		if(isFromSplashScreen){
			getAppConfiguration();
		}else{
			mProgressBar.setVisibility(View.GONE);
			mParentGateWayLayout.setVisibility(View.VISIBLE);
		}
	}

	private void getAppConfiguration(){
		mParentGateWayLayout.setVisibility(View.GONE);
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

	private void getUserIdAndToken(){
		MagikFlix app = (MagikFlix) getApplicationContext();
		String token = app.getToken();
		if( token.length() <= 0){
			createGuestUser();
		}else{
			mProgressBar.setVisibility(View.GONE);
			mParentGateWayLayout.setVisibility(View.VISIBLE);
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
			mIsDataLoaded = true;
		}
		mProgressBar.setVisibility(View.GONE);
		mParentGateWayLayout.setVisibility(View.VISIBLE);
	}

	private void saveUserIdAndToken(String userId, String token) {
		MagikFlix app = (MagikFlix) getApplication();
		app.setToken(token);
		app.setUserID(userId);
	}
	private void saveConfigSettings(AppConfigResult entity) {
		MagikFlix app = (MagikFlix) getApplication();
		app.setAppConfiguration(entity);
		app.setIsEmailOptional(entity.appConfig.Settings[3].SettingValue);
		app.setDefaultAge(entity.appConfig.Settings[2].SettingValue);
		app.setMinAge(entity.appConfig.Settings[0].SettingValue);
		app.setMaxAge(entity.appConfig.Settings[1].SettingValue);
	}


	private void setListnersToViews() {
		mEmailEt.setOnEditorActionListener(this);
		mEmailLayout.setOnClickListener(this);
		mSkipBtn.setOnClickListener(this);
		mParentControlBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mParentControlBtn.setBackground(getResources().getDrawable(R.drawable.shape_circular_selected));
					handel.postDelayed(run, 3000/* OR the amount of time you want */);
					break;
				case MotionEvent.ACTION_CANCEL:
					mParentControlBtn.setBackground(getResources().getDrawable(R.drawable.shape_circular));
					handel.removeCallbacks(run);
					break;
				case MotionEvent.ACTION_UP:
					mParentControlBtn.setBackground(getResources().getDrawable(R.drawable.shape_circular));
					handel.removeCallbacks(run);
					break;
				}
				return true;
			}
		});
	}

	private void setIdsToViews() {
		mParentControlBtn = (Button)findViewById(R.id.on_boarding_parent_ctrl_btn);
		mEmailLayout = (LinearLayout)findViewById(R.id.on_boarding_email_layout);
		mEmailEt = (EditText)findViewById(R.id.on_boarding_email_et);
		mProgressBar = (ProgressBar)findViewById(R.id.on_boarding_screen_pb);
		mParentGateWayLayout = (RelativeLayout)findViewById(R.id.on_boarding_parent_gate_way_layout);
		mSkipBtn = (Button)findViewById(R.id.on_board_screen_skip_btn);
		mNoNetworkTV  = (TextView)findViewById(R.id.on_boarding_screen_no_network_tv);

	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			isFromSplashScreen = bundle.getBoolean("isFromSplashScreen",false);
		}
		getActionBar().hide();
		mApplication = (MagikFlix)getApplication();
		APP_VERSION = Utils.getAppVersion(this);
		mLocalyticsSession = ((MagikFlix)getApplicationContext()).getLocatyticsSession();
		if(mLocalyticsSession != null)
			mLocalyticsSession.tagEvent(Constants.PARENT_GATE_OPEN);

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


	Runnable run = new Runnable() {
		@Override
		public void run() {
			if(isFromSplashScreen){
				if(mLocalyticsSession != null)
					mLocalyticsSession.tagEvent(Constants.PARENT_GATE_PASSED);
				mEmailLayout.setVisibility(View.VISIBLE);
				mParentControlBtn.setVisibility(View.GONE);
				if(((MagikFlix)getApplication()).isEmailOptional()){
					mSkipBtn.setVisibility(View.VISIBLE);
				}
			}else{
				OnBoardingScreen.this.finish();
				startActivity(new Intent(OnBoardingScreen.this,FamilySetUpActivity.class));
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.on_board_screen_skip_btn:
			navigateToHomeScreen();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
			String email = mEmailEt.getText().toString();
			if(email != null && email.length() >0){
				if(Utils.isValidEmail(email)){
					if(mLocalyticsSession != null)
						mLocalyticsSession.tagEvent(Constants.EMAIL_IS_VALID);
					registerEmail(email);
				}else{
					if(mLocalyticsSession != null)
						mLocalyticsSession.tagEvent(Constants.EMAIL_INVALID);
					showShortToast("Please enter valid email address");
				}
			}else{
				showShortToast("Please enter your email");	
			}

		}  
		return false;
	}



	public void registerEmail(String email) {
		MagikFlix app = (MagikFlix)getApplicationContext();
		RegisterEmailRequest  emailRequest = new RegisterEmailRequest();
		emailRequest.Token = app.getToken();
		emailRequest.email = email;
		emailRequest.requestDelegate = new MFlixJsonBuilder();
		emailRequest.requestType =  WebRequestType.DO_EMAIL_REGISTER;
		new DataApiAsyncTask(true, this, emailHandler, getProgressDialog()).execute(emailRequest);
	}

	private Handler emailHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processEmailResults((DataResult<EmaiResult>) msg.obj);
		}
	};

	private void processEmailResults(DataResult<EmaiResult> obj) {
		EmaiResult emailResponse = obj.entity;
		if(emailResponse != null && obj.entity.isSubscriptionRestored){
			((MagikFlix)getApplicationContext()).setIsSubscriptionRestored(obj.entity.isSubscriptionRestored);
		}
		if(emailResponse != null && emailResponse.message.equalsIgnoreCase("Email set.")){
			MagikFlix app = (MagikFlix)getApplicationContext();
			app.setEmail(mEmailEt.getText().toString());
			navigateToHomeScreen();
		}else{
			showShortToast("Email is not set.please try agian");
		}
	}

	private void navigateToHomeScreen() {
		this.finish();
		startActivity(new Intent(OnBoardingScreen.this,AgeSelectionActivity.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		((MagikFlix)getApplication()).setIsAppRunningBackground(false);
		registerReceiver(mConnReceiver,  new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
		registerReceiver(mPopUpDismissBroadCastReceiver, mPopUpDismissIntent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(!mIsBackbtnPressed){
			((MagikFlix)getApplication()).setIsAppRunningBackground(true);
		}
		unregisterReceiver(mConnReceiver);
		unregisterReceiver(mAppTimerBroadCastReceiver);
		unregisterReceiver(mPopUpDismissBroadCastReceiver);
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			MLogger.logInfo(TAG, "isNetworkConnected :: "+NetworkConnection.isConnected(context));
			//			mParentGateWayLayout.setVisibility(View.GONE);
			//			if(!NetworkConnection.isConnected(context)){
			//				mProgressBar.setVisibility(View.GONE);
			//				mNoNetworkTV.setVisibility(View.VISIBLE);
			//			}else if(mApplication.getAppConfiguration() == null){
			//				mNoNetworkTV.setVisibility(View.GONE);
			//				mProgressBar.setVisibility(View.VISIBLE);
			//				getAppConfiguration();
			//			}else {
			//				mNoNetworkTV.setVisibility(View.GONE);
			//				mProgressBar.setVisibility(View.VISIBLE);
			//				getUserIdAndToken();
			//			}

		}

	};


	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		super.onBackPressed();
	}

}
