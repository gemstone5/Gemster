package ui.monsterbook;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import core.CustomOnTouchListener;
import core.gemster.R;
import ui.EffectManager;

/**
 * Created by WONSEOK OH on 2016-12-08.
 */

public class MonsterBookFragment extends Fragment implements EffectManager.EffectCompleteListener {

    private Context mContext;

    private EffectManager mEffectManager;

    private ImageButton mImageButtonBack;
    private GridView mGridViewCollection;

    private CustomOnTouchListener mOnTouchListener;

    public static MonsterBookFragment newInstance() {
        MonsterBookFragment fragment = new MonsterBookFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        mEffectManager = new EffectManager(mContext);
        mEffectManager.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_monster_book, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initListener();
        setListener();
    }

    private void initView() {
        mImageButtonBack = (ImageButton) getView().findViewById(R.id.mb_imageButton_back);
        mGridViewCollection = (GridView) getView().findViewById(R.id.mb_gridView_collection);

        mGridViewCollection.setAdapter(new MonsterBookImageAdapter(mContext));
    }

    private void initListener() {
        mOnTouchListener = new CustomOnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    this.setRectAndIgnore(view);
                    startClickScaleAnimation(view);
                    processActionDown(view);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (this.getIgnore()) return false;
                    endClickScaleAnimation(view);
                    processActionUp(view);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && this.isOutOfBounds(view, event)) {
                    if (this.getIgnore()) return false;
                    endClickScaleAnimation(view);
                    setIgnore(true);
                }
                return false;
            }
        };
    }

    private void setListener() {
        mImageButtonBack.setOnTouchListener(mOnTouchListener);
    }

    private void startClickScaleAnimation(View view) {
        mEffectManager.startClickScaleAnimation(view);
    }

    private void endClickScaleAnimation(View view) {
        mEffectManager.endClickScaleAnimation(view);
    }

    private void processActionDown(View view) {
        if (view == null) return;
    }

    private void processActionUp(View view) {
        if (view == null) return;
        if (view.equals(mImageButtonBack)) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void complete(EffectManager.CompleteEventMode mode) {

    }
}
