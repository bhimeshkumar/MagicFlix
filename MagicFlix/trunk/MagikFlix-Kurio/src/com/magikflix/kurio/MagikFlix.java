package com.magikflix.kurio;

import android.app.Application;
import android.text.TextUtils;

import com.magikflix.kurio.app.api.results.VideoResult;
import com.magikflix.kurio.app.utils.Constants;
import com.magikflix.kurio.utils.PrefManager;

public class MagikFlix  extends Application {
	
	private VideoResult mVideoResult;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		BackBone bb = new BackBone();
//		bb.setup(this,false, 0);
		
	}


	public void setToken(String token) {
		if(!TextUtils.isEmpty(token)){
			PrefManager.saveString(getApplicationContext(), Constants.TOKEN, token);
		}
	}

	public String getToken(){
		String token = PrefManager.getString(getApplicationContext(), Constants.TOKEN, "");
		return token;
	}

	public void setUserID(String userId) {
		if(!TextUtils.isEmpty(userId)){
			PrefManager.saveString(getApplicationContext(), Constants.USER_ID, userId);
		}
	}

	public String getUserId(){
		String userId = PrefManager.getString(getApplicationContext(), Constants.USER_ID, "");
		return userId;
	}

	public void setIsEmailOptional(String settingValue) {
		if(!TextUtils.isEmpty(settingValue)){
			PrefManager.saveBool(getApplicationContext(), Constants.IS_EMAIL_OPTIONAL, Boolean.valueOf(settingValue));
		}

	}

	public void setEmail(String email){
		if(!TextUtils.isEmpty(email)){
			PrefManager.saveString(getApplicationContext(), Constants.PREF_EMAIL, email);
		}

	}
	
	public String getEmail(){
		return PrefManager.getString(getApplicationContext(), Constants.PREF_EMAIL, "");
	}
	
	public void setDefaultAge(String defaultAge) {
		if(!TextUtils.isEmpty(defaultAge)){
			PrefManager.saveString(getApplicationContext(), Constants.DEFAULT_AGE, defaultAge);
		}

	}

	public void setMinAge(String minAge) {
		if(!TextUtils.isEmpty(minAge)){
			PrefManager.saveString(getApplicationContext(), Constants.MIN_AGE, minAge);
		}

	}
	public void setMaxAge(String maxAge) {
		if(!TextUtils.isEmpty(maxAge)){
			PrefManager.saveString(getApplicationContext(), Constants.MAX_AGE, maxAge);
		}

	}


	public boolean isEmailOptional(){

		return PrefManager.getBool(getApplicationContext(), Constants.IS_EMAIL_OPTIONAL, false);
	}

	public String getMinAge(){
		return PrefManager.getString(getApplicationContext(), Constants.MIN_AGE, "");
	}

	public String getMaxAge(){
		return PrefManager.getString(getApplicationContext(), Constants.MAX_AGE, "");
	}

	public String  getDefaultAge(){
		return PrefManager.getString(getApplicationContext(), Constants.DEFAULT_AGE, "");
	}

	public void  setSelectedAge(String ageSelected){
		if(!TextUtils.isEmpty(ageSelected)){
			PrefManager.saveString(getApplicationContext(), Constants.AGE_SELECTED, ageSelected);
		}
	}
	
	public String  getSelectedAge(){
		return PrefManager.getString(getApplicationContext(), Constants.AGE_SELECTED, "");
	}

	public void setVideosResult(VideoResult videoResult){
		this.mVideoResult = videoResult;
		
	}
	
	public VideoResult getVideoResult(){
		return mVideoResult;
	}
}
