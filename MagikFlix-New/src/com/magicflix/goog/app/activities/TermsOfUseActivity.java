package com.magicflix.goog.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.SecretCodeRequest;
import com.magicflix.goog.app.api.results.SecretCodeResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;

public class TermsOfUseActivity extends BaseActivity implements OnClickListener{

	private TextView mSecretCodeTV;
	private TextView mTermsOfUseTV;
	private Button mOkButton;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_terms_of_use);
		getActionBar().hide();
		setIdsToViews();
		setListnersToViews();
		getSecretCode();
	} 

	private void setIdsToViews() {
		mTermsOfUseTV = (TextView)findViewById(R.id.terms_of_use_tv);
		mSecretCodeTV = (TextView)findViewById(R.id.terms_of_use_secret_code_tv);
		mOkButton = (Button)findViewById(R.id.terms_of_use_ok_btn);
		mTermsOfUseTV.setMovementMethod(LinkMovementMethod.getInstance());
	}


	private void setListnersToViews() {
		mOkButton.setOnClickListener(this);
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
