package com.magicflix.goog.app.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.magicflix.goog.R;
import com.magicflix.goog.app.camera.CameraPreview;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.broadcasts.AppTimerBroadCastReceiver;
import com.magicflix.goog.broadcasts.PopUpDismissBroadCastReceiver;
import com.magicflix.goog.utils.MLogger;

public class CameraActivity extends BaseActivity implements OnClickListener, OnDismissListener {
	private static final String TAG = "CamTestActivity";
	private CameraPreview mCameraPreview;
	private Button mSwapBtn, mSnapBtn, mCancelBtn;
	private Camera mCamera;
	private boolean cameraFront = false;
	private PictureCallback mPicture;
	private AppTimerBroadCastReceiver mAppTimerBroadCastReceiver;
	private IntentFilter mAppTimerIntent,mPopUpDismissIntent;
	private PopUpDismissBroadCastReceiver mPopUpDismissBroadCastReceiver;
	private PopupWindow popupWindow ;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		init();
		setIdsToViews();
		setCameraPreview();
		setListnersToViews();
	}

	private void setIdsToViews() {
		mSwapBtn = (Button) findViewById(R.id.camera_screen_swap_btn);
		mSnapBtn = (Button) findViewById(R.id.camera_screen_snap_btn);
		mCancelBtn = (Button) findViewById(R.id.camera_screen_close_btn);
	}

	private void setListnersToViews() {
		mSwapBtn.setOnClickListener(this);
		mSnapBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	private void setCameraPreview() {
		mCameraPreview = new CameraPreview(this, mCamera);
		((FrameLayout) findViewById(R.id.layout)).addView(mCameraPreview);
		mCameraPreview.setKeepScreenOn(true);
	}

	private void init() {
		getActionBar().hide();
		mAppTimerIntent = new IntentFilter(Constants.INTENT_APP_TIMER_EXPIRED);

		mAppTimerBroadCastReceiver = new AppTimerBroadCastReceiver() {

			@Override
			protected void onTimerExpired() {
				showTimerAlert();

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

	private void showTimerAlert() {
		popupWindow =getTrialExpiredPopUp((Constants.APP_TIMER_VALUE == 0) ? getString(R.string.times_up_txt) : getString(R.string.app_timer_msg));
		popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
		popupWindow.setOnDismissListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mAppTimerBroadCastReceiver, mAppTimerIntent);
		registerReceiver(mPopUpDismissBroadCastReceiver, mPopUpDismissIntent);
		setUpCamera();
	}

	private void setUpCamera() {
		if (!hasCamera(this)) {
			showShortToast("Sorry, your phone does not have a camera!");
			finish();
		}
		if (mCamera == null) {
			if (findFrontFacingCamera() < 0) {
				//showShortToast("No front facing camera found.");
				mSwapBtn.setVisibility(View.GONE);
			}			
			mCamera = Camera.open(findBackFacingCamera());
			mPicture = getPictureCallback();
			mCameraPreview.refreshCamera(mCamera);
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mAppTimerBroadCastReceiver);
		unregisterReceiver(mPopUpDismissBroadCastReceiver);
		
		if(mCamera != null) {
			mCamera.stopPreview();
			mCameraPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
		super.onPause();
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				File pictureFile = getOutputMediaFile();
				if (pictureFile == null) {
					return;
				}
				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					Intent intent=new Intent();  
					intent.putExtra("filePath",pictureFile.getAbsolutePath());  
					setResult(FamilySetUpActivity.TAKE_PICTURE_REQUEST,intent);  
					finish();

				} catch (FileNotFoundException e) {
					mCameraPreview.refreshCamera(mCamera);
					MLogger.logInfo(TAG, "Exception in saving the photo :: "+e.getMessage());
				} catch (IOException e) {
					mCameraPreview.refreshCamera(mCamera);
					MLogger.logInfo(TAG, "Exception in saving the photo :: "+e.getMessage());
				}
				mCameraPreview.refreshCamera(mCamera);
			}
		};
		return picture;
	}

	private  File getOutputMediaFile() {

		PackageManager m = this.getPackageManager();
		String s = getPackageName();
		PackageInfo p = null;
		try {
			p = m.getPackageInfo(s, 0);
		} catch (NameNotFoundException e) {
			MLogger.logInfo(TAG, "Exception in  getOutputMediaFile"+e.getLocalizedMessage());
		}
		s = p.applicationInfo.dataDir;



		//		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (s + "/Profiles");
		dir.mkdirs();
		//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String fileName = String.format("%d.jpg", System.currentTimeMillis());
		File outFile = new File(dir, fileName);
		return outFile;
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_screen_swap_btn:
			swapCamera();
			break;
		case R.id.camera_screen_snap_btn:
			mCamera.takePicture(null, null, mPicture);
			break;
		case R.id.camera_screen_close_btn:
			this.finish();
			break;

		default:
			break;
		}

	}

	private void swapCamera() {
		int camerasNumber = Camera.getNumberOfCameras();
		if (camerasNumber > 1) {
			releaseCamera();
			chooseCamera();
		} else {
			Toast toast = Toast.makeText(this, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true; 
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	private boolean hasCamera(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	public void chooseCamera() {
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {
				mCamera = Camera.open(cameraId);	
				mPicture = getPictureCallback();
				mCameraPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mCameraPreview.refreshCamera(mCamera);
			}
		}
	}

	@Override
	public void onDismiss() {
		Constants.IS_APP_TIMER_SHOWN = true;
	}


}

