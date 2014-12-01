package com.magicflix.goog.app.api;

import com.google.gson.Gson;
import com.magicflix.goog.api.data.AbstractDataRequest;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.api.data.IDataRequestType;
import com.magicflix.goog.api.http.HttpResult;
import com.magicflix.goog.app.api.requests.CustomLogginRequest;
import com.magicflix.goog.app.api.requests.FavoriteJsonRequest;
import com.magicflix.goog.app.api.requests.FavourireVideoRequest;
import com.magicflix.goog.app.api.requests.GuestRequest;
import com.magicflix.goog.app.api.requests.RecentVideoRequest;
import com.magicflix.goog.app.api.requests.RegisterEmailRequest;
import com.magicflix.goog.app.api.requests.SecretCodeRequest;
import com.magicflix.goog.app.api.requests.VideoRequest;
import com.magicflix.goog.app.api.results.AppConfigResult;
import com.magicflix.goog.app.api.results.GuestResult;
import com.magicflix.goog.app.api.results.SecretCodeResult;
import com.magicflix.goog.app.api.results.VideoResult;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.builders.BaseBuilder;
import com.magicflix.goog.builders.CommonJsonBuilder;

public class MFlixJsonBuilder extends BaseBuilder {

	@SuppressWarnings("unchecked")
	public <T> DataResult<T> execute(AbstractDataRequest dataRequest) {

		switch ((WebRequestType)dataRequest.requestType) {
		case GET_VIDEOS :
			return (DataResult<T>) getVideos((VideoRequest)dataRequest);
		case DO_GUEST_LOGIN :
			return (DataResult<T>) doGuestLogin((GuestRequest)dataRequest);
		case GET_APP_CONFIG :
			return (DataResult<T>) getAppConfig((GuestRequest)dataRequest);
			//		case GET_FAVOURITE_VIDEOS :
			//			return (DataResult<T>) getFavouriteVideos((VideosRequest)dataRequest);
			//		case GET_RECENT_VIDEOS :
			//			return (DataResult<T>) getRecentVideos((VideosRequest)dataRequest);
		case POST_RECENT_VIDEOS :
			return (DataResult<T>) postRecentVideos((RecentVideoRequest)dataRequest);
		case POST_FAV_VIDEOS :
			return (DataResult<T>) postFavouriteVideos((FavourireVideoRequest)dataRequest);
		case POST_VIDEO_LOGGING :
			return (DataResult<T>) postCustomLoggging((CustomLogginRequest)dataRequest);
		case DO_EMAIL_REGISTER :
			return (DataResult<T>) registerUserEmail((RegisterEmailRequest)dataRequest);
		case GET_SECRET_CODE :
			return (DataResult<T>) getSecretCode((SecretCodeRequest)dataRequest);
		default:
			return null;
		}

	}

	private DataResult<String> postCustomLoggging(CustomLogginRequest dataRequest) {
		Gson gson = new Gson();
		String json = "["+gson.toJson(dataRequest.videoInfo)+"]";
		//		String json = dataRequest.videoInfo.toString();
		HttpResult httpResult = httpHelper.postString(MFlixUrlBuilder.getCustomLogginURL(dataRequest), json);
		DataResult<String> result= new DataResult<String>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String.class) : null);
		return result;
	}

	private DataResult<VideoResult> getVideos(VideoRequest dataRequest) {
		HttpResult httpResult = httpHelper.getString(MFlixUrlBuilder.getVideosURL(dataRequest));
		DataResult<VideoResult> result= new DataResult<VideoResult>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, VideoResult.class) : null);
		return result;
	}

	private DataResult<GuestResult> doGuestLogin(GuestRequest dataRequest) {
		Gson gson = new Gson();
		String json = gson.toJson(dataRequest);
		HttpResult httpResult = httpHelper.postString(MFlixUrlBuilder.getGuestLoginURL(), json,Constants.AUTH_KEY);
		DataResult<GuestResult> result= new DataResult<GuestResult>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, GuestResult.class) : null);
		return result;
	}
	//	
	//	private DataResult<String[]> getFavouriteVideos(VideosRequest dataRequest) {
	//		HttpResult httpResult = httpHelper.getString(WachableUrlBuilder.getFavoriteVideos(dataRequest));
	//		DataResult<String[]> result= new DataResult<String[]>();
	//		result.successful = isResultSuccesfull(httpResult);
	//		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String[].class) : null);
	//		return result;
	//	}
	//	
	//	private DataResult<String[]> getRecentVideos(VideosRequest dataRequest) {
	//		HttpResult httpResult = httpHelper.getString(WachableUrlBuilder.getRecentVideos(dataRequest));
	//		DataResult<String[]> result= new DataResult<String[]>();
	//		result.successful = isResultSuccesfull(httpResult);
	//		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String[].class) : null);
	//		return result;
	//	}


	private DataResult<String> postFavouriteVideos(FavourireVideoRequest dataRequest) {
		Gson gson = new Gson();
		String data = "{"+"videoid:"+gson.toJson(dataRequest.videoId)+"}";
		HttpResult httpResult ;
		if(dataRequest.isFavorite){
			System.out.println("favourite--");
			httpResult = httpHelper.postString(MFlixUrlBuilder.postFavVideos(dataRequest), data);
		}else{
			System.out.println("Un favourite--");
			httpResult = httpHelper.deleteString(MFlixUrlBuilder.removeFavVideoURL(dataRequest));
		}
		DataResult<String> result= new DataResult<String>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String.class) : null);
		return result;
	}

	private DataResult<String> postRecentVideos(RecentVideoRequest dataRequest) {
		Gson gson = new Gson();
		String json = gson.toJson(dataRequest.recent);
		HttpResult httpResult = httpHelper.postString(MFlixUrlBuilder.postRecentVideos(dataRequest), json,Constants.AUTH_KEY);
		DataResult<String> result= new DataResult<String>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String.class) : null);
		return result;
	}

	private DataResult<String> registerUserEmail(RegisterEmailRequest dataRequest) {

		Gson gson = new Gson();
		String json = "{"+"email:"+gson.toJson(dataRequest.email)+"}";
		System.out.println("Json email-->"+json);
		HttpResult httpResult = httpHelper.putString(MFlixUrlBuilder.getEmailRegisterURL(dataRequest), json);
		DataResult<String> result= new DataResult<String>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, String.class) : null);
		return result;
	}

	private DataResult<AppConfigResult> getAppConfig(GuestRequest dataRequest) {
		HttpResult httpResult = httpHelper.getString(MFlixUrlBuilder.getAppConfigURL(dataRequest));
		DataResult<AppConfigResult> result= new DataResult<AppConfigResult>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, AppConfigResult.class) : null);
		return result;
	}


	public static enum WebRequestType implements IDataRequestType{
		GET_VIDEOS, GET_FAVOURITE_VIDEOS, GET_RECENT_VIDEOS,POST_RECENT_VIDEOS,POST_FAV_VIDEOS, DO_GUEST_LOGIN, POST_VIDEO_LOGGING, DO_EMAIL_REGISTER, GET_APP_CONFIG,GET_SECRET_CODE;
	}

	private DataResult<SecretCodeResult> getSecretCode(SecretCodeRequest dataRequest) {
		HttpResult httpResult = httpHelper.getString(MFlixUrlBuilder.getSecretCodeURL(dataRequest));
		DataResult<SecretCodeResult> result= new DataResult<SecretCodeResult>();
		result.successful = isResultSuccesfull(httpResult);
		result.entity = (isResultSuccesfull(httpResult) ?new CommonJsonBuilder().getEntityForJson(httpResult.result, SecretCodeResult.class) : null);
		return result;
	}


}
