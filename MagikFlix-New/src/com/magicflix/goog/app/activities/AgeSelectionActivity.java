package com.magicflix.goog.app.activities;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.localytics.android.LocalyticsAmpSession;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.AgeRequest;
import com.magicflix.goog.app.api.requests.GuestRequest;
import com.magicflix.goog.app.api.results.AppConfigResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.app.utils.Constants;

public class AgeSelectionActivity extends BaseActivity implements OnSeekBarChangeListener, OnClickListener{
	private SeekBar mSeekBar;
	private TextView mProgressTV;
	private TextView mMinAgeTV,mMaxAgeTV;
	private Button mOkBtn , mSkipBtn;
	private String mSelectedAge;
	private LocalyticsAmpSession mLocalyticsSession;
	private Map<String, String> mLocalyticsAttributes ;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_age_selection);
		init();
		setIdsToViews();
		setListnersToViews();
		setUpUI();
	}

	private void setListnersToViews() {
		mSeekBar.setOnSeekBarChangeListener(this);
		mOkBtn.setOnClickListener(this);
		mSkipBtn.setOnClickListener(this);

	}

	private void setUpUI() {
		MagikFlix app = (MagikFlix)getApplication();
		mSeekBar.setProgress((Integer.parseInt(app.getDefaultAge())*10) -( Integer.parseInt(app.getMinAge()) * 10));
		mMinAgeTV.setText(app.getMinAge());
	}

	private void init() {
		getActionBar().hide();
		mLocalyticsAttributes = new HashMap<String, String>();
		mLocalyticsSession = ((MagikFlix)getApplicationContext()).getLocatyticsSession();
	}

	private void setIdsToViews() {
		mSeekBar = (SeekBar)findViewById(R.id.on_board_age_sb);
		mProgressTV = (TextView)findViewById(R.id.on_board_age_tv);
		mMinAgeTV = (TextView)findViewById(R.id.on_board_min_age_tv);
		mMaxAgeTV = (TextView)findViewById(R.id.on_board_max_age_tv);
		mOkBtn = (Button)findViewById(R.id.on_board_age_screen_ok_btn);
		mSkipBtn = (Button)findViewById(R.id.on_board_age_screen_skip_btn);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		progress = progress + 20;
		int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
		mProgressTV.setVisibility(View.GONE);
		mProgressTV.setText(String.valueOf(progress/10 ));
		mProgressTV.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
		Bitmap bitmap = Bitmap.createBitmap(70, 70, Bitmap.Config.ARGB_8888);
		Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas c = new Canvas(bmp);
		String text = Integer.toString(seekBar.getProgress());
		Paint p = new Paint();
		p.setTypeface(Typeface.DEFAULT_BOLD);
		p.setTextSize(40);
		p.setColor(Color.BLACK);
		int width = (int) p.measureText(text);
		int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
		c.drawColor(Color.WHITE);
		Rect r = new Rect();
		p.getTextBounds(text, 0, text.length(), r);
		p.setTextAlign(Align.CENTER);
		c.drawText(String.valueOf(progress/10 ), (c.getWidth() / 2) ,yPos, p);
		seekBar.setThumb(new BitmapDrawable(getResources(), bmp));
		mSelectedAge = String.valueOf(progress/10);

	}



	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.on_board_age_screen_ok_btn:
			setAge(mSelectedAge);
			break;
		case R.id.on_board_age_screen_skip_btn:
			navigateToMainScreen();
			break;

		default:
			break;
		}

	}

	private void navigateToMainScreen() {
		startActivity(new Intent(this,HomeActivity.class));
		this.finish();

	}
	
	
	private void setAge(String age){
		AgeRequest  guestRequest = new AgeRequest();
		guestRequest.token = ((MagikFlix)getApplication()).getToken();
		guestRequest.age = age;
		guestRequest.requestDelegate = new MFlixJsonBuilder();
		guestRequest.requestType =  WebRequestType.SET_AGE;	
		new DataApiAsyncTask(true, this, appConfigHandler, getProgressDialog()).execute(guestRequest);
	}

	private Handler appConfigHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processAppConfigResults((DataResult<String>) msg.obj);
		}
	};

	private void processAppConfigResults(DataResult<String> appConfigResult) {
		mLocalyticsAttributes.clear();
		mLocalyticsAttributes.put(Constants.AGE, String.valueOf(mSelectedAge));
		if(mLocalyticsSession != null)
			mLocalyticsSession.tagEvent(Constants.AGE_IS_SELECTED, mLocalyticsAttributes);
		MagikFlix app = (MagikFlix)getApplication();
		app.setAgeIsSelected(true);
		navigateToMainScreen();
	}


}
