package com.magikflix.kurio.app.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.VideoView;

import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.app.db.Db4oHelper;

public class SplashActivity extends BaseActivity implements  OnCompletionListener{

	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		MagikFlix app = (MagikFlix) getApplicationContext();
		String token = app.getToken();
		if( token.length() <= 0 || (!app.isEmailOptional() && TextUtils.isEmpty(app.getEmail())) || (token.length() <= 0 || !(app.isAgeSelected())) ){
			new Db4oHelper(this).delete(); // deleting old db
			setUpScreen();
		}else{
			Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
			intent.putExtra("isFromSplashScreen", true);
			startActivity(intent);
			this.finish();
		}

	}

	private void setUpScreen() {
		getActionBar().hide();
		setContentView(R.layout.activity_splash);
		setIdsToViews();
		setListnersToViews();
		playVideo();
	}

	private void setListnersToViews() {
		mVideoView.setOnCompletionListener(this);

	}

	private void playVideo() {
		String path = "android.resource://" + getPackageName() + "/" + R.raw.splash_screen_video;
		mVideoView.setVideoURI(Uri.parse(path));
		mVideoView.start();

	}

	private void setIdsToViews() {
		mVideoView = (VideoView)findViewById(R.id.splash_screen_video_view);

	}

	public void goToNextScreen() {
		if(isFinishing())
			return;
		navigateToHome();
		finish();
	}

	/**
	 * TO redirect next screen
	 */
	public void navigateToHome() {
		MagikFlix app = (MagikFlix) getApplicationContext();
		String token = app.getToken();
		Intent intent ;
		if( token.length() <= 0 || (!app.isEmailOptional() && TextUtils.isEmpty(app.getEmail())) ){
			intent = new Intent(getApplicationContext(), OnBoardingScreen.class);
			intent.putExtra("isFromSplashScreen", true);
			startActivity(intent);
		}else if( token.length() <= 0 || !(app.isAgeSelected())){
			intent = new Intent(getApplicationContext(), AgeSelectionActivity.class);
			intent.putExtra("isFromSplashScreen", true);
			startActivity(intent);
		}
		finish();
	}


	/**
	 * It will close the app if the network is not available
	 */
	public void closeApp() {

		Toast.makeText(getApplicationContext(), "No internet connection.  Shutting down.",
				Toast.LENGTH_SHORT).show();
		new CountDownTimer(4000, 1000) {
			@Override
			public void onFinish() {
				finish();
			}
			@Override
			public void onTick(long millisUntilFinished) {
			}
		}.start();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onCompletion(MediaPlayer mp) {
		goToNextScreen();
	}
}
