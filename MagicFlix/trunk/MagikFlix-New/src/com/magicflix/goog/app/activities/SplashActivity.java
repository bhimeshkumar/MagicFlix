package com.magicflix.goog.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.animation.FasterAnimationsContainer;
import com.magicflix.goog.animation.FasterAnimationsContainer.OnAnimationStoppedListener;
import com.magicflix.goog.app.asyntasks.SetupAsyncTask;
import com.magicflix.goog.app.utils.Constants;

public class SplashActivity extends Activity implements  OnAnimationStoppedListener{

	private ImageView mAnimatedImageView;
	private FasterAnimationsContainer mFasterAnimationsContainer;
	private static final int ANIMATION_INTERVAL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpScreen();
		new SetupAsyncTask(this).execute();
		//		startAnimation();
		mAnimatedImageView = (ImageView) findViewById(R.id.magikflix_logo);
		mAnimatedImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.blux_appear_26));

	}

	/**
	 * To setup application such as checking the connections and creating DB
	 */
	/*private void setUpApp() {

		new SetupAsyncTask(this).execute();
	}*/

	private void startAnimation() {
		mAnimatedImageView = (ImageView) findViewById(R.id.magikflix_logo);
		mFasterAnimationsContainer = null;
		mFasterAnimationsContainer = FasterAnimationsContainer
				.getInstance(mAnimatedImageView);
		mFasterAnimationsContainer.addAllFrames(Constants.SPLASH_IMAGE_RESOURCES,
				ANIMATION_INTERVAL);
		mFasterAnimationsContainer.setOnAnimationStoppedListener(this);
		mFasterAnimationsContainer.start();
	}


	/**
	 * TO setup layout and the screen settings 
	 */
	private void setUpScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
	}

	public void goToNextScreen() {
		new CountDownTimer(4000, 1000) {
			@Override
			public void onFinish() {

				navigateToHome();
			}

			@Override
			public void onTick(long millisUntilFinished) {
			}
		}.start();
	}
	/**
	 * TO redirect next screen
	 */
	public void navigateToHome() {
		MagikFlix app = (MagikFlix) getApplicationContext();
		String token = app.getToken();

		if( token.length() <= 0 || (!app.isEmailOptional() && TextUtils.isEmpty(app.getEmail())) ){
			startActivity(new Intent(getApplicationContext(), LandingScreen.class));
		}else{
			startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
		//				mFasterAnimationsContainer.stop();
		//				mFasterAnimationsContainer.clearInstance();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}


	@Override
	public void onAnimationStopped() {
		mAnimatedImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.blux_appareance_skew20));
		new CountDownTimer(5000, 1000) {
			@Override
			public void onFinish() {

				goToNextScreen();
			}

			@Override
			public void onTick(long millisUntilFinished) {
			}
		}.start();
	}

}
