package ui.monstermain;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by WONSEOK OH on 2016-12-11.
 */

public class CustomEditText extends EditText {
    private OnBackPressListener mListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mListener != null) {
            mListener.onBackPress();
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnBackPressListener(OnBackPressListener $listener) {
        mListener = $listener;
    }

    public interface OnBackPressListener {
        public void onBackPress();
    }
}