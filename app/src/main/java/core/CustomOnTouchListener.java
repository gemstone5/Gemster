package core;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by WONSEOK OH on 2016-12-09.
 */

public class CustomOnTouchListener implements View.OnTouchListener {

    private Rect mRect;
    private boolean mIgnore;

    protected void setRectAndIgnore(View view) {
        mRect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        setIgnore(false);
    }

    protected boolean isOutOfBounds(View view, MotionEvent event) {
        if (!mRect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
            return true;
        }
        return false;
    }

    protected void setIgnore(boolean state) {
        mIgnore = state;
    }

    protected boolean getIgnore() {
        return mIgnore;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }
}
