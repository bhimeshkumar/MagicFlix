package com.magicflix.goog.app.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.views.CircularSeekBar;
import com.magicflix.goog.app.views.CircularSeekBar.OnCircleSeekBarChangeListener;

public class SettingsActivity extends BaseActivity implements OnClickListener, OnCircleSeekBarChangeListener{

	private Button mOkBtn;
	private CircularSeekBar mCircularSeekBar;
	private MagikFlix mApplication;

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
		mApplication.setAppTimerValue(String.valueOf(seekBar.getValue()));
		Constants.TIMER_LIMIT_UPDATED = true;
		Constants.DEFAULT_APP_TIMER_LIMIT = seekBar.getValue() * 60 * 1000;
	}
	
}
