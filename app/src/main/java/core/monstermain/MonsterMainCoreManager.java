package core.monstermain;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import java.util.Timer;

import core.Common;
import core.RepeatTimerTask;
import core.RepeatUpdater;
import ui.monstermain.MonsterMainInterfaceManager;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class MonsterMainCoreManager implements MonsterMainInterfaceManager.EventListener, RepeatTimerTask.EventListener, RepeatUpdater.RepeatUpdaterEventListener {

    private Context mContext;

    private MonsterMainInterfaceManager mInterfaceManager;

    private Timer mTimerDNA;
    private RepeatTimerTask mTimerTaskDNA;
    private int mTimeRemain;
    private RepeatUpdater mRepeatUpdater;

    Handler mHandler = new Handler();

    public enum EventMode {
        EVENT_OPEN_MONSTER_BOOK, EVENT_EVOLUTION_SUCCESS
    }

    private EventListener mListener;

    public interface EventListener {
        void onMainFragmentEvent(EventMode mode);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public MonsterMainCoreManager(Context context, Activity activity) {
        mContext = context;

        // Handle exceptional tier case
        handleExceptionalTierCase();

        // Init repeat updater
        initRepeatUpdater();

        // Init interface manager
        mInterfaceManager = new MonsterMainInterfaceManager(context, activity);
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

    public void setTouchable(boolean state) {
        mInterfaceManager.setTouchable(state);
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
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        final int quantity = Common.getDNAQuantity(tier);
        Common.setPrefData(mContext, Common.MAIN_DNA, String.valueOf(feed + quantity));

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
                mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.DNA_COUNT_SET);
                mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.STATUS_EFFECT_DNA_GET_START, quantity);
                setDebugDescription(Common.DEBUG_DEFAULT);
            }
        });
    }

    private void tryEvolution() {
        if (Common.isExceptionalTier(mContext)) {
            mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
            return;
        }

        int DNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        if (DNA < 1) {
            mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.UTIL_BUTTONS_ENABLE);
            return;
        }

        DNA -= useDNA;
        Common.setPrefData(mContext, Common.MAIN_DNA, String.valueOf(DNA));
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.DNA_COUNT_SET);

        mTimeRemain = (int) (Common.EVOLUTION_TIME / Common.TIME_DELAY);
        processRepeatTimer(RepeatTimerTask.TYPE_TRY_EVOLUTION);
    }

    private void setUseDNA(boolean isEvolSuccess) {
        int DNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        if (DNA == 0 || useDNA > DNA) {
            useDNA = ((DNA == 0) ? 1 : DNA);
            Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(useDNA));
        } else if (!isEvolSuccess) {
            int count = Common.getCompleteDNAUseCount(0);
            if (count < useDNA) {
                Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(count));
            }
        }
    }

    private void completeTryEvolution() {
        final int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        final int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);

        double perProb = Common.getPerProb(tier);
        double prob = perProb * useDNA;
        double rand = Math.random();
        final boolean result = (rand <= prob);

        setUseDNA(result);

        if (result) {
            Common.setPrefData(mContext, Common.MAIN_TIER, String.valueOf(tier + 1));
            final int spec = (int) Common.getPrefData(mContext, Common.MAIN_SPEC);
            Common.setIsCollected(mContext, spec, tier + 1);
        } else {
            Common.setPrefData(mContext, Common.MAIN_TIER, "0");
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.GAME_VIEW_SET);
                if (result) {
                    mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.MONSTER_EFFECT_EVOLUTION_SUCCESS_START);
                } else {
                    mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.MONSTER_EFFECT_EVOLUTION_FAILED_START);
                }
                setDebugDescription(Common.DEBUG_DEFAULT);
                if (result) {
                    mListener.onMainFragmentEvent(EventMode.EVENT_EVOLUTION_SUCCESS);
                }
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

        int count = Common.getCurrentCompleteDNAUseCount(mContext);
        boolean isNeedIncrement = useDNA < count;

        if (isNeedIncrement && useDNA < DNA) {
            useDNA += 1;
            if (useDNA > DNA) {
                useDNA = DNA;
            }
        }
        Common.setPrefData(mContext, Common.MAIN_DNA_USE, String.valueOf(useDNA));
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.DNA_USE_SET);
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.MONSTER_PROB_SET);
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
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.DNA_USE_SET);
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.MONSTER_PROB_SET);
    }

    private void openMonsterBook() {
        mListener.onMainFragmentEvent(EventMode.EVENT_OPEN_MONSTER_BOOK);
    }

    @Override
    public void onMainInterfaceEvent(MonsterMainInterfaceManager.EventMode mode, Object param) {
        if (MonsterMainInterfaceManager.EventMode.EVENT_SHOW_TOAST.equals(mode)) {
            mInterfaceManager.showToast((String) param);
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_OPEN_MONSTER_BOOK.equals(mode)) {
            openMonsterBook();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_GET_DNA.equals(mode)) {
            getDNA();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_TRY_EVOLUTION.equals(mode)) {
            tryEvolution();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_TOUCH_DNA_UP_START.equals(mode)) {
            incrementDNAUse();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_TOUCH_DNA_DOWN_START.equals(mode)) {
            decrementDNAUSE();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_LONG_CLICK_DNA_UP.equals(mode)) {
            mRepeatUpdater.setMode(RepeatUpdater.MODE_AUTO_INCREMENT);
            mRepeatUpdater.run();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_LONG_CLICK_DNA_DOWN.equals(mode)) {
            mRepeatUpdater.setMode(RepeatUpdater.MODE_AUTO_DECREMENT);
            mRepeatUpdater.run();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP.equals(mode)) {
            mRepeatUpdater.stop();
        } else if (MonsterMainInterfaceManager.EventMode.EVENT_RESET_FOR_DEBUG.equals(mode)) {
            resetForDebug();
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

    public void resetForDebug() {
        Common.setPrefData(mContext, Common.MAIN_TIER, Common.getDefaultValue(Common.MAIN_TIER));
        Common.setPrefData(mContext, Common.MAIN_DNA, Common.getDefaultValue(Common.MAIN_DNA));
        Common.setPrefData(mContext, Common.MAIN_DNA_USE, Common.getDefaultValue(Common.MAIN_DNA_USE));
        Common.setPrefData(mContext, Common.MAIN_DATA_COLLECT, "");
        Common.setIsCollected(mContext, 0, 0);
        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.GAME_VIEW_SET);
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

        mInterfaceManager.call(MonsterMainInterfaceManager.CallMode.DEBUG_INFO_SET, desc);
    }

    public void dismissPopupWindow() {
        mInterfaceManager.dismissPopupWindow();
    }
}
