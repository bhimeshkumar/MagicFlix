package com.magicflix.goog.api.http;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.magicflix.goog.utils.MLogger;

/**
 * @author bhimesh
 * 
 */
public abstract class AbstractHttpHelper {
	protected abstract void setupRequest(HttpRequestBase request);
	protected abstract DefaultHttpClient createHttpClient();

	public HttpResult getString(String url) {
		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: GET  ", this.getClass().getName()));
		return doRequest(new HttpGet(), url);
	}

	public HttpResult putString(String url,String data) {
		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: PUT  ", this.getClass().getName()));
		return doRequest(new HttpPut(), url,data);
	}

	private HttpResult doRequest(HttpPut request, String url, String data) {
		HttpResult httpResult = null;
		DefaultHttpClient client = createHttpClient();
		try {
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  URL is = %s  ", this.getClass().getName(), url));
			request.setURI(URI.create(url.trim()));
			if(data != null){
			StringEntity entity = new StringEntity(data);
			//			 entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"text/plain;charset=UTF-8"));
			entity.setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
			request.setEntity(entity);
			
			}
			setupRequest(request);
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%S Data Sent to Server:  %s" , this.getClass().getName(), data));
			httpResult = getHttpResult(client.execute(request));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Status code from server is = %s  ", this.getClass().getName(), String.valueOf(httpResult.statusCode)));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Response from server is = %s  ", this.getClass().getName(), httpResult.result));
			client.getConnectionManager().shutdown();
		}
		catch (ClientProtocolException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ClientProtocolException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (IOException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  IOException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  Exception: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		return httpResult;
	}
	public HttpResult deleteString(String url) {
		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: DELETE  ", this.getClass().getName()));
		return doRequest(new HttpDelete(), url);
	}

	public HttpResult postString(String url, String data) {
		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: POST  ", this.getClass().getName()));
		HttpPost postRequest = new HttpPost();
		try {
			if(data != null) {
				StringEntity entity = new StringEntity(data);
				entity.setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
				entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
				postRequest.setEntity(entity);
			}
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%S Data Sent to Server:  %s" , this.getClass().getName(), data));
		}
		catch (UnsupportedEncodingException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  UnsupportedEncodingException: postString(): Error occurred.  ", this.getClass().getName()), e);
			e.printStackTrace();
		}		
		return doRequest(postRequest, url);
	}
	
	public HttpResult postString(String url, String data ,String authToken) {
		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: POST  ", this.getClass().getName()));
		HttpPost postRequest = new HttpPost();
		try {
			if(data != null) {
				StringEntity entity = new StringEntity(data);
				postRequest.addHeader("M-AUTH-TOKEN", authToken);
				entity.setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
				entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
				postRequest.setEntity(entity);
			}
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%S Data Sent to Server:  %s" , this.getClass().getName(), data));
		}
		catch (UnsupportedEncodingException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  UnsupportedEncodingException: postString(): Error occurred.  ", this.getClass().getName()), e);
			e.printStackTrace();
		}		
		return doRequest(postRequest, url);
	}


	public HttpResult postNameValuePairs(String url, Map<String, String> data) {
		List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();

		for (Entry<String, String> entry : data.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  Data sent to server:  %s=%s", this.getClass().getName(), entry.getKey(), entry.getValue()));
		}

		MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  HTTP type is: POST  ", this.getClass().getName()));
		HttpPost postRequest = new HttpPost();
		try {
			if (data != null) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
				postRequest.setEntity(entity);
			}
		}
		catch (UnsupportedEncodingException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  UnsupportedEncodingException: postString(): Error occurred.  ", this.getClass().getName()), e);
			e.printStackTrace();
		}
		return doRequest(postRequest, url);
	}

	private HttpResult doRequest(HttpRequestBase request, String url) {
		HttpResult httpResult = null;
		DefaultHttpClient client = createHttpClient();
		try {
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  URL is = %s  ", this.getClass().getName(), url));
			request.setURI(URI.create(url.trim()));
			setupRequest(request);
			httpResult = getHttpResult(client.execute(request));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Status code from server is = %s  ", this.getClass().getName(), String.valueOf(httpResult.statusCode)));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Response from server is = %s  ", this.getClass().getName(), httpResult.result));
			client.getConnectionManager().shutdown();
		}
		catch (ClientProtocolException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ClientProtocolException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (IOException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  IOException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  Exception: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		return httpResult;
	}

	private HttpResult getHttpResult(HttpResponse httpResponse) throws ParseException, IOException {
		String responseMessage = null;
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) responseMessage = EntityUtils.toString(httpEntity);
		HttpResult httpResult = new HttpResult();
		httpResult.result = responseMessage;
		int statusCode = httpResponse.getStatusLine() == null ? 0 : httpResponse.getStatusLine().getStatusCode();
		httpResult.statusCode = statusCode;
		return httpResult;
	}
	
	private HttpResult doRequest(HttpRequestBase request, String url, String data) {
		HttpResult httpResult = null;
		DefaultHttpClient client = createHttpClient();
//		try {
//	        HttpEntity entity = new StringEntity(data);
//	        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
//	        httpDeleteWithBody.setEntity(entity);
//	        MLogger.logInfo(MLogger.LOG_TAG, String.format("%S Data Sent to Server:  %s" , this.getClass().getName(), data));
//	        httpResult = getHttpResult(client.execute(httpDeleteWithBody));
//	        MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Status code from server is = %s  ", this.getClass().getName(), String.valueOf(httpResult.statusCode)));
//			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Response from server is = %s  ", this.getClass().getName(), httpResult.result));
//
//	    } catch (UnsupportedEncodingException e) {
//	        e.printStackTrace();
//	    } catch (ClientProtocolException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
		try {
			
	        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
	        HttpEntity entity = new StringEntity(data);
	        ((AbstractHttpEntity) entity).setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
			((AbstractHttpEntity) entity).setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
	        httpDeleteWithBody.setEntity(entity);
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  URL is = %s  ", this.getClass().getName(), url));
			request.setURI(URI.create(url.trim()));
			setupRequest(request);
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%S Data Sent to Server:  %s" , this.getClass().getName(), data));
			httpResult = getHttpResult(client.execute(request));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Status code from server is = %s  ", this.getClass().getName(), String.valueOf(httpResult.statusCode)));
			MLogger.logInfo(MLogger.LOG_TAG, String.format("%s  doRequest(): Response from server is = %s  ", this.getClass().getName(), httpResult.result));
			client.getConnectionManager().shutdown();
		}
		catch (ClientProtocolException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ClientProtocolException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (IOException e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  IOException: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  Exception: doRequest(): Error occurred while contacting server.  ", this.getClass().getName()), e);
			client.getConnectionManager().shutdown();
		}
		return httpResult;
	}
	
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
	    public static final String METHOD_NAME = "DELETE";
	    public String getMethod() { return METHOD_NAME; }

	    public HttpDeleteWithBody(final String uri) {
	        super();
	        setURI(URI.create(uri));
	    }
	    public HttpDeleteWithBody(final URI uri) {
	        super();
	        setURI(uri);
	    }
	    public HttpDeleteWithBody() { super(); }
	}
}
