package com.magicflix.goog.medialplayer;

import android.content.Context;

public interface PlayerControlInterface extends
android.widget.MediaController.MediaPlayerControl {
	void setVolume(float leftVolume, float rightVolume);
	void setFullscreen(boolean fullscreen, Context mContext);
}
