package com.magikflix.kurio.app.asyntasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.magikflix.kurio.api.data.AbstractDataRequest;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.utils.MLogger;
import com.magikflix.kurio.utils.NetworkConnection;



public abstract class AbstractDataApiAsyncTask extends AsyncTask<AbstractDataRequest, Void, DataResult<?>> {

	protected Context context;
	protected boolean isConnected;
	protected boolean shouldToastWhenNoNetworkIsPresent;

	/**
	 * If context isn't available, pass null. Note that for this case, the
	 * system will not be able to detect whether or not a network connection
	 * exists.
	 */
	protected AbstractDataApiAsyncTask(Context context, boolean shouldToastWhenNoNetworkIsPresent) {
		if (context != null) this.context = context.getApplicationContext();
		this.shouldToastWhenNoNetworkIsPresent = shouldToastWhenNoNetworkIsPresent;
	}

	@Override
	protected DataResult<?> doInBackground(AbstractDataRequest... params) {
		if (context != null) {
			isConnected = NetworkConnection.isConnected(context);
			MLogger.logInfo(this.getClass().getName(), String.format("isConnected to network = %s", String.valueOf(isConnected)));
		}
		return null;
	}

	@Override
	protected void onPostExecute(DataResult<?> result) {
		//TODO: put string literal into string.xml
		// not using the field isConnected as things may change in between
		if (context != null && !NetworkConnection.isConnected(context) && shouldToastWhenNoNetworkIsPresent) {
			Toast.makeText(context, "Internet service failure", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}
}
