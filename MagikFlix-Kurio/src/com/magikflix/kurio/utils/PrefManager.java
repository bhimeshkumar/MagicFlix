package com.magikflix.kurio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.magikflix.kurio.app.utils.Constants;

public class PrefManager {

	public static void saveString(Context context, String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void saveInt(Context context, String key, int value) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void saveBool(Context context, String key, boolean value) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	
	public static String getString(Context context, String key, String defValue){
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		return pref.getString(key, defValue);	
	}
	
	public static int getInt(Context context, String key, int defValue){
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		return pref.getInt(key, defValue);
	}
	
	public static boolean getBool(Context context, String key, boolean defValue){
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(key, defValue);	
	}
	

}