package com.magikflix.kurio.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class TrialExpiredBroadCastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		onTrialExpired();

	}

	protected abstract void onTrialExpired();

}
