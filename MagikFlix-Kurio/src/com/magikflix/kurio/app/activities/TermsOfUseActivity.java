package com.magikflix.kurio.app.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.api.data.DataResult;
import com.magikflix.kurio.app.api.MFlixJsonBuilder;
import com.magikflix.kurio.app.api.MFlixJsonBuilder.WebRequestType;
import com.magikflix.kurio.app.api.requests.SecretCodeRequest;
import com.magikflix.kurio.app.api.results.SecretCodeResult;
import com.magikflix.kurio.app.asyntasks.DataApiAsyncTask;
import com.magikflix.kurio.app.utils.Constants;
import com.magikflix.kurio.broadcasts.AppTimerBroadCastReceiver;
import com.magikflix.kurio.broadcasts.PopUpDismissBroadCastReceiver;
import com.magikflix.kurio.utils.MLogger;

public class TermsOfUseActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "TermsOfUseActivity";
	private TextView mSecretCodeTV;
	private TextView mTermsOfUseTV;
	private Button mOkButton;
	private boolean mIsBackbtnPressed = false;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow ;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_terms_of_use);
		getActionBar().hide();
		init();
		setIdsToViews();
		setListnersToViews();
		getSecretCode();
	} 
	
	private void init() {
		mAppTimerIntent = new IntentFilter(Constants.INTENT_APP_TIMER_EXPIRED);
		
		 mAppTimerBroadCastReceiver = new AppTimerBroadCastReceiver() {
			
			@Override
			protected void onTimerExpired() {
				popupWindow = getTimerAlert();
				popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
			}
		};
		
		mPopUpDismissIntent = new IntentFilter(Constants.INTENT_APP_ALERT_DISMISS);
		mPopUpDismissBroadCastReceiver = new PopUpDismissBroadCastReceiver() {

			@Override
			protected void dismissPopUp() {
				try {
					if(popupWindow != null && popupWindow.isShowing()){
						popupWindow.dismiss();
					}
				} catch (Exception e) {
					MLogger.logInfo(TAG, "Exception in mPopUpDismissBroadCastReceiver ::"+e.getLocalizedMessage());
				}
			}
		};
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
	
	@Override
	protected void onResume() {
		super.onResume();
		((MagikFlix)getApplication()).setIsAppRunningBackground(false);
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
		registerReceiver(mPopUpDismissBroadCastReceiver, mPopUpDismissIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!mIsBackbtnPressed){
			((MagikFlix)getApplication()).setIsAppRunningBackground(true);
		}
		unregisterReceiver(mAppTimerBroadCastReceiver);
		unregisterReceiver(mPopUpDismissBroadCastReceiver);
	}
	
	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		super.onBackPressed();
	}
	
}
