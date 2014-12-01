package com.magikflix.kurio.app.api.requests;

import com.magikflix.kurio.api.data.AbstractDataRequest;
import com.magikflix.kurio.app.api.results.VideoInfo;

public class CustomLogginRequest  extends AbstractDataRequest{

	public String token;
	public VideoInfo videoInfo;

}