package com.magicflix.goog.app.adapters;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Message;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.activities.BaseActivity;
import com.magicflix.goog.app.activities.LandingScreen;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.RegisterEmailRequest;
import com.magicflix.goog.app.api.results.GuestResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;
import com.magicflix.goog.seekbar.VerticalSeekBar;

public class ImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private PopupWindow mAccountsPopupWindow;
	private int age;

	private Context mContext;
	private ProgressBar mProgressBar;
	private EditText mEmailEt;
	public ImageAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		MagikFlix app  =  (MagikFlix)mContext.getApplicationContext();
		age = Integer.parseInt(app.getDefaultAge());

	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position == 0){
			convertView = mInflater.inflate(R.layout.age_selection_screen, null);

			SeekBar verticalSeekBar = (VerticalSeekBar)convertView.findViewById(R.id.vertical_Seekbar);

			verticalSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, Mode.SRC_ATOP);
			String defaultAge = ((MagikFlix)mContext.getApplicationContext()).getDefaultAge();
			if(defaultAge.length() > 0){
				verticalSeekBar.setProgress(10 * Integer.parseInt(defaultAge));
			}else{
				verticalSeekBar.setProgress(0);
			}
			verticalSeekBar.setMax(100);
			verticalSeekBar.getThumb().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
			verticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					progress = progress / 10;
					progress = progress * 10;
					seekBar.setProgress(progress);
					age = progress;
				}
			});
			((Button) convertView.findViewById(R.id.age_selection_screen_ok_btn)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MagikFlix app  =  (MagikFlix)mContext.getApplicationContext();
					app.setDefaultAge(String.valueOf(age));
					((LandingScreen)mContext).setAdapterToViewFlow();

				}
			});
		}
		else{
			convertView = mInflater.inflate(R.layout.image_item, null);
			ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.email_set_screen_pb);
			progressBar.setVisibility(View.INVISIBLE);
			mProgressBar = progressBar;
			final EditText emailET = (EditText)convertView.findViewById(R.id.email_screen_email_et);
			mEmailEt = emailET;
			emailET.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
						validateEmail(emailET.getText().toString());
					}  
					return false;
				}
			});
			((Button) convertView.findViewById(R.id.email_send_screen_ok_btn)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String email = emailET.getText().toString();
					validateEmail(email);
				}


			});
			showDeviceAccountsPopUp();

			Button skipButton = (Button)convertView.findViewById(R.id.email_send_screen_skip_btn);

			MagikFlix app  =  (MagikFlix)mContext.getApplicationContext();
			if(app.isEmailOptional()){
				skipButton.setVisibility(View.VISIBLE);
			}else{
				skipButton.setVisibility(View.GONE);
			}
			skipButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String email = emailET.getText().toString();
					((LandingScreen)mContext).navigateToHomeScreen();

				}
			});
		}
		return convertView;
	}

	public void showDeviceAccountsPopUp() {
		//		if(getGmailAccounts().size() > 1){
		//			mAccountsPopupWindow = popupWindowDeviceAccounts(getGmailAccounts(),mEmailEt);
		//			mAccountsPopupWindow.showAsDropDown(mEmailEt);	
		if(getGmailAccounts().size() > 0){
			mEmailEt.setText(getGmailAccounts().get(0));
		}
	}

	private void validateEmail(String email) {

		if(email != null && email.length() >0){
			String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
			Boolean isValidEmail = email.matches(EMAIL_REGEX);

			if(isValidEmail){
				registerEmail(email);
			}else{
				((BaseActivity)mContext).showShortToast("Please enter valid email address");
			}
		}else{
			((BaseActivity)mContext).showShortToast("Please enter your email");
		}
	}


	public void registerEmail(String email) {
		mProgressBar.setVisibility(View.VISIBLE);
		MagikFlix app = (MagikFlix)mContext.getApplicationContext();
		RegisterEmailRequest  emailRequest = new RegisterEmailRequest();
		emailRequest.Token = app.getToken();
		emailRequest.email = email;
		emailRequest.requestDelegate = new MFlixJsonBuilder();
		emailRequest.requestType =  WebRequestType.DO_EMAIL_REGISTER;
		app.setEmail(email);
		new DataApiAsyncTask(true, mContext, emailHandler, ((BaseActivity)mContext).getProgressDialog()).execute(emailRequest);
	}

	private Handler emailHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processEmailResults((DataResult<GuestResult>) msg.obj);
		}
	};

	private void processEmailResults(DataResult<GuestResult> obj) {
		mProgressBar.setVisibility(View.GONE);
		((LandingScreen)mContext).navigateToHomeScreen();

	}

	private ArrayList<String> getGmailAccounts(){
		ArrayList<String> accList = new ArrayList<String>();
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(mContext).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				accList.add(possibleEmail);
			}
		}
		return accList;
	}

	public PopupWindow popupWindowDeviceAccounts(ArrayList<String> accountsList,EditText emailEt) {
		ListView acclistView = new ListView(mContext);
		acclistView.setAdapter(emailAdapter(accountsList,emailEt));
		acclistView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		PopupWindow popupWindow = new PopupWindow(acclistView, acclistView.getMeasuredWidth(),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setContentView(acclistView);
		return popupWindow;
	}

	private ArrayAdapter<String> emailAdapter(final ArrayList<String> accountsList,final EditText emailEt) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, accountsList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				String text = accountsList.get(position);
				final TextView listItem = new TextView(mContext);
				listItem.setText(text);
				listItem.setTag(position);
				listItem.setTextSize(18);
				listItem.setPadding(10, 10, 10, 10);
				listItem.setTextColor(Color.BLACK);
				listItem.setBackgroundColor(Color.WHITE);
				listItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						emailEt.setText(listItem.getText().toString());
						if(mAccountsPopupWindow != null && mAccountsPopupWindow.isShowing()){
							mAccountsPopupWindow.dismiss();
						}
					}
				});
				return listItem;
			}
		};
		return adapter;
	}
}
