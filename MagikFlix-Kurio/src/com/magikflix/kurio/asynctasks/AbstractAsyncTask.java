package com.magikflix.kurio.asynctasks;

/**
 * @author Bhimesh
 *
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.magikflix.kurio.utils.MLogger;
import com.magikflix.kurio.utils.NetworkConnection;

public abstract class AbstractAsyncTask extends AsyncTask<Void, Void, Void> {
	protected Context context;
	protected boolean isConnected;
	protected boolean shouldToast;

	protected AbstractAsyncTask(Context context) {
		super();
		// trying to not leak contexts by using the application's context
		// http://android-developers.blogspot.com/2009/01/avoiding-memory-leaks.html
		// Also, some request may not have a context
		if (context != null) this.context = context.getApplicationContext();
	}

	protected AbstractAsyncTask(Context context, boolean shouldToast) {
		this(context);
		this.shouldToast = shouldToast;
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (context != null) {
			isConnected = NetworkConnection.isConnected(context);
			MLogger.logInfo("Request", "isConnected " + NetworkConnection.isConnected(context));
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// not using the field isConnected as things may change in between
		if (context != null && !NetworkConnection.isConnected(context) && shouldToast) {
			Toast.makeText(context, "Internet service failure", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}
	

	

}
