package com.magikflix.kurio.app.api.requests;

import com.magikflix.kurio.api.data.AbstractDataRequest;

public class GuestRequest extends AbstractDataRequest{
	
	public String appid;
	public String appversion;
	public String locale;
	public String device;
	public int platform;

}
