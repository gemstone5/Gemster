package ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by WONSEOK OH on 2016-12-06.
 */

public class CustomAnimationDrawable extends AnimationDrawable {

    private ImageView mImageView;
    private Handler mHandler;

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

    @Override
    public void start() {
        super.start();
        mHandler = new Handler();
        checkIfAnimationDone();
    }

    private void checkIfAnimationDone() {
        final AnimationDrawable animation = getParent();
        int timeBetweenChecks = 40;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (animation.getCurrent() != animation.getFrame(getParent().getNumberOfFrames() - 1)) {
                    checkIfAnimationDone();
                } else {
                    mAnimationFinishListener.onAnimationFinished(animation, mImageView);
                }
            }
        }, timeBetweenChecks);
    }

}