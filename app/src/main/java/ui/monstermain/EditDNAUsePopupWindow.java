package ui.monstermain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import core.Common;
import core.CustomOnTouchListener;
import core.gemster.R;
import ui.EffectManager;

/**
 * Created by WONSEOK OH on 2016-12-11.
 */

public class EditDNAUsePopupWindow extends PopupWindow implements CustomEditText.OnBackPressListener {

    private Activity mActivity;

    private CustomEditText mEditTextDNAUse;
    private ImageButton mImageButtonPositive;
    private ImageButton mImageButtonNegative;

    private CustomOnTouchListener mOnTouchListener;

    private EffectManager mEffectManager;

    private InputMethodManager mInputManager;

    private EventListener mListener;

    public interface EventListener {
        void onCompleteEditDNAUseEvent();
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public EditDNAUsePopupWindow(final Activity activity, EffectManager effectManager) {
        super(activity);
        mActivity = activity;
        mEffectManager = effectManager;

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_edit_dna_use_popup_window, null, false);

        this.setContentView(layout);
        this.setFocusable(true);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupWindowAnimationStyle);
        this.setBackgroundDrawable(null);

        mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        initListener();
        setListener();
    }

    private void initView() {
        mEditTextDNAUse = (CustomEditText) getContentView().findViewById(R.id.edupw_editText_DNA_use);
        mEditTextDNAUse.setOnBackPressListener(this);
        mEditTextDNAUse.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mInputManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        setTextVerticalShader(mEditTextDNAUse, Color.GRAY, Color.DKGRAY);

        mImageButtonPositive = (ImageButton) getContentView().findViewById(R.id.edpw_imageButton_positive);
        mImageButtonNegative = (ImageButton) getContentView().findViewById(R.id.edpw_imageButton_negative);
    }

    private void startClickScaleAnimation(View view) {
        mEffectManager.startClickScaleAnimation(view);
    }

    private void endClickScaleAnimation(View view) {
        mEffectManager.endClickScaleAnimation(view);
    }

    private void processActionUp(View view) {
        if (view == null) return;
        if (view.equals(mImageButtonPositive)) {
            processPositive();
        } else if (view.equals(mImageButtonNegative)) {
            processNegative();
        }
    }

    private void initListener() {
        mOnTouchListener = new CustomOnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setRectAndIgnore(view);
                    startClickScaleAnimation(view);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (this.getIgnore()) return false;
                    endClickScaleAnimation(view);
                    processActionUp(view);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && this.isOutOfBounds(view, event)) {
                    if (this.getIgnore()) return false;
                    endClickScaleAnimation(view);
                    this.setIgnore(true);
                }
                return true;
            }
        };
    }

    private void setListener() {
        mImageButtonPositive.setOnTouchListener(mOnTouchListener);
        mImageButtonNegative.setOnTouchListener(mOnTouchListener);
    }

    private void setDNAUseCount() {
        int count = (int) Common.getPrefData(mActivity, Common.MAIN_DNA_USE);
        mEditTextDNAUse.setText(String.valueOf(count));
    }

    public void show(View parent) {
        setDNAUseCount();
        this.showAtLocation(parent, Gravity.CENTER, 0,
                -mActivity.getResources().getDimensionPixelSize(R.dimen.edit_dna_use_popup_window_margin_bottom));
        mEditTextDNAUse.requestFocus();
    }

    private void processPositive() {
        int DNACount = (int) Common.getPrefData(mActivity, Common.MAIN_DNA);
        int useCount = Integer.parseInt(mEditTextDNAUse.getText().toString());

        if (DNACount < useCount) {
            cancelApply();
            return;
        }

        Common.setPrefData(mActivity, Common.MAIN_DNA_USE, String.valueOf(useCount));
        mListener.onCompleteEditDNAUseEvent();
        dismiss();
    }

    private void processNegative() {
        dismiss();
    }

    private void cancelApply() {
        mEffectManager.startVibrateAnimation(getContentView());
    }

    private void setTextVerticalShader(TextView textView, int topColor, int bottomColor) {
        Shader shader = new LinearGradient(
                0, 0, 0, textView.getTextSize(), topColor, bottomColor, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
    }

    @Override
    public void dismiss() {
        mInputManager.hideSoftInputFromWindow(mEditTextDNAUse.getWindowToken(), 0);
        super.dismiss();
    }

    @Override
    public void onBackPress() {
        dismiss();
    }
}
