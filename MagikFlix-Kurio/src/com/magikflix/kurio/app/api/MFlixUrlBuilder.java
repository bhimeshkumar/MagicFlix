package com.magikflix.kurio.app.api;

import com.magikflix.kurio.app.api.requests.AddSubscriptionrequest;
import com.magikflix.kurio.app.api.requests.AgeRequest;
import com.magikflix.kurio.app.api.requests.CustomLogginRequest;
import com.magikflix.kurio.app.api.requests.FavourireVideoRequest;
import com.magikflix.kurio.app.api.requests.GuestRequest;
import com.magikflix.kurio.app.api.requests.RecentVideoRequest;
import com.magikflix.kurio.app.api.requests.RedeemCodeRequest;
import com.magikflix.kurio.app.api.requests.RegisterEmailRequest;
import com.magikflix.kurio.app.api.requests.SecretCodeRequest;
import com.magikflix.kurio.app.api.requests.VideoRequest;
import com.magikflix.kurio.app.api.requests.VimeoVideoRequest;


public class MFlixUrlBuilder {

	private static String BASE_URL = "http://magikflixtest.cloudapp.net/"; //Development
//	private static String BASE_URL = "http://mflixsvc.cloudapp.net/";      //Production

	public static String getVideosURL(VideoRequest request) {
		return String.format("%s/api/v2/Application/InitialData?appid=%s&appversion=%s&token=%s", BASE_URL,request.appid,request.appversion, request.token);
	}
	
	public static String postFavVideos(FavourireVideoRequest dataRequest) {
	    return String.format("%sapi/v2/User/FavoriteVideo?Token=%s",BASE_URL,dataRequest.token);
	}

	public static String postRecentVideos(RecentVideoRequest dataRequest) {
		return String.format("%s/api/v2/videos/recent?Token=%s",BASE_URL,dataRequest.token);
	}

	public static String getGuestLoginURL() {
		return String.format("%s/api/v2/auth/register/guest?",BASE_URL);
	}
	
	public static String getCustomLogginURL(CustomLogginRequest dataRequest) {
		return String.format("%s/api/v2/Events/log?Token=%s",BASE_URL,dataRequest.token);
	}

	public static String getEmailRegisterURL(RegisterEmailRequest dataRequest) {
		return String.format("%s/api/v2/User/Email?Token=%s",BASE_URL,dataRequest.Token);
	}
	
	public static String getAppConfigURL(GuestRequest dataRequest) {
		return String.format("%s/api/v2/Application/AppConfig?appid=%s&appversion=%s",BASE_URL,dataRequest.appid,dataRequest.appversion);
	}
	
	public static String getSecretCodeURL(SecretCodeRequest dataRequest) {
		return String.format("%sapi/v2/User/SecretCode?Token=%s",BASE_URL,dataRequest.token);
	}

	public static String removeFavVideoURL(FavourireVideoRequest dataRequest) {
		 return String.format("%sapi/v2/User/FavoriteVideo?Token=%s&VideoId=%s",BASE_URL,dataRequest.token,dataRequest.videoId);
	}
	
	public static String getPromotionCodeURL(RedeemCodeRequest dataRequest){
		return String.format("%sapi/v2/Promotion/Redeem?token=%s",BASE_URL,dataRequest.token);
	}
	
	public static String getAddSubcriptionURL(AddSubscriptionrequest dataRequest){
		return String.format("%sapi/v2/Subscription/Add?token=%s",BASE_URL,dataRequest.token);
	}
	
	public static String getSubcriptionURL(AddSubscriptionrequest dataRequest){
		return String.format("%sapi/v2/User/Subscription?token=%s",BASE_URL,dataRequest.token);
	}
	
	public static String getUpdateAgeURL(AgeRequest dataRequest){
		return String.format("%sapi/v2/User/Age?token=%s&age=%s",BASE_URL,dataRequest.token,dataRequest.age);
	}
	
	
	public static String getVimeoVideoURL(VimeoVideoRequest dataRequest){
		return String.format("http://player.vimeo.com/video/%s/config",dataRequest.videoId);
	}
	
}
