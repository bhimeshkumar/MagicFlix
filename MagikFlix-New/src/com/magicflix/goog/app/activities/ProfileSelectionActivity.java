package com.magicflix.goog.app.activities;

import it.sephiroth.android.library.widget.HListView;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.magicflix.goog.R;
import com.magicflix.goog.app.adapters.ProfileSelectionAdapter;
import com.magicflix.goog.app.db.Db4oHelper;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.broadcasts.AppTimerBroadCastReceiver;

public class ProfileSelectionActivity extends BaseActivity implements OnDismissListener {

	private HListView mProfileList;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent;

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
				showTimerAlert();
				
			}
		};
		
	}
	
	private void showTimerAlert() {
		PopupWindow popupWindow =getTrialExpiredPopUp((Constants.APP_TIMER_VALUE == 0) ? getString(R.string.times_up_txt) : getString(R.string.app_timer_msg));
		popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
		popupWindow.setOnDismissListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mAppTimerBroadCastReceiver);
	}
	private void setIdsToViews() {
		getActionBar().hide();
		mProfileList = (HListView)findViewById(R.id.profile_screen_profile_list);

		mProfileList.setAdapter(new ProfileSelectionAdapter(this,new Db4oHelper(this).getUserProfiles()));
	}

	@Override
	public void onDismiss() {
		Constants.IS_APP_TIMER_SHOWN = true;
		
	}

}
