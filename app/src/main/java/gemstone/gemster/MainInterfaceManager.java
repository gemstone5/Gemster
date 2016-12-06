package gemstone.gemster;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
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

public class MainInterfaceManager {

    private final Context mContext;
    private final Activity mActivity;

    private TextView mTextViewDebug;
    private TextView mTextViewFeedCount;
    private RelativeLayout mRelativeLayoutMonsterSet;
    private ImageView mImageViewMonster;
    private ImageView mImageViewMonsterEffect;
    private TextView mTextViewMonsterName;
    private TextView mTextViewMonsterProb;
    private ImageButton mImageButtonFeed;
    private ImageButton mImageButtonEvolution;

    private View.OnClickListener mOnClickListener;

    EffectManager mEffectManager;

    public final static int EVENT_SHOW_TOAST = 0;
    public final static int EVENT_GET_FEED = 1;
    public final static int EVENT_TRY_EVOLUTION = 2;

    private EventListener mListener;

    public interface EventListener {
        void onMainInterfaceEvent(int mode, Object param);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public enum CallMode {
        FEED_BUTTON_ENABLE, FEED_BUTTON_DISABLE, EVOLUTION_BUTTON_ENABLE, EVOLUTION_BUTTON_DISABLE,
        FEED_COUNT_SET, MONSTER_IMAGE_SET, MONSTER_NAME_SET, MONSTER_PROB_SET,
        MONSTER_EFFECT_EVOLUTION_SUCCESS_START, MONSTER_EFFECT_EVOLUTION_FAILED_START,
        GAME_VIEW_SET, DEBUG_INFO_SET
    }

    public MainInterfaceManager(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;

        mEffectManager = new EffectManager(mContext);
    }

    public void init() {
        initOnClickListener();
        initViews();
        setClickListener();
    }

    private void initOnClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClickScaleAnimation(view);
                if (view.equals(mImageViewMonster)) { // image view monster

                } else if (view.equals(mImageButtonFeed)) { // image button feed
                    mImageButtonFeed.setEnabled(false);
                    mImageButtonEvolution.setEnabled(false);
                    mListener.onMainInterfaceEvent(EVENT_GET_FEED, null);
                } else if (view.equals(mImageButtonEvolution)) { // image button evolution
                    mImageButtonFeed.setEnabled(false);
                    mImageButtonEvolution.setEnabled(false);
                    mListener.onMainInterfaceEvent(EVENT_TRY_EVOLUTION, null);
                }
            }
        };
    }

    private void initViews() {
        mTextViewDebug = (TextView) mActivity.findViewById(R.id.textView_debug);
        mTextViewFeedCount = (TextView) mActivity.findViewById(R.id.textView_feed_count);
        mRelativeLayoutMonsterSet = (RelativeLayout) mActivity.findViewById(R.id.layout_monster_set);
        mImageViewMonster = (ImageView) mActivity.findViewById(R.id.imageView_monster);
        mImageViewMonsterEffect = (ImageView) mActivity.findViewById(R.id.imageView_monster_effect);
        mTextViewMonsterName = (TextView) mActivity.findViewById(R.id.textView_monster_name);
        mTextViewMonsterProb = (TextView) mActivity.findViewById(R.id.textView_monster_prob);
        mImageButtonFeed = (ImageButton) mActivity.findViewById(R.id.imageButton_feed);
        mImageButtonEvolution = (ImageButton) mActivity.findViewById(R.id.imageButton_evolution);

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
        setTextViewFeedCount();
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

    private void setTextViewFeedCount() {
        int count = (int) Common.getPrefData(mContext, Common.MAIN_FEED);
        mTextViewFeedCount.setText(count + "개");
    }

    private void setClickListener() {
        mImageViewMonster.setOnClickListener(mOnClickListener);
        mImageButtonFeed.setOnClickListener(mOnClickListener);
        mImageButtonEvolution.setOnClickListener(mOnClickListener);
    }

    public void call(CallMode mode) {
        call(mode, null);
    }

    public void call(CallMode mode, Object param) {
        switch (mode) {
            case FEED_BUTTON_ENABLE:
                mImageButtonFeed.setEnabled(true);
                break;
            case FEED_BUTTON_DISABLE:
                mImageButtonFeed.setEnabled(false);
                break;

            case EVOLUTION_BUTTON_ENABLE:
                mImageButtonEvolution.setEnabled(true);
                break;
            case EVOLUTION_BUTTON_DISABLE:
                mImageButtonEvolution.setEnabled(true);
                break;

            case FEED_COUNT_SET:
                setTextViewFeedCount();
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

    public void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
