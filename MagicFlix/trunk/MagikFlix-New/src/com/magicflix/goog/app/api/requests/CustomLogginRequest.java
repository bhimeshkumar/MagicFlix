package com.magicflix.goog.app.api.requests;

import com.magicflix.goog.api.data.AbstractDataRequest;
import com.magicflix.goog.app.api.results.VideoInfo;

public class CustomLogginRequest  extends AbstractDataRequest{

	public String token;
	public VideoInfo videoInfo;

}