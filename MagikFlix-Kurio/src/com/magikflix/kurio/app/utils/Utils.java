package com.magikflix.kurio.app.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.magikflix.kurio.utils.MLogger;

public class Utils {

	private static String TAG = Utils.class.getName();

	public static String getAppVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			MLogger.logError(TAG, "Error :: getAppVersion");
		}
		String version = info.versionName;

		return version;
	}

	public static boolean isNetworkConnected(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE );
		NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
		return isConnected;   
	}


	public static Bitmap convertByteArrayToBitmap(byte[] imageData){
		return  BitmapFactory.decodeByteArray(imageData , 0, imageData .length);

	}

	public static byte[] convertBitMapToByteArray(Bitmap bitmap){

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;

	}

	public static boolean isValidEmail(String email) {



		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches())
			return true;
		else
			return false;

		//		return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

		//		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		//		Boolean isValidEmail = email.matches(EMAIL_REGEX);
		//		return isValidEmail;

		//		if(isValidEmail){
		//			if(mLocalyticsSession != null)
		//				mLocalyticsSession.tagEvent(Constants.EMAIL_IS_VALID);
		//			registerEmail(email);
		//			return true;
		//		}else{
		//			
		//			return false;
		//			if(mLocalyticsSession != null)
		//				mLocalyticsSession.tagEvent(Constants.EMAIL_INVALID);
		//			showShortToast("Please enter valid email address");
		//		}
	}


	public static boolean isValidUserName(String username){
//		String USERNAME_PATTERN =   "^[a-z0-9_-]{3,15}$";
		Pattern pattern = Pattern.compile("^[-_,A-Za-z0-9]$");
		Matcher matcher = pattern.matcher(username);
		return !matcher.matches();
	}

}
