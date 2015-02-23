package com.magicflix.goog.animation;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import com.magicflix.goog.animation.FasterAnimationsContainer.OnAnimationStoppedListener;
import com.magicflix.goog.app.utils.Constants;

public class AnimationsContainer {
    public int FPS = 2;  // animation FPS

    // single instance procedures
    private static AnimationsContainer mInstance;

    private AnimationsContainer() {
    };

    public static AnimationsContainer getInstance() {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        return mInstance;
    }

    // animation progress dialog frames
    private int[] mProgressAnimFrames = Constants.SPLASH_IMAGE_RESOURCES ;

    // animation splash screen frames
    private int[] mSplashAnimFrames = Constants.SPLASH_IMAGE_RESOURCES;

    /**
     * @param imageView 
     * @return progress dialog animation
     */
    public FramesSequenceAnimation createProgressDialogAnim(ImageView imageView) {
        return new FramesSequenceAnimation(imageView, mProgressAnimFrames, FPS);
    }

    /**
     * @param imageView
     * @return splash screen animation
     */
    public FramesSequenceAnimation createSplashAnim(ImageView imageView) {
        return new FramesSequenceAnimation(imageView, mSplashAnimFrames, FPS);
    }

    /**
     * AnimationPlayer. Plays animation frames sequence in loop
     */
public class FramesSequenceAnimation {
    private int[] mFrames; // animation frames
    private int mIndex; // current frame
    private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
    private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
    private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
    private Handler mHandler;
    private int mDelayMillis;
    private OnAnimationStoppedListener mOnAnimationStoppedListener;

    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;

    public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
        mHandler = new Handler();
        mFrames = frames;
        mIndex = -1;
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
        mShouldRun = false;
        mIsRunning = false;
        mDelayMillis = 1000 / fps;

        imageView.setImageResource(mFrames[0]);

        // use in place bitmap to save GC work (when animation images are the same size & type)
        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config = bmp.getConfig();
            mBitmap = Bitmap.createBitmap(width, height, config);
            mBitmapOptions = new BitmapFactory.Options();
            // setup bitmap reuse options. 
            mBitmapOptions.inBitmap = mBitmap;
            mBitmapOptions.inMutable = true;
            mBitmapOptions.inSampleSize = 1;
        }
    }

    private int getNext() {
        mIndex++;
        if (mIndex >= mFrames.length)
            mIndex = 0;
        return mFrames[mIndex];
    }

    /**
     * Starts the animation
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ImageView imageView = mSoftReferenceImageView.get();
                if (!mShouldRun || imageView == null) {
                    mIsRunning = false;
                    if (mOnAnimationStoppedListener != null) {
                        mOnAnimationStoppedListener.onAnimationStopped();
                    }
                    return;
                }

                mIsRunning = true;
                mHandler.postDelayed(this, mDelayMillis);

                if (imageView.isShown()) {
                    int imageRes = getNext();
                    if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(imageRes);
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                    } else {
                        imageView.setImageResource(imageRes);
                    }
                }

            }
        };

        mHandler.post(runnable);
    }

        /**
         * Stops the animation
         */
        public synchronized void stop() {
            mShouldRun = false;
        }
    }
}
