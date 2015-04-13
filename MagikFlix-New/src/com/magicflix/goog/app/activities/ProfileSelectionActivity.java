package com.magicflix.goog.app.activities;

import it.sephiroth.android.library.widget.HListView;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.PopupWindow;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.adapters.ProfileSelectionAdapter;
import com.magicflix.goog.app.db.Db4oHelper;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.broadcasts.AppTimerBroadCastReceiver;
import com.magicflix.goog.broadcasts.PopUpDismissBroadCastReceiver;
import com.magicflix.goog.utils.MLogger;

public class ProfileSelectionActivity extends BaseActivity  {

	private static String TAG = ProfileSelectionActivity.class.getName();
	private HListView mProfileList;
	private boolean mIsBackbtnPressed = false;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow ;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		init();
		setContentView(R.layout.activity_profile_selection);
		setIdsToViews();
	}

	private void init() {
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
	

	@Override
	protected void onResume() {
		super.onResume();
		((MagikFlix)getApplication()).setIsAppRunningBackground(false);
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
		registerReceiver(mPopUpDismissBroadCastReceiver, mPopUpDismissIntent);
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
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
	private void setIdsToViews() {
		getActionBar().hide();
		mProfileList = (HListView)findViewById(R.id.profile_screen_profile_list);

		mProfileList.setAdapter(new ProfileSelectionAdapter(this,new Db4oHelper(this).getUserProfiles()));
	}

	
	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		super.onBackPressed();
	}

}
