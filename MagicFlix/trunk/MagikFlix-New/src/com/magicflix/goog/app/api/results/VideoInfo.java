package com.magicflix.goog.app.api.results;

import com.magicflix.goog.api.data.AbstractDataRequest;

public class VideoInfo extends AbstractDataRequest{

	public int type;
	public String video_id;
	public int watched_time;
	public long time_stamp;
	public boolean isComplete;
	
//	@Override
//	public String toString() {
//	   return " [type=" + type + ", video_id=" + video_id + ", watched_time="
//		+ watched_time + "]";
//	}

}
