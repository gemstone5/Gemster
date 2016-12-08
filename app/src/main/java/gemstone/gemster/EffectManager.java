package gemstone.gemster;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class EffectManager implements CustomAnimationDrawable.IAnimationFinishListener {

    private final CustomAnimationDrawable mAniEvolutionSuccess;
    private final CustomAnimationDrawable mAniEvolutionFailed;

    private Animation mAniActionUp;
    private Animation mAniActionDown;

    private final int ANI_DURATION = 40;

    private boolean mIsBreathAnimationEnabled = false;

    public enum CompleteEventMode {
        EVOLUTION, BREATH_INTERCEPT
    }

    public interface EffectCompleteListener {
        void complete(CompleteEventMode mode);
    }

    private EffectCompleteListener mListener;
    private Animation.AnimationListener mAnimationListener;

    public void setListener(EffectCompleteListener listener) {
        mListener = listener;
    }

    public EffectManager(Context context) {

        mAniEvolutionSuccess = new CustomAnimationDrawable();
        mAniEvolutionSuccess.setOneShot(true);
        for (int idx = 1; idx <= 35; idx++) {
            String name = "success_effect00";
            if (idx < 10) name += "0";
            name += idx;
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            Drawable drawable;
            drawable = ContextCompat.getDrawable(context, id);
            mAniEvolutionSuccess.addFrame(drawable, ANI_DURATION);
        }

        mAniEvolutionFailed = new CustomAnimationDrawable();
        mAniEvolutionFailed.setOneShot(true);
        for (int idx = 1; idx <= 30; idx++) {
            String name = "failed_effect00";
            if (idx < 10) name += "0";
            name += idx;
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            Drawable drawable;
            drawable = ContextCompat.getDrawable(context, id);
            mAniEvolutionFailed.addFrame(drawable, ANI_DURATION);
        }

        mAniActionDown = initAniActionUpDown(true);
        mAniActionUp = initAniActionUpDown(false);

        initAnimationListener();
        setAnimationListener();
    }

    private Animation initAniActionUpDown(boolean isDown) {
        float startValue = isDown ? 1f : 1.05f;
        float endValue = isDown ? 1.05f : 1f;
        Animation animation = new ScaleAnimation(
                startValue, endValue, // Start and end values for the X axis scaling
                startValue, endValue, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        animation.setFillAfter(true); // Needed to keep the result of the animation
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    private void initAnimationListener() {
        mAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animation == null) return;
                if (!mIsBreathAnimationEnabled && (animation.equals(mAniActionDown) || animation.equals(mAniActionUp))) {
                    if (mListener != null) {
                        mListener.complete(CompleteEventMode.BREATH_INTERCEPT);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    private void setAnimationListener() {
        mAniActionDown.setAnimationListener(mAnimationListener);
        mAniActionUp.setAnimationListener(mAnimationListener);
    }

    public void startSuccessEffect(ImageView view) {
        mAniEvolutionSuccess.setAnimationFinishListener(this, view);
        view.setVisibility(View.VISIBLE);
        view.setBackground(mAniEvolutionSuccess);
        startAnimationWithEndTimer(mAniEvolutionSuccess);
    }

    public void startFailedEffect(ImageView view) {
        mAniEvolutionFailed.setAnimationFinishListener(this, view);
        view.setVisibility(View.VISIBLE);
        view.setBackground(mAniEvolutionFailed);
        startAnimationWithEndTimer(mAniEvolutionFailed);
    }

    private void startAnimationWithEndTimer(final AnimationDrawable animation) {
        animation.start();
        setAnimationEndTimer(animation);
    }

    private void setAnimationEndTimer(final AnimationDrawable animation) {
        long totalDuration = 0;
        for (int i = 0; i < animation.getNumberOfFrames(); i++) {
            totalDuration += animation.getDuration(i);
        }
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                animation.stop();
            }
        };
        timer.schedule(timerTask, totalDuration);
    }

    private void processClickScaleAnimation(View view, boolean isStart) {
        view.startAnimation(isStart ? mAniActionDown : mAniActionUp);
    }

    public void startClickScaleAnimation(View view) {
        processClickScaleAnimation(view, true);
    }

    public void endClickScaleAnimation(View view) {
        processClickScaleAnimation(view, false);
    }

    @Override
    public void onAnimationFinished(AnimationDrawable animation, ImageView view) {
        if (animation.isRunning()) {
            animation.stop();
        }
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        if (mListener != null) {
            if (animation.equals(mAniEvolutionSuccess) || animation.equals(mAniEvolutionFailed)) {
                mListener.complete(CompleteEventMode.EVOLUTION);
            }
        }
    }

    public void disableBreathAnimation() {
        mIsBreathAnimationEnabled = false;
    }

    public void enableBreathAnimation(Context context, ImageView view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_scale_breath);
        view.startAnimation(anim);
        mIsBreathAnimationEnabled = true;
    }
}
