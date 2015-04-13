package com.magicflix.goog.app.activities;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.magicflix.goog.R;
import com.magicflix.goog.app.utils.Constants;

public class BaseActivity extends YouTubeBaseActivity implements OnDismissListener{

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public boolean appCanUseDefaultPlayer() {
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE) ;
		boolean isYouTubeAppInstalled = mSharedPreferences.getBoolean("useDefaultPlayer", false);
		return  isYouTubeAppInstalled;
	}

	public Typeface getMagikFlixFontBold(){
		return Typeface.createFromAsset(getAssets(), "fonts/proxima_nova_bold.otf");
	}

	public Typeface getMagikFlixFontRegular(){
		return Typeface.createFromAsset(getAssets(), "fonts/proxima_nova_regular.otf");
	}

	public ProgressDialog getProgressDialog() {
		ProgressDialog progressDialog = getProgressDialogInternal();
		progressDialog.setMessage("Please wait...");
		return progressDialog;
	}

	private ProgressDialog getProgressDialogInternal() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		return progressDialog;
	}




	@SuppressLint("InflateParams") public ActionBar setUpCustomActionBar(boolean homeButtonEnabled) throws IOException{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.magikflix_actionbar, null);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_bg)));
		android.app.ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams (ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, 0);
		actionBar.setCustomView(v, layoutParams);
		return actionBar;
	}

	public void showShortToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

	}

	public void showLongToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

	}

	@SuppressLint("InflateParams") @SuppressWarnings("deprecation")
	public PopupWindow getTrialExpiredPopUp(String message){

		LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
		View popupView = layoutInflater.inflate(R.layout.time_up_pop_up, null);  

		TextView messsageLabel = (TextView)popupView.findViewById(R.id.times_up_tv);
		messsageLabel.setText(message);
		final PopupWindow popupMessage = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		popupMessage.setContentView(popupView);
		popupMessage.setTouchable(true);
		popupMessage.setFocusable(true);
		popupMessage.setBackgroundDrawable(new BitmapDrawable());
		popupMessage.setOutsideTouchable(true);
		popupMessage.getContentView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupMessage.dismiss();

			}
		});
		return popupMessage;


	}

	public void showInvalidDateDialog(){

		LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
		View popupView = layoutInflater.inflate(R.layout.free_trail_dialog, null);  
		final Dialog trailDialog = new Dialog(this);
		trailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		trailDialog.setContentView(popupView);
		Button okBtn = (Button)popupView.findViewById(R.id.trial_popup_ok_btn);
		TextView trialValue = (TextView)popupView.findViewById(R.id.trial_popup_trial_value_tv);
		trialValue.setText("Please select valid age");
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				trailDialog.dismiss();
			}
		});
		trailDialog.setCancelable(false);
		trailDialog.show();
	}

	public void showAlertDialog(String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public PopupWindow getTimerAlert() {
		PopupWindow popupWindow =getTrialExpiredPopUp((Constants.APP_TIMER_VALUE == 0) ? getString(R.string.times_up_txt) : getString(R.string.app_timer_msg));
		popupWindow.setOnDismissListener(this);
		return popupWindow;
	}

	@Override
	public void onDismiss() {
		Constants.IS_APP_TIMER_SHOWN = true;
		
	}
}
