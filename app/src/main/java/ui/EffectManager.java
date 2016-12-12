package ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import core.gemster.R;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class EffectManager implements CustomAnimationDrawable.IAnimationFinishListener {

    private Context mContext;

    private CustomAnimationDrawable mAniEvolutionSuccess;
    private CustomAnimationDrawable mAniEvolutionFailed;
    private AnimationDrawable mAniEvolutionWhile;

    private final int ANI_DURATION = 15;

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

        mContext = context;

        initAniEvolutionSuccess();
        initAniEvolutionFailed();
        initAniEvolutionWhile();

        initAnimationListener();
    }

    private void initAniEvolutionSuccess() {
        mAniEvolutionSuccess = new CustomAnimationDrawable();
        mAniEvolutionSuccess.setOneShot(true);
        for (int idx = 1; idx <= 30; idx++) {
            String name = "success_effect00";
            if (idx < 10) name += "0";
            name += idx;
            int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
            Drawable drawable;
            drawable = ContextCompat.getDrawable(mContext, id);
            mAniEvolutionSuccess.addFrame(drawable, ANI_DURATION);
        }
    }

    private void initAniEvolutionFailed() {
        mAniEvolutionFailed = new CustomAnimationDrawable();
        mAniEvolutionFailed.setOneShot(true);
        for (int idx = 1; idx <= 30; idx++) {
            String name = "failed_effect00";
            if (idx < 10) name += "0";
            name += idx;
            int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
            Drawable drawable;
            drawable = ContextCompat.getDrawable(mContext, id);
            mAniEvolutionFailed.addFrame(drawable, ANI_DURATION);
        }
    }

    private void initAniEvolutionWhile() {
        mAniEvolutionWhile = new AnimationDrawable();
        mAniEvolutionWhile.setOneShot(false);
        for (int idx = 1; idx <= 35; idx++) {
            String name = "evoluting00";
            if (idx < 10) name += "0";
            name += idx;
            int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
            Drawable drawable;
            drawable = ContextCompat.getDrawable(mContext, id);
            mAniEvolutionWhile.addFrame(drawable, ANI_DURATION);
        }
    }

    private Animation getAniActionUpDown(boolean isDown) {
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
        animation.setAnimationListener(mAnimationListener);
        return animation;
    }

    private void initAnimationListener() {
        mAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!mIsBreathAnimationEnabled) {
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

    public void startSuccessEffect(ImageView view) {
        stopEvolutionWhileEffect(view);
        mAniEvolutionSuccess.setAnimationFinishListener(this, view);
        view.setVisibility(View.VISIBLE);
        view.setBackground(mAniEvolutionSuccess);
        mAniEvolutionSuccess.start();
    }

    public void startFailedEffect(ImageView view) {
        stopEvolutionWhileEffect(view);
        mAniEvolutionFailed.setAnimationFinishListener(this, view);
        view.setVisibility(View.VISIBLE);
        view.setBackground(mAniEvolutionFailed);
        mAniEvolutionFailed.start();
    }

    public void startEvolutionWhileEffect(ImageView view) {
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        view.setBackground(mAniEvolutionWhile);
        mAniEvolutionWhile.start();
    }

    public void stopEvolutionWhileEffect(ImageView view) {
        mAniEvolutionWhile.stop();
        view.clearAnimation();
    }

    private void processClickScaleAnimation(View view, boolean isDown) {
        view.startAnimation(getAniActionUpDown(isDown));
    }

    public void startClickScaleAnimation(View view) {
        processClickScaleAnimation(view, true);
    }

    public void endClickScaleAnimation(View view) {
        processClickScaleAnimation(view, false);
    }

    @Override
    public void onAnimationFinished(AnimationDrawable animation, ImageView view) {
        animation.stop();
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

    public void enableBreathAnimation(ImageView view) {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale_breath);
        view.startAnimation(anim);
        mIsBreathAnimationEnabled = true;
    }

    public void startGetEffect(View view) {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale_get);
        view.startAnimation(anim);
    }

    public void startCountAnimation(final TextView textView, int fromValue, int toValue) {
        int dif = toValue - fromValue;
        int duration = dif < 100 ? dif * 10 : 1000;
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(fromValue, toValue);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText("" + (int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void startVibrateAnimation(View view) {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_vibrate_horizontal);
        view.startAnimation(anim);
    }

    public void changeMonsterImageViewWithAnimation(Context context, final ImageView view, final int id) {
        // Change monster image view with evolution effect
        if (view.getTag() != null && (int) view.getTag() == id) {
            return;
        }
        view.setTag(id);
        final Animation anim_out = AnimationUtils.loadAnimation(context, R.anim.anim_scale_fade_out);
        final Animation anim_in = AnimationUtils.loadAnimation(context, R.anim.anim_fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("gemtest", "repeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("gemtest", "end");
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        enableBreathAnimation(view);
                    }
                });
                view.startAnimation(anim_in);
                view.setImageResource(id);
                enableBreathAnimation(view);
            }
        });
        view.startAnimation(anim_out);
    }
}
