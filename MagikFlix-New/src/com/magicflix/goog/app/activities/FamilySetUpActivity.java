package com.magicflix.goog.app.activities;

import it.sephiroth.android.library.widget.HListView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.adapters.ChildProfileAdapter;
import com.magicflix.goog.app.api.Model.UserProfile;
import com.magicflix.goog.app.db.Db4oHelper;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.app.utils.Utils;
import com.magicflix.goog.broadcasts.AppTimerBroadCastReceiver;
import com.magicflix.goog.broadcasts.PopUpDismissBroadCastReceiver;
import com.magicflix.goog.utils.MLogger;

public class FamilySetUpActivity extends BaseActivity implements OnClickListener, OnEditorActionListener {

	public static final int TAKE_PICTURE_REQUEST = 1;
	public static final String TAG = FamilySetUpActivity.class.getName();
	private static final String[] AGES = {"2", "3","4","5","6","7","8","9","10","11","12",};

	private ImageView mTakePictureIV,mProfileDeteteIV;
	private HListView mChildProfileList;
	private ChildProfileAdapter mChildProfileAdapter;
	private Button mAddProfileBtn;
	private EditText mAgeSelectionEt , mChildNameEt,mEmailEt;
	private Button mOkBtn,mInfoBtn , mCameraRollBtn;
	private PopupWindow mAgeSelectionPopUp;
	private MagikFlix mApplication;
	private int mSelectedProfile = 0;
	private Db4oHelper mDb4oProvider;
	private RelativeLayout mDeleteLayout;

	private boolean blueAdded, greenAdded ,kidAdded ,pinkAdded,purpleAdded;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private IntentFilter mAppTimerIntent , mPopUpDismissIntent;
	private PopupWindow popupWindow;
	private boolean mIsBackbtnPressed = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_family_setup);
		init();
		setIdsToViews();
		setOnListnersToViews();
		setDefalutAge();
		setProfileAdapter();
		setUpUI(mSelectedProfile);
		setProfileAvatar();
	}

	public void setUpUI(int position) {
		try {
			mDeleteLayout.setVisibility(mDb4oProvider.getUserProfiles().size() >1 ? View.VISIBLE :View.INVISIBLE);
			if(mDb4oProvider.getUserProfiles().size() == 5){
				mAddProfileBtn.setVisibility(View.INVISIBLE);
			}else{
				mAddProfileBtn.setVisibility(View.VISIBLE);
			}
			mAgeSelectionEt.setText(mDb4oProvider.getUserProfileById(mSelectedProfile).age);
			mChildNameEt.setText(mDb4oProvider.getUserProfileById(mSelectedProfile).childName);
			mEmailEt.setText(mDb4oProvider.getUserProfileById(mSelectedProfile).email);
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in setUpUI"+e.getMessage());
		}
	}

	private void setDefalutAge() {
		mAgeSelectionEt.setText(String.valueOf(mApplication.getDefaultAge()));

	}

	private void setProfileAdapter() {
		mChildProfileAdapter = new ChildProfileAdapter(this,mDb4oProvider.getUserProfiles());
		mChildProfileList.setAdapter(mChildProfileAdapter);

	}

	private void setOnListnersToViews() {
		mTakePictureIV.setOnClickListener(this);
		mAddProfileBtn.setOnClickListener(this);
		mAgeSelectionEt.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mInfoBtn.setOnClickListener(this);
		mCameraRollBtn.setOnClickListener(this);
		mProfileDeteteIV.setOnClickListener(this);
		mEmailEt.setOnEditorActionListener(this);
		mChildNameEt.setOnEditorActionListener(this);

	}

	private void setIdsToViews() {
		mTakePictureIV = (ImageView)findViewById(R.id.family_screen_take_picture_iv);
		mChildProfileList = (HListView)findViewById(R.id.family_setup_screen_profile_list);
		mAddProfileBtn = (Button)findViewById(R.id.family_screen_add_profile_btn);
		mAgeSelectionEt = (EditText)findViewById(R.id.family_screen_child_age_et);
		mChildNameEt = (EditText)findViewById(R.id.family_screen_child_name_et);
		mOkBtn = (Button)findViewById(R.id.family_screen_ok_btn);
		mInfoBtn = (Button)findViewById(R.id.family_screen_info_btn);
		mEmailEt = (EditText)findViewById(R.id.family_screen_child_email_et);
		mCameraRollBtn = (Button)findViewById(R.id.family_screen_camera_roll_btn);
		mDeleteLayout = (RelativeLayout)findViewById(R.id.family_screen_delete_layout);
		mProfileDeteteIV = (ImageView)findViewById(R.id.family_screen_delete_iv);
	}

	private void init() {
		getActionBar().hide();
		mApplication = (MagikFlix)getApplication();
		mSelectedProfile = mApplication.getSelectedProfileIndex();
		mDb4oProvider = new Db4oHelper(this);
		mAgeSelectionPopUp = getAgeSelectionPopUp();

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

	private PopupWindow getAgeSelectionPopUp(){

		View agePopUpView = getLayoutInflater().inflate(R.layout.age_selection_pop_up, null);
		PopupWindow popupMessage = new PopupWindow(agePopUpView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		ListView ageListView = (ListView)agePopUpView.findViewById(R.id.age_selection_list_view);
		ageListView.setAdapter(new ArrayAdapter<String>(this, R.layout.age_slection_list_item,AGES));
		ageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				mAgeSelectionEt.setText(AGES[position]);
				if(mAgeSelectionPopUp != null && mAgeSelectionPopUp.isShowing()){
					mAgeSelectionPopUp.dismiss();
				}

			}
		});
		popupMessage.setBackgroundDrawable(new BitmapDrawable());
		popupMessage.setOutsideTouchable(true);
		popupMessage.setContentView(agePopUpView);

		return popupMessage;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.family_screen_take_picture_iv:
			navigateToCameraScreen();
			break;
		case R.id.family_screen_add_profile_btn:
			saveUserProfile(true , false);
			break;

		case R.id.family_screen_child_age_et:
			mAgeSelectionPopUp.showAsDropDown(mAgeSelectionEt);
			//			showAgeMenu();
			break;

		case R.id.family_screen_ok_btn:	
			saveUserProfile(false , true);
			break;
		case R.id.family_screen_info_btn:	
			startActivity(new Intent(this,TermsOfUseActivity.class));
			break;
		case R.id.family_screen_camera_roll_btn:
			startActivity(new Intent(this,VideoListActivity.class));
			break;

		case R.id.family_screen_delete_iv:
			mProfileDeteteIV.setEnabled(false);
			mProfileDeteteIV.setClickable(false);
			showDeleteProfilePopUp();
			break;

		default:
			break;
		}

	}

	private void addNewProfile() {
		int profileSize = mDb4oProvider.getUserProfiles().size();
		mChildNameEt.setText("");
		mAgeSelectionEt.setText(mApplication.getDefaultAge());
		mEmailEt.setText(mApplication.getEmail());
		UserProfile userProfile = new UserProfile();
		userProfile.childName = mChildNameEt.getText().toString();
		userProfile.age = mAgeSelectionEt.getText().toString();
		addedDeaultAvatars();
		userProfile.deaultAvatar = getDefaultProfilePath();

		userProfile.id = profileSize ;
		userProfile.email = mEmailEt.getText().toString();
		mDb4oProvider.store(userProfile);
		mDeleteLayout.setVisibility(mDb4oProvider.getUserProfiles().size() >1 ? View.VISIBLE :View.INVISIBLE);
		if(mDb4oProvider.getUserProfiles().size() == 5){
			mAddProfileBtn.setVisibility(View.INVISIBLE);
		}
		mSelectedProfile = profileSize;
		mApplication.setSelectedProfileIndex(mSelectedProfile);
		setProfileAdapter();
		setProfileAvatar();
	}

	public boolean saveUserProfile(boolean canAddNewProfile, boolean canFinish) {
		Constants.IS_PROFILE_SELECTION_CHANGED = true;
		UserProfile userProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());
		if(userProfile != null){
			userProfile.childName = mChildNameEt.getText().toString();
			userProfile.age = mAgeSelectionEt.getText().toString();
			if( mEmailEt.getText().toString().length() > 0){
				if(Utils.isValidEmail( mEmailEt.getText().toString())){
					userProfile.email = mEmailEt.getText().toString();
					mDb4oProvider.store(userProfile);
					if(canAddNewProfile)
						addNewProfile();
					if(canFinish){
						FamilySetUpActivity.this.finish();
					}
					return true;
				}else{
					showAlertDialog(getString(R.string.email_invalid_msg));
					return false;
					//showShortToast("Please enter valid email");
				}
			}else{
				showAlertDialog(getString(R.string.email_invalid_msg));
				return false;
				//showShortToast("Please enter email");
			}
		}
		return false;

	}

	private String getDefaultProfilePath(){
		String defaultProfilePath = null ;
		if(!blueAdded){
			return defaultProfilePath =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_blue;
		}
		if(!greenAdded){
			return defaultProfilePath =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_green;
		}
		if(!kidAdded){
			return defaultProfilePath =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_kid;
		}
		if(!pinkAdded){
			return defaultProfilePath =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_pink;
		}
		if(!purpleAdded){
			return defaultProfilePath =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_purple;
		}
		return defaultProfilePath;
	}

	private int getDefaultAvatarIndex(UserProfile userProfile) {
		List<UserProfile> userProfileLsit = mDb4oProvider.getUserProfiles();
		userProfile.deaultAvatar =  "android.resource://com.magicflix.goog/" + R.drawable.avatar_kid;
		for (int i = 0; i < userProfileLsit.size(); i++) {
			if(userProfile.deaultAvatar.equalsIgnoreCase(userProfileLsit.get(i).deaultAvatar)){
				return i;
			}
		}
		return -1;
	}

	private void deleteProfile() {
		mDb4oProvider.deleteProfileById(mApplication.getSelectedProfileIndex());

		List<UserProfile> profileList = mDb4oProvider.getUserProfiles();
		for (int i = 0; i < profileList.size(); i++) {
			UserProfile userProfile = profileList.get(i);
			userProfile.id = i;
			mDb4oProvider.store(profileList.get(i));
		}
		mSelectedProfile =  mApplication.getSelectedProfileIndex() -1;

		if(mSelectedProfile == -1){
			mSelectedProfile = 0;
		}

		mApplication.setSelectedProfileIndex(mSelectedProfile);

		if(profileList.size() == 1){
			mDeleteLayout.setVisibility(View.INVISIBLE);
		}

		setUpUI(mSelectedProfile);
		setProfileAdapter();
		setProfileAvatar();

	}

	/*private void saveProfile() {
		Constants.IS_PROFILE_SELECTION_CHANGED = true;
		UserProfile userProfile = mDb4oProvider.getUserProfileById(mSelectedProfile);
		if(userProfile != null){
			userProfile.childName = mChildNameEt.getText().toString().length() > 0 ? mChildNameEt.getText().toString() : "" ; 
			userProfile.age = mAgeSelectionEt.getText().toString();
			userProfile.
			mDb4oProvider.store(userProfile);
		}
	}*/

	private void setProfileAvatar() {
		try {

			UserProfile userProfile = mDb4oProvider.getUserProfiles().get(mApplication.getSelectedProfileIndex());

			if(userProfile.avatar != null && userProfile.avatar.length() > 0){
				Bitmap myBitmap = BitmapFactory.decodeFile(userProfile.avatar);
				mTakePictureIV.setImageBitmap(myBitmap);

			}else{
				mTakePictureIV.setImageURI(Uri.parse(userProfile.deaultAvatar));
			}
		} catch (Exception e) {
			MLogger.logInfo(TAG, "Exception in setProfileAvatar :: "+e.getMessage());
		}

	}


	private void navigateToCameraScreen() {
		Intent intent = new Intent(this,CameraActivity.class);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE_REQUEST) {
			try {
				if(data != null){
					String filePath = data.getStringExtra("filePath");
					UserProfile userProfile = mDb4oProvider.getUserProfileById(mSelectedProfile);
					userProfile.avatar = filePath;
					Bitmap avatarBitmap = BitmapFactory.decodeFile(filePath);
					mDb4oProvider.store(userProfile);
					mTakePictureIV.setImageBitmap(avatarBitmap);
					mChildProfileAdapter.notifyDataSetChanged();
				}
			} catch (Exception e) {
				MLogger.logInfo(TAG, "Exception in onActivityResult ::  "+e.getMessage());
			}
		}
	}

	private void deleteImage(String path) {
		File file = new File(path);
		file.delete();
		if(file.exists()){
			try {
				file.getCanonicalFile().delete();
			} catch (IOException e) {
				MLogger.logInfo(TAG, "Exception in deleteImage"+e.getLocalizedMessage());
			}
			if(file.exists()){
				getApplicationContext().deleteFile(file.getName());
			}
		}

	}



	public void updateUI(int position) {

		mSelectedProfile = position;
		mApplication.setSelectedProfileIndex(mSelectedProfile);
		String selectedAge = AGES[position];
		mAgeSelectionEt.setText(selectedAge);
		setProfileAvatar();
		setUpUI(position);
	}

	private void addedDeaultAvatars(){
		blueAdded = false; 
		greenAdded = false ; 
		kidAdded = false ;
		pinkAdded = false;
		purpleAdded = false;
		for (UserProfile userProfile : mDb4oProvider.getUserProfiles()) {
			if(userProfile.deaultAvatar.equalsIgnoreCase("android.resource://com.magicflix.goog/" + R.drawable.avatar_blue)){
				blueAdded = true;
			}if(userProfile.deaultAvatar.equalsIgnoreCase("android.resource://com.magicflix.goog/" + R.drawable.avatar_green)){
				greenAdded = true;
			}
			if(userProfile.deaultAvatar.equalsIgnoreCase("android.resource://com.magicflix.goog/" + R.drawable.avatar_kid)){
				kidAdded = true;
			}
			if(userProfile.deaultAvatar.equalsIgnoreCase("android.resource://com.magicflix.goog/" + R.drawable.avatar_pink)){
				pinkAdded = true;
			}
			if(userProfile.deaultAvatar.equalsIgnoreCase("android.resource://com.magicflix.goog/" + R.drawable.avatar_purple)){
				purpleAdded = true;
			}

		}
	}

	private void showDeleteProfilePopUp(){
		LayoutInflater inflater = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.profile_delete_pop_up,null);
		TextView deleteYes = (TextView)layout.findViewById(R.id.delete_yes_tv);
		TextView deleteNo = (TextView)layout.findViewById(R.id.delete_no_tv);
		final PopupWindow mCustomPopUp = new PopupWindow(layout, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mCustomPopUp.setAnimationStyle(R.style.PopUpAnimation);
		mCustomPopUp.setFocusable(true);
		mCustomPopUp.setContentView(layout);
		mCustomPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);

		deleteYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCustomPopUp.dismiss();
				deleteProfile();

			}
		});
		deleteNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCustomPopUp.dismiss();

			}
		});

		mCustomPopUp.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mProfileDeteteIV.setEnabled(true);
				mProfileDeteteIV.setClickable(true);

			}
		});

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
			if(v == mEmailEt){
				String email = mEmailEt.getText().toString();
				if(email != null && email.length() >0){
					if(Utils.isValidEmail(email)){
						UserProfile userProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());
						userProfile.email = email;
						mDb4oProvider.store(userProfile);
					}else{
						//showShortToast("Please enter valid email address");
						showAlertDialog(getString(R.string.email_invalid_msg));
					}
				}else{
					showAlertDialog(getString(R.string.email_invalid_msg));
					//showShortToast("Please enter your email");	
				}
			}
			else if(v == mChildNameEt){
				
				String name = mChildNameEt.getText().toString();
				if(name != null && name.length() >0){
//					if(Utils.isValidUserName((name))){
						UserProfile userProfile = mDb4oProvider.getUserProfileById(mApplication.getSelectedProfileIndex());
						userProfile.childName = name;
						mDb4oProvider.store(userProfile);
//					}else{
//						//showShortToast("Please enter valid email address");
//						showAlertDialog(getString(R.string.info_valid_username_msg));
//					}
				}

			}

		}  
		return false;
	}

	
	
	@Override
	public void onBackPressed() {
		mIsBackbtnPressed = true;
		saveUserProfile(false , true);
		super.onBackPressed();
		
	}

}
