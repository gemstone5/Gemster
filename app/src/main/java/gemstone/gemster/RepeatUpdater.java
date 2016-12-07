package gemstone.gemster;

import android.os.Handler;
import android.util.Log;

/**
 * Created by WONSEOK OH on 2016-12-07.
 */

public class RepeatUpdater {

    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;

    private Handler mHandler = new Handler();
    private RunnableUpdater mRunnableUpdater = new RunnableUpdater();

    public static int MODE_NONE = 0;
    public static int MODE_AUTO_INCREMENT = 1;
    public static int MODE_AUTO_DECREMENT = 2;
    private int mMode = MODE_NONE;

    private final int REPEAT_DELAY = 50;

    public enum EventMode {
        EVENT_INCREMENT, EVENT_DECREMENT
    }

    private RepeatUpdaterEventListener mListener;

    public interface RepeatUpdaterEventListener {
        void onRepeatUpdaterEvent(EventMode mode, Object param);
    }

    public void setEventListener(RepeatUpdaterEventListener listener) {
        mListener = listener;
    }

    public void setMode(int mode) {
        mMode = mode;
        if (mMode == MODE_AUTO_INCREMENT) {
            this.mAutoIncrement = true;
        } else if (mMode == this.MODE_AUTO_DECREMENT) {
            this.mAutoDecrement = true;
        }
    }

    public void run() {
        mHandler.removeCallbacks(mRunnableUpdater);
        mHandler.post(mRunnableUpdater);
    }

    public void stop() {
        if (mMode == MODE_AUTO_INCREMENT) {
            this.mAutoIncrement = false;
        } else if (mMode == this.MODE_AUTO_DECREMENT) {
            this.mAutoDecrement = false;
        }
    }

    private void increment() {
        Log.d("gemtest", "increment");
        if (mListener != null) {
            mListener.onRepeatUpdaterEvent(EventMode.EVENT_INCREMENT, null);
        }
    }

    private void decrement() {
        Log.d("gemtest", "decrement");
        if (mListener != null) {
            mListener.onRepeatUpdaterEvent(EventMode.EVENT_DECREMENT, null);
        }
    }

    private void processPostDelayed() {
        mHandler.postDelayed(mRunnableUpdater, REPEAT_DELAY);
    }

    class RunnableUpdater implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                increment();
            } else if (mAutoDecrement) {
                decrement();
            }
            processPostDelayed();
        }
    }
}
