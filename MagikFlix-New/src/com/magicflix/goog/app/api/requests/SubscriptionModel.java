package com.magicflix.goog.app.api.requests;

import com.magicflix.goog.api.data.AbstractDataRequest;
import com.magicflix.goog.app.api.results.PayLoad;

public class SubscriptionModel extends AbstractDataRequest {
	
	public int isFree;
	public PayLoad payload;
	public String product;
	public String email;
	public boolean isSubscription;
//	public String receipt;
//	public String packageName;
//	public String platform;
//	public boolean isIos6;

}
