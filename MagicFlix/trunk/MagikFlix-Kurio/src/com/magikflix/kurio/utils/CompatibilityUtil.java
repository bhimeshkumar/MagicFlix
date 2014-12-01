package com.magikflix.kurio.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

public class CompatibilityUtil {

	/** Get the current Android API level. */
	public static int getSdkVersion() {
		return Build.VERSION.SDK_INT;
	}

	/** Determine if the device is running API level 8 or higher. */
	public static boolean isFroyo() {
		return getSdkVersion() >= Build.VERSION_CODES.FROYO;
	}

	/** Determine if the device is running API level 11 or higher. */
	public static boolean isHoneycomb() {
		return getSdkVersion() >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * Determine if the device is a tablet (i.e. it has a large screen).
	 * 
	 * @param context The calling context.
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Determine if the device is a HoneyComb tablet.
	 * 
	 * @param context The calling context.
	 */
	public static boolean isHoneycombTablet(Context context) {
		return isHoneycomb() && isTablet(context);
	}

	public static boolean isTenInchTblet(Context context){
		DisplayMetrics metrics = new DisplayMetrics();
		//		  context.getApplicationContext().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		float scaleFactor = metrics.density;
		float widthDp = widthPixels / scaleFactor;
		float heightDp = heightPixels / scaleFactor;
		float widthDpi = metrics.xdpi;
		float heightDpi = metrics.ydpi;
		float widthInches = widthPixels / widthDpi;
		float heightInches = heightPixels / heightDpi;
		double diagonalInches = Math.sqrt(
				(widthInches * widthInches) 
				+ (heightInches * heightInches));
		if (diagonalInches >= 10) {
			//Device is a 10" tablet
			return true;
		}
		return false; 
		

	}


	/** This class can't be instantiated. */
	private CompatibilityUtil() { }
}