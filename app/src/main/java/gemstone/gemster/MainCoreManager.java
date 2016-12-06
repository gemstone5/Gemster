package gemstone.gemster;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;

import java.util.Timer;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class MainCoreManager implements MainInterfaceManager.EventListener, RepeatTimerTask.EventListener {

    private Context mContext;

    private MainInterfaceManager mInterfaceManager;

    private Timer mTimerFeed;
    private RepeatTimerTask mTimerTaskFeed;
    private int mTimeRemain;

    Handler mHandler = new Handler();

    public enum CallMode {
        RESET_FOR_DEBUG
    }

    public MainCoreManager(Context context, Activity activity) {
        mContext = context;

        mInterfaceManager = new MainInterfaceManager(context, activity);
        mInterfaceManager.setEventListener(this);
        mInterfaceManager.init();
    }

    public void processRepeatTimer(int type) {
        final int timerType = type;
        mTimerFeed = new Timer(true);
        mTimerTaskFeed = new RepeatTimerTask(type, mTimeRemain);
        mTimerTaskFeed.setEventListener(this);
        mTimerFeed.schedule(mTimerTaskFeed, Common.TIME_DELAY);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (timerType == RepeatTimerTask.TYPE_GET_FEED) {
                    setDebugDescription(Common.DEBUG_CHECK_FEED_TIME);
                } else if (timerType == RepeatTimerTask.TYPE_TRY_EVOLUTION) {
                    setDebugDescription(Common.DEBUG_CHECK_EVOLUTION_TIME);
                }
            }
        });
    }

    private void getFeed() {
        mTimeRemain = (int) (Common.FEED_TIME / Common.TIME_DELAY);
        processRepeatTimer(RepeatTimerTask.TYPE_GET_FEED);
    }

    private void completeGettingFeed() {
        int feed = (int) Common.getPrefData(mContext, Common.MAIN_FEED);
        Common.setPrefData(mContext, Common.MAIN_FEED, String.valueOf(feed + 1));

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MainInterfaceManager.CallMode.FEED_BUTTON_ENABLE);
                mInterfaceManager.call(MainInterfaceManager.CallMode.EVOLUTION_BUTTON_ENABLE);
                mInterfaceManager.call(MainInterfaceManager.CallMode.FEED_COUNT_SET);
                setDebugDescription(Common.DEBUG_DEFAULT);
            }
        });
    }

    private void tryEvolution() {
        int feed = (int) Common.getPrefData(mContext, Common.MAIN_FEED);
        if (feed < 3) {
            mInterfaceManager.call(MainInterfaceManager.CallMode.FEED_BUTTON_ENABLE);
            mInterfaceManager.call(MainInterfaceManager.CallMode.EVOLUTION_BUTTON_ENABLE);
            return;
        }

        Common.setPrefData(mContext, Common.MAIN_FEED, String.valueOf(feed - 3));
        mInterfaceManager.call(MainInterfaceManager.CallMode.FEED_COUNT_SET);

        mTimeRemain = (int) (Common.EVOLUTION_TIME / Common.TIME_DELAY);
        processRepeatTimer(RepeatTimerTask.TYPE_TRY_EVOLUTION);
    }

    private void completeTryEvolution() {
        final boolean result;
        final int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        TypedArray arrProb = mContext.getResources().obtainTypedArray(R.array.array_evol_prob);
        double rand = Math.random();

        result = (rand < arrProb.getFloat(tier, 0F));

        if (result) {
            Common.setPrefData(mContext, Common.MAIN_TIER, String.valueOf(tier + 1));
        } else {
            Common.setPrefData(mContext, Common.MAIN_TIER, "0");
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInterfaceManager.call(MainInterfaceManager.CallMode.FEED_BUTTON_ENABLE);
                mInterfaceManager.call(MainInterfaceManager.CallMode.EVOLUTION_BUTTON_ENABLE);
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
    public void onTimerFeedEvent(int type, int mode) {
        if (mode == RepeatTimerTask.EVENT_COMPLETE) {
            if (type == RepeatTimerTask.TYPE_GET_FEED) {
                completeGettingFeed();
            } else if (type == RepeatTimerTask.TYPE_TRY_EVOLUTION) {
                completeTryEvolution();
            }
        } else if (mode == RepeatTimerTask.EVENT_REMAIN) {
            mTimeRemain--;
            processRepeatTimer(type);
        }
    }

    @Override
    public void onMainInterfaceEvent(int mode, Object param) {
        if (mode == MainInterfaceManager.EVENT_SHOW_TOAST) {
            mInterfaceManager.showToast((String) param);
        } else if (mode == MainInterfaceManager.EVENT_GET_FEED) {
            getFeed();
        } else if (mode == MainInterfaceManager.EVENT_TRY_EVOLUTION) {
            tryEvolution();
        }
    }

    public void call(CallMode mode) {
        switch (mode) {
            case RESET_FOR_DEBUG:
                Common.setPrefData(mContext, Common.MAIN_TIER, Common.INTEGER_STRING_DEFAULT_VALUE);
                Common.setPrefData(mContext, Common.MAIN_FEED, Common.INTEGER_STRING_DEFAULT_VALUE);
                mInterfaceManager.call(MainInterfaceManager.CallMode.GAME_VIEW_SET);
                break;
        }
    }

    private void setDebugDescription(int mode) {
        String desc = "Debug information";

        switch (mode) {
            case Common.DEBUG_DEFAULT:
                break;
            case Common.DEBUG_CHECK_FEED_TIME:
                desc += "\nFeed time left: " + String.valueOf((float) (mTimeRemain * Common.TIME_DELAY) / 1000F) + "sec";
                break;
            case Common.DEBUG_CHECK_EVOLUTION_TIME:
                desc += "\nEvolution time left: " + String.valueOf((float) (mTimeRemain * Common.TIME_DELAY) / 1000F) + "sec";
                break;
        }

        mInterfaceManager.call(MainInterfaceManager.CallMode.DEBUG_INFO_SET, desc);

    }
}
