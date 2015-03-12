package com.magicflix.goog.utils;

import java.util.Date;

import android.util.Log;

/**
 *@author bhimesh
 * 
 */
public class MLogger {
	public static final String LOG_TAG = "Magicflix";
	
	private static boolean inTestMode(){
		return true;
	}
	
	public static void logInfo(String tag, String msg) {
		if (inTestMode()) {
			logForTestMode(tag, msg);
			return;
		}
		Log.d(tag, msg);
	}

	private static void logForTestMode(String tag, String msg, Exception... optional) {
		Exception e = optional == null || optional.length == 0 ? null : optional[0];
		if (e == null) {
			System.out.println(String.format("%s - %s - %s", new Date().toString(), tag, msg));
		}
		else {
			System.out.println(String.format("%s - %s - %s  Error message = %s", new Date().toString(), tag, msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
		}
	}

	public static void logError(String tag, String msg, Exception... optional) {
		if (inTestMode()) {
			logForTestMode(tag, msg, optional);
			return;
		}
		Exception e = optional == null || optional.length == 0 ? null : optional[0];
		if (e == null) {
			Log.e(tag, String.format("%s  Error message = %s  Exception e is null.  ", msg, ""));
		}
		else {
			Log.e(tag, String.format("%s  Error message = %s", msg, e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
		}
	}
}
