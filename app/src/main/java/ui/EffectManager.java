package ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import core.gemster.R;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class EffectManager implements FramesSequenceAnimation.OnAnimationStoppedListener {

    private Context mContext;

    private FramesSequenceAnimation mAniEvolutionSuccess;
    private FramesSequenceAnimation mAniEvolutionFailed;
    private FramesSequenceAnimation mAniEvolutionWhile;

    private final int ANI_DURATION = 15;

    private boolean mIsBreathAnimationEnabled = false;

    private final int TAG_ANIM_EVOLUTION_SUCCESS = 0;
    private final int TAG_ANIM_EVOLUTION_FAILED = 1;
    private final int TAG_ANIM_EVOLUTION_WHILE = 2;

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

        initEvolutionAnimations();
        initAnimationListener();
    }

    public void initEvolutionAnimations() {
        mAniEvolutionSuccess = new FramesSequenceAnimation(TAG_ANIM_EVOLUTION_SUCCESS, getFrameList("success_effect", 30), 60, false);
        mAniEvolutionFailed = new FramesSequenceAnimation(TAG_ANIM_EVOLUTION_FAILED, getFrameList("failed_effect", 30), 60, false);
        mAniEvolutionWhile = new FramesSequenceAnimation(TAG_ANIM_EVOLUTION_WHILE, getFrameList("evoluting", 35), 60, true);

        mAniEvolutionSuccess.setOnAnimationStoppedListener(this);
        mAniEvolutionFailed.setOnAnimationStoppedListener(this);
        mAniEvolutionWhile.setOnAnimationStoppedListener(this);
    }

    public ArrayList<Integer> getFrameList(String preName, int count) {
        ArrayList<Integer> mList = new ArrayList<>();
        for (int idx = 1; idx <= count; idx++) {
            String name = preName + "00";
            if (idx < 10) name += "0";
            name += idx;
            int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
            mList.add(id);
        }
        return mList;
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
        mAniEvolutionSuccess.start(view);
    }

    public void startFailedEffect(ImageView view) {
        stopEvolutionWhileEffect(view);
        mAniEvolutionFailed.start(view);
    }

    public void startEvolutionWhileEffect(ImageView view) {
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        mAniEvolutionWhile.start(view);
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

    @Override
    public void AnimationStopped(int tag, ImageView view) {
        if (mListener != null) {
            if (tag == TAG_ANIM_EVOLUTION_SUCCESS || tag == TAG_ANIM_EVOLUTION_FAILED) {
                mListener.complete(CompleteEventMode.EVOLUTION);
            }
        }
    }
}
