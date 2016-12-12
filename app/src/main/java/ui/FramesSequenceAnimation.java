package ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class FramesSequenceAnimation {
    private int mTag;
    private ArrayList<Integer> mFrames; // animation frames
    private int mIndex; // current frame
    private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
    private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
    private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
    private Handler mHandler;
    private int mDelayMillis;
    private boolean mLoop;

    private OnAnimationStoppedListener mOnAnimationStoppedListener;

    public void setOnAnimationStoppedListener(OnAnimationStoppedListener listener) {
        mOnAnimationStoppedListener = listener;
    }

    public interface OnAnimationStoppedListener {
        void AnimationStopped(int tag, ImageView view);
    }

    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;

    public FramesSequenceAnimation(int tag, ArrayList<Integer> frames, int fps, boolean loop) {
        mHandler = new Handler();
        mTag = tag;
        mFrames = frames;
        mIndex = -1;
        mShouldRun = false;
        mIsRunning = false;
        mDelayMillis = 1000 / fps;
        mLoop = loop;
    }

    public void setView(ImageView view) {
        mSoftReferenceImageView = new SoftReference<>(view);
        view.setImageResource(mFrames.get(0));

        // use in place bitmap to save GC work (when animation images are the same size & type)
        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bmp = ((BitmapDrawable) view.getDrawable()).getBitmap();
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
        if (mIndex >= mFrames.size()) {
            mIndex = 0;
            if (!mLoop) {
                return -1;
            }
        }
        return mFrames.get(mIndex);
    }

    public synchronized void start(ImageView view) {

        setView(view);

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
                        mOnAnimationStoppedListener.AnimationStopped(mTag, imageView);
                    }
                    return;
                }

                mIsRunning = true;
                mHandler.postDelayed(this, mDelayMillis);

                if (imageView.isShown()) {
                    int imageRes = getNext();

                    if (imageRes == -1) {
                        stop();
                    }

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

    public synchronized void stop() {
        mShouldRun = false;
    }
}