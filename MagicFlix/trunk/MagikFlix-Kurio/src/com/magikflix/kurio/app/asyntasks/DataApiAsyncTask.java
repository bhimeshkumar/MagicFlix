package com.magikflix.kurio.app.asyntasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.magikflix.kurio.api.data.AbstractDataRequest;
import com.magikflix.kurio.api.data.DataResult;


public class DataApiAsyncTask extends AbstractDataApiAsyncTask {

	private ProgressDialog _progressDialog;
	private Handler _handle;

	public DataApiAsyncTask(boolean shouldToastWhenNoNetworkIsPresent, Context context, Handler handle, ProgressDialog progressDialog) {
		super(context, shouldToastWhenNoNetworkIsPresent);
		_handle = handle;
		_progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (_progressDialog != null) _progressDialog.show();
	}

	@Override
	protected DataResult<?> doInBackground(AbstractDataRequest... params) {
		super.doInBackground(params);
		if (params == null || params[0] == null) return null;
		return params[0].requestDelegate.execute(params[0]);
	}

	@Override
	protected void onPostExecute(DataResult<?> result) {
		super.onPostExecute(result);
		sendMessage(result);
		if (_progressDialog != null) _progressDialog.cancel();
	}

	private void sendMessage(DataResult<?> result) {
		if (_handle == null) return;
		Message m = Message.obtain();
		m.obj = result;
		_handle.sendMessage(m);
	}

}
