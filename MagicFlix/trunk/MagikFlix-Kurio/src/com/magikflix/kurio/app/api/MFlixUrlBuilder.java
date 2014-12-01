package com.magikflix.kurio.app.api;

import com.magikflix.kurio.app.api.requests.CustomLogginRequest;
import com.magikflix.kurio.app.api.requests.FavourireVideoRequest;
import com.magikflix.kurio.app.api.requests.GuestRequest;
import com.magikflix.kurio.app.api.requests.RecentVideoRequest;
import com.magikflix.kurio.app.api.requests.RegisterEmailRequest;
import com.magikflix.kurio.app.api.requests.SecretCodeRequest;
import com.magikflix.kurio.app.api.requests.VideoRequest;


public class MFlixUrlBuilder {

//	private static String cloudUrl = "http://magikflixtest.cloudapp.net";
	private static String cloudUrl = "http://mflixsvc.cloudapp.net/";

	public static String getVideosURL(VideoRequest request) {
		return String.format("%s/api/v2/Application/InitialData?appid=%s&appversion=%s&token=%s", cloudUrl,request.appid,request.appversion, request.token);
	}
	
	public static String postFavVideos(FavourireVideoRequest dataRequest) {
	    return String.format("%sapi/v2/User/FavoriteVideo?Token=%s",cloudUrl,dataRequest.token);
	}

	public static String postRecentVideos(RecentVideoRequest dataRequest) {
		return String.format("%s/api/v2/videos/recent?Token=%s",cloudUrl,dataRequest.token);
	}

	public static String getGuestLoginURL() {
		return String.format("%s/api/v2/auth/register/guest?",cloudUrl);
	}
	
	public static String getCustomLogginURL(CustomLogginRequest dataRequest) {
		return String.format("%s/api/v2/diagnostics/log?Token=%s",cloudUrl,dataRequest.token);
	}

	public static String getEmailRegisterURL(RegisterEmailRequest dataRequest) {
		return String.format("%s/api/v2/User/Email?Token=%s",cloudUrl,dataRequest.Token);
	}
	
	public static String getAppConfigURL(GuestRequest dataRequest) {
		return String.format("%s/api/v2/Application/AppConfig?appid=%s&appversion=%s",cloudUrl,dataRequest.appid,dataRequest.appversion);
	}
	
	public static String getSecretCodeURL(SecretCodeRequest dataRequest) {
		return String.format("%sapi/v2/User/SecretCode?Token=%s",cloudUrl,dataRequest.token);
	}
	
	public static String removeFavVideoURL(FavourireVideoRequest dataRequest) {
		 return String.format("%sapi/v2/User/FavoriteVideo?Token=%s&VideoId=%s",cloudUrl,dataRequest.token,dataRequest.videoId);
	}
	 
}
