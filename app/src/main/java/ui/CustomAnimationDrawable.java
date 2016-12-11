package ui;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WONSEOK OH on 2016-12-06.
 */

public class CustomAnimationDrawable extends AnimationDrawable {

    private ImageView mImageView;
    private Handler mHandler;
    private ArrayList<Drawable> mFrameList;
    private HashMap<Integer, Boolean> mIsFramed;

    public interface IAnimationFinishListener {
        void onAnimationFinished(AnimationDrawable animation, ImageView view);
    }

    private IAnimationFinishListener mAnimationFinishListener;

    public void setAnimationFinishListener(
            IAnimationFinishListener animationFinishListener, ImageView view) {
        this.mAnimationFinishListener = animationFinishListener;
        this.mImageView = view;
    }

    private AnimationDrawable getParent() {
        return this;
    }

    public CustomAnimationDrawable() {
        mFrameList = new ArrayList<>();
        mIsFramed = new HashMap<>();
    }

    @Override
    public void addFrame(Drawable frame, int duration) {
        super.addFrame(frame, duration);
        mFrameList.add(frame);
    }

    @Override
    public void start() {
        super.start();
        mHandler = new Handler();
        checkIfAnimationDone();
    }


    private void checkIfAnimationDone() {
        final AnimationDrawable animation = getParent();
        int timeBetweenChecks = 30;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Drawable current = animation.getCurrent();
                int index = mFrameList.indexOf(current);
                if (index == -1 || index >= mFrameList.size() - 1 || (mIsFramed.get(index) != null && mIsFramed.get(index))) {
                    mAnimationFinishListener.onAnimationFinished(animation, mImageView);
                    mIsFramed.clear();
                } else {
                    Log.d("gemtest", String.valueOf(mFrameList.indexOf(current)));
                    mIsFramed.put(mFrameList.indexOf(current), true);
                    checkIfAnimationDone();
                }
            }
        }, timeBetweenChecks);
    }

}