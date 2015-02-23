package com.magicflix.goog.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

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
}
