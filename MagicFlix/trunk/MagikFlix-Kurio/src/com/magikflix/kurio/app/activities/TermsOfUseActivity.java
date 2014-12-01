package com.magikflix.kurio.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.app.api.MFlixJsonBuilder;
import com.magikflix.kurio.app.api.MFlixJsonBuilder.WebRequestType;
import com.magikflix.kurio.app.api.requests.SecretCodeRequest;
import com.magikflix.kurio.app.api.results.SecretCodeResult;
import com.magikflix.kurio.app.asyntasks.DataApiAsyncTask;

public class TermsOfUseActivity extends BaseActivity implements OnClickListener{

	private TextView mSecretCodeTV;
	private WebView mWebView;
	private Button mOkButton;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_terms_of_use);
		getActionBar().hide();
		setIdsToViews();
		setListnersToViews();
		loadTermsOfUseText();
		getSecretCode();
	} 

	private void setIdsToViews() {
		mWebView = (WebView)findViewById(R.id.terms_of_use_wv);
		mSecretCodeTV = (TextView)findViewById(R.id.terms_of_use_secret_code_tv);
		mOkButton = (Button)findViewById(R.id.terms_of_use_ok_btn);
	}
	

	private void setListnersToViews() {
		mOkButton.setOnClickListener(this);
		
	}

	
	private void loadTermsOfUseText() {
		mWebView.loadUrl("file:///android_asset/terms.html");
		mWebView.getSettings().setJavaScriptEnabled(true);
	}

	private void getSecretCode() {
		SecretCodeRequest  secretCodeRequest = new SecretCodeRequest();
		secretCodeRequest.token = ((MagikFlix)getApplicationContext()).getToken();
		secretCodeRequest.requestDelegate = new MFlixJsonBuilder();
		secretCodeRequest.requestType =  WebRequestType.GET_SECRET_CODE;	
		new DataApiAsyncTask(true, this, secretCodeHandler, null).execute(secretCodeRequest);
	}

	private Handler secretCodeHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processVideoResults((DataResult<SecretCodeResult>) msg.obj);
		}
	};

	private void processVideoResults(DataResult<SecretCodeResult> obj) {
		if(obj.entity != null ){
			String secretCode = obj.entity.code;
			mSecretCodeTV.setText(secretCode);
		}
	}

	@Override
	public void onClick(View view) {
		this.finish();
		
	}

}
