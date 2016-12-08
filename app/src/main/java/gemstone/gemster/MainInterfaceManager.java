package gemstone.gemster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class MainInterfaceManager implements EffectManager.EffectCompleteListener {

    private final Context mContext;
    private final Activity mActivity;

    private TextView mTextViewDebug;
    private TextView mTextViewDNACount;
    private ImageButton mImageButtonSetting;
    private ImageButton mImageButtonMonsterBook;
    private ImageView mImageViewMonsterFrame;
    private ImageButton mImageButtonMonster;
    private ImageView mImageViewMonsterEffect;
    private TextView mTextViewMonsterName;
    private TextView mTextViewMonsterProb;
    private ImageButton mImageButtonDNA;
    private ImageButton mImageButtonEvolution;
    private TextView mTextViewDNAUse;
    private ImageButton mImageButtonDnaUp;
    private ImageButton mImageButtonDnaDown;

    private View.OnLongClickListener mOnLongClickListener;
    private View.OnTouchListener mOnTouchListener;

    EffectManager mEffectManager;

    public enum EventMode {
        EVENT_LONG_CLICK_DNA_UP, EVENT_LONG_CLICK_DNA_DOWN,
        EVENT_TOUCH_DNA_UP_START, EVENT_TOUCH_DNA_DOWN_START, EVENT_TOUCH_DNA_UP_OR_DOWN_STOP,
        EVENT_SHOW_TOAST, EVENT_GET_DNA, EVENT_TRY_EVOLUTION
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
        GAME_VIEW_SET, DEBUG_INFO_SET
    }

    public MainInterfaceManager(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;

        mEffectManager = new EffectManager(mContext);
        mEffectManager.setListener(this);
    }

    public void init() {
        initViewListener();
        initViews();
        setListener();
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
        } else if (view.equals(mImageButtonDNA)) {
            getDNA();
        } else if (view.equals(mImageButtonEvolution)) {
            tryEvolution();
        } else if (view.equals(mImageButtonDnaUp) || view.equals(mImageButtonDnaDown)) {
            mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP, null);
        }
    }

    private void processActionCancel(View view) {
        if (view == null) return;
        if (view.equals(mImageButtonDnaUp) || view.equals(mImageButtonDnaDown)) {
            mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP, null);
        }
    }

    private void initOnTouchListener() {
        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d("gemtest", "onTouch view : " + view.getTag());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (view.equals(mImageButtonMonster)) {
                        mEffectManager.disableBreathAnimation();
                    }
                    startClickScaleAnimation(view);
                    processActionDown(view);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    endClickScaleAnimation(view);
                    processActionUp(view);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    endClickScaleAnimation(view);
                    processActionCancel(view);
                }
                return false;
            }
        };
    }

    private void openMonsterBook() {
        mContext.startActivity(new Intent(mContext, MonsterBookActivity.class));
    }

    private void getDNA() {
        setUtilButtonsEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_GET_DNA, null);
    }

    private void tryEvolution() {
        setUtilButtonsEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_TRY_EVOLUTION, null);
    }

    private void initViews() {
        mTextViewDebug = (TextView) mActivity.findViewById(R.id.textView_debug);
        mTextViewDNACount = (TextView) mActivity.findViewById(R.id.textView_DNA_count);
        mImageButtonSetting = (ImageButton) mActivity.findViewById(R.id.imageButton_setting);
        mImageButtonMonsterBook = (ImageButton) mActivity.findViewById(R.id.imageButton_monster_book);
        mImageViewMonsterFrame = (ImageView) mActivity.findViewById(R.id.imageView_monster_frame);
        mImageButtonMonster = (ImageButton) mActivity.findViewById(R.id.imageButton_monster);
        mImageViewMonsterEffect = (ImageView) mActivity.findViewById(R.id.imageView_monster_effect);
        mTextViewMonsterName = (TextView) mActivity.findViewById(R.id.textView_monster_name);
        mTextViewMonsterProb = (TextView) mActivity.findViewById(R.id.textView_monster_prob);
        mImageButtonDNA = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA);
        mImageButtonEvolution = (ImageButton) mActivity.findViewById(R.id.imageButton_evolution);
        mTextViewDNAUse = (TextView) mActivity.findViewById(R.id.textView_DNA_use);
        mImageButtonDnaUp = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_up);
        mImageButtonDnaDown = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_down);

        setImageViewMonsterLayoutCollection();

        setGameView();
    }

    private void setImageViewMonsterLayoutCollection() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int size = displayMetrics.widthPixels;

        // Set monster effect layout
        ViewGroup.LayoutParams paramEffect = mImageViewMonsterEffect.getLayoutParams();
        paramEffect.width = paramEffect.height = size;
        mImageViewMonsterEffect.setLayoutParams(paramEffect);

        // Set monster frame layout
        int marginFrame = mContext.getResources().getDimensionPixelSize(R.dimen.imageView_monster_frame_margin_size);
        RelativeLayout.LayoutParams paramFrame = (RelativeLayout.LayoutParams) mImageViewMonsterFrame.getLayoutParams();
        paramFrame.width = paramFrame.height = size - marginFrame;
        int halfOfMarginFrame = marginFrame / 2;
        paramFrame.setMargins(halfOfMarginFrame, halfOfMarginFrame, 0, 0);
        mImageViewMonsterFrame.setLayoutParams(paramFrame);

        // Set monster layout
        int marginImage = marginFrame + mContext.getResources().getDimensionPixelSize(R.dimen.imageView_monster_image_margin_size);
        RelativeLayout.LayoutParams paramImage = (RelativeLayout.LayoutParams) mImageButtonMonster.getLayoutParams();
        paramImage.width = paramImage.height = size - marginImage;
        int halfOfMarginImage = marginImage / 2;
        paramImage.setMargins(halfOfMarginImage, halfOfMarginImage, 0, 0);
        mImageButtonMonster.setLayoutParams(paramImage);
    }

    private void setGameView() {
        setImageViewMonsterImage();
        setTextViewMonsterName();
        setTextViewMonsterProb();
        setTextViewDNACount();
        setTextViewDNAUse();
    }

    private void setImageViewMonsterImage() {
        int spec = (int) Common.getPrefData(mContext, Common.MAIN_SPEC);
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        int id = Common.getGemDrawableId(mContext, spec, tier);
        mImageButtonMonster.setImageResource(id);

        mEffectManager.enableBreathAnimation(mImageButtonMonster);
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
        TypedArray arrPerProb = mContext.getResources().obtainTypedArray(R.array.array_evol_prob);
        float perProb = arrPerProb.getFloat(tier, 0F);
        float prob = perProb * useDNA;

        if (prob > 1.0F) prob = 1.0F;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String text = "(다음 진화확률: " + df.format(prob * 100) + "%)";
        mTextViewMonsterProb.setText(text);
    }

    private void startEvolutionSuccessEffect() {
        mEffectManager.startSuccessEffect(mImageViewMonsterEffect);
    }

    private void startEvolutionFailedEffect() {
        mEffectManager.startFailedEffect(mImageViewMonsterEffect);
    }

    private void startClickScaleAnimation(View view) {
        mEffectManager.startClickScaleAnimation(view);
    }

    private void endClickScaleAnimation(View view) {
        mEffectManager.endClickScaleAnimation(view);
    }

    private void setTextViewDNACount() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        mTextViewDNACount.setText(count + "개");
    }

    private void setTextViewDNAUse() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_DNA_USE);
        mTextViewDNAUse.setText(String.valueOf(count));
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
        mImageButtonMonster.setOnTouchListener(mOnTouchListener);
        mImageButtonDNA.setOnTouchListener(mOnTouchListener);
        mImageButtonEvolution.setOnTouchListener(mOnTouchListener);

        mImageButtonDnaUp.setOnLongClickListener(mOnLongClickListener);
        mImageButtonDnaUp.setOnTouchListener(mOnTouchListener);
        mImageButtonDnaDown.setOnLongClickListener(mOnLongClickListener);
        mImageButtonDnaDown.setOnTouchListener(mOnTouchListener);
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

            case GAME_VIEW_SET:
                setGameView();
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

    public void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
