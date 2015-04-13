package com.magikflix.kurio.app.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.app.utils.Constants;
import com.magikflix.kurio.app.views.CircularSeekBar;
import com.magikflix.kurio.app.views.CircularSeekBar.OnCircleSeekBarChangeListener;
import com.magikflix.kurio.broadcasts.AppTimerBroadCastReceiver;
import com.magikflix.kurio.broadcasts.PopUpDismissBroadCastReceiver;
import com.magikflix.kurio.utils.MLogger;

public class SettingsActivity extends BaseActivity implements OnClickListener, OnCircleSeekBarChangeListener{
	
	private static String TAG = HomeActivity.class.getName();
	private Button mOkBtn;
	private CircularSeekBar mCircularSeekBar;
	private MagikFlix mApplication;
	private int mProgressValue;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow ;
	private boolean mIsBackbtnPressed = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_settings);
		setIdsToViews();
		init();
		setListnersToViews();
	}

	private void init() {
		getActionBar().hide();
		mApplication = (MagikFlix)getApplicationContext();
		mProgressValue = mCircularSeekBar.getValue();

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

	private void setListnersToViews() {
		mOkBtn.setOnClickListener(this);
		mCircularSeekBar.setOnSeekBarChangeListener(this);
	}

	private void setIdsToViews() {
		mOkBtn = (Button)findViewById(R.id.settings_screen_ok_btn);
		mCircularSeekBar = (CircularSeekBar)findViewById(R.id.settings_circular_sb);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.settings_screen_ok_btn:
			if(Constants.TIMER_LIMIT_UPDATED){
				mApplication.setAppTimerValue(String.valueOf(mProgressValue));
				mApplication.setSeekBarValue(String.valueOf(mProgressValue));
				Constants.DEFAULT_APP_TIMER_LIMIT = mProgressValue;
			}
			this.finish();
			break;
		default:
			break;
		}
	}

	public void onProgressChanged(CircularSeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(CircularSeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(CircularSeekBar seekBar) {
		mProgressValue = seekBar.getValue();
		Constants.TIMER_LIMIT_UPDATED = true;
	}

	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		super.onBackPressed();
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
		super.onPause();
		if(!mIsBackbtnPressed){
			((MagikFlix)getApplication()).setIsAppRunningBackground(true);
		}
		unregisterReceiver(mAppTimerBroadCastReceiver);
		unregisterReceiver(mPopUpDismissBroadCastReceiver);
	}

}
