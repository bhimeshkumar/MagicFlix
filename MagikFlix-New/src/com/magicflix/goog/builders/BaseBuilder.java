package com.magicflix.goog.builders;

import com.magicflix.goog.api.data.IDataRequestDelegate;
import com.magicflix.goog.api.http.HttpResult;




public abstract class BaseBuilder  implements IDataRequestDelegate{
	protected MagikFlixHttpHelper httpHelper =new MagikFlixHttpHelper();
	protected void preExecute() {
		httpHelper = getHttpHelper();
	}
	private MagikFlixHttpHelper getHttpHelper() {
		httpHelper = new MagikFlixHttpHelper();
		return httpHelper;
	}

	protected boolean isResultSuccesfull(HttpResult httpResult) {
		return !(httpResult == null || httpResult.statusCode != 200);
	}
}
