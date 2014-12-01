package com.magikflix.kurio.app.activities;

import java.io.IOException;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.magikflix.kurio.R;

public class BaseActivity extends YouTubeBaseActivity{

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



	
	public ActionBar setUpCustomActionBar(boolean homeButtonEnabled) throws IOException{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
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
	
	public ProgressDialog getProgressDialog() {
		ProgressDialog progressDialog = getProgressDialogInternal();
		progressDialog.setMessage("Please wait...");
		return progressDialog;
	}
	
	private ProgressDialog getProgressDialogInternal() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		return progressDialog;
	}
	
	public void showLongToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		
	}


}
