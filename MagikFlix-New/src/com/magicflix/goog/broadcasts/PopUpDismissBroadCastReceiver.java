package com.magicflix.goog.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class PopUpDismissBroadCastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		dismissPopUp();

	}

	protected abstract void dismissPopUp();

}
