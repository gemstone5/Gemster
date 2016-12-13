package ui.monstermain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gemstone5.gemster.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import core.Common;
import core.CustomOnTouchListener;
import core.SoundManager;
import ui.EffectManager;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class MonsterMainInterfaceManager implements EffectManager.EffectCompleteListener, EditDNAUsePopupWindow.EventListener {

    private final Context mContext;
    private final Activity mActivity;
    private boolean mIsTouchable;

    private final UnityAdsListener unityAdsListener = new UnityAdsListener();

    private EditDNAUsePopupWindow mEditDNAUsePopupWindow;

    private TextView mTextViewDebug;
    private LinearLayout mLinearLayoutDNACount;
    private TextView mTextViewDNACount;
    private ImageButton mImageButtonSetting;
    private ImageButton mImageButtonMonsterBook;
    private ImageButton mImageButtonBoost;
    private ImageView mImageViewMonsterFrame;
    private ImageButton mImageButtonMonster;
    private ImageView mImageViewMonsterEffect;
    private TextView mTextViewMonsterName;
    private TextView mTextViewMonsterProbDesc;
    private TextView mTextViewMonsterProbValue;
    private ImageButton mImageButtonDNA;
    private ImageButton mImageButtonEvolution;
    private TextView mTextViewDNAUse;
    private ImageButton mImageButtonDnaUp;
    private ImageButton mImageButtonDnaDown;
    private Button mButtonResetForDebug;

    private View.OnLongClickListener mOnLongClickListener;
    private CustomOnTouchListener mOnTouchListener;

    EffectManager mEffectManager;

    public enum EventMode {
        EVENT_OPEN_MONSTER_BOOK,
        EVENT_LONG_CLICK_DNA_UP, EVENT_LONG_CLICK_DNA_DOWN,
        EVENT_TOUCH_DNA_UP_START, EVENT_TOUCH_DNA_DOWN_START, EVENT_TOUCH_DNA_UP_OR_DOWN_STOP,
        EVENT_SHOW_TOAST, EVENT_GET_DNA, EVENT_TRY_EVOLUTION,
        EVENT_RESET_FOR_DEBUG
    }

    private EventListener mListener;

    public interface EventListener {
        void onMainInterfaceEvent(EventMode mode, Object param);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public enum CallMode {
        UTIL_BUTTONS_ENABLE, UTIL_BUTTONS_DISABLE, DNA_USE_SET,
        DNA_COUNT_SET, MONSTER_IMAGE_SET, MONSTER_NAME_SET, MONSTER_PROB_SET,
        MONSTER_EFFECT_EVOLUTION_SUCCESS_START, MONSTER_EFFECT_EVOLUTION_FAILED_START,
        STATUS_EFFECT_DNA_GET_START, MONSTER_EFFECT_EVOLUTION_WHILE_START,
        GAME_VIEW_SET, DEBUG_INFO_SET
    }

    public MonsterMainInterfaceManager(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mIsTouchable = true;

        mEffectManager = new EffectManager(mContext);
        mEffectManager.setListener(this);

        mEditDNAUsePopupWindow = new EditDNAUsePopupWindow(mActivity, mEffectManager);
        mEditDNAUsePopupWindow.setEventListener(this);
    }

    public void init() {
        initViewListener();
        initViews();
        setListener();

        initAds();
    }

    private void initViewListener() {
        initOnLongClickListener();
        initOnTouchListener();
    }

    private void initOnLongClickListener() {
        mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.equals(mImageButtonDnaUp)) {
                    mListener.onMainInterfaceEvent(EventMode.EVENT_LONG_CLICK_DNA_UP, null);
                } else if (view.equals(mImageButtonDnaDown)) {
                    mListener.onMainInterfaceEvent(EventMode.EVENT_LONG_CLICK_DNA_DOWN, null);
                }
                return false;
            }
        };
    }

    public void setTouchable(boolean state) {
        mIsTouchable = state;
    }

    private void processActionDown(View view) {
        if (view != null && view.equals(mImageButtonDnaUp)) {
            mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_START, null);
        } else if (view != null && view.equals(mImageButtonDnaDown)) {
            mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_DOWN_START, null);
        }
    }

    private void processActionUp(View view) {
        if (view == null) return;
        if (view.equals(mImageButtonSetting)) {
        } else if (view.equals(mImageButtonMonsterBook)) {
            openMonsterBook();
        } else if (view.equals(mImageButtonBoost)) {
            if (UnityAds.isReady()) {
                UnityAds.show(mActivity);
            } else {
                initUnityAds();
            }
        } else if (view.equals(mImageButtonDNA)) {
            getDNA();
        } else if (view.equals(mImageButtonEvolution)) {
            tryEvolution();
        } else if (view.equals(mImageButtonDnaUp) || view.equals(mImageButtonDnaDown)) {
            mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP, null);
        } else if (view.equals(mTextViewDNAUse)) {
            editDNAUse();
        } else if (view.equals(mButtonResetForDebug)) {
            resetForDebug();
        }
    }

    private boolean isDnaUpDownView(View view) {
        return view.equals(mImageButtonDnaUp) || view.equals(mImageButtonDnaDown);
    }

    private void initOnTouchListener() {
        mOnTouchListener = new CustomOnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mIsTouchable) {
                    Log.d("MonsterMain", "MonsterMain is not touchable");
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    SoundManager.startSound(SoundManager.SoundEnum.CLICK);
                    setRectAndIgnore(view);
                    startClickScaleAnimation(view);
                    processActionDown(view);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (this.getIgnore()) return false;
                    if (view.equals(mImageButtonMonster)) {
                        mEffectManager.disableBreathAnimation();
                    }
                    endClickScaleAnimation(view);
                    processActionUp(view);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && this.isOutOfBounds(view, event)) {
                    if (this.getIgnore()) return false;
                    if (view.equals(mImageButtonMonster)) {
                        mEffectManager.disableBreathAnimation();
                    }
                    endClickScaleAnimation(view);
                    if (isDnaUpDownView(view)) {
                        mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP, null);
                    }
                    this.setIgnore(true);
                }

                if (isDnaUpDownView(view)) return false;
                return true;
            }
        };
    }

    private void openMonsterBook() {
        mListener.onMainInterfaceEvent(EventMode.EVENT_OPEN_MONSTER_BOOK, null);
    }

    private void getDNA() {
        setUtilButtonsEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_GET_DNA, null);
    }

    private void tryEvolution() {
        setUtilButtonsEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_TRY_EVOLUTION, null);
    }

    private void editDNAUse() {
        mEditDNAUsePopupWindow.show(mActivity.findViewById(android.R.id.content));
    }

    private void initViews() {
        mTextViewDebug = (TextView) mActivity.findViewById(R.id.textView_debug);
        mLinearLayoutDNACount = (LinearLayout) mActivity.findViewById(R.id.linearLayout_DNA_count);
        mTextViewDNACount = (TextView) mActivity.findViewById(R.id.textView_DNA_count);
        mImageButtonSetting = (ImageButton) mActivity.findViewById(R.id.imageButton_setting);
        mImageButtonMonsterBook = (ImageButton) mActivity.findViewById(R.id.imageButton_monster_book);
        mImageButtonBoost = (ImageButton) mActivity.findViewById(R.id.imageButton_boost);
        mImageViewMonsterFrame = (ImageView) mActivity.findViewById(R.id.imageView_monster_frame);
        mImageButtonMonster = (ImageButton) mActivity.findViewById(R.id.imageButton_monster);
        mImageViewMonsterEffect = (ImageView) mActivity.findViewById(R.id.imageView_monster_effect);
        mTextViewMonsterName = (TextView) mActivity.findViewById(R.id.textView_monster_name);
        mTextViewMonsterProbDesc = (TextView) mActivity.findViewById(R.id.textView_monster_prob_desc);
        mTextViewMonsterProbValue = (TextView) mActivity.findViewById(R.id.textView_monster_prob_value);
        mImageButtonDNA = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA);
        mImageButtonEvolution = (ImageButton) mActivity.findViewById(R.id.imageButton_evolution);
        mTextViewDNAUse = (TextView) mActivity.findViewById(R.id.textView_DNA_use);
        mImageButtonDnaUp = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_up);
        mImageButtonDnaDown = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_down);
        mButtonResetForDebug = (Button) mActivity.findViewById(R.id.button_resetForDebug);

        setImageViewMonsterLayoutCollection();

        setGameView();
    }

    private void setImageViewMonsterLayoutCollection() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int size = displayMetrics.widthPixels;

        int marginFrame = mContext.getResources().getDimensionPixelSize(R.dimen.imageView_monster_frame_margin_size);
        int halfOfMarginFrame = marginFrame / 2;

        int marginImage = marginFrame + mContext.getResources().getDimensionPixelSize(R.dimen.imageView_monster_image_margin_size);
        int halfOfMarginImage = marginImage / 2;

        // Set monster effect layout
        RelativeLayout.LayoutParams paramEffect = (RelativeLayout.LayoutParams) mImageViewMonsterEffect.getLayoutParams();
        paramEffect.width = paramEffect.height = size;
        paramEffect.setMargins(0, halfOfMarginFrame * -1, 0, 0);
        mImageViewMonsterEffect.setLayoutParams(paramEffect);

        // Set monster frame layout
        RelativeLayout.LayoutParams paramFrame = (RelativeLayout.LayoutParams) mImageViewMonsterFrame.getLayoutParams();
        paramFrame.width = paramFrame.height = size - marginFrame;
        paramFrame.setMargins(halfOfMarginFrame, 0, 0, 0);
        mImageViewMonsterFrame.setLayoutParams(paramFrame);

        // Set monster layout
        RelativeLayout.LayoutParams paramImage = (RelativeLayout.LayoutParams) mImageButtonMonster.getLayoutParams();
        paramImage.width = paramImage.height = size - marginImage;
        paramImage.setMargins(halfOfMarginImage, halfOfMarginImage - halfOfMarginFrame, 0, 0);
        mImageButtonMonster.setLayoutParams(paramImage);
    }

    private void setGameView() {
        setGameView(false);
    }

    private void setGameView(boolean afterEvolution) {
        setImageViewMonsterImage(afterEvolution);
        setTextViewMonsterName();
        setTextViewMonsterProb();
        setTextViewDNACount();
        setTextViewDNAUse();
    }

    private void setImageViewMonsterImage() {
        setImageViewMonsterImage(false);
    }

    private void setImageViewMonsterImage(boolean afterEvolution) {
        int spec = (int) Common.getPrefData(mContext, Common.MAIN_SPEC);
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        int id = Common.getGemDrawableId(mContext, spec, tier);
        if (afterEvolution) {
            mEffectManager.changeMonsterImageViewWithAnimation(mContext, mImageButtonMonster, id);
        } else {
            mEffectManager.enableBreathAnimation(mImageButtonMonster);
            mImageButtonMonster.setImageResource(id);
        }
    }

    private void setTextViewMonsterName() {
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        String[] arrName = mContext.getResources().getStringArray(R.array.array_evol_name);
        String name = arrName[tier];
        mTextViewMonsterName.setText(name);
    }

    private void setTextViewMonsterProb() {
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        int useDNA = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);
        double perProb = Common.getPerProb(tier);
        double prob = perProb * useDNA;

        if (prob > 1.0F) prob = 1.0F;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String text = df.format(prob * 100) + "%";
        mTextViewMonsterProbValue.setText(text);

        int topColor, bottomColor;
        if (prob <= 0.3) {
            topColor = ContextCompat.getColor(mContext, R.color.color_red_60);
            bottomColor = ContextCompat.getColor(mContext, R.color.color_red);
        } else if (prob <= 0.6) {
            topColor = ContextCompat.getColor(mContext, R.color.color_orange_60);
            bottomColor = ContextCompat.getColor(mContext, R.color.color_orange);
        } else if (prob <= 0.95) {
            topColor = ContextCompat.getColor(mContext, R.color.color_dkgreen_60);
            bottomColor = ContextCompat.getColor(mContext, R.color.color_dkgreen);
        } else {
            topColor = ContextCompat.getColor(mContext, R.color.color_blue_60);
            bottomColor = ContextCompat.getColor(mContext, R.color.color_blue);
        }
        setTextVerticalShader(mTextViewMonsterProbValue, topColor, bottomColor);
        mEffectManager.startGetEffect(mTextViewMonsterProbValue);
    }

    private void startEvolutionSuccessEffect() {
        mEffectManager.startSuccessEffect(mImageViewMonsterEffect);
    }

    private void startEvolutionFailedEffect() {
        mEffectManager.startFailedEffect(mImageViewMonsterEffect);
    }

    private void startEvolutionWhileEffect() {
        mEffectManager.startEvolutionWhileEffect(mImageViewMonsterEffect);
    }

    private void startGetDNAEffect(int quantity) {
        int toValue = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        int fromValue = toValue - quantity;
        fromValue = fromValue < 0 ? 0 : fromValue;
        mEffectManager.startCountAnimation(mTextViewDNACount, fromValue, toValue);
        mEffectManager.startGetEffect(mLinearLayoutDNACount);
    }

    private void startClickScaleAnimation(View view) {
        mEffectManager.startClickScaleAnimation(view);
    }

    private void endClickScaleAnimation(View view) {
        mEffectManager.endClickScaleAnimation(view);
    }

    private void setTextViewDNACount() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        mTextViewDNACount.setText(String.valueOf(count));
        setTextVerticalShader(mTextViewDNACount, Color.GRAY, Color.DKGRAY);
    }

    private void setTextViewDNAUse() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);
        mTextViewDNAUse.setText(String.valueOf(count));
        setTextVerticalShader(mTextViewDNAUse, Color.GRAY, Color.DKGRAY);
    }

    private void setTextVerticalShader(TextView textView, int topColor, int bottomColor) {
        Shader shader = new LinearGradient(
                0, 0, 0, textView.getTextSize(), topColor, bottomColor, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
    }

    private void setUtilButtonsEnabled(boolean enable) {
        mImageButtonDNA.setEnabled(enable);
        mImageButtonEvolution.setEnabled(enable);
        mImageButtonDnaUp.setEnabled(enable);
        mImageButtonDnaDown.setEnabled(enable);
    }

    private void setListener() {
        mImageButtonSetting.setOnTouchListener(mOnTouchListener);
        mImageButtonMonsterBook.setOnTouchListener(mOnTouchListener);
        mImageButtonBoost.setOnTouchListener(mOnTouchListener);
        mImageButtonMonster.setOnTouchListener(mOnTouchListener);
        mImageButtonDNA.setOnTouchListener(mOnTouchListener);
        mImageButtonEvolution.setOnTouchListener(mOnTouchListener);
        mTextViewDNAUse.setOnTouchListener(mOnTouchListener);
        mButtonResetForDebug.setOnTouchListener(mOnTouchListener);

        mImageButtonDnaUp.setOnLongClickListener(mOnLongClickListener);
        mImageButtonDnaUp.setOnTouchListener(mOnTouchListener);
        mImageButtonDnaDown.setOnLongClickListener(mOnLongClickListener);
        mImageButtonDnaDown.setOnTouchListener(mOnTouchListener);
    }

    private void initAds() {
        AdView mAdView = (AdView) mActivity.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        initUnityAds();
    }

    private void initUnityAds() {
        UnityAds.initialize(mActivity, mActivity.getResources().getString(R.string.unity_ads_game_id), unityAdsListener, true);
    }

    public void dismissPopupWindow() {
        if (mEditDNAUsePopupWindow.isShowing()) {
            mEditDNAUsePopupWindow.dismiss();
        }
    }

    public void call(CallMode mode) {
        call(mode, null);
    }

    public void call(CallMode mode, Object param) {
        switch (mode) {
            case UTIL_BUTTONS_ENABLE:
                setUtilButtonsEnabled(true);
                break;

            case UTIL_BUTTONS_DISABLE:
                setUtilButtonsEnabled(false);
                break;

            case DNA_USE_SET:
                setTextViewDNAUse();
                break;

            case DNA_COUNT_SET:
                setTextViewDNACount();
                break;
            case MONSTER_IMAGE_SET:
                setImageViewMonsterImage();
                break;
            case MONSTER_NAME_SET:
                setTextViewMonsterName();
                break;
            case MONSTER_PROB_SET:
                setTextViewMonsterProb();
                break;

            case MONSTER_EFFECT_EVOLUTION_SUCCESS_START:
                startEvolutionSuccessEffect();
                break;
            case MONSTER_EFFECT_EVOLUTION_FAILED_START:
                startEvolutionFailedEffect();
                break;
            case MONSTER_EFFECT_EVOLUTION_WHILE_START:
                startEvolutionWhileEffect();
                break;

            case STATUS_EFFECT_DNA_GET_START:
                startGetDNAEffect((int) param);
                break;

            case GAME_VIEW_SET:
                if (param != null && (boolean) param) {
                    setGameView(true);
                } else {
                    setGameView();
                }
                break;

            case DEBUG_INFO_SET:
                mTextViewDebug.setText((String) param);
                break;
        }
    }

    @Override
    public void complete(EffectManager.CompleteEventMode mode) {
        if (EffectManager.CompleteEventMode.EVOLUTION.equals(mode)) {
            setUtilButtonsEnabled(true);
        } else if (EffectManager.CompleteEventMode.BREATH_INTERCEPT.equals(mode)) {
            mEffectManager.enableBreathAnimation(mImageButtonMonster);
        }
    }

    @Override
    public void onCompleteEditDNAUseEvent() {
        setGameView();

    }

    private class UnityAdsListener implements IUnityAdsExtendedListener {

        @Override
        public void onUnityAdsClick(String s) {

        }

        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            if (finishState != UnityAds.FinishState.SKIPPED) {

            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }

    }

    public void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    public void resetForDebug() {
        mListener.onMainInterfaceEvent(EventMode.EVENT_RESET_FOR_DEBUG, null);
    }
}
