package gemstone.gemster;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;

import java.util.Timer;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class MainCoreManager implements MainInterfaceManager.EventListener, RepeatTimerTask.EventListener, RepeatUpdater.RepeatUpdaterEventListener {

    private Context mContext;

    private MainInterfaceManager mInterfaceManager;

    private Timer mTimerDNA;
    private RepeatTimerTask mTimerTaskDNA;
    private int mTimeRemain;
    private RepeatUpdater mRepeatUpdater;

    Handler mHandler = new Handler();

    public enum CallMode {
        RESET_FOR_DEBUG
    }

    public MainCoreManager(Context context, Activity activity) {
        mContext = context;

        // Handle exceptional tier case
        handleExceptionalTierCase();

        // Init repeat updater
        initRepeatUpdater();

        // Init interface manager
        mInterfaceManager = new MainInterfaceManager(context, activity);
        mInterfaceManager.setEventListener(this);
        mInterfaceManager.init();
    }

    private void handleExceptionalTierCase() {
        if (Common.isExceptionalTier(mContext)) {
            Common.setPrefData(mContext, Common.MAIN_TIER, "0");
        }
    }

    private void initRepeatUpdater() {
        mRepeatUpdater = new RepeatUpdater();
        mRepeatUpdater.setEventListener(this);
    }

    public void processRepeatTimer(int type) {
        final int timerType = type;
        mTimerDNA = new Timer(true);
        mTimerTaskDNA = new RepeatTimerTask(type, mTimeRemain);
        mTimerTaskDNA.setEventListener(this);
        mTimerDNA.schedule(mTimerTaskDNA, Common.TIME_DELAY);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (timerType == RepeatTimerTask.TYPE_GET_DNA) {
                    setDebugDescription(Common.DEBUG_CHECK_DNA_TIME);
                } else if (timerType == RepeatTimerTask.TYPE_TRY_EVOLUTION) {
                    setDebugDescription(Common.DEBUG_CHECK_EVOLUTION_TIME);
                }
            }
        });
    }

    private void getDNA() {
        mTimeRemain = (int) (Common.DNA_TIME / Common.TIME_DELAY);
        processRepeatTimer(RepeatTimerTask.TYPE_GET_DNA);
    }

    private void completeGettingDNA() {
        int feed = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        Common.setPrefData(mContext, Common.MAIN_DNA, String.valueOf(feed + 1));

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
                mInterfaceManager.call(MainInterfaceManager.CallMode.DNA_COUNT_SET);
                setDebugDescription(Common.DEBUG_DEFAULT);
            }
        });
    }

    private void tryEvolution() {
        if (Common.isExceptionalTier(mContext)) {
            mInterfaceManager.call(MainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
            return;
        }

        int DNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        if (DNA < 1) {
            mInterfaceManager.call(MainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
            return;
        }

        DNA -= useDNA;
        Common.setPrefData(mContext, Common.MAIN_DNA, String.valueOf(DNA));
        mInterfaceManager.call(MainInterfaceManager.CallMode.DNA_COUNT_SET);

        mTimeRemain = (int) (Common.EVOLUTION_TIME / Common.TIME_DELAY);
        processRepeatTimer(RepeatTimerTask.TYPE_TRY_EVOLUTION);
    }

    private void setUseDNA() {
        int DNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        if (DNA == 0 || useDNA > DNA) {
            useDNA = ((DNA == 0) ? 1 : DNA);
            Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(useDNA));
        }
    }

    private void completeTryEvolution() {
        final int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        final int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        TypedArray arrPerProb = mContext.getResources().obtainTypedArray(R.array.array_evol_prob);
        double perProb = (double) arrPerProb.getFloat(tier, 0F);
        double prob = perProb * useDNA;
        double rand = Math.random();
        final boolean result = (rand <= prob);

        setUseDNA();

        if (result) {
            Common.setPrefData(mContext, Common.MAIN_TIER, String.valueOf(tier + 1));
        } else {
            Common.setPrefData(mContext, Common.MAIN_TIER, "0");
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MainInterfaceManager.CallMode.GAME_VIEW_SET);
                if (result) {
                    mInterfaceManager.call(MainInterfaceManager.CallMode.MONSTER_EFFECT_EVOLUTION_SUCCESS_START);
                } else {
                    mInterfaceManager.call(MainInterfaceManager.CallMode.MONSTER_EFFECT_EVOLUTION_FAILED_START);
                }
                setDebugDescription(Common.DEBUG_DEFAULT);
            }
        });
    }

    @Override
    public void onTimerDNAEvent(int type, int mode) {
        if (mode == RepeatTimerTask.EVENT_COMPLETE) {
            if (type == RepeatTimerTask.TYPE_GET_DNA) {
                completeGettingDNA();
            } else if (type == RepeatTimerTask.TYPE_TRY_EVOLUTION) {
                completeTryEvolution();
            }
        } else if (mode == RepeatTimerTask.EVENT_REMAIN) {
            mTimeRemain--;
            processRepeatTimer(type);
        }
    }

    private void incrementDNAUse() {
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);
        int DNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        if (useDNA < DNA) {
            useDNA += 1;
            if (useDNA > DNA) {
                useDNA = DNA;
            }
        }
        Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(useDNA));
        mInterfaceManager.call(MainInterfaceManager.CallMode.DNA_USE_SET);
        mInterfaceManager.call(MainInterfaceManager.CallMode.MONSTER_PROB_SET);
    }

    private void decrementDNAUSE() {
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);
        if (useDNA > 1) {
            useDNA -= 1;
            if (useDNA < 0) {
                useDNA = 1;
            }
        }
        Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(useDNA));
        mInterfaceManager.call(MainInterfaceManager.CallMode.DNA_USE_SET);
        mInterfaceManager.call(MainInterfaceManager.CallMode.MONSTER_PROB_SET);
    }

    @Override
    public void onMainInterfaceEvent(MainInterfaceManager.EventMode mode, Object param) {
        if (MainInterfaceManager.EventMode.EVENT_SHOW_TOAST.equals(mode)) {
            mInterfaceManager.showToast((String) param);
        } else if (MainInterfaceManager.EventMode.EVENT_GET_DNA.equals(mode)) {
            getDNA();
        } else if (MainInterfaceManager.EventMode.EVENT_TRY_EVOLUTION.equals(mode)) {
            tryEvolution();
        } else if (MainInterfaceManager.EventMode.EVENT_TOUCH_DNA_UP_START.equals(mode)) {
            incrementDNAUse();
        } else if (MainInterfaceManager.EventMode.EVENT_TOUCH_DNA_DOWN_START.equals(mode)) {
            decrementDNAUSE();
        } else if (MainInterfaceManager.EventMode.EVENT_LONG_CLICK_DNA_UP.equals(mode)) {
            mRepeatUpdater.setMode(RepeatUpdater.MODE_AUTO_INCREMENT);
            mRepeatUpdater.run();
        } else if (MainInterfaceManager.EventMode.EVENT_LONG_CLICK_DNA_DOWN.equals(mode)) {
            mRepeatUpdater.setMode(RepeatUpdater.MODE_AUTO_DECREMENT);
            mRepeatUpdater.run();
        } else if (MainInterfaceManager.EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP.equals(mode)) {
            mRepeatUpdater.stop();
        }
    }

    @Override
    public void onRepeatUpdaterEvent(RepeatUpdater.EventMode mode, Object param) {
        if (RepeatUpdater.EventMode.EVENT_INCREMENT.equals(mode)) {
            incrementDNAUse();
        } else if (RepeatUpdater.EventMode.EVENT_DECREMENT.equals(mode)) {
            decrementDNAUSE();
        }
    }

    public void call(CallMode mode) {
        switch (mode) {
            case RESET_FOR_DEBUG:
                Common.setPrefData(mContext, Common.MAIN_TIER, Common.getDefaultValue(Common.MAIN_TIER));
                Common.setPrefData(mContext, Common.MAIN_DNA, Common.getDefaultValue(Common.MAIN_DNA));
                Common.setPrefData(mContext, Common.MAIN_DNA_USE, Common.getDefaultValue(Common.MAIN_DNA_USE));
                mInterfaceManager.call(MainInterfaceManager.CallMode.GAME_VIEW_SET);
                break;
        }
    }

    private void setDebugDescription(int mode) {
        String desc = "Debug information";

        switch (mode) {
            case Common.DEBUG_DEFAULT:
                break;
            case Common.DEBUG_CHECK_DNA_TIME:
                desc += "\nDNA time left: " + String.valueOf((float) (mTimeRemain * Common.TIME_DELAY) / 1000F) + "sec";
                break;
            case Common.DEBUG_CHECK_EVOLUTION_TIME:
                desc += "\nEvolution time left: " + String.valueOf((float) (mTimeRemain * Common.TIME_DELAY) / 1000F) + "sec";
                break;
        }

        mInterfaceManager.call(MainInterfaceManager.CallMode.DEBUG_INFO_SET, desc);

    }
}
