package ui;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 * Created by WONSEOK OH on 2016-12-06.
 */

public class CustomAnimationDrawable extends AnimationDrawable {

    private ImageView mImageView;

    public interface IAnimationFinishListener {
        void onAnimationFinished(AnimationDrawable animation, ImageView view);
    }

    private IAnimationFinishListener animationFinishListener;

    public IAnimationFinishListener getAnimationFinishListener() {
        return animationFinishListener;
    }

    public void setAnimationFinishListener(
            IAnimationFinishListener animationFinishListener, ImageView view) {
        this.animationFinishListener = animationFinishListener;
        this.mImageView = view;
    }

    @Override
    public void start() {
        if (this.isRunning()) {
            this.stop();
        }
        super.start();
    }

    @Override
    public boolean selectDrawable(int idx) {
        boolean ret = super.selectDrawable(idx);

        if ((idx != 0) && (idx == getNumberOfFrames() - 1)) {
            if (animationFinishListener != null)
                animationFinishListener.onAnimationFinished(this, mImageView);
        }

        return ret;
    }

}