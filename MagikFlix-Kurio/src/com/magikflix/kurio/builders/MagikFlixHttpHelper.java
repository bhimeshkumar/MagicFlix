package com.magikflix.kurio.builders;


/**
 * @author Bhimesh
 *
 */

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.magikflix.kurio.api.http.AbstractHttpHelper;

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
