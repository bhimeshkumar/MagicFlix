package com.magikflix.kurio.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class FavouriteChangeListner extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		onFavoritedChange();

	}

	protected abstract void onFavoritedChange();

}

