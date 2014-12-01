package com.magikflix.kurio.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;

import com.magikflix.kurio.R;

public class Constants {
	public static final int LOGIN_COMPLETE = 0;
	public static String APP_API_BASE = "http://mflixsvc.cloudapp.net/";
	public static final String AUTH_ENDPOINT = "https://magikflix.azure-mobile.net";
	public static final String AUTH_KEY = "QbLOVuzzpYVORbDQbDXnyPfRVpabFA39";
	public static final String VIDEOS_URL = "https://magikflix.azure-mobile.net/api/v2/";
	public static String mTimerValue = "00:20:00";
	public static int TIMERLIMIT = 20;
	public static int TIMERLIMIT_COUNTER = 1;
	public static final int TIMER_REFRESH = 90;
	public static final int TIMER_EXPIRED = 99;
	public static final int TIMER_STOP = 100;
	public static final String USER_ID = "USER_ID";
	public static final String IS_EMAIL_OPTIONAL = "IS_EMAIL_OPTIONAL";
	public static final String MIN_AGE = "MIN_AGE";
	public static final String MAX_AGE = "MAX_AGE";
	public static final String DEFAULT_AGE = "DEFAULT_AGE";
	public static final String AGE_SELECTED = "AGE_SELECTED";
	public static final String TOKEN = "TOKEN";
	public static final int UPDATE_VIDEO_GALLERY = 3;
	public static final String EMPTY_STRING = "";
	public static int MAX_VIDEO_SMALL_TILES = 2;
	public static int MAX_VIDEO_MEDIUM_TILES = 2;
	public static int MAX_VIDEO_BIG_TILES = 2;
	public static int MAX_CATEGORY_TILES = 2;
	public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyAJPXicM5pJWvJPZhcLpueJsk-5AMwLxgg";
	public static final int LAUCH_HOME_FRAGMENT = 4;
	public static final String APP_VERSION = "2.0";
	public static final String FACEBOOK_APP_ID = "688395307918500";
	public static final String PREFS_FILE_NAME = "MFLIX_PREFERENCES";
	public static final String PREF_EMAIL = "PREF_EMAIL";
	public static ArrayList<String> FAV_VIDEOS_LIST = new ArrayList<String>();
	public static int CATEGORY_SELECTED_POSITION;
	public static String CATEGORY_NAME_SELECTED;

	public static final int[] SPLASH_IMAGE_RESOURCES = {
		R.drawable.blux_appear_0, R.drawable.blux_appear_1,
		R.drawable.blux_appear_2, R.drawable.blux_appear_3,
		R.drawable.blux_appear_4, R.drawable.blux_appear_5,
		R.drawable.blux_appear_6, R.drawable.blux_appear_7,
		R.drawable.blux_appear_8, R.drawable.blux_appear_9,
		R.drawable.blux_appear_10, R.drawable.blux_appear_11,
		R.drawable.blux_appear_12, R.drawable.blux_appear_13,
		R.drawable.blux_appear_14, R.drawable.blux_appear_15,
		R.drawable.blux_appear_16, R.drawable.blux_appear_17,
		R.drawable.blux_appear_18, R.drawable.blux_appear_19,
		R.drawable.blux_appear_20, R.drawable.blux_appear_21,
		R.drawable.blux_appear_22, R.drawable.blux_appear_23,
		R.drawable.blux_appear_24, R.drawable.blux_appear_25,
		R.drawable.blux_appear_26, R.drawable.blux_appear_19,
		R.drawable.blux_appear_20, R.drawable.blux_appear_21,
		R.drawable.blux_appear_22, R.drawable.blux_appear_23,
		R.drawable.blux_appear_24, R.drawable.blux_appear_25,
		R.drawable.blux_appear_26 };

	public static String readTxt(Context context, boolean isAboutThisApp) {
		InputStream inputStream = null;
		if (isAboutThisApp) {
			inputStream = context.getResources().openRawResource(R.raw.about);
		} else {
			inputStream = context.getResources().openRawResource(R.raw.terms);
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}

}
