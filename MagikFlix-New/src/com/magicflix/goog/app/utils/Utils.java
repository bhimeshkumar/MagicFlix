package com.magicflix.goog.app.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.magicflix.goog.utils.MLogger;

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


		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean isValidEmail = email.matches(EMAIL_REGEX);
		return isValidEmail;

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



}
