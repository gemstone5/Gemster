package gemstone.gemster;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class EffectManager implements CustomAnimationDrawable.IAnimationFinishListener {

    private final CustomAnimationDrawable mAniEvolutionSuccess;
    private final CustomAnimationDrawable mAniEvolutionFailed;

    private final int ANI_DURATION = 40;

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

    public void startClickScaleAnimation(Context context, View view) {
        Animation scale = AnimationUtils.loadAnimation(context, R.anim.anim_scale_click);
        // Animation set to join both scaling and moving
        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(scale);
        // Launching animation set
        view.startAnimation(animSet);
    }

    @Override
    public void onAnimationFinished(AnimationDrawable animation, ImageView view) {
        if (animation.isRunning()) {
            animation.stop();
        }
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }
}
