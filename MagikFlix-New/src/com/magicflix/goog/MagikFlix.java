package com.magicflix.goog;

import android.app.Application;
import android.text.TextUtils;

import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;
import com.magicflix.goog.app.api.results.VideoResult;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.utils.PrefManager;

public class MagikFlix  extends Application {

	private VideoResult mVideoResult;
	private boolean mIsSubscriptionRestored;
	private LocalyticsAmpSession localyticsSession;

	@Override
	public void onCreate() {
		super.onCreate();
		//		BackBone bb = new BackBone();
		//		bb.setup(this,false, 0);
		
		// Instantiate the object
        this.localyticsSession = new LocalyticsAmpSession(
                 this.getApplicationContext());  // Context used to access device resources

        // Register LocalyticsActivityLifecycleCallbacks
        this.registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(this.localyticsSession));

	}
	
	public LocalyticsAmpSession getLocatyticsSession(){
		return this.localyticsSession;
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


	public void setIsSubscriptionRestored(boolean isSubscriptionRestored) {

		PrefManager.saveBool(getApplicationContext(), Constants.IS_SUBSCRIPTION_RESTORED, Boolean.valueOf(isSubscriptionRestored));

	}

	public boolean isSubscriptionRestored() {
		return PrefManager.getBool(getApplicationContext(), Constants.IS_SUBSCRIPTION_RESTORED, false);

	}


	public void setVideoPlayTime(int videoPlayTime) {
			PrefManager.saveInt(getApplicationContext(), "videoPlayTime", videoPlayTime);
	}
	
	public int getVideoPlayTime() {
		return PrefManager.getInt(getApplicationContext(), "videoPlayTime", 0);
}


	public void setIsAppInstalled(boolean isAppInstalled) {
		PrefManager.saveBool(getApplicationContext(), "isAppInsatlled", isAppInstalled);
	}
	
	public boolean isAppInstalled(boolean isAppInstalled) {
		return PrefManager.getBool(getApplicationContext(), "isAppInsatlled", false);
	}


	public boolean isTrialPeriodExpired() {
		return PrefManager.getBool(getApplicationContext(), "isTrialPeriodExpired", false);
	}
	
	public void setIsTrialPeriod(boolean isTrialPeriodExpired) {
		PrefManager.saveBool(getApplicationContext(), "isTrialPeriodExpired", isTrialPeriodExpired);
	}


	public void setFreeTrailPeriod(int freeTrialPeriod) {
		PrefManager.saveInt(getApplicationContext(), "freeTrialPeriod", freeTrialPeriod);
		
	}
	
	public int getFreeTrailPeriod() {
		return PrefManager.getInt(getApplicationContext(), "freeTrialPeriod", 0);
		
	}


	public void setApplicationActiveTime(int activeTime) {
		PrefManager.saveInt(getApplicationContext(), "appActive", activeTime);
		
	}
	
	public int getApplicationActiveTime() {
		return PrefManager.getInt(getApplicationContext(), "appActive", 0);
		
	}

	public void setAgeIsSelected(boolean selectedAge) {
		PrefManager.saveBool(getApplicationContext(), "isAgeSelected", selectedAge);
		
	}
	
	public boolean isAgeSelected() {
		return PrefManager.getBool(getApplicationContext(), "isAgeSelected", false);
		
	}

	public void setIsUserSubscribed(boolean isUserSubscribed) {
		PrefManager.saveBool(getApplicationContext(), "isUserSubscribed", isUserSubscribed);
		
	}
	
	public boolean isUserSubscribed() {
		return PrefManager.getBool(getApplicationContext(), "isUserSubscribed", false);
		
	}

	public void setAppTimerValue(String timerString) {
		PrefManager.saveString(getApplicationContext(), "appTimerValue", timerString);
		
	}
	
	public String getAppTimerValue(){
		return PrefManager.getString(getApplicationContext(), "appTimerValue", "20");
	}
}
