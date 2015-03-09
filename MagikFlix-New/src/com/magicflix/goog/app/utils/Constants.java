package com.magicflix.goog.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Build;

import com.magicflix.goog.R;

public class Constants {
	public static final boolean IS_SUBSCRIPTION_ENABLED = false;
	public static final int LOGIN_COMPLETE = 0;
	public static String  APP_API_BASE		 = "http://mflixsvc.cloudapp.net/";
	public static final String AUTH_ENDPOINT	   = "https://magikflix.azure-mobile.net";
	public static final String AUTH_KEY			= "QbLOVuzzpYVORbDQbDXnyPfRVpabFA39";

	public static final String VIDEOS_URL	   = "https://magikflix.azure-mobile.net/api/v2/";
	public static String mTimerValue = "00:20:00";
	public static int TIMERLIMIT		     = 20;
	public static int TIMERLIMIT_COUNTER     = 1;
	public static final int	TIMER_REFRESH    = 90;
	public static final int	TIMER_EXPIRED    = 99;
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
	public static int MAX_VIDEO_SMALL_TILES  = 2;
	public static int MAX_VIDEO_MEDIUM_TILES = 2;
	public static int MAX_VIDEO_BIG_TILES    = 2;
	public static int MAX_CATEGORY_TILES     = 2;
	public static ArrayList<String> FAV_VIDEOS_LIST = new ArrayList<String>();
	public static int CATEGORY_SELECTED_POSITION;
	public static String CATEGORY_NAME_SELECTED;
	public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyAJPXicM5pJWvJPZhcLpueJsk-5AMwLxgg";
	public static final int LAUCH_HOME_FRAGMENT = 4;
	//	public static final String APP_VERSION = "1.0";
	public static final String FACEBOOK_APP_ID = "688395307918500";
	public static final String PREFS_FILE_NAME = "MFLIX_PREFERENCES";
	public static String IS_SUBSCRIPTION_RESTORED = "IS_SUBSCRIPTION_RESTORED";
	public static int VIDEO_PLAY_TIME;
	public static final String PREF_EMAIL ="PREF_EMAIL";
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
	public static final String MFLIX_PREFS_NAME = "MFLIX_PREFERENCES";
	public static final int PLATFORM = 1;
	public static final String APP_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhv8NDaUixhmVqO+GLQ4lUf0twYQq5jAVzTqinEiAVJbI+QiXsTSiTayPih/T3brlFjgaIw3MFOdB2PgctQ4tTNQvpod2Z2z1s4fVOLo6RiQU/N1v4XZbFaeLhaupLhucHQDkjA2MFxzOAYU0mpm9W1ILUcOH4h7iGFhgOUal2DI8ArVXElzAmvOOwT9kI5flLsEaZ3a8YPOk/MoM7skAzyBA+ekbfNq/EE8TIPkof73qtFiyoftmDx7hMS1Ghs02f6o2zKexmL7QT8fK1QNcbuD23hAbecsSBmo9knj3hvcr7n4H+uIo0Z/dhWFYIc/4KjDz1DJu9lKmEqdJ3UTBbQIDAQAB";

	//Locaytics tags
	public static final String CATEGORY_SELECTOR_EVENT = "categorySelector";
	public static final String CATEGORY = "category";
	public static final String INDEX = "index";
	public static final String AGE = "age";
	public static final String CATEGORY_VIDEO = "categoryVideo";
	public static final String RECOMMENDED_VIDEO = "recommendedVideo";
	public static final String EMAIL_IS_VALID = "emailIsValid";
	public static final String PROMO_CODE_APPLY = "promoCodeApply";
	public static final String PROMO_CODE_ERROR = "promoCodeError";
	public static final String PROMO_CODE_EMPTY = "promoCodeEmpty";
	public static final String AGE_IS_SELECTED = "ageSelected";
	public static final String DISMISS_SUBSCRIPTION = "dismissSubscription";
	public static final String BUY_PRODUCT = "buyProduct";
	public static final String EMAIL_INVALID = "emailInvalid";
	public static final String VIDEO_ID = "videoId";
	public static final String PARENT_GATE_OPEN = "parentGateOpen";
	public static final String PARENT_GATE_PASSED = "parentGatePassed";


	public static String readTxt(Context context, boolean isAboutThisApp){
		InputStream inputStream = null;
		if(isAboutThisApp){
			inputStream = context.getResources().openRawResource(R.raw.about);
		}else{
			inputStream = context.getResources().openRawResource(R.raw.terms);
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}


	public static String formatDuration(int seconds){

		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		if(!twoDigitString(hours).equalsIgnoreCase("00")){
			return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
		}else{
			return  twoDigitString(minutes) + " : " + twoDigitString(seconds);
		}

	}

	private static String twoDigitString(int number) {

		if (number == 0) {
			return "00";
		}

		if (number / 10 == 0) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}


	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	} 



	// Transparent background color codes

	//	100% � FF
	//	95% � F2
	//	90% � E6
	//	85% � D9
	//	80% � CC
	//	75% � BF
	//	70% � B3
	//	65% � A6
	//	60% � 99
	//	55% � 8C
	//	50% � 80
	//	45% � 73
	//	40% � 66
	//	35% � 59
	//	30% � 4D
	//	25% � 40
	//	20% � 33
	//	15% � 26
	//	10% � 1A
	//	5% � 0D
	//	0% � 00
}


