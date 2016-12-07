package gemstone.gemster;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
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
    private RelativeLayout mRelativeLayoutMonsterSet;
    private ImageView mImageViewMonster;
    private ImageView mImageViewMonsterEffect;
    private TextView mTextViewMonsterName;
    private TextView mTextViewMonsterProb;
    private ImageButton mImageButtonDNA;
    private ImageButton mImageButtonEvolution;
    private TextView mTextViewDnaUse;
    private ImageButton mImageButtonDnaUp;
    private ImageButton mImageButtonDnaDown;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private View.OnTouchListener mOnTouchListener;

    EffectManager mEffectManager;

    public enum EventMode {
        EVENT_LONG_CLICK_DNA_UP, EVENT_LONG_CLICK_DNA_DOWN, EVENT_TOUCH_DNA_UP_OR_DOWN_STOP,
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
        DNA_BUTTON_ENABLE, DNA_BUTTON_DISABLE, EVOLUTION_BUTTON_ENABLE, EVOLUTION_BUTTON_DISABLE,
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
        initOnClickListener();
        initOnLongClickListener();
        initOnTouchListener();
    }

    private void initOnClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClickScaleAnimation(view);
                if (view.equals(mImageViewMonster)) { // image view monster

                } else if (view.equals(mImageButtonDNA)) { // image button DNA
                    getDNA();
                } else if (view.equals(mImageButtonEvolution)) { // image button evolution
                    tryEvolution();
                }
            }
        };
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

    private void initOnTouchListener() {
        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mListener.onMainInterfaceEvent(EventMode.EVENT_TOUCH_DNA_UP_OR_DOWN_STOP, null);
                }
                return false;
            }
        };
    }

    private void getDNA() {
        mImageButtonDNA.setEnabled(false);
        mImageButtonEvolution.setEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_GET_DNA, null);
    }

    private void tryEvolution() {
        mImageButtonDNA.setEnabled(false);
        mImageButtonEvolution.setEnabled(false);
        mListener.onMainInterfaceEvent(EventMode.EVENT_TRY_EVOLUTION, null);
    }

    private void initViews() {
        mTextViewDebug = (TextView) mActivity.findViewById(R.id.textView_debug);
        mTextViewDNACount = (TextView) mActivity.findViewById(R.id.textView_DNA_count);
        mRelativeLayoutMonsterSet = (RelativeLayout) mActivity.findViewById(R.id.layout_monster_set);
        mImageViewMonster = (ImageView) mActivity.findViewById(R.id.imageView_monster);
        mImageViewMonsterEffect = (ImageView) mActivity.findViewById(R.id.imageView_monster_effect);
        mTextViewMonsterName = (TextView) mActivity.findViewById(R.id.textView_monster_name);
        mTextViewMonsterProb = (TextView) mActivity.findViewById(R.id.textView_monster_prob);
        mImageButtonDNA = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA);
        mImageButtonEvolution = (ImageButton) mActivity.findViewById(R.id.imageButton_evolution);
        mTextViewDnaUse = (TextView) mActivity.findViewById(R.id.textView_dna_use);
        mImageButtonDnaUp = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_up);
        mImageButtonDnaDown = (ImageButton) mActivity.findViewById(R.id.imageButton_DNA_down);

        setLayoutMonsterSet();

        setGameView();
    }

    private void setLayoutMonsterSet() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int size = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = mRelativeLayoutMonsterSet.getLayoutParams();
        params.width = params.height = size;
        mRelativeLayoutMonsterSet.setLayoutParams(params);
    }

    private void setGameView() {
        setImageViewMonsterImage();
        setTextViewMonsterName();
        setTextViewMonsterProb();
        setTextViewDNACount();
    }

    private void setImageViewMonsterImage() {
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        TypedArray arrImg = mContext.getResources().obtainTypedArray(R.array.array_evol_image);
        int id = arrImg.getResourceId(tier, 0);
        mImageViewMonster.setImageResource(id);
    }

    private void setTextViewMonsterName() {
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        String[] arrName = mContext.getResources().getStringArray(R.array.array_evol_name);
        String name = arrName[tier];
        mTextViewMonsterName.setText(name);
    }

    private void setTextViewMonsterProb() {
        int tier = (int) Common.getPrefData(mContext, Common.MAIN_TIER);
        TypedArray arrProb = mContext.getResources().obtainTypedArray(R.array.array_evol_prob);
        float prob = arrProb.getFloat(tier, 0F);

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
        mEffectManager.startClickScaleAnimation(mContext, view);
    }

    private void setTextViewDNACount() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_DNA);
        mTextViewDNACount.setText(count + "개");
    }

    private void setListener() {
        mImageViewMonster.setOnClickListener(mOnClickListener);
        mImageButtonDNA.setOnClickListener(mOnClickListener);
        mImageButtonEvolution.setOnClickListener(mOnClickListener);

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
            case DNA_BUTTON_ENABLE:
                mImageButtonDNA.setEnabled(true);
                break;
            case DNA_BUTTON_DISABLE:
                mImageButtonDNA.setEnabled(false);
                break;

            case EVOLUTION_BUTTON_ENABLE:
                mImageButtonEvolution.setEnabled(true);
                break;
            case EVOLUTION_BUTTON_DISABLE:
                mImageButtonEvolution.setEnabled(true);
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
    public void complete(AnimationDrawable animation) {
        if (mEffectManager.isEvolutionEffect(animation)) {
            mImageButtonDNA.setEnabled(true);
            mImageButtonEvolution.setEnabled(true);
        }
    }

    public void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
