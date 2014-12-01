package com.magicflix.goog.builders;


/**
 * @author Bhimesh
 *
 */

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.magicflix.goog.api.http.AbstractHttpHelper;

public class MagikFlixHttpHelper extends AbstractHttpHelper {

	@Override
	protected void setupRequest(HttpRequestBase request) {

	}

	@Override
	protected DefaultHttpClient createHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		return client;
	}

}
