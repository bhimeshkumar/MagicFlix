package com.magikflix.kurio.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AppTimerBroadCastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		onTimerExpired();

	}

	protected abstract void onTimerExpired();

}
