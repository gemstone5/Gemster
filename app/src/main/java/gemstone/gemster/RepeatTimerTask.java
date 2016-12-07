package gemstone.gemster;

import java.util.TimerTask;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class RepeatTimerTask extends TimerTask {

    public static final int TYPE_GET_DNA = 0;
    public static final int TYPE_TRY_EVOLUTION = 1;

    private final int mTimeRemain;
    private int mType;

    public final static int EVENT_COMPLETE = 0;
    public final static int EVENT_REMAIN = 1;

    private EventListener mListener;

    public interface EventListener {
        void onTimerDNAEvent(int type, int mode);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public RepeatTimerTask(int type, int timeRemain) {
        mType = type;
        mTimeRemain = timeRemain;
    }

    @Override
    public void run() {
        if (mTimeRemain == 0) {
            mListener.onTimerDNAEvent(mType, EVENT_COMPLETE);

        } else if (mTimeRemain > 0) {
            mListener.onTimerDNAEvent(mType, EVENT_REMAIN);
        }
    }
}
