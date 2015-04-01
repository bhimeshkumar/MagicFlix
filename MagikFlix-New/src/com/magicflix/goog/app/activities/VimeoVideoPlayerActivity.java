package com.magicflix.goog.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.VideoView;

import com.magicflix.goog.R;
import com.magicflix.goog.api.data.DataResult;
import com.magicflix.goog.app.api.MFlixJsonBuilder;
import com.magicflix.goog.app.api.MFlixJsonBuilder.WebRequestType;
import com.magicflix.goog.app.api.requests.VimeoVideoRequest;
import com.magicflix.goog.app.api.results.VimeoVideoResult;
import com.magicflix.goog.app.asyntasks.DataApiAsyncTask;

public class VimeoVideoPlayerActivity extends BaseActivity {

	public static final String TAG = "VimeoVideoPlayerActivity";
	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		mVideoView = (VideoView) findViewById(R.id.canvas);
		getActionBar().hide();
		getVimeoVideoURL();


		//        getWindow().setFormat(PixelFormat.TRANSPARENT);        
		//
		//                final long videoId =Long.valueOf("120986691");
		//                if (videoId == -1) throw new IllegalStateException("Video ID must be passed to player");
		//                
		//                final VideoView videoView = (VideoView) findViewById(R.id.canvas);
		//                
		//                Log.d(TAG, "Running video player for video " + videoId);
		//                
		//                new VimeoVideoPlayingTask(this, videoView.getHolder()) {
		//                    
		//                    private ProgressDialog progressDialog;
		//                        
		//                        @Override protected void onPreExecute() {
		//                            progressDialog = ProgressDialog.show(VimeoVideoPlayerActivity.this, "", "Loading...", true);
		//                                super.onPreExecute();
		//                        };
		//                        
		//                        @Override protected void onPostExecute(FileInputStream dataSource) {
		//                            progressDialog.dismiss();
		//                                super.onPostExecute(dataSource);                
		//                        };
		//                        
		//                        @Override protected void onNoSpaceForVideoCache(final long required, final long actual) {
		//                                runOnUiThread(new Runnable() {                                  
		//                                        @Override public void run() {
		////                                                Dialogs.makeLongToast(Player.this, getString(R.string.no_space_for_video_cache, 
		////                                                                                                     required >> 10, actual >> 10));
		//                                                
		//                                        }
		//                                }); 
		//                                finish();                               
		//                        };
		//                        
		//                        @Override protected void onFailedToGetVideoStream() {
		//                runOnUiThread(new Runnable() {                  
		//                    @Override public void run() {
		//                        //Dialogs.makeLongToast(Player.this, getString(R.string.failed_to_get_video_stream));
		//                    }
		//                }); 
		//                finish();                           
		//                        };
		//                        
		//                }.execute(videoId);

	}

	@Override
	protected void onStop() {
		super.onStop();

		//            VimeoVideoPlayingTask.ensureCleanedUp();
	}

	private void getVimeoVideoURL() {
		VimeoVideoRequest  vimeoVideoRequest = new VimeoVideoRequest();
		vimeoVideoRequest.videoId = "120986691";
		vimeoVideoRequest.requestDelegate = new MFlixJsonBuilder();
		vimeoVideoRequest.requestType =  WebRequestType.GET_VIMEO_VIDEO;	
		new DataApiAsyncTask(true, this, vimeoVideoHandler, null).execute(vimeoVideoRequest);
	}

	private Handler vimeoVideoHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			processVideoResults((DataResult<VimeoVideoResult>) msg.obj);
		}
	};

	private void processVideoResults(DataResult<VimeoVideoResult> obj) {
		if(obj.entity != null ){
			String videoURL = obj.entity.request.files.h264.mobile.url;
			mVideoView.setVideoPath(videoURL);
			mVideoView.requestFocus();
			mVideoView.start();
		}
	}

}

