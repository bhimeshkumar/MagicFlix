/**
 * 
 */
package com.magicflix.goog.app.manager;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.magicflix.goog.api.http.AbstractHttpHelper;


public class HttpHelper extends AbstractHttpHelper {

	
	/**
	 * 
	 * Used to setup the request such as modifying the headers before executing the request
	 */
	/* (non-Javadoc)
	 * @see com.motivity.androidshared.api.http.AbstractHttpHelper#setupRequest(org.apache.http.client.methods.HttpRequestBase)
	 */
	@Override
	protected void setupRequest(HttpRequestBase request) {

	}

	/**
	 * Used to create an instance of {@code DefaultHttpClient} and then setup
	 * the client - such as establishing the auth approach - before executing
	 * the request.
	 */
	@Override
	protected DefaultHttpClient createHttpClient() {
		//Example code
		
		DefaultHttpClient client  = new DefaultHttpClient();
		return client;
	}

}
