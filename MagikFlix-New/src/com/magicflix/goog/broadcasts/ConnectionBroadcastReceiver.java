package com.magicflix.goog.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class ConnectionBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		onConnectionChange(context);
    }
	
	protected abstract void onConnectionChange(Context context);
}
