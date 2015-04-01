package com.magicflix.goog.app.vimeo;

import java.io.InputStream;

import android.util.Log;

import com.magicflix.goog.app.vimeo.Utils.VideoQuality;

public class VimeoVideoStreamer {
	
	public static final String TAG = "VimeoVideoStreamer"; 
	
    public static InputStream getVideoStream(long videoId, VideoQuality quality) throws FailedToGetVideoStreamException { 
		// this method is removed from repository because it is not allowed by vimeo staff
		// to share this method with developers :(.
		Log.e(TAG, "This function is not provided in open-source version, please consult author");
		return null;
	}
	
	public static long getLastContentLength() { return -1; }

}