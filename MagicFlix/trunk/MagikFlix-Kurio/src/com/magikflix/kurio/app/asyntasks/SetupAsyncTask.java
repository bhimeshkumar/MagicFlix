
package com.magikflix.kurio.app.asyntasks;

import android.content.Context;
import android.os.AsyncTask;

import com.magikflix.kurio.app.activities.SplashActivity;
import com.magikflix.kurio.utils.NetworkConnection;


public class SetupAsyncTask extends AsyncTask<Void, Void, Void> {

	private SplashActivity mSplashScreen;
	private Context mContext;


	public SetupAsyncTask(SplashActivity splashScreen) {

		this.mSplashScreen = splashScreen; 
		this.mContext = splashScreen.getApplicationContext();

	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (!NetworkConnection.isConnected(mContext)) {
			mSplashScreen.closeApp();
		}
		else{
			mSplashScreen.goToNextScreen();
		}

	}	


}
